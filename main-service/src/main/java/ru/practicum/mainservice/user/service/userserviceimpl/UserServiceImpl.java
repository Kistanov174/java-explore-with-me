package ru.practicum.mainservice.user.service.userserviceimpl;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.DataNotFoundException;
import ru.practicum.mainservice.user.dto.NewUserDto;
import ru.practicum.mainservice.user.dto.UserDto;
import ru.practicum.mainservice.user.model.User;
import ru.practicum.mainservice.user.repository.UserRepository;
import ru.practicum.mainservice.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;


    @Transactional
    @Override
    public UserDto addUser(NewUserDto newUser) {
        if (checkIsUniqueEmail(newUser.getEmail())) {
            throw new ConflictException("could not execute statement; SQL [n/a];" +
                    " constraint [uq_email];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
        }
        User user = userRepository.save(modelMapper.map(newUser, User.class));
        log.info("Добавлен новый пользователь {}", user);
        return modelMapper.map(user, UserDto.class);
    }

    @Transactional
    @Override
    public boolean deleteUser(Long id) {
        userRepository.delete(getUserById(id));
        log.info("Пользователь с id = {} удален", id);
        return true;
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> findUsers(List<Long> ids, PageRequest page) {
        List<UserDto> usersDto;
        if (ids != null && !ids.isEmpty()) {
            usersDto = userRepository.findAllById(ids)
                    .stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        } else {
            usersDto = userRepository.findAll(page)
                    .stream()
                    .map(user -> modelMapper.map(user, UserDto.class))
                    .collect(Collectors.toList());
        }
        log.info("Запрошен список всех пользователей, результат: {}", usersDto);
        return usersDto;
    }

    private User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("User with id = " + id + " was not found"));
    }

    private boolean checkIsUniqueEmail(String email) {
        return userRepository.findUserByEmail(email) != null;
    }
}