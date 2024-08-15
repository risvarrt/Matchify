package com.matchify.service;

import com.matchify.dto.response.FetchInterestResponse;
import com.matchify.dto.request.FillInterestRequest;
import com.matchify.dto.response.FillInterestResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface InterestService {
    FillInterestResponse insertInterest(FillInterestRequest fillInterestRequest);
    List<FetchInterestResponse> fetchInterest();
}
