package com.ap.greenpole.stockbroker.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ap.greenpole.stockbroker.dto.Result;
import com.ap.greenpole.stockbroker.dto.SearchCriteria;
import com.ap.greenpole.stockbroker.dto.SearchSpecification;
import com.ap.greenpole.stockbroker.dto.SubQueryAttributes;
import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;
import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;
import com.ap.greenpole.stockbroker.holderModule.service.BondholderService;
import com.ap.greenpole.stockbroker.holderModule.service.ShareholderService;
import com.ap.greenpole.stockbroker.model.StockBroker;
import com.ap.greenpole.stockbroker.repository.StockBrokerRepository;
import com.ap.greenpole.stockbroker.service.StockBrokerService;
import com.ap.greenpole.stockbroker.utils.ConstantUtils;
import com.ap.greenpole.stockbroker.utils.Utils;


/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:32 AM
 */

@Service
public class StockBrokerServiceImpl implements StockBrokerService {
	
	private static Logger log = LoggerFactory.getLogger(StockBrokerServiceImpl.class);
	
	@Autowired
	private StockBrokerRepository stockBrokerRepository;
	
	@Autowired
	private ShareholderService shareholderService;
	
	@Autowired
	private BondholderService bondholderService;

	@Override
	public void createStockBroker(StockBroker stockBroker) {
		stockBroker.setActive(true);
		stockBroker.setValidationState(ConstantUtils.UNDEFINED);
		stockBroker.setSuspensionState(ConstantUtils.UNDEFINED);
		stockBroker.setDateCreated(new Date());
		log.info("Creating resource {} ", stockBroker);
		stockBrokerRepository.save(stockBroker);
		log.info("Created resource {} ", stockBroker);
	}

	@Override
	public List<StockBroker> getAllStockBrokers() {
		return stockBrokerRepository.findByActiveTrue();
	}

	@Override
	public Result<StockBroker> getAllStockBrokers(int pageNumber, int pageSize, Pageable pageable) {
		Page<StockBroker> allRecords = stockBrokerRepository.findByActiveTrue(pageable);
		long noOfRecords = allRecords.getTotalElements();
		return new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);
	}

	@Override
	public StockBroker getStockBrokerById(Long id) {
		StockBroker stockBroker = stockBrokerRepository.findByIdAndActiveTrue(id);
		if(stockBroker != null) {
			stockBroker.setEmailAddresses(Utils.commaSeperatedToList(stockBroker.getEmails()));
			stockBroker.setPhones(Utils.commaSeperatedToList(stockBroker.getPhoneNumbers()));
			
			stockBroker.setShareholders(shareholderService.findShareHoldersByStockBroker(stockBroker.getId()));
			
			stockBroker.setBondholders(bondholderService.findBondholderByStockBroker(stockBroker.getId()));
			
		}
		return stockBroker;
	}

	@Override
	public StockBroker getStockBrokerByCSCSAccountNumber(String cscsAccountNumber) {
		return stockBrokerRepository.findByCscsAccountNumberAndActiveTrue(cscsAccountNumber);
	}

	@Override
	public void updateStockBroker(StockBroker stockBroker) {
		stockBroker.setDateModified(new Date());
		log.info("Updating resource {} ", stockBroker);
		stockBrokerRepository.save(stockBroker);
		log.info("Updated resource {} ", stockBroker);
	}

	@Override
	public List<StockBroker> searchStockBroker(StockBroker stockBroker) {
		
		List<SearchCriteria> preparedCriterias = Utils.prepareSearchSpecification(stockBroker);
		SubQueryAttributes<Shareholder> shSubQueryAttributes = new SubQueryAttributes<>(
				Utils.prepareSubQuerySpecification(stockBroker, "shLowerRange", "shUpperRange"), new Shareholder(),
				"stockBroker", "shareholder_id");
		SubQueryAttributes<Bondholder> bhSubQueryAttributes = new SubQueryAttributes<>(
				Utils.prepareSubQuerySpecification(stockBroker, "bhLowerRange", "bhUpperRange"), new Bondholder(),
				"stockBroker", "bondholder_id");

		List<SubQueryAttributes<?>> subQueryAttList = new ArrayList<>();
		subQueryAttList.add(shSubQueryAttributes);
		subQueryAttList.add(bhSubQueryAttributes);

		SearchSpecification<StockBroker> searchSpecification = new SearchSpecification<>(preparedCriterias,
				true, true, subQueryAttList, "id");
		return stockBrokerRepository.findAll(searchSpecification);
	}
	
	@Override
	public Result<StockBroker> searchStockBrokerWithSpecification(StockBroker stockBroker, int pageNumber, int pageSize,
			Pageable pageable) {

		List<SearchCriteria> preparedCriterias = Utils.prepareSearchSpecification(stockBroker);
		SubQueryAttributes<Shareholder> shSubQueryAttributes = new SubQueryAttributes<>(
				Utils.prepareSubQuerySpecification(stockBroker, "shLowerRange", "shUpperRange"), new Shareholder(),
				"stockBroker", "shareholder_id");
		SubQueryAttributes<Bondholder> bhSubQueryAttributes = new SubQueryAttributes<>(
				Utils.prepareSubQuerySpecification(stockBroker, "bhLowerRange", "bhUpperRange"), new Bondholder(),
				"stockBroker", "bondholder_id");

		List<SubQueryAttributes<?>> subQueryAttList = new ArrayList<>();
		subQueryAttList.add(shSubQueryAttributes);
		subQueryAttList.add(bhSubQueryAttributes);

		SearchSpecification<StockBroker> searchSpecification = new SearchSpecification<>(preparedCriterias,
				true, true, subQueryAttList, "id");
		Page<StockBroker> allRecords = stockBrokerRepository.findAll(searchSpecification, pageable);
		if(allRecords != null && allRecords.getContent() != null && !allRecords.getContent().isEmpty()) {
			allRecords.getContent().forEach(sb -> {
				sb.setShareholders(shareholderService.findShareHoldersByStockBroker(sb.getId()));
				sb.setBondholders(bondholderService.findBondholderByStockBroker(sb.getId()));
			});
		}

		long noOfRecords = allRecords.getTotalElements();
		return new Result<>(0, allRecords.getContent(), noOfRecords, pageNumber, pageSize);
	}
}
