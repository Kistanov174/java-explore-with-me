package ru.practicum.mainservice.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.mainservice.event.dto.EventShortDto;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
public class CompilationDto {
    private Long id;
    private String title;
    private Boolean pinned;
    private Set<EventShortDto> events;
}