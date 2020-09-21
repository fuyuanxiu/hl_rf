package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.DevReHandleService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "报修委派")
@RestController
@RequestMapping(value = "/dev_handle")
public class DevReHandleController extends WebController  {
	@Autowired
	private DevReHandleService devReHandleService;
	
	@ApiOperation(value = "获取设备报修申请单", notes = "获取设备报修申请单")
	@RequestMapping(value = "/getHandleList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getHandleList(@RequestParam(value = "usercode") String usercode) {
		try {
			return devReHandleService.getHandleList(usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "委派报修申请单", notes = "委派报修申请单")
	@RequestMapping(value = "/assign", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult assign(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "repair_man") String repair_man) {
		try {
			return devReHandleService.assign(usercode,id,repair_man);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "转派报修申请单", notes = "转派报修申请单")
	@RequestMapping(value = "/transfer", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult transfer(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "id") String id,
			@RequestParam(value = "repair_man") String repair_man) {
		try {
			return devReHandleService.transfer(usercode,id,repair_man);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
	
	@ApiOperation(value = "提供辅助信息", notes = "提供辅助信息")
	@RequestMapping(value = "/getAuxiliaryInfo", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getAuxiliaryInfo() {
		try {
			return devReHandleService.getAuxiliaryInfo();
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
}
