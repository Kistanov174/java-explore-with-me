package ru.practicum.ewm.server.service.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.server.service.category.model.Category;
import ru.practicum.ewm.server.service.user.model.User;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventPublicDto {
    private Long id;
    private String annotation;
    private Category category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private User initiator;
    private boolean paid;
    private String title;
    private Long views;
}