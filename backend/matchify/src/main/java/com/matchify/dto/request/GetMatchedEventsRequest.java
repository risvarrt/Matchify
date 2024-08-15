package com.matchify.dto.request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMatchedEventsRequest {
    @NotEmpty(message = "Matched user ids cannot be empty")
    public List<Integer> matchedUserIds;
}

