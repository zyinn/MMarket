package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.PrimeCompany;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteWithContactDto;
import com.sumscope.optimus.moneymarket.model.dto.QuoteUserInfoDto;
import com.sumscope.optimus.moneymarket.service.InstitutionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by fan.bai on 2016/8/28.
 * MmQuoteWithContactDto 转换器
 */
@Component
public class MmQuoteWithContactDtoConverter {
    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private ContactUserInfoDtoConverter contactUserInfoDtoConverter;

    public List<MmQuoteWithContactDto> convertMMQuoteWithContactsAndPrimeInstitution(List<MmQuoteDto> MmQuoteDto, Institution institutions) {
        List<MmQuoteWithContactDto> resultList = new ArrayList<>();
        for (MmQuoteDto mMQuoteDto : MmQuoteDto) {
            //转换MmQuoteWithContactDto模型
            MmQuoteWithContactDto mmQuoteDtoWithContacts = new MmQuoteWithContactDto();
            mmQuoteDtoWithContacts.setContacts(getContactsByInstitutionID(mMQuoteDto.getInstitutionId()));
            mmQuoteDtoWithContacts.setMmQuoteDto(mMQuoteDto);
            PrimeCompany primeCompany = institutionService.retrievePrimeInstitutions().get(mMQuoteDto.getInstitutionId());
            //联盟机构的联系方式
            mmQuoteDtoWithContacts.setTelephone((primeCompany != null && primeCompany.getActive() != 0 && primeCompany.getTelephone() != null) ? primeCompany.getTelephone() : "");

            mmQuoteDtoWithContacts.setPrimeInstitutionName((institutions != null && institutions.getName() != null) ? institutions.getName() : "");
            resultList.add(mmQuoteDtoWithContacts);
        }
        return resultList;
    }
    private List<QuoteUserInfoDto> getContactsByInstitutionID(String institutionId) {
        Set<ContactUser> contactSet = institutionService.retrieveAllPrimeContacts().get(institutionId);
        return contactUserInfoDtoConverter.convertToDto(contactSet);
    }

}
