package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(long userId, long friendId) {
        userStorage.validationId(userId);
        userStorage.validationId(friendId);
        User user = userStorage.getUserById(userId);
        user.addFriend(friendId);
        userStorage.updateUser(user);
    }

    public void removeFriend(long userId, long friendId) {
        userStorage.validationId(userId);
        userStorage.validationId(friendId);
        User user = userStorage.getUserById(userId);
        user.removeFriend(friendId);
        userStorage.updateUser(user);
    }

    public List<User> getMutualFriends(long userId, long friendId) {
        Set<Long> mutualFriendsId = new HashSet<>(userStorage.getUserById(userId).getFriends());
        mutualFriendsId.retainAll(userStorage.getUserById(friendId).getFriends());
        List<User> mutualFriends = new ArrayList<>();
        for (Long id : mutualFriendsId) {
            mutualFriends.add(userStorage.getUserById(id));
        }
        return mutualFriends;
    }

    public List<User> getFriends(long id) {
        User user = userStorage.getUserById(id);
        List<User> friends = new ArrayList<>();
        for (Long friendId : user.getFriends()) {
            friends.add(userStorage.getUserById(friendId));
        }
        return friends;
    }
}