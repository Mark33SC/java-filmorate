package ru.yandex.practicum.filmorate.storage.dao.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

public interface GenreDao {
    Genre getGenreById(int id);

    Collection<Genre> getAllGenre();
}