package ru.practicum.mainservice.category.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.exception.NotFoundException;
import java.util.Collection;
import static ru.practicum.mainservice.config.Constant.CATEGORY;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public Collection<Category> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(
                () -> new NotFoundException(CATEGORY, categoryId));
    }

    @Override
    @Transactional
    public Category createOrUpdate(CategoryDto categoryDto) {
        Category category = categoryMapper.convert(categoryDto);
        return categoryRepository.save(category);
    }

    @Override
    @Transactional
    public void delete(long categoryId) {
        categoryRepository.deleteById(categoryId);
    }
}