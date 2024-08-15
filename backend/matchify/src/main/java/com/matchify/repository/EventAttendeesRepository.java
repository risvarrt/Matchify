package com.matchify.repository;

import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.model.Event;
import com.matchify.model.EventAttendees;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventAttendeesRepository extends JpaRepository<EventAttendees, Long> {

    @Transactional
    @Modifying
    @Query("delete from EventAttendees e where e.eventId=?1 and e.userId=?2")
    void deleteAttendees(Integer eventId, Integer userId);

    @Query("select new com.matchify.dto.response.EventAttendeesResponse(u.userId, u.firstName, u.lastName) from EventAttendees e left join User u on e.userId=u.userId where e.eventId=?1")
    List<EventAttendeesResponse> findEventAttendeesList(Integer eventId);

    /**
     * Fetch all events joined by a user
     *
     * @param userId the user id
     * @return the list of events
     */
    @Query("select e from Event e where e.eventId in (select ea.eventId from EventAttendees ea where ea.userId = ?1)")
    List<Event> fetchJoinedEvents(Integer userId);

    @Query("select i from EventAttendees i where i.eventId=?1 and i.userId=?2")
    EventAttendees findEventAttendees(Integer eventId, Integer userId);

}
