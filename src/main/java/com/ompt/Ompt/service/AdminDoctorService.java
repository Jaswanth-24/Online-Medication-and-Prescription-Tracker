package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.*;
import com.ompt.Ompt.model.AccountStatus;
import com.ompt.Ompt.model.Doctor;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.DoctorRepository;
import com.ompt.Ompt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AdminDoctorService {

    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final DoctorRepository doctorRepository;

    public DoctorResponseDTO registerDoctor(
            DoctorRegisterRequestDTO request,
            User admin
    ) {


        if (admin.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only admin can register doctors");
        }

        if (userRepository.existsByEmailIgnoreCase(request.getEmail())) {
            throw new IllegalArgumentException("Doctor email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setStatus(AccountStatus.PENDING);
        user.setEmail(request.getEmail().toLowerCase());
        user.setPassword(null);
        user.setRole(Role.DOCTOR);
        user.setHospital(admin.getHospital());

        String token = UUID.randomUUID().toString();
        user.setResetTokenHash(passwordEncoder.encode(token));
        user.setResetTokenExpiry(LocalDateTime.now().plusHours(24));

        userRepository.save(user);

        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setHospital(admin.getHospital());
        doctor.setProfileCompleted(false);

        doctorRepository.save(doctor);

        emailService.sendDoctorWelcomeMail(
                user.getEmail(),
                admin.getHospital().getName(),
                token
        );

        return new DoctorResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                admin.getHospital().getName(),
                false
        );
    }

    public List<DoctorProfileDTO> listDoctors(User admin) {

        return doctorRepository
                .findAllByUser_Hospital_Id(admin.getHospital().getId())
                .stream()
                .map(doctor -> {

                    String employmentType =
                            (doctor.getEmploymentType() == null || doctor.getEmploymentType().isBlank())
                                    ? "Full-time"
                                    : doctor.getEmploymentType();

                    String department =
                            (doctor.getDepartment() == null || doctor.getDepartment().isBlank())
                                    ? "General"
                                    : doctor.getDepartment();

                    return new DoctorProfileDTO(
                            doctor.getId(),

                            new Personal(
                                    doctor.getUser().getName()
                            ),

                            new Qualifications(
                                    "General"
                            ),

                            new Employment(
                                    employmentType,
                                    department
                            ),

                            new Performance(
                                    doctor.getRating(),
                                    doctor.getRatingCount()
                            )
                    );
                })
                .toList();
    }
    public Map<String, Object> getDoctorTemplateForReact() {

        Map<String, Object> root = new LinkedHashMap<>();

        root.put("id", null);
        root.put("hospitalId", null);
        root.put("hospitalName", null);

        root.put("personal", Map.of(
                "fullName", "",
                "employeeId", "",
                "dob", "",
                "gender", "",
                "phone", "",
                "email", "",
                "address", "",
                "emergencyContact", ""
        ));

        root.put("qualifications", Map.of(
                "degrees", "",
                "specialization", "",
                "university", "",
                "yearOfGraduation", "",
                "registrationNumber", ""
        ));

        root.put("employment", Map.of(
                "department", "",
                "designation", "",
                "type", "Full-time",
                "dateOfJoining", "",
                "experience", "",
                "reportingManager", ""
        ));

        root.put("licensing", Map.of(
                "licenseNumber", "",
                "validUntil", "",
                "certifications", "",
                "malpracticeInsurance", ""
        ));

        root.put("schedule", Map.of(
                "opdTimings", "",
                "dutyShifts", "",
                "leaveRecords", "0",
                "onCall", "No"
        ));

        root.put("clinical", Map.of(
                "areasOfExpertise", "",
                "procedures", "",
                "surgeriesConducted", 0,
                "consultationCount", 0
        ));

        root.put("performance", Map.of(
                "rating", 0,
                "ratingCount", 0,
                "ratingTotal", 0,
                "peerReviews", "",
                "trainingParticipation", ""
        ));

        root.put("financial", Map.of(
                "salaryStructure", "",
                "bankAccount", "",
                "taxInfo", ""
        ));

        root.put("digital", Map.of(
                "systemLogin", "",
                "accessLevel", "Doctor",
                "auditLogs", List.of()
        ));

        root.put("legal", Map.of(
                "contracts", "Signed",
                "ndaStatus", "Active",
                "disciplinaryActions", "None"
        ));

        root.put("research", Map.of(
                "publications", "",
                "clinicalTrials", ""
        ));

        return root;
    }


}
