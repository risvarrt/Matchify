package com.matchify.dto.response;

import com.matchify.model.ChatStatusForUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchConnectedUsersResponse {
    private Integer userId;
    private String firstName;
    private String lastName;
    private ChatStatusForUser status;
}
