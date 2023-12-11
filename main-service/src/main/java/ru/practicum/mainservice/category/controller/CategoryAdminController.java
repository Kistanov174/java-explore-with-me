package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;

@Slf4j
@Validated
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryAdminController {
    private final CategoryService categoryService;

    @PostMapping
    ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Получен запрос POST на добавление категории {}", newCategoryDto.toString());
        return new ResponseEntity<>(categoryService.addCategory(newCategoryDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{catId}")
    ResponseEntity<CategoryDto> updateCategory(@Positive @PathVariable("catId") Long catId,
                                          @Valid @RequestBody CategoryDto categoryDto) {
        log.info("Получен запрос PATCH на обновление категории {}", categoryDto.toString());
        return ResponseEntity.ok(categoryService.updateCategory(catId, categoryDto));
    }

    @DeleteMapping("/{catId}")
    ResponseEntity<Boolean> deleteCategory(@Positive @PathVariable("catId") Long catId) {
        log.info("Получен запрос DELETE для категории по id {}", catId);
        return new ResponseEntity<>(categoryService.deleteCategory(catId), HttpStatus.NO_CONTENT);
    }
}