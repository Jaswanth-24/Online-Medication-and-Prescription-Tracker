package com.ompt.Ompt.DTO;

import java.time.LocalDate;
import lombok.Data;

@Data
public class DoctorSelfProfileResponseDTO {

    // From User
    private String fullName;
    private String email;

    // From Doctor
    private LocalDate dob;
    private String gender;
    private String phone;
    private String address;
    private String emergencyContact;

    private String designation;
    private Integer yearsOfExperience;
    private LocalDate dateOfJoining;

    private String licenseNumber;
}