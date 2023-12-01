package ru.practicum.mainservice.user.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.AllArgsConstructor;

@Getter
@Builder(toBuilder = true)
@AllArgsConstructor
public class UserShortDto {
    private Long id;
    private String name;
}