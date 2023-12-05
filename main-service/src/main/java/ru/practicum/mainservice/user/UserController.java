package ru.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import ru.practicum.mainservice.user.dto.NewUserDto;
import ru.practicum.mainservice.user.service.UserService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    ResponseEntity<Object> createUser(@Valid @RequestBody NewUserDto newUserDto) {
        log.info("Получен запрос POST на создание пользователя {}", newUserDto.toString());
        return new ResponseEntity<>(userService.addUser(newUserDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{userId}")
    ResponseEntity<Object> deleteUserById(@Positive @PathVariable("userId") Long id) {
        log.info("Получен запрос DELETE для пользователя по id {}", id);
        return new ResponseEntity<>(userService.deleteUser(id), HttpStatus.NO_CONTENT);
    }

    @GetMapping
    ResponseEntity<Object> findAllUsers(
            @RequestParam(required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(defaultValue = "0") int from,
            @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET запрос на просмотр пользователей");
        return new ResponseEntity<>(userService.findUsers(ids, PageRequest.of(from / size, size)), HttpStatus.OK);
    }
}