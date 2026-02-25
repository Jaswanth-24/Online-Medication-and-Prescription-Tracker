package com.ompt.Ompt.DTO;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InventoryItemResponseDTO {
  private Long id;
  private String name;
  private String dosage;
  private int quantity;
  private Double price;
  private LocalDate expiry;
  private boolean lowStock;
}
