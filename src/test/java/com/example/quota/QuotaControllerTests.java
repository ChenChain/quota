package com.example.quota;

import com.example.quota.constant.ExceptionMsg;
import com.example.quota.dao.QuotaMapper;
import com.example.quota.model.ApiResult;
import com.example.quota.model.Quota;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import java.util.ArrayList;
import java.util.List;

import static com.example.quota.constant.BaseConstant.Status_Deleted;
import static com.example.quota.constant.ExceptionMsg.Parameter_Invalid;
import static com.example.quota.constant.ExceptionMsg.Quota_Avail_Has_Used;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.example.quota.constant.BaseConstant.Status_Available;

// use H2 db

@SpringBootTest
@AutoConfigureMockMvc
public class QuotaControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuotaMapper mapper; // use for init datas

    @Autowired
    private JdbcTemplate jdbcTemplate; // only use for delete table

    Long userID1 = 1L;
    Long userID2 = 2L;
    Long userID3 = 3L;
    Long userID4 = 4L;

    String dollarType = "dollar";
    String rmbType = "rmb";
    String fakeType = "fake";

    private ApiResult baseMvcPerform(String url) throws Exception {
        MvcResult result = mockMvc.perform(get(url))
                .andExpect(status().isOk()).andReturn();
        ApiResult apiResult= new ObjectMapper().readValue(result.getResponse().getContentAsString(), ApiResult.class);
        return apiResult;
    }

    private List<Quota> convertApiResultData(ApiResult r){
        if (r.getData() == null || r.getData().toString().equals("[null]") ||
                r.getData().toString().equals("")){
            return new ArrayList<Quota>();
        }

        ObjectMapper mapper = new ObjectMapper();
        List<Quota> list = mapper.convertValue(r.getData(), new TypeReference<List<Quota>>() { });
        return list;
    }

    Quota q1 = new Quota();
    Quota q2 = new Quota();
    Quota q3 = new Quota();
    Quota q4 = new Quota();

    @BeforeEach
    public void setup() {
        // not use
        q1.setAvail(10D);
        q1.setTotal(10D);
        q1.setType(dollarType);
        q1.setStatus(Status_Available);
        q1.setUserId(userID1);

        // used
        q2.setAvail(0D);
        q2.setTotal(10D);
        q2.setType(rmbType);
        q2.setStatus(Status_Available);
        q2.setUserId(userID1);

        // not used
        q3.setAvail(10D);
        q3.setTotal(10D);
        q3.setType(dollarType);
        q3.setStatus(Status_Available);
        q3.setUserId(userID2);

        // deleted
        q4.setAvail(10D);
        q4.setTotal(10D);
        q4.setType(dollarType);
        q4.setStatus(Status_Deleted);
        q4.setUserId(userID3);

       mapper.create(q1);
       mapper.create(q2);
       mapper.create(q3);
       mapper.create(q4);
       System.out.println("db data has been setup");
    }

    @AfterEach
    public void after(){
        jdbcTemplate.execute("delete from quota");
        System.out.println("db data has been deleted");

    }


    private void testGetQuotaMould(Long userID, String type, Integer expectDataSize) throws Exception {
        String url =  "/quota/get/" + userID + "/" + type;
        ApiResult result = baseMvcPerform(url);
        List<Quota> l = convertApiResultData(result);
        assertEquals(0, result.getCode());
        assertEquals(expectDataSize, l.size());
        // check user id and type
        for (Quota q: l) {
            assertEquals(userID, q.getUserId());
            if (!"".equals(type)){
                assertEquals(type, q.getType());
            }
        }
    }

    private void testGetQuotaMould(Long userID, String type, Double avail, Double total) throws Exception {
        String url =  "/quota/get/" + userID + "/" + type;
        ApiResult result = baseMvcPerform(url);
        List<Quota> l = convertApiResultData(result);
        assertEquals(0, result.getCode());
        assertEquals(1, l.size());
        assertEquals(avail, l.get(0).getAvail());
        assertEquals(total, l.get(0).getTotal());
    }

    // getQuota
    @Test
    public void getQuota() throws Exception {
        testGetQuotaMould(userID1, dollarType, 1);
        testGetQuotaMould(userID1, "", 2);
        testGetQuotaMould(userID1, fakeType, 0);

        testGetQuotaMould(userID3, dollarType, 0);
        testGetQuotaMould(userID4, "", 0);
    }


    private void testCreateQuotaMould(Long userID, String type, Double total, Integer expectCode) throws Exception {
        String url = "/quota/create/" + userID + "/" + type + "/" + total;
        ApiResult result = baseMvcPerform(url);
        assertEquals(expectCode, result.getCode());
        if (result.getCode()!=0){
            return;
        }

        // 查询椒盐一下
        testGetQuotaMould(userID, type, total, total);
    }

    @Test
    public void createQuota() throws Exception{
        testCreateQuotaMould(userID1, dollarType, 10D, ExceptionMsg.Quota_Has_Existed);

        testCreateQuotaMould(100L, dollarType, 10D, 0);
        testCreateQuotaMould(100L, rmbType, 10D, 0);

        testCreateQuotaMould(userID3, dollarType, 10D, 0);
    }


    private void testDeleteQuotaMould(Quota q, Integer expectCode) throws Exception{
        String url ="/quota/delete/" + q.getId();
        ApiResult result = baseMvcPerform(url);
        assertEquals(expectCode, result.getCode());
        if (result.getCode()!=0){
            return;
        }
        testGetQuotaMould(q.getUserId(), q.getType(), 0);
    }

    @Test
    public void deleteQuota() throws Exception{
        Quota notExistedQuota = new Quota();
        notExistedQuota.setUserId(199999999L);
        notExistedQuota.setType(rmbType);
        notExistedQuota.setId(12345L);
        testDeleteQuotaMould(notExistedQuota, Parameter_Invalid);

        testDeleteQuotaMould(q1, 0);

        testDeleteQuotaMould(q2, Quota_Avail_Has_Used);
    }


    private void testAddQuotaMould(Quota quota, Double count, Integer expectCode) throws Exception {
        String url = "/quota/add/" + quota.getId() + "/" + count;
        ApiResult result = baseMvcPerform(url);
        assertEquals(expectCode, result.getCode());
        if (result.getCode() != 0){
            return;
        }

        testGetQuotaMould(quota.getUserId(), quota.getType(),quota.getAvail() + count ,quota.getTotal());
    }
    @Test
    public void addQuota() throws Exception{
        // normal operate
        testAddQuotaMould(q2, 2D, 0);

        // not exist
        testAddQuotaMould(q4, 2D, Parameter_Invalid);

        // exceed total
        testAddQuotaMould(q2, 10000D, Parameter_Invalid);

        // invalid count
        testAddQuotaMould(q2, -10000D, Parameter_Invalid);

    }

    private void  testReduceQuotaMould(Quota quota, Double count, Integer expectCode) throws Exception{
        String url = "/quota/reduce/" + quota.getId() + "/" + count;
        ApiResult result = baseMvcPerform(url);
        assertEquals(expectCode, result.getCode());
        if (result.getCode() != 0){
            return;
        }

        testGetQuotaMould(quota.getUserId(), quota.getType(),quota.getAvail() - count ,quota.getTotal());
    }

    @Test
    public void reduceQuota() throws Exception{
        // normal operate
        testReduceQuotaMould(q1, 2D, 0);

        // not exist
        testReduceQuotaMould(q4, 2D, Parameter_Invalid);

        // exceed avail
        testReduceQuotaMould(q1, 10000D, Parameter_Invalid);

        // invalid count
        testReduceQuotaMould(q1, -10000D, Parameter_Invalid);
    }

}
