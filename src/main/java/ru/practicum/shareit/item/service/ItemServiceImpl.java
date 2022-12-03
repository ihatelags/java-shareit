package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.comment.CommentsRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentsRepository commentsRepository;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Override
    public ItemDtoWithBooking getById(long itemId, long userId) {
        final Item item = getItem(itemId);
        List<Comment> comments = commentsRepository.findAllByItemId(itemId);
        if (!item.getOwner().getId().equals(userId)) {
            return itemMapper.toItemDtoWithBooking(getItem(itemId), comments,
                    null, null);
        }

        Booking lastBooking = bookingRepository.findFirstBookingByItemIdAndEndIsBeforeOrderByEndDesc(itemId,
                LocalDateTime.now()).orElse(null);
        Booking nextBooking = bookingRepository.findFirstBookingByItemIdAndStartIsAfterOrderByStart(itemId,
                LocalDateTime.now()).orElse(null);

        return itemMapper.toItemDtoWithBooking(item, comments, lastBooking, nextBooking);
    }

    @Override
    public List<ItemDtoWithBooking> getAllByUserId(long userId, int from, int size) {
        int page = from / size;
        List<Item> items = new ArrayList<>(itemRepository.findAllByOwner(userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("Такого пользователя не существует")),
                PageRequest.of(page, size)));
        return items.stream().map(item -> itemMapper
                        .toItemDtoWithBooking(item, commentsRepository.findAllByItemId(item.getId()),
                                bookingRepository.findFirstBookingByItemIdAndEndIsBeforeOrderByEndDesc(item.getId(),
                                        LocalDateTime.now()).orElse(null),
                                bookingRepository.findFirstBookingByItemIdAndStartIsAfterOrderByStart(item.getId(),
                                        LocalDateTime.now()).orElse(null)))
                .sorted(Comparator.comparing(ItemDtoWithBooking::getId)).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> searchByText(String text, int from, int size) {
        int page = from / size;
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.searchByText(text, PageRequest.of(page, size)).stream()
                .filter(Item::isAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto add(long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Такого пользователя" +
                " не существует"));
        Item item = itemMapper.toItem(itemDto, owner);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(long userId, long itemId, ItemDto itemDto) {
        Item item = getItem(itemId);
        checkOwner(userId, itemId);
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void delete(long userId, long itemId) {
        checkOwner(userId, itemId);
        itemRepository.deleteById(itemId);
    }

    @Override
    public CommentDto addComment(long userId, long itemId, CommentDto commentDto) {
        Comment createdComment = commentMapper.toComment(commentDto, getUser(userId), getItem(itemId));
        if (createdComment.getText().isBlank()) {
            throw new InvalidCommentException("Комментарий не может быть пустым");
        }
        if (checkBooking(userId, itemId)) {
            return commentMapper.toCommentDto(commentsRepository
                    .save(createdComment));
        } else {
            throw new ItemAvailableException("Пользователь " + userId + " не брал вещь " + itemId + " в аренду");
        }
    }

    private boolean checkBooking(long userId, long itemId) {
        return bookingRepository.existsBookingByBookerIdAndItemIdAndStatusEqualsAndEndIsBefore(userId,
                itemId, BookingStatus.APPROVED, LocalDateTime.now());
    }

    private Item getItem(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException("Предмета с таким id: " + itemId + " не существует"));
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователя с таким id: " + userId + " не существует"));
    }

    private void checkOwner(Long userId, Long itemId) {
        Item item = getItem(itemId);
        if (item.getOwner().getId() != userId) {
            throw new AccessErrorException("Пользователь не является владельцем вещи");
        }
    }
}
