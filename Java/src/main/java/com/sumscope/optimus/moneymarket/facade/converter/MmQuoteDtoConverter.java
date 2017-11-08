package com.sumscope.optimus.moneymarket.facade.converter;

import com.sumscope.optimus.commons.exceptions.*;
import com.sumscope.optimus.moneymarket.commons.enums.*;
import com.sumscope.optimus.moneymarket.commons.util.*;
import com.sumscope.optimus.moneymarket.gatewayinvoke.model.QbUserId;
import com.sumscope.optimus.moneymarket.model.dbmodel.*;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDetailsDto;
import com.sumscope.optimus.moneymarket.model.dto.MmQuoteDto;
import com.sumscope.optimus.moneymarket.model.dto.MmTagDto;
import com.sumscope.optimus.moneymarket.model.dto.QuoteUserInfoDto;
import com.sumscope.optimus.moneymarket.service.InstitutionService;
import com.sumscope.optimus.moneymarket.service.MmQuoteQueryService;
import com.sumscope.optimus.moneymarket.service.MmTagService;
import com.sumscope.optimus.moneymarket.service.UserBaseService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by fan.bai on 2016/8/28.
 * Dto <--> model 转换器，用于对MmQuoteDto进行转换
 */
@Component
public class MmQuoteDtoConverter {
    private static final int TWENTY_FOUR = 24;
    private static final int SIXTY = 60;
    private static final int ONE_THOUSAND = 1000;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private MmTagService mmTagService;

    @Autowired
    private UserBaseService userBaseService;

    @Autowired
    private MmQuoteQueryService mmQuoteQueryService;

    public List<MmQuoteDto> convertMmQuoteListToMmQuoteDtoList(List<MmQuote> mmQuotes, boolean orderByInstitutionNamePY) {
        List<MmQuoteDto> mmQuoteDtos = new ArrayList<>();
        for (MmQuote mmQuote : mmQuotes) {
            MmQuoteDto mmQuoteDto;
            mmQuoteDto = convertMmQuoteToMmQuoteDto(mmQuote);
            mmQuoteDtos.add(mmQuoteDto);
        }
        if (orderByInstitutionNamePY) {
            Collections.sort(mmQuoteDtos, new Comparator<MmQuoteDto>() {
                @Override
                public int compare(MmQuoteDto o1, MmQuoteDto o2) {
                    return o1.getInstitutionNamePY().compareTo(o2.getInstitutionNamePY());
                }
            });
        }
        return mmQuoteDtos;
    }

    /**
     * 类转换 MmQuote-->MmQuoteDto
     *
     * @param mmQuote 需要转化的类
     * @return MmQuoteDto
     */
    public MmQuoteDto convertMmQuoteToMmQuoteDto(MmQuote mmQuote) {
        //获取mmQuote对应机构信息
        Map<String, Institution> allInstitution = institutionService.retrieveInstitutionsById();
        Institution institution = allInstitution.get(mmQuote.getInstitutionId());
        //组装MmQuoteDto类
        MmQuoteDto mmQuoteDto = new MmQuoteDto();
        BeanUtils.copyProperties(mmQuote, mmQuoteDto);

        if (mmQuote.getInstitutionId() != null) {
            PrimeCompany primeCompany = institutionService.retrievePrimeInstitutions().get(mmQuote.getInstitutionId());
            mmQuoteDto.setSource(primeCompany != null ? QuoteSource.PRIME_QB.getDisplayString() : mmQuote.getSource().name());
        }

        if (institution != null) {
            mmQuoteDto.setInstitutionName(institution.getName());
            mmQuoteDto.setInstitutionNamePY(institution.getPinyin());
        } else {
            mmQuoteDto.setInstitutionName("");
            mmQuoteDto.setInstitutionNamePY("");
        }
        //失效时间
        long time = TWENTY_FOUR * SIXTY * SIXTY * ONE_THOUSAND;
        if (!mmQuote.getQuoteType().equals(QuoteType.IBD) && mmQuote.getExpiredDate() != null) {
            mmQuoteDto.setValideDays((mmQuote.getExpiredDate().getTime() - Calendar.getInstance().getTime().getTime()) / time == 0 ? 1 : (int) ((mmQuote.getExpiredDate().getTime() - Calendar.getInstance().getTime().getTime()) / time) + 1);
        }
        //设置active，当expiredDate小于当前日期则设置active = false。
        Date now = new Date();
        mmQuoteDto.setActive(now.before(mmQuote.getExpiredDate()));
        List<MmQuoteDetailsDto> mmQuoteDetailsDtos = convertMmQuoteDetailsDtoList(mmQuote);
        mmQuoteDto.setQuoteDetailsDtos(mmQuoteDetailsDtos);

        //新增字段  报价联系人
        mmQuoteDto.setQuoteOperatorId(mmQuote.getQuoteOperatorId()!=null? mmQuote.getQuoteOperatorId() : null);
        Map<String, User> stringUserMap = userBaseService.retreiveAllUsersGroupByUserID();
        User user = stringUserMap.get(mmQuote.getQuoteUserId()!=null? mmQuote.getQuoteUserId() : null);
        mmQuoteDto.setQuoteOperatorName(user!=null? user.getDisplayName() : null);

        return mmQuoteDto;
    }

    /**
     * List<MmQuoteDetails> -->List<MmQuoteDetailsDto>
     */
    private List<MmQuoteDetailsDto> convertMmQuoteDetailsDtoList(MmQuote mmQuote) {
        List<MmQuoteDetails> mmQuoteDetailsList=mmQuote.getMmQuoteDetails();
        if (mmQuoteDetailsList != null && mmQuoteDetailsList.size() > 0) {
            List<MmQuoteDetailsDto> mmQuoteDetailsDtos = new ArrayList<>();
            List<MmQuoteDetailsDto> affectDetails=new ArrayList<>();
            for (MmQuoteDetails detail : mmQuoteDetailsList) {
                MmQuoteDetailsDto detailsDto;
                detailsDto = convertMmQuoteDetailsDto(detail,mmQuote);
                if(detailsDto.getDayLow()!=1 && detailsDto.getDayLow()==detailsDto.getDayHigh()){
                    affectDetails.add(detailsDto);
                }
                mmQuoteDetailsDtos.add(detailsDto);
            }
            if(mmQuoteDetailsDtos!=null && mmQuoteDetailsDtos.size()>0){
                setMobileIconForMmQuoteDetailsDto(mmQuoteDetailsDtos);
            }
            return mmQuoteDetailsDtos;
        } else {
            return null;
        }
    }

    private void setMobileIconForMmQuoteDetailsDto(List<MmQuoteDetailsDto> mmQuoteDetailsDtos) {
        for(MmQuoteDetailsDto mmQuoteDetailsDto:mmQuoteDetailsDtos){
            if(mmQuoteDetailsDto.getDayHigh().equals(mmQuoteDetailsDto.getDayLow()) && mmQuoteDetailsDto.getDayHigh()!=1){
                List<QuoteTimePeriod> affectTimePeroid = TimePeroidUtils.getAffectTimePeroid(mmQuoteDetailsDto.getDayLow(), mmQuoteDetailsDto.getDayHigh());
                if(affectTimePeroid!=null){
                    setMobileAffectTimePeroid(mmQuoteDetailsDtos, affectTimePeroid);
                }
            }
        }
    }

    private void setMobileAffectTimePeroid(List<MmQuoteDetailsDto> mmQuoteDetailsDtos, List<QuoteTimePeriod> affectTimePeroid) {
        for(QuoteTimePeriod affectPeroid:affectTimePeroid){
            gettMobileAffectMmQuoteDetailsDtos(mmQuoteDetailsDtos, affectPeroid);
        }
    }

    private void gettMobileAffectMmQuoteDetailsDtos(List<MmQuoteDetailsDto> mmQuoteDetailsDtos, QuoteTimePeriod affectPeroid) {
        for(MmQuoteDetailsDto mmQuoteDetails:mmQuoteDetailsDtos){
            List<QuoteTimePeriod> affect = TimePeroidUtils.getAffectTimePeroid(mmQuoteDetails.getDayLow(), mmQuoteDetails.getDayHigh());
             for(QuoteTimePeriod affects:affect){
                 if(affectPeroid==affects){
                     mmQuoteDetails.setIcon(true);
                 }
             }
        }
    }

    /**
     * MmQuoteDetails-->MmQuoteDetailsDto
     */
    private MmQuoteDetailsDto convertMmQuoteDetailsDto(MmQuoteDetails mmQuoteDetails,MmQuote mmQuote) {
        MmQuoteDetailsDto detailsDto = new MmQuoteDetailsDto();
        detailsDto.setId(mmQuoteDetails.getId());
        detailsDto.setLimitType(TimePeroidUtils.getPeriodFromValue(mmQuoteDetails.getDaysLow(), mmQuoteDetails.getDaysHigh()));
        detailsDto.setDayHigh(mmQuoteDetails.getDaysHigh());
        detailsDto.setDayLow(mmQuoteDetails.getDaysLow());
        detailsDto.setPrice(mmQuoteDetails.getPrice());
        detailsDto.setQuantity(mmQuoteDetails.getQuantity());//数量
        detailsDto.setActive(mmQuoteDetails.isActive());
        QuoteTimePeriod quoteTimePeriod=TimePeroidUtils.getAffectTimePeroid(mmQuoteDetails.getDaysLow(), mmQuoteDetails.getDaysHigh()).get(0);
        if(mmQuote.getQuoteType()!=QuoteType.IBD && (quoteTimePeriod==QuoteTimePeriod.T7D || quoteTimePeriod==QuoteTimePeriod.T14D)){
            quoteTimePeriod=QuoteTimePeriod.T1M;
        }
        detailsDto.setQuoteTimePeriod(quoteTimePeriod);
        return detailsDto;
    }

    public List<MmQuote> convertMmQuoteDtoToModelForNormalSave(List<MmQuoteDto> mmQuoteDtoList, User currentUser) {
        return convertMmQuoteDtoToModel(mmQuoteDtoList, currentUser, false);
    }

    public List<MmQuote> convertMmQuoteDtoToModelForAllienceSave(List<MmQuoteDto> mmQuoteDtoList, User currentUser) {
        return convertMmQuoteDtoToModel(mmQuoteDtoList, currentUser, true);
    }

    /**
     * 机构-人员关系转换 供 保存 到user_preference表用
     * @param mmQuoteDtoList
     * @return
     */
    public List<UserPreference> convertMmQuoteDtoToInstitutionUserMappingSave(List<MmQuoteDto> mmQuoteDtoList, User currentUser) {
        List<UserPreference> list=new ArrayList<>();
        if(mmQuoteDtoList!=null && mmQuoteDtoList.size()>0){
            UserPreference userPreference=new UserPreference();
            userPreference.setId(UUIDUtils.generatePrimaryKey());
            userPreference.setUserId(currentUser.getUserId());
            String sb = getInstitutionUserMapperToJson(mmQuoteDtoList);
            userPreference.setPreferenceValue(sb);
            userPreference.setPreferenceType(PreferenceType.INSTITUTION_USER_MAP);
            list.add(userPreference);
        }
       return list;
    }

    /**
     * 机构-人员关系转换 供 修改 到user_preference表用
     * @param mmQuoteDtoList
     * @param currentUser
     * @return
     */
    public List<UserPreference> convertMmQuoteDtoToInstitutionUserMapperUpdate( List<MmQuoteDto> mmQuoteDtoList,User currentUser) {
        List<UserPreference> list=new ArrayList<>();
        Map<String, UserPreference> userPreferenceMap = mmQuoteQueryService.retrieveInstitutionUserMappingByUserID();
        if(mmQuoteDtoList!=null && mmQuoteDtoList.size()>0 && userPreferenceMap!=null){
            String sb = getInstitutionUserMapperToJson(mmQuoteDtoList);
            UserPreference userPreference = userPreferenceMap.get(currentUser.getUserId());
            //如果偏好表中有对应的机构-人员关系 如与现对应关系一致，则不跟新也不新增，不一致则更新，不存在则新增
            if(userPreference!=null){
                if(!userPreference.getPreferenceValue().equals(sb) && !"".equals(sb)){
                    userPreference.setPreferenceValue(sb);
                    list.add(userPreference);
                    mmQuoteDtoList.removeAll(mmQuoteDtoList);
                }else{
                    mmQuoteDtoList.removeAll(mmQuoteDtoList);
                }
            }
        }
        return list;
    }

    /**
     * 机构-联系人关系 转 Json 录入user_preference表preference_value字段中
     * @param mmQuoteDtoList
     * @return
     */
    private String getInstitutionUserMapperToJson(List<MmQuoteDto> mmQuoteDtoList) {
        StringBuilder sb = new StringBuilder("");
        Map<String, List<MmQuoteDto>> mapMmQuoteDto = getMapMmQuoteDto(mmQuoteDtoList);
        List<MmQuoteDto> mmQuoteDto= getMmQuoteDtoList(mapMmQuoteDto);
        //拼接机构-人员对应关系
        toJsonStitchingInstitutionUserMapper(mmQuoteDto, sb);
        return sb.toString();
    }

    private  Map<String,List<MmQuoteDto>>  getMapMmQuoteDto(List<MmQuoteDto> mmQuoteDtoList){
        Map<String,List<MmQuoteDto>> map=new HashMap<>();
        for(int i=0;i<mmQuoteDtoList.size();i++){
            List<MmQuoteDto> list=new ArrayList<>();
            for(int j=i+1;j<mmQuoteDtoList.size();j++){
                if(mmQuoteDtoList.get(i).getInstitutionId().equals(mmQuoteDtoList.get(j).getInstitutionId())){
                   if(!list.contains(mmQuoteDtoList.get(i))){
                       list.add(mmQuoteDtoList.get(i));
                   }
                    list.add(mmQuoteDtoList.get(j));
                    mmQuoteDtoList.remove(mmQuoteDtoList.get(j));
                    j--;
                }
            }
            if(list.size()==0){
                list.add(mmQuoteDtoList.get(i));
            }
            map.put(mmQuoteDtoList.get(i).getInstitutionId(),list);
        }
        return map;
    }

    private List<MmQuoteDto> getMmQuoteDtoList(Map<String,List<MmQuoteDto>> map){
        List<MmQuoteDto> mmQuoteDtoList=new ArrayList<>();
        for(List<MmQuoteDto> list : map.values()){
           if(list.size()==1){
               mmQuoteDtoList.addAll(list);
           }else{
               for(int i=0;i< list.size();i++){
                   if(list.get(i).getId()==null){
                       mmQuoteDtoList.add(list.get(i));
                       break;
                   }else{
                       converterOnlyOneMmQuote(mmQuoteDtoList, list);
                       break;
                   }
               }
           }
        }
        return mmQuoteDtoList;
    }

    private void converterOnlyOneMmQuote(List<MmQuoteDto> mmQuoteDtoList, List<MmQuoteDto> list) {
        List<MmQuoteDto> mmQuoteList=new ArrayList<>();
        mmQuoteList.addAll(list);
        for(int j=0;j<list.size();j++){
            if(!list.get(j).isActive() || list.get(j).getId()!=null){
                list.remove(list.get(j));
                j--;
            }
        }
        if(list!=null && list.size()>0){
            mmQuoteDtoList.add(list.get(list.size()-1));
            return;
        }else{
            mmQuoteDtoList.add(mmQuoteList.get(mmQuoteList.size()-1));
            return;
        }
    }

    /**
     * 机构-联系人关系转 json
     * @param mmQuoteDtoList
     * @param sb
     * @return
     */
    private void toJsonStitchingInstitutionUserMapper(List<MmQuoteDto> mmQuoteDtoList, StringBuilder sb) {
        if(mmQuoteDtoList!=null && mmQuoteDtoList.size()>0){
            for(int i=0;i< mmQuoteDtoList.size();i++){
                sb.append(i==0 ? "{'" : "");
                sb.append(mmQuoteDtoList.get(i).getInstitutionId()+"':'"+ mmQuoteDtoList.get(i).getQuoteUserId());
                sb.append(i!=mmQuoteDtoList.size()-1 ? "'," : "'}");
            }
        }
    }

    /**
     * 根据MmQuoteDto生成MmQuote模型，用于联盟报价，根据前端传入的机构ID设置相应信息
     *
     * @return List<MmQuote>, 注意根据机构写入省份和机构属性信息
     */
    private List<MmQuote> convertMmQuoteDtoToModel(List<MmQuoteDto> mmQuoteDtoList, User currentUser, boolean allienceSave) {
        List<MmQuote> mmQuotes = new ArrayList<MmQuote>();
        List<ValidationExceptionDetails> invalids = new ArrayList<>();
        for (MmQuoteDto mmQuoteDto : mmQuoteDtoList) {
            checkMmQuoteDtoValidate(mmQuoteDto);
            MmQuote mmQuote = new MmQuote();
            if (!allienceSave) {
                //当非联盟或代报价时，使用当前用户的所属机构,并设置机构相关属性至报价单主表。
                mmQuoteDto.setInstitutionId(currentUser.getCompanyId());
                setMmQuoteDtoByInstitution(mmQuote, currentUser.getCompanyId());
                mmQuoteDto.setQuoteUserId(currentUser.getUserId());
                mmQuote.setQuoteOperatorId(mmQuoteDto.getQuoteUserId());
            } else {
                //联盟报价或代报价时，先判断用户是否拥有合法权限。再设置机构相关属性至报价单主表。
                checkAllianceInstitutionIdByUser(mmQuote, mmQuoteDto);
                mmQuoteDto.setQuoteUserId(mmQuoteDto.getQuoteUserId());
                mmQuote.setQuoteOperatorId(currentUser.getUserId());
            }
            mapToMmQuoteModel(mmQuote, mmQuoteDto, invalids);
            if (allienceSave) {
                // 对于联盟报价，报价单的有效性直接设置在主表上，既要么全部有效，要么全部无效。对于删除了价格的明细数据
                // 前端并不发往服务端
                setupDetailsActiveStatus(mmQuoteDto.isActive(), mmQuote);
            }

            mmQuotes.add(mmQuote);
        }

        if (invalids.size() > 0) {
            ValidationException validationException = new ValidationException();
            validationException.setExceptionDetails(invalids);
            throw validationException;
        }
        return mmQuotes;
    }

    private void setupDetailsActiveStatus(boolean active, MmQuote mmQuote) {
        if (!CollectionsUtil.isEmptyOrNullCollection(mmQuote.getMmQuoteDetails())) {
            for (MmQuoteDetails details : mmQuote.getMmQuoteDetails()) {
                details.setActive(active);
            }
        }
    }

    /**
     * 设置报价单各个字段信息转换成MmQuote模型
     */
    private void mapToMmQuoteModel(MmQuote mmQuote, MmQuoteDto mmQuoteDto, List<ValidationExceptionDetails> invalids) {
        mmQuote.setId(mmQuoteDto.getId() != null ? mmQuoteDto.getId() : "");
        mmQuote.setDirection(mmQuoteDto.getDirection());
        mmQuote.setMemo(mmQuoteDto.getMemo() != null ? mmQuoteDto.getMemo() : "");
        mmQuote.setQuoteUserId(mmQuoteDto.getQuoteUserId());
        if (mmQuoteDto.getQuoteType() != null) {
            mmQuote.setQuoteType(mmQuoteDto.getQuoteType());
        } else {
            invalids.add(new ValidationExceptionDetails(GeneralValidationErrorType.DATA_INVALID, "quoteType", "报价类型不可为空"));
        }
        mmQuote.setSource(QuoteSource.QB);
        mmQuote.setMethodType(mmQuoteDto.getMethodType() != null ? mmQuoteDto.getMethodType() : MethodType.SEF);

        //失效时间
        if (QuoteType.IBD == mmQuoteDto.getQuoteType()) {
            mmQuote.setExpiredDate(QuoteDateUtils.getExpiredTimeOfDate(new Date())); // 吸存的保存至当天11点
        } else {
            if (mmQuote.getMethodType().equals(MethodType.ALC) || mmQuote.getMethodType().equals(MethodType.BRK)) {
                mmQuoteDto.setValideDays(7);
                mmQuote.setExpiredDate(getExpiredDateForQuote(mmQuoteDto));
            } else {
                mmQuote.setExpiredDate(getExpiredDateForQuote(mmQuoteDto));
            }
        }
        mapToMmQuoteDetailsDto(mmQuote, mmQuoteDto);
        mapTagsDto(mmQuote, mmQuoteDto, invalids);
    }

    private void mapTagsDto(MmQuote mmQuote, MmQuoteDto mmQuoteDto, List<ValidationExceptionDetails> invalids) {
        if (mmQuoteDto.getTags() != null && mmQuoteDto.getTags().size() > 0) {
            mmQuote.setTags(new HashSet<>());
            for (MmTagDto tagDto : mmQuoteDto.getTags()) {
                MmQuoteTag tag = new MmQuoteTag();
                //check valid codes
                MmTag mmTag = mmTagService.retrieveAllTags().get(tagDto.getTagCode());
                if (mmTag == null || !mmTag.isActive()) {
                    ValidationExceptionDetails validationExceptionDetails = new ValidationExceptionDetails(GeneralValidationErrorType.DATA_INVALID, "tags", "Invalid Tag");
                    invalids.add(validationExceptionDetails);
                }
                tag.setQuoteId(mmQuoteDto.getId());
                tag.setTagCode(tagDto.getTagCode());
                mmQuote.getTags().add(tag);
            }
        }
    }

    /**
     * 设置报价单各个字段信息转换成mmQuoteDetails模型
     */
    private void mapToMmQuoteDetailsDto(MmQuote mmQuote, MmQuoteDto mmQuoteDto) {
        List<MmQuoteDetailsDto> quoteDetailsDtos = mmQuoteDto.getQuoteDetailsDtos();
        if (quoteDetailsDtos != null && quoteDetailsDtos.size() > 0) {
            List<MmQuoteDetails> quoteDetails = new ArrayList<>();
            for (MmQuoteDetailsDto mmQuoteDetailsDto : quoteDetailsDtos) {
                MmQuoteDetails mmQuoteDetails = new MmQuoteDetails();
                BigDecimal quantity = mmQuoteDetailsDto.getQuantity() == null ? null : mmQuoteDetailsDto.getQuantity();
                BigDecimal price = mmQuoteDetailsDto.getPrice() == null ? null : mmQuoteDetailsDto.getPrice();
                if (mmQuoteDetailsDto.getLimitType() == null) {
                    if (mmQuoteDetailsDto.getDayLow() != null && mmQuoteDetailsDto.getDayHigh() != null) {
                        mmQuoteDetails.setDaysHigh(mmQuoteDetailsDto.getDayHigh() > mmQuoteDetailsDto.getDayLow() ? mmQuoteDetailsDto.getDayHigh() : mmQuoteDetailsDto.getDayLow());
                        mmQuoteDetails.setDaysLow(mmQuoteDetailsDto.getDayHigh() > mmQuoteDetailsDto.getDayLow() ? mmQuoteDetailsDto.getDayLow() : mmQuoteDetailsDto.getDayHigh());
                    } else if (mmQuoteDetailsDto.getDayLow() == null && mmQuoteDetailsDto.getDayHigh() != null) {
                        mmQuoteDetails.setDaysHigh(mmQuoteDetailsDto.getDayHigh());
                        mmQuoteDetails.setDaysLow(mmQuoteDetailsDto.getDayHigh());
                    } else if (mmQuoteDetailsDto.getDayLow() != null && mmQuoteDetailsDto.getDayHigh() == null) {
                        mmQuoteDetails.setDaysHigh(mmQuoteDetailsDto.getDayLow());
                        mmQuoteDetails.setDaysLow(mmQuoteDetailsDto.getDayLow());
                    } else {
                        throw new BusinessRuntimeException(BusinessRuntimeExceptionType.OTHER, "天数必须区间值必须存在");
                    }
                } else {
                    mmQuoteDetails.setDaysHigh(mmQuoteDetailsDto.getLimitType().getDaysHigh());
                    mmQuoteDetails.setDaysLow(mmQuoteDetailsDto.getLimitType().getDaysLow());
                }
                mmQuoteDetails.setPriceLow(price);
                mmQuoteDetails.setQuantityLow(quantity);
                mmQuoteDetails.setPrice(price);
                mmQuoteDetails.setQuantity(quantity);
                mmQuoteDetails.setId(mmQuoteDetailsDto.getId());
                mmQuoteDetails.setActive(mmQuoteDetailsDto.getActive());
                quoteDetails.add(mmQuoteDetails);
            }
            mmQuote.setMmQuoteDetails(quoteDetails);
        }
    }

    private Date getExpiredDateForQuote(MmQuoteDto mmQuoteDto) {
        if (mmQuoteDto.getValideDays() < 1) {
            mmQuoteDto.setValideDays(7);
        }
        Calendar time = Calendar.getInstance();
        time.set(Calendar.DATE, time.get(Calendar.DATE) + (mmQuoteDto.getValideDays() - 1));
        return QuoteDateUtils.getExpiredTimeOfDate(time.getTime());
    }


    /**
     * 联盟机构报价---设置用户当前机构,省份和机构属性信息到报价单
     */
    private void checkAllianceInstitutionIdByUser(MmQuote mmQuote, MmQuoteDto mmQuoteDto) {
        if (mmQuoteDto.getInstitutionId() != null && !"-1".equals(mmQuoteDto.getInstitutionId())) {
            setMmQuoteDtoByInstitution(mmQuote, mmQuoteDto.getInstitutionId());
        } else {
            throw new BusinessRuntimeException(
                    BusinessRuntimeExceptionType.PARAMETER_INVALID, "联盟机构ID非空");
        }
    }

    /**
     * 通过Institution转化为mmQuote的省和托管机构、银行性质
     *
     * @param mmQuote ,institutionId
     */
    private void setMmQuoteDtoByInstitution(MmQuote mmQuote, String institutionId) {
        mmQuote.setInstitutionId(institutionId);
        Institution institution = institutionService.retrieveInstitutionsById().get(institutionId);//获取机构规模信息（包含机构规模和父节点id集合）
        //直辖市的省份在数据库内是空，因此需要填入直辖市名称
        if(institution!=null){
            mmQuote.setProvince(institution.getProvince() != null ? institution.getProvince() : institution.getCityName());
            mmQuote.setBankNature(institution.getBankNature() != null ? institution.getBankNature() : "");
            if ("1".equals(institution.getQualification())) {
                mmQuote.setCustodianQualification(true);
            } else {
                mmQuote.setCustodianQualification(false);
            }
            mmQuote.setFundSize(institution.getFundSize());//设置报价记录的机构规模字段
        }else{
            throw new BusinessRuntimeException(
                    BusinessRuntimeExceptionType.PARAMETER_INVALID, "联盟机构ID不合法："+institutionId);
        }
    }

    /**
     * MmQuoteDto 数据验证
     */
    private void checkMmQuoteDtoValidate(MmQuoteDto mmQuoteDto){
        QuoteType quoteType = mmQuoteDto.getQuoteType();
        if (!QuoteType.GTF.equals(quoteType) && !QuoteType.UR2.equals(quoteType) &&
                !QuoteType.UR3.equals(quoteType) && !QuoteType.IBD.equals(quoteType)) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quoteType不合法");
        }

        String institutionId = mmQuoteDto.getInstitutionId();
        if (institutionId != null && institutionId.length() > 32) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "institutionId不合法");
        }
        mmQuoteDto.setInstitutionId(Utils.validate(institutionId));

        String quoteUserId = mmQuoteDto.getQuoteUserId();
        if (quoteUserId != null && quoteUserId.length() > 32) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quoteUserId不合法");
        }
        mmQuoteDto.setQuoteUserId(Utils.validate(quoteUserId));

        String quoteOperatorId = mmQuoteDto.getQuoteOperatorId();
        if (quoteOperatorId != null && quoteOperatorId.length() > 32) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quoteOperatorId不合法");
        }
        mmQuoteDto.setQuoteOperatorId(Utils.validate(quoteOperatorId));

        int sequence = mmQuoteDto.getSequence();
        if (sequence > 99999 || sequence < -9999) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "sequence必须小于99999的一个整数");
        }

        Direction dir = mmQuoteDto.getDirection();
        if (!Direction.IN.equals(dir) && !Direction.OUT.equals(dir)) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "direction不合法");
        }

        String memo = mmQuoteDto.getMemo();
        if (memo != null && memo.length() > 512) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "memo不合法");
        }
        mmQuoteDto.setMemo(Utils.validateStr(memo));

        String source = mmQuoteDto.getSource();
        if (source != null && source.length() > 3) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "source不合法");
        }
        mmQuoteDto.setSource(Utils.validate(source));

        String province = mmQuoteDto.getProvince();
        if (province != null && province.length() > 32) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "province不合法");
        }
        mmQuoteDto.setProvince(Utils.validate(province));

        BigDecimal fundSize = mmQuoteDto.getFundSize();
        if (fundSize != null && fundSize.toString().length() > 10) {
            throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "fundSize不合法");
        }
        List<MmQuoteDetailsDto> quoteDetailsDtos = mmQuoteDto.getQuoteDetailsDtos();
        if(quoteDetailsDtos!=null && quoteDetailsDtos.size()>0){
        for(MmQuoteDetailsDto mmQuoteDetailsDto: quoteDetailsDtos){
            BigDecimal price=mmQuoteDetailsDto.getPrice();
            if (price != null && price.toString().length() > 14) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "price不合法");
            }
            BigDecimal quantity = mmQuoteDetailsDto.getQuantity();
            if (quantity != null && quantity.toString().length() > 16) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "quantity不合法");
            }
            Integer dayHigh = mmQuoteDetailsDto.getDayHigh();
            Integer dayLow = mmQuoteDetailsDto.getDayLow();
            if (dayHigh != null && dayLow!=null && (dayHigh.toString().length()>11 || dayLow.toString().length()>11)) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "dayHigh或者dayLow不合法");
            }
            String id = mmQuoteDetailsDto.getId();
            if (id != null && id.length() >32) {
                throw new BusinessRuntimeException(BusinessRuntimeExceptionType.PARAMETER_INVALID, "id不合法");
            }
        }
        }
    }

    public List<MmQuoteDto> converterToMobileMmQuoteDto(List<MmQuote> mmQuotes, boolean orderByInstitutionNamePY, Map<String,QbUserId> qbQmMapper){
        List<MmQuoteDto> mmQuoteDtos = convertMmQuoteListToMmQuoteDtoList(mmQuotes, orderByInstitutionNamePY);
        Map<String, User> stringUserMap = userBaseService.retreiveAllUsersGroupByUserID();
        for(MmQuoteDto mmQuoteDto: mmQuoteDtos){
            QbUserId resultTable = qbQmMapper.get(mmQuoteDto.getQuoteUserId());
            QuoteUserInfoDto quoteUserInfoDto=new QuoteUserInfoDto();
            quoteUserInfoDto.setQb_id(resultTable!=null ? resultTable.getAccountid() : "");
            User user = stringUserMap.get(resultTable!=null ?  resultTable.getAccountid() : mmQuoteDto.getQuoteUserId());
            quoteUserInfoDto.setQmId(resultTable!=null ? resultTable.getUserid() : "");
            quoteUserInfoDto.setTelephone(user!=null ? user.getTelephone() : "");
            quoteUserInfoDto.setMobile(user!=null ? user.getMobile() : "");
            quoteUserInfoDto.setName(user!=null ? user.getDisplayName() : "");
            quoteUserInfoDto.setEmail(user!=null ? user.getEmail() : "");
            mmQuoteDto.setTrader(quoteUserInfoDto);

            if(mmQuoteDto.getQuoteDetailsDtos()!=null && mmQuoteDto.getQuoteDetailsDtos().size()>0){
                Collections.sort(mmQuoteDto.getQuoteDetailsDtos(), new Comparator<MmQuoteDetailsDto>() {
                    @Override
                    public int compare(MmQuoteDetailsDto o1, MmQuoteDetailsDto o2) {
                        return o1.getDayHigh().compareTo(o2.getDayHigh());
                    }
                });
            }
        }
        return mmQuoteDtos;
    }

}
