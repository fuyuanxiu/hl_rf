package com.web.app.controller;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.web.app.service.DeviceInventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(description = "设备盘点")
@RestController
@RequestMapping(value= "/device_inventory")
public class DeviceInventoryController extends WebController {
    @Autowired
    private DeviceInventoryService deviceService;

    @ApiOperation(value = "根据工号获取盘点权限", notes = "查看用户盘点权限")
    @RequestMapping(value = "/judgeUserPower", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult judgeUserPower(@RequestParam(value = "usercode") String usercode){
        try{
            return deviceService.judgeUserPower(usercode);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "判断盘点区域&时间", notes = "判断盘点区域&时间")
    @RequestMapping(value = "/judgeArea", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult judgeArea(@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "area") String area,
    		@RequestParam(value = "right_area") String right_area,
    		@RequestParam(value = "check_id") String check_id){
        try{
            return deviceService.judgeArea(usercode,area,right_area,check_id);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
    @ApiOperation(value = "判断盘点设备", notes = "判断盘点设备")
    @RequestMapping(value = "/judgeDevice", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult judgeDevice(@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "area") String area,
    		@RequestParam(value = "check_id") String check_id,
    		@RequestParam(value = "eq_code") String eq_code){
        try{
            return deviceService.judgeDevice(usercode,area,check_id,eq_code);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    @ApiOperation(value = "提交盘点", notes = "提交盘点")
    @RequestMapping(value = "/sumbitInventory", method = RequestMethod.POST, produces = "application/json")
    public ApiResponseResult sumbitInventory(@RequestParam(value = "usercode") String usercode,
    		@RequestParam(value = "area") String area,
    		@RequestParam(value = "check_id") String check_id,
    		@RequestParam(value = "eq_code") String eq_code){
        try{
            return deviceService.sumbitInventory(usercode,area,check_id,eq_code);
        }catch (Exception e){
            e.printStackTrace();
            return ApiResponseResult.failure(e.toString());
        }
    }
    
}
