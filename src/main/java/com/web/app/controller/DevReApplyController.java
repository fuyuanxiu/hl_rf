package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.DevReApplyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "设备报修")
@RestController
@RequestMapping(value = "/dev_apply")
public class DevReApplyController extends WebController  {
	@Autowired
	private DevReApplyService devReApplyService;
	
	@ApiOperation(value = "获取设备报修单", notes = "获取设备报修单")
	@RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getList(@RequestParam(value = "usercode") String usercode) {
		try {
			return devReApplyService.getApplyList(usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "报修详情页的进度信息", notes = "报修详情页的进度信息")
	@RequestMapping(value = "/getScheduleList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getScheduleList(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "apply_id") String apply_id) {
		try {
			return devReApplyService.getScheduleList(usercode,apply_id);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "新建报修申请单", notes = "新建报修申请单")
	@RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult add(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "eq_code") String eq_code,
			@RequestParam(value = "fault_des") String fault_des,
			@RequestParam(value = "break_type") String break_type) {
		try {
			return devReApplyService.add(usercode,eq_code,fault_des,break_type);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "取消报修申请单", notes = "取消报修申请单")
	@RequestMapping(value = "/cannel", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult cannel(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "apply_id") String apply_id) {
		try {
			return devReApplyService.cannel(usercode,apply_id);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "完结报修申请单", notes = "完结报修申请单")
	@RequestMapping(value = "/finish", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult finish(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "apply_id") String apply_id) {
		try {
			return devReApplyService.finish(usercode,apply_id);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
}
