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
    private final CommentRepository commentRepository;

    @Override
    public EventFullDto addEventByUser(Long userId, EventNewDto eventNewDto) {
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
    public EventFullPrivateDto getUserHisEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        addViews(List.of(event));

        return eventMapper.eventToEventFullPrivateDto(event);
    }

    @Override
    public List<EventShortDto> getUserHisEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        List<Event> events = eventRepository.findByInitiatorId(userId, PageUtils.convertToPageSettings(from, size));
        addViews(events);

        return events.stream().map(eventMapper::eventToEventShotDto).collect(Collectors.toList());

    }

    @Override
    public EventFullPrivateDto updateUserHisEvent(Long userId, Long eventId, EventUpdateByUserDto updateDto) {
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User with id=" + userId + " was not found"));
        checkingAndSetEventStatusForUser(event, updateDto);
        updateEventForUser(event, updateDto);

        return eventMapper.eventToEventFullPrivateDto(eventRepository.save(event));

    }


    @Override
    public List<EventFullPrivateDto> getEventsByAdmin(EventSearchAdminRequestDto param) {
        Page<Event> eventsPage = eventRepository.findAll(addSearchAdminConditions(param), PageUtils.convertToPageSettings(param.getFrom(), param.getSize()));
        List<Event> events = eventsPage.stream().collect(Collectors.toList());
        addViews(events);

        return events.stream().map(eventMapper::eventToEventFullPrivateDto).collect(Collectors.toList());
    }


    @Override
    public EventFullPrivateDto updateEventByAdmin(Long eventId, EventUpdateByAdminDto updateDto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        validateEventDateForAdmin(event);
        checkingAndSetEventStatusForAdmin(event, updateDto.getStateAction(), false);
        updateEventForAdmin(event, updateDto);

        return eventMapper.eventToEventFullPrivateDto(eventRepository.save(event));
    }


    @Override
    public List<EventShortDto> getEvents(EventSearchPublicRequestDto param) {

        if (param.getRangeEnd().isBefore(param.getRangeStart())) {
            throw new ParameterRequestException("Bad rangeEnd");
        }

        statsClient.hit(RequestStatDto.builder()
                .timestamp(LocalDateTime.now())
                .app("ewm-service")
                .uri(param.getRequest().getRequestURI())
                .ip(param.getRequest().getRemoteAddr())
                .build());

        addSearchPublicConditions(param);

        Page<Event> eventsSort = eventRepository.findAll(addSearchPublicConditions(param), getPublicSorted(param));
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

    public EventFullPrivateDto changeModerationStatusByAdmin(Long eventId, EventModerationByAdminDto moderation) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
        EventStateAdminAction stateAdminAction = moderation.getStateAction();
        checkingAndSetEventStatusForAdmin(event, stateAdminAction, true);

        if (moderation.getCommentOnRejection() != null) {
            Comment comment = Comment.builder()
                    .text(moderation.getCommentOnRejection())
                    .event(event)
                    .created(LocalDateTime.now())
                    .updated(LocalDateTime.now())
                    .build();
            commentRepository.save(comment);
        }

        return eventMapper.eventToEventFullPrivateDto(eventRepository.save(event));
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

    private void updateEventForUser(Event event, EventUpdateByUserDto updateDto) {
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

        if (updateDto.getEventDate() != null) {
            validateEventDateForUser(updateDto);
            event.setEventDate(updateDto.getEventDate());
        }
    }

    private void validateEventDateForUser(EventUpdateByUserDto updateDto) {
        if (updateDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new ValidationException("the date and time for which the event is scheduled cannot be earlier than two hours from the current moment");
        }

    }


    private void checkingAndSetEventStatusForUser(Event event, EventUpdateByUserDto updateDto) {
        if (updateDto.getStateAction() != null) {
            switch (updateDto.getStateAction()) {
                case SEND_TO_REVIEW:
                    if (event.getState().equals(EventState.CANCELED)) {
                        event.setState(EventState.PENDING);
                    } else {
                        throw new ValidationException("you can only resend an event with the CANCELED status to the review");
                    }
                    break;
                case CANCEL_REVIEW:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new ValidationException("Invalid state action");
            }
        } else {
            switch (event.getState()) {
                case PUBLISHED:
                    throw new ValidationException("you can only change canceled events or events in the state of waiting for moderation");
                case MODERATION:
                    throw new ValidationException("You cannot edit events during moderation");
            }
        }
    }


    private BooleanExpression addSearchAdminConditions(EventSearchAdminRequestDto param) {
        BooleanExpression resultExpression = Expressions.asBoolean(true).isTrue();

        if (param.getUsers() != null) {
            resultExpression = QEvent.event.initiator.id.in(param.getUsers());
        }

        if (param.getStates() != null) {
            resultExpression = resultExpression.and(QEvent.event.state.in(param.getStates()));
        }

        if (param.getCategories() != null) {
            resultExpression = resultExpression.and(QEvent.event.category.id.in(param.getCategories()));
        }

        if (param.getRangeStart() != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.after(param.getRangeStart()));
        }

        if (param.getRangeEnd() != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        }

        return resultExpression;
    }

    private void validateEventDateForAdmin(Event event) {
        if (event.getPublishedOn() != null && event.getEventDate().isBefore(event.getPublishedOn().plusHours(1))) {
            throw new ValidationException("the start date of the event to be modified must be no earlier than an hour from the date of publication.");
        }

    }

    private void checkingAndSetEventStatusForAdmin(Event event, EventStateAdminAction stateAction, Boolean newModerationMethod) {
        if (stateAction != null) {
            switch (stateAction) {
                case IN_WORK_EVENT:
                    if (event.getState().equals(EventState.PENDING)) {
                        event.setState(EventState.MODERATION);
                    } else {
                        throw new ValidationException("only events that are in the PENDING status can be moderated");
                    }
                    break;
                case PUBLISH_EVENT:
                    if (event.getState().equals(EventState.PENDING) && !newModerationMethod) {
                        event.setState(EventState.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else if (event.getState().equals(EventState.MODERATION) && newModerationMethod) {
                        event.setState(EventState.PUBLISHED);
                        event.setPublishedOn(LocalDateTime.now());
                    } else {
                        throw new ValidationException("only events that have passed PENDING or MODERATION for new moderation method can be published");
                    }
                    break;
                case REJECT_EVENT:
                    if (!event.getState().equals(EventState.PUBLISHED) && !newModerationMethod) {
                        event.setState(EventState.CANCELED);
                    } else if (event.getState().equals(EventState.MODERATION)) {
                        event.setState(EventState.CANCELED);
                    } else {
                        throw new ValidationException("only events that have passed not PUBLISHED or MODERATION for new moderation method can be canceled");
                    }
                    break;
                default:
                    throw new ValidationException("Invalid state action");
            }
        } else {
            if (event.getState().equals(EventState.PUBLISHED)) {
                throw new ValidationException("you can not change PUBLISHED events");
            }
        }

    }

    private void updateEventForAdmin(Event event, EventUpdateByAdminDto updateDto) {
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

    private Pageable getPublicSorted(EventSearchPublicRequestDto param) {
        switch (param.getSort()) {
            case EVENT_DATE:
                return PageUtils.convertToPageSettings(param.getFrom(), param.getSize(), "eventDate");
            case VIEWS:
                return PageUtils.convertToPageSettings(param.getFrom(), param.getSize(), "views");
            default:
                throw new ValidationException("Invalid state action");
        }
    }

    private BooleanExpression addSearchPublicConditions(EventSearchPublicRequestDto param) {
        BooleanExpression resultExpression = QEvent.event.state.eq(EventState.PUBLISHED);

        if (param.getText() != null) {
            resultExpression = resultExpression.and(QEvent.event.annotation.likeIgnoreCase(param.getText()).or(QEvent.event.description.likeIgnoreCase(param.getText())));

        }

        if (param.getCategories() != null) {
            resultExpression = resultExpression.and(QEvent.event.category.id.in(param.getCategories()));
        }

        if (param.getPaid() != null && param.getPaid()) {
            resultExpression = resultExpression.and(QEvent.event.paid.isTrue());
        } else if (param.getPaid() != null) {
            resultExpression = resultExpression.and(QEvent.event.paid.isFalse());
        }

        if (param.getRangeStart() != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.after(param.getRangeStart()));
        }

        if (param.getRangeEnd() != null) {
            resultExpression = resultExpression.and(QEvent.event.eventDate.before(param.getRangeEnd()));
        }
        if (param.getOnlyAvailable()) {
            resultExpression = resultExpression.and(QEvent.event.participantLimit.ne(QEvent.event.confirmedRequests));
        }
        return resultExpression;
    }


}
