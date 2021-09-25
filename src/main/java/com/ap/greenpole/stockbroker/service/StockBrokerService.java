package com.ap.greenpole.stockbroker.service;

import java.util.List;

import org.springframework.data.domain.Pageable;

import com.ap.greenpole.stockbroker.dto.Result;
import com.ap.greenpole.stockbroker.model.StockBroker;

/**
 * Created By: Oyindamola Akindele
 * Date: 8/11/2020 2:33 AM
 */

public interface StockBrokerService {

	void createStockBroker(StockBroker stockBroker);
	List<StockBroker> getAllStockBrokers();
	Result<StockBroker> getAllStockBrokers(int pageNumber, int pageSize, Pageable pageable);
	StockBroker getStockBrokerById(Long id);
	StockBroker getStockBrokerByCSCSAccountNumber(String cSCSAccountNumber);
	void updateStockBroker(StockBroker stockBroker);
	List<StockBroker> searchStockBroker(StockBroker stockBroker);
	Result<StockBroker>searchStockBrokerWithSpecification(StockBroker stockBroker, int pageNumber, int pageSize, Pageable pageable);
}
