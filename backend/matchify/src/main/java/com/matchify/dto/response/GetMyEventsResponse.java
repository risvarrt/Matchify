package com.matchify.dto.response;

import com.matchify.dto.UserWithoutPassword;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMyEventsResponse {
    private float eventId;
    UserWithoutPassword createdBy;
    private String eventName;
    private String description;
    private String eventDate;
    private String startTime;
    private String endTime;
    private String additionalInstructions;
    private String address;
    private String pincode;
    private String city;
    private String imageURL;
    private String latitude;
    private String longitude;
}

