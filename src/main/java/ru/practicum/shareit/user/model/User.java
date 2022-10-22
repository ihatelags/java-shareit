package ru.practicum.shareit.user.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class User {
    private long id;
    private String name;
    @NotBlank(message = "Необходимо задать email пользователя")
    @Email(message = "Email должен быть корректным")
    private String email;
}
