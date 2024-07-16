package com.example.quota.service;

import com.example.quota.dao.QuotaMapper;
import com.example.quota.model.Quota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.example.quota.constant.BaseConstant.Status_Available;
import static com.example.quota.constant.BaseConstant.Status_Deleted;

@Service
public class QuotaServiceImpl implements QuotaService {

    @Autowired
    private QuotaMapper quotaMapper;

    @Override
    public List<Quota> getQuotasByUser(Long userId) {
        return quotaMapper.findByUserId(userId);
    }

    @Override
    public List<Quota> getAllQuotas() {
        return quotaMapper.find();
    }

    @Override
    public Quota getQuotaByUser(Long userId, String type) {
        return quotaMapper.findByUserIdAndType(userId, type);
    }

    @Override
    public Quota getQuotaById(Long id) {
        return quotaMapper.findById(id);
    }

    @Override
    @Transactional(timeout = 2)
    public Quota create(Long userId, String type, Double total) {
        Quota q = new Quota();
        q.setAvail(total);
        q.setUserId(userId);
        q.setTotal(total);
        q.setType(type);
        q.setStatus(Status_Available);
        // 不使用insert on duplicate key
        try {
            quotaMapper.create(q);
        } catch (DuplicateKeyException e) {
            q = quotaMapper.findUnscopedByUserIdAndType(userId, type);
            if (!q.getStatus().equals(Status_Deleted)) { // 禁止重复创建
                return q;
            }
            quotaMapper.updateUserQuotaAvailable(q.getId(), total, total);
        }

        return q;
    }

    @Override
    public Boolean addUserQuota(Long id, Double count) {
        return quotaMapper.addUserQuota(id, count) > 0;
    }

    @Override
    public Boolean reduceUserQuota(Long id, Double count) {
        return quotaMapper.reduceUserQuota(id, count) > 0;
    }


    @Override
    public Boolean delete(Long id) {
        return quotaMapper.deleteUserQuota(id) > 0;
    }
}
