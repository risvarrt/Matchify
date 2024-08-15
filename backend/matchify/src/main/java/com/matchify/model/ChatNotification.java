package com.matchify.model;

import java.util.Date;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatNotification {
    private Long id;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
}
