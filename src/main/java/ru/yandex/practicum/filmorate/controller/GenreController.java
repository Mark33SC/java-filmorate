package ru.yandex.practicum.filmorate.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.dao.genre.GenreDao;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final GenreDao genreDao;

    @Autowired
    private GenreController(GenreDao genreDao) {
        this.genreDao = genreDao;
    }

    @GetMapping
    public Collection<Genre> getAllGenre() {
        return genreDao.getAllGenre();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable int id) {
        return genreDao.getGenreById(id);
    }
}