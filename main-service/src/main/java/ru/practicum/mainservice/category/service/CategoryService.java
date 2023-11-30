package ru.practicum.mainservice.category.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    boolean deleteCategory(Long catId);

    CategoryDto updateCategory(Long catId, CategoryDto categoryDto);

    CategoryDto findById(Long catId);

    List<CategoryDto> findAll(PageRequest pageRequest);
}