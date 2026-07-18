package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.AttendanceRequestDto;
import com.aditya.siteexpensemanager.dto.response.AttendanceResponseDto;
import com.aditya.siteexpensemanager.entity.Attendance;
import com.aditya.siteexpensemanager.entity.Ledger;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.AttendanceMapper;
import com.aditya.siteexpensemanager.repository.AttendanceRepository;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final SiteRepository siteRepository;
    private final LedgerRepository ledgerRepository;
    private final AttendanceMapper attendanceMapper;

    // Configurable in application.properties as app.food-rate-per-person (default 330).
    @Value("${app.food-rate-per-person:330}")
    private BigDecimal foodRatePerPerson;

    @Override
    @Transactional
    public AttendanceResponseDto markAttendance(AttendanceRequestDto requestDto) {

        Site site = siteRepository.findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: " + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalArgumentException(
                    "Attendance cannot be marked for an inactive site"
            );
        }

        if (requestDto.getAttendanceDate().isBefore(site.getStartDate())
                || requestDto.getAttendanceDate().isAfter(site.getEndDate())) {
            throw new IllegalArgumentException(
                    "Attendance date must be between site start date and end date"
            );
        }

        if (attendanceRepository.existsBySiteIdAndAttendanceDateAndDeletedFalse(
                site.getId(), requestDto.getAttendanceDate())) {
            throw new IllegalStateException(
                    "Attendance already marked for this site on " + requestDto.getAttendanceDate()
            );
        }

        BigDecimal totalAmount = foodRatePerPerson
                .multiply(BigDecimal.valueOf(requestDto.getPresentCount()));

        Attendance attendance = new Attendance();
        attendance.setSite(site);
        attendance.setAttendanceDate(requestDto.getAttendanceDate());
        attendance.setPresentCount(requestDto.getPresentCount());
        attendance.setFoodRateApplied(foodRatePerPerson);
        attendance.setTotalFoodAmount(totalAmount);
        attendance.setDeleted(false);

        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Auto-accrue the food expense as a DEBIT ledger entry against the site.
        Ledger ledger = Ledger.builder()
                .site(site)
                .entryType(LedgerEntryType.DEBIT)
                .sourceType(LedgerSourceType.ATTENDANCE)
                .sourceId(savedAttendance.getId())
                .amount(totalAmount)
                .description("Food accrual " + requestDto.getPresentCount()
                        + " x " + foodRatePerPerson + " on " + requestDto.getAttendanceDate())
                .transactionDate(requestDto.getAttendanceDate())
                .deleted(false)
                .build();

        ledgerRepository.save(ledger);

        return attendanceMapper.toResponseDto(savedAttendance);
    }



    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAllAttendance() {

        return attendanceRepository.findAllByDeletedFalseAndSiteDeletedFalse()
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponseDto getAttendanceById(Long id) {

        return attendanceMapper.toResponseDto(findActiveAttendance(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceResponseDto> getAttendanceBySiteId(Long siteId) {

        return attendanceRepository
                .findAllBySiteIdAndDeletedFalseAndSiteDeletedFalse(siteId)
                .stream()
                .map(attendanceMapper::toResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void softDeleteAttendance(Long id) {

        Attendance attendance = findActiveAttendance(id);

        attendance.setDeleted(true);
        attendanceRepository.save(attendance);

        // Reverse the linked ledger entry so the site balance stays correct.
        ledgerRepository.findAllBySiteIdAndDeletedFalse(attendance.getSite().getId())
                .stream()
                .filter(l -> l.getSourceType() == LedgerSourceType.ATTENDANCE
                        && id.equals(l.getSourceId())
                        && !l.getDeleted())
                .forEach(l -> {
                    l.setDeleted(true);
                    ledgerRepository.save(l);
                });
    }

    private Attendance findActiveAttendance(Long id) {

        return attendanceRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Attendance not found with id: " + id
                        )
                );
    }
}
