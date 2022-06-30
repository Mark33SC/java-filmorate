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

    @Autowired
    public GenreDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<Genre> getAllGenre() {
        String sql = "SELECT * FROM GENRE ORDER BY GENRE_ID";
        return jdbcTemplate.query(sql, (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                rs.getString("NAME")));
    }

    @Override
    public Genre getGenreById(int id) {
        checkId(id);
        String sql = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> new Genre(rs.getInt("GENRE_ID"),
                rs.getString("NAME")), id);
    }

    private boolean checkId(int id) {
        if (id > 0) {
            String sql = "SELECT EXISTS (SELECT GENRE_ID FROM GENRE WHERE GENRE_ID = ?)";
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id))) {
                return true;
            } else {
                throw new NotFoundException("Жанра с таким id не существует");
            }
        } else
            throw new NotFoundException("Id не может быть меньше 1");
    }
}