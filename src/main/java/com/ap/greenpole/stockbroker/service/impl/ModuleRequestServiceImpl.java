package com.ap.greenpole.stockbroker.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ap.greenpole.stockbroker.dto.Error;
import com.ap.greenpole.stockbroker.dto.Notification;
import com.ap.greenpole.stockbroker.dto.RequestAuthorization;
import com.ap.greenpole.stockbroker.dto.Result;
import com.ap.greenpole.stockbroker.model.ModuleRequest;
import com.ap.greenpole.stockbroker.model.StockBroker;
import com.ap.greenpole.stockbroker.repository.ModuleRequestRepository;
import com.ap.greenpole.stockbroker.service.ModuleRequestService;
import com.ap.greenpole.stockbroker.service.NotificationPostingsService;
import com.ap.greenpole.stockbroker.utils.BeanUtils;
import com.ap.greenpole.stockbroker.utils.ConstantUtils;
import com.ap.greenpole.stockbroker.utils.Utils;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

@Service
public class ModuleRequestServiceImpl implements ModuleRequestService {

	private static Logger log = LoggerFactory.getLogger(ModuleRequestServiceImpl.class);

	@Autowired
	private ModuleRequestRepository moduleRequestRepository;

	@Autowired
	private StockBrokerServiceImpl stockBrokerService;
	
	@Autowired
	private NotificationPostingsService notificationPostingsService;
	
	@Autowired
	private UserService userService;

	private Map<String, Integer> approvalStatus;

	@PostConstruct
	private void setDefaultHeaders() {
		approvalStatus = new HashMap<>();
		approvalStatus.put(ConstantUtils.APPROVAL_STATUS[1], ConstantUtils.PENDING);
		approvalStatus.put(ConstantUtils.APPROVAL_STATUS[2], ConstantUtils.REJECTED);
		approvalStatus.put(ConstantUtils.APPROVAL_STATUS[3], ConstantUtils.ACCEPTED);
	}

	@Override
	public Long createApprovalRequest(ModuleRequest moduleRequest, List<String> dataOwnerEmail, String dataOwnerName,
			List<String> dataOwnerPhones, User user) {

		log.info("Creating resource {} ", moduleRequest);
		moduleRequest.setStatus(ConstantUtils.PENDING);
		moduleRequest.setCreatedOn(new Date());
		moduleRequest.setRequesterId(user.getId());
		moduleRequest.setModules(ConstantUtils.MODULE);
		moduleRequest.setRequestCode(requestCode(ConstantUtils.MODULE));
		moduleRequest = moduleRequestRepository.save(moduleRequest);
		log.info("Created resource {} ", moduleRequest);

		Notification notification = new Notification(dataOwnerName, user.getFirstName(), dataOwnerPhones,
				dataOwnerEmail, Utils.commaSeperatedToList(user.getPhone()),
				Utils.commaSeperatedToList(user.getEmail()));

		notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.PENDING);

		return moduleRequest.getRequestId();
	}

	@Override
	public List<ModuleRequest> getAllApprovalRequest() {
		return moduleRequestRepository.findAllModuleRequestByModules(ConstantUtils.MODULE);
	}

	@Override
	public Result<ModuleRequest> getAllApprovalRequest(int pageNumber, int pageSize, Pageable pageable) {

		Page<ModuleRequest> allRecords = moduleRequestRepository.findAllModuleRequestByModules(ConstantUtils.MODULE,
				pageable);
		long noOfRecords = allRecords.getTotalElements();

		return new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);
	}

	@Override
	public ModuleRequest getApprovalRequestById(Long approvalRequestId) {
		return moduleRequestRepository.findModuleRequestByRequestIdAndModules(approvalRequestId, ConstantUtils.MODULE);
	}

	@Override
	public void updateApprovalRequest(ModuleRequest moduleRequest) {
		moduleRequest.setApprovedOn(new Date());
		log.info("Updating resource {} ", moduleRequest);
		moduleRequest = moduleRequestRepository.save(moduleRequest);
		log.info("Updated resource {} ", moduleRequest);
	}

	@Override
	public ModuleRequest getApprovalRequestByResourceIdAndActionRequired(ModuleRequest moduleRequest) {
		return moduleRequestRepository
				.findFirstModuleRequestByResourceIdAndActionRequiredAndModulesOrderByRequestIdDesc(
						moduleRequest.getResourceId(), moduleRequest.getActionRequired(), ConstantUtils.MODULE);
	}

	private String requestCode(String stockBroker) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmsss");
		String date = simpleDateFormat.format(new Date());
		return stockBroker + date;
	}

	@Override
	public Object authorizeRequest(RequestAuthorization requestAuthorization, User user)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		List<Error> errors = new ArrayList<>();
		for (long requestId : requestAuthorization.getRequestIds()) {

			ModuleRequest request = getApprovalRequestById(requestId);
			if (request == null) {
				Error error = new Error(null, "Request ID " + requestId + " does not exist");
				errors.add(error);
			} else {
				if (ConstantUtils.PENDING != request.getStatus()) {
					Error error = new Error(null, "Request ID: " + requestId + " has been "
							+ ConstantUtils.APPROVAL_STATUS[request.getStatus()]);
					errors.add(error);
				} else {
					Gson gson = new GsonBuilder().setDateFormat("MMM dd, yyyy, HH:mm:ss a").create();
					if (ConstantUtils.REQUEST_TYPES[0].equalsIgnoreCase(request.getActionRequired())) {
						StockBroker pendingStBroker = gson.fromJson(request.getNewRecord(), StockBroker.class);
						if (pendingStBroker != null) {
							if (ConstantUtils.APPROVAL_ACTIONS[0].equalsIgnoreCase(requestAuthorization.getAction())) {
								StockBroker stockBrokerInDb = stockBrokerService
										.getStockBrokerByCSCSAccountNumber(pendingStBroker.getCscsAccountNumber());
								if (stockBrokerInDb != null) {
									Error error = new Error(null,
											"Stockbroker CSCS account number with "
													+ pendingStBroker.getCscsAccountNumber() + " "
													+ " already exists, Request ID: " + requestId);
									errors.add(error);
								} else {
									stockBrokerService.createStockBroker(pendingStBroker);
									request = Utils.setRequestStatus(ConstantUtils.ACCEPTED, user.getId(), null,
											request);
									updateApprovalRequest(request);

									Notification notification = Utils.setUpNotification(pendingStBroker, user);
									notificationPostingsService.determineVerdictAndCallNotificationService(notification,
											ConstantUtils.ACCEPTED);
								}
							} else {
								request = Utils.setRequestStatus(ConstantUtils.REJECTED, user.getId(),
										requestAuthorization.getComment(), request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(pendingStBroker, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification,
										ConstantUtils.REJECTED);
							}
						}
					} else if (ConstantUtils.REQUEST_TYPES[1].equalsIgnoreCase(request.getActionRequired())) {
						StockBroker newRecord = gson.fromJson(request.getNewRecord(), StockBroker.class);
						StockBroker oldRecord = gson.fromJson(request.getOldRecord(), StockBroker.class);
						if (newRecord != null && oldRecord != null) {
							if (ConstantUtils.APPROVAL_ACTIONS[0].equalsIgnoreCase(requestAuthorization.getAction())) {
								BeanUtils<StockBroker> notNull = new BeanUtils<>();
								notNull.copyNonNullProperties(oldRecord, newRecord);

								stockBrokerService.updateStockBroker(oldRecord);
								request = Utils.setRequestStatus(ConstantUtils.ACCEPTED, user.getId(), null, request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification,
										ConstantUtils.ACCEPTED);
							} else {
								request = Utils.setRequestStatus(ConstantUtils.REJECTED, user.getId(),
										requestAuthorization.getComment(), request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.REJECTED);
							}
						}
					} else if (ConstantUtils.REQUEST_TYPES[2].equalsIgnoreCase(request.getActionRequired())) {
						StockBroker oldRecord = gson.fromJson(request.getOldRecord(), StockBroker.class);
						if (oldRecord != null) {
							if (ConstantUtils.APPROVAL_ACTIONS[0].equalsIgnoreCase(requestAuthorization.getAction())) {
								oldRecord.setActive(false);
								stockBrokerService.updateStockBroker(oldRecord);
								request = Utils.setRequestStatus(ConstantUtils.ACCEPTED, user.getId(), null, request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.ACCEPTED);
							} else {
								request = Utils.setRequestStatus(ConstantUtils.REJECTED, user.getId(),
										requestAuthorization.getComment(), request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.REJECTED);
							}
						}
					} else if (ConstantUtils.REQUEST_TYPES[3].equalsIgnoreCase(request.getActionRequired())) {
						StockBroker newRecord = gson.fromJson(request.getNewRecord(), StockBroker.class);
						StockBroker oldRecord = gson.fromJson(request.getOldRecord(), StockBroker.class);
						if (newRecord != null && oldRecord != null) {
							if (ConstantUtils.APPROVAL_ACTIONS[0].equalsIgnoreCase(requestAuthorization.getAction())) {
								BeanUtils<StockBroker> notNull = new BeanUtils<>();
								notNull.copyNonNullProperties(oldRecord, newRecord);

								stockBrokerService.updateStockBroker(oldRecord);
								request = Utils.setRequestStatus(ConstantUtils.ACCEPTED, user.getId(), null, request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.ACCEPTED);
							} else {
								request = Utils.setRequestStatus(ConstantUtils.REJECTED, user.getId(),
										requestAuthorization.getComment(), request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.REJECTED);
							}
						}
					} else if (ConstantUtils.REQUEST_TYPES[4].equalsIgnoreCase(request.getActionRequired())) {
						StockBroker newRecord = gson.fromJson(request.getNewRecord(), StockBroker.class);
						StockBroker oldRecord = gson.fromJson(request.getOldRecord(), StockBroker.class);
						if (newRecord != null && oldRecord != null) {
							if (ConstantUtils.APPROVAL_ACTIONS[0].equalsIgnoreCase(requestAuthorization.getAction())) {
								BeanUtils<StockBroker> notNull = new BeanUtils<>();
								notNull.copyNonNullProperties(oldRecord, newRecord);

								stockBrokerService.updateStockBroker(oldRecord);
								request = Utils.setRequestStatus(ConstantUtils.ACCEPTED, user.getId(), null, request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.ACCEPTED);
							} else {
								request = Utils.setRequestStatus(ConstantUtils.REJECTED, user.getId(),
										requestAuthorization.getComment(), request);
								updateApprovalRequest(request);

								Notification notification = Utils.setUpNotification(oldRecord, user);
								notificationPostingsService.determineVerdictAndCallNotificationService(notification, ConstantUtils.REJECTED);
							}
						}
					}
				}
			}
		}
		return errors;
	}

	@Override
	public Result<ModuleRequest> getStockBrokerApprovalNotifications(Pageable pageable, String status) {

		Page<ModuleRequest> allRecords = moduleRequestRepository
				.findAllModuleRequestByModulesAndStatus(ConstantUtils.MODULE, approvalStatus.get(status), pageable);
		long noOfRecords = allRecords.getTotalElements();

		return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
	}

	@Override
	public Result<ModuleRequest> getStockBrokerApprovalNotificationByDateCreated(Date date, Pageable pageable) {
		
		Page<ModuleRequest> allRecords = moduleRequestRepository
				.findAllModuleRequestByModulesAndCreatedOnEquals(ConstantUtils.MODULE, date, pageable);
		long noOfRecords = allRecords.getTotalElements();

		return new Result<>(0, allRecords.getContent(), noOfRecords, pageable.getPageNumber() + 1, pageable.getPageSize());
	}

	@Override
	public List<ModuleRequest> getStockBrokerApprovalNotificationByUserAndAction(RequestAuthorization request) {
		return moduleRequestRepository.findAllModuleRequestByModulesAndStatusAndApproverId(ConstantUtils.MODULE,
				approvalStatus.get(request.getAction()), request.getApproverId());
	}

	@Override
	public Result<ModuleRequest> getApprovalRequestSearch(String query, String status, Pageable pageable) {
		Page<ModuleRequest> allRecords = moduleRequestRepository
				.findAllModuleRequestByModulesAndStatusAndOldRecordContainingOrNewRecordContaining(ConstantUtils.MODULE,
						approvalStatus.get(status), query, query, pageable);

		return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
				pageable.getPageSize());
	}

	@Override
	public Result<ModuleRequest> getStockBrokerApprovalNotificationByDateCreatedAndStatus(Date dateObject,
			String status, Pageable pageable) {
		DateTime dateTime = new DateTime(dateObject);
		DateTime dateBound = dateTime.plusDays(1);
		Page<ModuleRequest> allRecords = moduleRequestRepository
				.findAllModuleRequestByModulesAndCreatedOnBetweenAndStatus(ConstantUtils.MODULE, dateObject,
						dateBound.toDate(), approvalStatus.get(status), pageable);

		return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
				pageable.getPageSize());
	}

	@Override
	public Result<ModuleRequest> getStockBrokerApprovalNotificationsByStatusAndType(Pageable pageable, String status,
			String type, Long userId) {
		if(Utils.isEmptyString(type) || ConstantUtils.TYPES[1].equalsIgnoreCase(type)) {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndStatus(ConstantUtils.MODULE,
							approvalStatus.get(status), pageable);  
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		}else {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndStatusAndRequesterId(ConstantUtils.MODULE,
							approvalStatus.get(status), userId, pageable);    
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		}
	}

	@Override
	public Result<ModuleRequest> getApprovalRequestSearch(String query, String status, String type, Long userId,
			Pageable pageable) {
		if (Utils.isEmptyString(type) || ConstantUtils.TYPES[1].equalsIgnoreCase(type)) {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndStatusAndOldRecordContainingOrNewRecordContaining(
							ConstantUtils.MODULE, approvalStatus.get(status), query, query, pageable);
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		} else {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndStatusAndRequesterIdAndOldRecordContainingOrNewRecordContaining(
							ConstantUtils.MODULE, approvalStatus.get(status), userId, query, query, pageable);
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		}
	}

	@Override
	public Result<ModuleRequest> getStockBrokerApprovalNotificationByDateCreatedAndStatusAndType(Date dateObject,
			String status, String type, Long userId, Pageable pageable) {
		DateTime dateTime = new DateTime(dateObject);
		DateTime dateBound = dateTime.plusDays(1);
		if (Utils.isEmptyString(type) || ConstantUtils.TYPES[1].equalsIgnoreCase(type)) {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndCreatedOnBetweenAndStatus(ConstantUtils.MODULE, dateObject,
							dateBound.toDate(), approvalStatus.get(status), pageable);
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		}else {
			Page<ModuleRequest> allRecords = moduleRequestRepository
					.findAllModuleRequestByModulesAndCreatedOnBetweenAndStatusAndRequesterId(ConstantUtils.MODULE, dateObject,
							dateBound.toDate(), approvalStatus.get(status), userId, pageable);
			
			if(allRecords != null && !allRecords.isEmpty()) {
				allRecords.forEach(request -> request.setRequester(userService.getUserById(request.getRequesterId()).get()));
			}

			return new Result<>(0, allRecords.getContent(), allRecords.getTotalElements(), pageable.getPageNumber() + 1,
					pageable.getPageSize());
		}
	}
}
