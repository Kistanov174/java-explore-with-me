package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.service.CategoryService;
import ru.practicum.mainservice.config.Create;
import ru.practicum.mainservice.config.Update;

import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping("/categories")
    public Collection<CategoryDto> getAll(@RequestParam(defaultValue = "0", required = false) @Min(0)  int from,
                                          @RequestParam (defaultValue = "10", required = false) @Min(1) int size) {
        log.info("GET request: запрос всех категорий");
        return categoryService.getAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::convertToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getById(@PathVariable("catId") long categoryId) {
        log.info("GET request: запрос категории c id={}", categoryId);
        return categoryMapper.convertToDto(categoryService.getById(categoryId));
    }

    @PostMapping("/admin/categories")
    public CategoryDto create(@Validated({Create.class}) @RequestBody CategoryDto categoryDto) {
        log.info("POST request: создание категории:{}", categoryDto);
        return categoryMapper.convertToDto(categoryService.createOrUpdate(categoryDto));
    }

    @PatchMapping("/admin/categories")
    public CategoryDto update(@Validated({Update.class}) @RequestBody CategoryDto categoryDto) {
        log.info("PATCH request: обновление категории:{}", categoryDto);
        return categoryMapper.convertToDto(categoryService.createOrUpdate(categoryDto));
    }

    @DeleteMapping("/admin/categories/{catId}")
    public void delete(@PathVariable("catId") long categoryId) {
        log.info("DELETE request: удаление категории с id={}", categoryId);
        categoryService.delete(categoryId);
    }
}