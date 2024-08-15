package com.matchify.controller;

import com.matchify.dto.response.FetchInterestResponse;
import com.matchify.dto.request.FillInterestRequest;
import com.matchify.dto.response.FillInterestResponse;
import com.matchify.service.InterestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/interest")
public class InterestController {
    @Autowired
    private InterestService interestService;

    @GetMapping
    public ResponseEntity<List<FetchInterestResponse>> fetchAllInterests() {
        return ResponseEntity.status(HttpStatus.OK).body(interestService.fetchInterest());
    }

    @PostMapping("/fill-my-interest")
    public ResponseEntity<FillInterestResponse> fillMyInterest(@RequestBody FillInterestRequest fillInterestRequest) {
        var fillInterestResponse = interestService.insertInterest(fillInterestRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(fillInterestResponse);
    }
}
