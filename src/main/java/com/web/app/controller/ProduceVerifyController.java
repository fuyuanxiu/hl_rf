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
@RequestMapping(value= "/produceVerify")
public class ProduceVerifyController extends WebController {
	@Autowired
    private ProduceVerifyService createService;

    @ApiOperation(value = "根据工单号获取工单信息", notes = "根据工单号获取工单信息")
    @RequestMapping(value = "/getProduceVerifyList", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult getProduceVerifyList(@RequestParam(value = "usercode") String usercode){
        try{
            return createService.getProduceVerifyList(usercode);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "获取生产审核数据", notes = "获取生产审核数据")
    @RequestMapping(value = "/getProduceVerifyDetail", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult produceReport(
    		@RequestParam(value = "usercode") String usercode,//用户ID
    		@RequestParam(value = "proc") String proc,//工序
    		@RequestParam(value = "workCenter") String workCenter,//工作中心
    		@RequestParam(value = "taskNo") String taskNo,//工单号
    		@RequestParam(value = "eq_code") String eq_code)//设备编号
    {
        try{
            return createService.getProduceVerifyDetail(usercode,proc,
            		workCenter,taskNo,eq_code);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
}
