package com.web.app.controller;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.StartCheckService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "开工授权")
@RestController
@RequestMapping(value= "/start")
public class StartCheckController extends WebController {
    @Autowired
    private StartCheckService createService;

    @ApiOperation(value = "根据工单号获取待开工的工单信息", notes = "根据工单号获取待开工的工单信息")
    @RequestMapping(value = "/getStartList", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult getStartList(@RequestParam(value = "usercode") String usercode){
        try{
            return createService.getStartList(usercode);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "执行开工授权", notes = "执行开工授权")
    @RequestMapping(value = "/addStartCheck", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult addStartCheck(
    		@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "proc") String proc,
    		@RequestParam(value = "workCenter") String workCenter,
    		@RequestParam(value = "taskNo") String taskNo,
    		@RequestParam(value = "eq_code") String eq_code,
    		@RequestParam(value = "staffNoInfo") String staffNoInfo,
    		@RequestParam(value = "eq_id_Info") String eq_id_Info,
    		@RequestParam(value = "pid") String pid
    		){
        try{
            return createService.addStartCheck(usercode,proc,workCenter,taskNo,eq_code,
    				staffNoInfo,eq_id_Info,pid);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
}
