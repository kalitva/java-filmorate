package ru.yandex.practicum.filmorate.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private FilmService filmService;

    @InjectMocks
    private FilmController filmController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(filmController).build();
    }

    @Test
    void get_shouldReturnFilm() throws Exception {
        Film film = film(u -> u.setId(1L));
        when(filmService.getFilm(1L)).thenReturn(film);
        mockMvc.perform(get("/films/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film)));
        verify(filmService).getFilm(1L);
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        when(filmService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(filmService).getAll();
    }

    @Test
    void getAll_shouldReturnFilms() throws Exception {
        List<Film> films = List.of(film(u -> u.setId(1L)), film(u -> u.setId(2L)));
        when(filmService.getAll()).thenReturn(films);
        mockMvc.perform(get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));
        verify(filmService).getAll();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidFilms")
    void create_shouldResponseWithBadRequest_ifFilmIsInvalid(Film film) throws Exception {
        String json = objectMapper.writeValueAsString(film);
        mockMvc.perform(post("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(filmService);
    }

    @Test
    void update_shouldResponseWithNotFound_ifFilmDoseNotExist() throws Exception {
        Film film = film(f -> f.setId(1000L));
        String json = objectMapper.writeValueAsString(film);
        when(filmService.updateFilm(film)).thenThrow(NotFoundException.class);
        mockMvc.perform(put("/films").contentType("application/json").content(json))
                .andExpect(status().isNotFound());
        verify(filmService).updateFilm(film);
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
    void addLikeTest() throws Exception {
        mockMvc.perform(put("/films/1/like/2")).andExpect(status().isOk());
        verify(filmService).addLike(1, 2);
    }

    @Test
    void deleteLikeTest() throws Exception {
        mockMvc.perform(delete("/films/1/like/2")).andExpect(status().isOk());
        verify(filmService).removeLike(1, 2);
    }

    @Test
    void getMostPopularTest() throws Exception {
        List<Film> films = List.of(film(u -> u.setId(100L)), film(u -> u.setId(200L)));
        when(filmService.getMostPopular(2)).thenReturn(films);
        mockMvc.perform(get("/films/popular").param("count", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(films)));
        verify(filmService).getMostPopular(2);
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