package com.matchify.controller;

import com.matchify.dto.UserWithoutPassword;
import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.request.GetMatchedEventsRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.dto.response.GetMatchedEventsResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.model.EventAttendees;
import com.matchify.service.EventService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.ArgumentMatchers.isA;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @Mock
    EventService eventService;

    @InjectMocks
    EventController eventController;

    @Test
    void testCreateEvent() {
        // Arrange
        CreateEventRequest request = new CreateEventRequest("eventName", "description",
                LocalDate.of(2024, Month.MARCH, 25), LocalTime.of(15, 40, 7), LocalTime.of(15, 40, 7),
                "additionalInstructions", "address", "pincode", "city");
        CreateEventResponse expectedResponse = new CreateEventResponse(1, "message");

        // Act
        when(eventService.createEvent(any(CreateEventRequest.class), any())).thenReturn(expectedResponse);
        ResponseEntity<CreateEventResponse> responseEntity = eventController.createEvent("authorizationHeader", request,
                null);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testJoinEvent() {
        // Arrange
        EventAttendees eventAttendees = new EventAttendees(1, 1, 1); // Sample eventAttendees object
        String expectedResponse = "Successfully joined the event";

        // Act
        when(eventService.joinEvent(any(EventAttendees.class))).thenReturn(expectedResponse);
        ResponseEntity<String> responseEntity = eventController.joinEvent(eventAttendees);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testMatchedEvents() {
        // Arrange
        GetMatchedEventsRequest request = new GetMatchedEventsRequest(List.of(1)); // Example request
        List<GetMatchedEventsResponse> expectedResponse = List.of(
                new GetMatchedEventsResponse(
                        1f, new UserWithoutPassword(3, "Bob", "builder", "b.bd@example.com", "Halifax", "25-30"),
                        "Music Festival", "Description", "2024-03-25", "15:40:00", "18:00:00", "Bring friends",
                        "122 music St.", "12345", "Halifax", "url_to_image", "40.7128", "-74.0060"));

        // Act
        when(eventService.getMatchedEvents(any())).thenReturn(expectedResponse);
        ResponseEntity<List<GetMatchedEventsResponse>> responseEntity = eventController
                .getMatchedEvents("authorizationHeader", request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertFalse(responseEntity.getBody().isEmpty());
        Assertions.assertEquals("Music Festival", responseEntity.getBody().get(0).getEventName());
    }

    @Test
    void testMyEvents() {
        // Arrange
        List<GetMyEventsResponse> expectedResponse = List.of(new GetMyEventsResponse(1f,
                new UserWithoutPassword(3, "Oswald", "Big", "Os.b@example.com", "Los Angeles", "18-24"), "Art Exhibit",
                "Artistic gathering", "2024-04-15", "12:00:00", "15:00:00", "Free entry", "456 Art Way", "67890",
                "Los Angeles", "url_to_image2", "34.0522", "-118.2437"));

        // Act
        when(eventService.getMyEvents()).thenReturn(expectedResponse);
        ResponseEntity<List<GetMyEventsResponse>> responseEntity = eventController.getMyEvents("authorizationHeader");

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertFalse(responseEntity.getBody().isEmpty());
        Assertions.assertEquals("Art Exhibit", responseEntity.getBody().get(0).getEventName());
    }

    @Test
    void testLeaveEvent() {
        // Arrange
        EventAttendees eventAttendees = new EventAttendees(1, 1, 3); // Sample eventAttendees object for leave event
        String expectedResponse = "Successfully left the event";

        // Act
        when(eventService.leaveEvent(any(EventAttendees.class))).thenReturn(expectedResponse);
        ResponseEntity<String> responseEntity = eventController.leaveEvent(eventAttendees);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertEquals(expectedResponse, responseEntity.getBody());
    }

    @Test
    void testAttendeesList() {
        // Arrange
        EventAttendeesListRequest request = new EventAttendeesListRequest(1); // Request for attendees list of an event
        List<EventAttendeesResponse> expectedResponse = List.of(
                new EventAttendeesResponse(1, "Bob", "Builder"),
                new EventAttendeesResponse(1, "Oswald", "Big"));

        // Act
        when(eventService.findAllEventAttendees(any(EventAttendeesListRequest.class))).thenReturn(expectedResponse);
        ResponseEntity<List<EventAttendeesResponse>> responseEntity = eventController.attendeesList(request);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        Assertions.assertFalse(responseEntity.getBody().isEmpty());
        Assertions.assertEquals(2, responseEntity.getBody().size());
        Assertions.assertEquals("Bob", responseEntity.getBody().get(0).getFirstName());
        Assertions.assertEquals("Oswald", responseEntity.getBody().get(1).getFirstName());
    }
}
