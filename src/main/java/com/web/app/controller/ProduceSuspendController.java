package com.web.app.controller;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceSuspendService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "生产停工")
@RestController
@RequestMapping(value= "/suspend")
public class ProduceSuspendController extends WebController {
    @Autowired
    private ProduceSuspendService createService;

    @ApiOperation(value = "根据工单号获取工单信息", notes = "根据工单号获取工单信息")
    @RequestMapping(value = "/getList", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult getList(@RequestParam(value = "usercode") String usercode){
        try{
            return createService.getList(usercode);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "生产状态切换", notes = "生产状态切换")
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult changeStatus(
    		@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "taskNo") String taskNo,
    		@RequestParam(value = "itemNo") String itemNo,
    		@RequestParam(value = "plan_id") String plan_id,
    		@RequestParam(value = "status") String status
    		){
        try{
            return createService.changeStatus(usercode,taskNo,itemNo,plan_id,status);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
}
