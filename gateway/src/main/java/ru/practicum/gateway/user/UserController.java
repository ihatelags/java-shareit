package ru.practicum.gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.gateway.item.dto.Create;
import ru.practicum.gateway.user.dto.UserRequestDto;

import javax.validation.Valid;

@Validated
@RequiredArgsConstructor
@Controller
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /users.");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable long userId) {
        log.info("ShareIt-gateway: Получен GET запрос к эндпоинту /users/{}.",
                userId);
        return userClient.getById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> add(@Valid @Validated({Create.class}) @RequestBody UserRequestDto userRequestDto) {
        log.info("ShareIt-gateway: Получен POST запрос к эндпоинту /users. user dto: {}",
                userRequestDto);
        return userClient.add(userRequestDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid @PathVariable("userId") long userId,
                                         @RequestBody UserRequestDto userRequestDto) {
        log.info("ShareIt-gateway: Получен PATCH запрос к эндпоинту /users/{}.",
                userId);
        return userClient.update(userId, userRequestDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable("userId") long userId) {
        log.info("ShareIt-gateway: Получен DELETE запрос к эндпоинту /users/{}.",
                userId);
        return userClient.delete(userId);
    }

}
