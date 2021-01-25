package com.web.kanban.service.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
import org.springframework.util.ClassUtils;

import com.app.base.data.ApiResponseResult;
import com.system.user.dao.SysUserDao;
import com.web.kanban.service.KanbanService;
import com.web.report.service.internal.ImpUtils;

@Service(value = "KanbanService")
@Transactional(propagation = Propagation.REQUIRED)
public class KanbanImpl extends ReportPrcUtils implements KanbanService {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private SysUserDao sysUserDao;

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
	private List getAreaListPrc(String prc_name) throws Exception {
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
		List<Object> list = this.getTaskListPrc(area, "prc_board_area_info");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}

		return ApiResponseResult.success().data(list.get(2));
	}

	private List getTaskListPrc(String area, String prc_name) throws Exception {
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
		List<Object> list = this.getKanbanListPrc(area, taskNo, itemNo);
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}
		Map map = new HashMap();
		map.put("CN", list.get(2));// 产能预警
		map.put("XC", list.get(3));// 异常警报（质量&设备）
		map.put("Task", list.get(4));// 当前工单信息
		map.put("UnTask", list.get(5));// 待下发工单
		map.put("GLZ", list.get(6));// 管理者信息（车间经理，班组长，检验员）
		map.put("REN", list.get(7));// 【人】（当前区域工单生产人员）
		map.put("JI", list.get(8));// 【机】（当前区域工单生产设备）
		map.put("LIAO", list.get(9));// 【料】（当前区域工单生产物料）
		map.put("FA", list.get(10));// 【法】（当前区域工单产品SOP 图纸）
		map.put("HUAN", list.get(11));// 【环】（当前区域分部示意图）
		try {
			Map m12 = new HashMap();
			if (list.get(12) != null) {
				List<Map<String, Object>> ce = (List<Map<String, Object>>) list.get(12);
				if (ce.size() > 0) {
					String[] xAxis = new String[ce.size()];
					String[] data = new String[ce.size()];
					String[] data_line = new String[ce.size()];

					for (int i = 0; i < ce.size(); i++) {
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
			map.put("CE", m12);// 【测】（当前区域工单产品不良柏拉图）
		} catch (Exception e) {
			Map m12 = new HashMap();
			m12.put("xAxis", null);
			m12.put("data", null);
			m12.put("data_line", null);
			map.put("CE", m12);// 【测】（当前区域工单产品不良柏拉图）
		}
		// 测

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

				cs.execute();
				result.add(cs.getInt(4));
				result.add(cs.getString(5));
				if (cs.getString(4).toString().equals("0")) {
					// 游标处理
					// ResultSet rs = (ResultSet) cs.getObject(6);
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
						List<Map<String, Object>> l = new ArrayList();
						// result.add(fitMap(rs10));
						while (rs10.next()) {
							Map m = new HashMap();
							m.put("FCODE", getEmpty(rs10.getString("FCODE")));
							m.put("FNAME", getEmpty(rs10.getString("FNAME")));
							m.put("GENRE", getEmpty(rs10.getString("GENRE")));
							l.add(m);
							getUserImg(rs10.getString("FCODE"));
							// saveImg(inputStream,getEmpty(rs10.getString("FCODE")));
							/*
							 * InputStream inputStream =
							 * rs10.getBinaryStream("EXP_FIELD3");
							 * saveImg(inputStream,getEmpty(rs10.getString(
							 * "FCODE")));
							 */

						}
						result.add(l);
					} catch (Exception e) {
						System.out.println(e.toString());
						result.add(null);
					}
					try {
						List<Map<String, Object>> l = new ArrayList();
						// result.add(fitMap(rs10));
						while (rs11.next()) {
							Map m = new HashMap();
							m.put("FCODE", getEmpty(rs11.getString("FCODE")));
							m.put("FNAME", getEmpty(rs11.getString("FNAME")));
							m.put("WORPROC_NAME", getEmpty(rs11.getString("WORPROC_NAME")));
							m.put("BG_QTY", getEmpty(rs11.getString("BG_QTY")));
							m.put("PERCENTAGE", getEmpty(rs11.getString("PERCENTAGE")));
							m.put("IS_COLOR", getEmpty(rs11.getString("IS_COLOR")));
							l.add(m);
							getUserImg(rs11.getString("FCODE"));
						}
						result.add(l);
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
						/*
						 * getFaImg(itemNo); result.add(itemNo);
						 */
					} catch (Exception e) {
						System.out.println(e.toString());
						result.add(null);
					}
					try {
						// result.add(fitMap(rs15));
						getHuanImg(area);
						result.add(area);
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

	/**
	 * 获取车间信息 lst-2021-01-16
	 **/
	public ApiResponseResult getWorkshopList() throws Exception {
		List<Object> list = this.getAreaListPrc("PRC_Board_Workshop_Info");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}
		return ApiResponseResult.success().data(list.get(2));
	}

	/**
	 * 车间看板数据 lst-2021-01-16
	 **/
	public ApiResponseResult getWorkshopKanban(String workShopId) throws Exception {
		List<Object> list = this.getWorkshopPrc("PRC_Board_Workshop_Report", workShopId);
		if (!list.get(0).toString().equals("0")) {
			return ApiResponseResult.failure(list.get(1).toString());
		}
		Map map = new HashMap<>();
		map.put("planEmp", list.get(2));// --在编人数
		map.put("onEmp", list.get(3));// -在岗人数
		map.put("taskNum", list.get(4));// --工单任务数
		map.put("onEmpRate", list.get(5));// --员工在岗率（不带百分号的百分数）
		map.put("onDevRate", list.get(6));// --设备使用率（不带百分号的百分数）
		map.put("onTimeRate", list.get(7));// --报工及时率（不带百分号的百分数）
		map.put("linerInfo", list.get(8));// --车间负责人信息
		map.put("taskInfo", list.get(9));// --在制工单汇总信息
		map.put("warnInfo", list.get(10));// --报警信息（设备报警，质量报警，欠料报警）
		map.put("classStatus", list.get(11));// --班组生产达标情况
		map.put("classOk", list.get(12));// --班组合格率信息（柏拉图）
		map.put("prodOk", list.get(13));// --产品工序总合格率信息（柏拉图）
		return ApiResponseResult.success().data(map);
	}

	// 执行存储获取数据
	private List getWorkshopPrc(String prc_name, String workShopId) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, workShopId);// 不需要参数
				cs.registerOutParameter(2, java.sql.Types.INTEGER);// 返回标识
				cs.registerOutParameter(3, java.sql.Types.VARCHAR);// 输出错误文本
				cs.registerOutParameter(4, java.sql.Types.INTEGER);// --在编人数
				cs.registerOutParameter(5, java.sql.Types.INTEGER);// -在岗人数
				cs.registerOutParameter(6, java.sql.Types.INTEGER);// --工单任务数
				cs.registerOutParameter(7, java.sql.Types.INTEGER);// --员工在岗率（不带百分号的百分数）
				cs.registerOutParameter(8, java.sql.Types.INTEGER);// --设备使用率（不带百分号的百分数）
				cs.registerOutParameter(9, java.sql.Types.INTEGER);// --报工及时率（不带百分号的百分数）
				cs.registerOutParameter(10, -10);// --车间负责人信息
				cs.registerOutParameter(11, -10);// --在制工单汇总信息
				cs.registerOutParameter(12, -10);// --报警信息（设备报警，质量报警，欠料报警）
				cs.registerOutParameter(13, -10);// --班组生产达标情况
				cs.registerOutParameter(14, -10);// --班组合格率信息（柏拉图）
				cs.registerOutParameter(15, -10);// --产品工序总合格率信息（柏拉图）
				return cs;
			}
		}, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getInt(2));
				result.add(cs.getString(3));
				if (cs.getString(2).toString().equals("0")) {
					// 字段处理
					result.add(cs.getInt(4));// --在编人数
					result.add(cs.getInt(5));// -在岗人数
					result.add(cs.getInt(6));// --工单任务数
					result.add(cs.getInt(7));// --员工在岗率（不带百分号的百分数）
					result.add(cs.getInt(8));// --设备使用率（不带百分号的百分数）
					result.add(cs.getInt(9));// --报工及时率（不带百分号的百分数）

					// 游标处理
					ResultSet rs10 = (ResultSet) cs.getObject(10);
					ResultSet rs11 = (ResultSet) cs.getObject(11);
					ResultSet rs12 = (ResultSet) cs.getObject(12);
					ResultSet rs13 = (ResultSet) cs.getObject(13);
					ResultSet rs14 = (ResultSet) cs.getObject(14);
					ResultSet rs15 = (ResultSet) cs.getObject(15);

					try {
						List<Map<String, Object>> l = new ArrayList();
						// result.add(fitMap(rs10));
						while (rs10.next()) {
							Map m = new HashMap();
							m.put("FCODE", getEmpty(rs10.getString("FCODE")));
							m.put("FNAME", getEmpty(rs10.getString("FNAME")));
							// m.put("GENRE",
							// getEmpty(rs10.getString("GENRE")));
							l.add(m);
							getUserImg(rs10.getString("FCODE"));
							// saveImg(inputStream,getEmpty(rs10.getString("FCODE")));

							// InputStream inputStream =
							// rs10.getBinaryStream("EXP_FIELD3");
							// saveImg(inputStream,getEmpty(rs10.getString(
							// "FCODE")));

						}
						result.add(l);
					} catch (Exception e) {
						System.out.println(e.toString());
						result.add(null);
					}

					try {
						result.add(fitMap(rs11));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs12));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs13));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs14));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs15));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
				}
				return result;
			}
		});
		return resultList;
	}

	/**
	 * 公司看板数据 lst-2021-01-18
	 **/
	public ApiResponseResult getCompanyKanban() throws Exception {
		List<Object> list = this.getCompanyPrc("PROC_COMPANY_KANBAN");
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}
		Map map = new HashMap();
		map.put("Result", list.get(2));// --用户在线比例-result
		map.put("Result1", list.get(3));// --客户端累计PPM-result1
		map.put("Result2", list.get(4));// --包装车间生产产量-result2
		map.put("Result3", list.get(5));// --毛坯车间生产产量-result3
		map.put("Result4", list.get(6));// --表面处理不合格率-result4
		map.put("Result5", list.get(7));// --原料毛坯过程PPM-result5
		map.put("Result6", list.get(8));// --外协过程PPM-result6
		return ApiResponseResult.success().data(map);
	}

	// 执行存储获取数据
	private List getCompanyPrc(String prc_name) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?,?,?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.registerOutParameter(1, java.sql.Types.INTEGER);// 返回标识
				cs.registerOutParameter(2, java.sql.Types.VARCHAR);// 输出错误文本
				cs.registerOutParameter(3, -10);// --用户在线比例-result
				cs.registerOutParameter(4, -10);// --客户端累计PPM-result1
				cs.registerOutParameter(5, -10);// --包装车间生产产量-result2
				cs.registerOutParameter(6, -10);// --毛坯车间生产产量-result3
				cs.registerOutParameter(7, -10);// --表面处理不合格率-result4
				cs.registerOutParameter(8, -10);// --原料毛坯过程PPM-result5
				cs.registerOutParameter(9, -10);// --外协过程PPM-result6
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
					ResultSet rs3 = (ResultSet) cs.getObject(3);
					ResultSet rs4 = (ResultSet) cs.getObject(4);
					ResultSet rs5 = (ResultSet) cs.getObject(5);
					ResultSet rs6 = (ResultSet) cs.getObject(6);
					ResultSet rs7 = (ResultSet) cs.getObject(7);
					ResultSet rs8 = (ResultSet) cs.getObject(8);
					ResultSet rs9 = (ResultSet) cs.getObject(9);
					try {
						result.add(fitMap(rs3));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs4));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs5));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs6));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs7));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs8));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
					try {
						result.add(fitMap(rs9));
					} catch (Exception e) {
						result.add(null);
						System.out.println(e.toString());
					}
				}
				return result;
			}
		});
		return resultList;
	}

	/**
	 * 获取看板刷新时间间隔 2021-01-22
	 **/
	public ApiResponseResult getRotime(String code) throws Exception {
		List<Object> list = this.getRotimePrc("PRC_Board_RefreshTime_Info", code);
		if (!list.get(0).toString().equals("0")) {// 存储过程调用失败 //判断返回游标
			return ApiResponseResult.failure(list.get(1).toString());
		}
		return ApiResponseResult.success().data(list.get(2));
	}

	// 执行存储获取数据
	private List getRotimePrc(String prc_name, String code) throws Exception {
		List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
			@Override
			public CallableStatement createCallableStatement(Connection con) throws SQLException {
				String storedProc = "{call " + prc_name + "(?,?,?,?)}";// 调用的sql
				CallableStatement cs = con.prepareCall(storedProc);
				cs.setString(1, code);// 看板编号
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

	@Override
	public ApiResponseResult getImg() throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> lmp = sysUserDao.getImg();
		for (Map<String, Object> map : lmp) {
			getUserImg(map.get("FCODE").toString());
		}
		return null;
	}
}
