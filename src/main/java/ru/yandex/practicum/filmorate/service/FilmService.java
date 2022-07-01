package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.addLike(userId);
        filmStorage.updateFilm(film);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        Film film = filmStorage.getFilmById(filmId);
        film.removeLike(userId);
        filmStorage.updateFilm(film);
    }

    public Collection<Film> getMostLikedFilms(int count) {
        return filmStorage.getMostLikedFilms(count);
    }
}