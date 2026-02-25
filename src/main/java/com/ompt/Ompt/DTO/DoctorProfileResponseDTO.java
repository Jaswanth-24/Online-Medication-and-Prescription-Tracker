package com.ompt.Ompt.DTO;

import java.util.List;
import lombok.*;

@Data
public class DoctorProfileResponseDTO {

  private Long doctorId;
  private String fullName;
  private String email;
  private String phone;

  private String department;
  private String designation;
  private Integer yearsOfExperience;

  private String licenseNumber;

  private List<DoctorDegreeDTO> degrees;
  private List<String> specializations;

  private Long hospitalId;
  private String hospitalName;
}
