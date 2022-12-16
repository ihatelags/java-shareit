package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EntityScan("ru.practicum.shareit.user.dao")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream().map(userMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        return userMapper.toUserDto(getUser(userId));
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        final User userInStorage = getUser(userId);
        if (userDto.getName() != null) {
            userInStorage.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userInStorage.setEmail(userDto.getEmail());
        }
        userRepository.save(userInStorage);
        return userMapper.toUserDto(userInStorage);
    }

    @Override
    public void delete(long userId) {
        final User user = getUser(userId);
        userRepository.delete(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id: " + userId + " не существует"));
    }
}
