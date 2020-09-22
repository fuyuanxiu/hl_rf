package com.web.app.service.internal;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.web.app.service.DevRepairService;

@Service(value = "DevRepairService")
@Transactional(propagation = Propagation.REQUIRED)
public class DevRepairlmpl implements DevRepairService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getList(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getListPrc(usercode, "PRC_EQ_Repair_ServiceList");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
			return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
		}
		return ApiResponseResult.success().data(list.get(2));// 返回数据集
	}

	private List getListPrc(String usercode, String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, usercode);// 账号
				cs.registerOutParameter(2, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(3, java.sql.Types.VARCHAR);// 输出参数 返回标识
				cs.registerOutParameter(4, -10);// 输出参数 追溯数据
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				List<Map<String, Object>> l = new ArrayList();
				cs.execute();
				result.add(cs.getInt(2));
				result.add(cs.getString(3));
				if (cs.getString(2).toString().equals("0")) {
					// 游标处理
					ResultSet rs = (ResultSet) cs.getObject(4);
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
	public ApiResponseResult respond(String usercode,String id,
			String eq_code,String cause,String repair_plan) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.respondPrc(usercode, id,eq_code,cause,repair_plan, "PRC_EQ_Repair_Respond");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
			return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
		}
		return ApiResponseResult.success(list.get(1).toString());// 返回字段
	}

	private List respondPrc(String usercode,String id,String eq_code,String cause,
			String repair_plan, String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, usercode);// 账号
				cs.setString(2, id);// 报修申请单ID
				cs.setString(3, eq_code);// 设备编号
				cs.setString(4, cause);// 故障原因
				cs.setString(5, repair_plan);//维修方案
				cs.registerOutParameter(6, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(7, java.sql.Types.VARCHAR);// 输出参数 返回标识
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getInt(6));
				result.add(cs.getString(7));
				return result;
			}
		});
		return resultList;
	}
	
	 @Override
		public ApiResponseResult complete(String usercode,String id,String content,String results) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.completePrc(usercode,id,content,results,"PRC_EQ_Repair_ServiceComplete");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }			
					return ApiResponseResult.success(list.get(1).toString());//返回数据
		} 
	 
	 private List completePrc(String usercode,String id,String content,String results,String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, usercode);// 账号
					cs.setString(2, id);// 报修申请单ID
					cs.setString(3, content);// 维修内容
					cs.setString(4, results);// 维修结果
					cs.registerOutParameter(5, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(6, java.sql.Types.VARCHAR);// 输出参数  返回字段
					return cs;
				}
			}, new CallableStatementCallback() {
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					List<Object> result = new ArrayList<>();
					cs.execute();
					result.add(cs.getInt(5));
					result.add(cs.getString(6));
					return result;
				}
			});
			return resultList;
		}

	private List<Map<String, Object>> fitMap(ResultSet rs) throws Exception {
		List<Map<String, Object>> list = new ArrayList<>();
		if (null != rs) {
			Map<String, Object> map;
			int colNum = rs.getMetaData().getColumnCount();
			List<String> columnNames = new ArrayList<String>();
			for (int i = 1; i <= colNum; i++) {
				columnNames.add(rs.getMetaData().getColumnName(i));
			}
			while (rs.next()) {
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
