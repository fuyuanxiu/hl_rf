package com.web.report.service;

import com.app.base.data.ApiResponseResult;

import java.util.List;

import org.springframework.data.domain.PageRequest;

import javax.servlet.http.HttpServletResponse;

public interface ReportService {

    public List<Object> getTreeList(String factory, String company, String in_str, String usercode, String ip, int page, int size) throws Exception;
 
    public ApiResponseResult getMaterialsList(String keyword,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getQualityList(String keyword,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getDeviceList(String keyword,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getFixtureList(String keyword,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getHcfrList(String keyword,String ptype,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getFRList(String keyword,String stime,String etime,String ptype,  PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getOCVList(String prc_name,String keyword,String stime,String etime,String lineno,String  pmodel,PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getOCV2List(String keyword,String stime,String etime,String lineno, String  pmodel, PageRequest pageRequest) throws Exception;
    
    public ApiResponseResult getLineList() throws Exception;

    public ApiResponseResult getJYList(String keyword, String stime,String etime,PageRequest pageRequest) throws Exception;

    public ApiResponseResult getKCList(String keyword, PageRequest pageRequest) throws Exception;

    public ApiResponseResult getBCPList(String keyword, PageRequest pageRequest) throws Exception;

    public ApiResponseResult getCom1List(String prc_name,String pname,String keyword,String stime,String etime, PageRequest pageRequest) throws Exception;
   
    public ApiResponseResult getComList(String prc_name,String pname,String stime,String etime, PageRequest pageRequest) throws Exception;

    public ApiResponseResult getExcel(String keyword, HttpServletResponse response) throws Exception;
}
