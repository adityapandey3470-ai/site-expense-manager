package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.TravelExpenseMapper;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.TravelExpenseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TravelExpenseServiceImpl
        implements TravelExpenseService {

    private final TravelExpenseRepository travelExpenseRepository;
    private final SiteRepository siteRepository;
    private final TravelExpenseMapper travelExpenseMapper;

    @Override
    public TravelExpenseResponseDto createTravelExpense(
            TravelExpenseRequestDto requestDto
    ) {

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalArgumentException(
                    "Travel expense cannot be created for inactive site"
            );
        }

        TravelExpense travelExpense =
                travelExpenseMapper.toEntity(requestDto, site);

        travelExpense.setTravelCode(generateTravelCode());
        travelExpense.setTravelStatus(
                TravelExpenseStatus.PENDING
        );
        travelExpense.setDeleted(false);

        TravelExpense savedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(savedTravelExpense);
    }

    @Override
    public List<TravelExpenseResponseDto>
    getAllTravelExpenses() {

        return travelExpenseRepository
                .findAllByDeletedFalse()
                .stream()
                .map(travelExpenseMapper::toResponseDto)
                .toList();
    }

    @Override
    public TravelExpenseResponseDto
    getTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        return travelExpenseMapper
                .toResponseDto(travelExpense);
    }

    @Override
    public TravelExpenseResponseDto
    updateTravelExpenseById(
            Long id,
            TravelExpenseRequestDto requestDto
    ) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalArgumentException(
                    "Travel expense cannot be assigned to inactive site"
            );
        }

        travelExpenseMapper.updateEntity(
                travelExpense,
                requestDto,
                site
        );

        TravelExpense updatedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(updatedTravelExpense);
    }

    @Override
    public void softDeleteTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        travelExpense.setDeleted(true);

        travelExpenseRepository.save(travelExpense);
    }

    @Override
    public void hardDeleteTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                travelExpenseRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Travel expense not found with id: "
                                                + id
                                )
                        );

        travelExpenseRepository.delete(travelExpense);
    }

    @Override
    public TravelExpenseResponseDto
    approveTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        if (travelExpense.getTravelStatus()
                == TravelExpenseStatus.APPROVED) {

            throw new IllegalArgumentException(
                    "Travel expense is already approved"
            );
        }

        travelExpense.setTravelStatus(
                TravelExpenseStatus.APPROVED
        );

        TravelExpense approvedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(approvedTravelExpense);
    }

    @Override
    public TravelExpenseResponseDto
    rejectTravelExpenseById(Long id) {

        TravelExpense travelExpense =
                findActiveTravelExpenseById(id);

        if (travelExpense.getTravelStatus()
                == TravelExpenseStatus.REJECTED) {

            throw new IllegalArgumentException(
                    "Travel expense is already rejected"
            );
        }

        travelExpense.setTravelStatus(
                TravelExpenseStatus.REJECTED
        );

        TravelExpense rejectedTravelExpense =
                travelExpenseRepository.save(travelExpense);

        return travelExpenseMapper
                .toResponseDto(rejectedTravelExpense);
    }

    private TravelExpense findActiveTravelExpenseById(
            Long id
    ) {

        return travelExpenseRepository
                .findByIdAndDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Travel expense not found with id: "
                                        + id
                        )
                );
    }

    private String generateTravelCode() {

        String travelCode;

        do {
            travelCode = "TRV-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (
                travelExpenseRepository
                        .existsByTravelCode(travelCode)
        );

        return travelCode;
    }
}