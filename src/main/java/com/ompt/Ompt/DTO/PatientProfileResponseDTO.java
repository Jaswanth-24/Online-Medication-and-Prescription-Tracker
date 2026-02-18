package com.ompt.Ompt.DTO;


import com.ompt.Ompt.model.PatientProfile;
import lombok.*;

@Getter
@AllArgsConstructor
public class PatientProfileResponseDTO {

    private String patientName;
    private String gender;
    private Integer age;
    private String phoneNumber;
    private String bloodGroup;
    private String address;

    public static PatientProfileResponseDTO from (PatientProfile p) {
        return new PatientProfileResponseDTO(
                p.getPatientName(),
                p.getGender(),
                p.getAge(),
                p.getPhoneNumber(),
                p.getBloodGroup(),
                p.getAddress()
        );
    }
}
