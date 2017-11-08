package com.sumscope.optimus.moneymarket.service;

import com.sumscope.optimus.commons.cachemanagement.annotation.CacheMe;
import com.sumscope.optimus.moneymarket.commons.Constant;
import com.sumscope.optimus.moneymarket.commons.enums.PreferenceType;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteTimePeriod;
import com.sumscope.optimus.moneymarket.commons.enums.QuoteType;
import com.sumscope.optimus.moneymarket.commons.enums.TrustType;
import com.sumscope.optimus.moneymarket.commons.util.Utils;
import com.sumscope.optimus.moneymarket.dao.UserPreferenceDao;
import com.sumscope.optimus.moneymarket.model.dbmodel.Institution;
import com.sumscope.optimus.moneymarket.model.dbmodel.User;
import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class QbBaseServiceImpl implements QbBaseService{
	private static final String PROVINCE = "province";

	private static final int MAX_RESULT_SIZE = 4;

	@Autowired
    private UserPreferenceDao userPreferenceDao;

    @Autowired
    private UserBaseService userBaseService;

	@Autowired
	private MmQuoteQueryService mmQuoteQueryService;


	@Override
	public List<Institution> queryByKeyword(String keyword) {
		if(StringUtils.isBlank(keyword) || keyword.length() < 2){
			return new ArrayList<>();
		}
		Map<String, Institution> institutionMap = mmQuoteQueryService.retrieveQuoteInstitutions();
		Collection<Institution> list = institutionMap.values();
		if(list == null || list.size() == 0){
			return new ArrayList<>();
		}
		List<Institution> resultList = new ArrayList<Institution>(list.size());
		// 匹配关键词
		for(Institution company : list){
			if(Utils.isMatched(company.getName(), keyword)
					|| Utils.isMatched(company.getPinyin(), keyword)
					|| Utils.isMatched(company.getPinyinFull(), keyword)){
				resultList.add(company);
			}
		}
		return resultList;
	}

    @Override
    public boolean setUserArea(UserPreference userPreference) {
        Map<String, Object> map = new HashMap<>();
        map.put(UserPreferenceDao.USER_ID, userPreference.getUserId());
        map.put(UserPreferenceDao.PREFERENCE_TYPE, userPreference.getPreferenceType());
        //通过用户ID及偏好类型判断用户偏好是否存在
        if (userPreferenceDao.isRecordExist(map)!=null) {
            userPreferenceDao.updateUserPreference(userPreference);
        }else {
            String id=UUID.randomUUID().toString();
            id = id.replace("-","");
            userPreference.setId(id);
            userPreferenceDao.insertUserPreference(userPreference);
        }
        return true;
    }


    @Override
    public UserPreference getUserArea(String userid) {
    	Map<String, Object> map = new HashMap<>();
    	map.put(UserPreferenceDao.USER_ID, userid);
        map.put(UserPreferenceDao.PREFERENCE_TYPE, PreferenceType.AREA);
        return userPreferenceDao.isRecordExist(map);
    }

	@Override
	@CacheMe(timeout = Constant.CACHE_TIMEOUT, synchornizeUpdate = false)
	public List<Map<String,Object>> getAreaList() {
		List<Map<String, Object>> list= userPreferenceDao.getAreaList();
		List<Map<String, Object>> arealist = new ArrayList<>();
		Map<String, Object> bmap = new HashMap<>();
		Map<String, Object> smap = new HashMap<>();
		bmap.put(PROVINCE, "北京");
		smap.put(PROVINCE, "上海");
		arealist.add(bmap);
		arealist.add(smap);
		for(Map<String, Object> map : list){
			if (map.get(PROVINCE).equals("北京")) {
				continue;
			}
			if (map.get(PROVINCE).equals("上海")) {
				continue;
			}
			arealist.add(map);
		}
		return arealist;
	}


	@Override
	public String getUserProvince(String userid) {
		Map<String, User> users = userBaseService.retreiveAllUsersGroupByUserID();
		User user = users.get(userid);

		String userProvince = user.getProvince();
		if(userProvince ==null){
			//对于QB直辖市用户，省份字段为null，取城市字段
			userProvince = user.getCityName();
		}
		return userProvince;
	}


	@Override
	public Map<String, Object> period() {
		Map<String, Object> periodtype = new HashMap<>();
		for (int i = 0; i < QuoteTimePeriod.values().length; i++) {
			Map<String, Object> map = new HashMap<>();
			map.put("min", QuoteTimePeriod.values()[i].getDaysLow());
			map.put("max", QuoteTimePeriod.values()[i].getDaysHigh());
			periodtype.put(QuoteTimePeriod.values()[i].name(), map);
		}
		return periodtype;
	}


	@Override
	public List<Map<String, Object>> quoteType() {
		List<Map<String, Object>> list = new ArrayList<>();
		for(int i=0 ; i<QuoteType.values().length;i++){
			Map<String, Object> map = new HashMap<>();
			map.put("name", QuoteType.values()[i].getDisplayName());
			map.put("id", QuoteType.values()[i]);
			list.add(map);
		}
		return list;
	}


	@Override
	public List<Map<String, Object>> trustType() {
		List<Map<String, Object>> list = new ArrayList<>();
		for(int i=0 ; i<TrustType.values().length;i++){
			Map<String, Object> map = new HashMap<>();
			map.put("name", TrustType.values()[i].getName());
			map.put("id", TrustType.values()[i].getValue());
			list.add(map);
		}
		return list;
	}

	@Override
	public List<List<User>> queryAllUserWithPinYin(String keyword) {
		// 一定要有关键字
		if (StringUtils.isBlank(keyword) || keyword.length() < 2) {
			return new ArrayList<>();
		}
		Map<String, User> stringUserMap = mmQuoteQueryService.retrieveQuoteUsers();
		Collection<User> users = stringUserMap.values();
		List<User> qqList = new ArrayList<>();
		List<User> qbList = new ArrayList<>();
		for (User allUser : users) {
			if (qqList.size() > MAX_RESULT_SIZE && qbList.size() > MAX_RESULT_SIZE) {
				break;
			}
			if (StringUtils.isBlank(keyword)) {
				addKeyWordToArray(allUser, qqList, qbList);
			} else {
				// 关键词匹配
				if (Utils.isMatched(allUser.getDisplayName(), keyword)
						|| Utils.isMatched(allUser.getDisplayNamePinYin(), keyword)
						|| Utils.isMatched(allUser.getDisplayNamePY(), keyword)) {
					addKeyWordToArray(allUser, qqList, qbList);
				}
			}
		}
		List<List<User>> retList = new ArrayList<>(2);
		retList.add(0, qbList);
		retList.add(1, qqList);
		return retList;
	}

	private void addKeyWordToArray(User user, List<User> qqList, List<User> qbList) {
		if ("QQ".equals(user.getSource())) {
			if (qqList.size() <= MAX_RESULT_SIZE) {
				qqList.add(user);
			}
		} else {
			if (qbList.size() <= MAX_RESULT_SIZE) {
				qbList.add(user);
			}
		}
	}
}
