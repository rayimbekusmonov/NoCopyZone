package com.rayimbek.nocopyzone.service;

import com.rayimbek.nocopyzone.entity.Role;
import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.RoleRepository;
import com.rayimbek.nocopyzone.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Data
    @AllArgsConstructor
    public static class UserDetailResponse {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private boolean active;
        private boolean blocked;
        private String blockedReason;
        private String createdAt;
    }

    @Data
    public static class CreateUserRequest {
        private String fullName;
        private String email;
        private String password;
        private String role;
    }

    // Barcha foydalanuvchilar
    public List<UserDetailResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::toResponse).collect(Collectors.toList());
    }

    // Rol bo'yicha filter
    public List<UserDetailResponse> getUsersByRole(String roleName) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRole().getName().equals("ROLE_" + roleName.toUpperCase()))
                .map(this::toResponse).collect(Collectors.toList());
    }

    // Foydalanuvchi yaratish
    @Transactional
    public UserDetailResponse createUser(CreateUserRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Bu email allaqachon mavjud: " + req.getEmail());
        }
        Role role = roleRepository.findByName("ROLE_" + req.getRole().toUpperCase())
                .orElseThrow(() -> new RuntimeException("Rol topilmadi"));
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(passwordEncoder.encode(req.getPassword()));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    // Bloklash
    @Transactional
    public UserDetailResponse blockUser(Long userId, String reason) {
        User user = getUser(userId);
        user.setBlocked(true);
        user.setBlockedReason(reason);
        user.setActive(false);
        return toResponse(userRepository.save(user));
    }

    // Blokdan chiqarish
    @Transactional
    public UserDetailResponse unblockUser(Long userId) {
        User user = getUser(userId);
        user.setBlocked(false);
        user.setBlockedReason(null);
        user.setActive(true);
        return toResponse(userRepository.save(user));
    }

    // Rol o'zgartirish
    @Transactional
    public UserDetailResponse changeRole(Long userId, String roleName) {
        User user = getUser(userId);
        Role role = roleRepository.findByName("ROLE_" + roleName.toUpperCase())
                .orElseThrow(() -> new RuntimeException("Rol topilmadi"));
        user.setRole(role);
        return toResponse(userRepository.save(user));
    }

    // Parol tiklash
    @Transactional
    public void resetPassword(Long userId, String newPassword) {
        User user = getUser(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    // O'chirish
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    private User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Foydalanuvchi topilmadi: " + id));
    }

    private UserDetailResponse toResponse(User u) {
        return new UserDetailResponse(
                u.getId(), u.getFullName(), u.getEmail(),
                u.getRole().getName(), u.isEnabled(),
                u.getBlocked() != null && u.getBlocked(),
                u.getBlockedReason(),
                u.getCreatedAt().toString());
    }
}