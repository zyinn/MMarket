package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.moneymarket.model.dbmodel.MmTag;
import com.sumscope.optimus.moneymarket.model.dto.MmTagDto;
import org.springframework.stereotype.Component;

/**
 * Created by fan.bai on 2016/8/29.
 * MmTag 转换器
 */
@Component
public class MmTagDtoConverter {
    public MmTagDto convertToDto(MmTag tag){
        MmTagDto dto = new MmTagDto();
        dto.setTagCode(tag.getTagCode());
        dto.setTagName(tag.getTagName());
        dto.setSeq(tag.getSeq());
        return dto;
    }
}
