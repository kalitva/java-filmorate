package ru.yandex.practicum.filmorate.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.time.LocalDate;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setMockMvc() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @Test
    void getAll_shouldReturnEmptyList() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUsers")
    void create_shouldResponseWithBadRequest_ifUserIsInvalid(User user) throws Exception {
        String json = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
        mockMvc.perform(put("/users").contentType("application/json").content(json))
                .andExpect(status().isBadRequest());
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