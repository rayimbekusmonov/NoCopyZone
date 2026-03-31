package com.rayimbek.nocopyzone.controller;

import com.rayimbek.nocopyzone.entity.User;
import com.rayimbek.nocopyzone.repository.SubmissionRepository;
import com.rayimbek.nocopyzone.service.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileController {

    private final FileStorageService fileStorageService;
    private final SubmissionRepository submissionRepository;

    private static final List<String> ALLOWED_TYPES = Arrays.asList(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.openxmlformats-officedocument.presentationml.presentation",
            "image/png", "image/jpeg",
            "application/zip", "text/plain"
    );

    private static final long MAX_SIZE = 50 * 1024 * 1024; // 50MB

    // Fayl yuklash (submission uchun)
    @PostMapping("/upload/{submissionId}")
    public ResponseEntity<Map<String, String>> upload(
            @PathVariable Long submissionId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User student) {

        if (file.isEmpty()) {
            throw new RuntimeException("Fayl bo'sh");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new RuntimeException("Fayl hajmi 50MB dan oshmasligi kerak");
        }
        if (!ALLOWED_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("Fayl turi qo'llab-quvvatlanmaydi: " + file.getContentType());
        }

        var submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Submission topilmadi"));

        if (!submission.getStudent().getId().equals(student.getId())) {
            throw new RuntimeException("Ruxsat yo'q");
        }

        String fileUrl = fileStorageService.store(file);
        submission.setFileUrl(fileUrl);
        submission.setStatus("SUBMITTED");
        submissionRepository.save(submission);

        return ResponseEntity.ok(Map.of(
                "fileUrl", fileUrl,
                "fileName", file.getOriginalFilename() != null ? file.getOriginalFilename() : "file",
                "size", String.valueOf(file.getSize())
        ));
    }

    // Fayl yuklash (task uchun — ustoz topshiriq faylini qo'shadi)
    @PostMapping("/task-file")
    public ResponseEntity<Map<String, String>> uploadTaskFile(
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) throw new RuntimeException("Fayl bo'sh");
        if (file.getSize() > MAX_SIZE) throw new RuntimeException("Fayl 50MB dan oshmasin");

        String fileUrl = fileStorageService.store(file);
        return ResponseEntity.ok(Map.of(
                "fileUrl", fileUrl,
                "fileName", file.getOriginalFilename() != null ? file.getOriginalFilename() : "file"
        ));
    }

    // Faylni yuklab olish
    @GetMapping("/{fileName}")
    public ResponseEntity<byte[]> download(@PathVariable String fileName) {
        byte[] data = fileStorageService.load(fileName);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}