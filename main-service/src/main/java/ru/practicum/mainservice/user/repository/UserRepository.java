package ru.practicum.mainservice.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.mainservice.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Collection;

public interface UserRepository extends JpaRepository<User, Long> {
    Page<User> findAllByIdIn(Collection<Long> id, Pageable pageable);
}