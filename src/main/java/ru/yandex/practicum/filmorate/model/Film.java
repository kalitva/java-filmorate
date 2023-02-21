package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validator.After;

@Data
public class Film {
    private static final String FILMS_BIRTHDAY = "1885-12-28";

    private Long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @NotNull
    @After(FILMS_BIRTHDAY)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate releaseDate;

    @Positive
    private long duration;
}
