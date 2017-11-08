package com.sumscope.optimus.moneymarket;

import com.sumscope.optimus.commons.util.JsonUtil;
import com.sumscope.optimus.moneymarket.dao.MmQuoteDao;
import com.sumscope.optimus.moneymarket.gatewayinvoke.GatewayHTTPService;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

/**
 * Created by Administrator on 2016/5/16.
 */
@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(ServletInitializer.class)
@WebIntegrationTest({"application.port=9999", "management.port=0","application.enableAuthorization=true","useMockQMUser=false"})
public class MatrixPriceRequestTest extends MMTest {

    @Autowired
    private MmQuoteDao mmQuoteDao;

    @Autowired
    private GatewayHTTPService httpSender;

    @BeforeClass
    public static void init() {
    }

    @AfterClass
    public static void uninit() {
//        stop();
    }

    @Before
    public void setUp(){
//        不再需要手动新启工作实例, 标签中已经指定为Web的集成测试
//        start();
        reset(mmQuoteDao);
    }

    @After
    public void tearDown(){
    }


    @Test
    public void testMatrixFacade() {
            System.out.println("testMatrixFacade started ...");
            //1. 准备Mock
            when(mmQuoteDao.queryMmQuoteMain(Mockito.any())).thenReturn(Collections.emptyList());
            //2. 准备发送请求的内容和Header
            String jsonBody = "{\n" +
                    "\t\"quote_type\" : null,\n" +
                    "\t\"province\" : []\n" +
                    "}";
            Map<String, String> headers = getUserHeaderMap();
            //3. 发送请求至web server, 并且获取返回结果
            String response = httpSender.sendHttpRequest("http://127.0.0.1:9999/inner/matrix_price",jsonBody,headers);


            //4. 将返回结果同预期结果做比较
            Map map = JsonUtil.readValue(response,Map.class);
            System.out.println(map);
            Assert.assertEquals(0, map.get("return_code"));
    }
    @Test
    public void testMatrixFacade2() {
        Object o = mmQuoteDao.queryMmQuoteWithDetails(Mockito.any());
        System.out.println(o);
    }

    private Map<String, String> getUserHeaderMap() {
        Map<String,String> headers = new HashMap<>();
        headers.put("username","optimusptest2325");
        headers.put("password","123456");
        return headers;
    }
}