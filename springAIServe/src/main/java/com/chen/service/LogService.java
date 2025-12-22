package com.chen.service;

import com.chen.pojo.PageResult;
import com.chen.pojo.entity.OperateLog;
import com.chen.pojo.vo.UserVO;

import java.util.List;

public interface LogService {

    PageResult<List<OperateLog>> queryLog(Integer pageNum, Integer pageSize);
}
