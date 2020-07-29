package com.web.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceBgService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "生产报工")
@RestController
@RequestMapping(value= "/produce")
public class ProduceBgController extends WebController {
	@Autowired
    private ProduceBgService createService;

    @ApiOperation(value = "根据工单号获取工单信息", notes = "根据工单号获取工单信息")
    @RequestMapping(value = "/getProduceList", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult getProduceList(@RequestParam(value = "usercode") String usercode){
        try{
            return createService.getProduceList(usercode);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "添加生产报工数据", notes = "添加生产报工数据")
    @RequestMapping(value = "/produceReport", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult produceReport(
    		@RequestParam(value = "usercode") String usercode,//用户ID
    		@RequestParam(value = "factory") String factory,//工厂编号
    		@RequestParam(value = "company") String company,//公司编号
    		@RequestParam(value = "plan_id") String plan_id,//排产计划ID
    		@RequestParam(value = "proc") String proc,//工序编号
    		@RequestParam(value = "bg_Qty") String bg_Qty,//报工数量
    		@RequestParam(value = "unqu_Qty") String unqu_Qty,//不良品数量
    		@RequestParam(value = "qu_Qty") String qu_Qty,//良品数量
    		@RequestParam(value = "eqCODE") String eqCODE)//设备编码
    {
        try{
            return createService.produceReport(usercode,factory,
            		company,plan_id,proc,bg_Qty,
            		unqu_Qty,qu_Qty,eqCODE);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
}
