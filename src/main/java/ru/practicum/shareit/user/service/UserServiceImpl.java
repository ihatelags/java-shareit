package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(userRepository.getById(userId));
    }

    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.add(userDto));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        return UserMapper.toUserDto(userRepository.update(userId, userDto));
    }

    @Override
    public void delete(long userId) {
        userRepository.delete(userId);
    }
}
