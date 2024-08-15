package com.matchify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String location;
    private String ageRange;
    private List<GetMyEventsResponse> joinedEvents;
}
