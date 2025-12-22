package com.chen.service.impl;

import com.chen.mapper.OperateLogMapper;
import com.chen.pojo.PageResult;
import com.chen.pojo.entity.OperateLog;
import com.chen.pojo.entity.User;
import com.chen.pojo.vo.UserVO;
import com.chen.service.LogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private OperateLogMapper operateLogMapper;

    @Override
    public PageResult<List<OperateLog>> queryLog(Integer pageNum, Integer pageSize) {

        PageHelper.startPage(pageNum, pageSize);

        List<OperateLog> operateLogs = operateLogMapper.queryAllLogs();
        PageInfo<OperateLog> page = new PageInfo<>(operateLogs);


        return new PageResult<>(page.getList(), page.getTotal());
    }
}
