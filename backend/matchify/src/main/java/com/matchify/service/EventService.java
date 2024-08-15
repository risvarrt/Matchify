package com.matchify.service;

import com.matchify.dto.AddressCoordinates;
import com.matchify.dto.EventAddress;
import com.matchify.dto.EventImageName;
import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.GetMatchedEventsResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.model.Event;
import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.model.EventAttendees;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface EventService {
    Event storeEvent(CreateEventRequest createEventRequest , String imageURL, AddressCoordinates coordinates);

    CreateEventResponse createEvent(CreateEventRequest createEventRequest, MultipartFile file);

    String uploadFile(MultipartFile file , EventImageName eventImageName);

    AddressCoordinates getCoordinates(EventAddress eventAddress);

    /**
     * Fetches the matched events for the given user ids.
     *
     * @param userIds the user ids for which the matched events are to be fetched.
     * @return the matched events.
     */
    List<GetMatchedEventsResponse> getMatchedEvents(List<Integer> userIds);

    /**
     * Fetches the events for the logged-in user.
     *
     * @return the events.
     */
    List<GetMyEventsResponse> getMyEvents();
    String joinEvent(EventAttendees eventAttendees);

    String leaveEvent(EventAttendees eventAttendees);

    List<EventAttendeesResponse> findAllEventAttendees(EventAttendeesListRequest eventAttendeesListRequest);

}
