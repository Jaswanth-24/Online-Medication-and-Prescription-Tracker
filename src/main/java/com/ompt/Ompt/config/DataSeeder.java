package com.ompt.Ompt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ompt.Ompt.DTO.AssignMedicineRequestDTO;
import com.ompt.Ompt.model.*;
import com.ompt.Ompt.repository.*;
import com.ompt.Ompt.service.PatientRecordService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class DataSeeder implements CommandLineRunner {

    private static final String DEMO_PASSWORD = "Pass@1234";

    private final HospitalRepository hospitalRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final DoctorDegreeRepository doctorDegreeRepository;
    private final DoctorSpecializationRepository doctorSpecializationRepository;
    private final SpecializationRepository specializationRepository;
    private final PatientRecordRepository patientRecordRepository;
    private final UserProfileRepository userProfileRepository;
    private final PharmacyRepository pharmacyRepository;
    private final InventoryItemRepository inventoryItemRepository;
    private final DeliveryRepository deliveryRepository;
    private final MedicineMasterRepository medicineMasterRepository;
    private final PatientRecordService patientRecordService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        seedSpecializations();
        seedMedicines();
        seedHospitalUsersAndDoctors();
        seedPharmacy();
    }

    // ------------------------------------------------------------------
    // SPECIALIZATIONS
    // ------------------------------------------------------------------
    private void seedSpecializations() {
        if (specializationRepository.count() > 0) return;

        Specialization s1 = new Specialization();
        s1.setName("General Physician");
        s1.setDescription("Primary care and general medicine");

        Specialization s2 = new Specialization();
        s2.setName("Cardiology");
        s2.setDescription("Heart and cardiovascular diseases");

        Specialization s3 = new Specialization();
        s3.setName("Diabetology");
        s3.setDescription("Diabetes and metabolic disorders");

        specializationRepository.saveAll(List.of(s1, s2, s3));
    }


    // ------------------------------------------------------------------
    // MEDICINES
    // ------------------------------------------------------------------
    private void seedMedicines() throws Exception {
        if (medicineMasterRepository.count() > 0) return;

        medicineMasterRepository.saveAll(List.of(
                createMedicine("Metformin", "500mg", "Tablet", List.of("08:00", "20:00")),
                createMedicine("Amlodipine", "5mg", "Tablet", List.of("09:00")),
                createMedicine("Atorvastatin", "10mg", "Tablet", List.of("21:00"))
        ));
    }

    private MedicineMaster createMedicine(
            String name, String strength, String type, List<String> schedule
    ) throws Exception {
        MedicineMaster m = new MedicineMaster();
        m.setName(name);
        m.setStrength(strength);
        m.setType(type);
        m.setDefaultScheduleJson(objectMapper.writeValueAsString(schedule));
        return m;
    }

    // ------------------------------------------------------------------
    // HOSPITAL + USERS + DOCTORS
    // ------------------------------------------------------------------
    private void seedHospitalUsersAndDoctors() {

        Hospital hospital = hospitalRepository
                .findByNameIgnoreCase("OMPT Demo Hospital")
                .orElseGet(() -> {
                    Hospital h = new Hospital();
                    h.setName("OMPT Demo Hospital");
                    return hospitalRepository.save(h);
                });

        User admin = upsertUser(
                "demo.admin@ompt.test",
                "Demo Admin",
                Role.ADMIN,
                hospital
        );

        User doctor1 = upsertUser(
                "demo.doctor1@ompt.test",
                "Dr. Asha Menon",
                Role.DOCTOR,
                hospital
        );

        User doctor2 = upsertUser(
                "demo.doctor2@ompt.test",
                "Dr. Ravi Iyer",
                Role.DOCTOR,
                hospital
        );

        Doctor d1 = ensureDoctorProfile(doctor1, 8, "AP-MCI-2016-45678", "General Physician");
        Doctor d2 = ensureDoctorProfile(doctor2, 12, "TS-MCI-2012-99881", "Cardiology");

        User patient1 = upsertUser(
                "demo.patient1@ompt.test",
                "Ananya Rao",
                Role.PATIENT,
                hospital
        );

        ensurePatientProfile(patient1, "Female", 34);
        ensurePatientRecord(patient1, doctor1);

        assignDemoMedicine(doctor1, patient1);
    }

    private User upsertUser(String email, String name, Role role, Hospital hospital) {
        return userRepository.findByEmailIgnoreCase(email)
                .orElseGet(() -> {
                    User u = new User();
                    u.setEmail(email.toLowerCase());
                    u.setName(name);
                    u.setRole(role);
                    u.setHospital(hospital);
                    u.setStatus(AccountStatus.ACTIVE);
                    u.setPassword(passwordEncoder.encode(DEMO_PASSWORD));
                    return userRepository.save(u);
                });
    }

    private Doctor ensureDoctorProfile(
            User doctorUser,
            int experience,
            String license,
            String specializationName
    ) {
        Doctor doctor = doctorRepository.findByUserId(doctorUser.getId())
                .orElseGet(() -> {
                    Doctor d = new Doctor();
                    d.setUser(doctorUser);
                    d.setHospital(doctorUser.getHospital());
                    return doctorRepository.save(d);
                });

        doctor.setYearsOfExperience(experience);
        doctor.setLicenseNumber(license);
        doctor.setProfileCompleted(true);
        doctorRepository.save(doctor);

        doctorDegreeRepository.deleteByDoctorId(doctor.getId());
        DoctorDegree degree = new DoctorDegree();
        degree.setDoctor(doctor);
        degree.setDegreeName("MBBS");
        degree.setInstitution("AIIMS Delhi");
        degree.setYearCompleted(2010);
        doctorDegreeRepository.save(degree);

        doctorSpecializationRepository.deleteByDoctorId(doctor.getId());
        Specialization spec = specializationRepository
                .findByNameIgnoreCase(specializationName)
                .orElseThrow();

        DoctorSpecialization ds = new DoctorSpecialization();
        ds.setDoctor(doctor);
        ds.setSpecialization(spec);
        doctorSpecializationRepository.save(ds);

        return doctor;
    }

    // ------------------------------------------------------------------
    // PATIENT
    // ------------------------------------------------------------------
    private void ensurePatientProfile(User patient, String gender, int age) {
        if (userProfileRepository.findByUser(patient).isPresent()) return;

        UserProfile profile = new UserProfile();
        profile.setUser(patient);
        profile.setPatientName(patient.getName());
        profile.setGender(gender);
        profile.setAge(age);
        profile.setPhoneNumber("9999999999");
        profile.setBloodGroup("O+");
        profile.setAddress("Demo Address");
        userProfileRepository.save(profile);
    }

    private void ensurePatientRecord(User patient, User doctor) {
        if (patientRecordRepository.findByUser(patient).isEmpty()) {
            patientRecordService.createForNewPatient(patient, doctor);
        }
    }

    private void assignDemoMedicine(User doctor, User patient) {
        inventoryItemRepository.findAll().stream().findFirst()
                .ifPresent(item ->
                        patientRecordService.assignMedicine(
                                doctor,
                                patient.getId(),
                                new AssignMedicineRequestDTO(
                                        "Metformin",
                                        "500mg",
                                        "Tablet",
                                        "After meals",
                                        item.getId(),
                                        List.of("08:00", "20:00")
                                )
                        )
                );
    }

    // ------------------------------------------------------------------
    // PHARMACY
    // ------------------------------------------------------------------
    private void seedPharmacy() {

        Hospital hospital = hospitalRepository.findByNameIgnoreCase("OMPT Demo Hospital")
                .orElseThrow();

        User pharmacyUser = upsertUser(
                "demo.pharmacy@ompt.test",
                "OMPT Pharmacy Admin",
                Role.PHARMACY,
                hospital
        );

        Pharmacy pharmacy = pharmacyRepository.findByUserId(pharmacyUser.getId())
                .orElseGet(() -> {
                    Pharmacy p = new Pharmacy();
                    p.setUser(pharmacyUser);
                    p.setPharmacyName("OMPT Care Pharmacy");
                    p.setLocation("2nd Floor, OMPT Demo Hospital");
                    return pharmacyRepository.save(p);
                });

        if (inventoryItemRepository.findByPharmacy(pharmacy).isEmpty()) {
            InventoryItem item = new InventoryItem();
            item.setPharmacy(pharmacy);
            item.setName("Metformin");
            item.setDosage("500mg");
            item.setQuantity(150);
            item.setPrice(5.5);
            item.setExpiry(LocalDate.now().plusMonths(12));
            item.setLowStock(false);
            inventoryItemRepository.save(item);
        }

        if (deliveryRepository.findByPharmacy(pharmacy).isEmpty()) {
            Delivery d = new Delivery();
            d.setPharmacy(pharmacy);
            d.setPatientName("Ananya Rao");
            d.setMedicineName("Metformin 500mg");
            d.setStatus("pending");
            d.setPrescribedAt(LocalDateTime.now().minusDays(1));
            deliveryRepository.save(d);
        }
    }
}
