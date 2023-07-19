package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.RequestStatDto;
import ru.practicum.ewm.dto.ResponseStatDto;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.repository.EventRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {
    private final EventRepository eventRepository;

    @Override
    public EndpointHit addEvent(RequestStatDto dtoRequest) {
        return eventRepository.save(EventMapper.RequestStatDtoToEvent(dtoRequest));
    }

    @Override
    public List<ResponseStatDto> getEvents(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return EventMapper.HitEventListToRequestStatDtoList(eventRepository.getUniqueEvents(start, end, uris));
        } else {
            return EventMapper.HitEventListToRequestStatDtoList(eventRepository.getEvents(start, end, uris));
        }
    }
}
