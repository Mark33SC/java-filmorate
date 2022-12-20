package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import java.time.Duration;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Data
public class Film {
    private long id;
    @NotBlank
    private String name;
    @Size(min = 1, max = 200)
    @NotBlank
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Min(1)
    private int duration;
    private Set<Long> likes;
    private Integer rate;
    private Set<Genre> genres;
    @NotNull
    private MPA mpa;

    public Film(long id, String name, String description, LocalDate releaseDate, int duration, Integer rate,
                Collection<Genre> genres, MPA mpa, Collection<Long> likes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.rate = rate;
        if (genres != null) {
            this.genres = new HashSet<>(genres);
        } else {
            this.genres = null;
        }
        if (likes != null) {
            this.likes = new HashSet<>(likes);
        } else {
            this.likes = null;
        }
        this.mpa = mpa;
    }

    public void addLike(long userId) {
        likes.add(userId);
        ++rate;
    }

    public void removeLike(long userId) {
        if (likes.contains(userId)) {
            likes.remove(userId);
            --rate;
        } else
            throw new UserNotFoundException("Пользователь с таким id не ставил лайк этому фильму");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return id == film.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}