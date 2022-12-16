package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {
    private UserServiceImpl userService;
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
        UserDto dto = new UserDto(1L, "user 1", "user1@email");
        when(userRepository.save(user))
                .thenReturn(user);
        userService.saveUser(dto);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUserWithNotFoundUserExTest() {
        UserDto dto = new UserDto(1L, "user 1", "user1@email");
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1L, dto));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void updateUserWithNullNameAndEmailTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto dto = new UserDto(1L, null, null);
        when(userMapper.toUserDto(user))
                .thenReturn(new UserDto(1L, user.getName(), user.getEmail()));
        when(userRepository.save(user))
                .thenReturn(user);
        UserDto dtoNew = userService.updateUser(1L, dto);
        assertNotNull(dtoNew);
        assertEquals(dtoNew.getName(), user.getName());
        assertEquals(dtoNew.getEmail(), user.getEmail());
    }

    @Test
    void updateUserTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto dto = new UserDto(1L, "user", "user1@email");
        when(userMapper.toUserDto(user))
                .thenReturn(dto);
        when(userRepository.save(user))
                .thenReturn(user);
        assertEquals(dto, userService.updateUser(1L, dto));
    }

    @Test
    void getUserDtoTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        userService.get(1L);
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getWithNotFoundUserExTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());

        final var ex = assertThrows(RuntimeException.class, () -> userService.get(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void findAllUsersTest() {
        UserDto dto = new UserDto(1L, "user 1", "user1@email");
        userService.saveUser(dto);
        final List<User> allUsers = new ArrayList<>(Collections.singletonList(user));
        when(userRepository.findAll())
                .thenReturn(allUsers);

        final List<UserDto> userDtos = userService.findAllUsers();

        assertNotNull(userDtos);
        assertEquals(1, userDtos.size());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateAndDeleteUserByIdTest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        UserDto dto = new UserDto(1L, "user update", "user1@email");
        userService.updateUser(1L, dto);
        userService.deleteUserById(1L);
        verify(userRepository, times(1)).save(user);
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void deleteUserWithExceptionTest() {
        when(userRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());
        final var ex = assertThrows(RuntimeException.class, () -> userService.deleteUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }
}
