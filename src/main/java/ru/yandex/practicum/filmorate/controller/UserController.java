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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private long nextId;

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        log.info("getting all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addFilm(@Valid @RequestBody User user) {
        long id = ++nextId;
        log.info("adding a new user");
        user.setId(id);
        checkName(user);
        users.put(id, user);
        return user;
    }

    @PutMapping
    public User updateFilm(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("user not found, id " + user.getId());
        }
        log.info("updateing film {}", user.getId());
        checkName(user);
        users.put(user.getId(), user);
        return user;
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
