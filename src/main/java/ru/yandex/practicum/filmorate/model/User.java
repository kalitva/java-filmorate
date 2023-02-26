package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import lombok.Data;

@Data
public class User {
    private Long id;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;

    private final Set<Long> friendIds = new HashSet<>();

    public void addFriendId(long friendId) {
        friendIds.add(friendId);
    }

    public void removeFriendId(long friendId) {
        friendIds.remove(friendId);
    }

    public Set<Long> getFriendIds() {
        return Collections.unmodifiableSet(friendIds);
    }
}
