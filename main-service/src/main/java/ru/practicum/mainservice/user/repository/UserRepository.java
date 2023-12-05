package ru.practicum.mainservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.user.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Object findUserByEmail(String email);
}
