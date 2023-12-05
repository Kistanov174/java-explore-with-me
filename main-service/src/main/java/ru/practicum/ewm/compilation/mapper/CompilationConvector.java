package ru.practicum.ewm.compilation.mapper;

import lombok.Getter;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;

@Component
public class CompilationConvector {
    private final ModelMapper modelMapper;

    @Getter
    private final TypeMap<UpdateCompilationRequest, Compilation> propertyMapper;

    public CompilationConvector() {
        this.modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setSkipNullEnabled(true);
        this.propertyMapper = modelMapper.createTypeMap(UpdateCompilationRequest.class, Compilation.class);
    }

    public CompilationDto toDto(Compilation entity) {
        return modelMapper.map(entity, CompilationDto.class);
    }

    public Compilation toEntityFromDto(CompilationDto dto) {
        return modelMapper.map(dto, Compilation.class);
    }

    public Compilation toEntityFromUpdate(UpdateCompilationRequest upd) {
        return modelMapper.map(upd, Compilation.class);
    }

    public Compilation toEntityFromNew(NewCompilationDto newDto) {
        return modelMapper.map(newDto, Compilation.class);
    }
}