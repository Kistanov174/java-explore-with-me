package ru.practicum.ewm.server.service.request.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.server.service.request.dto.RequestDto;
import ru.practicum.ewm.server.service.request.model.Request;

@Component
public class RequestMapper {
    private final ModelMapper modelMapper;

    public RequestMapper() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        modelMapper.createTypeMap(Request.class, RequestDto.class)
                .addMappings(m -> m.map(req -> req.getRequester().getId(), RequestDto::setRequester))
                .addMappings(m -> m.map(req -> req.getEvent().getId(), RequestDto::setEvent));
    }

    public RequestDto toDto(Request entity) {
        return modelMapper.map(entity, RequestDto.class);
    }

    public Request toEntity(RequestDto dto) {
        return modelMapper.map(dto, Request.class);
    }
}