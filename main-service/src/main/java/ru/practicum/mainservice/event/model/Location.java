package ru.practicum.mainservice.event.model;

import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import javax.persistence.Embeddable;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Embeddable
public class Location {
    private Float lat;
    private Float lon;
}