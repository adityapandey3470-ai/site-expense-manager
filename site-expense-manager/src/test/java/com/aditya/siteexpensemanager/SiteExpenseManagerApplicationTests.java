package com.aditya.siteexpensemanager;

import com.aditya.siteexpensemanager.dto.request.LedgerRequestDto;
import com.aditya.siteexpensemanager.dto.request.RequestRequestDto;
import com.aditya.siteexpensemanager.entity.Request;
import com.aditya.siteexpensemanager.entity.Site;
import com.aditya.siteexpensemanager.entity.TravelExpense;
import com.aditya.siteexpensemanager.enums.LedgerEntryType;
import com.aditya.siteexpensemanager.enums.LedgerSourceType;
import com.aditya.siteexpensemanager.enums.RequestStatus;
import com.aditya.siteexpensemanager.enums.RequestType;
import com.aditya.siteexpensemanager.enums.TravelExpenseStatus;
import com.aditya.siteexpensemanager.enums.TravelMode;
import com.aditya.siteexpensemanager.repository.RequestRepository;
import com.aditya.siteexpensemanager.repository.SiteRepository;
import com.aditya.siteexpensemanager.repository.TravelExpenseRepository;
import com.aditya.siteexpensemanager.service.LedgerService;
import com.aditya.siteexpensemanager.service.RequestService;
import com.aditya.siteexpensemanager.service.SiteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class SiteExpenseManagerApplicationTests {

	@Autowired
	private SiteRepository siteRepository;

	@Autowired
	private TravelExpenseRepository travelExpenseRepository;

	@Autowired
	private RequestRepository requestRepository;

	@Autowired
	private RequestService requestService;

	@Autowired
	private LedgerService ledgerService;

	@Autowired
	private SiteService siteService;

	@Test
	void contextLoads() {
	}

	@Test
	@Transactional
	void lockingRepositoryMethodAndPendingRequestExistsQueryWork() {
		Site site = siteRepository.save(buildSite("SITE-LOCK"));
		TravelExpense travelExpense =
				travelExpenseRepository.save(buildTravelExpense(site, "TRV-LOCK"));

		var lockedTravelExpense =
				travelExpenseRepository
						.findLockedByIdAndDeletedFalseAndSiteDeletedFalse(
								travelExpense.getId()
						);

		assertTrue(lockedTravelExpense.isPresent());
		assertEquals(site.getId(), lockedTravelExpense.get().getSite().getId());

		Request request = new Request();
		request.setRequestCode("REQ-LOCK");
		request.setSite(site);
		request.setTravelExpense(travelExpense);
		request.setRequestedBy("Aditya");
		request.setRequestType(RequestType.TRAVEL_EXPENSE);
		request.setDescription("Travel approval");
		request.setStatus(RequestStatus.PENDING);
		request.setRequestDate(LocalDate.now());
		request.setActive(true);
		request.setDeleted(false);

		requestRepository.saveAndFlush(request);

		assertTrue(
				requestRepository
						.existsByTravelExpense_IdAndStatusAndDeletedFalseAndActiveTrue(
								travelExpense.getId(),
								RequestStatus.PENDING
						)
		);
	}

	@Test
	void duplicatePendingTravelExpenseRequestIsRejected() {
		Site site = siteRepository.save(buildSite("SITE-DUP"));
		TravelExpense travelExpense =
				travelExpenseRepository.save(buildTravelExpense(site, "TRV-DUP"));

		RequestRequestDto requestDto = new RequestRequestDto();
		requestDto.setSiteId(site.getId());
		requestDto.setTravelExpenseId(travelExpense.getId());
		requestDto.setRequestedBy("Aditya");
		requestDto.setRequestType(RequestType.TRAVEL_EXPENSE);
		requestDto.setDescription("Travel approval");

		requestService.createRequest(requestDto);

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> requestService.createRequest(requestDto)
		);

		assertEquals(
				"A pending request already exists for this travel expense",
				exception.getMessage()
		);
	}

	@Test
	void ledgerCannotBeCreatedTwiceForSameTravelExpenseThroughRequest() {
		Site site = siteRepository.save(buildSite("SITE-LEDGER-DUP"));
		TravelExpense travelExpense =
				travelExpenseRepository.save(buildTravelExpense(site, "TRV-LEDGER-DUP"));

		RequestRequestDto requestDto = buildTravelRequestDto(site, travelExpense);

		var request = requestService.createRequest(requestDto);
		requestService.approveRequest(request.getId(), "Manager");

		LedgerRequestDto requestLedgerDto = buildLedgerRequestDto(
				site.getId(),
				LedgerSourceType.REQUEST,
				request.getId()
		);

		ledgerService.createLedger(requestLedgerDto);

		LedgerRequestDto travelLedgerDto = buildLedgerRequestDto(
				site.getId(),
				LedgerSourceType.TRAVEL_EXPENSE,
				travelExpense.getId()
		);

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> ledgerService.createLedger(travelLedgerDto)
		);

		assertEquals(
				"Ledger already exists for this travel expense",
				exception.getMessage()
		);
	}

	@Test
	void requestReferencedByLedgerCannotBeSoftDeleted() {
		Site site = siteRepository.save(buildSite("SITE-REQ-DELETE"));
		TravelExpense travelExpense =
				travelExpenseRepository.save(buildTravelExpense(site, "TRV-REQ-DELETE"));

		RequestRequestDto requestDto = buildTravelRequestDto(site, travelExpense);

		var request = requestService.createRequest(requestDto);
		requestService.approveRequest(request.getId(), "Manager");

		ledgerService.createLedger(
				buildLedgerRequestDto(
						site.getId(),
						LedgerSourceType.REQUEST,
						request.getId()
				)
		);

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> requestService.softDeleteRequest(request.getId())
		);

		assertEquals(
				"Request cannot be deleted because it is referenced by a ledger",
				exception.getMessage()
		);
	}

	@Test
	void siteWithRelatedTravelExpenseCannotBeSoftDeleted() {
		Site site = siteRepository.save(buildSite("SITE-SOFT-DELETE"));
		travelExpenseRepository.save(buildTravelExpense(site, "TRV-SOFT-DELETE"));

		IllegalStateException exception = assertThrows(
				IllegalStateException.class,
				() -> siteService.deleteSiteById(site.getId())
		);

		assertEquals(
				"Cannot delete site because related records exist.",
				exception.getMessage()
		);
	}

	private Site buildSite(String siteCode) {
		Site site = new Site();
		site.setSiteName("Metro Site");
		site.setSiteCode(siteCode);
		site.setLocation("Pune");
		site.setProjectManager("Aditya");
		site.setBudget(new BigDecimal("100000.00"));
		site.setStartDate(LocalDate.now().minusDays(1));
		site.setEndDate(LocalDate.now().plusDays(30));
		site.setActive(true);
		site.setDeleted(false);
		return site;
	}

	private TravelExpense buildTravelExpense(Site site, String travelCode) {
		TravelExpense travelExpense = new TravelExpense();
		travelExpense.setTravelCode(travelCode);
		travelExpense.setSite(site);
		travelExpense.setEmployeeName("Aditya");
		travelExpense.setEmployeeId("EMP-1");
		travelExpense.setTravelDate(LocalDate.now());
		travelExpense.setFromLocation("Pune");
		travelExpense.setToLocation("Mumbai");
		travelExpense.setTravelMode(TravelMode.CAR);
		travelExpense.setTravelCost(new BigDecimal("1200.00"));
		travelExpense.setTravelPurpose("Site visit");
		travelExpense.setTravelStatus(TravelExpenseStatus.PENDING);
		travelExpense.setDeleted(false);
		return travelExpense;
	}

	private RequestRequestDto buildTravelRequestDto(
			Site site,
			TravelExpense travelExpense
	) {
		RequestRequestDto requestDto = new RequestRequestDto();
		requestDto.setSiteId(site.getId());
		requestDto.setTravelExpenseId(travelExpense.getId());
		requestDto.setRequestedBy("Aditya");
		requestDto.setRequestType(RequestType.TRAVEL_EXPENSE);
		requestDto.setDescription("Travel approval");
		return requestDto;
	}

	private LedgerRequestDto buildLedgerRequestDto(
			Long siteId,
			LedgerSourceType sourceType,
			Long sourceId
	) {
		LedgerRequestDto ledgerRequestDto = new LedgerRequestDto();
		ledgerRequestDto.setSiteId(siteId);
		ledgerRequestDto.setEntryType(LedgerEntryType.DEBIT);
		ledgerRequestDto.setSourceType(sourceType);
		ledgerRequestDto.setSourceId(sourceId);
		ledgerRequestDto.setTransactionDate(LocalDate.now());
		ledgerRequestDto.setDescription("Travel expense ledger");
		return ledgerRequestDto;
	}

}
