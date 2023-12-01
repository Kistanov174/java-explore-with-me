package ru.practicum.mainservice.category.dto;

import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import ru.practicum.mainservice.config.Create;
import ru.practicum.mainservice.config.Update;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class CategoryDto {
    @NotNull(groups = {Update.class})
    private Long id;

    @NotBlank(message = "Поле name не должно быть пустым", groups = {Create.class})
    @NotBlank(message = "Поле name не должно быть пустым", groups = {Update.class})
    private String name;
}