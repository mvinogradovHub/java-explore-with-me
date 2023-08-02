package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventNewDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.EventUpdateDto;
import ru.practicum.ewm.model.EventSort;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.EventStateAdminAction;
import ru.practicum.ewm.model.EventStateUserAction;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto addUserEvent(Long userId, EventNewDto event);

    EventFullDto getUserEvent(Long userId, Long eventId);

    List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size);

    EventFullDto updateUserEvent(Long userId, Long eventId, EventUpdateDto<EventStateUserAction> updateDto);

    List<EventFullDto> getAdminEvents(List<Long> users,
                                      List<EventState> states,
                                      List<Long> categories,
                                      LocalDateTime rangeStart,
                                      LocalDateTime rangeEnd,
                                      Integer from,
                                      Integer size);

    EventFullDto updateAdminEvent(Long eventId, EventUpdateDto<EventStateAdminAction> updateDto);

    List<EventShortDto> getEvents(String text,
                                  List<Long> categories,
                                  Boolean paid,
                                  LocalDateTime rangeStart,
                                  LocalDateTime rangeEnd,
                                  Boolean onlyAvailable,
                                  EventSort sort,
                                  Integer from,
                                  Integer size,
                                  HttpServletRequest request
    );

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

}
