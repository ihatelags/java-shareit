package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingOutputDtoForItem;
import ru.practicum.shareit.item.comment.CommentDto;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDtoWithBooking {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private List<CommentDto> comments;
    private BookingOutputDtoForItem lastBooking;
    private BookingOutputDtoForItem nextBooking;
}
