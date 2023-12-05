package ru.practicum.ewm.server.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import ru.practicum.ewm.server.service.event.model.Location;
import ru.practicum.ewm.server.service.event.model.StateAction;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventUserDto {
    @Size(max = 2000, min = 20)
    private String annotation;
    @Positive
    private Long category;
    @Size(max = 7000, min = 20)
    private String description;
    @Future
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    @Enumerated(EnumType.STRING)
    private StateAction stateAction;
    @Size(max = 120, min = 3)
    private String title;
}