package com.ap.greenpole.stockbroker.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.greenpole.stockbroker.model.StockBroker;

@Repository
public interface StockBrokerRepository extends JpaRepository<StockBroker, Long>, JpaSpecificationExecutor<StockBroker>{
	
	
	Page<StockBroker> findByActiveTrue(Pageable pageable);
	StockBroker findByCscsAccountNumberAndActiveTrue(@Param("cscs_account_number") String cscsAcountNumber);
	List<StockBroker> findByActiveTrue();
	StockBroker findByIdAndActiveTrue(Long id);
}
