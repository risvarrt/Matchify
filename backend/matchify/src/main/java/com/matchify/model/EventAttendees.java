package com.matchify.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "event_attendees")
public class EventAttendees {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventAttendeesId;

    private Integer eventId;

    private Integer userId;
}
