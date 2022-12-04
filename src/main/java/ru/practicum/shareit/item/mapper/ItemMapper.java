package ru.practicum.shareit.item.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemMapper {
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    public BookingDto toItemDto(Item item) {
        return new BookingDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequestId(),
                new ArrayList<>()
        );
    }

    public ItemDtoWithBooking toItemDtoWithBooking(Item item, List<Comment> comments,
                                                   Booking lastBooking, Booking nextBooking) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                comments.size() != 0 ? comments.stream()
                        .map(commentMapper::toItemWithBookingComment).collect(Collectors.toList()) : new ArrayList<>(),
                lastBooking != null ? bookingMapper.toBookingDtoForItem(lastBooking) : null,
                nextBooking != null ? bookingMapper.toBookingDtoForItem(nextBooking) : null
        );
    }

    public Item toItem(BookingDto itemDto, User owner) {
        return new Item(
                itemDto.getId(),
                owner,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable(),
                itemDto.getRequestId()
        );
    }

}
