package com.ompt.Ompt.Controller;

import java.util.Map;

import com.ompt.Ompt.DTO.AuthRequestDTO;
import com.ompt.Ompt.Util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ompt.Ompt.model.User;
import com.ompt.Ompt.service.AuthService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;


    @PostMapping("/register")
    public User register(@RequestBody User user) throws Exception{
        return authService.register(user);
    }

    @PostMapping("/login")
    public Map<String,String> login(@RequestBody Map<String,String> req) throws Exception{
        User user=authService.login(req.get("email"), req.get("password"));

        String token=jwtUtil.generateToken(user.getEmail(),user.getRole());
        return Map.of("message","Login Successfull","name",user.getName(),"role",user.getRole(),"token",token);
        
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody AuthRequestDTO request){
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("If email exists, reset link sent");
    }

    @PostMapping("/reset-password")
    public  ResponseEntity<String> resetPassword(@RequestBody AuthRequestDTO request){
        authService.resetPassword(request.getToken(),request.getNewPassword());
        return ResponseEntity.ok("Password Reset Succesful");
    }
}
