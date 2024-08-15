package com.matchify.controller;

import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.request.GetMatchedEventsRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.GetMatchedEventsResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.model.Event;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.model.EventAttendees;
import com.matchify.model.User;
import com.matchify.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/event")
public class EventController {
  @Autowired
  private EventService eventService;

  // PostMapping for handling POST requests for creating events.
  @PostMapping("/create-event")
  public ResponseEntity<CreateEventResponse> createEvent(
          @Valid @RequestHeader("Authorization") String authorizationHeader,
          @ModelAttribute CreateEventRequest createEventRequest,
          @RequestParam(value = "EventImage", required = false) MultipartFile file) {
    return new ResponseEntity<CreateEventResponse>(
            eventService.createEvent(createEventRequest, file), HttpStatusCode.valueOf(200));
  }

  @PostMapping("/join-event")
  public ResponseEntity<String> joinEvent(@Valid @RequestBody EventAttendees eventAttendees) {
    return new ResponseEntity<String>(
            eventService.joinEvent(eventAttendees), HttpStatusCode.valueOf(200));
  }

  /**
   * Handles POST requests for fetching matched events for the given user ids.
   *
   * @param getMatchedEventsRequest the request containing the user ids for which the matched events
   *     are to be fetched.
   * @return the response containing the matched events.
   */
  @PostMapping("/matched-events")
  public ResponseEntity<List<GetMatchedEventsResponse>> getMatchedEvents(
      @Valid @RequestHeader("Authorization") String authorizationHeader,
      @Valid @RequestBody GetMatchedEventsRequest getMatchedEventsRequest) {
    List<Integer> matchedUserIds = getMatchedEventsRequest.getMatchedUserIds();
    List<GetMatchedEventsResponse> matchedEvents = eventService.getMatchedEvents(matchedUserIds);
    return ResponseEntity.ok().body(matchedEvents);
  }

  /**
   * Handles GET requests for fetching events for the logged-in user.
   *
   * @return the response containing the events for the logged-in user.
   */
  @GetMapping("/my-events")
  public ResponseEntity<List<GetMyEventsResponse>> getMyEvents(
      @Valid @RequestHeader("Authorization") String authorizationHeader) {
    return ResponseEntity.ok().body(eventService.getMyEvents());
  }

  @PostMapping("/leave-event")
  public ResponseEntity<String> leaveEvent(@Valid @RequestBody EventAttendees eventAttendees) {
    return new ResponseEntity<String>(
            eventService.leaveEvent(eventAttendees), HttpStatusCode.valueOf(200));
  }

  @PostMapping("/event-attendees-list")
  public ResponseEntity<List<EventAttendeesResponse>> attendeesList(@Valid @RequestBody EventAttendeesListRequest eventAttendeesListRequest) {
    return new ResponseEntity<List<EventAttendeesResponse>>(
            eventService.findAllEventAttendees(eventAttendeesListRequest), HttpStatusCode.valueOf(200));
  }
}
