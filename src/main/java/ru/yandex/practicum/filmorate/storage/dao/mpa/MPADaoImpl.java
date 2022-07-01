package ru.yandex.practicum.filmorate.storage.dao.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;

import java.util.Collection;

@Component
public class MPADaoImpl implements MPARateDao {
    private final JdbcTemplate jdbcTemplate;
    private final String getAllMPASql = "SELECT * FROM MPA_RATE";
    private final String getMPAByIdSql = "SELECT * FROM MPA_RATE WHERE MPA_RATE_ID = ?";
    private final String checkIdSql = "SELECT EXISTS (SELECT MPA_RATE_ID FROM MPA_RATE WHERE MPA_RATE_ID = ?)";

    @Autowired
    public MPADaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<MPA> getAllMPA() {
        return jdbcTemplate.query(getAllMPASql, (rs, rowNum) -> new MPA(rs.getInt("MPA_RATE_ID"),
                rs.getString("NAME")));
    }

    @Override
    public MPA getMPAById(int id) {
        checkId(id);
        return jdbcTemplate.queryForObject(getMPAByIdSql, (rs, rowNum) -> new MPA(rs.getInt("MPA_RATE_ID"),
                rs.getString("NAME")), id);
    }

    private boolean checkId(int id) {
        if (id > 0) {
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(checkIdSql, Boolean.class, id))) {
                return true;
            } else {
                throw new NotFoundException("Рейтинга с таким id не существует");
            }
        } else
            throw new NotFoundException("Id не может быть меньше 1");
    }
}