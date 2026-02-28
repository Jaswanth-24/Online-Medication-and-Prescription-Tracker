package com.ompt.Ompt.Controller;

import com.ompt.Ompt.DTO.HospitalSummaryDTO;
import com.ompt.Ompt.DTO.MedicineMasterDTO;
import com.ompt.Ompt.DTO.PharmacyAvailabilityDTO;
import com.ompt.Ompt.DTO.PublicDoctorDTO;
import com.ompt.Ompt.repository.HospitalRepository;
import com.ompt.Ompt.service.DoctorProfileService;
import com.ompt.Ompt.service.MedicineMasterService;
import com.ompt.Ompt.service.PharmacyService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
@AllArgsConstructor
public class PublicController {

  private final HospitalRepository hospitalRepository;
  private final DoctorProfileService doctorProfileService;
  private final PharmacyService pharmacyService;
  private final MedicineMasterService medicineMasterService;
  @GetMapping("/hospitals")
  public List<HospitalSummaryDTO> listHospitals() {
    return hospitalRepository.findAll().stream()
        .map(h -> new HospitalSummaryDTO(h.getId(), h.getName()))
        .toList();
  }

  @GetMapping("/doctors")
  public List<PublicDoctorDTO> listDoctors() {
    return doctorProfileService.listPublicDoctors();
  }

  @GetMapping("/medicines")
  public List<MedicineMasterDTO> listMedicines() {
    return medicineMasterService.listAll();
  }

  @GetMapping("/medicines/search")
  public List<PharmacyAvailabilityDTO> searchAvailableMedicines(
          @RequestParam String q) {
    return pharmacyService.listAvailability(q);
  }

  @GetMapping("/doctors/by-hospital/{hospitalId}")
  public List<PublicDoctorDTO> listDoctorsByHospital(@PathVariable Long hospitalId) {
    return doctorProfileService.listPublicDoctorsByHospital(hospitalId);
  }
}
