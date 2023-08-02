package ru.practicum.ewm.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.mapper.CompilationMapper;
import ru.practicum.ewm.model.Compilation;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.QCompilation;
import ru.practicum.ewm.repository.CompilationRepository;
import ru.practicum.ewm.repository.EventRepository;
import ru.practicum.ewm.service.CompilationService;
import ru.practicum.ewm.utils.PageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto addCompilation(CompilationNewDto compilation) {
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        Compilation comp = compilationMapper.compilationNewDtoToCompilation(compilation);

        if (compilation.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(compilation.getEvents());
            comp.setEvents(events);
        }

        return compilationMapper.compilationToCompilationDto(compilationRepository.save(comp));
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, CompilationUpdateDto compilation) {
        Compilation compilationInRepo = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));

        if (compilation.getEvents() != null) {
            List<Event> events = eventRepository.findByIdIn(compilation.getEvents());

            compilationInRepo.setEvents(events);
        }
        if (compilation.getPinned() != null) {
            compilationInRepo.setPinned(compilation.getPinned());
        }
        if (compilation.getTitle() != null) {
            compilationInRepo.setTitle(compilation.getTitle());
        }
        return compilationMapper.compilationToCompilationDto(compilationRepository.save(compilationInRepo));
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        BooleanExpression resultExpression = Expressions.asBoolean(true).isTrue();
        if (pinned != null) {
            resultExpression = QCompilation.compilation.pinned.eq(pinned);
        }

        Page<Compilation> compilations = compilationRepository.findAll(resultExpression, PageUtils.convertToPageSettings(from, size));

        return compilations.stream().map(compilationMapper::compilationToCompilationDto).collect(Collectors.toList());
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " was not found"));
        return compilationMapper.compilationToCompilationDto(compilation);
    }
}
