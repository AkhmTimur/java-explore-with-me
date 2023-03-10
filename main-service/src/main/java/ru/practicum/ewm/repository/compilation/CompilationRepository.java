package ru.practicum.ewm.repository.compilation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.event.compilation.Compilation;

import java.util.List;


public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    List<Compilation> findAllByIdIsGreaterThanEqualAndPinnedIs(Long from, boolean pinned, Pageable pageable);
}
