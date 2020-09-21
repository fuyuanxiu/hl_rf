package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.DevRepairService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "设备维修")
@RestController
@RequestMapping(value = "/dev_repair")
public class DevRepairController extends WebController  {
	@Autowired
	private DevRepairService devRepairService;
	
	@ApiOperation(value = "获取设备报修申请单列表及明细", notes = "获取设备报修申请单列表及明细")
	@RequestMapping(value = "/getRepairList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getRepairList(@RequestParam(value = "usercode") String usercode) {
		try {
			return devRepairService.getRepairList(usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "响应报修申请单", notes = "响应报修申请单")
	@RequestMapping(value = "/respond", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult respond(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "eq_code") String eq_code,
			@RequestParam(value = "cause") String cause,
			@RequestParam(value = "repair_plan") String repair_plan) {
		try {
			return devRepairService.respond(usercode,id,eq_code,cause,repair_plan);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "完成报修申请单", notes = "完成报修申请单")
	@RequestMapping(value = "/complete", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult complete(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "content") String content,
			@RequestParam(value = "results") String results) {
		try {
			return devRepairService.complete(usercode,id,content,results);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
}
