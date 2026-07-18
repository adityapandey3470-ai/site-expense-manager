package com.aditya.siteexpensemanager.controller;

import com.aditya.siteexpensemanager.dto.request.AttendanceRequestDto;
import com.aditya.siteexpensemanager.dto.response.AttendanceResponseDto;
import com.aditya.siteexpensemanager.service.AttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/attendances")
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @PostMapping
    public ResponseEntity<AttendanceResponseDto> markAttendance(
            @Valid @RequestBody AttendanceRequestDto requestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(attendanceService.markAttendance(requestDto));
    }

    @GetMapping
    public ResponseEntity<List<AttendanceResponseDto>> getAllAttendance() {
        return ResponseEntity.ok(attendanceService.getAllAttendance());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AttendanceResponseDto> getAttendanceById(@PathVariable Long id) {
        return ResponseEntity.ok(attendanceService.getAttendanceById(id));
    }

    @GetMapping("/site/{siteId}")
    public ResponseEntity<List<AttendanceResponseDto>> getAttendanceBySite(@PathVariable Long siteId) {
        return ResponseEntity.ok(attendanceService.getAttendanceBySiteId(siteId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> softDeleteAttendance(@PathVariable Long id) {
        attendanceService.softDeleteAttendance(id);
        return ResponseEntity.noContent().build();
    }
}
