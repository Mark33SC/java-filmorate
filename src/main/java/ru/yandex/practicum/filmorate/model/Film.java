package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.hibernate.validator.constraints.time.DurationMin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

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
    @DurationMin(nanos = 1)
    private Duration duration;
}
