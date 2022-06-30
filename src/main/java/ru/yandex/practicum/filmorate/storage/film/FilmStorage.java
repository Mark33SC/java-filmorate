package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(long id);

    Film addFilm(Film film);

    void removeFilm(long id);

    Film updateFilm(Film film);

    boolean validationId(long id);

    Collection<Film> getMostLikedFilms(int count);
}