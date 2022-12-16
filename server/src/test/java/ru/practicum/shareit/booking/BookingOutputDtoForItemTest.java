package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingOutputDtoForItemTest {
    @Autowired
    private JacksonTester<BookingOutputDtoForItem> json;

    @Test
    void testSerialize() throws Exception {
        BookingOutputDtoForItem bookingOutputDtoForItem = new BookingOutputDtoForItem(
                1L, 1L, 1L,
                LocalDateTime.of(2021, 11, 3, 19, 55),
                LocalDateTime.of(2022, 11, 8, 19, 55),
                BookingStatus.APPROVED);
        JsonContent<BookingOutputDtoForItem> result = json.write(bookingOutputDtoForItem);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.status");
        assertThat(result).extractingJsonPathValue("$.status").isEqualTo(bookingOutputDtoForItem.getStatus().toString());
    }
}