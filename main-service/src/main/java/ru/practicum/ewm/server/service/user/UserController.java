package ru.practicum.ewm.server.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewm.server.service.user.dto.NewUserDto;
import ru.practicum.ewm.server.service.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

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