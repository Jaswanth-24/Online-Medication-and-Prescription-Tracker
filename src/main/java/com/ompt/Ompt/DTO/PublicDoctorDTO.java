package com.ompt.Ompt.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicDoctorDTO {

  private Long doctorId;
  private String name;
  private String hospitalName;
  private String department;
  private String designation;
  private Integer yearsOfExperience;
  private Performance performance;

  @Data
  @AllArgsConstructor
  public static class Performance {
    private Double rating;
    private Integer ratingCount;
  }
}