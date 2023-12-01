package ru.practicum.mainservice.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.model.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto convertToDto(Category category);

    Category convert(CategoryDto categoryDto);
}