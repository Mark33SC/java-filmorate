package ru.yandex.practicum.filmorate.storage.dao.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Component
public class GenreDaoImpl implements GenreDao {
    private final JdbcTemplate jdbcTemplate;
    private final String getAllGenreSql = "SELECT * FROM GENRE ORDER BY GENRE_ID";
    private final String getGenreByIdSql = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
    private final String checkIdSql = "SELECT EXISTS (SELECT GENRE_ID FROM GENRE WHERE GENRE_ID = ?)";

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenre() {
        return jdbcTemplate.query(getAllGenreSql, (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                rs.getString("NAME")));
    }

    @Override
    public Genre getGenreById(int id) {
        checkId(id);
        return jdbcTemplate.queryForObject(getGenreByIdSql, (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                rs.getString("NAME")), id);
    }

    private boolean checkId(int id) {
        if (id > 0) {
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(checkIdSql, Boolean.class, id))) {
                return true;
            } else {
                throw new NotFoundException("Жанра с таким id не существует");
            }
        } else
            throw new NotFoundException("Id не может быть меньше 1");
    }
}