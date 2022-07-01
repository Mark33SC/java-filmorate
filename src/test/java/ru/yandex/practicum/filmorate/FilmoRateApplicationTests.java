package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)

public class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;

    LocalDate birthday = LocalDate.of(1990, 12, 1);
    User userLoginNull = new User(4, "kis@mail.ru", " ", "Kis", birthday, null);
    User userLoginNull2 = new User(4, "kis@mail.ru", " ", "Kissing", birthday, null);

    @Test
    void testGetUserById() {
        User user = userStorage.getUserById(2);
        assertEquals("friend", user.getLogin());
    }

    @Test
    public void testAddUser() {
        User user = userStorage.addUser(userLoginNull);
        assertEquals("kis@mail.ru", user.getEmail());
    }

    @Test
    public void testUpdateUser() {
        User user = userStorage.updateUser(userLoginNull2);
        assertEquals("Kissing", user.getName());
    }

    @Test
    public void testRemoveUser() {
        userStorage.removeUser(4);
        assertThrows(UserNotFoundException.class, () -> userStorage.getUserById(4));
    }

    @Test
    public void testValidationId() {
        assertThrows(UserNotFoundException.class, () -> userStorage.validationId(4));
    }

    @Test
    public void testFindFilmById() {
        Film film = filmStorage.getFilmById(1);
        assertEquals("labore nulla", film.getName());
    }

    @Test
    public void testFindAllFilms() {
        List<Film> films = filmStorage.getAllFilms();

        assertEquals(2, films.size());
    }
}
