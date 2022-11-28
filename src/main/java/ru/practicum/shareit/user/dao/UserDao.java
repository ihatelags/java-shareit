package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    List<User> getAll();

    User getById(long userId);

    User add(UserDto userDto);

    User update(long userId, UserDto userDto);

    void delete(long userId);

}
