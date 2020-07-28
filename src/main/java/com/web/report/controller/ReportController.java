package com.web.report.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.app.base.control.WebController;
import com.app.base.data.ApiResponseResult;
import com.utils.UserUtil;
import com.web.report.service.ReportService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "批次追溯")
@CrossOrigin
@ControllerAdvice
//@RestController
@Controller
@RequestMapping(value = "/report")
public class ReportController extends WebController {

    @Autowired
    private ReportService reportService;
    
    /**
	 * 权限列表
	 * @return ok/fail
	 */
	@RequestMapping(value = "/toList", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView permList() {
		String method = "/report/toList";String methodName ="批次追溯列表";
		//getSysLogService().debug(method,methodName);
		logger.debug(method);
		ModelAndView mav = new ModelAndView("/report/batch");
		try {
			String barcode = "1C200327700010001";
			//String factory, String company, String in_str, String usercode, String ip, int page, int size
			//List<Object> permList = reportService.getTreeList("10000","1000",barcode,UserUtil.getSessionUser().getFcode(),"123",5,1);
			//logger.debug("批次追溯列表查询=permList:" + permList);
			//getSysLogService().success(method,methodName,permList);
			mav.addObject("permList", reportService.getTreeList("10000","1000",barcode,UserUtil.getSessionUser().getFcode(),"123",5,1));
			mav.addObject("msg", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("权限查询异常！", e);
			getSysLogService().error(method,methodName,e.toString());
		}
		return mav;
	}
	
	@RequestMapping(value = "/toFrhcDetail", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toFrhcDetail(HttpServletRequest request) {
		String method = "/report/toFrhcDetail";String methodName ="批次追溯列表";
		//getSysLogService().debug(method,methodName);
		logger.debug(method);
		ModelAndView mav = new ModelAndView("/report/hcfr");
		try {
			
			String keyword = request.getParameter("keyword");
			System.out.println(keyword);
			mav.addObject("msg", "ok");
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("权限查询异常！", e);
			getSysLogService().error(method,methodName,e.toString());
		}
		return mav;
	}
	@RequestMapping(value = "/toPZ", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toPZ(String p) {
		String method = "/report/toPZ";String methodName ="品质信息";
		ModelAndView mav=new ModelAndView();
		mav.addObject("pname", p);
		mav.setViewName("/report/pz");//返回路径
		return mav;
	}
	
	@RequestMapping(value = "/toInout", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toInout(String p) {
		String method = "/report/toInout";String methodName ="投入产出列表";
		ModelAndView mav=new ModelAndView();
		mav.addObject("pname", p);
		mav.setViewName("/report/inout");//返回路径
		return mav;
	}
	
	@RequestMapping(value = "/toInoutDetail", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toInoutDetail(String p,String stime,String etime,String task_no) {
		String method = "/report/toInout";String methodName ="投入产出列表";
		ModelAndView mav=new ModelAndView();
		mav.addObject("pname", p);
		mav.addObject("stime", stime);
		mav.addObject("etime", etime);
		mav.addObject("task_no", task_no);
		mav.setViewName("/report/inout_detail");//返回路径
		return mav;
	}
	
	@RequestMapping(value = "/toTrace")
    public String toTrace(){
        return "/report/trace";
    }
	@RequestMapping(value = "/toFR")
    public String toFR(){
        return "/report/fr";
    }
	@RequestMapping(value = "/toHC")
    public String toHC(){
        return "/report/hc";
    }
	@RequestMapping(value = "/toOCV1")
    public String toOCV1(){
        return "/report/ocv1";
    }
	@RequestMapping(value = "/toOCV2")
    public String toOCV2(){
        return "/report/ocv2";
    }
	@RequestMapping(value = "/toOCV4")
    public String toOCV4(){
        return "/report/ocv4";
    }
	@RequestMapping(value = "/toOCV3")
    public String toOCV3(){
        return "/report/ocv3";
    }
	@RequestMapping(value = "/toHcfr")
    public String toHcfr(){
        return "/report/hcfr";
    }
    @RequestMapping(value = "/toJY")
    public String toJY(){
        return "/report/jy";
    }
    @RequestMapping(value = "/toKC")
    public String toKC(){
        return "/report/kc";
    }
    @RequestMapping(value = "/toBCP")
    public String toBCP(){
        return "/report/bcp";
    }
    //注液结果数据
    @RequestMapping(value = "/toZy")
    public String toZy(){
        return "/report/zy";
    }
    private Map<String, Object> emtyMap(){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", new ArrayList());
        map.put("page", 1);
        map.put("pageSize", 10);
        map.put("total", 0);
        return map;
	}
	
	 @ApiOperation(value = "获取物料信息列表", notes = "获取物料信息列表")
	    @RequestMapping(value = "/getMaterialsList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getMaterialsList(@RequestParam(value = "keyword", required = false) String keyword) {
	        String method = "/report/getMaterialsList";String methodName ="获取物料信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getMaterialsList(keyword, super.getPageRequest(sort));
	            //logger.debug("获取应急处置信息列表=获取物料信息列表:");
	            getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取物料信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取物料信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取品质信息列表", notes = "获取品质信息列表")
	    @RequestMapping(value = "/getQualityList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getQualityList(@RequestParam(value = "keyword", required = false) String keyword) {
	        String method = "/report/getQualityList";String methodName ="获取品质信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getQualityList(keyword, super.getPageRequest(sort));
	            logger.debug("获取品质信息列表=getQualityList:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取品质信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取品质信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取设备人员信息列表", notes = "获取设备人员信息列表")
	    @RequestMapping(value = "/getDeviceList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getDeviceList(@RequestParam(value = "keyword", required = false) String keyword) {
	        String method = "/report/getDeviceList";String methodName ="获取设备人员信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getDeviceList(keyword, super.getPageRequest(sort));
	            logger.debug("获取设备人员信息列表=device:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取设备人员信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取设备人员信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 
	 @ApiOperation(value = "获取工装夹具信息列表", notes = "获取工装夹具信息列表")
	    @RequestMapping(value = "/getFixtureList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getFixtureList(@RequestParam(value = "keyword", required = false) String keyword) {
	        String method = "/report/getFixtureList";String methodName ="获取工装夹具信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getFixtureList(keyword, super.getPageRequest(sort));
	            logger.debug("获取工装夹具信息列表=getFixtureList:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取工装夹具信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取工装夹具信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取化成过程数据信息列表", notes = "获取化成过程数据信息列表")
	    @RequestMapping(value = "/getHcfrList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getHcfrList(@RequestParam(value = "keyword", required = false) String keyword,
	    		@RequestParam(value = "ptype", required = false) String ptype) {
	        String method = "/report/getHcfrList";String methodName ="获取化成过程数据信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getHcfrList(keyword,ptype, super.getPageRequest(sort));
	            logger.debug("获取化成过程数据信息列表=getHcfrList:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取化成过程数据信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取化成过程数据信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取OCV1信息列表", notes = "获取OCV1信息列表")
	    @RequestMapping(value = "/getFRList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getFRList(@RequestParam(value = "keyword", required = false) String keyword,
	    		@RequestParam(value = "stime", required = false) String stime,@RequestParam(value = "etime", required = false) String etime,
	    		@RequestParam(value = "ptype", required = false) String ptype) {
	        String method = "/report/getFRList";String methodName ="获取OCV1信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getFRList(keyword,stime,etime,ptype, super.getPageRequest(sort));
	            logger.debug("获取OCV1信息列表=getOCV1List:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取OCV1信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取OCV1信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取OCV1信息列表", notes = "获取OCV1信息列表")
	    @RequestMapping(value = "/getOCVList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getOCV1ist(@RequestParam(value = "keyword", required = false) String keyword,
	    		@RequestParam(value = "stime", required = false) String stime,@RequestParam(value = "etime", required = false) String etime,
	    		@RequestParam(value = "lineno", required = false) String lineno
	    		,@RequestParam(value = "pmodel", required = false) String pmodel
	    		,@RequestParam(value = "prc_name", required = false) String prc_name) {
	        String method = "/report/getOCVList";String methodName ="获取OCV1信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getOCVList(prc_name,keyword,stime,etime,lineno,pmodel, super.getPageRequest(sort));
	            logger.debug("获取OCV1信息列表=getOCV1List:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取OCV1信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取OCV1信息列表失败！").data(emtyMap());
	        }
	    }
	 
	 @ApiOperation(value = "获取OCV2信息列表", notes = "获取OCV2信息列表")
	    @RequestMapping(value = "/getOCV2List", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getOCV2List(@RequestParam(value = "keyword", required = false) String keyword,
	    		@RequestParam(value = "stime", required = false) String stime,@RequestParam(value = "etime", required = false) String etime,
	    		@RequestParam(value = "lineno", required = false) String lineno
	    		,@RequestParam(value = "pmodel", required = false) String pmodel) {
	        String method = "/report/getOCV2List";String methodName ="获取OCV2信息列表";
	        try {
	            Sort sort = new Sort(Sort.Direction.DESC, "id");
	            ApiResponseResult result = reportService.getOCV2List(keyword,stime,etime,lineno,pmodel, super.getPageRequest(sort));
	            logger.debug("获取OCV2信息列表=getOCV2List:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取OCV2信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取OCV2信息列表失败！");
	        }
	    }
	 
	 @ApiOperation(value = "获取线体信息列表", notes = "获取线体信息列表")
	    @RequestMapping(value = "/getLineList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getLineList() {
	        String method = "/report/getLineList";String methodName ="获取线体信息列表";
	        try {
	            ApiResponseResult result = reportService.getLineList();
	            logger.debug("获取线体信息列表=getLineList:");
	            //getSysLogService().success(method, methodName, null);
	            return result;
	        } catch (Exception e) {
	            e.printStackTrace();
	            logger.error("获取线体信息列表失败！", e);
	            getSysLogService().error(method, methodName, e.toString());
	            return ApiResponseResult.failure("获取线体信息列表失败！").data(emtyMap());
	        }
	    }

    @ApiOperation(value = "获取物料条码交易明细列表", notes = "获取物料条码交易明细列表")
    @RequestMapping(value = "/getJYList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponseResult getJYList(@RequestParam(value = "keyword", required = false) String keyword
    		,@RequestParam(value = "stime", required = false) String stime
    		,@RequestParam(value = "etime", required = false) String etime) {
        String method = "/report/getJYList";String methodName ="获取物料条码交易明细列表";
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            ApiResponseResult result = reportService.getJYList(keyword, stime,etime, super.getPageRequest(sort));
            logger.debug("获取物料条码交易明细列表=getJYList:");
            //getSysLogService().success(method, methodName, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取物料条码交易明细列表失败！", e);
            //getSysLogService().error(method, methodName, e.toString());
            return ApiResponseResult.failure("获取物料条码交易明细列表失败！").data(emtyMap());
        }
    }

    @ApiOperation(value = "获取物料条码库存明细列表", notes = "获取物料条码库存明细列表")
    @RequestMapping(value = "/getKCList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponseResult getKCList(@RequestParam(value = "keyword", required = false) String keyword
    		) {
        String method = "/report/getKCList";String methodName ="获取物料条码库存明细列表";
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            ApiResponseResult result = reportService.getKCList(keyword,super.getPageRequest(sort));
            logger.debug("获取物料条码库存明细列表=getKCList:");
            //getSysLogService().success(method, methodName, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取物料条码库存明细列表失败！", e);
            //getSysLogService().error(method, methodName, e.toString());
            return ApiResponseResult.failure("获取物料条码库存明细列表失败！").data(emtyMap());
        }
    }

    @ApiOperation(value = "获取半成品条码库存明细列表", notes = "获取半成品条码库存明细列表")
    @RequestMapping(value = "/getBCPList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponseResult getBCPList(@RequestParam(value = "keyword", required = false) String keyword) {
        String method = "/report/getBCPList";String methodName ="获取半成品条码库存明细列表";
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            ApiResponseResult result = reportService.getBCPList(keyword, super.getPageRequest(sort));
            logger.debug("获取半成品条码库存明细列表=getBCPList:");
            //getSysLogService().success(method, methodName, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取半成品条码库存明细列表失败！", e);
            //getSysLogService().error(method, methodName, e.toString());
            return ApiResponseResult.failure("获取半成品条码库存明细列表失败！").data(emtyMap());
        }
    }
    @ApiOperation(value = "获取列表", notes = "获取列表")
    @RequestMapping(value = "/getCom1List", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponseResult getCom1List(@RequestParam(value = "prc_name", required = false) String prc_name
    		,@RequestParam(value = "keyword", required = false) String keyword
    		,@RequestParam(value = "pname", required = false) String pname
    		,@RequestParam(value = "stime", required = false) String stime
    		,@RequestParam(value = "etime", required = false) String etime) {
        String method = "/report/getCom1List?type="+prc_name;String methodName ="获取列表";
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            String pname_ = URLDecoder.decode(pname,"UTF-8");
            ApiResponseResult result = reportService.getCom1List(prc_name,pname_,keyword, stime,etime,super.getPageRequest(sort));
            //getSysLogService().success(method, methodName, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取列表失败！", e);
            //getSysLogService().error(method, methodName, e.toString());
            return ApiResponseResult.failure("获取列表失败！").data(emtyMap());
        }
    }
    @ApiOperation(value = "获取列表", notes = "获取列表")
    @RequestMapping(value = "/getComList", method = RequestMethod.GET)
    @ResponseBody
    public ApiResponseResult getComList(@RequestParam(value = "prc_name", required = false) String prc_name
    		,@RequestParam(value = "pname", required = false) String pname
    		,@RequestParam(value = "stime", required = false) String stime
    		,@RequestParam(value = "etime", required = false) String etime) {
        String method = "/report/getComList?type="+prc_name;String methodName ="获取列表";
        try {
            Sort sort = new Sort(Sort.Direction.DESC, "id");
            String pname_ = URLDecoder.decode(pname,"UTF-8");
            ApiResponseResult result = reportService.getComList(prc_name,pname_, stime,etime,super.getPageRequest(sort));
            //getSysLogService().success(method, methodName, null);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("获取列表失败！", e);
            //getSysLogService().error(method, methodName, e.toString());
            return ApiResponseResult.failure("获取列表失败！").data(emtyMap());
        }
    }

    @ApiOperation(value="导出", notes="导出")
    @RequestMapping(value = "/getExcel", method = RequestMethod.GET)
    @ResponseBody
    public void getExcel(String keyword){
        try {
            reportService.getExcel(keyword, getResponse());
        } catch (Exception e) {
            logger.error(e.toString(), e);
        }
    }
}
