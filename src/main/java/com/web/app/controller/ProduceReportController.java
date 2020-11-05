package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "生产报工")
@RestController
@RequestMapping(value = "/produce")
public class ProduceReportController extends WebController {
	@Autowired
	private ProduceReportService createService;

	@ApiOperation(value = "获取工单信息", notes = "获取工单信息")
	@RequestMapping(value = "/getProduceList", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getProduceList(@RequestParam(value = "usercode") String usercode) {
		try {
			return createService.getProduceList(usercode);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "执行报工", notes = "执行报工")
	@RequestMapping(value = "/produceReport", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult produceReport(@RequestParam(value = "usercode") String usercode, // 用户ID
			@RequestParam(value = "factory") String factory, // 工厂编号
			@RequestParam(value = "company") String company, // 公司编号
			@RequestParam(value = "plan_id") String plan_id, // 排产计划ID
			@RequestParam(value = "proc") String proc, // 工序编号
			@RequestParam(value = "bg_Qty") String bg_Qty, // 报工数量
			@RequestParam(value = "unqu_Qty") String unqu_Qty, // 不良品数量
			@RequestParam(value = "qu_Qty") String qu_Qty, // 良品数量
			@RequestParam(value = "eqCODE") String eqCODE)// 设备编码
	{
		try {
			return createService.produceReport(usercode, factory, company, plan_id, proc, bg_Qty, unqu_Qty, qu_Qty,
					eqCODE);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "获取报工信息详情", notes = "获取报工信息详情")
	@RequestMapping(value = "/getReportRecord", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult getReportRecord(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "plan_ID") String plan_ID, // 工厂编号
			@RequestParam(value = "proc") String proc, // 公司编号
			@RequestParam(value = "eq_CODE") String eq_CODE// 排产计划ID
	) {
		try {
			return createService.getReportRecord(usercode, plan_ID, proc, eq_CODE);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "修改报工记录", notes = "修改报工记录")
	@RequestMapping(value = "/updateReportRecord", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult updateReportRecord(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "plan_ID") String plan_ID, // 排产计划 ID
			@RequestParam(value = "report_ID") String report_ID, // 报工明细 ID
			@RequestParam(value = "bg_QTY") String bg_QTY, // 报工数
			@RequestParam(value = "unqua_Qty") String unqua_Qty, // 不合格数
			@RequestParam(value = "qua_Qty") String qua_Qty// 合格数
	) {
		try {
			return createService.updateReportRecord(usercode, plan_ID, report_ID, bg_QTY, unqua_Qty, qua_Qty);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}

	@ApiOperation(value = "暂停报工", notes = "暂停报工")
	@RequestMapping(value = "/suspendReport", method = RequestMethod.POST, produces = "application/json")
	public ApiResponseResult suspendReport(@RequestParam(value = "usercode") String usercode,
			@RequestParam(value = "plan_ID") String plan_ID// 排产计划 ID
	) {
		try {
			return createService.suspendReport(usercode, plan_ID);
		} catch (Exception e) {
			e.printStackTrace();
			return ApiResponseResult.failure(e.toString());
		}
	}
}
