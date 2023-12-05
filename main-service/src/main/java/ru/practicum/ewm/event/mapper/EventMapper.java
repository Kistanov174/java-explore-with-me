package ru.practicum.ewm.event.mapper;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.model.Request;

@Component
public class EventMapper {
    private final ModelMapper modelMapper;
    @Getter
    private final TypeMap<EventUserDto, Event> propertyUpdEventUserReq;
    @Getter
    private final TypeMap<EventAdminDto, Event> propertyUpdEventAdminReq;

    public EventMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.propertyUpdEventAdminReq = modelMapper.createTypeMap(EventAdminDto.class, Event.class);
        this.propertyUpdEventUserReq = modelMapper.createTypeMap(EventUserDto.class, Event.class);
        modelMapper.createTypeMap(Request.class, RequestDto.class);
        propertyUpdEventUserReq.addMappings(m -> m.map(EventUserDto::getCategory, Event::setCategory));
        propertyUpdEventAdminReq.addMappings(m -> m.map(EventAdminDto::getCategory, Event::setCategory));
    }

    public Event mapToEventFromEventAdminDto(NewEventDto newEvent) {
        return modelMapper.map(newEvent, Event.class);
    }

    public EventFullDto mapToEventFullDtoFromEvent(Event event) {
        return modelMapper.map(event, EventFullDto.class);
    }

    public EventShortDto mapToEventShortDtoFromEvent(Event event) {
        return modelMapper.map(event, EventShortDto.class);
    }

    public Event mapToEventFromEventAdminDto(EventUserDto updatedEvent) {
        return modelMapper.map(updatedEvent, Event.class);
    }

    public Event mapToEventFromEventAdminDto(EventAdminDto updatedEvent) {
        return modelMapper.map(updatedEvent, Event.class);
    }

    public EventPublicDto mapToEventPublicDto(Event event) {
        return modelMapper.map(event, EventPublicDto.class);
    }
}