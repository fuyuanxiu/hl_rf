package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface ProduceVerifyService {
	 //获取工单信息
    public ApiResponseResult getProduceVerifyList(String usercode) throws Exception;
    //根据工单号获取报工信息
    public ApiResponseResult getProduceVerifyDetail(
    		String usercode,
    		String proc,
    		String workCenter,
    		String taskNo,
    		String eq_code) throws Exception;
}
