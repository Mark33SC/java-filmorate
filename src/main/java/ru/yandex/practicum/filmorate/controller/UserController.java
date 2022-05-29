package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping("/users")
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping("/users")
    public User add(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), user);
        users.put(user.getId(), user);

        return user;
    }

    @PutMapping("/users")
    public User update(@Valid @RequestBody User user, HttpServletRequest request) {
        log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                request.getRequestURI(), user);
        users.put(user.getId(), user);
        return user;
    }
}
