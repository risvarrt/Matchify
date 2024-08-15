package com.matchify.service;

import com.matchify.dto.response.FindMatchesResponse;
import org.springframework.stereotype.Service;


@Service
public interface MatchService {

  FindMatchesResponse findMatches();
}
