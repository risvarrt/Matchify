package com.matchify.dto.response;

import com.matchify.model.InterestCategory;
import jdk.jshell.Snippet;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InterestCategoryResponse {
    private Integer id;
    private String name;
}
