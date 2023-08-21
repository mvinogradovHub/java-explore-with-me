package ru.practicum.ewm.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventFullPrivateDto;
import ru.practicum.ewm.dto.EventNewDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.model.Event;


@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category.id", target = "category")
    EventNewDto eventToEventNewDto(Event event);

    EventFullDto eventToEventFullDto(Event event);

    EventFullPrivateDto eventToEventFullPrivateDto(Event event);

    EventShortDto eventToEventShotDto(Event event);


    @Mapping(source = "category", target = "category.id")
    Event eventNewDtoToEvent(EventNewDto event);


}
