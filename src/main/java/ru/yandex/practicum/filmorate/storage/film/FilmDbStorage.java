package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MPA;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Component
@ComponentScan(basePackages = {"com.sample"})
@ComponentScan("config")
@Qualifier("filmDbStorage")
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT FILM_ID, TITLE, DESCRIPTION, RELEASEDATE, DURATION, RATE, MR.NAME AS MPA " +
                "FROM FILMS JOIN MPA_RATE MR on MR.MPA_RATE_ID = FILMS.MPA_RATE_ID ";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)));
    }

    @Override
    public Film getFilmById(long id) {
        validationId(id);
        String sql = "SELECT * FROM FILMS WHERE FILM_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id);
    }

    @Override
    public Film addFilm(Film film) {
        if (validation(film)) {
            String sql = "INSERT INTO FILMS (TITLE, DESCRIPTION, RELEASEDATE, DURATION, RATE, MPA_RATE_ID) " +
                    "VALUES (:title, :description, :releaseDate, :duration, :rate, :mpa_rate_id)";
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            MapSqlParameterSource parameterSource = new MapSqlParameterSource();
            parameterSource.addValue("title", film.getName());
            parameterSource.addValue("description", film.getDescription());
            parameterSource.addValue("releaseDate", film.getReleaseDate());
            parameterSource.addValue("duration", film.getDuration());
            parameterSource.addValue("rate", film.getRate());
            parameterSource.addValue("mpa_rate_id", film.getMpa().getId());
            GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(sql, parameterSource, keyHolder);
            film.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            if (film.getGenres() != null && film.getGenres().size() > 0) {
                insertFilmGenre(film);
            }
            if (film.getLikes() != null && film.getLikes().size() > 0) {
                addLikes(film);
            }
            return film;
        } else {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        if (validation(film)) {
            validationId(film.getId());
            String sql = "UPDATE FILMS SET TITLE = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, RATE = ?," +
                    " MPA_RATE_ID = ? WHERE FILM_ID = ?";
            jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(), film.getDuration(),
                    film.getRate(), film.getMpa().getId(), film.getId());
            if (film.getGenres() != null && film.getGenres().size() > 0) {
                insertFilmGenre(film);
            } else {
                removeFilmGenre(film.getId());
            }
            if (film.getLikes() != null && film.getLikes().size() > 0) {
                addLikes(film);
            } else {
                removeLikes(film.getId());
            }
            Film updateFilmFromBD = getFilmById(film.getId());
            if (film.getGenres() != null && film.getGenres().size() == 0) {
                updateFilmFromBD.setGenres(film.getGenres());
            }
            return updateFilmFromBD;
        } else {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    @Override
    public void removeFilm(long id) {
        if (validationId(id)) {
            String sql = "DELETE FROM FILMS WHERE FILM_ID = ?";
            jdbcTemplate.update(sql, id);
            removeFilmGenre(id);
            removeLikes(id);
        }
    }

    @Override
    public boolean validationId(long id) {
        if (id > 0) {
            String sql = "SELECT EXISTS (SELECT film_id FROM films WHERE film_id = ?)";
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id))) {
                return true;
            } else {
                throw new FilmNotFoundException("Фильма с таким id не существует");
            }
        } else
            throw new FilmNotFoundException("Id не может быть меньше 0");
    }

    @Override
    public Collection<Film> getMostLikedFilms(int count) {
        String sql = "SELECT FILM_ID, TITLE, DESCRIPTION, RELEASEDATE, DURATION, RATE, MR.NAME AS MPA " +
                "FROM FILMS JOIN MPA_RATE MR on MR.MPA_RATE_ID = FILMS.MPA_RATE_ID " +
                "WHERE RATE IS NOT NULL OR RATE <> 0 ORDER BY RATE DESC LIMIT ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> makeFilm(rs)), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        long id = rs.getLong("FILM_ID");
        String name = rs.getString("TITLE");
        String description = rs.getString("DESCRIPTION");
        LocalDate releaseDate = rs.getDate("RELEASEDATE").toLocalDate();
        int duration = rs.getInt("DURATION");
        Integer rate = rs.getInt("RATE");
        Collection<Genre> genre = getFilmGenre(id);
        MPA mpa = getMpa(id);
        Collection<Long> likes = getLikes(id);
        return new Film(id, name, description, releaseDate, duration, rate, genre, mpa, likes);
    }

    private Collection<Genre> getFilmGenre(long filmId) {
        String sql = "SELECT GENRE.GENRE_ID, NAME FROM GENRE JOIN FILM_GENRE FG " +
                "ON GENRE.GENRE_ID = FG.GENRE_ID WHERE FG.FILM_ID = ?";
        Collection<Genre> genres = jdbcTemplate.query(sql, ((rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                rs.getString("NAME"))), filmId);
        if (genres.size() > 0) {
            return genres;
        } else {
            return null;
        }
    }

    private MPA getMpa(long filmId) {
        String sql = "SELECT MPA_RATE_ID, NAME FROM MPA_RATE WHERE MPA_RATE_ID" +
                " IN (SELECT MPA_RATE_ID FROM FILMS WHERE FILM_ID = ?)";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) ->
                new MPA(rs.getInt("MPA_RATE_ID"), rs.getString("NAME")), filmId);
    }

    private void insertFilmGenre(Film film) {
        removeFilmGenre(film.getId());
        for (Genre genre : film.getGenres()) {
            String addGenreSql = "INSERT INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
            jdbcTemplate.update(addGenreSql, film.getId(), genre.getId());
        }
    }

    private void removeFilmGenre(long filmId) {
        String deleteGenresSql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenresSql, filmId);
    }

    private List<Long> getLikes(long filmId) {
        String sql = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("USER_ID")), filmId);
    }

    private void addLikes(Film film) {
        removeLikes(film.getId());
        for (Long userId : film.getLikes()) {
            String addFriendSql = "INSERT INTO LIKES (USER_ID, FILM_ID) VALUES (?, ?)";
            jdbcTemplate.update(addFriendSql, userId, film.getId());
        }
    }

    private void removeLikes(long filmId) {
        String deleteLikesSql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteLikesSql, filmId);
    }

    private boolean validation(Film film) throws ValidationException {
        return !film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
    }
}