package com.ompt.Ompt.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Service
@AllArgsConstructor

public class AuthService {
    
    private final UserRepository userrepo;
    private final BCryptPasswordEncoder passwordEncoder;

    

    public User register(User user) throws Exception {
        if(userrepo.findByEmail(user.getEmail()).isPresent()){
            throw new IllegalStateException("Email already registered");
        }        
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userrepo.save(user);
    }

    public User login(String email,String rawPassword) throws Exception{
        User user=userrepo.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("Invalid Username"));
        if(!passwordEncoder.matches(rawPassword, user.getPassword())){
            throw new BadCredentialsException("Invalid Password");
        }
        return user;
    }

    public void forgotPassword(String email){
        userrepo.findByEmail(email).ifPresent(user -> {
            String token= UUID.randomUUID().toString();
            user.setResetTokenHash(passwordEncoder.encode(token));
            user.setResetTokenExpiry(LocalDateTime.now().plusMinutes(15));
            userrepo.save(user);
            System.out.println("Reset Link: http://localhost:8080/reset-password?token="+token);
        });
    }

    public void resetPassword(String token, String newPassword) {
        User user=userrepo
                .findAll()
                .stream()
                .filter(u->u.getResetTokenHash()!=null)
                .filter(u-> passwordEncoder.matches(token,u.getResetTokenHash()))
                .findFirst()
                .orElseThrow(()->new IllegalStateException("Invalid or Expired Token")
                );
        if(user.getResetTokenExpiry().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("Token Expired");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetTokenHash(null);
        user.setResetTokenExpiry(null);
        userrepo.save(user);
    }
}
