package ru.practicum.mainservice.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.dto.EventFullDto;
import ru.practicum.mainservice.event.dto.AdminUpdateEventDto;
import ru.practicum.mainservice.event.dto.UpdateEventRequest;
import ru.practicum.mainservice.event.dto.NewEventDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.user.mapper.UserMapper;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryService.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {
    @Mapping(target = "category", source = "category")
    @Mapping(target = "lon", source = "location.lon")
    @Mapping(target = "lat", source = "location.lat")
    @Mapping(target = "state", constant = "PENDING")
    @Mapping(target = "confirmedRequests", constant = "0")
    @Mapping(target = "participantLimit", source = "participantLimit", defaultValue = "0")
    @Mapping(target = "requestModeration", source = "requestModeration", defaultValue = "true")
    @Mapping(target = "paid", source = "paid", defaultValue = "false")
    Event convert(NewEventDto newEventDto);

    @Mapping(target = "location.lon", source = "lon")
    @Mapping(target = "location.lat", source = "lat")
    EventFullDto convertToFullDto(Event event);

    EventShortDto convertToShortDto(Event event);

    @Mapping(target = "event.id", source = "eventId")
    void updateFromEventDto(UpdateEventRequest updateEventRequest, @MappingTarget Event event);

    Event updateFromAdminDto(AdminUpdateEventDto adminUpdateEventRequest, @MappingTarget Event event);
}