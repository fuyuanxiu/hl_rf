package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface DevReHandleService {
	//获取设备报修申请单列表及明细
	public ApiResponseResult getList(String usercode) throws Exception;
	//委派报修申请单
	public ApiResponseResult assign(String usercode,String id,String repair_man) throws Exception;
	//转派报修申请单
	public ApiResponseResult transfer(String usercode,String id,String repair_man) throws Exception;
	//提供辅助信息
	public ApiResponseResult getAuxiliaryInfo() throws Exception;
}
