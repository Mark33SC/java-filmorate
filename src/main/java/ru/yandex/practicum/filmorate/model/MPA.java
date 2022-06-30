package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class MPA {
    @NotNull
    private int id;
    @JsonSerialize(using = Serializer.class)
    private String name;

    public MPA(int mpa_rate_id, String name) {
        this.id = mpa_rate_id;
        this.name = name;
    }
}