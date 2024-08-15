package com.matchify.dto.response;

import com.matchify.model.ChatStatusForUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserChatStatusResponse {
    private Integer userId;
    private String fullName;
    private ChatStatusForUser status;
}
