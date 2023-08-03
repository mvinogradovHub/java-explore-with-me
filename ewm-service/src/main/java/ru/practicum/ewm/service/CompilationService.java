package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(CompilationNewDto compilation);

    CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilation);

    void deleteCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size);

    CompilationDto getCompilation(Long compId);

}
