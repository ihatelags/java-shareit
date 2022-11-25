package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingInputDto {
    private long id;
    private Long bookerId;
    @NotNull
    private Long itemId;
    @NotNull
    @JsonProperty("start")
    private LocalDateTime startDate;
    @NotNull
    @JsonProperty("end")
    private LocalDateTime endDate;
    private BookingStatus status;

}
