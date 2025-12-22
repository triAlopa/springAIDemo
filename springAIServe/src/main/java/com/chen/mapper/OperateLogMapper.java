package com.chen.mapper;

import com.chen.pojo.entity.OperateLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OperateLogMapper {
    
    //插入日志数据
    @Insert("insert into tb_ai_log (operate_id, operate_time, class_name, method_name, method_params, return_value, cost_time) " +
            "values (#{operateId}, #{operateTime}, #{className}, #{methodName}, #{methodParams}, #{returnValue}, #{costTime});")
    void insert(OperateLog log);

    @Select("select id, operate_id, operate_time, class_name, method_name, method_params, return_value, cost_time from tb_ai_log ")
    List<OperateLog> queryAllLogs();
}