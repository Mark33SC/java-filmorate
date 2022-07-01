package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, @Qualifier("filmDbStorage") FilmStorage filmStorage) {
        this.filmService = filmService;
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), film);
        return filmStorage.addFilm(film);
    }

    @PutMapping
    public @Valid Film updateFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), film);
        return filmStorage.updateFilm(film);
    }

    @DeleteMapping("/{id}")
    public void removeFilm(@PathVariable long id) {
        filmStorage.removeFilm(id);
    }

    @GetMapping("/{id}")
    public Optional<Film> getFilmById(@PathVariable long id) {
        return Optional.ofNullable(filmStorage.getFilmById(id));
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("Параметр count не может быть меньше 1");
        }
        return filmService.getMostLikedFilms(count);
    }
}