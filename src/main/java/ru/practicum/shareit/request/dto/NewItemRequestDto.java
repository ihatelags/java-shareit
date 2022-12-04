package ru.practicum.shareit.request.dto;

import lombok.*;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewItemRequestDto {
    @NotBlank(groups = {OnCreate.class})
    private String description;
}