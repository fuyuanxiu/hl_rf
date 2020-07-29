package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface ProduceBgService {
	
	 //根据工单号获取待生产工单信息
    public ApiResponseResult getProduceList(String usercode) throws Exception;//方法
    
    //报工数量
    public ApiResponseResult produceReport(
    		String usercode,
    		String factory,
    		String company,
    		String plan_ID,
    		String proc,
    		String bg_Qty,
    		String unqu_Qty,
    		String qu_Qty,
    		String eqCODE) throws Exception;
}
