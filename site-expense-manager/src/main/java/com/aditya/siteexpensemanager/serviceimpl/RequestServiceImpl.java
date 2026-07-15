package com.aditya.siteexpensemanager.serviceimpl;

import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.dto.response.RequestResponseDto;
import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.RequestType;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.exception.ResourceNotFoundException;
import com.aditya.siteexpensemanager.mapper.RequestMapper;
import com.aditya.siteexpensemanager.repository.LedgerRepository;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.RequestService;
import com.aditya.siteexpensemanager.service.TravelExpenseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final SiteRepository siteRepository;
    private final TravelExpenseRepository travelExpenseRepository;
    private final RequestMapper requestMapper;
    private final TravelExpenseService travelExpenseService;
    private final LedgerRepository ledgerRepository;

    @Override
    @Transactional
    public RequestResponseDto createRequest(RequestRequestDto requestDto) {

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalStateException(
                    "Cannot create request for an inactive site"
            );
        }

        if (requestDto.getRequestType() == RequestType.TRAVEL_EXPENSE
                && requestDto.getTravelExpenseId() == null) {

            throw new IllegalArgumentException(
                    "Travel expense id is required for TRAVEL_EXPENSE request type"
            );
        }

        if (requestDto.getRequestType() != RequestType.TRAVEL_EXPENSE
                && requestDto.getTravelExpenseId() != null) {

            throw new IllegalArgumentException(
                    "Travel expense id is allowed only for TRAVEL_EXPENSE request type"
            );
        }

        TravelExpense travelExpense = null;

        if (requestDto.getTravelExpenseId() != null) {

            travelExpense = travelExpenseRepository
                    .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
                            requestDto.getTravelExpenseId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Travel expense not found with id: "
                                            + requestDto.getTravelExpenseId()
                            )
                    );
            if (!travelExpense.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException(
                        "Travel expense does not belong to the selected site"
                );
            }

            if (travelExpense.getTravelStatus() != TravelExpenseStatus.PENDING) {
                throw new IllegalStateException(
                        "Only pending travel expense can be requested"
                );
            }

            if (requestRepository
                    .existsByTravelExpense_IdAndStatusAndDeletedFalseAndActiveTrue(
                            travelExpense.getId(),
                            RequestStatus.PENDING
                    )) {
                throw new IllegalStateException(
                        "A pending request already exists for this travel expense"
                );
            }
        }

        Request request = requestMapper.toEntity(requestDto);

        request.setSite(site);
        request.setTravelExpense(travelExpense);
        request.setRequestCode(generateRequestCode());
        request.setStatus(RequestStatus.PENDING);
        request.setRequestDate(LocalDate.now());
        request.setActive(true);
        request.setDeleted(false);

        Request savedRequest = requestRepository.save(request);

        return requestMapper.toResponseDto(savedRequest);
    }

    @Override
    public List<RequestResponseDto> getAllRequests() {

        return requestRepository
                .findAllByDeletedFalseAndSiteDeletedFalse()
                .stream()
                .map(requestMapper::toResponseDto)
                .toList();
    }

    @Override
    public RequestResponseDto getRequestById(Long id) {

        Request request = getLockedExistingRequest(id);

        return requestMapper.toResponseDto(request);
    }

    @Override
    @Transactional
    public RequestResponseDto updateRequest(
            Long id,
            RequestRequestDto requestDto
    ) {

        Request existingRequest = getLockedExistingRequest(id);

        if (existingRequest.getStatus()
                != RequestStatus.PENDING) {

            throw new IllegalStateException(
                    "Only pending requests can be updated"
            );
        }

        Site site = siteRepository
                .findByIdAndDeletedFalse(requestDto.getSiteId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Site not found with id: "
                                        + requestDto.getSiteId()
                        )
                );

        if (!site.getActive()) {
            throw new IllegalStateException(
                    "Cannot update request for an inactive site"
            );
        }
        if (requestDto.getRequestType() == RequestType.TRAVEL_EXPENSE
                && requestDto.getTravelExpenseId() == null) {

            throw new IllegalArgumentException(
                    "Travel expense id is required for TRAVEL_EXPENSE request type"
            );
        }
        if (requestDto.getRequestType() != RequestType.TRAVEL_EXPENSE
                && requestDto.getTravelExpenseId() != null) {

            throw new IllegalArgumentException(
                    "Travel expense id is allowed only for TRAVEL_EXPENSE request type"
            );
        }

        TravelExpense travelExpense = null;

        if (requestDto.getTravelExpenseId() != null) {

            travelExpense = travelExpenseRepository
                    .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
                            requestDto.getTravelExpenseId()
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Travel expense not found with id: "
                                            + requestDto.getTravelExpenseId()
                            )
                    );

            if (!travelExpense.getSite().getId().equals(site.getId())) {
                throw new IllegalArgumentException(
                        "Travel expense does not belong to the selected site"
                );
            }

            if (travelExpense.getTravelStatus() != TravelExpenseStatus.PENDING) {
                throw new IllegalStateException(
                        "Only pending travel expense can be requested"
                );
            }

            if (requestRepository
                    .existsByTravelExpense_IdAndStatusAndDeletedFalseAndActiveTrueAndIdNot(
                            travelExpense.getId(),
                            RequestStatus.PENDING,
                            existingRequest.getId()
                    )) {
                throw new IllegalStateException(
                        "A pending request already exists for this travel expense"
                );
            }
        }

        requestMapper.updateEntity(
                requestDto,
                existingRequest
        );

        existingRequest.setSite(site);
        existingRequest.setTravelExpense(travelExpense);

        Request updatedRequest =
                requestRepository.save(existingRequest);

        return requestMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public void softDeleteRequest(Long id) {

        Request request = getLockedExistingRequest(id);

        if (ledgerRepository.existsBySourceTypeAndSourceIdAndDeletedFalse(
                LedgerSourceType.REQUEST,
                id
        )) {
            throw new IllegalStateException(
                    "Request cannot be deleted because it is referenced by a ledger"
            );
        }

        request.setDeleted(true);
        request.setActive(false);

        requestRepository.save(request);
    }

    @Override
    public void hardDeleteRequest(Long id) {

        Request request = requestRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found with id: " + id
                        )
                );

        if (ledgerRepository.existsBySourceTypeAndSourceIdAndDeletedFalse(
                LedgerSourceType.REQUEST,
                id
        )) {
            throw new IllegalStateException(
                    "Request cannot be hard deleted because it is referenced by a ledger"
            );
        }

        requestRepository.delete(request);
    }

    @Override
    @Transactional
    public RequestResponseDto activateRequest(Long id) {

        Request request = getLockedExistingRequest(id);

        if (!request.getSite().getActive()) {
            throw new IllegalStateException(
                    "Cannot activate request for an inactive site"
            );
        }

        request.setActive(true);

        Request updatedRequest =
                requestRepository.save(request);

        return requestMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto deactivateRequest(Long id) {

        Request request = getLockedExistingRequest(id);

        request.setActive(false);

        Request updatedRequest =
                requestRepository.save(request);

        return requestMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto approveRequest(
            Long id,
            String approverName
    ) {

        Request request = getLockedExistingRequest(id);

        if (approverName == null || approverName.isBlank()) {
            throw new IllegalArgumentException(
                    "Approver name is required"
            );
        }

        checkPendingStatus(request);

        request.setStatus(RequestStatus.APPROVED);
        request.setApproverName(approverName);
        request.setActionDate(LocalDate.now());
        request.setRejectionReason(null);


        if (request.getTravelExpense() != null) {
            travelExpenseService.markAsApproved(
                    request.getTravelExpense().getId()
            );
        }

        Request updatedRequest =
                requestRepository.save(request);

        return requestMapper.toResponseDto(updatedRequest);
    }

    @Override
    @Transactional
    public RequestResponseDto rejectRequest(
            Long id,
            String approverName,
            String rejectionReason
    ) {

        Request request = getExistingRequest(id);

        if (approverName == null || approverName.isBlank()) {
            throw new IllegalArgumentException(
                    "Approver name is required"
            );
        }

        if (rejectionReason == null || rejectionReason.isBlank()) {
            throw new IllegalArgumentException(
                    "Rejection reason is required"
            );
        }

        checkPendingStatus(request);

        request.setStatus(RequestStatus.REJECTED);
        request.setApproverName(approverName);
        request.setRejectionReason(rejectionReason);
        request.setActionDate(LocalDate.now());

        if (request.getTravelExpense() != null) {
            travelExpenseService.markAsRejected(
                    request.getTravelExpense().getId()
            );
        }

        Request updatedRequest =
                requestRepository.save(request);

        return requestMapper.toResponseDto(updatedRequest);
    }

    private Request getExistingRequest(Long id) {

        return requestRepository
                .findByIdAndDeletedFalseAndSiteDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found with id: " + id
                        )
                );
    }

    private Request getLockedExistingRequest(Long id) {

        return requestRepository
                .findLockedByIdAndDeletedFalseAndSiteDeletedFalse(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Request not found with id: " + id
                        )
                );
    }

    private void checkPendingStatus(Request request) {

        if (request.getStatus()
                != RequestStatus.PENDING) {

            throw new IllegalStateException(
                    "Request is already approved or rejected"
            );
        }
    }

    private String generateRequestCode() {

        String requestCode;

        do {
            requestCode = "REQ-"
                    + UUID.randomUUID()
                    .toString()
                    .substring(0, 8)
                    .toUpperCase();

        } while (
                requestRepository
                        .existsByRequestCode(requestCode)
        );

        return requestCode;
    }
}
