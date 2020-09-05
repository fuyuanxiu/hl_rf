package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceVerifyService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "报工审核")
@RestController
@RequestMapping(value = "/produce_verify")
public class ProduceVerifyController extends WebController {
	@Autowired
	private ProduceVerifyService createService;

	@ApiOperation(value = "根据工单号获取工单信息", notes = "根据工单号获取工单信息")
	@RequestMapping(value = "/getProduceVerifyList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getProduceVerifyList(@RequestParam(value = "usercode") String usercode) {
		try {
			return createService.getProduceVerifyList(usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "获取生产审核数据", notes = "获取生产审核数据")
	@RequestMapping(value = "/getProduceVerifyDetail", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getProduceVerifyDetail(@RequestParam(value = "usercode") String usercode, // 用户ID
			@RequestParam(value = "proc") String proc, // 工序
			@RequestParam(value = "taskNo") String taskNo, // 工单号
			@RequestParam(value = "eq_code") String eq_code)// 设备编号
	{
		try {
			return createService.getProduceVerifyDetail(usercode, proc, taskNo, eq_code);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "提交审核信息", notes = "提交审核信息")
    @RequestMapping(value = "/sumbitProduceVerify", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult sumbitProduceVerify(
    		@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "reportInfo") String reportInfo){
        try{
            return createService.sumbitProduceVerify(usercode,reportInfo);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }

	@ApiOperation(value = "个人报工明细", notes = "获取个人明细")
	@RequestMapping(value = "/getProduceRecordDetail", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getProduceRecordDetail(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "plan_id") String plan_id, @RequestParam(value = "role") String role) {
		try {
			return createService.getProduceRecordDetail(usercode, plan_id, role);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "提交个人报工明细", notes = "提交个人明细修改")
	@RequestMapping(value = "/sumbitProduceRecordDetail", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult sumbitProduceRecordDetail(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "proc") String proc, @RequestParam(value = "task_no") String task_no,
			@RequestParam(value = "eq_code") String eq_code, @RequestParam(value = "reportInfo") String reportInfo,
			@RequestParam(value = "role") String role) {
		try {
			return createService.sumbitProduceRecordDetail(usercode, proc, task_no, eq_code, reportInfo, role);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
}
