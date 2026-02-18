package com.ompt.Ompt.DTO;

import com.ompt.Ompt.DTO.Personal;
import com.ompt.Ompt.DTO.Qualifications;
import com.ompt.Ompt.DTO.Employment;
import com.ompt.Ompt.DTO.Performance;

public class DoctorProfileDTO {

    private Long id;
    private Personal personal;
    private Qualifications qualifications;
    private Employment employment;
    private Performance performance;

    public DoctorProfileDTO(
            Long id,
            Personal personal,
            Qualifications qualifications,
            Employment employment,
            Performance performance
    ) {
        this.id = id;
        this.personal = personal;
        this.qualifications = qualifications;
        this.employment = employment;
        this.performance = performance;
    }

    public Long getId() { return id; }
    public Personal getPersonal() { return personal; }
    public Qualifications getQualifications() { return qualifications; }
    public Employment getEmployment() { return employment; }
    public Performance getPerformance() { return performance; }
}
