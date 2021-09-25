package com.ap.greenpole.stockbroker.holderModule.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;
import com.ap.greenpole.stockbroker.holderModule.dao.ShareholderRepository;
import com.ap.greenpole.stockbroker.holderModule.dto.SearchShareholder;

@Service
public class ShareholderService {
    
    @Autowired
    private ShareholderRepository shareholderRepository;
    
    public List<Shareholder> findShareHoldersByStockBroker(long stockBroker){
    	return shareholderRepository.findShareholderByStockBroker(stockBroker);
    }

    public List<Shareholder> searchShareholderByElement(SearchShareholder searchShareholder) {
        return shareholderRepository.searchShareholderByElement(searchShareholder.getElement(), searchShareholder.getValue(),
        		searchShareholder.getStockBroker());
    }
}
