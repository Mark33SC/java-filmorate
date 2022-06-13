package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getById(long id);

    Film addFilm(Film film);

    void removeFilm(long id);

    Film updateFilm(Film film);

    public boolean validationId(long id);
}