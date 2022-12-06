package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoWithBookingsTest {
    @Autowired
    private JacksonTester<ItemDtoWithBookings> json;

    @Test
    void testSerialize() throws Exception {
        ItemDtoWithBookings itemDtoWB = new ItemDtoWithBookings(1L, "дрель", "дрель ударная Макита", true, 1L, null, null, null);

        JsonContent<ItemDtoWithBookings> result = json.write(itemDtoWB);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoWB.getAvailable());
    }
}
