package com.skillcapsule.skillcapsule.service;

import com.skillcapsule.skillcapsule.config.JwtUtil;
import com.skillcapsule.skillcapsule.dto.AuthResponse;
import com.skillcapsule.skillcapsule.dto.LoginRequest;
import com.skillcapsule.skillcapsule.dto.RegisterRequest;
import com.skillcapsule.skillcapsule.entity.User;
import com.skillcapsule.skillcapsule.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    // ✅ REGISTER
    public AuthResponse register(RegisterRequest request) {

        // 🔒 CHECK DUPLICATE EMAIL (THIS WAS MISSING)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setProvider("LOCAL");

        userRepository.save(user);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build();

        String token = jwtUtil.generateToken(userDetails);

        // ✅ Send welcome email
        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    // ✅ LOGIN
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User
                        .withUsername(user.getEmail())
                        .password(user.getPassword())
                        .authorities("USER")
                        .build();

        String token = jwtUtil.generateToken(userDetails);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        return new AuthResponse(
                token,
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }
}
