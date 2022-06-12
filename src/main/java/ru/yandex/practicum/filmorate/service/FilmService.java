package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;

@Service
public class FilmService {
    FilmStorage filmStorage;
    UserStorage userStorage;
    private final Comparator<Film> comparator = ((o1, o2) -> {
        if (o1.getId() == o2.getId()) {
            return 0;
        } else {
            if (o2.getRate() > o1.getRate())
                return 1;
            else
                return -1;
        }
    });
    private final Set<Film> mostLikedFilms = new HashSet<>();

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(long filmId, long userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        filmStorage.getById(filmId).addLike(userId);
        updateMostLikedFilms(filmId);
    }

    public void removeLike(long filmId, long userId) {
        filmStorage.validationId(filmId);
        userStorage.validationId(userId);
        filmStorage.getById(filmId).removeLike(userId);
        updateMostLikedFilms(filmId);
    }

    public Set<Film> getMostLikedFilms() {
        Set<Film> sortedMostLikedFilms = new TreeSet<>(comparator);
        sortedMostLikedFilms.addAll(mostLikedFilms);
        return sortedMostLikedFilms;
    }

    public void updateMostLikedFilms(long filmId) {
        if (filmStorage.getById(filmId).getRate() > 0) {
            mostLikedFilms.remove(filmStorage.getById(filmId));
            mostLikedFilms.add(filmStorage.getById(filmId));
        } else
            mostLikedFilms.remove(filmStorage.getById(filmId));
    }
}