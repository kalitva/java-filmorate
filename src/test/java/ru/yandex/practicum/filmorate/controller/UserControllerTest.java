package ru.yandex.practicum.filmorate.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.validator.NotFoundException;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void get_shouldReturnUser() throws Exception {
        User user = user(u -> u.setId(1L));
        when(userService.getUser(1L)).thenReturn(user);
        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user)));
        verify(userService).getUser(1L);
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        when(userService.getAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
        verify(userService).getAll();
    }

    @Test
    void getAll_shouldReturnUsers() throws Exception {
        List<User> users = List.of(user(u -> u.setId(1L)), user(u -> u.setId(2L)));
        when(userService.getAll()).thenReturn(users);
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
        verify(userService).getAll();
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    void create_shouldResponseWithBadRequest_ifUserIsInvalid(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(userService);
    }

    private static Stream<Arguments> provideInvalidUsers() {
        return Stream.of(
                Arguments.of(user(u -> u.setEmail(null))),
                Arguments.of(user(u -> u.setEmail(""))),
                Arguments.of(user(u -> u.setEmail("email"))),
                Arguments.of(user(u -> u.setLogin(""))),
                Arguments.of(user(u -> u.setLogin("log in"))),
                Arguments.of(user(u -> u.setBirthday(LocalDate.now().plusYears(1))))
        );
    }

    @Test
    void update_shouldResponseWithNotFound_ifUserDoseNotExist() throws Exception {
        User user = user(u -> u.setId(1000L));
        when(userService.updateUser(user)).thenThrow(NotFoundException.class);
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isNotFound());
        verify(userService).updateUser(user);
    }

    @Test
    void addFriendTest() throws Exception {
        mockMvc.perform(put("/users/1/friends/2")).andExpect(status().isOk());
        verify(userService).addFriend(1, 2);
    }

    @Test
    void deleteFriendTest() throws Exception {
        mockMvc.perform(delete("/users/1/friends/2")).andExpect(status().isOk());
        verify(userService).deleteFriend(1, 2);
    }

    @Test
    void getFriendsTest() throws Exception {
        List<User> users = List.of(user(u -> u.setId(100L)), user(u -> u.setId(200L)));
        when(userService.getFriends(1)).thenReturn(users);
        mockMvc.perform(get("/users/1/friends"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
        verify(userService).getFriends(1);
    }

    @Test
    void getCommonFriendsTest() throws Exception {
        List<User> users = List.of(user(u -> u.setId(100L)), user(u -> u.setId(200L)));
        when(userService.getCommonFriends(1, 2)).thenReturn(users);
        mockMvc.perform(get("/users/1/friends/common/2"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(users)));
        verify(userService).getCommonFriends(1, 2);
    }

    private static User user() {
        User user = new User();
        user.setName("Name");
        user.setLogin("Login");
        user.setEmail("user@mail.com");
        user.setBirthday(LocalDate.parse("2000-01-01"));
        return user;
    }

    private static User user(Consumer<User> consumer) {
        User user = user();
        consumer.accept(user);
        return user;
    }
}