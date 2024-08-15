package com.matchify.controller;

import com.matchify.dto.response.FindMatchesResponse;
import com.matchify.service.MatchService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/match")
public class MatchController {
  @Autowired
  private MatchService matchService;

  // PostMapping for handling POST requests for showing matches.
  @GetMapping("/findMatches")
  public ResponseEntity<FindMatchesResponse> findMatches(
      @Valid @RequestHeader("Authorization") String authorizationHeader) {
    return new ResponseEntity<FindMatchesResponse>(
        matchService.findMatches(), HttpStatusCode.valueOf(200));
  }

}
