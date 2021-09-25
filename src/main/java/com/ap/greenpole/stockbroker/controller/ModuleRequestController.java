package com.ap.greenpole.stockbroker.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ap.greenpole.stockbroker.dto.RequestAuthorization;
import com.ap.greenpole.stockbroker.dto.Result;
import com.ap.greenpole.stockbroker.dto.StockBrokerAPIResponseCode;
import com.ap.greenpole.stockbroker.dto.StockBrokerBaseResponse;
import com.ap.greenpole.stockbroker.model.ModuleRequest;
import com.ap.greenpole.stockbroker.service.ModuleRequestService;
import com.ap.greenpole.stockbroker.utils.ConstantUtils;
import com.ap.greenpole.stockbroker.utils.Utils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import com.google.gson.Gson;

/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:30 AM
 */

@RestController
@RequestMapping("/api/v1/stockbroker")
public class ModuleRequestController {

	private static Logger logger = LoggerFactory.getLogger(ModuleRequestController.class);
	
	@Autowired
	private ModuleRequestService moduleRequestService;
	
	@Autowired
    private UserService userService;
	
	@GetMapping(value = "/request/{requestId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST"})
	public StockBrokerBaseResponse<ModuleRequest> getStockBrokerApprovalRequestById(@PathVariable long requestId) {
		logger.info("[+] In getStockBrokerApprovalRequestById with approvalRequestId: {}", requestId);
		StockBrokerBaseResponse<ModuleRequest> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		try {
			if (requestId <= 0) {
				response.setStatusMessage("Approval request ID is required");
				return response;
			}
			ModuleRequest moduleRequest = moduleRequestService.getApprovalRequestById(requestId);
			logger.info("getApprovalRequestById returned {}", moduleRequest);
			if (moduleRequest != null) {
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(moduleRequest);
				return response;
			}
			response.setStatusMessage("Stockbroker Approval request not found");
			return response;
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting getStockBrokerApprovalRequestById {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
			return response;
		}
	}
	
	@GetMapping(value = "/requests/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST"})
	public StockBrokerBaseResponse<List<ModuleRequest>> getAllStockBrokerApprovalRequests() {
		logger.info("[+] In getAllStockBrokerApprovalRequests");
		StockBrokerBaseResponse<List<ModuleRequest>> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		List<ModuleRequest> allApprovalRequest = null;
		try {
			allApprovalRequest = moduleRequestService.getAllApprovalRequest();
			if(allApprovalRequest != null && !allApprovalRequest.isEmpty()){
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(allApprovalRequest);
				return response;
			}
			response.setStatusMessage("No result found");
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting AllStockBrokerApprovalRequests {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
			return response;
		}
		return response;
	}
	
	@GetMapping(value = "/requests/paginated", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST"})
	public StockBrokerBaseResponse<List<ModuleRequest>> getAllStockBrokerApprovalRequestsPaginated(
			@RequestHeader(value = "pageSize", required = false, defaultValue = "" + Integer.MAX_VALUE) String pageSize,
			@RequestHeader(value = "pageNumber", required = false, defaultValue = "1") String pageNumber) {
		logger.info("[+] In getAllStockBrokerAccountPaginated with pageSize: {},  pageNumber : {}", pageSize, pageNumber);
		StockBrokerBaseResponse<List<ModuleRequest>> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		try {
            logger.info("[+] Attempting to parse the pagination variables");
            int page = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);
            page = Math.max(0, page - 1);
            size = Math.max(1, size);
            Pageable pageable = PageRequest.of(page, size, Sort.by("requestId").descending());

			Result<ModuleRequest> allApprovalRequest = moduleRequestService
					.getAllApprovalRequest(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable);
			response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
			response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
			response.setData(allApprovalRequest.getList());
			response.setCount(allApprovalRequest.getNoOfRecords());
			return response;
        } catch (NumberFormatException e) {
            logger.error("[-] Error {} occurred while parsing page variable with message: {}",
            		e.getClass().getSimpleName(), e.getMessage());
			response.setStatusMessage("The entered page and size must be integer values.");
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting allStockBrokerAccountPaginated {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
		}
		return response;
	}
	
	@PostMapping(value = "/request/approval", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_STOCKBROKER_APPROVAL"})
	public StockBrokerBaseResponse<Object> authorizeRequest(
			@RequestBody @Validated RequestAuthorization requestAuthorization, HttpServletRequest request) {
		
		StockBrokerBaseResponse<Object> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		
		Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
		
    	logger.info("[+] In authorizeRequest with payload: {}", requestAuthorization);
		try {
			if(requestAuthorization == null) {
				response.setStatusMessage("At least one Approval request ID is required");
				return response;
			}
			if(requestAuthorization.getRequestIds() == null || requestAuthorization.getRequestIds().isEmpty()){
				response.setStatusMessage("Request body is required");
				return response;
			}
			if (!Arrays.asList(ConstantUtils.APPROVAL_ACTIONS).contains(requestAuthorization.getAction())){
				response.setStatusMessage("Approval action invalid, options include " + Arrays.toString(ConstantUtils.APPROVAL_ACTIONS));
				return response;
			}
			if(ConstantUtils.APPROVAL_ACTIONS[1].equalsIgnoreCase(requestAuthorization.getAction()) 
					&& Utils.isEmptyString(requestAuthorization.getComment())) {
				response.setStatusMessage("Comment is required");
				return response;
			}

			Object authorizeRequestResponse = moduleRequestService.authorizeRequest(requestAuthorization, user.get());
			logger.info("getApprovalRequestById returned {}", new Gson().toJson(authorizeRequestResponse));
			if (authorizeRequestResponse != null) {
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(authorizeRequestResponse);
				return response;
			}
		}  catch (Exception e) {
			logger.info("[-] Exception happened while authorizing request {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
		}
		return response;
	}
	
	@GetMapping(value = "/requests", produces = MediaType.APPLICATION_JSON_VALUE)
	@PreAuthorizePermission({ "PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST" })
	public StockBrokerBaseResponse<List<ModuleRequest>> getStockBrokerApprovalRequestsByStatus(
			@RequestParam("status") String status, @RequestParam("offset") int offset, @RequestParam("limit") int limit,
			@RequestParam("type") String type, HttpServletRequest request) {
		logger.info("[+] In getStockBrokerApprovalRequestsByStatus with status: {}, offset: {}, limit: {}, type: {}",
				status, offset, limit, type);
		StockBrokerBaseResponse<List<ModuleRequest>> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		
		Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
		
		try {
			status = Utils.capitalizeFirstLetter(status);
			if (!Arrays.asList(ConstantUtils.APPROVAL_STATUS).contains(status)) {
				response.setStatusMessage("Invalid status [" + status + "], Options include "
						+ Arrays.toString(ConstantUtils.APPROVAL_STATUS));
				return response;
			}
			if(!Utils.isEmptyString(type) && !Arrays.asList(ConstantUtils.TYPES).contains(type.toUpperCase())) {
				response.setStatusMessage("Invalid type [" + type + "], Options include "
						+ Arrays.toString(ConstantUtils.TYPES));
				return response;
			}
			
			logger.info("[+] Attempting to parse the pagination variables");
			int page = Math.max(0, offset - 1);
			int size = Math.max(1, limit);
			Pageable pageable = PageRequest.of(page, size, Sort.by("requestId").descending());

			Result<ModuleRequest> moduleRequest = moduleRequestService
					.getStockBrokerApprovalNotificationsByStatusAndType(pageable, status, type, user.get().getId());
			logger.info("getStockBrokerApprovalNotificationsByStatusAndType returned {}", moduleRequest);
			if (moduleRequest != null) {
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(moduleRequest.getList());
				response.setCount(moduleRequest.getNoOfRecords());
				return response;
			}
			response.setStatusMessage("Stockbroker Approval request not found");
			return response;
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting getStockBrokerApprovalNotificationsByStatusAndType {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
			return response;
		}
	}
	
	@GetMapping(value = "/requests/search", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST"})
	public StockBrokerBaseResponse<List<ModuleRequest>> getStockBrokerApprovalSearch(@RequestParam("query") String query,
			@RequestParam("status") String status, @RequestParam("offset") int offset, @RequestParam("limit") int limit,
			@RequestParam("type")String type, HttpServletRequest request) {
		logger.info("[+] In getStockBrokerApprovalSearch with query: {}, status: {}, type: {}", query, status, type);
		StockBrokerBaseResponse<List<ModuleRequest>> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		
		Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
		
		try {
			
			status = Utils.capitalizeFirstLetter(status);
			if (!Arrays.asList(ConstantUtils.APPROVAL_STATUS).contains(status)) {
				response.setStatusMessage("Invalid status [" + status + "], Options include "
						+ Arrays.toString(ConstantUtils.APPROVAL_STATUS));
				return response;
			}
			if(!Utils.isEmptyString(type) && !Arrays.asList(ConstantUtils.TYPES).contains(type.toUpperCase())) {
				response.setStatusMessage("Invalid type [" + type + "], Options include "
						+ Arrays.toString(ConstantUtils.TYPES));
				return response;
			}
			
			logger.info("[+] Attempting to parse the pagination variables");
			int page = Math.max(0, offset - 1);
			int size = Math.max(1, limit);
			Pageable pageable = PageRequest.of(page, size, Sort.by("requestId").descending());
			
			Result<ModuleRequest> moduleRequest = moduleRequestService.getApprovalRequestSearch(query, status, type, user.get().getId(), pageable);
			logger.info("getApprovalRequestSearch returned {}", moduleRequest);
			if (moduleRequest != null) {
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(moduleRequest.getList());
				response.setCount(moduleRequest.getNoOfRecords());
				return response;
			}
			response.setStatusMessage("Stockbroker Approval request not found");
			return response;
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting getStockBrokerApprovalRequestById {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
			return response;
		}
	}
	
	@GetMapping(value = "/requests/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_APPROVAL_REQUEST"})
	public StockBrokerBaseResponse<List<ModuleRequest>> getStockBrokerApprovalRequestsByDateCreated(@RequestParam("date")String date,
			@RequestParam("status") String status, @RequestParam("offset") int offset, @RequestParam("limit") int limit,
			@RequestParam("type") String type, HttpServletRequest request) {
		logger.info(
				"[+] In getStockBrokerApprovalRequestsByDateCreated with date: {}, offset: {}, limit: {}, type: {}",
				date, offset, limit);
		StockBrokerBaseResponse<List<ModuleRequest>> response = new StockBrokerBaseResponse<>();
		response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
		response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
		
		Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
		
		try {
			
			status = Utils.capitalizeFirstLetter(status);
			if (!Arrays.asList(ConstantUtils.APPROVAL_STATUS).contains(status)) {
				response.setStatusMessage("Invalid status [" + status + "], Options include "
						+ Arrays.toString(ConstantUtils.APPROVAL_STATUS));
				return response;
			}
			if(!Utils.isEmptyString(type) && !Arrays.asList(ConstantUtils.TYPES).contains(type.toUpperCase())) {
				response.setStatusMessage("Invalid type [" + type + "], Options include "
						+ Arrays.toString(ConstantUtils.TYPES));
				return response;
			}
			if (Utils.isEmptyString(date)) {
				response.setStatusMessage("Date is required");
				return response;
			}
			if (!Utils.isValidDate(ConstantUtils.FILTER_DATE_FORMAT, date)) {
				response.setStatusMessage("Date [" + date + "] is invalid, please use " + ConstantUtils.FILTER_DATE_FORMAT);
				return response;
			}
			Date dateObject = Utils.getDate(ConstantUtils.FILTER_DATE_FORMAT, date);
			if (dateObject == null) {
				response.setStatusMessage("start date or end date parsed is null");
				return response;
			}
			
			logger.info("[+] Attempting to parse the pagination variables");
			int page = Math.max(0, offset - 1);
			int size = Math.max(1, limit);
			Pageable pageable = PageRequest.of(page, size, Sort.by("requestId").descending());

			Result<ModuleRequest> moduleRequest = moduleRequestService
					.getStockBrokerApprovalNotificationByDateCreatedAndStatusAndType(dateObject, status, type, user.get().getId(), pageable);
			logger.info("getApprovalRequestById returned {}", moduleRequest);
			if (moduleRequest != null) {
				response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
				response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
				response.setData(moduleRequest.getList());
				response.setCount(moduleRequest.getNoOfRecords());
				return response;
			}
			response.setStatusMessage("Stockbroker Approval request not found");
			return response;
		} catch (Exception e) {
			logger.info("[-] Exception happened while getting getStockBrokerApprovalRequestById {}", e.getMessage());
			response.setStatusMessage("Error Processing Request");
			return response;
		}
	}
}
