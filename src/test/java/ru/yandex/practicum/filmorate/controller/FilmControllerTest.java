package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.yandex.practicum.filmorate.model.Film;

class FilmControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    private FilmController filmController = new FilmController();

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilms")
    void create_shouldResponseWithBadRequest_ifFilmIsInvalid(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_shouldResponseWithNotFound_ifFilmDoseNotExist() throws Exception {
        String json = objectMapper.writeValueAsString(film(f -> f.setId(1000L)));
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isNotFound());
    }

    private static Stream<Arguments> provideInvalidFilms() {
        return Stream.of(
                Arguments.of(film(f -> f.setName(""))),
                Arguments.of(film(f -> f.setName(null))),
                Arguments.of(film(f -> f.setDescription("a long string".repeat(20)))),
                Arguments.of(film(f -> f.setReleaseDate(LocalDate.parse("1000-01-01")))),
                Arguments.of(film(f -> f.setDuration(-1)))
        );
    }

    private static Film film() {
        Film film = new Film();
        film.setName("film");
        film.setDescription("description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.parse("2000-01-01"));
        return film;
    }

    private static Film film(Consumer<Film> consumer) {
        Film film = film();
        consumer.accept(film);
        return film;
    }
}