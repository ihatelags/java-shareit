package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoWithBookingsTest {
    @Autowired
    private JacksonTester<ItemDtoWithBooking> json;

    @Test
    void testSerialize() throws Exception {
        ItemDtoWithBooking itemDtoWB = new ItemDtoWithBooking(1L, "item", "desc", true,
                new ArrayList<>(), null, null);
        JsonContent<ItemDtoWithBooking> result = json.write(itemDtoWB);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).hasJsonPath("$.name");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(itemDtoWB.getAvailable());
    }
}