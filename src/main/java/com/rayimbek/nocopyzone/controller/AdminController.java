package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.service.AdminService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<List<AdminService.UserDetailResponse>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/users/role/{role}")
    public ResponseEntity<List<AdminService.UserDetailResponse>> getByRole(@PathVariable String role) {
        return ResponseEntity.ok(adminService.getUsersByRole(role));
    }

    @PostMapping("/users")
    public ResponseEntity<AdminService.UserDetailResponse> createUser(
            @RequestBody AdminService.CreateUserRequest request) {
        return ResponseEntity.ok(adminService.createUser(request));
    }

    @PostMapping("/users/{userId}/block")
    public ResponseEntity<AdminService.UserDetailResponse> blockUser(
            @PathVariable Long userId,
            @RequestBody BlockRequest request) {
        return ResponseEntity.ok(adminService.blockUser(userId, request.getReason()));
    }

    @PostMapping("/users/{userId}/unblock")
    public ResponseEntity<AdminService.UserDetailResponse> unblockUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.unblockUser(userId));
    }

    @PostMapping("/users/{userId}/role")
    public ResponseEntity<AdminService.UserDetailResponse> changeRole(
            @PathVariable Long userId,
            @RequestBody RoleRequest request) {
        return ResponseEntity.ok(adminService.changeRole(userId, request.getRole()));
    }

    @PostMapping("/users/{userId}/reset-password")
    public ResponseEntity<Void> resetPassword(
            @PathVariable Long userId,
            @RequestBody PasswordRequest request) {
        adminService.resetPassword(userId, request.getPassword());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok().build();
    }

    @Data static class BlockRequest { private String reason; }
    @Data static class RoleRequest { private String role; }
    @Data static class PasswordRequest { private String password; }
}