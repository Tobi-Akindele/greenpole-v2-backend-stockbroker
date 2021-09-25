package com.ap.greenpole.stockbroker.holderModule.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.ap.greenpole.stockbroker.holderModule.Entity.Shareholder;

public class ShareholderRowMapper implements RowMapper<Shareholder> {
    @Override
    public Shareholder mapRow(ResultSet rs, int arg1) throws SQLException {
        Shareholder shareholder = new Shareholder();
        shareholder.setShareholder_id(rs.getLong("shareholder_id"));
        shareholder.setLastName(rs.getString("last_name"));
        shareholder.setFirstName(rs.getString("first_name"));
        shareholder.setMiddleName(rs.getString("middle_name"));
        shareholder.setDob(rs.getString("dob"));
        shareholder.setPhone(rs.getString("phone"));
        shareholder.setOccupation(rs.getString("occupation"));
        shareholder.setEmail(rs.getString("email"));
        shareholder.setGender(rs.getString("gender"));
        shareholder.setMaritalStatus(rs.getString("marital_status"));
        shareholder.setAddress(rs.getString("address"));
        shareholder.setCountry(rs.getString("country"));
        shareholder.setCity(rs.getString("city"));
        shareholder.setPostalCode(rs.getString("postal_code"));
        shareholder.setStateOfOrigin(rs.getString("state_of_origin"));
        shareholder.setLgaOfOrigin(rs.getString("lga_of_origin"));
        shareholder.setMarriageCertificateNumber(rs.getString("marriage_certificate_number"));
        shareholder.setKinName(rs.getString("kin_name"));
        shareholder.setRelationship(rs.getString("relationship"));
        shareholder.setKinEmail(rs.getString("kin_email"));
        shareholder.setKinPhone(rs.getString("kin_phone"));
        shareholder.setKinAddress(rs.getString("kin_address"));
        shareholder.setKinCountry(rs.getString("kin_country"));
        shareholder.setKinState(rs.getString("kin_state"));
        shareholder.setKinLga(rs.getString("kin_lga"));
        shareholder.setShareholderType(rs.getString("shareholder_type"));
        shareholder.setStockBroker(rs.getInt("stock_broker"));
        shareholder.setRin(rs.getString("rin"));
        shareholder.setNuban(rs.getString("nuban"));
        shareholder.setBankName(rs.getString("bank_name"));
        shareholder.setBankAccount(rs.getString("bank_account"));
        shareholder.setBvn(rs.getString("bvn"));
        shareholder.setClearingHousingNumber(rs.getString("clearing_housing_number"));
        shareholder.setTaxExemption(rs.getBoolean("tax_exemption"));
        shareholder.setRegistrarMandated(rs.getBoolean("registrar_mandated"));
        shareholder.setEsopStatus(rs.getString("esop_status"));
        shareholder.setShareUnit(rs.getInt("share_unit"));
        shareholder.setClientCompany(rs.getLong("client_company"));
        shareholder.setCreatedOn(rs.getDate("created_on"));
        return shareholder;
    }
}
