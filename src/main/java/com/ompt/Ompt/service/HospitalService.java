package com.ompt.Ompt.service;

import com.ompt.Ompt.DTO.OrganizationRegisterRequest;
import com.ompt.Ompt.DTO.OrganizationResponse;
import com.ompt.Ompt.model.Hospitals;
import com.ompt.Ompt.model.Role;
import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.OrganizationRepository;
import com.ompt.Ompt.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final  UserRepository userrepo;


    //Hospitals Registration Service
    @Transactional
    public OrganizationResponse registerOrganization(OrganizationRegisterRequest request) {

        organizationRepository.findByName(request.getOrganizationName())
                .ifPresent(o -> {
                    throw new IllegalArgumentException("Hospitals already exists");
                });
        organizationRepository.findByName(request.getEmail())
                .ifPresent(o -> {
                    throw new IllegalArgumentException("Email already exists");
                });

        Hospitals hospitals = new Hospitals();
        hospitals.setName(request.getOrganizationName());

        Hospitals savedHospitals = organizationRepository.save(hospitals);

        User user = new User();
        user.setName(request.getAdminName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ADMIN);
        user.setHospitals(savedHospitals);
        userrepo.save(user);

        return new OrganizationResponse(
                savedHospitals.getId(),
                savedHospitals.getName(),
                savedHospitals.isActive()

        );
    }
}
