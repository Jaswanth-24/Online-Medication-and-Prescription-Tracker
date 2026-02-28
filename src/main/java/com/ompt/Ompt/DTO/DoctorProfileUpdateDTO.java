package com.ompt.Ompt.DTO;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class DoctorProfileUpdateDTO {

  ///Personal Details
  @NotBlank
  private String name;
  @Email
  @NotBlank private String email;

  @NonNull
  private LocalDate dob;
  @NotBlank
  private String gender;
  @NotBlank private String phone;
  @NotBlank private String address;
  @NotBlank private String emergencyContact;

  @NotBlank private String department;

  // Employment
  @NotNull
  private Integer yearsOfExperience;
  private LocalDate dateOfJoining;
  @NotBlank private String designation;
  @NotBlank private String employmentType;

  // Licensing
  @NotBlank private String licenseNumber;

}
