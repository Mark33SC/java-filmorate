package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static long filmId = 0;
    private Map<Long, Film> films = new HashMap<>();

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

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(long id) {
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

    @Override
    public Collection<Film> getMostLikedFilms(int count) { // переписал, не тестил
        Set<Film> sortedMostLikedFilms = new TreeSet<>(comparator);
        for (Film film : films.values()) {
            if (film.getRate() != null && film.getRate() > 0) {
                sortedMostLikedFilms.add(film);
            }
        }
        return sortedMostLikedFilms.stream().limit(count).collect(Collectors.toList());
    }

    private long getNewId() {
        return ++filmId;
    }
}