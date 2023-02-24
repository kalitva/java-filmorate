package ru.yandex.practicum.filmorate.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(long id) {
        return userStorage.findById(id);
    }

    public List<User> getAll() {
        log.info("getting all users");
        return userStorage.findAll();
    }

    public User addUser(User user) {
        log.info("adding a new user");
        checkName(user);
        return userStorage.create(user);
    }

    public User updateUser(User user) {
        log.info("updateing user {}", user.getId());
        checkName(user);
        return userStorage.update(user);
    }

    public void addFriend(long id, long friendId) {
        log.info("user '{}' adding a new friend '{}'", id, friendId);
        if (userStorage.findById(friendId) == null) {
            throw new NotFoundException("User '" + id + "' does not exist");
        }
        userStorage.findById(id).addFriendId(friendId);
    }

    public void deleteFriend(long id, long friendId) {
        log.info("user '{}' removing a friend '{}'", id, friendId);
        if (userStorage.findById(friendId) == null) {
            throw new NotFoundException("User '" + id + "' does not exist");
        }
        userStorage.findById(id).removeFriendId(friendId);
    }

    public List<User> getFriends(long id) {
        return userStorage.findById(id).getFriendIds()
                .stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(long id, long otherId) {
        List<Long> friendIds = new ArrayList<>(userStorage.findById(id).getFriendIds());
        Set<Long> otherFriendIds = userStorage.findById(otherId).getFriendIds();
        friendIds.retainAll(otherFriendIds);
        return friendIds.stream()
                .map(userStorage::findById)
                .collect(Collectors.toList());
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
