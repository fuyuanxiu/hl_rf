package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface ProduceVerifyService {
	 //获取工单信息
    public ApiResponseResult getProduceVerifyList(String usercode) throws Exception;
    //根据工单号获取报工信息
    public ApiResponseResult getProduceVerifyDetail(
    		String usercode,
    		String proc,
    		String taskNo,
    		String eq_code,String tcode) throws Exception;
    //提交审核信息
    public ApiResponseResult sumbitProduceVerify(
    		String usercode,
    		String proc,
    		String task_no,
    		String eq_code,
    		String role,String tcode
    		) throws Exception;
    //获取个人报工详情的明细
    public ApiResponseResult getProduceRecordDetail(
    		String usercode,String plan_id,String role,String aid
    		) throws Exception;
    
    //提交修改过的报工明细
    public ApiResponseResult sumbitProduceRecordDetail(
    		String usercode,
    		String proc,
    		String task_no,
    		String eq_code,
    		String reportInfo,
    		String role,String tcode
    		) throws Exception;
}

