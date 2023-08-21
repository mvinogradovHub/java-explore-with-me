package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    EventFullDto addEventByUser(Long userId, EventNewDto event);

    EventFullPrivateDto getUserHisEvent(Long userId, Long eventId);

    List<EventShortDto> getUserHisEvents(Long userId, Integer from, Integer size);

    EventFullPrivateDto updateUserHisEvent(Long userId, Long eventId, EventUpdateByUserDto updateDto);

    List<EventFullPrivateDto> getEventsByAdmin(EventSearchAdminRequestDto param);

    EventFullPrivateDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto updateDto);

    List<EventShortDto> getEvents(EventSearchPublicRequestDto dto);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

    EventFullPrivateDto changeModerationStatusByAdmin(Long eventId, EventModerationByAdminDto statusByAdminDto);


}
