package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.DoctorDegreeRepository;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.DoctorSpecializationRepository;
import com.ompt.Ompt.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminDoctorService {

  private final UserRepository userRepository;
  private final DoctorRepository doctorRepository;
  private final DoctorSpecializationRepository doctorSpecializationRepository;
  private final DoctorDegreeRepository doctorDegreeRepository;
  private final EmailService emailService;
  private final PasswordEncoder passwordEncoder;

  // ADMIN creates doctor

  @Transactional
  public DoctorResponseDTO registerDoctor(DoctorRegisterRequestDTO request, User admin) {

    if (admin.getRole() != Role.ADMIN) {
      throw new AccessDeniedException("Only admin can create doctors");
    }

    if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
      throw new IllegalArgumentException("Doctor email already exists");
    }

    // ---- USER ----
    User user = new User();
    user.setName(request.getName());
    user.setEmail(request.getEmail().toLowerCase());
    user.setRole(Role.DOCTOR);
    user.setHospital(admin.getHospital());
    user.setStatus(AccountStatus.PENDING); // inactive until password set
    user.setPassword(null);

    String token = UUID.randomUUID().toString();
    user.setResetTokenHash(passwordEncoder.encode(token));
    user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

    userRepository.save(user);

    // ---- DOCTOR ----
    Doctor doctor = new Doctor();
    doctor.setUser(user);
    doctor.setHospital(admin.getHospital());
    doctor.setDepartment(request.getDepartment() == null ? "General" : request.getDepartment());
    doctor.setDesignation(request.getDesignation());
    doctor.setEmploymentType(
        request.getEmploymentType() == null ? "Full-time" : request.getEmploymentType());
    doctor.setProfileCompleted(false);

    doctorRepository.save(doctor);

    // ---- EMAIL ----
    emailService.sendDoctorWelcomeMail(user.getEmail(), admin.getHospital().getName(), token);

    return new DoctorResponseDTO(
        doctor.getId(), user.getName(), user.getEmail(), admin.getHospital().getName(), false);
  }

  // ADMIN lists doctors
  public List<DoctorResponseDTO> listDoctors(User admin) {

    if (admin.getRole() != Role.ADMIN) {
      throw new AccessDeniedException("Only admin can view doctors");
    }

    return doctorRepository.findAllByUser_Hospital_Id(admin.getHospital().getId()).stream()
        .map(
            doctor ->
                new DoctorResponseDTO(
                    doctor.getId(),
                    doctor.getUser().getName(),
                    doctor.getUser().getEmail(),
                    admin.getHospital().getName(),
                    doctor.isProfileCompleted()))
        .toList();
  }

  @Transactional(readOnly = true)
  public DoctorProfileResponseDTO getDoctorProfileForAdmin(User admin, Long doctorId) {

    if (admin.getRole() != Role.ADMIN) {
      throw new AccessDeniedException("Only admin can view doctor profile");
    }

    Doctor doctor =
        doctorRepository
            .findById(doctorId)
            .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

    DoctorProfileResponseDTO dto = new DoctorProfileResponseDTO();

    // -------- Core identity --------
    dto.setDoctorId(doctor.getId());
    dto.setFullName(doctor.getUser().getName());
    dto.setEmail(doctor.getUser().getEmail());
    dto.setPhone(doctor.getPhone());

    // -------- Employment --------
    dto.setDepartment(doctor.getDepartment());
    dto.setDesignation(doctor.getDesignation());
    dto.setYearsOfExperience(doctor.getYearsOfExperience());

    // -------- Licensing --------
    dto.setLicenseNumber(doctor.getLicenseNumber());

    // -------- Hospital --------
    if (doctor.getHospital() != null) {
      dto.setHospitalId(doctor.getHospital().getId());
      dto.setHospitalName(doctor.getHospital().getName());
    }

    // -------- Degrees --------
    List<DoctorDegreeDTO> degreeDTOs =
        doctorDegreeRepository.findByDoctorId(doctor.getId()).stream()
            .map(
                degree ->
                    new DoctorDegreeDTO(
                        degree.getDegreeName(), degree.getInstitution(), degree.getYearCompleted()))
            .toList();

    dto.setDegrees(degreeDTOs);

    // -------- Specializations --------
    List<String> specializations =
        doctorSpecializationRepository.findByDoctorId(doctor.getId()).stream()
            .map(ds -> ds.getSpecialization().getName())
            .toList();

    dto.setSpecializations(specializations);

    return dto;
  }
}
