package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.validate.OnCreate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class ItemDto {
    private long id;
    @NotBlank(groups = OnCreate.class)
    private String name;
    @NotBlank(groups = OnCreate.class)
    private String description;
    @NotNull(groups = OnCreate.class)
    private Boolean available;
    private List<Comment> comments;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comment {
        private long id;
        private String text;
        private long itemId;
        private long authorId;
        private String authorName;
        private LocalDateTime created;
    }
}
