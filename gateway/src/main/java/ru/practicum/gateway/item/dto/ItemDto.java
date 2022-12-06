package ru.practicum.gateway.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")
public class ItemDto {
    @NotNull(groups = {Update.class})
    private long id;
    @NotBlank(groups = {Create.class}, message = "Название вещи не может быть пустым")
    private String name;
    @NotBlank(groups = {Create.class}, message = "Описание вещи не может быть пустым")
    private String description;
    @NotNull(groups = {Create.class}, message = "Нет доступа к предмету")
    private Boolean available;
    private Long requestId;
}
