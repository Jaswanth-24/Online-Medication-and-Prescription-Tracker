package com.ompt.Ompt.Controller;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.model.UserProfile;
import com.ompt.Ompt.repository.UserRepository;
import com.ompt.Ompt.service.UserProfileService;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/profile")
@AllArgsConstructor
public class ProfileController {

    private final UserRepository userRepository;
    private final UserProfileService userProfileService;

    @PostMapping
    public UserProfile saveProfile(@RequestBody UserProfile userProfile, Authentication authentication) throws UsernameNotFoundException {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElseThrow(() ->new UsernameNotFoundException("User not found"));

        return userProfileService.saveOrUpdateProfile(user, userProfile);
    }

}
