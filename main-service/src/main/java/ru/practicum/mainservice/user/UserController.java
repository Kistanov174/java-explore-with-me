package ru.practicum.mainservice.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.mainservice.config.Create;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.mapper.UserMapper;
import ru.practicum.mainservice.user.service.UserService;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userConverter;

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam (defaultValue = "0", required = false) @Min(0)  int from,
                                        @RequestParam (defaultValue = "10", required = false) @Min(1) int size,
                                        @RequestParam (required = false) Long[] ids) {
        log.info("GET request: запрос пользователя с id={}", ids);
        return userService.getUsers(ids, PageRequest.of(from / size, size)).stream()
                .map(userConverter::convertToDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public UserDto create(@Validated(Create.class)
                          @RequestBody UserDto userDto) {
        log.info("POST request: создание пользователя id={}", userDto.toString());
        return userConverter.convertToDto(userService.create(userDto));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable("userId") long userId) {
        log.info("DELETE request: удаление пользователя с id={}", userId);
        userService.delete(userId);
    }
}