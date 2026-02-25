package com.ompt.Ompt.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(
    name = "doctor_specializations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"doctor_id", "specialization_id"})})
public class DoctorSpecialization {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "doctor_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_doctor_specialization_doctor"))
  private Doctor doctor;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(
      name = "specialization_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_doctor_specialization_specialization"))
  private Specialization specialization;
}
