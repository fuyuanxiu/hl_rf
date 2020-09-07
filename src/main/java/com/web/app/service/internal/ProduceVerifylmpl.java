package com.web.app.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceVerifyService;

@Service(value = "ProduceVerifyService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProduceVerifylmpl implements ProduceVerifyService {
	
	 @Autowired
	    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getProduceVerifyList(String usercode) throws Exception {
		List<Object> list = this.getProduceVerifyListPrc(usercode, "PRC_Produce_BGVerify_GetInfo01");	        
		if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回游标
            return ApiResponseResult.failure(list.get(1).toString());
        }
		List<Map<String, Object>> l = (List<Map<String, Object>>) list.get(3);
		//处理数据
		//先去掉重复的TASK_NO的记录
		List<Map<String, Object>> l_new = new ArrayList<Map<String, Object>>();
		l_new.add(l.get(0));
		for(int i=1;i<l.size();i++){
			Map<String, Object> map0 = l.get(i-1);
			Map<String, Object> map1 = l.get(i);
			String task_no0 = map0.get("TASK_NO").toString();
			String task_no1 = map1.get("TASK_NO").toString();
			if(!task_no0.equals(task_no1)){
				l_new.add(map1);
			}
		}
		//挨个获取新的TASK_NO的字数据
		List<Map<String, Object>> l_last= new ArrayList<Map<String, Object>>();
		for(int j=0;j<l_new.size();j++){
			List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
			for(int k=0;k<l.size();k++){
				if(l_new.get(j).get("TASK_NO").toString().equals(l.get(k).get("TASK_NO").toString())){
					Map<String, Object> m = l.get(k);
					Map<String, Object> m_new = new HashMap<String, Object>();
					m_new.put("WORPROC_NAME", m.get("WORPROC_NAME").toString());
					m_new.put("WORPROC_CODE", m.get("WORPROC_CODE").toString());
					m_new.put("EQU_CODE", m.get("EQU_CODE").toString());//-
					m_new.put("EQU_NAME", m.get("EQU_NAME").toString());//-
					m_new.put("PRODUCE_STATE", m.get("PRODUCE_STATE").toString());
					m_new.put("WORKSHOP_CENTER_NAME", m.get("WORKSHOP_CENTER_NAME").toString());
					child.add(m_new);
				}
			}
			Map<String, Object> m = l_new.get(j);
			Map<String, Object> m_new = new HashMap<String, Object>();
			m_new.put("TASK_NO", m.get("TASK_NO").toString());
			//m_new.put("BOARD_ITEM", m.get("BOARD_ITEM").toString());
			m_new.put("PRO_CODE", m.get("PRO_CODE").toString());
			m_new.put("PLAN_QTY", m.get("PLAN_QTY").toString());
			m_new.put("COMPLETE_QTY", m.get("COMPLETE_QTY").toString());
			m_new.put("PRO_NAME", m.get("PRO_NAME").toString());
			m_new.put("Child", child);
			m_new.put("Role", list.get(2));
			l_last.add(m_new);
		}
		return ApiResponseResult.success().data(l_last);//返回数据集
	}
	//执行存储过程，获取工单信息
	private List getProduceVerifyListPrc(String usercode,String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.registerOutParameter(2,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 输出参数 返回标识
                cs.registerOutParameter(4,-10);// 输出参数 追溯数据
                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 输出参数 返回角色标识
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(2));
                result.add(cs.getString(3));
                result.add(cs.getString(5));
                if(cs.getString(2).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(4);
                    try {
						l = fitMap(rs);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    result.add(l);
                }
                System.out.println(l);
                return result;
            }
        });
        return resultList;
    }
	
	@Override
	public ApiResponseResult getProduceVerifyDetail(
			String usercode,
    		String proc,
    		String taskNo,
    		String eq_code) throws Exception {
		// TODO Auto-generated method stub
				List<Object> list = this.getProduceVerifyDetailPrc(usercode,proc,
						 taskNo,eq_code,"PRC_Produce_BGVerify_GetInfo02");	        
				if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
		            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
		        }			
				return ApiResponseResult.success().data(list.get(2));//返回数据集
	}
	
	//执行存储--上报报工数量
	 private List getProduceVerifyDetailPrc(
			 String usercode,
	    		String proc,
	    		String taskNo,
	    		String eq_code,String prc_name)throws Exception{
	        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, usercode);// 账号
	                cs.setString(2, proc);//工序
	                cs.setString(3, taskNo);//工单号
	                cs.setString(4, eq_code);//设备编号
	                cs.registerOutParameter(5,java.sql.Types.INTEGER);// 输出参数 返回标识
	                cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 输出参数 返回标识
	                cs.registerOutParameter(7,-10);// 输出参数 追溯数据
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	            	List<Object> result = new ArrayList<>();
	                List<Map<String, Object>> l = new ArrayList();
	                cs.execute();
	                result.add(cs.getInt(5));
	                result.add(cs.getString(6));
	                if(cs.getString(5).toString().equals("0")){
	                    //游标处理
	                    ResultSet rs = (ResultSet)cs.getObject(7);
	                    try {
							l = fitMap(rs);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    result.add(l);
	                }
	                System.out.println(l);
	                return result;
	            }
	        });
	        return resultList;
	    }
	
	 @Override
		public ApiResponseResult sumbitProduceVerify(
				String usercode,
	    		String proc,
	    		String task_no,
	    		String eq_code,
	    		String role
	    		) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.sumbitProduceVerifyPrc(usercode,proc,task_no,eq_code,role,"PRC_Produce_BGVerify");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }
					
					return ApiResponseResult.success(list.get(1).toString());//返回判断字段数据
		}
	 
	//执行存储--提交审核
	 private List sumbitProduceVerifyPrc(
			 String usercode,
			 String proc,
	    		String task_no,
	    		String eq_code,
	    		String role,
	    		String prc_name)throws Exception{
	        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, usercode);// 账号
	                cs.setString(2, proc);//工序
	                cs.setString(3, task_no);// 工单
	                cs.setString(4, eq_code);//设备编号
	                cs.setString(5, role);// 角色
	                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
	                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 输出参数 返回标识
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<Object> result = new ArrayList<>();
	                cs.execute();
	                result.add(cs.getInt(6));//（标识）
	                result.add(cs.getString(7));//返回信息
	                System.out.print(result);;
	                return result;
	            }
	        });
	        return resultList;
	    }
	 //获取个人的报工明细
	 @Override
		public ApiResponseResult getProduceRecordDetail(
				String usercode,String plan_id,String role) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.getProduceRecordDetailPrc(usercode,
							plan_id,role,"PRC_Produce_BGVerify_GetInfo03");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }			
					return ApiResponseResult.success().data(list.get(2));//返回数据集
		}
	 
	//获取个人的报工明细
		private List getProduceRecordDetailPrc(String usercode,String plan_id,String role,String prc_name)throws Exception{
	        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, usercode);// 账号
	                cs.setString(2, plan_id);// 账号
	                cs.setString(3, role);// 账号
	                cs.registerOutParameter(4,java.sql.Types.INTEGER);// 输出参数 返回标识
	                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 输出参数 返回标识
	                cs.registerOutParameter(6,-10);// 输出参数 追溯数据
	                
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<Object> result = new ArrayList<>();
	                List<Map<String, Object>> l = new ArrayList();
	                cs.execute();
	                result.add(cs.getInt(4));
	                result.add(cs.getString(5));
	                if(cs.getString(4).toString().equals("0")){
	                    //游标处理
	                    ResultSet rs = (ResultSet)cs.getObject(6);
	                    try {
							l = fitMap(rs);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    result.add(l);
	                }
	                System.out.println(l);
	                return result;
	            }
	        });
	        return resultList;
	    }
	//保存修改后的报工明细
		@Override
		public ApiResponseResult sumbitProduceRecordDetail(
				String usercode,
	    		String proc,
	    		String task_no,
	    		String eq_code,
	    		String reportInfo,
	    		String role
	    		) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.sumbitProduceRecordDetailPrc(usercode,proc,task_no,eq_code,reportInfo,role,"PRC_Produce_BGVerify_Save");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }
					
					return ApiResponseResult.success(list.get(1).toString());//返回判断字段数据
		}
	//执行保存
		 private List sumbitProduceRecordDetailPrc(
				 String usercode,String proc,String task_no,String eq_code,String reportInfo,String role,
		    		String prc_name)throws Exception{
		        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
		            @Override
		            public CallableStatement createCallableStatement(Connection con) throws SQLException {
		                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?)}";// 调用的sql
		                CallableStatement cs = con.prepareCall(storedProc);
		                cs.setString(1, usercode);// 账号
		                cs.setString(2, proc);//工序
		                cs.setString(3, task_no);// 工单号
		                cs.setString(4, eq_code);//设备
		                cs.setString(5, reportInfo);//参数信息字符串
		                cs.setString(6, role);// 角色
		                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
		                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回标识
		                return cs;
		            }
		        }, new CallableStatementCallback() {
		            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		                List<Object> result = new ArrayList<>();
		                cs.execute();
		                result.add(cs.getInt(7));//（标识）
		                result.add(cs.getString(8));//返回信息
		                System.out.print(result);;
		                return result;
		            }
		        });
		        return resultList;
		    }
	//值为"null"或者null转换成""
    private String getEmpty(String str){
        if(str == null){
            return "";
        }
        if(StringUtils.equals("null", str)){
            return "";
        }

        String[] strs = str.split("\\.");
        if(strs.length > 0){
        	if(strs[0].equals("") || strs[0]==null){
        		return "0"+str;
        	}
        }
        return str;
    }
	 private List<Map<String, Object>> fitMap(ResultSet rs) throws Exception{
			List<Map<String, Object>> list = new ArrayList<>(); 
			if(null!=rs) {
				Map<String, Object> map;
				int colNum = rs.getMetaData().getColumnCount();
				List<String> columnNames = new ArrayList<String>();
				for (int i = 1; i <= colNum; i++) {
					columnNames.add(rs.getMetaData().getColumnName(i));
				}
				while(rs.next()) {
					map = new HashMap<String, Object>();
					for (String columnName : columnNames) {
						map.put(columnName, rs.getString(columnName));
					}
					list.add(map);
				}
			}
			return list;
	 }
}
