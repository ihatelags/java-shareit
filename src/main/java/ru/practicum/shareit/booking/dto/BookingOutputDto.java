package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingOutputDto {
    private long id;
    private User booker;
    private Item item;
    @JsonProperty("start")
    private LocalDateTime startDate;
    @JsonProperty("end")
    private LocalDateTime endDate;
    private BookingStatus status;

    @Data
    public static class Item {
        private final long id;
        private final String name;
    }

    @Data
    public static class User {
        private final long id;
    }
}
