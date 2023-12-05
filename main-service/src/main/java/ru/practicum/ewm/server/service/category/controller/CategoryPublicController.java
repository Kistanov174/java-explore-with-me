package ru.practicum.ewm.server.service.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.ewm.server.service.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryPublicController {
    private final CategoryService categoryService;

    @GetMapping
    ResponseEntity<Object> findAll(
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET запрос на просмотр категорий");
        return new ResponseEntity<>(categoryService.findAll(PageRequest.of(from / size, size)), HttpStatus.OK);
    }

    @GetMapping("/{catId}")
    ResponseEntity<Object> findById(@Positive @PathVariable("catId") Long catId) {
        log.info("Получен GET запрос на просмотр категории по id {}", catId);
        return new ResponseEntity<>(categoryService.findById(catId), HttpStatus.OK);
    }
}