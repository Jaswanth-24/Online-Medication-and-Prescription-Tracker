package com.ompt.Ompt.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.repository.UserRepository;

import lombok.*;


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
}
