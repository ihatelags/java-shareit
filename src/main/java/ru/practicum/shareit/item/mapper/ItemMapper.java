package ru.practicum.shareit.item.mapper;

import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                new ArrayList<>()
        );
    }

    public static ItemDtoWithBooking toItemDtoWithBooking(Item item, List<Comment> comments,
                                                          Booking lastBooking, Booking nextBooking) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                comments.size() != 0 ? comments.stream()
                        .map(CommentMapper::toItemWithBookingComment).collect(Collectors.toList()) : new ArrayList<>(),
                lastBooking != null ? BookingMapper.toBookingDtoForItem(lastBooking) : null,
                nextBooking != null ? BookingMapper.toBookingDtoForItem(nextBooking) : null
        );
    }

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getId(),
                null,
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.getAvailable()
        );
    }

}
