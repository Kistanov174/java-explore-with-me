package ru.practicum.ewm.server.service.compilation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.server.service.compilation.model.Compilation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    Object findCompilationByTitleIgnoreCase(String title);

    Page<Compilation> findAllByPinned(Boolean pinned, PageRequest pageRequest);
}