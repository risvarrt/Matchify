package com.matchify.service.implementation;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.matchify.config.GeocodingConfig;
import com.matchify.dto.AddressCoordinates;
import com.matchify.dto.EventAddress;
import com.matchify.dto.EventImageName;
import com.matchify.dto.UserWithoutPassword;
import com.matchify.dto.request.CreateEventRequest;
import com.matchify.dto.request.EventAttendeesListRequest;
import com.matchify.dto.response.CreateEventResponse;
import com.matchify.dto.response.GetMatchedEventsResponse;
import com.matchify.dto.response.GetMyEventsResponse;
import com.matchify.dto.response.EventAttendeesResponse;
import com.matchify.exception.APINotAvailableException;
import com.matchify.exception.ImageUploadException;
import com.matchify.exception.UserNotFoundException;
import com.matchify.model.Event;
import com.matchify.model.User;
import com.matchify.model.EventAttendees;
import com.matchify.repository.EventAttendeesRepository;
import com.matchify.repository.EventRepository;
import com.matchify.repository.UserRepository;
import com.matchify.service.AuthService;
import com.matchify.service.EventService;
import com.matchify.utils.FileUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventServiceImpl implements EventService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Value("${aws.bucketName}")
    private String bucketName;

    @Autowired
    private GeocodingConfig geocodingConfig;

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private AuthService authService;

    @Autowired
    private EventAttendeesRepository eventAttendeesRepository;

    private static final int MAX_EVENTS_TO_FETCH = 10;

    /* Creates an event by handling the image upload, fetching the address coordinates,
      and storing the event details in the database.  */
    @Override
    public CreateEventResponse createEvent(CreateEventRequest createEventRequest, MultipartFile file) {
        EventImageName eventImageName = modelMapper.map(createEventRequest, EventImageName.class);
        EventAddress eventAddress = modelMapper.map(createEventRequest, EventAddress.class);
        String imgURL= file == null ? null :uploadFile(file, eventImageName);
        Event storedEvent = storeEvent(createEventRequest,imgURL, getCoordinates(eventAddress));
        return new CreateEventResponse(storedEvent.getEventId(), "Event Created Successfully");
    }

    //Stores the event details in the database, including the user, image URL, and address coordinates.
    @Override
    public Event storeEvent(CreateEventRequest createEventRequest, String imageURL, AddressCoordinates coordinates) {
        Event event = modelMapper.map(createEventRequest, Event.class);
        event.setEventId(null);
        event.setUser(userRepository.findByUserId(authService.getUserIdFromToken()));
        event.setImageURL(imageURL);
        event.setLatitude(coordinates.getLatitude());
        event.setLongitude(coordinates.getLongitude());
        return eventRepository.save(event);
    }

    //Uploads the event image file to AWS S3 and generates a URL for the uploaded image.
    @Override
    public String uploadFile(MultipartFile file, EventImageName eventImageName) {
        if (FileUtils.isImageFile(file)) {
            String fileName =
                    FileUtils.generateUniqueFileNameForImage(
                            String.valueOf(eventImageName.getUserid()),
                            eventImageName.getEventname(),
                            FileUtils.getFileExtention(file));
            s3Client.putObject(new PutObjectRequest(bucketName, fileName, FileUtils.generateTempFilePath(file).toFile()));
            return FileUtils.generateImageURL(bucketName, s3Client.getRegionName(), fileName);
        } else {
            throw new ImageUploadException("Error uploading image to S3");
        }
    }

    /* Fetches the geographical coordinates (latitude and longitude) for a given address.
     Uses the Google Geocoding API to convert an address to coordinates. */
    @Override
    public AddressCoordinates getCoordinates(EventAddress eventAddress) {
        String fullAddress = String.format("%s, %s, %s", eventAddress.getAddress(), eventAddress.getPincode(), eventAddress.getCity());
        AddressCoordinates coordinates = new AddressCoordinates();
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geocodingConfig.geoApiContext(), fullAddress).await();
            if (results.length > 0) {
                String lat = String.valueOf(results[0].geometry.location.lat);
                String lng = String.valueOf(results[0].geometry.location.lng);
                coordinates.setLatitude(lat);
                coordinates.setLongitude(lng);
                return coordinates;
            } else {
                throw new APINotAvailableException("Error getting the coordinates");
            }
        } catch (RuntimeException | ApiException | IOException e) {
            throw new APINotAvailableException(e.getMessage());
        } catch (InterruptedException e) {
            throw new APINotAvailableException(e.getMessage());
        }
    }

  /**
   * Retrieves events matching the preferences of given user IDs.
   *
   * @param userIds List of user IDs to match the events.
   * @return List of events that match the user's preferences.
   * @throws UserNotFoundException If no users are found for the given user IDs.
   */
  public List<GetMatchedEventsResponse> getMatchedEvents(List<Integer> userIds)
      throws UserNotFoundException {
    User loggedInUser = getLoggedInUser();
    String loggedInUserCity = loggedInUser.getLocation();
    List<User> users = getUsers(userIds);
    List<Event> combinedEvents = getCombinedEvents(loggedInUser, loggedInUserCity, users);
    List<Event> eventsToReturn = getEventsToReturn(combinedEvents);
    return mapEventsToGetMatchedEventsResponse(eventsToReturn);
  }

    /**
     * Retrieves the logged-in user.
     *
     * @return The logged-in user.
     */
    private User getLoggedInUser() {
        Integer loggedInUserId = authService.getUserIdFromToken();
        return userRepository.findByUserId(loggedInUserId);
    }

    /**
     * Retrieves the users for the given user IDs.
     *
     * @param userIds List of user IDs to fetch users for.
     * @return List of users.
     * @throws UserNotFoundException If no users are found for the given user IDs.
     */
    private List<User> getUsers(List<Integer> userIds) {
        List<User> users = userRepository.findAllByUserIdIn(userIds);
        if (users.isEmpty()) {
            throw new UserNotFoundException("No users found for the given user IDs");
        }
        return users;
    }

    /**
     * Retrieves the combined events for the given users and city.
     *
     * @param loggedInUser The logged-in user.
     * @param loggedInUserCity The city of the logged-in user.
     * @param users List of users to fetch events for.
     * @return List of combined events.
     */
    private List<Event> getCombinedEvents(User loggedInUser, String loggedInUserCity, List<User> users) {
        List<Event> events = eventRepository.findEventsForUsersByCitySortedByDate(users, loggedInUserCity, loggedInUser.getUserId(), PageRequest.of(0, MAX_EVENTS_TO_FETCH));
        boolean shouldFetchEventsForCity = events.isEmpty() || events.size() < MAX_EVENTS_TO_FETCH;
        if (shouldFetchEventsForCity) {
            events.addAll(eventRepository.findEventsForCity(loggedInUserCity, loggedInUser.getUserId(), PageRequest.of(0, MAX_EVENTS_TO_FETCH)));
            events = removeDuplicates(events);
        }
        return events;
    }

    /**
     * Removes duplicate events from the list of events.
     *
     * @param events List of events to remove duplicates from.
     * @return List of events with duplicates removed.
     */
    private List<Event> removeDuplicates(List<Event> events) {
        Set<Event> eventSet = new HashSet<>(events);
        return new ArrayList<>(eventSet);
    }

    /**
     * Returns the list of events to return based on the maximum number of events to fetch.
     *
     * @param combinedEvents List of combined events.
     * @return List of events to return.
     */
    private List<Event> getEventsToReturn(List<Event> combinedEvents) {
        int numberOfEventsToReturn = Math.min(combinedEvents.size(), MAX_EVENTS_TO_FETCH);
        return combinedEvents.subList(0, numberOfEventsToReturn);
    }

    /**
     * Maps the events to the response object.
     *
     * @param events List of events to map.
     * @return List of GetMatchedEventsResponse objects.
     */
    private List<GetMatchedEventsResponse> mapEventsToGetMatchedEventsResponse(List<Event> events) {
        return events.stream()
                .map(event -> {
                    GetMatchedEventsResponse eventResponse = modelMapper.map(event, GetMatchedEventsResponse.class);
                    eventResponse.setCreatedBy(modelMapper.map(event.getUser(), UserWithoutPassword.class));
                    return eventResponse;
                })
                .collect(Collectors.toList());
    }

    /**
     * Retrieves events associated with the currently authenticated user.
     *
     * @return List of events associated with the user.
     * @throws UserNotFoundException If the user is not found.
     */
    public List<GetMyEventsResponse> getMyEvents() {
        User user = getUserFromToken();
        List<Event> events = eventRepository.findAllByUser(user);
        return mapEventsToMyEventsResponse(events);
    }

    /**
     * Retrieves the user associated with the authentication token.
     *
     * @return The authenticated user.
     * @throws UserNotFoundException If the user is not found.
     */
    private User getUserFromToken() {
        Integer userId = authService.getUserIdFromToken();
        User user = userRepository.findByUserId(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }
        return user;
    }

    /**
     * Maps a list of events to a list of response objects specific to user's events.
     *
     * @param events The list of events to map.
     * @return The list of mapped response objects.
     */
    private List<GetMyEventsResponse> mapEventsToMyEventsResponse(List<Event> events) {
        return events.stream()
                .map(event -> {
                    GetMyEventsResponse eventResponse = modelMapper.map(event, GetMyEventsResponse.class);
                    eventResponse.setCreatedBy(modelMapper.map(event.getUser(), UserWithoutPassword.class));
                    return eventResponse;
                })
                .collect(Collectors.toList());
    }

    @Override
    public String joinEvent(EventAttendees eventAttendees) {
       EventAttendees previousEventAttendees = eventAttendeesRepository.findEventAttendees(eventAttendees.getEventId(), eventAttendees.getUserId());
      if (previousEventAttendees != null) {
            return "User has already joined the event.";
        }
        eventAttendeesRepository.save(eventAttendees);
        return "Event joined successfully.";
    }

    @Override
    public String leaveEvent(EventAttendees eventAttendees){
        eventAttendeesRepository.deleteAttendees(eventAttendees.getEventId(), eventAttendees.getUserId());
        return "Event left successfully.";
    }

    @Override
    public List<EventAttendeesResponse> findAllEventAttendees(EventAttendeesListRequest eventAttendeesListRequest) {

        List<EventAttendeesResponse> eventAttendeesResponseList = eventAttendeesRepository.findEventAttendeesList(eventAttendeesListRequest.getEventId());
        return eventAttendeesResponseList;
    }

}
