package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.IncorrectParameterException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmService filmService, FilmStorage filmStorage) {
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
        Film addedFilm = filmStorage.addFilm(film);
        if (addedFilm.getRate() != 0) {
            filmService.updateMostLikedFilms(addedFilm.getId());
        }
        return addedFilm;
    }

    @PutMapping
    public @Valid Film updateFilm(@Valid @RequestBody Film film, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), film);
        Film updatedFilm = filmStorage.updateFilm(film);
        if (updatedFilm.getRate() != 0) {
            filmService.updateMostLikedFilms(updatedFilm.getId());
        }
        return updatedFilm;
    }

    @DeleteMapping("/{filmId}")
    public String removeFilm(@PathVariable long id) {
        filmStorage.removeFilm(id);
        return "Фильм успешно удален";
    }

    @GetMapping("/{filmId}")
    public Film getFilmById(@PathVariable long filmId) {
        if (filmId == 0) {
            throw new IncorrectParameterException("id");
        }
        return filmStorage.getById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public String addLike(@PathVariable long id, @PathVariable long userId) {
        filmService.addLike(id, userId);
        return "Лайк успешно добавлен";
    }

    @DeleteMapping("/{id}/like/{userId}")
    public String removeLike(@PathVariable long id, @PathVariable long userId) {
        filmService.removeLike(id, userId);
        return "Лайк удален";
    }

    @GetMapping("/popular")
    public Stream<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) Integer count) {
        if (count <= 0) {
            throw new IncorrectParameterException("Параметр count не может быть меньше 1");
        }
        return filmService.getMostLikedFilms().stream().limit(count);
    }
}