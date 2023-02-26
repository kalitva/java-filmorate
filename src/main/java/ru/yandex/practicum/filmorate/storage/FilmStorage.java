package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {

    List<Film> findAll();

    Film findById(long id);

    Film create(Film film);

    Film update(Film film);
}
