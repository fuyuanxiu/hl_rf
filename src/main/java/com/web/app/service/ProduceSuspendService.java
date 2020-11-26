package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface ProduceSuspendService {

    //根据工单号获取工单信息
    public ApiResponseResult getList(String usercode) throws Exception;

    //状态变更
    public ApiResponseResult changeStatus(
    		String usercode,
    		String taskNo,
    		String itemNo,
    		String plan_id,
    		String status
    		) throws Exception;
}
