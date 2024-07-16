package com.example.quota.controller;

import com.example.quota.constant.ExceptionMsg;
import com.example.quota.model.ApiResult;
import com.example.quota.model.Quota;
import com.example.quota.service.QuotaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/quota")
public class QuotaController {
    @Autowired
    private QuotaService service;

    @ResponseBody
    @GetMapping({"/get/{userId}/{type}", "/get/{userId}"})
    public ApiResult getUserQuotas(@PathVariable() Long userId, @PathVariable(required = false) String type) {
        // user维度的quota不多，暂不考虑分页
        List<Quota> list = new ArrayList<>();
        if ("".equals(type) || type == null) {
            list = service.getQuotasByUser(userId);
        } else {
            Quota q = service.getQuotaByUser(userId, type);
            if (q!=null){
                list.add(q);
            }
        }
        return ApiResult.buildSuccess(list);
    }


    // create
    @ResponseBody
    @GetMapping("/create/{userId}/{type}/{total}")
    public ApiResult createQuota(@PathVariable Long userId, @PathVariable String type, @PathVariable Double total) {
        if (!(checkUserId(userId) || checkQuotaType(type) || checkQuotaTotal(total))) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        Long ts = System.currentTimeMillis();

        // 禁止重复创建
        Quota quota = service.getQuotaByUser(userId, type);
        if (quota != null && quota.getId() > 0) {
            return ApiResult.buildError(ExceptionMsg.Quota_Has_Existed_Msg, ExceptionMsg.Quota_Has_Existed);
        }

        Quota q = service.create(userId, type, total);
        if (q.getUpdateAt() != null && q.getUpdateAt().getTime() < ts && q.getUpdateAt().getTime() > 0) {
            return ApiResult.buildError(ExceptionMsg.Quota_Has_Existed_Msg, ExceptionMsg.Quota_Has_Existed);
        }
        return ApiResult.buildSuccess(q);
    }

    @ResponseBody
    @GetMapping("/delete/{id}")
    public ApiResult deleteQuota(@PathVariable Long id) {
        if (!checkId(id)) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        Quota quota = service.getQuotaById(id);
        if (quota == null || quota.getId() <= 0) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        if (!(quota.getAvail().equals(quota.getTotal()))) {
            return ApiResult.buildError(ExceptionMsg.Quota_Avail_Has_Used_Msg, ExceptionMsg.Quota_Avail_Has_Used);
        }
        Boolean success = service.delete(quota.getId());
        if (!success) {
            return ApiResult.buildError(ExceptionMsg.Quota_Not_Qualifying_Msg, ExceptionMsg.Quota_Not_Qualifying);
        }
        return ApiResult.buildSuccess();
    }


    @ResponseBody
    @GetMapping("/add/{id}/{count}")
    public ApiResult addQuota(@PathVariable Long id, @PathVariable Double count) {
        if (!(checkId(id) && checkQuotaCount(count))) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        Quota q = service.getQuotaById(id);
        if (q == null || q.getId() <= 0 || q.getAvail() + count > q.getTotal()) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        Boolean success = service.addUserQuota(id, count);
        if (!success) {
            return ApiResult.buildError(ExceptionMsg.Quota_Not_Qualifying_Msg, ExceptionMsg.Quota_Not_Qualifying);
        }
        return ApiResult.buildSuccess();
    }

    @ResponseBody
    @GetMapping("/reduce/{id}/{count}")
    public ApiResult reduceQuota(@PathVariable Long id, @PathVariable Double count) {
        if (!(checkId(id) && checkQuotaCount(count))) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }
        Quota q = service.getQuotaById(id);
        if (q == null || q.getId() <= 0 || q.getAvail() < count) {
            return ApiResult.buildError(ExceptionMsg.Parameter_Invalid_Msg, ExceptionMsg.Parameter_Invalid);
        }

        Boolean success = service.reduceUserQuota(id, count);
        if (!success) {
            return ApiResult.buildError(ExceptionMsg.Quota_Avail_Not_Enough_Msg, ExceptionMsg.Quota_Avail_Not_Enough);
        }
        return ApiResult.buildSuccess();
    }

    private boolean checkUserId(Long userId) {
        return userId != null && userId > 0;
    }

    private boolean checkId(Long Id) {
        return Id != null && Id > 0;
    }

    private boolean checkQuotaCount(Double count){
        return count != null && count >= 0;
    }

    private boolean checkQuotaType(String type) {
        return !"".equals(type);
    }

    private boolean checkQuotaTotal(Double total) {
        return total != null && total >= 0;
    }
}
