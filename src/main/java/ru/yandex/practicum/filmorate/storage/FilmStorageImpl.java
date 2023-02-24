package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

public class FilmStorageImpl implements FilmStorage {
    private long nextId;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film findById(long id) {
        return films.get(id);
    }

    @Override
    public List<Film> findAll() {
      return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        long id = ++nextId;
        film.setId(id);
        films.put(id, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new NotFoundException("film not found, id " + film.getId());
        }
        films.put(film.getId(), film);
        return film;
    }
}
