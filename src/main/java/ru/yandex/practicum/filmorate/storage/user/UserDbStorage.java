package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@ComponentScan(basePackages = {"com.sample"})
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {


    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> users = jdbcTemplate.query(sql, ((rs, rowNum) -> makeUser(rs)));
        for (User user : users) {
            user.addFriend(getFriends(user.getId()));
        }
        return users;
    }

    @Override
    public User getUserById(long id) {
        validationId(id);
        String sql = "SELECT * FROM users WHERE user_id = ?";
        User user = jdbcTemplate.queryForObject(sql, ((rs, rowNum) -> makeUser(rs)), id);
        assert user != null;
        user.addFriend(getFriends(id));
        return user;
    }

    @Override
    public User addUser(User user) {
        String sql = "INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY) values (:email,:login,:name,:birthday)";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("email", user.getEmail());
        parameters.addValue("login", user.getLogin());
        parameters.addValue("name", user.getName());
        parameters.addValue("birthday", user.getBirthday());
        GeneratedKeyHolder generatedKeyHolder = new GeneratedKeyHolder();

        namedParameterJdbcTemplate.update(sql, parameters, generatedKeyHolder);
        user.setId(Objects.requireNonNull(generatedKeyHolder.getKey()).longValue());
        if (user.getFriends() != null && user.getFriends().size() > 0) {
            addFriend(user);
        }
        return user;
    }

    @Override
    public User updateUser(User user) {
        validationId(user.getId());
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE user_id = ?";
        jdbcTemplate.update(sql, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        if (user.getFriends() != null && user.getFriends().size() > 0) {
            addFriend(user);
        } else {
            removeFriend(user.getId());
        }
        return user;
    }

    @Override
    public void removeUser(long userId) {
        if (validationId(userId)) {
            String sql = "DELETE FROM users WHERE user_id = ?";
            jdbcTemplate.update(sql, userId);
            removeFriend(userId);
        }
    }

    @Override
    public boolean validationId(long id) {
        if (id > 0) {
            String sql = "SELECT EXISTS (SELECT USER_ID FROM USERS WHERE USER_ID = ?)";
            if (Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, id))) {
                return true;
            } else {
                throw new UserNotFoundException("Пользователя с таким id не существует");
            }
        } else
            throw new UserNotFoundException("Id не может быть меньше 0");
    }

    private User makeUser(ResultSet rs) throws SQLException {
        long id = rs.getLong("user_id");
        String email = rs.getString("email");
        String login = rs.getString("login");
        String name = rs.getString("name");
        LocalDate birthday = rs.getDate("birthday").toLocalDate();
        Set<Long> friends = new HashSet<>(getFriends(id));
        return new User(id, email, login, name, birthday, friends);
    }

    private List<Long> getFriends(long id) {
        String sql = "SELECT FRIEND_ID FROM FRIENDS WHERE USER_ID = ?";
        return jdbcTemplate.query(sql, ((rs, rowNum) -> rs.getLong("FRIEND_ID")), id);
    }

    private void addFriend(User user) {
        removeFriend(user.getId());
        for (Long friendId : user.getFriends()) {
            String addFriendSql = "INSERT INTO FRIENDS (USER_ID, FRIEND_ID) VALUES (?, ?)";
            jdbcTemplate.update(addFriendSql, user.getId(), friendId);
        }
    }

    private void removeFriend(long userId) {
        String deleteFriendSql = "DELETE FROM FRIENDS WHERE USER_ID = ?";
        jdbcTemplate.update(deleteFriendSql, userId);
    }
}