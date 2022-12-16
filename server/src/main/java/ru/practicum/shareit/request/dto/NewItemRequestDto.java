package ru.practicum.shareit.request.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NewItemRequestDto {
    private String description;
}