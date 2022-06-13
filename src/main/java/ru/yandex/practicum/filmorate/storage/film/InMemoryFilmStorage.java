package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long filmId = 0;
    private Map<Long, Film> films = new HashMap<>();

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getById(long id) {
        validationId(id);
        return films.get(id);
    }

    @Override
    public Film addFilm(Film film) {
        if (validation(film)) {
            long id = getNewId();
            film.setId(id);
            films.put(id, film);
            return film;
        } else {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    @Override
    public void removeFilm(long id) {
        validationId(id);
        films.remove(id);
    }

    @Override
    public Film updateFilm(Film film) {
        if (validation(film)) {
            validationId(film.getId());
            films.put(film.getId(), film);
            return film;
        } else {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
    }

    @Override
    public boolean validationId(long id) {
        if (films.containsKey(id)) {
            return true;
        } else {
            throw new FilmNotFoundException("Фильма с таким id не существует");
        }
    }

    private boolean validation(Film film) throws ValidationException {
        return !film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28));
    }

    private long getNewId() {
        return ++filmId;
    }
}