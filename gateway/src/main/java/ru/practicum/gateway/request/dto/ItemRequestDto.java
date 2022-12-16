package ru.practicum.gateway.request.dto;

import lombok.*;
import ru.practicum.gateway.item.dto.Create;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ItemRequestDto {
    @NotBlank(groups = {Create.class})
    private String description;
}
