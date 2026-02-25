package com.ompt.Ompt.DTO;

import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class DoctorProfileUpdateDTO {

  // Personal
  private LocalDate dob;
  private String gender;
  private String phone;
  private String address;
  private String emergencyContact;

  // Employment
  private String designation;
  private Integer yearsOfExperience;
  private LocalDate dateOfJoining;

  // Licensing
  private String licenseNumber;
}
