package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserDto {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @Email(groups = OnCreate.class)
    @NotBlank(groups = OnCreate.class)
    private String email;
}
