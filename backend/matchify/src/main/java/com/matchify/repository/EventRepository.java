package com.matchify.repository;

import com.matchify.model.Event;
import com.matchify.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

  /**
   * Fetches the events for the given user ids and the date.
   *
   * @param users the user ids for which the events are to be fetched.
   * @return the events.
   */
  @Query(
      "SELECT e FROM Event e "
          + "LEFT JOIN EventAttendees ea ON e.eventId = ea.eventId "
          + "WHERE e.user IN (:users) "
          + "AND (ea.userId IS NULL OR ea.userId != :loggedInUserId) "
          + "AND e.city = :city "
          + "AND e.eventDate > CURRENT_DATE "
          + "ORDER BY e.eventDate ASC")
  List<Event> findEventsForUsersByCitySortedByDate(
      @Param("users") List<User> users,
      @Param("city") String city,
      @Param("loggedInUserId") Integer loggedInUserId,
      Pageable pageable);

  /**
   * Fetches the events for the given city.
   *
   * @param city the city for which the events are to be fetched.
   * @return the events.
   */
  @Query(
      "SELECT e FROM Event e "
          + "LEFT JOIN EventAttendees ea ON e.eventId = ea.eventId "
          + "WHERE e.city = :city "
          + "AND (ea.userId IS NULL OR ea.userId != :loggedInUserId) "
          + "AND e.eventDate > CURRENT_DATE "
          + "ORDER BY e.eventDate ASC")
  List<Event> findEventsForCity(
      @Param("city") String city,
      @Param("loggedInUserId") Integer loggedInUserId,
      Pageable pageable);

  /**
   * Fetches the events for the given user.
   *
   * @param user the user for which the events are to be fetched.
   * @return the events.
   */
  List<Event> findAllByUser(User user);
}
