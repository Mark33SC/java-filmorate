package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User film);

    void removeUser(User film);

    User updateUser(User film);

    User getUserById(long id);

    boolean validationId(long id);
}