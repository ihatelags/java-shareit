package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.repository.UserRepository;
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

    private final UserRepository repository;

    @Override
    public List<UserDto> getAll() {
        return repository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto getById(long userId) {
        return UserMapper.toUserDto(getUser(userId));
    }

    @Transactional
    @Override
    public UserDto add(UserDto userDto) {
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User user = getUser(userId);
        userDto.setId(userId);
        User updatedUser = UserMapper.toUser(userDto);
        if (updatedUser.getEmail() == null) {
            updatedUser.setEmail(user.getEmail());
        }
        if (updatedUser.getName() == null) {
            updatedUser.setName(user.getName());
        }
        return UserMapper.toUserDto(repository.save(updatedUser));
    }

    @Override
    public void delete(long userId) {
        repository.deleteById(userId);
    }

    private User getUser(Long userId) {
        return repository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id: " + userId + " не существует"));
    }
}
