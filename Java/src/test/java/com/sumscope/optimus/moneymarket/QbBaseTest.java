package com.sumscope.optimus.moneymarket;

import static org.junit.Assert.*;

//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//
//import javax.annotation.security.RunAs;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.SpringApplicationConfiguration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import org.springframework.test.context.web.WebAppConfiguration;
//
//import com.sumscope.optimus.commons.util.JsonUtil;
//import com.sumscope.optimus.moneymarket.model.dbmodel.UserPreference;
//import com.sumscope.optimus.moneymarket.commons.enums.PreferenceType;
//import com.sumscope.optimus.moneymarket.dao.UserPreferenceDao;
//import com.sumscope.optimus.moneymarket.gateway.http.GatewayServiceHttpImpl;
//import com.sumscope.optimus.moneymarket.service.QbBaseService;


//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringApplicationConfiguration(classes= ServletInitializer.class)
//@WebAppConfiguration
public class QbBaseTest {

	/*@Autowired
	private QbBaseService qbBaseService;
	
	@Autowired
    private UserPreferenceDao userPreferenceDao; 
	
	@Autowired
    private GatewayServiceHttpImpl httpSender;
	
	@Test
	public void getAreaListServiceTest() {
		System.out.println(qbBaseService.getAreaList());
	}
	
	@Test
	public void getAreaListDaoTest() {
		System.out.println(userPreferenceDao.getAreaList());
	}
	
//	@Test
//	public void getAreaListFacafeTest() {
//		Map<String, String> headers = getUserHeaderMap();
//		String response = httpSender.sendHttpRequest("http://127.0.0.1:8888/base/areaList","",headers);
//		//4. 将返回结果同预期结果做比较
//        List<Map<String, Object>> map = JsonUtil.readValue(response,List.class);
//        System.out.println(map);
//	}
	
	@Test
	public void getUserAreaServiceTest() {
		System.out.println(qbBaseService.getUserArea("004687495e234a7682f3bd8c4ff84426"));
	}
	
	@Test
	public void getUserAreaDaoTest() {
		Map<String, Object> map = new HashMap<>();
    	map.put(UserPreferenceDao.USER_ID, "004687495e234a7682f3bd8c4ff84426");
        map.put(UserPreferenceDao.PREFERENCE_TYPE, PreferenceType.AREA);
		System.out.println(userPreferenceDao.isRecordExist(map).getPreferenceValue());
	}
	
	@Test
	public void setUserAreaServiceTest() {
		UserPreference userPreference = new UserPreference();
		userPreference.setPreferenceType(PreferenceType.AREA);
		userPreference.setPreferenceValue("['北京','上海','陕西','新泽西']");
		userPreference.setUserId("004687495e234a7682f3bd8c4ff84426");
		System.out.println(qbBaseService.setUserArea(userPreference));
	}
	
	@Test
	public void setUserAreaDaoTest() {
//		String id=UUID.randomUUID().toString();
//        id = id.replace("-","");
		UserPreference userPreference = new UserPreference();
//		userPreference.setId(id);
		userPreference.setPreferenceType(PreferenceType.AREA);
		userPreference.setPreferenceValue("['北京','上海','陕西','广东']");
		userPreference.setUserId("004687495e234a7682f3bd8c4ff84426");
		System.out.println(userPreferenceDao.updateUserPreference(userPreference));
//		System.out.println(userPreferenceDao.insertUserPreference(userPreference));
	}
	
	private Map<String, String> getUserHeaderMap() {
        Map<String,String> headers = new HashMap<>();
        headers.put("username","optimusptest2325");
        headers.put("password","e10adc3949ba59abbe56e057f20f883e");
        return headers;
    }*/

}
