package ru.practicum.mainservice.compilation.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.config.EntityReferenceMapper;

@Mapper(componentModel = "spring", uses = {EntityReferenceMapper.class})
public interface CompilationMapper {
    Compilation convert(NewCompilationDto dto);

    CompilationDto convertToDto(Compilation compilation);
}