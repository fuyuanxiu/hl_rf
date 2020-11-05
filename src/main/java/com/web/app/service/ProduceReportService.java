package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface ProduceReportService {
	
	 //根据工单号获取待生产工单信息
    public ApiResponseResult getProduceList(String usercode) throws Exception;
    
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
    
    //查询报工明细列表 
    public ApiResponseResult getReportRecord(
    		String usercode,
    		String plan_ID,
    		String proc,
    		String eq_CODE
    		) throws Exception;
    //修改报工明细
    public ApiResponseResult updateReportRecord(
    		String usercode, //用户ID
    		String plan_ID,//  排产计划 ID
    		String report_ID,//报工明细 ID
    		String bg_QTY,//  报工数量
    		String unqua_Qty,  // 不合格品数量
    		String qua_Qty//合格数量 
    		) throws Exception;
    
    //暂停报工
    public ApiResponseResult suspendReport(String usercode,String plan_id)throws Exception;
}
