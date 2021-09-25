package com.ap.greenpole.stockbroker.holderModule.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;

@Repository
public interface ShareholderRepository extends JpaRepository<Shareholder, Long> {

	List<Shareholder> findShareholderByStockBroker(Long stockBroker);
	
	@Query(value = "SELECT * FROM shareholder WHERE :element = :value AND stock_broker = :stockbroker", nativeQuery = true)
	List<Shareholder> searchShareholderByElement(@Param("element") String element, @Param("value") String value,
			@Param("stockbroker") Long stockbroker);
}
