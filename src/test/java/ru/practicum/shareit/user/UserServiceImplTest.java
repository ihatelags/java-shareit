package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    private User user;

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(userRepository, userMapper);
        user = new User(1L, "user 1", "user1@email");
    }

    @Test
    void saveUserTest() {
        when(userRepository.save(user))
                .thenReturn(user);

        assertNotNull(user);
        assertEquals(1, user.getId());
    }

    @Test
    void getUserDtoTest() {
        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));
        userService.getById(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findAllUsersTest() {
        UserDto dto = new UserDto(1L, "user 1", "user1@email");
        userService.add(dto);
        final List<User> allUsers = new ArrayList<>(Collections.singletonList(user));
        when(userRepository.findAll())
                .thenReturn(allUsers);

        final List<UserDto> userDtos = userService.getAll();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto dto = new UserDto(1L, "user", "user1@email");
        when(userMapper.toUserDto(user))
                .thenReturn(dto);
        when(userRepository.save(user))
                .thenReturn(user);
        assertEquals(dto, userService.update(1L, dto));
    }

    @Test
    void deleteTest() {
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        userService.delete(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void getUserWithExceptionTest() {
        when(userRepository.findById(1L))
                .thenThrow(new UserNotFoundException("Message"));
        final var ex = assertThrows(RuntimeException.class, () -> userService.getById(1L));
        verify(userRepository, times(1)).findById(1L);
    }
}