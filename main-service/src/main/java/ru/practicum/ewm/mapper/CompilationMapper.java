package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.compilation.CompilationDto;
import ru.practicum.ewm.dto.compilation.NewCompilationDto;
import ru.practicum.ewm.model.compilation.Compilation;

import java.util.stream.Collectors;

public class CompilationMapper {
    public static Compilation compilationToNewCompilationDto(NewCompilationDto c) {
        return Compilation.builder()
                .title(c.getTitle())
                .pinned(c.isPinned())
                .build();
    }

    public static CompilationDto compilationToCompilationDto(Compilation c) {
        return CompilationDto.builder()
                .events(c.getEvents().stream().map(EventMapper::eventToEventShortDto).collect(Collectors.toList()))
                .id(c.getId())
                .pinned(c.isPinned())
                .title(c.getTitle())
                .build();
    }
}
