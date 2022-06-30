package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Repository
public interface UserStorage {
    List<User> getAllUsers();

    User addUser(User user);

    void removeUser(long id);

    User updateUser(User user);

    User getUserById(long id);

    boolean validationId(long id);
}