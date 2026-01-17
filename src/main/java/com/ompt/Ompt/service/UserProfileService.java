package com.ompt.Ompt.service;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.model.UserProfile;
import com.ompt.Ompt.repository.UserProfileRepository;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor

public class UserProfileService {
    private final UserProfileRepository profileRepository;
    public UserProfile saveOrUpdateProfile(User user,UserProfile data){
        UserProfile profile=profileRepository.findByUser(user).orElse(new UserProfile());

        profile.setUser(user);
        profile.setPatientName(data.getPatientName());
        profile.setPhoneNumber(data.getPhoneNumber());
        profile.setAge(data.getAge());
        profile.setGender(data.getGender());
        profile.setBloodGroup(data.getBloodGroup());
        profile.setAddress(data.getAddress());
        return profileRepository.save(profile);
    }

}
