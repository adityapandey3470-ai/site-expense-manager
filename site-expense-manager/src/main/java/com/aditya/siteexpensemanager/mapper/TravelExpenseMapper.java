package com.aditya.siteexpensemanager.mapper;

import com.aditya.siteexpensemanager.dto.request.TravelExpenseRequestDto;
import com.aditya.siteexpensemanager.dto.response.TravelExpenseResponseDto;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import org.springframework.stereotype.Component;

@Component
public class TravelExpenseMapper {

    public TravelExpense toEntity(
            TravelExpenseRequestDto requestDto,
            Site site
    ) {

        TravelExpense travelExpense = new TravelExpense();

        travelExpense.setSite(site);
        travelExpense.setEmployeeName(requestDto.getEmployeeName());
        travelExpense.setEmployeeId(requestDto.getEmployeeId());
        travelExpense.setTravelDate(requestDto.getTravelDate());
        travelExpense.setFromLocation(requestDto.getFromLocation());
        travelExpense.setToLocation(requestDto.getToLocation());
        travelExpense.setTravelMode(requestDto.getTravelMode());
        travelExpense.setTravelCost(requestDto.getTravelCost());
        travelExpense.setTravelPurpose(requestDto.getTravelPurpose());
        travelExpense.setRemarks(requestDto.getRemarks());

        return travelExpense;
    }

    public TravelExpenseResponseDto toResponseDto(
            TravelExpense travelExpense
    ) {

        TravelExpenseResponseDto responseDto =
                new TravelExpenseResponseDto();

        responseDto.setId(travelExpense.getId());
        responseDto.setTravelCode(travelExpense.getTravelCode());

        responseDto.setSiteId(
                travelExpense.getSite().getId()
        );

        responseDto.setSiteName(
                travelExpense.getSite().getSiteName()
        );

        responseDto.setEmployeeName(
                travelExpense.getEmployeeName()
        );

        responseDto.setEmployeeId(
                travelExpense.getEmployeeId()
        );

        responseDto.setTravelDate(
                travelExpense.getTravelDate()
        );

        responseDto.setFromLocation(
                travelExpense.getFromLocation()
        );

        responseDto.setToLocation(
                travelExpense.getToLocation()
        );

        responseDto.setTravelMode(
                travelExpense.getTravelMode()
        );

        responseDto.setTravelCost(
                travelExpense.getTravelCost()
        );

        responseDto.setTravelPurpose(
                travelExpense.getTravelPurpose()
        );

        responseDto.setTravelStatus(
                travelExpense.getTravelStatus()
        );

        responseDto.setRemarks(
                travelExpense.getRemarks()
        );

        return responseDto;
    }

    public void updateEntity(
            TravelExpense travelExpense,
            TravelExpenseRequestDto requestDto,
            Site site
    ) {

        travelExpense.setSite(site);
        travelExpense.setEmployeeName(requestDto.getEmployeeName());
        travelExpense.setEmployeeId(requestDto.getEmployeeId());
        travelExpense.setTravelDate(requestDto.getTravelDate());
        travelExpense.setFromLocation(requestDto.getFromLocation());
        travelExpense.setToLocation(requestDto.getToLocation());
        travelExpense.setTravelMode(requestDto.getTravelMode());
        travelExpense.setTravelCost(requestDto.getTravelCost());
        travelExpense.setTravelPurpose(requestDto.getTravelPurpose());
        travelExpense.setRemarks(requestDto.getRemarks());
    }
}