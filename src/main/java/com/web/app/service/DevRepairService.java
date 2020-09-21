package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface DevRepairService {
	//获取设备报修申请单列表及明细。
	public ApiResponseResult getRepairList(String usercode) throws Exception;
	//响应报修申请单
	public ApiResponseResult respond(String usercode,String id,String eq_code,String cause,String repair_plan) throws Exception;
	//完成报修申请单
	public ApiResponseResult complete(String usercode,String id,String content,String results) throws Exception;
}
