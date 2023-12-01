package ru.practicum.mainservice.user.service;

import ru.practicum.mainservice.user.dto.UserDto;
import org.springframework.data.domain.Pageable;
import ru.practicum.mainservice.user.model.User;
import java.util.Collection;

public interface UserService {
    User create(UserDto userDto);

    void delete(long userId);

    Collection<User> getUsers(Long[] ids, Pageable pageable);
}