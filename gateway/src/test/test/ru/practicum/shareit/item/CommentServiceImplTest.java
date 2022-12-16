package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.exception.NotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class CommentServiceImplTest {

    private CommentService commentService;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingService bookingService;
    @Mock
    private CommentMapper commentMapper;

    private Comment comment;
    private User user;
    private Item item;
    private BookingDto bookingDto;
    private CommentDto commentDto;


    @BeforeEach
    void beforeEach() {
        commentService = new CommentServiceImpl(commentRepository, itemRepository, userRepository, bookingService, commentMapper);
        user = new User(1L, "user 1", "user1@email");
        item = new Item(1L, "дрель", "дрель ударная Макита", user, true, 1L);
        comment = new Comment(1L, "работает до 4 часов без подзаряда", item, user, null);
        bookingDto = new BookingDto(1L, LocalDateTime.of(2022, 10, 1, 11, 20), LocalDateTime.of(2022, 10, 4, 11, 20), new BookingDto.Item(1L, "дрель"), new BookingDto.Booker(1L), BookingStatus.APPROVED);
        commentDto = new CommentDto(1L, "работает до 4 часов без подзаряда", 1L, "user 1", null);
    }

    @Test
    void saveCommentTest() {
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(bookingService.getBookingOfUser(user.getId(), BookingState.PAST, 0, 10))
                .thenReturn(List.of(bookingDto));
        when(commentMapper.toComment(any(), any(), any()))
                .thenReturn(comment);
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        commentService.saveComment(commentDto, 1L);
        when(commentMapper.toCommentDto(any()))
                .thenReturn(commentDto);

        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).toCommentDto(comment);
    }

    @Test
    void saveCommentWithNotFoundItemExTest() {
        when(itemRepository.findById(anyLong()))
                .thenThrow(new NotFoundException());
        final var ex = assertThrows(RuntimeException.class, () -> commentService.saveComment(commentDto, 1L));
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void saveCommentWithIllegalArgumentExceptionTest() {
        Item itemNew = new Item(1L, "дрель", "дрель ударная Макита", user, false, 1L);
        User userNew = new User(2L, "user 2", "user2@email");
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemNew));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(userNew));
        final var ex = assertThrows(RuntimeException.class, () -> commentService.saveComment(commentDto, 1L));
        assertEquals("You can leave a comment only after booking a thing", ex.getMessage());
        verify(itemRepository, times(1)).findById(1L);
    }

}
