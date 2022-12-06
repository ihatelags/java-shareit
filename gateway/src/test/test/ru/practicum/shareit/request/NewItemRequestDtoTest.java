package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.NewItemRequestDto;


import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class NewItemRequestDtoTest {
    @Autowired
    private JacksonTester<NewItemRequestDto> json;

    @Test
    void testSerialize() throws Exception {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto("газонокосилка");
        JsonContent<NewItemRequestDto> result = json.write(newItemRequestDto);
        assertThat(result).hasJsonPath("$.description");
        assertThat(result).extractingJsonPathValue("$.description").isEqualTo(newItemRequestDto.getDescription());
    }
}
