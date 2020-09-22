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
import com.web.app.service.DevReHandleService;

@Service(value = "DevReHandleService")
@Transactional(propagation = Propagation.REQUIRED)
public class DevReHandlelmpl implements DevReHandleService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getList(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getListPrc(usercode, "PRC_EQ_Repair_HandleList");
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
		public ApiResponseResult assign(String usercode,String id,String repair_man) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.assignPrc(usercode,id,repair_man,"PRC_EQ_Repair_Handle");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }			
					return ApiResponseResult.success(list.get(1).toString());//返回字段
		} 
	 
	 private List assignPrc(String usercode,String id,String repair_man,String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, usercode);// 账号
					cs.setString(2, id);// 设申请单ID
					cs.setString(3, repair_man);// 维修人工号
					cs.registerOutParameter(4, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(5, java.sql.Types.VARCHAR);// 输出参数  返回字段
					return cs;
				}
			}, new CallableStatementCallback() {
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					List<Object> result = new ArrayList<>();
					cs.execute();
					result.add(cs.getInt(4));
					result.add(cs.getString(5));
					return result;
				}
			});
			return resultList;
		}
	 
	 @Override
		public ApiResponseResult transfer(String usercode,String id,String repair_man) throws Exception {
			// TODO Auto-generated method stub
					List<Object> list = this.transferPrc(usercode,id,repair_man,"PRC_EQ_Repair_HandleTransfer");	        
					if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
			            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
			        }			
					return ApiResponseResult.success(list.get(1).toString());//返回字段
		} 
	 
	 private List transferPrc(String usercode,String id,String repair_man,String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, usercode);// 账号
					cs.setString(2, id);// 设申请单ID
					cs.setString(3, repair_man);// 维修人工号
					cs.registerOutParameter(4, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(5, java.sql.Types.VARCHAR);// 输出参数  返回字段
					return cs;
				}
			}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getInt(4));
				result.add(cs.getString(5));
				return result;
			}
			});
			return resultList;
		}
	 
	 @Override
	public ApiResponseResult getAuxiliaryInfo() throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getAuxiliaryInfoPrc("PRC_EQ_Repair_Auxiliary");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
			return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
		}
		List<Map<String, Object>> l_new= new ArrayList<Map<String, Object>>();
		Map<String, Object> m_new = new HashMap<String, Object>();
		m_new.put("FettlerInfo", list.get(2));
		m_new.put("Programme", list.get(3));
		m_new.put("Result", list.get(4));
		l_new.add(m_new);
		return ApiResponseResult.success().data(l_new);// 返回数字集合
	}

	private List getAuxiliaryInfoPrc(String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.registerOutParameter(1, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(2, java.sql.Types.VARCHAR);// 输出参数 返回字段
				cs.registerOutParameter(3, -10);// 输出参数 追溯数据 维修员数据集合
				cs.registerOutParameter(4, -10);// 输出参数 追溯数据 维修方案数据集合
				cs.registerOutParameter(5, -10);// 输出参数 追溯数据 维修结果数据集合
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getInt(1));
				result.add(cs.getString(2));
				if (cs.getString(1).toString().equals("0")) {
					// 游标处理
					for(int i=3;i<6;i++){			
						List<Map<String, Object>> l = new ArrayList();
						ResultSet rs = (ResultSet) cs.getObject(i);
						try {
							l = fitMap(rs);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						result.add(l);
					}
				}
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
