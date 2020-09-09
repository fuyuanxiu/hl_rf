package com.web.app.service;

import com.app.base.data.ApiResponseResult;

public interface DeviceInventoryService {

    //根据工号获取判断用户权限
    public ApiResponseResult judgeUserPower(String usercode) throws Exception;

    //判断盘点区域是否正确
    public ApiResponseResult judgeArea(
    		String usercode,
    		String area,
    		String right_area,//被授权的区域
    		String check_id//盘点单号
    		) throws Exception;
    
  //判断盘点设备
    public ApiResponseResult judgeDevice(
    		String usercode,
    		String area,//当前区域
    		String check_id,//盘点单号
    		String eq_code//设备编号
    		) throws Exception;
    //保存设备盘点记录
    public ApiResponseResult sumbitInventory(
    		String usercode,
    		String area,//当前区域
    		String check_id,//盘点单号
    		String eq_code//设备编号
    		) throws Exception;
}
