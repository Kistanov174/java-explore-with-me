package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.practicum.mainservice.config.Create;
import ru.practicum.mainservice.event.model.Location;
import ru.practicum.mainservice.validation.EventCreationDate;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import static ru.practicum.mainservice.config.Constant.DATE_FORMAT;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class NewEventDto {
    @Size(min = 20, max = 2000, groups = {Create.class})
    @NotNull(message = "Поле annotation не должно быть пустым", groups = {Create.class})
    private String annotation;

    @NotNull(message = "Поле categoryId не должно быть пустым", groups = {Create.class})
    private Long category;

    @Size(min = 20, max = 7000, groups = {Create.class})
    @NotNull(message = "Поле description не должно быть пустым", groups = {Create.class})
    private String description;

    @EventCreationDate(groups = {Create.class})
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_FORMAT)
    private LocalDateTime eventDate;

    @NotNull(message = "Поле location не должно быть пустым", groups = {Create.class})
    private Location location;

    private Boolean paid;

    @PositiveOrZero
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120, groups = {Create.class})
    @NotNull(message = "Поле title не должно быть пустым", groups = {Create.class})
    private String title;
}