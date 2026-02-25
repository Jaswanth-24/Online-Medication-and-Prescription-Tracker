package com.ompt.Ompt.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "deliveries")
public class Delivery {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "pharmacy_id", nullable = false)
  private Pharmacy pharmacy;

  @Column(nullable = false, length = 150)
  private String patientName;

  @Column(nullable = false, length = 150)
  private String medicineName;

  @Column(nullable = false, length = 30)
  private String status;

  @Column(nullable = false)
  private LocalDateTime prescribedAt;

  @PrePersist
  public void onCreate() {
    if (this.prescribedAt == null) {
      this.prescribedAt = LocalDateTime.now();
    }
  }
}
