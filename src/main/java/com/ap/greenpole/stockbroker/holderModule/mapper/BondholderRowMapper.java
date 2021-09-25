package com.ap.greenpole.stockbroker.holderModule.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ap.greenpole.stockbroker.holderModule.Entity.Bondholder;

public class BondholderRowMapper implements RowMapper<Bondholder> {

    @Override
    public Bondholder mapRow(ResultSet rs, int arg1) throws SQLException {
        Bondholder bondholder = new Bondholder();
        bondholder.setBondholder_id(rs.getLong("bondholder_id"));
        bondholder.setLastName(rs.getString("last_name"));
        bondholder.setFirstName(rs.getString("first_name"));
        bondholder.setMiddleName(rs.getString("middle_name"));
        bondholder.setDob(rs.getString("dob"));
        bondholder.setPhone(rs.getString("phone"));
        bondholder.setOccupation(rs.getString("occupation"));
        bondholder.setEmail(rs.getString("email"));
        bondholder.setGender(rs.getString("gender"));
        bondholder.setMaritalStatus(rs.getString("marital_status"));
        bondholder.setAddress(rs.getString("address"));
        bondholder.setCountry(rs.getString("country"));
        bondholder.setCity(rs.getString("city"));
        bondholder.setPostalCode(rs.getString("postal_code"));
        bondholder.setStateOfOrigin(rs.getString("state_of_origin"));
        bondholder.setLgaOfOrigin(rs.getString("lga_of_origin"));
        bondholder.setMarriageCertificateNumber(rs.getString("marriage_certificate_number"));
        bondholder.setKinName(rs.getString("kin_name"));
        bondholder.setRelationship(rs.getString("relationship"));
        bondholder.setKinEmail(rs.getString("kin_email"));
        bondholder.setKinPhone(rs.getString("kin_phone"));
        bondholder.setKinAddress(rs.getString("kin_address"));
        bondholder.setKinCountry(rs.getString("kin_country"));
        bondholder.setKinState(rs.getString("kin_state"));
        bondholder.setKinLga(rs.getString("kin_lga"));
        bondholder.setBondholderType(rs.getString("bondholder_type"));
        bondholder.setStockBroker(rs.getInt("stock_broker"));
        bondholder.setNuban(rs.getString("nuban"));
        bondholder.setBankName(rs.getString("bank_name"));
        bondholder.setBankAccount(rs.getString("bank_account"));
        bondholder.setBvn(rs.getString("bvn"));
        bondholder.setClearingHousingNumber(rs.getString("clearing_housing_number"));
        bondholder.setTaxExemption(rs.getBoolean("tax_exemption"));
        bondholder.setEsopStatus(rs.getString("esop_status"));
        bondholder.setBondUnit(rs.getInt("bond_unit"));
        bondholder.setClientCompany(rs.getLong("client_company"));
        bondholder.setCreatedOn(rs.getDate("created_on"));
        return bondholder;
    }
}



