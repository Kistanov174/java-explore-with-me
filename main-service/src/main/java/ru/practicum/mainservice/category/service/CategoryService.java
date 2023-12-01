package ru.practicum.mainservice.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.model.Category;
import java.util.Collection;

public interface CategoryService {
    Collection<Category> getAll(Pageable pageable);

    Category getById(long categoryId);

    Category createOrUpdate(CategoryDto categoryDto);

    void delete(long categoryId);
}