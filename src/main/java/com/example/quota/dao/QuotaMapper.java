package com.example.quota.dao;

import com.example.quota.model.Quota;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface QuotaMapper {
    // crud operate

    @Select("SELECT * FROM quota WHERE user_id = #{userId} and `status` = 'Available'")
    List<Quota> findByUserId(Long userId);

    @Select("SELECT * FROM quota WHERE `status` = 'Available'")
    List<Quota> find();

    @Select("SELECT * FROM quota WHERE user_id = #{userId} and `type` = #{type} and `status` = 'Available'")
    Quota findByUserIdAndType(Long userId, String type);

    @Select("SELECT * FROM quota WHERE user_id = #{userId} and `type` = #{type}")
    Quota findUnscopedByUserIdAndType(Long userId, String type);

    @Select("SELECT * FROM quota WHERE id = #{id} and `status` = 'Available'")
    Quota findById(Long id);


    @Update("UPDATE quota SET avail = avail + #{count} WHERE id = #{id}  and `status` = 'Available' and total >= avail + #{count}")
    int addUserQuota(@Param("id") Long id, @Param("count") double count);

    @Update("UPDATE quota SET avail = avail - #{count} WHERE id = #{id} and avail >= #{count}  and `status` = 'Available'")
    int reduceUserQuota(@Param("id") Long id, @Param("count") double count);


    @Update("UPDATE quota SET `status` =  'Deleted' WHERE id = #{id} and avail = total")
    int deleteUserQuota(@Param("id") Long id);


    // create or re-create
    @Insert("INSERT INTO quota(user_id, `type`, avail, total, `status`) " +
            "VALUES(#{userId}, #{type}, #{avail}, #{total}, #{status}) ")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int create(Quota quota);

    @Update("UPDATE quota SET `status` = 'Available', total = #{total}, avail = #{avail} WHERE id = #{id} and `status` = 'Deleted'")
    int updateUserQuotaAvailable(@Param("id") Long id, @Param("total") Double total, @Param("avail") Double avail);
}
