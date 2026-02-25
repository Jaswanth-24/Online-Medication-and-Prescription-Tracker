package com.ompt.Ompt.DTO;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryResponseDTO {
  private Long id;
  private String patient;
  private String medicine;
  private String status;
  private LocalDateTime date;
}
