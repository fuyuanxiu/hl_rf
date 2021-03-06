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
import com.web.app.service.ProduceReportService;

/**
 * 生产报工
 * */
@Service(value = "ProduceReportService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProduceReportlmpl implements ProduceReportService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getProduceList(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getProduceListPrc(usercode, "PRC_Produce_BG_GetWOInfo");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}

		List<Map<String, Object>> l = (List<Map<String, Object>>) list.get(2);
		// 处理数据
		// 去掉重复的WORL_SINGNUM的记录
		List<Map<String, Object>> l_new = new ArrayList<Map<String, Object>>();
		l_new.add(l.get(0));
		for (int i = 1; i < l.size(); i++) {
			Map<String, Object> map0 = l.get(i - 1);
			Map<String, Object> map1 = l.get(i);
			String task_no0 = map0.get("WORL_SINGNUM").toString();
			String task_no1 = map1.get("WORL_SINGNUM").toString();
			if (!task_no0.equals(task_no1)) {
				l_new.add(map1);
			}
		}
		// 挨个获取新的WORL_SINGNUM的字数据
		List<Map<String, Object>> l_last = new ArrayList<Map<String, Object>>();
		for (int j = 0; j < l_new.size(); j++) {
			List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
			for (int k = 0; k < l.size(); k++) {
				if (l_new.get(j).get("WORL_SINGNUM").toString().equals(l.get(k).get("WORL_SINGNUM").toString())) {
					Map<String, Object> m = l.get(k);
					Map<String, Object> m_new = new HashMap<String, Object>();
					
					m_new.put("ID",  getNull(m.get("ID")));
					m_new.put("WORPROC_CODE",  getNull(m.get("WORPROC_CODE")));
				//	m_new.put("WORPROC_NAME", m.get("WORPROC_NAME").toString());替换为TECHNICS_NAME
					m_new.put("TECHNICS_NAME", getNull(m.get("TECHNICS_NAME")));
					m_new.put("EQU_CODE", getNull(m.get("EQU_CODE")));
					m_new.put("EQU_NAME", getNull(m.get("EQU_NAME")));
					m_new.put("STATUS", getNull(m.get("STATUS")));
					child.add(m_new);//-20201126
				}
			}
			Map<String, Object> m = l_new.get(j);
			Map<String, Object> m_new = new HashMap<String, Object>();
			m_new.put("WORL_SINGNUM", getNull(m.get("WORL_SINGNUM")));
			m_new.put("PRO_NAME", getNull(m.get("PRO_NAME")));
			m_new.put("PRO_CODE", getNull(m.get("PRO_CODE")));
			m_new.put("PERSON_NAME", getNull(m.get("PERSON_NAME")));
			m_new.put("PERSON_CODE", getNull(m.get("PERSON_CODE")));
			m_new.put("PROD_DATE_END", getNull(m.get("PROD_DATE_END")));
			m_new.put("PROD_DATE", getNull(m.get("PROD_DATE")));
			m_new.put("OUTPUT_QTY",getNull( m.get("OUTPUT_QTY")));
			m_new.put("BG_QTY", getNull(m.get("BG_QTY")));
			m_new.put("HG_QTY", getNull(m.get("HG_QTY")));// 合格数量
			m_new.put("LASTUPDATE_DATE",getNull( m.get("LASTUPDATE_DATE")));// 最后一次报数时间
			m_new.put("BHG_QTY", getNull(m.get("BHG_QTY")));// 不合格数量
			m_new.put("Child", child);
			m_new.put("TECHNICS_NAME", getNull(m.get("TECHNICS_NAME")));
			m_new.put("TECHNICS_CODE", getNull(m.get("TECHNICS_CODE")));//工艺编码
			l_last.add(m_new);
		}
		return ApiResponseResult.success().data(l_last);// 返回数据集
	}
	
	private String getNull(Object o){
		if(o == null){
			return "";
		}
		return o.toString();
	}
	// 执行存储获取数据
	private List getProduceListPrc(String usercode, String prc_name) throws Exception {
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
	public ApiResponseResult produceReport(String usercode, String factory, String company, String plan_id, String proc,
			String bg_Qty, String unqu_Qty, String qu_Qty, String eqCODE) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.produceReportPrc(usercode, factory, company, plan_id, proc, bg_Qty, unqu_Qty, qu_Qty,
				eqCODE, "PRC_Produce_BG_Report");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
			return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
		}

		return ApiResponseResult.success(list.get(1).toString());// 返回判断字段数据
	}

	// 执行存储--上报报工数量
	private List produceReportPrc(String usercode, String factory, String company, String plan_id, String proc,
			String bg_Qty, String unqu_Qty, String qu_Qty, String eqCODE, String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, usercode);// 账号
				cs.setString(2, factory);// 工厂编号
				cs.setString(3, company);// 公司编号
				cs.setString(4, plan_id);// 排产计划ID
				cs.setString(5, proc);// 工序编码
				cs.setString(6, bg_Qty);// 报工数量
				cs.setString(7, unqu_Qty);// 不良数量
				cs.setString(8, qu_Qty);// 良品数量
				cs.setString(9, eqCODE);// 设备编码
				cs.registerOutParameter(10, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(11, java.sql.Types.VARCHAR);// 输出参数 返回标识
				cs.registerOutParameter(12, java.sql.Types.VARCHAR);
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getString(10));// （标识）
				result.add(cs.getString(11));// 返回信息
				result.add(cs.getString(12));// 报工数据
				System.out.print(result);
				;
				return result;
			}
		});
		return resultList;
	}
	// 报工记录详细
	@Override
	 public ApiResponseResult getReportRecord(String usercode,String plan_ID,
			String proc,String eq_CODE) throws Exception {
		// TODO Auto-generated method stub
				List<Object> list = this.getReportRecordPrc(usercode,
						plan_ID,proc,eq_CODE, "PRC_Produce_BG_Detail");	    
				if(!list.get(0).toString().equals("0")){//存储过程调用失败 //判断返回标识
		            return ApiResponseResult.failure(list.get(1).toString());//失败返回字段
		        }
				return ApiResponseResult.success().data(list.get(2));//返回判断字段数据
	}
	//执行
	private List getReportRecordPrc(String usercode,String plan_ID,String proc,String eq_CODE, String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, usercode);// 账号
				cs.setString(2, plan_ID);// 排产计划
				cs.setString(3, proc);// 工序
				cs.setString(4, eq_CODE);// 设备
				cs.registerOutParameter(5, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(6, java.sql.Types.VARCHAR);// 输出参数 返回标识
				cs.registerOutParameter(7, -10);// 输出参数 追溯数据
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				List<Map<String, Object>> l = new ArrayList();
				cs.execute();
				result.add(cs.getInt(5));
				result.add(cs.getString(6));
				if (cs.getString(5).toString().equals("0")) {
					// 游标处理
					ResultSet rs = (ResultSet) cs.getObject(7);

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
	//修改报工-返回结果
	public ApiResponseResult updateReportRecord(String usercode, String plan_ID,
    		String report_ID,String bg_QTY,String unqua_Qty,String qua_Qty
    		) throws Exception{
		// TODO Auto-generated method stub
				List<Object> list = this.updateReportRecordPrc(usercode, plan_ID, report_ID, bg_QTY, unqua_Qty, 
						qua_Qty,"PRC_Produce_BG_Alter");
				if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
					return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
				}

				return ApiResponseResult.success(list.get(1).toString());// 返回判断字段数据
	}
	
	private List updateReportRecordPrc(String usercode, String plan_ID,String report_ID,String bg_QTY, 
			String unqua_Qty ,String qua_Qty,String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, usercode);// 账号
				cs.setString(2, plan_ID);// 排产计划ID
				cs.setString(3, report_ID);// 报工
				cs.setString(4, bg_QTY);// 报工数量
				cs.setString(5, unqua_Qty);// 不合格数量
				cs.setString(6, qua_Qty);// 合格数量
				cs.registerOutParameter(7, java.sql.Types.INTEGER);// 输出参数 返回标识
				cs.registerOutParameter(8, java.sql.Types.VARCHAR);// 输出参数 返回标识
				cs.registerOutParameter(9, java.sql.Types.VARCHAR);
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getString(7));// （标识）
				result.add(cs.getString(8));// 返回信息
				result.add(cs.getString(9));// 报工数据
				System.out.print(result);
				;
				return result;
			}
		});
		return resultList;
	}
	
	//暂停报工-返回结果
		public ApiResponseResult suspendReport(String usercode, String plan_ID
	    		) throws Exception{
			// TODO Auto-generated method stub
					List<Object> list = this.suspendReportPrc(usercode, plan_ID, "PRC_Produce_BG_Suspension");
					if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回标识
						return ApiResponseResult.failure(list.get(1).toString());// 失败返回字段
					}
					return ApiResponseResult.success(list.get(1).toString());// 返回判断字段数据
		}
		
		private List suspendReportPrc(String usercode, String plan_ID,String prc_name) throws Exception {
			List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
				@Override
				public CallableStatement createCallableStatement(Connection con) throws SQLException {
					String storedProc = "{call " + prc_name + "(?,?,?,?)}";// 调用的sql
					CallableStatement cs = con.prepareCall(storedProc);
					cs.setString(1, usercode);// 账号
					cs.setString(2, plan_ID);// 排产计划ID
					cs.registerOutParameter(3, java.sql.Types.INTEGER);// 输出参数 返回标识
					cs.registerOutParameter(4, java.sql.Types.VARCHAR);// 输出参数 返回标识
					return cs;
				}
			}, new CallableStatementCallback() {
				public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
					List<Object> result = new ArrayList<>();
					cs.execute();
					result.add(cs.getString(3));// （标识）
					result.add(cs.getString(4));// 返回信息
					System.out.print(result);
					;
					return result;
				}
			});
			return resultList;
		}
	
	
	// 值为"null"或者null转换成""
	private String getEmpty(String str) {
		if (str == null) {
			return "";
		}
		if (StringUtils.equals("null", str)) {
			return "";
		}

		String[] strs = str.split("\\.");
		if (strs.length > 0) {
			if (strs[0].equals("") || strs[0] == null) {
				return "0" + str;
			}
		}
		return str;
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
