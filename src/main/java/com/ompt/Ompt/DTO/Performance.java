package com.ompt.Ompt.DTO;

import lombok.Getter;

@Getter
public class Performance {
  private Double rating;
  private Integer ratingCount;

  public Performance(Double rating, Integer ratingCount) {
    this.rating = rating;
    this.ratingCount = ratingCount;
  }
}
