package ru.yandex.practicum.filmorate.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private long nextId;

    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        log.info("getting all films");
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        long id = ++nextId;
        log.info("adding a new film");
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("film not found, id " + film.getId());
        }
        log.info("updateing film {}", film.getId());
        films.put(film.getId(), film);
        return film;
    }
}
