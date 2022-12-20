package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryUserStorage implements UserStorage {

    private long userId = 0;
    private Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        long id = getNewId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public void removeUser(long id) {
        if (validationId(id)) {
            users.remove(id);
        }
    }

    @Override
    public User updateUser(User user) {
        validationId(user.getId());
        users.put(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public User getUserById(long id) {
        validationId(id);
        return users.get(id);
    }

    @Override
    public boolean validationId(long id) {
        if (users.containsKey(id)) {
            return true;
        } else {
            throw new UserNotFoundException("Пользователя с таким id не существует");
        }
    }

    private long getNewId() {
        return ++userId;
    }
}