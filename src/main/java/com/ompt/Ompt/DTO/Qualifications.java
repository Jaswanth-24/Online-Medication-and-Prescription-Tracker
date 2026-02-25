package com.ompt.Ompt.DTO;

import lombok.Getter;

@Getter
public class Qualifications {
  private String specialization;

  public Qualifications(String specialization) {
    this.specialization = specialization;
  }
}
