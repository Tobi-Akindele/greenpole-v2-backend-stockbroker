package com.ap.greenpole.stockbroker.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ap.greenpole.stockbroker.dto.Result;
import com.ap.greenpole.stockbroker.dto.StockBrokerAPIResponseCode;
import com.ap.greenpole.stockbroker.dto.StockBrokerBaseResponse;
import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;
import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;
import com.ap.greenpole.stockbroker.holderModule.dto.SearchBondholder;
import com.ap.greenpole.stockbroker.holderModule.dto.SearchShareholder;
import com.ap.greenpole.stockbroker.holderModule.service.BondholderService;
import com.ap.greenpole.stockbroker.holderModule.service.ShareholderService;
import com.ap.greenpole.stockbroker.model.ModuleRequest;
import com.ap.greenpole.stockbroker.model.StockBroker;
import com.ap.greenpole.stockbroker.service.ModuleRequestService;
import com.ap.greenpole.stockbroker.service.StockBrokerService;
import com.ap.greenpole.stockbroker.utils.BeanUtils;
import com.ap.greenpole.stockbroker.utils.ConstantUtils;
import com.ap.greenpole.stockbroker.utils.Utils;
import com.ap.greenpole.usermodule.annotation.PreAuthorizePermission;
import com.ap.greenpole.usermodule.model.User;
import com.ap.greenpole.usermodule.service.UserService;
import com.google.gson.Gson;

/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:31 AM
 */

@RestController
@RequestMapping("/api/v1/stockbroker")
public class StockBrokerController {

    private static Logger logger = LoggerFactory.getLogger(StockBrokerController.class);

    @Autowired
    private StockBrokerService stockBrokerService;

    @Autowired
    private ModuleRequestService moduleRequestService;
    
    @Autowired
    private ShareholderService shareholderService;
    
    @Autowired
    private BondholderService bondholderService;
    
    @Autowired
    private UserService userService;

    @PostMapping(value = "/create", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_CREATE_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<Map<String, Long>> createStockBroker(@RequestBody @Validated StockBroker stockBroker, HttpServletRequest request) {
        
    	StockBrokerBaseResponse<Map<String, Long>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
    	
    	Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
    	
    	logger.info("[+] In createStockBroker with payload : {}", new Gson().toJson(stockBroker));
        try {
            if (stockBroker == null) {
                response.setStatusMessage("Request body is missing");
                return response;
            }
            if (Utils.isEmptyString(stockBroker.getStockBrokerName())) {
                response.setStatusMessage("Stockbroker name is required");
                return response;
            }
            if (Utils.isEmptyString(stockBroker.getCscsAccountNumber())) {
                response.setStatusMessage("Stockbroker CSCS Acount number is required");
                return response;
            }
            if (stockBroker.getEmailAddresses() == null || stockBroker.getEmailAddresses().isEmpty()) {
                response.setStatusMessage("At least one Email address is required");
                return response;
            }
            if (stockBroker.getPhones() == null || stockBroker.getPhones().isEmpty()) {
                response.setStatusMessage("At least one Phone number is required");
                return response;
            }
            
            if(!Utils.isValidListValues(stockBroker.getEmailAddresses())) {
            	response.setStatusMessage("Please ensure all email address(s) are not null");
                return response;
            }
            
            if(!Utils.isValidListValues(stockBroker.getPhones())) {
            	response.setStatusMessage("Please ensure all Phone numbers are not null");
                return response;
            }
            
            if (!Utils.isValidEmails(stockBroker.getEmailAddresses())) {
                response.setStatusMessage("Please ensure all email address(s) are valid");
                return response;
            }
            //validate if its duplicate CSCS number
            StockBroker stockBrokerInDb = stockBrokerService.getStockBrokerByCSCSAccountNumber(stockBroker.getCscsAccountNumber());
            logger.info("[+] getStockBrokerByCSCSAccountNumber in stockBrokerService returned to createStockBroker {}", new Gson().toJson(stockBrokerInDb));
            if (stockBrokerInDb != null) {
                response.setStatusMessage("Stockbroker CSCS account number with " + stockBroker.getCscsAccountNumber() + " "
                        + "already exists");
                return response;
            }
            List<String> dataOwnerEmails = stockBroker.getEmailAddresses();
            List<String> dataOwnerPhones = stockBroker.getPhones();
            stockBroker.setEmails(StringUtils.join(stockBroker.getEmailAddresses(), ','));
            stockBroker.setPhoneNumbers(StringUtils.join(stockBroker.getPhones(), ','));

            String[] fieldNames = {"emailAddresses", "phones"};
            BeanUtils<StockBroker> setProps = new BeanUtils<>();
            setProps.setPropertiesNull(stockBroker, Arrays.asList(fieldNames));

            Gson gson = new Gson();
            ModuleRequest moduleRequest = new ModuleRequest(null, gson.toJson(stockBroker));
			moduleRequest.setActionRequired(ConstantUtils.REQUEST_TYPES[0]);
			Long requestId = moduleRequestService.createApprovalRequest(moduleRequest, dataOwnerEmails,
					stockBroker.getStockBrokerName(), dataOwnerPhones, user.get());
			logger.info(" createApprovalRequest in stockBrokerApprovalRequestService returned ID: {} Created", requestId);
            if (requestId > 0) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                Map<String, Long> approvalRequestIdMap = new HashMap<>();
                approvalRequestIdMap.put("id", requestId);
                response.setData(approvalRequestIdMap);
                return response;
            }
        } catch (Exception e) {
            logger.info("[-] Exception happened while creating StockBroker {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @GetMapping(value = "/details/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<StockBroker> getStockBrokerById(@PathVariable long id) {
        logger.info("[+] In getStockBrokerById with ID : {}", id);
        StockBrokerBaseResponse<StockBroker> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        try {
            if (id <= 0) {
                response.setStatusMessage("Stockbroker account ID is required");
                return response;
            }
            StockBroker stockBroker = stockBrokerService.getStockBrokerById(id);
            logger.info(" getStockBrokerById in stockBrokerService returned: {}", new Gson().toJson(stockBroker));
            if (stockBroker != null) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                response.setData(stockBroker);
                return response;
            }
            response.setStatusMessage("Stockbroker account not found or has been deactivated");
        } catch (Exception e) {
            logger.info("[-] Exception happened while getting StockBrokerById {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<List<StockBroker>> getAllStockBrokerAccount() {
        logger.info("[+] In getAllStockBrokerAccount");
        StockBrokerBaseResponse<List<StockBroker>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        List<StockBroker> allStockbrokers = null;
        try {
            allStockbrokers = stockBrokerService.getAllStockBrokers();
            if (allStockbrokers != null && !allStockbrokers.isEmpty()) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                response.setData(allStockbrokers);
                return response;
            }
            response.setStatusMessage("No result found");
        } catch (Exception e) {
            logger.info("[-] Exception happened while getting allStockBrokerAccount {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @GetMapping(value = "/all/paginated", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<List<StockBroker>> getAllStockBrokerAccountPaginated(
            @RequestHeader(value = "pageSize", required = false, defaultValue = "" + Integer.MAX_VALUE) String pageSize,
            @RequestHeader(value = "pageNumber", required = false, defaultValue = "1") String pageNumber) {
        logger.info("[+] In getAllStockBrokerAccountPaginated with pageSize: {},  pageNumber : {}", pageSize, pageNumber);
        StockBrokerBaseResponse<List<StockBroker>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        try {
            logger.info("[+] Attempting to parse the pagination variables");
            int page = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);
            page = Math.max(0, page - 1);
            size = Math.max(1, size);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Result<StockBroker> allStockBrokers = stockBrokerService
                    .getAllStockBrokers(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable);
            response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
            response.setData(allStockBrokers.getList());
            response.setCount(allStockBrokers.getNoOfRecords());
            return response;
        } catch (NumberFormatException e) {
            logger.error("[-] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
        } catch (Exception e) {
            logger.info("[-] Exception happened while getting allStockBrokerAccountPaginated {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @PutMapping(value = "/update/{stockBrokerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_UPDATE_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<Map<String, Long>> updateStockBroker(@RequestBody @Validated StockBroker stockBroker, @PathVariable Long stockBrokerId,
    		HttpServletRequest request) {
        
        StockBrokerBaseResponse<Map<String, Long>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
        
    	logger.info("[+] Inside updateStockBroker with payload: {}", new Gson().toJson(stockBroker));
        try {
            if (stockBrokerId == null || stockBrokerId <= 0) {
                response.setStatusMessage("Stocker broker ID is required");
                return response;
            }
            if (stockBroker == null) {
                response.setStatusMessage("Request body is missing");
                return response;
            }
            StockBroker stockBrokerInDb = stockBrokerService.getStockBrokerById(stockBrokerId);
            logger.info(" getStockBrokerById in stockBrokerService returned: {}", new Gson().toJson(stockBrokerInDb));
            if (stockBrokerInDb == null) {
                response.setStatusMessage("Stockbroker account does not exist or has been deactivated");
                return response;
            }
            if (ConstantUtils.VALIDATION_STATUS[1].equalsIgnoreCase(stockBrokerInDb.getValidationState())) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.VALIDATION_STATUS[1]);
                return response;
            }
            if (ConstantUtils.SUSPENSION_STATUS[0].equalsIgnoreCase(stockBrokerInDb.getSuspensionState())) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.SUSPENSION_STATUS[0]);
                return response;
            }
            ModuleRequest moduleRequest = Utils.checkForOngoingApproval(moduleRequestService,
                    stockBrokerId, ConstantUtils.REQUEST_TYPES[1]);
            logger.info(" checkForOngoingApproval in Utils returned: {}", moduleRequest);
            if (Utils.isEmptyString(stockBroker.getStockBrokerName())) {
                response.setStatusMessage("Stockbroker name is required");
                return response;
            }
            if (stockBroker.getEmailAddresses() == null || stockBroker.getEmailAddresses().isEmpty()) {
                response.setStatusMessage("At least one Email address is required");
                return response;
            }
            if (stockBroker.getPhones() == null || stockBroker.getPhones().isEmpty()) {
                response.setStatusMessage("At least one Phone number is required");
                return response;
            }
            
            if(!Utils.isValidListValues(stockBroker.getEmailAddresses())) {
            	response.setStatusMessage("Please ensure all email address(s) are not null");
                return response;
            }
            
            if(!Utils.isValidListValues(stockBroker.getPhones())) {
            	response.setStatusMessage("Please ensure all Phone numbers are not null");
                return response;
            }
            
            if (!Utils.isValidEmails(stockBroker.getEmailAddresses())) {
                response.setStatusMessage("Please ensure all email address(s) are valid");
                return response;
            }

            List<String> dataOwnerEmails = stockBroker.getEmailAddresses();
            List<String> dataOwnerPhones = stockBroker.getPhones();
            stockBroker.setEmails(StringUtils.join(stockBroker.getEmailAddresses(), ','));
            stockBroker.setPhoneNumbers(StringUtils.join(stockBroker.getPhones(), ','));

            String[] fieldNames = {"id", "emailAddresses", "phones", "cscsAccountNumber", "validationState", "suspensionState", "shareholders", "bondholders"};
            BeanUtils<StockBroker> setProps = new BeanUtils<>();
            setProps.setPropertiesNull(stockBroker, Arrays.asList(fieldNames));

            Gson gson = new Gson();
            stockBroker.setActive(stockBrokerInDb.getActive());
            moduleRequest.setOldRecord(gson.toJson(stockBrokerInDb));
			moduleRequest.setNewRecord(gson.toJson(stockBroker));
			Long approvalRequestId = moduleRequestService.createApprovalRequest(moduleRequest, dataOwnerEmails,
					stockBrokerInDb.getStockBrokerName(), dataOwnerPhones, user.get());
			logger.info(" createApprovalRequest in stockBrokerApprovalRequestService returned ID: {} Created", approvalRequestId);
            if (approvalRequestId > 0) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                Map<String, Long> approvalRequestIdMap = new HashMap<>();
                approvalRequestIdMap.put("id", approvalRequestId);
                response.setData(approvalRequestIdMap);
                return response;
            }
        } catch (Exception e) {
            logger.info("[-] Exception happened while creating StockBroker {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/deactivate/{stockBrokerId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_DELETE_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<Map<String, Long>> deactivateStockBroker(@PathVariable Long stockBrokerId, HttpServletRequest request) {
        
        StockBrokerBaseResponse<Map<String, Long>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
    	
    	logger.info("[+] Inside deactivateStockBroker with ID: {}", stockBrokerId);
        try {
            if (stockBrokerId == null || stockBrokerId <= 0) {
                response.setStatusMessage("Stocker broker ID is required");
                return response;
            }
            StockBroker stockBrokerInDb = stockBrokerService.getStockBrokerById(stockBrokerId);
            logger.info("[+] getStockBrokerById in stockBrokerService returned to deactivateStockBroker {}", new Gson().toJson(stockBrokerInDb));
            if (stockBrokerInDb == null) {
                response.setStatusMessage("Stockbroker account does not exist or has been deactivated");
                return response;
            }
            ModuleRequest moduleRequest = Utils.checkForOngoingApproval(moduleRequestService,
                    stockBrokerId, ConstantUtils.REQUEST_TYPES[2]);
            logger.info(" checkForOngoingApproval in Utils returned: {}", moduleRequest);

            Gson gson = new Gson();
			moduleRequest.setOldRecord(gson.toJson(stockBrokerInDb));
			Long approvalRequestId = moduleRequestService.createApprovalRequest(moduleRequest,
					Utils.commaSeperatedToList(stockBrokerInDb.getEmails()), stockBrokerInDb.getStockBrokerName(),
					Utils.commaSeperatedToList(stockBrokerInDb.getPhoneNumbers()), user.get());
			logger.info(" createApprovalRequest in stockBrokerApprovalRequestService returned ID: {} Created", approvalRequestId);
            if (approvalRequestId > 0) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                Map<String, Long> approvalRequestIdMap = new HashMap<>();
                approvalRequestIdMap.put("id", approvalRequestId);
                response.setData(approvalRequestIdMap);
                return response;
            }
        } catch (Exception e) {
            logger.info("[-] Exception happened while deactivating StockBroker {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/search",produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<List<StockBroker>> searchStockBrokerAccountPaginated(
            @RequestHeader(value = "pageSize", required = false, defaultValue = "" + Integer.MAX_VALUE) String pageSize,
            @RequestHeader(value = "pageNumber", required = false, defaultValue = "1") String pageNumber,
            @RequestBody @Validated StockBroker stockBroker) {
        logger.info("[+] In searchStockBrokerAccountPaginated with pageSize: {},  pageNumber : {}", pageSize, pageNumber);
        StockBrokerBaseResponse<List<StockBroker>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        try {
            if (stockBroker == null) {
                response.setStatusMessage("Request body is missing");
                return response;
            }
            if(stockBroker.getShLowerRange() < 0 || stockBroker.getShUpperRange() < 0) {
            	response.setStatusMessage("Shareholder lower range or upper range is invalid");
                return response;
            }
            if(stockBroker.getBhLowerRange() < 0 || stockBroker.getBhUpperRange() < 0) {
            	response.setStatusMessage("Shareholder lower range is required");
                return response;
            }
            logger.info("[+] Attempting to parse the pagination variables");
            int page = Integer.parseInt(pageNumber);
            int size = Integer.parseInt(pageSize);
            page = Math.max(0, page - 1);
            size = Math.max(1, size);
            stockBroker.setActive(true);
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Result<StockBroker> stockBrokerResult = stockBrokerService
                    .searchStockBrokerWithSpecification(stockBroker, Integer.parseInt(pageNumber), Integer.parseInt(pageSize), pageable);
            response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
            response.setData(stockBrokerResult.getList());
            response.setCount(stockBrokerResult.getNoOfRecords());
            return response;
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
        } catch (Exception e) {
            logger.info("[-] Exception happened while searching StockBrokerAccountPaginated {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/validation", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_STOCKBROKER_ACCOUNT_VALIDATION"})
    public StockBrokerBaseResponse<Map<String, Long>> validateStockBroker(@RequestHeader(value = "stockBrokerId", required = false) Long stockBrokerId,
                                                                          @RequestHeader(value = "validationOption", required = false) String validationOption,
                                                                          HttpServletRequest request) {
        StockBrokerBaseResponse<Map<String, Long>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
        
    	logger.info("[+] In validateStockBroker with stockBrokerId: {},  validationOption: {}", stockBrokerId, validationOption);
        try {
            if (stockBrokerId == null || stockBrokerId <= 0) {
                response.setStatusMessage("Stocker broker ID is required");
                return response;
            }
            if (!Arrays.asList(ConstantUtils.VALIDATION_ACTIONS).contains(validationOption)) {
                response.setStatusMessage("Validation option not valid, options include " + Arrays.toString(ConstantUtils.VALIDATION_ACTIONS));
                return response;
            }
            StockBroker stockBrokerInDb = stockBrokerService.getStockBrokerById(stockBrokerId);
            logger.info("[+] getStockBrokerById in stockBrokerService returned to validateStockBroker {}", stockBrokerInDb);
            if (stockBrokerInDb == null) {
                response.setStatusMessage("Stockbroker account does not exist or has been deactivated");
                return response;
            }

            //Check if there're shareholders linked with this Stockbroker account
            List<Shareholder> shareHolders = shareholderService.findShareHoldersByStockBroker(stockBrokerId);
            if(shareHolders != null && !shareHolders.isEmpty()) {
            	response.setStatusMessage("This Stockbroker has Shareholders linked to it, request denied!");
            	return response;
            }

            if (ConstantUtils.VALIDATION_STATUS[0].equalsIgnoreCase(stockBrokerInDb.getValidationState())
                    && ConstantUtils.VALIDATION_ACTIONS[0].equalsIgnoreCase(validationOption)) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.VALIDATION_STATUS[0]);
                return response;
            }
            if (ConstantUtils.VALIDATION_STATUS[1].equalsIgnoreCase(stockBrokerInDb.getValidationState())
                    && ConstantUtils.VALIDATION_ACTIONS[1].equalsIgnoreCase(validationOption)) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.VALIDATION_STATUS[1]);
                return response;
            }

            ModuleRequest moduleRequest = Utils.checkForOngoingApproval(moduleRequestService, stockBrokerId, ConstantUtils.REQUEST_TYPES[3]);
            logger.info(" checkForOngoingApproval in Utils returned: {}", moduleRequest);
            validationOption = ConstantUtils.VALIDATION_ACTIONS[0].equalsIgnoreCase(validationOption)
                    ? ConstantUtils.VALIDATION_STATUS[0]
                    : ConstantUtils.VALIDATION_STATUS[1];
            StockBroker newData = new StockBroker();
            newData.setValidationState(validationOption);
            newData.setActive(stockBrokerInDb.getActive());
            Gson gson = new Gson();
			moduleRequest.setOldRecord(gson.toJson(stockBrokerInDb));
			moduleRequest.setNewRecord(gson.toJson(newData));
			Long approvalRequestId = moduleRequestService.createApprovalRequest(moduleRequest,
					Utils.commaSeperatedToList(stockBrokerInDb.getEmails()), stockBrokerInDb.getStockBrokerName(),
					Utils.commaSeperatedToList(stockBrokerInDb.getPhoneNumbers()), user.get());
            logger.info(" createApprovalRequest in stockBrokerApprovalRequestService returned ID: {} Created", approvalRequestId);
            if (approvalRequestId > 0) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                Map<String, Long> approvalRequestIdMap = new HashMap<>();
                approvalRequestIdMap.put("id", approvalRequestId);
                response.setData(approvalRequestIdMap);
                return response;
            }
        } catch (Exception e) {
            logger.info("[-] Exception happened while validating StockBroker {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }

    @PostMapping(value = "/suspension", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_STOCKBROKER_ACCOUNT_SUSPENSION"})
    public StockBrokerBaseResponse<Map<String, Long>> suspendStockBroker(@RequestHeader(value = "stockBrokerId", required = false) Long stockBrokerId,
                                                                         @RequestHeader(value = "suspensionOption", required = false) String suspensionOption,
                                                                         HttpServletRequest request) {
        StockBrokerBaseResponse<Map<String, Long>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        Optional<User> user = userService.memberFromAuthorization(request.getHeader(ConstantUtils.AUTHORIZATION));
    	if(!user.isPresent()) {
    		response.setStatusMessage("User details not found");
    		return response;
    	}
        
    	logger.info("[+] In validateStockBroker with stockBrokerId: {},  suspensionOption: {}", stockBrokerId, suspensionOption);
        try {
            if (stockBrokerId == null || stockBrokerId <= 0) {
                response.setStatusMessage("Stockbroker ID is required");
                return response;
            }
            if (!Arrays.asList(ConstantUtils.SUSPENSION_ACTIONS).contains(suspensionOption)) {
                response.setStatusMessage("Suspension option not valid, options include " + Arrays.toString(ConstantUtils.SUSPENSION_ACTIONS));
                return response;
            }
            StockBroker stockBrokerInDb = stockBrokerService.getStockBrokerById(stockBrokerId);
            logger.info("[+] getStockBrokerById in stockBrokerService returned to validateStockBroker {}", new Gson().toJson(stockBrokerInDb));
            if (stockBrokerInDb == null) {
                response.setStatusMessage("Stockbroker account does not exist or has been deactivated");
                return response;
            }
            if (ConstantUtils.SUSPENSION_STATUS[0].equalsIgnoreCase(stockBrokerInDb.getSuspensionState())
                    && ConstantUtils.SUSPENSION_ACTIONS[0].equalsIgnoreCase(suspensionOption)) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.SUSPENSION_STATUS[0]);
                return response;
            }
            if (ConstantUtils.SUSPENSION_STATUS[1].equalsIgnoreCase(stockBrokerInDb.getValidationState())
                    && ConstantUtils.SUSPENSION_ACTIONS[1].equalsIgnoreCase(suspensionOption)) {
                response.setStatusMessage("Stockbroker account has been " + ConstantUtils.SUSPENSION_STATUS[1]);
                return response;
            }
            ModuleRequest moduleRequest = Utils.checkForOngoingApproval(moduleRequestService, stockBrokerId, ConstantUtils.REQUEST_TYPES[4]);
            logger.info(" checkForOngoingApproval in Utils returned: {}", moduleRequest);

            suspensionOption = ConstantUtils.SUSPENSION_ACTIONS[0].equalsIgnoreCase(suspensionOption)
                    ? ConstantUtils.SUSPENSION_STATUS[0]
                    : ConstantUtils.SUSPENSION_STATUS[1];
            StockBroker newData = new StockBroker();
            newData.setSuspensionState(suspensionOption);
            newData.setActive(stockBrokerInDb.getActive());
            Gson gson = new Gson();
            moduleRequest.setOldRecord(gson.toJson(stockBrokerInDb));
			moduleRequest.setNewRecord(gson.toJson(newData));
			Long approvalRequestId = moduleRequestService.createApprovalRequest(moduleRequest,
					Utils.commaSeperatedToList(stockBrokerInDb.getEmails()), stockBrokerInDb.getStockBrokerName(),
					Utils.commaSeperatedToList(stockBrokerInDb.getPhoneNumbers()), user.get());
			logger.info(" createApprovalRequest in stockBrokerApprovalRequestService returned ID: {} Created",
					approvalRequestId);
			if (approvalRequestId > 0) {
                response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
                response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
                Map<String, Long> approvalRequestIdMap = new HashMap<>();
                approvalRequestIdMap.put("id", approvalRequestId);
                response.setData(approvalRequestIdMap);
                return response;
            }
        } catch (Exception e) {
            logger.info("[-] Exception happened while suspending StockBroker {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }
    
    @PostMapping(value = "/search/shareholders", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<List<Shareholder>> searchStockBrokerShareholderList(
           @RequestHeader(value = "stockBrokerId", required = false) long stockBrokerId,
           @RequestBody @Validated SearchShareholder searchShareholder) {

    	StockBrokerBaseResponse<List<Shareholder>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        try {
        	if(stockBrokerId <= 0) {
        		response.setStatusMessage("Stockbroker account ID is required");
                return response;
        	}
        	
        	StockBroker stockBrokerById = stockBrokerService.getStockBrokerById(stockBrokerId);
        	if(stockBrokerById == null) {
        		response.setStatusMessage("Stockbroker account does not exist");
                return response;
        	}
        	
            if (searchShareholder == null) {
                response.setStatusMessage("Request body is missing");
                return response;
            }
            searchShareholder.setStockBroker(stockBrokerId);
			List<Shareholder> queryResult = shareholderService.searchShareholderByElement(searchShareholder);

			response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
            response.setData(queryResult);
            return response;
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
        } catch (Exception e) {
            logger.info("[-] Exception happened while searching Shareholder list {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }
    
    @PostMapping(value = "/search/bondholders", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorizePermission({"PERMISSION_GET_STOCKBROKER_ACCOUNT"})
    public StockBrokerBaseResponse<List<Bondholder>> searchStockBrokerBondholderList(
           @RequestHeader(value = "stockBrokerId", required = false) long stockBrokerId,
           @RequestBody @Validated SearchBondholder searchBondholder) {

    	StockBrokerBaseResponse<List<Bondholder>> response = new StockBrokerBaseResponse<>();
        response.setStatus(StockBrokerAPIResponseCode.FAILED.getStatus());
        response.setStatusMessage(StockBrokerAPIResponseCode.FAILED.name());
        try {
        	if(stockBrokerId <= 0) {
        		response.setStatusMessage("Stockbroker account ID is required");
                return response;
        	}
        	
        	StockBroker stockBrokerById = stockBrokerService.getStockBrokerById(stockBrokerId);
        	if(stockBrokerById == null) {
        		response.setStatusMessage("Stockbroker account does not exist");
                return response;
        	}
        	
            if (searchBondholder == null) {
                response.setStatusMessage("Request body is missing");
                return response;
            }
            searchBondholder.setStockBroker(stockBrokerId);
			List<Bondholder> queryResult = bondholderService.searchBondholderByElement(searchBondholder);

			response.setStatus(StockBrokerAPIResponseCode.SUCCESSFUL.getStatus());
            response.setStatusMessage(StockBrokerAPIResponseCode.SUCCESSFUL.name());
            response.setData(queryResult);
            return response;
        } catch (NumberFormatException e) {
            logger.error("[+] Error {} occurred while parsing page variable with message: {}",
                    e.getClass().getSimpleName(), e.getMessage());
            response.setStatusMessage("The entered page and size must be integer values.");
        } catch (Exception e) {
            logger.info("[-] Exception happened while searching Shareholder list {}", e.getMessage());
            response.setStatusMessage("Error Processing Request: " + e.getMessage());
        }
        return response;
    }
}
