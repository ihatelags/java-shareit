package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.validate.OnCreate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll(HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI());
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId);
        return userService.getById(userId);
    }

    @PostMapping
    @Validated(OnCreate.class)
    public UserDto add(@Valid @RequestBody UserDto userDto, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, тело запроса {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userDto);
        return userService.add(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable long userId, @Valid @RequestBody UserDto userDto,
                          HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}, тело запроса: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId,
                userDto);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId, HttpServletRequest httpServletRequest) {
        log.info("Получен запрос {} к эндпоинту: {}, user id: {}",
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                userId);
        userService.delete(userId);
    }


}
