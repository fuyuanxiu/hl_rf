package com.web.kanban.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.web.kanban.service.KanbanService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@Api(description = "看板")
@CrossOrigin
@ControllerAdvice
//@RestController
@Controller
@RequestMapping(value = "/kanban")
public class kanbanController extends WebController {

    @Autowired
    private KanbanService kanbanService;
	
    @RequestMapping(value = "/toIndex", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toIndex() {
		String method = "/kanban/toIndex";String methodName ="看板demo";
		ModelAndView mav=new ModelAndView();
		try {
			mav.addObject("area", kanbanService.getAreaList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mav.addObject("area", null);
		}
		mav.setViewName("/kanban/index");//返回路径
		return mav;
	}
    
	@RequestMapping(value = "/toDemo", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toDemo() {
		String method = "/kanban/toDemo";String methodName ="看板demo";
		ModelAndView mav=new ModelAndView();
		//mav.addObject("pname", p);
		mav.setViewName("/kanban/demo");//返回路径
		return mav;
	}
	@RequestMapping(value = "/toHlDemo", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toHlDemo() {
		String method = "/kanban/toHlDemo";String methodName ="恒联看板demo";
		ModelAndView mav=new ModelAndView();
		//mav.addObject("pname", p);
		mav.setViewName("/kanban/hl_area_50cun");//返回路径hl_demo
		return mav;
	}
	
	@RequestMapping(value = "/toHlWorkShop", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toHlWorkShop() {
		String method = "/kanban/toHlWorkShop";String methodName ="恒联车间看板";
		ModelAndView mav=new ModelAndView();
		//mav.addObject("pname", p);
		mav.setViewName("/kanban/hl_workshop");//返回路径hl_demo
		return mav;
	}
	
	
	@RequestMapping(value = "/toHlCompany", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toHlCompany() {
		String method = "/kanban/toHlCompany";String methodName ="恒联公司看板";
		ModelAndView mav=new ModelAndView();
		//mav.addObject("pname", p);
		mav.setViewName("/kanban/hl_company");//返回路径hl_demo
		return mav;
	}
	
	@RequestMapping(value = "/toHlArea", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toHlArea(String line)  {
		String method = "/kanban/toHlArea";String methodName ="恒联区域看板";
		ModelAndView mav=new ModelAndView();
		mav.addObject("line", line);
		try{
			Object object = kanbanService.getWoList(line).getData();
			mav.addObject("WoList", object);
			if(object != null){
				List<Map<String, Object>> lm = (List<Map<String, Object>>) object;
				System.out.println(lm.get(0).get("TASK_NO"));
				//mav.addObject("KanBanList", kanbanService.getKanbanList(line, lm.get(0).get("TASK_NO").toString(), lm.get(0).get("PRO_CODE").toString()).getData());
				mav.addObject("KanBanList", kanbanService.getKanbanList(lm.get(0).get("ARE_CODE").toString(), lm.get(0).get("TASK_NO").toString(), lm.get(0).get("PRO_CODE").toString()).getData());
			}
		}catch(Exception e){
			System.out.println(e.toString());
			mav.addObject("WoList", null);
			mav.addObject("KanBanList", null);
		}
		
		mav.setViewName("/kanban/hl_area");//返回路径
		return mav;
	}
	
	@RequestMapping(value = "/toHlArea1", method = RequestMethod.GET)
	@ResponseBody
	public ModelAndView toHlArea1(String line)  {
		String method = "/kanban/toHlArea";String methodName ="恒联区域看板";
		ModelAndView mav=new ModelAndView();
		mav.addObject("line", line);
		try{
			Object object = kanbanService.getWoList(line).getData();
			mav.addObject("WoList", object);
			if(object != null){
				List<Map<String, Object>> lm = (List<Map<String, Object>>) object;
				System.out.println(lm.get(0).get("TASK_NO"));
				mav.addObject("KanBanList", kanbanService.getKanbanList(line, lm.get(0).get("TASK_NO").toString(), lm.get(0).get("PRO_CODE").toString()).getData());
			}
		}catch(Exception e){
			System.out.println(e.toString());
			mav.addObject("WoList", null);
			mav.addObject("KanBanList", null);
		}
		
		mav.setViewName("/kanban/hl_area2");//返回路径
		return mav;
	}
	
	 @ApiOperation(value = "获取区域信息", notes = "获取区域信息",hidden = true)
	    @RequestMapping(value = "/getAreaList", method = RequestMethod.POST)
	    @ResponseBody
	    public ApiResponseResult getAreaList() {
			try {
				return kanbanService.getAreaList();
			} catch (Exception e) {
				e.printStackTrace();
				return ApiResponseResult.failure(e.toString());
			}
	   }
	 
	   @ApiOperation(value = "根据区域获取工单信息", notes = "根据区域获取工单信息",hidden = true)
	    @RequestMapping(value = "/getTaskList", method = RequestMethod.POST)
	    @ResponseBody
	    public ApiResponseResult getTaskList(String area) {
			try {
				return kanbanService.getTaskList(area);
			} catch (Exception e) {
				e.printStackTrace();
				return ApiResponseResult.failure(e.toString());
			}
	    }
	   
	   @ApiOperation(value = "根据区域获取工单", notes = "根据区域获取工单",hidden = true)
	    @RequestMapping(value = "/getWoList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getWoList(String line) {
		   //定时下载所有员工照片-20210105-fyx
		   try {
				kanbanService.getImg();
			} catch (Exception e) {
				System.out.println(e.toString());
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		   //---end---
			try {
				return kanbanService.getWoList(line);
			} catch (Exception e) {
				e.printStackTrace();
				return ApiResponseResult.failure(e.toString());
			}
			
	    }
	   
	   @ApiOperation(value = "根据区域和工单获取看板信息", notes = "根据区域和工单获取看板信息",hidden = true)
	    @RequestMapping(value = "/getKanbanList", method = RequestMethod.GET)
	    @ResponseBody
	    public ApiResponseResult getKanbanList(String area,String taskNo,String itemNo) {
			try {
				return kanbanService.getKanbanList(area,taskNo,itemNo);
			} catch (Exception e) {
				System.out.println(e.toString());
				e.printStackTrace();
				return ApiResponseResult.failure(e.toString());
			}
	    }
}
