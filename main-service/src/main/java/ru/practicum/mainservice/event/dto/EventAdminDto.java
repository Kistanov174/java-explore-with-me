package ru.practicum.mainservice.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.Future;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import ru.practicum.mainservice.event.model.Location;
import ru.practicum.mainservice.event.model.StateActionByAdmin;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventAdminDto {
    @Size(max = 2000, min = 20)
    private String annotation;
    @Positive
    private Long category;
    @Size(max = 7000, min = 20)
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Future
    private LocalDateTime eventDate;
    private Location location;
    private Boolean paid;
    @PositiveOrZero
    private Integer participantLimit;
    private Boolean requestModeration;
    private StateActionByAdmin stateAction;
    @Size(max = 120, min = 3)
    private String title;
}