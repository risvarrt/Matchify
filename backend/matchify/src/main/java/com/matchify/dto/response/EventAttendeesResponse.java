package com.matchify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventAttendeesResponse {
    private Integer userId;
    private String firstName;
    private String lastName;
}
