package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getById(long userId);

    UserDto add(UserDto userDto);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);

}
