package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface DevReApplyService {
	// 获取本人的设备报修单
	 public ApiResponseResult getApplyList(String usercode) throws Exception;
	// 报修详情页的进度信息----通用
	 public ApiResponseResult getScheduleList(String usercode,String apply_id) throws Exception;
	// 新增报修申请（提交报修申请）
	 public ApiResponseResult add(String usercode,String eq_code,String fault_des,String breakType) throws Exception;
	// 取消已提交的报修申请
	 public ApiResponseResult cannel(String usercode,String apply_id) throws Exception;
	// 完结已被处理的保修申请
	 public ApiResponseResult finish(String usercode,String apply_id) throws Exception;
}
