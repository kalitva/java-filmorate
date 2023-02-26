package ru.yandex.practicum.filmorate.storage;

import java.util.List;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {

    User findById(long id);

    List<User> findAll();

    User create(User user);

    User update(User user);
}
