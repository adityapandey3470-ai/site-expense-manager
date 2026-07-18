package com.aditya.siteexpensemanager.mapper;

import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.dto.response.RequestResponseDto;
import com.aditya.siteexpensemanager.entity.Request;
import org.springframework.stereotype.Component;

@Component
public class RequestMapper {

    public Request toEntity(RequestRequestDto requestDto) {

        Request request = new Request();

        request.setRequestedBy(requestDto.getRequestedBy());
        request.setRequestType(requestDto.getRequestType());
        request.setDescription(requestDto.getDescription());
        request.setAmount(requestDto.getAmount());

        return request;
    }

    public RequestResponseDto toResponseDto(Request request) {

        RequestResponseDto responseDto = new RequestResponseDto();

        responseDto.setId(request.getId());
        responseDto.setRequestCode(request.getRequestCode());

        responseDto.setSiteId(request.getSite().getId());

        if (request.getTravelExpense() != null) {
            responseDto.setTravelExpenseId(request.getTravelExpense().getId());
        }

        responseDto.setRequestedBy(request.getRequestedBy());
        responseDto.setRequestType(request.getRequestType());
        responseDto.setDescription(request.getDescription());
        responseDto.setAmount(request.getAmount());
        responseDto.setStatus(request.getStatus());
        responseDto.setApprovalStage(request.getApprovalStage());
        responseDto.setApproverName(request.getApproverName());
        responseDto.setRejectionReason(request.getRejectionReason());
        responseDto.setRequestDate(request.getRequestDate());
        responseDto.setActionDate(request.getActionDate());
        responseDto.setActive(request.isActive());

        return responseDto;
    }

    public void updateEntity(RequestRequestDto requestDto, Request request) {

        request.setRequestedBy(requestDto.getRequestedBy());
        request.setRequestType(requestDto.getRequestType());
        request.setDescription(requestDto.getDescription());
        request.setAmount(requestDto.getAmount());
    }
}