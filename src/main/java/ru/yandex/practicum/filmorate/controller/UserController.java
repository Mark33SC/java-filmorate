package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
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
@RequestMapping("/users")
public class UserController {
    private static long userId = 0;
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User add(@Valid @RequestBody User user, HttpServletRequest request) {
        try {
            if (!validation(user)) {
                return user;
            }
            log.info("Получен запрос к эндпоинту: {} {}, тело запроса {}", request.getMethod(),
                    request.getRequestURI(), user);
            users.put(user.getId(), user);
        } catch (ValidationException e) {
            log.info("Ошибка валидации: " + e.getMessage());
            throw new ValidationException(e.getMessage());
        }
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user, HttpServletRequest request) {
        return add(user, request);
    }

    private boolean validation(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getId() == 0) {
            user.setId(getNewId());
        }
        if (user.getId() < 0) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "id должен быть положительным");
        }
        return true;
    }

    private long getNewId() {
        return ++userId;
    }
}