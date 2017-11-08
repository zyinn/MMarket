package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.model.dbmodel.ContactUser;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dto.QuoteUserInfoDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by fan.bai on 2016/8/28.
 */
@Component
public class ContactUserInfoDtoConverter {
    public List<QuoteUserInfoDto> convertToDto(Collection<ContactUser> contactUsers){
        List<QuoteUserInfoDto> contacts = new ArrayList<>();
        if (contactUsers != null) {
            for (ContactUser contactUser : contactUsers) {
                //设置联系人
                QuoteUserInfoDto quoteUserInfoDto = new QuoteUserInfoDto();
                quoteUserInfoDto.setName(contactUser.getDisplayName() != null ? contactUser.getDisplayName() : "");
                quoteUserInfoDto.setMobile(contactUser.getMobile() != null ? contactUser.getMobile() : "");
                quoteUserInfoDto.setEmail(contactUser.getEmail() != null ? contactUser.getEmail() : "");
                quoteUserInfoDto.setTelephone(contactUser.getTelephone() != null ? contactUser.getTelephone() : "");
                quoteUserInfoDto.setQb_id(contactUser.getUserId() != null ? contactUser.getUserId() : "");
                quoteUserInfoDto.setQuoteAttribute(contactUser.getQuoteAttribute());
                contacts.add(quoteUserInfoDto);
            }
        }
        return contacts;
    }
    public QuoteUserInfoDto convertQuoteUserToDto(User user){
        //设置联系人
        QuoteUserInfoDto quoteUserInfoDto = new QuoteUserInfoDto();
        if(user!=null){
            quoteUserInfoDto.setName(user.getDisplayName() != null ? user.getDisplayName() : "");
            quoteUserInfoDto.setMobile(user.getMobile() != null ? user.getMobile() : "");
            quoteUserInfoDto.setEmail(user.getEmail() != null ? user.getEmail() : "");
            quoteUserInfoDto.setTelephone(user.getTelephone() != null ? user.getTelephone() : "");
            quoteUserInfoDto.setQb_id(user.getUserId() != null ? user.getUserId() : "");
        }
        return quoteUserInfoDto;
    }
}
