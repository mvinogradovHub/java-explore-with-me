package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.RequestStatDto;
import ru.practicum.ewm.dto.ResponseStatDto;
import ru.practicum.ewm.model.Event;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsService {
    Event addEvent(RequestStatDto dtoRequest);

    List<ResponseStatDto> getEvents(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
