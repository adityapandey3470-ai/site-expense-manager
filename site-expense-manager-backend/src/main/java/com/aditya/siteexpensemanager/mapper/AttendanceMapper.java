package com.aditya.siteexpensemanager.mapper;

import com.aditya.siteexpensemanager.dto.response.AttendanceResponseDto;
import com.aditya.siteexpensemanager.entity.Attendance;
import org.springframework.stereotype.Component;

@Component
public class AttendanceMapper {

    public AttendanceResponseDto toResponseDto(Attendance attendance) {

        AttendanceResponseDto responseDto = new AttendanceResponseDto();
        responseDto.setId(attendance.getId());
        responseDto.setSiteId(attendance.getSite().getId());
        responseDto.setSiteName(attendance.getSite().getSiteName());
        responseDto.setAttendanceDate(attendance.getAttendanceDate());
        responseDto.setPresentCount(attendance.getPresentCount());
        responseDto.setFoodRateApplied(attendance.getFoodRateApplied());
        responseDto.setTotalFoodAmount(attendance.getTotalFoodAmount());

        return responseDto;
    }
}
