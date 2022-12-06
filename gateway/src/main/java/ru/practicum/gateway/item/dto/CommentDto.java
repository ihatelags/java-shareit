package ru.practicum.gateway.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode(of = "id")

public class CommentDto {

    private Long id;
    @NotBlank(groups = {Create.class}, message = "Текст комментария не может быть пустым")
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime dateOfCreation;

}
