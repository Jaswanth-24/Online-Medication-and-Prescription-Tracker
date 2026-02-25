package com.ompt.Ompt.DTO;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryRequestDTO {

  @NotBlank(message = "Patient is required")
  private String patient;

  @NotBlank(message = "Medicine is required")
  private String medicine;

  private String status;

  private LocalDateTime date;
}
