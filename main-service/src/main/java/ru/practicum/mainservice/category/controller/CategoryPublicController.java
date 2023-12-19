package ru.practicum.mainservice.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.service.CategoryService;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    ResponseEntity<List<CategoryDto>> findAll(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET запрос на просмотр категорий");
        return ResponseEntity.ok(categoryService.findAll(PageRequest.of(from / size, size)));
    }

    @GetMapping("/{catId}")
    ResponseEntity<CategoryDto> findById(@Positive @PathVariable("catId") Long catId) {
        log.info("Получен GET запрос на просмотр категории по id {}", catId);
        return ResponseEntity.ok(categoryService.findById(catId));
    }
}