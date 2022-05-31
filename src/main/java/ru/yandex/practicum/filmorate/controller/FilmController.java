package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static long filmId = 0;
    private Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film, HttpServletRequest request) {
        try {
            if (!validation(film)) {
                return film;
            }
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), film);
            films.put(film.getId(), film);
        } catch (ValidationException e) {
            log.info("Ошибка валидации: " + e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, HttpServletRequest request) {
        return add(film, request);
    }

    private boolean validation(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            throw new ValidationException("Дата релиза не может быть раньше 28.12.1895");
        }
        if (film.getId() == 0) {
            film.setId(getNewId());
        }
        if (film.getId() < 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "id должен быть больше положительным");
        }
        return true;
    }

    private long getNewId() {
        return ++filmId;
    }
}
