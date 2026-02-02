package com.ompt.Ompt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "doctor_degrees")
@Getter @Setter
@NoArgsConstructor
public class DoctorDegree {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(nullable = false)
    private String degreeName;

    @Column(nullable = false)
    private String institution;

    private Integer yearCompleted;
}
