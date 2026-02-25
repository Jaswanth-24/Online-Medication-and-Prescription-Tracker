package com.ompt.Ompt.DTO;

import lombok.Getter;

@Getter
public class Employment {
  private String type;
  private String department;

  public Employment(String type, String department) {
    this.type = type;
    this.department = department;
  }
}
