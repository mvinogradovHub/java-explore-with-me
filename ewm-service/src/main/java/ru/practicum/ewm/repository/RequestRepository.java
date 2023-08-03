package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Request;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;
import java.util.Optional;


@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByEventIdAndStatusIn(Long eventId, List<RequestStatus> requestStatuses);

    List<Request> findByRequesterId(Long userId);

    Optional<Request> findByRequesterIdAndId(Long userId, Long requestId);

    List<Request> findByEventInitiatorIdAndEventId(Long userId, Long requestId);

    List<Request> findByEventInitiatorIdAndEventIdAndStatusIn(Long userId, Long requestId, List<RequestStatus> requestStatuses);
}
