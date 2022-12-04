package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutputDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingOutputDtoTest {
    @Autowired
    private JacksonTester<BookingOutputDto> json;

    @Test
    void testSerialize() throws Exception {
        LocalDateTime timeStart = LocalDateTime.of(2023, 9, 10, 12, 0);
        LocalDateTime timeEnd = LocalDateTime.of(2023, 9, 11, 12, 0);
        BookingOutputDto bookingOutputDto = new BookingOutputDto(1L, new BookingOutputDto.User(1L),
                new BookingOutputDto.Item(1L, "item 1"),
                timeStart, timeEnd,
                BookingStatus.APPROVED);
        JsonContent<BookingOutputDto> result = json.write(bookingOutputDto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(bookingOutputDto.getStatus().toString());
    }
}