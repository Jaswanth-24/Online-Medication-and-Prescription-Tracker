package com.ompt.Ompt.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "pharmacy_inventory")
public class InventoryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "medicine_id")
  private MedicineMaster medicine;


  @ManyToOne(optional = false)
  private Pharmacy pharmacy;

  @Column(length = 100)
  private String dosage;

  @Column(nullable = false)
  private int quantity;

  @Column private Double price;

  private LocalDate expiry;

  @Column(nullable = false)
  private boolean lowStock;
}
