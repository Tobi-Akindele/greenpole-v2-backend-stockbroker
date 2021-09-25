package com.ap.greenpole.stockbroker.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ap.greenpole.stockbroker.model.ModuleRequest;

@Repository
public interface ModuleRequestRepository extends JpaRepository<ModuleRequest, Long> {

	public List<ModuleRequest> findAllModuleRequestByModules(String modules);

	public Page<ModuleRequest> findAllModuleRequestByModules(String modules, Pageable pageable);

	public ModuleRequest findModuleRequestByRequestIdAndModules(Long requestId, String modules);

	public ModuleRequest findFirstModuleRequestByResourceIdAndActionRequiredAndModulesOrderByRequestIdDesc(
			Long resourceId, String actionRequired, String modules);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatus(String module, int status, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnBetween(String module, Date startDate,
			Date endDate, Pageable pageable);

	public List<ModuleRequest> findAllModuleRequestByModulesAndStatusAndApproverId(String module, int status,
			long approverId);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndOldRecordContainingOrNewRecordContaining(String module,
			String oldRecord, String newRecord, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatusAndOldRecordContainingOrNewRecordContaining(
			String module, int status, String oldRecord, String newRecord, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnEquals(String module, Date createdOn,
			Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnAndStatus(String module, Date createdOn,
			int status, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnBetweenAndStatus(String module, Date startDate,
			Date endDate, int status, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatusAndActionRequired(
			String module, int status, String actionRequired, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatusAndActionRequiredAndOldRecordContainingOrNewRecordContaining(
			String module, int status, String actionRequired, String oldRecord, String newRecord, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnBetweenAndStatusAndActionRequired(String module,
			Date startDate, Date endDate, int status, String actionRequired, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatusAndRequesterId(String module, int status,
			Long requesterId, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndStatusAndRequesterIdAndOldRecordContainingOrNewRecordContaining(
			String module, int status, Long userId, String oldRecord, String newRecord, Pageable pageable);

	public Page<ModuleRequest> findAllModuleRequestByModulesAndCreatedOnBetweenAndStatusAndRequesterId(String module,
			Date dateObject, Date date, int status, Long userId, Pageable pageable);
}
