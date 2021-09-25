package com.ap.greenpole.stockbroker.holderModule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;
import com.ap.greenpole.stockbroker.holderModule.dao.BondholderRepository;
import com.ap.greenpole.stockbroker.holderModule.dto.SearchBondholder;

@Service
public class BondholderService {

	@Autowired
	private BondholderRepository bondholderRepository;

	public List<Bondholder> findBondholderByStockBroker(long stockBroker) {
		return bondholderRepository.findBondholderByStockBroker(stockBroker);
	}

	public List<Bondholder> searchBondholderByElement(SearchBondholder searchBondholder) {
		return bondholderRepository.searchBondholderByElement(searchBondholder.getElement(),
				searchBondholder.getValue(), searchBondholder.getStockBroker());
	}
}
