package com.ompt.Ompt.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "doctors")
public class Doctor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "hospital_id")
  private Hospital hospital;

  @Column(name = "rating_total")
  private double ratingTotal;

  @Column(name = "rating_count")
  private int ratingCount;

  @Column(name = "rating")
  private double rating;

  // Personal
  private String employeeId;
  private LocalDate dob;
  private String gender;
  private String phone;
  private String address;
  private String emergencyContact;

  // Employment
  private String department;
  private String designation;
  private String employmentType;
  private LocalDate dateOfJoining;
  private Integer yearsOfExperience;
  private String reportingManager;

  // Licensing
  private String licenseNumber;
  private LocalDate licenseValidUntil;

  // Status
  private boolean profileCompleted;
}
