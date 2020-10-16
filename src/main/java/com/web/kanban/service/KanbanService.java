package com.web.kanban.service;

import com.app.base.data.ApiResponseResult;

import java.util.List;

import org.springframework.data.domain.PageRequest;

public interface KanbanService {

	public ApiResponseResult getAreaList() throws Exception;
	
	public ApiResponseResult getTaskList(String area) throws Exception;
    
	public ApiResponseResult getKanbanList(String area,String taskNo,String itemNo) throws Exception;
	
	public ApiResponseResult getWoList(String area) throws Exception;
}
