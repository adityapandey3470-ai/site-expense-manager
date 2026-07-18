package com.aditya.siteexpensemanager.service;

import com.aditya.siteexpensemanager.dto.request.AttendanceRequestDto;
import com.aditya.siteexpensemanager.dto.response.AttendanceResponseDto;

import java.util.List;

public interface AttendanceService {

    AttendanceResponseDto markAttendance(AttendanceRequestDto requestDto);

    List<AttendanceResponseDto> getAllAttendance();

    AttendanceResponseDto getAttendanceById(Long id);

    List<AttendanceResponseDto> getAttendanceBySiteId(Long siteId);

    void softDeleteAttendance(Long id);
}
