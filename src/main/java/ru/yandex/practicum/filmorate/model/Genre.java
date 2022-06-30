package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class Genre {
    @NotNull
    private int id;
    @JsonSerialize(using = Serializer.class)
    private String name;

    public Genre(int genre_id, String name) {
        this.id = genre_id;
        this.name = name;
    }
}