package com.web.app.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.web.app.service.ProduceBgService;

@Service(value = "ProduceBgService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProduceBglmpl implements ProduceBgService {
	
	 @Autowired
	    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getProduceList(String usercode) throws Exception {
		// TODO Auto-generated method stub
				List<Object> list = this.getProduceListPrc(usercode, "PRC_Produce_BG_GetWOInfo");	        
				if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回游标
		            return ApiResponseResult.failure(list.get(1).toString());
		        }
				return ApiResponseResult.success().data(list.get(2));//返回数据集
	}
	//执行存储获取数据
	 private List getProduceListPrc(String usercode,String prc_name)throws Exception{
	        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
	            @Override
	            public CallableStatement createCallableStatement(Connection con) throws SQLException {
	                String storedProc = "{call "+prc_name+"(?,?,?,?)}";// 调用的sql
	                CallableStatement cs = con.prepareCall(storedProc);
	                cs.setString(1, usercode);// 账号
	                cs.registerOutParameter(2,java.sql.Types.INTEGER);// 输出参数 返回标识
	                cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 输出参数 返回标识
	                cs.registerOutParameter(4,-10);// 输出参数 追溯数据
	                return cs;
	            }
	        }, new CallableStatementCallback() {
	            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
	                List<Object> result = new ArrayList<>();
	                List<Map<String, Object>> l = new ArrayList();
	                cs.execute();
	                result.add(cs.getInt(2));
	                result.add(cs.getString(3));
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
		public ApiResponseResult produceReport(
				String usercode,
	    		String factory,
	    		String company,
	    		String plan_id,
	    		String proc,
	    		String bg_Qty,
	    		String unqu_Qty,
	    		String qu_Qty,
	    		String eqCODE) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.produceReportPrc(usercode,factory,
							company,plan_id,proc,bg_Qty,unqu_Qty,
							qu_Qty,eqCODE, "PRC_Produce_BG_Report");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }
					
					return ApiResponseResult.success(list.get(1).toString());//返回判断字段数据
		}
		
		//执行存储--上报报工数量
		 private List produceReportPrc(
				 String usercode,
		    		String factory,
		    		String company,
		    		String plan_id,
		    		String proc,
		    		String bg_Qty,
		    		String unqu_Qty,
		    		String qu_Qty,
		    		String eqCODE,String prc_name)throws Exception{
		        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
		            @Override
		            public CallableStatement createCallableStatement(Connection con) throws SQLException {
		                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
		                CallableStatement cs = con.prepareCall(storedProc);
		                cs.setString(1, usercode);// 账号
		                cs.setString(2, factory);//工厂编号
		                cs.setString(3, company);//公司编号
		                cs.setString(4, plan_id);//排产计划ID
		                cs.setString(5, proc);//工序编码
		                cs.setString(6, bg_Qty);//报工数量
		                cs.setString(7, unqu_Qty);//不良数量
		                cs.setString(8, qu_Qty);//良品数量
		                cs.setString(9, eqCODE);//设备编码
		                cs.registerOutParameter(10,java.sql.Types.INTEGER);// 输出参数 返回标识
		                cs.registerOutParameter(11,java.sql.Types.VARCHAR);// 输出参数 返回标识
		                cs.registerOutParameter(12,java.sql.Types.VARCHAR);
		                return cs;
		            }
		        }, new CallableStatementCallback() {
		            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
		                List<Object> result = new ArrayList<>();
		                cs.execute();
		                result.add(cs.getString(10));//（标识）
		                result.add(cs.getString(11));//返回信息
		                result.add(cs.getString(12));//报工数据
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
