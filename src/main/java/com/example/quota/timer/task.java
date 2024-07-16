package com.example.quota.timer;

import com.example.quota.controller.QuotaController;
import com.example.quota.model.ApiResult;
import com.example.quota.model.Quota;
import com.example.quota.service.QuotaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.quota.constant.ExceptionMsg.Internal_Error;

@Component
@Slf4j
public class task {
    @Autowired
    private QuotaController controller;

    @Autowired
    private QuotaService service;

    private Integer nThreads = 10;

    private ExecutorService executorService = Executors.newFixedThreadPool(nThreads);

    private Random random = new Random();


    @Scheduled(fixedRate = 10000) // 10s
    public void simulateUserOperations() {
        List<Quota> quotas = service.getAllQuotas();
        if (quotas.isEmpty()) {
            return;
        }

        // 模拟多用户操作
        // 从定时任务调度的controller方法，没有request信息
        for (int i = 0; i < nThreads; i++) {
            executorService.submit(() -> {
                Quota quota = quotas.get(random.nextInt(quotas.size()));
                double count = random.nextDouble() * quota.getAvail();
                boolean isAdd = random.nextBoolean();
                ApiResult result;

                if (isAdd) {
                    result = controller.addQuota(quota.getId(), count);
                    logMsg("Add", quota, count, result);

                } else {
                    result = controller.reduceQuota(quota.getId(), count);
                    logMsg("Reduce", quota, count, result);
                }

            });
        }
    }

    private void logMsg(String  operate, Quota quota, Double count,ApiResult result){
        String str = "[schedule operate] %s userId: %s, quota type: %s, count: %f,  result code: %d".
                formatted(operate, quota.getUserId(), quota.getType(), count, result.getCode());
        if (result.getCode() == 0){
            log.info(str);
        }else if (result.getCode() == Internal_Error){
            log.error(str);
        }else {
            log.warn(str);
        }
    }

}