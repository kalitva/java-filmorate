package ru.yandex.practicum.filmorate.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film getFilm(long id) {
        return filmStorage.findById(id);
    }

    public List<Film> getAll() {
        log.info("getting all films");
        return filmStorage.findAll();
    }

    public Film addFilm(Film film) {
        log.info("adding a new film");
        return filmStorage.create(film);
    }

    public Film updateFilm(Film film) {
        log.info("updateing film {}", film.getId());
        return filmStorage.update(film);
    }

    public void addLike(long id, long userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("User '" + id + "' does not exist");
        }
        log.info("new like to film {}", id);
        filmStorage.findById(id).addLike(userId);
    }

    public void removeLike(long id, long userId) {
        if (userStorage.findById(userId) == null) {
            throw new NotFoundException("User '" + id + "' does not exist");
        }
        log.info("remove like to film {}", id);
        filmStorage.findById(id).removeLike(userId);
    }

    public List<Film> getMostPopular(int count) {
        return filmStorage.findAll().stream()
                .sorted(Comparator.comparing(f -> f.getLikedUserIds().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }
}
