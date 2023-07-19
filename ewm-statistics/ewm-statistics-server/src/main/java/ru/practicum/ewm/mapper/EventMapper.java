package ru.practicum.ewm.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.dto.RequestStatDto;
import ru.practicum.ewm.dto.ResponseStatDto;
import ru.practicum.ewm.model.EndpointHit;
import ru.practicum.ewm.model.ViewStats;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class EventMapper {
    public static EndpointHit RequestStatDtoToEvent(RequestStatDto statDto) {
        return EndpointHit.builder()
                .app(statDto.getApp())
                .uri(statDto.getUri())
                .ip(statDto.getIp())
                .timestamp(statDto.getTimestamp())
                .build();
    }

    public static ResponseStatDto hitEventToRequestStatDto(ViewStats event) {
        return ResponseStatDto.builder()
                .app(event.getApp())
                .hits(event.getHits())
                .uri(event.getUri())
                .build();
    }

    public static List<ResponseStatDto> HitEventListToRequestStatDtoList(List<ViewStats> events) {
        return events.stream().map(EventMapper::hitEventToRequestStatDto).collect(Collectors.toList());
    }


}
