package ru.yandex.practicum.filmorate.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    private long nextId;

    @Override
    public User getById(long id) {
        if (!users .containsKey(id)) {
            throw new NotFoundException("user not found, id " + id);
        }
        return users.get(id);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        long id = nextId++;
        user.setId(id);
        users.put(id, user);
        return user;
    }

    @Override
    public User update(User user) {
        if (!users .containsKey(user.getId())) {
            throw new NotFoundException("user not found, id " + user.getId());
        }
        users.put(user.getId(), user);
        return user;
    }
}
