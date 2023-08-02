package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.dto.CompilationNewDto;
import ru.practicum.ewm.model.Compilation;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto compilationToCompilationDto(Compilation compilation);

    @Mapping(ignore = true, target = "events")
    Compilation compilationNewDtoToCompilation(CompilationNewDto compilationDto);

}
