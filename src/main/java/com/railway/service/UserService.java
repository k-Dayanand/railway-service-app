package com.railway.service;

import com.railway.dto.RegisterDTO;
import com.railway.model.User;
import com.railway.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    private final UserRepository  userRepository;
    private final PasswordEncoder passwordEncoder;
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /** Register a new USER */
    public User register(RegisterDTO dto) {
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new RuntimeException("Email already registered: " + dto.getEmail());

        if (!dto.getPassword().equals(dto.getConfirmPassword()))
            throw new RuntimeException("Passwords do not match");

        User user = User.builder()
            .name(dto.getName())
            .email(dto.getEmail())
            .password(passwordEncoder.encode(dto.getPassword()))
            .phone(dto.getPhone())
            .role(User.Role.ADMIN)
            .build();

        return userRepository.save(user);
    }

    /** Find user by email */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /** Get all users (admin) */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /** Count total users */
    public long countUsers() {
        return userRepository.count();
    }

    /** Delete user (admin) */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
