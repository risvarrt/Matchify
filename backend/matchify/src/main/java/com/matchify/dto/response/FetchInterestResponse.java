package com.matchify.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchInterestResponse {
    private Integer groupId;
    private String groupName;
    private List<InterestCategoryResponse> categories;
}
