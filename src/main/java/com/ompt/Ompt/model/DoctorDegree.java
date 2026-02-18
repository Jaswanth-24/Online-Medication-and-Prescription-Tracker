package com.ompt.Ompt.model;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "doctor_degrees")
public class DoctorDegree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Doctor doctor;

    private String degreeName;
    private String institution;
    private Integer yearCompleted;
}

