package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private List<Comment> comments;
    private BookingOutputDtoForItem lastBooking;
    private BookingOutputDtoForItem nextBooking;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private long id;
        private String text;
        private long itemId;
        private long authorId;
        private String authorName;
        private LocalDateTime created;
    }
}
