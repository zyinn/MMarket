package com.sumscope.optimus.moneymarket.model.dto;

import java.util.List;

/**
 * 查看联盟报价详情
 */
public class MmQuoteWithContactDto {
	private MmQuoteDto mmQuoteDto;
	private List<QuoteUserInfoDto> contacts;
	private String telephone;

	private String primeInstitutionName;
	public MmQuoteDto getMmQuoteDto() {
		return mmQuoteDto;
	}

	public void setMmQuoteDto(MmQuoteDto mmQuoteDto) {
		this.mmQuoteDto = mmQuoteDto;
	}

	public List<QuoteUserInfoDto> getContacts() {
		return contacts;
	}

	public void setContacts(List<QuoteUserInfoDto> contacts) {
		this.contacts = contacts;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getPrimeInstitutionName() {
		return primeInstitutionName;
	}

	public void setPrimeInstitutionName(String primeInstitutionName) {
		this.primeInstitutionName = primeInstitutionName;
	}

}
