package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addEventByUser(Long userId, EventNewDto event);

    EventFullPrivateDto getEventByUser(Long userId, Long eventId);

    List<EventShortDto> getEventsByUser(Long userId, Integer from, Integer size);

    EventFullPrivateDto updateEventByUser(Long userId, Long eventId, EventUpdateByUserDto updateDto);

    List<EventFullPrivateDto> getEventsByAdmin(List<Long> users,
                                               List<EventState> states,
                                               List<Long> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size);

    EventFullPrivateDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto updateDto);

    List<EventShortDto> getEvents(EventSearchRequestDto dto);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

}
