package com.ompt.Ompt.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "patient_profiles")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PatientProfile {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @Column(nullable = false)
  private String patientName;

  @Column private String gender;

  @Column private Integer age;

  @Column(length = 10)
  private String phoneNumber;

  @Column private String bloodGroup;

  @Column private String address;
}
