package com.matchify.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer eventId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(length = 64)
    @NotBlank(message = "Event Name is mandatory")
    private String eventName;

    @Column(length = 1000)
    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Date of the Event is mandatory")
    private LocalDate eventDate;

    @NotNull(message = "Start time of the Event is mandatory")
    private LocalTime startTime;

    @NotNull(message = "End time of the Event is mandatory")
    private LocalTime endTime;

    private String additionalInstructions;

    @NotBlank(message = "Address is mandatory")
    private String address;

    @NotBlank(message = "Pincode is mandatory")
    private String pincode;

    @NotBlank(message = "City is mandatory")
    private String city;

    private String imageURL;
    private String latitude;
    private String longitude;
}
