package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.dto.CompilationUpdateDto;
import ru.practicum.ewm.service.CompilationService;

import javax.validation.Valid;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody CompilationNewDto compilation) {
        log.info("Received request to POST /admin/compilations with body: {}", compilation);
        return compilationService.addCompilation(compilation);
    }

    @PatchMapping("/{compId}")
    public CompilationDto updateCompilation(@Valid @RequestBody CompilationUpdateDto compilationDto, @PathVariable Long compId) {
        log.info("Received request to PATCH /admin/compilations/{} with body: {}", compId, compilationDto);
        return compilationService.updateCompilation(compId, compilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info("Received request to DELETE /admin/compilations/{}", compId);
        compilationService.deleteCompilation(compId);
    }

}
