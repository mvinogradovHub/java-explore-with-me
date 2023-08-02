package ru.practicum.ewm.service.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.*;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.ParameterRequestException;
import ru.practicum.ewm.exception.ValidationException;
import ru.practicum.ewm.mapper.EventMapper;
import ru.practicum.ewm.model.*;
import ru.practicum.ewm.repository.*;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.utils.PageUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.ewm.utils.DateFormat.formatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final CategoryRepository categoryRepository;
    private final LocationRepository locationRepository;
    private final StatsClient statsClient;

    @Override
    public EventFullDto addUserEvent(Long userId, EventNewDto eventNewDto) {
        Event event = eventMapper.eventNewDtoToEvent(eventNewDto);

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new ValidationException("the date and time for which the event is scheduled cannot be earlier than two hours from the current moment");
        }

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        Category category = categoryRepository.findById(eventNewDto.getCategory()).orElseThrow(() -> new NotFoundException("Category with id=" + eventNewDto.getCategory() + " was not found"));

        Optional<Location> location = locationRepository.findByLatAndLon(eventNewDto.getLocation().getLat(), eventNewDto.getLocation().getLon());
        if (location.isEmpty()) {
            location = Optional.of(locationRepository.save(eventNewDto.getLocation()));
        }

        event.setCategory(category);
        event.setInitiator(user);
        event.setLocation(location.get());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(EventState.PENDING);
        event.setViews(0L);
        event.setConfirmedRequests(0);

        if (event.getPaid() == null) {
            event.setPaid(false);
        }
        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }
        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        return eventMapper.eventToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getUserEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        addViews(List.of(event));
        return eventMapper.eventToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getUserEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Event> events = eventRepository.findByInitiatorId(userId, PageUtils.convertToPageSettings(from, size));
        addViews(events);
        return events.stream().map(eventMapper::eventToEventShotDto).collect(Collectors.toList());

    }

    @Override
    public EventFullDto updateUserEvent(Long userId, Long eventId, EventUpdateDto<EventStateUserAction> updateDto) {

        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));

        if (event.getState().equals(EventState.PUBLISHED)) {
            throw new ValidationException("you can only change canceled events or events in the state of waiting for moderation");
        }

        if (updateDto.getEventDate() != null) {
            if (updateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
                throw new ValidationException("the date and time for which the event is scheduled cannot be earlier than two hours from the current moment");
            }
            event.setEventDate(updateDto.getEventDate());
        }

        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ValidationException("Invalid state action");
            }
        }
        updateEvent(event, updateDto);

        return eventMapper.eventToEventFullDto(eventRepository.save(event));

    }

    @Override
    public List<EventFullDto> getAdminEvents(List<Long> users,
                                             List<EventState> states,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd,
                                             Integer from, Integer size) {
        BooleanExpression resultExpression = Expressions.asBoolean(true).isTrue();

        if (users != null) {
            resultExpression = QEvent.event.initiator.id.in(users);
        }

        if (states != null) {
            resultExpression = resultExpression.and(QEvent.event.state.in(states));
        }

        if (categories != null) {
            resultExpression = resultExpression.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.before(rangeEnd));
        }

        Page<Event> eventsPage = eventRepository.findAll(resultExpression, PageUtils.convertToPageSettings(from, size));
        List<Event> events = eventsPage.stream().collect(Collectors.toList());
        addViews(events);
        return events.stream().map(eventMapper::eventToEventFullDto).collect(Collectors.toList());
    }

    @Override
    public EventFullDto updateAdminEvent(Long eventId, EventUpdateDto<EventStateAdminAction> updateDto) {

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));


        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case PUBLISH_EVENT:
                    if (event.getState().equals(EventState.PENDING)) {
                        event.setState(EventState.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new ValidationException("Invalid state action");
                    }
                    break;
                case REJECT_EVENT:
                    if (!event.getState().equals(EventState.PUBLISHED)) {
                        event.setState(EventState.CANCELED);
                    } else {
                        throw new ValidationException("Invalid state action");
                    }
                    break;
                default:
                    throw new ValidationException("Invalid state action");
            }
        }

        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new ValidationException("the start date of the event to be modified must be no earlier than an hour from the date of publication.");
        }

        updateEvent(event, updateDto);

        return eventMapper.eventToEventFullDto(eventRepository.save(event));

    }

    private <T> void updateEvent(Event event, EventUpdateDto<T> updateDto) {

        if (updateDto.getAnnotation() != null) {
            event.setAnnotation(updateDto.getAnnotation());
        }

        if (updateDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateDto.getCategory()).orElseThrow(() -> new NotFoundException("Category with id=" + updateDto.getCategory() + " was not found"));
            event.setCategory(category);
        }

        if (updateDto.getDescription() != null) {
            event.setDescription(updateDto.getDescription());
        }

        if (updateDto.getLocation() != null) {
            Optional<Location> location = locationRepository.findByLatAndLon(updateDto.getLocation().getLat(), updateDto.getLocation().getLon());
            if (location.isEmpty()) {
                location = Optional.of(locationRepository.save(updateDto.getLocation()));
            }
            event.setLocation(location.get());
        }

        if (updateDto.getPaid() != null) {
            event.setPaid(updateDto.getPaid());
        }

        if (updateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateDto.getParticipantLimit());
        }

        if (updateDto.getRequestModeration() != null) {
            event.setRequestModeration(updateDto.getRequestModeration());
        }


        if (updateDto.getTitle() != null) {
            event.setTitle(updateDto.getTitle());
        }

    }

    @Override
    public List<EventShortDto> getEvents(String text,
                                         List<Long> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         EventSort sort,
                                         Integer from,
                                         Integer size,
                                         HttpServletRequest request) {

        if (rangeEnd.isBefore(rangeStart)) {
            throw new ParameterRequestException("Bad rangeEnd");
        }

        statsClient.hit(RequestStatDto.builder()
                .timestamp(LocalDateTime.now())
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());

        BooleanExpression resultExpression = Expressions.asBoolean(true).isTrue();

        if (text != null) {
            resultExpression = QEvent.event.annotation.likeIgnoreCase(text).or(QEvent.event.description.likeIgnoreCase(text));

        }
        if (paid != null && paid) {
            resultExpression = resultExpression.and(QEvent.event.paid.isTrue());
        } else if (paid != null) {
            resultExpression = resultExpression.and(QEvent.event.paid.isFalse());
        }

        if (rangeStart != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.after(rangeStart));
        }

        if (rangeEnd != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.before(rangeEnd));
        }
        if (onlyAvailable) {
            resultExpression = resultExpression.and(QEvent.event.participantLimit.ne(QEvent.event.confirmedRequests));
        }

        Pageable pageable;

        switch (sort) {
            case EVENT_DATE:
                pageable = PageUtils.convertToPageSettings(from, size, "eventDate");
                break;
            case VIEWS:
                pageable = PageUtils.convertToPageSettings(from, size, "views");
                break;
            default:
                throw new ValidationException("Invalid state action");
        }
        Page<Event> eventsSort = eventRepository.findAll(resultExpression, pageable);
        List<Event> events = eventsSort.stream().collect(Collectors.toList());
        addViews(events);
        return events.stream().map(eventMapper::eventToEventShotDto).collect(Collectors.toList());

    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(eventId, EventState.PUBLISHED).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));

        statsClient.hit(RequestStatDto.builder()
                .timestamp(LocalDateTime.now())
                .app("ewm-service")
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .build());


        addViews(List.of(event));

        return eventMapper.eventToEventFullDto(event);
    }

    private void addViews(List<Event> events) {
        List<String> uris = events.stream().map(x -> "/events/" + x.getId()).collect(Collectors.toList());
        List<ResponseStatDto> statDto = statsClient.stats(LocalDateTime.now().minusYears(10).format(formatter), LocalDateTime.now().format(formatter), uris, true);

        Map<Long, Long> urisId = statDto.stream().collect(Collectors.toMap(x -> uriToId(x.getUri()), ResponseStatDto::getHits));

        for (Event event : events) {
            if (urisId.isEmpty()) {
                event.setViews(0L);
            } else {
                event.setViews(urisId.get(event.getId()));
            }

        }
    }

    private Long uriToId(String uri) {
        String[] parts = uri.split("/");
        String lastOne = parts[parts.length - 1];
        if (StringUtils.isNumeric(lastOne)) {
            return Long.parseLong(lastOne);
        } else {
            return -1L;
        }
    }


}
