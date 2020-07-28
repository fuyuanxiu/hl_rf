package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface StartCheckService {

    //根据工单号获取待开工的工单信息
    public ApiResponseResult getStartList(String usercode) throws Exception;

}
