package com.example.quota.service;

import com.example.quota.model.Quota;

import java.util.List;

public interface QuotaService {

    List<Quota> getQuotasByUser(Long userId);
    List<Quota> getAllQuotas();

    Quota getQuotaByUser(Long userId, String type);
    Quota getQuotaById(Long id);

    Quota create(Long userId, String type, Double total);
    Boolean delete(Long id);

    Boolean addUserQuota(Long id, Double count);
    Boolean reduceUserQuota(Long id, Double count);

}
