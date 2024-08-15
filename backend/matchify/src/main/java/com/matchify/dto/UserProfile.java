package com.matchify.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
  private Integer userId;
  private String name;
  private String location;
  private String ageRange;
  private Integer similarityScore;
}
