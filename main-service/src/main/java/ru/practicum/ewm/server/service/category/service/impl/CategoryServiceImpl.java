package ru.practicum.ewm.server.service.category.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.server.service.category.dto.CategoryDto;
import ru.practicum.ewm.server.service.category.dto.NewCategoryDto;
import ru.practicum.ewm.server.service.category.model.Category;
import ru.practicum.ewm.server.service.category.repository.CategoryRepository;
import ru.practicum.ewm.server.service.category.service.CategoryService;
import ru.practicum.ewm.server.service.event.repository.EventRepository;
import ru.practicum.ewm.server.service.exception.ConflictException;
import ru.practicum.ewm.server.service.exception.DataNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final ModelMapper modelMapper;

    @Transactional
    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        if (checkIsUniqueName(newCategoryDto.getName())) {
            throw new ConflictException("could not execute statement;" +
                    " SQL [n/a]; constraint [uq_category_name];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        Category category = categoryRepository.save(modelMapper.map(newCategoryDto, Category.class));
        CategoryDto categoryDto = modelMapper.map(category, CategoryDto.class);
        log.info("Добавлена новая категория {}", categoryDto);
        return categoryDto;
    }

    @Transactional
    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        String newName = categoryDto.getName();

        if (checkIsUniqueName(newName) && !getCategory(catId).getName().equals(newName)) {
            throw new ConflictException("could not execute statement;" +
                    " SQL [n/a]; constraint [uq_category_name];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        Category category = getCategory(catId);
        category.setName(newName);
        CategoryDto actualCategoryDto = modelMapper.map(categoryRepository.save(category), CategoryDto.class);
        log.info("Название категории обновлено {}", actualCategoryDto);
        return actualCategoryDto;
    }

    @Transactional
    @Override
    public boolean deleteCategory(Long catId) {
        if (!eventRepository.findAllByCategory_Id(catId).isEmpty()) {
            throw new ConflictException("The category is not empty");
        }
        categoryRepository.delete(getCategory(catId));
        log.info("Категория с id {} удалена", catId);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto findById(Long catId) {
        CategoryDto categoryDto = modelMapper.map(getCategory(catId), CategoryDto.class);
        log.info("Запрошена категория по id {}", categoryDto);
        return categoryDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> findAll(PageRequest page) {
        List<CategoryDto> categories = categoryRepository.findAll(page)
                .stream()
                .map(category -> modelMapper.map(category, CategoryDto.class))
                .collect(Collectors.toList());
        log.info("Запрошен список категорий {}", categories);
        return categories;
    }

    private Category getCategory(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new DataNotFoundException("Category with id=" + catId + " was not found"));
    }

    private boolean checkIsUniqueName(String name) {
        return categoryRepository.findCategoryByNameIgnoreCase(name) != null;
    }
}