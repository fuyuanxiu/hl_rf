package com.web.kanban.service.internal;

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
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.app.base.data.ApiResponseResult;
import com.web.kanban.service.KanbanService;
import com.web.report.service.internal.ImpUtils;

@Service(value = "KanbanService")
@Transactional(propagation = Propagation.REQUIRED)
public class KanbanImpl  implements KanbanService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getAreaList() throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getAreaListPrc("prc_board_area_info");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}
		
		return ApiResponseResult.success().data(list.get(2));
	}
	// 执行存储获取数据
		private List getAreaListPrc( String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.registerOutParameter(1, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(2, java.sql.Types.VARCHAR);// 输出参数 返回标识
					cs.registerOutParameter(3, -10);// 输出参数 追溯数据
					return cs;
				}
			}, new CallableStatementCallback() {
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					List<Object> result = new ArrayList<>();
					List<Map<String, Object>> l = new ArrayList();
					cs.execute();
					result.add(cs.getInt(1));
					result.add(cs.getString(2));
					if (cs.getString(1).toString().equals("0")) {
						// 游标处理
						ResultSet rs = (ResultSet) cs.getObject(3);

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
		@Override
		public ApiResponseResult getTaskList(String area) throws Exception {
			// TODO Auto-generated method stub
			List<Object> list = this.getTaskListPrc(area,"prc_board_area_info");
			if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
				return ApiResponseResult.failure(list.get(1).toString());
			}
			
			return ApiResponseResult.success().data(list.get(2));
		}
		private List getTaskListPrc(String area,String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, area);// 区域
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
		public ApiResponseResult getKanbanList(String area, String taskNo, String itemNo) throws Exception {
			// TODO Auto-generated method stub
			List<Object> list = this.getKanbanListPrc(area,taskNo,itemNo);
			if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
				return ApiResponseResult.failure(list.get(1).toString());
			}
			Map map = new HashMap();
			map.put("CN", list.get(2));//
			map.put("XC", list.get(3));
			map.put("Task", list.get(4));
			map.put("UnTask", list.get(5));
			map.put("GLZ", list.get(6));
			map.put("REN", list.get(7));
			map.put("JI", list.get(8));
			map.put("LIAO", list.get(9));
			map.put("FA", list.get(10));
			map.put("HUAN", list.get(11));
			try{
				 Map m12 = new HashMap();
					if(list.get(12) != null){
						List<Map<String, Object>> ce = (List<Map<String, Object>>) list.get(12);
						if(ce.size() > 0){
							String[] xAxis = new String[ce.size()];
							String[] data = new String[ce.size()];
							String[] data_line = new String[ce.size()];
							
							
							for(int i=0;i<ce.size();i++){
								Map<String, Object> m = ce.get(i);
								xAxis[i] = m.get("BAD_ITEMS").toString();
								data[i] = m.get("BAD_QTY").toString();
								data_line[i] = m.get("BAD_RATE").toString();
							}
							m12.put("xAxis", xAxis);
							m12.put("data", data);
							m12.put("data_line", data_line);
						} 
					}
					map.put("CE", m12);
			}catch(Exception e){
				 Map m12 = new HashMap();
				 m12.put("xAxis", null);
					m12.put("data", null);
					m12.put("data_line", null);
				map.put("CE", m12);
			}
			//测
		   
			return ApiResponseResult.success().data(map);
		}
		private List getKanbanListPrc(String area, String taskNo, String itemNo) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call PRC_Board_Area_Report(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, area);// 区域
					cs.setString(2, taskNo);// 工单号
					cs.setString(3, itemNo);// 产品编号
					cs.registerOutParameter(4, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(5, java.sql.Types.VARCHAR);// 输出参数 返回标识
					cs.registerOutParameter(6, java.sql.Types.INTEGER);// 
					cs.registerOutParameter(7, -10);// 输出参数 追溯数据
					cs.registerOutParameter(8, -10);// 输出参数 追溯数据
					cs.registerOutParameter(9, -10);// 输出参数 追溯数据
					cs.registerOutParameter(10, -10);// 输出参数 追溯数据
					cs.registerOutParameter(11, -10);// 输出参数 追溯数据
					cs.registerOutParameter(12, -10);// 输出参数 追溯数据
					cs.registerOutParameter(13, -10);// 输出参数 追溯数据
					cs.registerOutParameter(14, -10);// 输出参数 追溯数据
					cs.registerOutParameter(15, -10);// 输出参数 追溯数据
					cs.registerOutParameter(16, -10);// 输出参数 追溯数据
					return cs;
				}
			}, new CallableStatementCallback() {
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					List<Object> result = new ArrayList<>();
					List<Map<String, Object>> l = new ArrayList();
					cs.execute();
					result.add(cs.getInt(4));
					result.add(cs.getString(5));
					if (cs.getString(4).toString().equals("0")) {
						// 游标处理
						//ResultSet rs = (ResultSet) cs.getObject(6);
						result.add(cs.getInt(6));
						ResultSet rs7 = (ResultSet) cs.getObject(7);
						ResultSet rs8 = (ResultSet) cs.getObject(8);
						ResultSet rs9 = (ResultSet) cs.getObject(9);
						ResultSet rs10 = (ResultSet) cs.getObject(10);
						ResultSet rs11 = (ResultSet) cs.getObject(11);
						ResultSet rs12 = (ResultSet) cs.getObject(12);
						ResultSet rs13 = (ResultSet) cs.getObject(13);
						ResultSet rs14 = (ResultSet) cs.getObject(14);
						ResultSet rs15 = (ResultSet) cs.getObject(15);
						ResultSet rs16 = (ResultSet) cs.getObject(16);
						try {
							result.add(fitMap(rs7));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs8));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs9));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs10));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs11));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs12));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs13));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs14));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs15));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						try {
							result.add(fitMap(rs16));
						} catch (Exception e) {
							System.out.println(e.toString());
							result.add(null);
						}
						
					}
					System.out.println(l);
					return result;
				}

			});
			return resultList;
		}
		@Override
		public ApiResponseResult getWoList(String area) throws Exception {
			// TODO Auto-generated method stub
			List<Object> list = this.getWoListPrc(area);
			if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
				return ApiResponseResult.failure(list.get(1).toString());
			}
			
			return ApiResponseResult.success().data(list.get(2));
		}
		private List getWoListPrc(String area) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call PRC_Board_Area_GetWOInfo(?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, area);// 区域
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
}
