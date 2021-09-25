package com.ap.greenpole.stockbroker.holderModule.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;

@Repository
public interface BondholderRepository extends JpaRepository<Bondholder, Long> {

	List<Bondholder> findBondholderByStockBroker(Long stockBroker);

	@Query(value = "SELECT * FROM bondholder WHERE :element = :value AND stock_broker = :stockbroker", nativeQuery = true)
	List<Bondholder> searchBondholderByElement(@Param("element") String element, @Param("value") String value,
			@Param("stockbroker") Long stockbroker);
}
