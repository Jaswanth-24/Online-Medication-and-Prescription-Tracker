package com.ompt.Ompt.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DoctorRegisterRequestDTO {

  //Personal Details
  @NotBlank private String name;
  @Email @NotBlank private String email;

  @NonNull
  private LocalDate dob;
  @NotBlank
  private String gender;
  @NotBlank private String phone;
  private String address;
  private String emergencyContact;



  // Employment
  @NotBlank private String department;
  @NotNull private Integer yearsOfExperience;
  private LocalDate dateOfJoining;
  @NotBlank private String designation; // optional
  private String employmentType; // optional (default handled in service)

  // Licensing
  @NotBlank private String licenseNumber;

}
