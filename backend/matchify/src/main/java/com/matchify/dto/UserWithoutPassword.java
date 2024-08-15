package com.matchify.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserWithoutPassword {
  private float userId;
  private String firstName;
  private String lastName;
  private String email;
  private String location;
  private String ageRange;
}
