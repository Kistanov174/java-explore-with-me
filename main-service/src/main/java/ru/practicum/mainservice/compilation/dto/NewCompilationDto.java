package ru.practicum.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.config.Create;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class NewCompilationDto {
    private Set<Long> events;

    @NotNull(message = "Поле title не должно быть пустым", groups = {Create.class})
    private String title;

    private Boolean pinned;
}