package com.web.report.service.internal;

import com.app.base.data.ApiResponseResult;
import com.web.report.service.ReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.Color;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value = "ReportService")
@Transactional(propagation = Propagation.REQUIRED)
public class ReportImpl  implements ReportService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public List<Object> getTreeList(String factory, String company, String in_str, String usercode, String ip, int page,int size) throws Exception{

        return this.doPrc("app_prc_rwd_retrospect", factory, company, in_str, usercode, ip, page, size);
    }

    //执行存储获取数据
    //itemname 物料名称,lifno 供应商编码， qty 数量, lot_no 物料批次，proc_name 工序名（追溯树索引），task_no 生产工单，parent_id  父ID ，child_id 子ID
    public List doPrc(String prc_name, String param, String param2, String param3, String param4, String param5, int param6, int param7){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, param);// 工厂
                cs.setString(2, param2);// 公司
                cs.setString(3, param3);// 批次条码
                cs.setString(4, param4);// 用户
                cs.setString(5, param5);// IP 地址
                cs.setInt(6, param6);// 每页记录数
                cs.setInt(7, param7);// 当前页码
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(10,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                //result.add(cs.getInt(8));
                //result.add(cs.getString(9));
                if(cs.getString(8).toString().endsWith("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(10);
                   
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("ITEMNAME", getEmpty(rs.getString("ITEMNAME")));
                        m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));
                        m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));
                        m.put("LIFNO", getEmpty(rs.getString("LIFNO")));
                        m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));
                        m.put("QTY", getEmpty(rs.getString("QTY")));
                        m.put("TASK_NO", getEmpty(rs.getString("TASK_NO")));
                        if(rs.getString("PARENT_ID").equals("1")){
                        	m.put("parentId", "0");
                        }else{
                        	m.put("parentId", getEmpty(rs.getString("PARENT_ID")));
                        }
                        
                        m.put("id", getEmpty(rs.getString("CHILD_ID")));
                        l.add(m);
                    }
                    result.add(l);
                }
                System.out.println(l);
                return l;
            }
        });

        return resultList;
    }

    

	@Override
	public ApiResponseResult getMaterialsList(String keyword, PageRequest pageRequest)
			throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.doMaterialsListPrc("10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doMaterialsListPrc( String factory , String company, String lotno, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call App_Zhuisu_Wuliao_Info(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));//工序
                        m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));
                        m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));
                        m.put("ITEM_NAME", getEmpty(rs.getString("ITEM_NAME")));
                        m.put("TR_LOTNO", getEmpty(rs.getString("TR_LOTNO")));
                        m.put("SUPP_NO", getEmpty(rs.getString("SUPP_NO")));
                        m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));
                        m.put("LOT_DATE", getEmpty(rs.getString("LOT_DATE")));
                        m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
                        m.put("PLAN_QTY", getEmpty(rs.getString("PLAN_QTY")));
                        m.put("FPUT_QTY", getEmpty(rs.getString("FPUT_QTY")));
                        m.put("UNIT", getEmpty(rs.getString("UNIT")));
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getQualityList(String keyword, PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.doQualityListPrc("10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doQualityListPrc( String factory , String company, String lotno, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call App_Zhuisu_Pinzhi_Info(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));//工序
                        m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));
                        m.put("FCHECK_ITEM", getEmpty(rs.getString("FCHECK_ITEM")));
                        m.put("VCHECK_RESU", getEmpty(rs.getString("VCHECK_RESU")));
                        m.put("FSECOND_RESU", getEmpty(rs.getString("FSECOND_RESU")));
                        m.put("FSTAND", getEmpty(rs.getString("FSTAND")));
                        m.put("FDOWN_ALLOW", getEmpty(rs.getString("FDOWN_ALLOW")));
                        m.put("FUP_ALLOW", getEmpty(rs.getString("FUP_ALLOW")));
                        m.put("FSPEC_REQU", getEmpty(rs.getString("FSPEC_REQU")));
                        
                        m.put("FCHECK_BY", getEmpty(rs.getString("FCHECK_BY")));
                        m.put("FCHECK_DATE", getEmpty(rs.getString("FCHECK_DATE")));
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getDeviceList(String keyword, PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.doDeviceListPrc("10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doDeviceListPrc( String factory , String company, String lotno, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call App_Zhuisu_Shebei_Info(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));//工序
                        m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
                        m.put("MACHINE_CODE", getEmpty(rs.getString("MACHINE_CODE")));
                        m.put("CREATE_BY", getEmpty(rs.getString("CREATE_BY")));
                        m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));
                        m.put("EQ_NAME", getEmpty(rs.getString("EQ_NAME")));
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getFixtureList(String keyword, PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.doFixtureListPrc("10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doFixtureListPrc( String factory , String company, String lotno, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call App_Zhuisu_Gongzhuang_Info(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));//工序
                        m.put("CREATE_BY", getEmpty(rs.getString("CREATE_BY")));
                        m.put("M_CODE", getEmpty(rs.getString("M_CODE")));
                        m.put("EQ_NAME", getEmpty(rs.getString("EQ_NAME")));
                        m.put("S_CODE", getEmpty(rs.getString("S_CODE")));
                        m.put("CUT_NAME", getEmpty(rs.getString("CUT_NAME")));
                        m.put("DOWN_DATE", getEmpty(rs.getString("DOWN_DATE")));
                        m.put("DOWN_USER_BY", getEmpty(rs.getString("DOWN_USER_BY")));
                        m.put("NOW_TIME", getEmpty(rs.getString("NOW_TIME")));
                        m.put("AVAILABLE_TIME", getEmpty(rs.getString("AVAILABLE_TIME")));
                        m.put("TOTAL_TIME", getEmpty(rs.getString("TOTAL_TIME")));
                        m.put("LOTNO", getEmpty(rs.getString("LOTNO")));
                        m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getHcfrList(String keyword, String ptype, PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.doHcfrListPrc("10000", "1000", keyword, ptype, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doHcfrListPrc( String factory , String company, String lotno, String type,int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call APP_HCFR_DETAIL(?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setString(4, type);// 类型(HC表示化成/FR表示分容)
                cs.setInt(5, page);// 每页记录数
                cs.setInt(6, size);// 当前页码
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(10,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(8));
                result.add(cs.getString(9));
                if(cs.getString(8).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(10);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("STARTTIME", getEmpty(rs.getString("STARTDATE")) + " "+getEmpty(rs.getString("STARTTIME")));
                        m.put("ENDTIME", getEmpty(rs.getString("ENDDATE")) + " "+getEmpty(rs.getString("ENDTIME")));
                        m.put("SSMODE", getEmpty(rs.getString("SSMODE")));
                        m.put("DEVID", getEmpty(rs.getString("DEVID")));
                        m.put("WOINDEX", getEmpty(rs.getString("WOINDEX")));
                        m.put("WONO", getEmpty(rs.getString("WONO")));
                        m.put("CYCNO", getEmpty(rs.getString("CYCNO")));
                        m.put("DEVDESC", getEmpty(rs.getString("DEVDESC")));
                        m.put("PALLETNUM", getEmpty(rs.getString("PALLETNUM")));
                        m.put("LOCATION", getEmpty(rs.getString("LOCATION")));
                        m.put("PASSAGEWAY", getEmpty(rs.getString("PASSAGEWAY")));
                        m.put("OPENV", getEmpty(rs.getString("OPENV")));
                        m.put("AVRV", getEmpty(rs.getString("AVRV")));
                        m.put("ENDV", getEmpty(rs.getString("ENDV")));
                        m.put("TVALUE", getEmpty(rs.getString("TVALUE")));
                        m.put("CVALUE", getEmpty(rs.getString("CVALUE")));
                        m.put("CHARGEI", getEmpty(rs.getString("CHARGEI")));
                        m.put("ENDI", getEmpty(rs.getString("ENDI")));
                        m.put("DISCHARGEI", getEmpty(rs.getString("DISCHARGEI")));
                        
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(7));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getFRList(String keyword, String stime, String etime,String ptype, PageRequest pageRequest)
			throws Exception {
		// TODO Auto-generated method stub
		if(getEmpty(ptype).equals("")){
			//封装数据
	        return ApiResponseResult.success().data(emtyMap());
		}
		List<Object> list = this.doFRListPrc("10000", "1000", keyword, stime,etime,ptype, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	private Map<String, Object> emtyMap(){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", new ArrayList());
        map.put("page", 1);
        map.put("pageSize", 10);
        map.put("total", 0);
        return map;
	}
	
	//执行存储获取数据
    private List doFRListPrc( String factory , String company, String lotno, String stime,String etime,String ptype,int page, int size){
    	String prc_name = ptype.equals("HC")?"APP_HC_DATA":"APP_FR_DATA";
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setString(4, stime);
                cs.setString(5, etime);
                cs.setInt(6, page);// 每页记录数
                cs.setInt(7, size);// 当前页码
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(9,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(11,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(9));
                result.add(cs.getString(10));
                if(cs.getString(9).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(11);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("BARCODE", getEmpty(rs.getString("BARCODE")));
                        if( ptype.equals("FR")){
                        	m.put("CAPACITY", getEmpty(rs.getString("CAPACITY")));
                        }
                        m.put("OPENV", getEmpty(rs.getString("OPENV")));
                        m.put("AVGV", getEmpty(rs.getString("AVGV")));
                        m.put("ENDV", getEmpty(rs.getString("ENDV")));
                        m.put("STARTDATE", getEmpty(rs.getString("STARTDATE")));
                        m.put("ENDDATE", getEmpty(rs.getString("ENDDATE")));
                        m.put("PASSAGEWAY", getEmpty(rs.getString("PASSAGEWAY")));
                        m.put("FIP", getEmpty(rs.getString("FIP")));
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(8));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getOCVList(String prc_name,String keyword, String stime, String etime, String lineno,String  pmodel,
			PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		if(getEmpty(stime).equals("") &&getEmpty(etime).equals("")){
			//封装数据
	        return ApiResponseResult.success().data(emtyMap());
		}
		//"APP_OCV1_DATA"
		List<Object> list = this.doOCVListPrc(prc_name,"10000", "1000", keyword, stime,etime,lineno, pmodel,pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		System.out.println(list);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString()).data(emtyMap());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}

	@Override
	public ApiResponseResult getOCV2List(String keyword, String stime, String etime, String lineno,String  pmodel,
			PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		if(getEmpty(stime).equals("") &&getEmpty(etime).equals("")){
			//封装数据
	        return ApiResponseResult.success().data(emtyMap());
		}
		List<Object> list = this.doOCVListPrc("APP_OCV2_DATA","10000", "1000", keyword, stime,etime,lineno,pmodel, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
		if(!list.get(0).toString().equals("0")){
			return ApiResponseResult.failure(list.get(1).toString()).data(emtyMap());
		}
		//封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doOCVListPrc(String prc_name, String factory , String company, String lotno, String stime,String etime,String lineno,String pmodel,int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, lotno);// 批次条码
                cs.setString(4, lineno);
                cs.setString(5, pmodel);
                cs.setString(6, stime);
                cs.setString(7, etime);
                cs.setInt(8, page);// 每页记录数
                cs.setInt(9, size);// 当前页码
                cs.registerOutParameter(10,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(11,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(12,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(13,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(11));
                result.add(cs.getString(12));
                if(cs.getString(11).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(13);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("PRO_MODEL", getEmpty(rs.getString("PRO_MODEL")));
                        m.put("MODEL", getEmpty(rs.getString("MODEL")));
                        m.put("SN", getEmpty(rs.getString("SN")));
                        if(prc_name.equals("APP_OCV1_DATA")){
                        	 m.put("OCT1", getEmpty(rs.getString("OCT1")));
                        	 m.put("OCV1RESULT", getEmpty(rs.getString("OCV1RESULT")));
                            m.put("OCV1", getEmpty(rs.getString("OCV1")));
                            m.put("OCV1MIN", getEmpty(rs.getString("OCV1MIN")));
                            m.put("OCV1MAX", getEmpty(rs.getString("OCV1MAX")));
                            m.put("VOL1RESULT", getEmpty(rs.getString("VOL1RESULT")));
                            m.put("OCR1", getEmpty(rs.getString("OCR1")));
                            m.put("IMP1MIN", getEmpty(rs.getString("IMP1MIN")));
                            m.put("IMP1MAX", getEmpty(rs.getString("IMP1MAX")));
                            m.put("IMP1RESULT", getEmpty(rs.getString("IMP1RESULT")));
                            m.put("THICKNESS1", getEmpty(rs.getString("THICKNESS1")));
                            m.put("THICKNESS1MIN", getEmpty(rs.getString("THICKNESS1MIN")));
                            m.put("THICKNESS1MAX", getEmpty(rs.getString("THICKNESS1MAX")));
                            m.put("THICRESULT1", getEmpty(rs.getString("THICRESULT1")));
                            m.put("LINENO", getEmpty(rs.getString("LINENO")));
                        }else if(prc_name.equals("APP_OCV2_DATA")){
                        	m.put("OCT2", getEmpty(rs.getString("OCT2")));
                        	m.put("OCV2RESULT", getEmpty(rs.getString("OCV2RESULT")));
                        	m.put("OCV2", getEmpty(rs.getString("OCV2")));
                        	m.put("OCV2MIN", getEmpty(rs.getString("OCV2MIN")));
                        	m.put("OCV2MAX", getEmpty(rs.getString("OCV2MAX")));
                        	m.put("VOL2RESULT", getEmpty(rs.getString("VOL2RESULT")));
                        	m.put("OCR2", getEmpty(rs.getString("OCR2")));
                        	m.put("IMP2MIN", getEmpty(rs.getString("IMP2MIN")));
                        	m.put("IMP2MAX", getEmpty(rs.getString("IMP2MAX")));
                        	m.put("IMP2RESULT", getEmpty(rs.getString("IMP2RESULT")));
                        	m.put("K12", getEmpty(rs.getString("K12")));
                        	m.put("K2MIN", getEmpty(rs.getString("K2MIN")));
                        	m.put("K2MAX", getEmpty(rs.getString("K2MAX")));
                        	m.put("K12RESULT", getEmpty(rs.getString("K12RESULT")));
                        	m.put("THICKNESS2", getEmpty(rs.getString("THICKNESS2")));
                        	m.put("THICKNESS2MIN", getEmpty(rs.getString("THICKNESS2MIN")));
                        	m.put("THICKNESS2MAX", getEmpty(rs.getString("THICKNESS2MAX")));
                        	m.put("THICRESULT2", getEmpty(rs.getString("THICRESULT2")));
                        	m.put("LINENO", getEmpty(rs.getString("LINENO")));
                        }else if(prc_name.equals("APP_OCV3_DATA")){
                        	m.put("OCT3", getEmpty(rs.getString("OCT3")));
                        	m.put("OCV3RESULT", getEmpty(rs.getString("OCV3RESULT")));
                        	m.put("OCV3", getEmpty(rs.getString("OCV3")));
                        	m.put("OCV3MIN", getEmpty(rs.getString("OCV3MIN")));
                        	m.put("OCV3MAX", getEmpty(rs.getString("OCV3MAX")));
                        	m.put("VOL3RESULT", getEmpty(rs.getString("VOL3RESULT")));
                        	m.put("OCR3", getEmpty(rs.getString("OCR3")));
                        	m.put("IMP3MIN", getEmpty(rs.getString("IMP3MIN")));
                        	m.put("IMP3MAX", getEmpty(rs.getString("IMP3MAX")));
                        	m.put("IMP3RESULT", getEmpty(rs.getString("IMP3RESULT")));
                        	m.put("K13", getEmpty(rs.getString("K13")));
                        	m.put("K3MIN", getEmpty(rs.getString("K3MIN")));
                        	m.put("K3MAX", getEmpty(rs.getString("K3MAX")));
                        	m.put("K13RESULT", getEmpty(rs.getString("K13RESULT")));
                        	m.put("THICKNESS3", getEmpty(rs.getString("THICKNESS3")));
                        	m.put("THICKNESS3MIN", getEmpty(rs.getString("THICKNESS3MIN")));
                        	m.put("THICKNESS3MAX", getEmpty(rs.getString("THICKNESS3MAX")));
                        	m.put("THICRESULT3", getEmpty(rs.getString("THICRESULT3")));
                        	m.put("LINENO", getEmpty(rs.getString("LINENO")));
                        }else if(prc_name.equals("APP_OCV4_DATA")){
                        	m.put("OCT4", getEmpty(rs.getString("OCT4")));
                        	m.put("OCV4RESULT", getEmpty(rs.getString("OCV4RESULT")));
                        	m.put("OCV4", getEmpty(rs.getString("OCV4")));
                        	m.put("OCV4MIN", getEmpty(rs.getString("OCV4MIN")));
                        	m.put("OCV4MAX", getEmpty(rs.getString("OCV4MAX")));
                        	m.put("VOL4RESULT", getEmpty(rs.getString("VOL4RESULT")));
                        	m.put("OCR4", getEmpty(rs.getString("OCR4")));
                        	m.put("IMP4MIN", getEmpty(rs.getString("IMP4MIN")));
                        	m.put("IMP4MAX", getEmpty(rs.getString("IMP4MAX")));
                        	m.put("IMP4RESULT", getEmpty(rs.getString("IMP4RESULT")));
                        	m.put("K14", getEmpty(rs.getString("K14")));
                        	m.put("K4MIN", getEmpty(rs.getString("K4MIN")));
                        	m.put("K4MAX", getEmpty(rs.getString("K4MAX")));
                        	m.put("K14RESULT", getEmpty(rs.getString("K14RESULT")));
                        	m.put("THICKNESS4", getEmpty(rs.getString("THICKNESS4")));
                        	m.put("THICKNESS4MIN", getEmpty(rs.getString("THICKNESS4MIN")));
                        	m.put("THICKNESS4MAX", getEmpty(rs.getString("THICKNESS4MAX")));
                        	m.put("THICRESULT4", getEmpty(rs.getString("THICRESULT4")));
                        	m.put("LINENO", getEmpty(rs.getString("LINENO")));
                        }
                        
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(10));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getLineList() throws Exception {
		// TODO Auto-generated method stub
		return ApiResponseResult.success().data(this.doLineListPrc("10000", "1000").get(2));
	}
	
	//执行存储获取数据
    private List doLineListPrc(String factory , String company){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call  app_lineno_info(?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.registerOutParameter(3,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(4,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(5,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(3));
                result.add(cs.getString(4));
                if(cs.getString(3).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(5);
                  
                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("LINE_NO", getEmpty(rs.getString("LINE_NO")));
                        l.add(m);
                    }
                    result.add(l);
                }

                return result;
            }
        });

        return resultList;
    }

    //获取物料条码交易明细
    @Override
    @Transactional
    public ApiResponseResult getJYList(String keyword,String stime,String etime, PageRequest pageRequest) throws Exception{
        //获取数据
        List<Object> list = this.doJYListPrc("APP_WULIAO_JIAOYI_JL","10000", "1000", keyword, stime, etime, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
        if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
        //封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
    }
    //执行存储获取数据——获取物料条码交易明细
    private List doJYListPrc(String prc_name, String factory, String company, String barcode, String stime,String etime,int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, barcode);// 物料或者条码
                cs.setString(4, stime);// 物料或者条码
                cs.setString(5, etime);// 物料或者条码
                cs.setInt(6, page);// 每页记录数
                cs.setInt(7, size);// 当前页码
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(9,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(11,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(9));
                result.add(cs.getString(10));
                if(cs.getString(9).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(11);

                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("BILL_NO", getEmpty(rs.getString("BILL_NO")));//单号
                        m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));//物料条码
                        m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));//物料编码
                        m.put("ITEM_DESCRIPTION", getEmpty(rs.getString("ITEM_DESCRIPTION")));//物料描述
                        m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));//物料数量
                        m.put("ITEM_UNIT", getEmpty(rs.getString("ITEM_UNIT")));//单位
                        m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));//批次
                        m.put("TO_DEPOTNO", getEmpty(rs.getString("TO_DEPOTNO")));//入库仓库
                        m.put("FROM_DEPOTNO", getEmpty(rs.getString("FROM_DEPOTNO")));//出库仓库
                        m.put("BILL_TYPE_NAME", getEmpty(rs.getString("BILL_TYPE_NAME")));//单据类型
                        m.put("TRANSACTION_NAME", getEmpty(rs.getString("TRANSACTION_NAME")));//事务类型
                        m.put("CREATE_BY", getEmpty(rs.getString("CREATE_BY")));//操作人
                        m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));//操作日期
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(8));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

    //获取物料条码库存明细
    @Override
    @Transactional
    public ApiResponseResult getKCList(String keyword, PageRequest pageRequest) throws Exception{
        //获取数据
        List<Object> list = this.doKCListPrc("APP_WULIAO_KUCUN_JL","10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
        if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
        //封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
    }
    //执行存储获取数据——获取物料条码库存明细
    private List doKCListPrc(String prc_name, String factory, String company, String barcode, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, barcode);// 物料或者条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);

                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));//物料条码
                        m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));//物料编码
                        m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));//批次
                        m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));//物料数量
                        m.put("TR_LOTNO", getEmpty(rs.getString("TR_LOTNO")));//供应商批次
                        m.put("DEPOT_NAME", getEmpty(rs.getString("DEPOT_NAME")));//仓库
                        m.put("PROD_DATE", getEmpty(rs.getString("PROD_DATE")));//生产日期
                        m.put("INDEP_DATE", getEmpty(rs.getString("INDEP_DATE")));//入库日期
                        m.put("ITEM_DESCRIPTION", getEmpty(rs.getString("ITEM_DESCRIPTION")));//物料名称
                        m.put("SUPP_NAME", getEmpty(rs.getString("SUPP_NAME")));//供应商
                        m.put("DAYS", getEmpty(rs.getString("DAYS")));//在库时间
                        m.put("TO_EXTDATE", getEmpty(rs.getString("TO_EXTDATE")));//到期日期
                        m.put("UNIT", getEmpty(rs.getString("UNIT")));//单位
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

    //获取半成品条码库存明细
    @Override
    @Transactional
    public ApiResponseResult getBCPList(String keyword, PageRequest pageRequest) throws Exception{
        //获取数据
        List<Object> list = this.doBCPListPrc("APP_BANCHENGPING_KUCUN","10000", "1000", keyword, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
        if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
        //封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
    }
    //执行存储获取数据——获取半成品条码库存明细
    private List doBCPListPrc(String prc_name, String factory, String company, String barcode, int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, barcode);// 物料或者条码
                cs.setInt(4, page);// 每页记录数
                cs.setInt(5, size);// 当前页码
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(7,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(8,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(9,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(7));
                result.add(cs.getString(8));
                if(cs.getString(7).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(9);

                    while(rs.next()){
                        Map m = new HashMap();
                        m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));//批次条码
                        m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));//产品编码
                        m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));//批次
                        m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));//条码数量
                        m.put("OLDQTY", getEmpty(rs.getString("OLDQTY")));//剩余数量
                        m.put("MOCODE", getEmpty(rs.getString("MOCODE")));//状态
                        m.put("LIFNO", getEmpty(rs.getString("LIFNO")));//投料单
                        m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));//工序
                        m.put("LINE_NO", getEmpty(rs.getString("LINE_NO")));//线体
                        m.put("FBZ", getEmpty(rs.getString("FBZ")));//备注
                        m.put("ITEM_NAME", getEmpty(rs.getString("ITEM_NAME")));//产品名称
                        m.put("ITEM_MODEL", getEmpty(rs.getString("ITEM_MODEL")));//产品规格
                        m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));//入库日期
                        m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));//工序名称
                        m.put("HH", getEmpty(rs.getString("HH")));//在线时长
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(6));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }

	@Override
	public ApiResponseResult getCom1List(String prc_name,String pname, String keyword, String stime, String etime,
			PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		//获取数据
		//获取数据
        List<Object> list = this.doCom1ListPrc(prc_name,pname,"10000", "1000", keyword, stime, etime, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
        if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
        //封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doCom1ListPrc(String prc_name,String pname, String factory, String company, String keyword, String stime,String etime,int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, stime);// 开始时间
                cs.setString(4, etime);// 结束时间
                cs.setString(5, keyword);// 
                cs.setString(6, pname);// 工序
                cs.setInt(7, page);// 每页记录数
                cs.setInt(8, size);// 当前页码
                cs.registerOutParameter(9,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(10,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(11,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(12,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(10));
                result.add(cs.getString(11));
                if(cs.getString(10).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(12);

                    while(rs.next()){
                        Map m = ImpUtils.getMapByPro(prc_name, rs);
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(9));
                System.out.println(l);
                return result;
            }
        });

        return resultList;
    }
    
    @Override
	public ApiResponseResult getComList(String prc_name,String pname,  String stime, String etime,
			PageRequest pageRequest) throws Exception {
		// TODO Auto-generated method stub
		//获取数据
		//获取数据
        List<Object> list = this.doComListPrc(prc_name,pname,"10000", "1000",  stime, etime, pageRequest.getPageSize(), pageRequest.getPageNumber()+1);
        if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
        //封装数据
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("rows", list.get(2));
        map.put("page", pageRequest.getPageNumber() + 1);
        map.put("pageSize", pageRequest.getPageSize());
        map.put("total", list.get(3));

        return ApiResponseResult.success().data(map);
	}
	//执行存储获取数据
    private List doComListPrc(String prc_name,String pname, String factory, String company,  String stime,String etime,int page, int size){
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, factory);// 工厂
                cs.setString(2, company);// 公司
                cs.setString(3, stime);// 开始时间
                cs.setString(4, etime);// 结束时间
                cs.setString(5, pname);// 
                cs.setInt(6, page);// 每页记录数
                cs.setInt(7, size);// 当前页码
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(9,java.sql.Types.INTEGER);// 输出参数 返回标识
                cs.registerOutParameter(10,java.sql.Types.VARCHAR);// 输出参数 返回错误信息
                cs.registerOutParameter(11,-10);// 输出参数 追溯数据
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
                List l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(9));
                result.add(cs.getString(10));
                if(cs.getString(9).toString().equals("0")){
                    //游标处理
                    ResultSet rs = (ResultSet)cs.getObject(11);

                    while(rs.next()){
                        Map m = ImpUtils.getMapByPro(prc_name, rs);
                        l.add(m);
                    }
                    result.add(l);
                }
                result.add(cs.getString(8));
                System.out.println(l);
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

	@Override
    public ApiResponseResult getExcel(String keyword, HttpServletResponse response) throws Exception{
        try{
            //1.获取信息
            //1.1获取物料信息
            List<Map<String, Object>> list1 = new ArrayList<>();
            try{
                List<Object> mateList = this.doMaterialsListPrc("10000", "1000", keyword, 10000, 1);
                list1 = (List<Map<String, Object>>) mateList.get(2);
            }catch (Exception e){
            }

            //1.2获取品质信息
            List<Map<String, Object>> list2 = new ArrayList<>();
            try{
                List<Object> qualityList = this.doQualityListPrc("10000", "1000", keyword, 10000, 1);
                list2 = (List<Map<String, Object>>) qualityList.get(2);
            }catch (Exception e){
            }

            //1.3获取人员设备信息
            List<Map<String, Object>> list3 = new ArrayList<>();
            try{
                List<Object> deviceList = this.doDeviceListPrc("10000", "1000", keyword, 10000, 1);
                list3 = (List<Map<String, Object>>) deviceList.get(2);
            }catch(Exception e){
            }

            //1.4获取工装夹具信息
            List<Map<String, Object>> list4 = new ArrayList<>();
            try{
                List<Object> fixtureList = this.doFixtureListPrc("10000", "1000", keyword, 10000, 1);
                list4 = (List<Map<String, Object>>) fixtureList.get(2);
            }catch (Exception e){
            }

            //2.创建Excel文件
            OutputStream outputStream = response.getOutputStream();
            XSSFWorkbook workbook = new XSSFWorkbook();   //创建一个工作簿
            //Sheet sheet = workbook.createSheet("物料信息");
            List<XSSFCellStyle> cellStyleList = this.getStyle(workbook);
            //List<String> headerList = new ArrayList<String>(); //初始化
            //List<List<String>> bodyList = new ArrayList<>();//初始化

            //2.1创建物料信息Sheet页
            workbook = this.getMaterialsSheet(workbook, list1, cellStyleList);

            //2.2创建品质信息Sheet页
            workbook = this.getQualitySheet(workbook, list2, cellStyleList);

            //2.3创建人员设备信息Sheet页
            workbook = this.getDeviceSheet(workbook, list3, cellStyleList);

            //2.4创建工装夹具信息Sheet页
            workbook = this.getFixtureSheet(workbook, list4, cellStyleList);

            response.reset();
            response.setContentType("multipart/form-data");
            String fileName = URLEncoder.encode("全流程追溯报表" + keyword, "UTF-8")+ ".xlsx";
            response.setHeader("Content-disposition", "attachment; filename=" + fileName);
            workbook.write(outputStream);

            return ApiResponseResult.success("导出成功！");
        }catch (Exception e){
            return ApiResponseResult.failure("导出失败！");
        }
    }

    //获取物料信息Sheet页
    private XSSFWorkbook getMaterialsSheet(XSSFWorkbook workbook, List<Map<String, Object>> list1, List<XSSFCellStyle> cellStyleList){
        Sheet sheet = workbook.createSheet("物料信息");
        List<String> headerList = new ArrayList<String>(); //初始化
        List<List<String>> bodyList = new ArrayList<>();//初始化

        if(list1.size() > 0){
            for(int i = 0; i < list1.size(); i++){
                Map<String, Object> map = list1.get(i);
                List<String> body = new ArrayList<>();
                if(map != null){
                    body.add(String.valueOf(i + 1));//序号
                    body.add(map.get("PROC_NO")!=null ? map.get("PROC_NO").toString() : "");//工序编号
                    body.add(map.get("PROC_NAME")!=null ? map.get("PROC_NAME").toString() : "");//工序
                    body.add(map.get("ITEM_BARCODE")!=null ? map.get("ITEM_BARCODE").toString() : "");//物料条码
                    body.add(map.get("ITEM_NO")!=null ? map.get("ITEM_NO").toString() : "");//物料编号
                    body.add(map.get("ITEM_NAME")!=null ? map.get("ITEM_NAME").toString() : "");//物料名称
                    body.add(map.get("TR_LOTNO")!=null ? map.get("TR_LOTNO").toString() : "");//物料批次
                    body.add(map.get("SUPP_NO")!=null ? map.get("SUPP_NO").toString() : "");//供应商
                    body.add(map.get("QUANTITY")!=null ? map.get("QUANTITY").toString() : "");//物料用量
                    body.add(map.get("UNIT")!=null ? map.get("UNIT").toString() : "");//单位
                    body.add(map.get("LOT_DATE")!=null ? map.get("LOT_DATE").toString() : "");//来料日期
                    body.add(map.get("PLAN_QTY")!=null ? map.get("PLAN_QTY").toString() : "");//工单计划数量
                    body.add(map.get("FPUT_QTY")!=null ? map.get("FPUT_QTY").toString() : "");//本次生产数量
                    bodyList.add(body);
                }
            }
        }

        //创建表头信息
        headerList.add("序号");//1
        headerList.add("工序编号");//2
        headerList.add("工序");//3
        headerList.add("物料条码");//4
        headerList.add("物料编号");//5
        headerList.add("物料名称");//6
        headerList.add("物料批次");//7
        headerList.add("供应商");//8
        headerList.add("物料用量");//9
        headerList.add("单位");//10
        headerList.add("来料日期");//11
        headerList.add("工单计划数量");//12
        headerList.add("本次生产数量");//13

        //创建行（标题）
        Row createRow = sheet.createRow(0);
        for(int i = 0; i < headerList.size(); i++){
            createRow.createCell(i);
        }
        //设置行高
        sheet.getRow(0).setHeightInPoints((float) 25);
        //添加样式和数据
        for(int i = 0; i < 1; i++){
            Cell cell = sheet.getRow(0).getCell(0);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("物料信息");
            cell.setCellStyle(cellStyleList.get(2));
        }
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(0,0,0,headerList.size()-1);
        sheet.addMergedRegion(region1);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region1, sheet, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region1, sheet, workbook);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region1, sheet, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region1, sheet, workbook);

        //创建行（表头）
        Row createRow1 = sheet.createRow(1);
        for(int i = 0; i < headerList.size(); i++){
            createRow1.createCell(i);
        }
        //设置列宽
        for(int i = 0; i < headerList.size(); i++){
            if(headerList.get(i).equals("物料条码")){
                sheet.setColumnWidth(i, 30*256);
            }else if(headerList.get(i).equals("物料编号")|| headerList.get(i).equals("物料名称")){
                sheet.setColumnWidth(i, 20*256);
            }else if( headerList.get(i).equals("物料批次") || headerList.get(i).equals("工单计划数量") || headerList.get(i).equals("本次生产数量")){
                sheet.setColumnWidth(i, 15*256);
            }else{
                sheet.setColumnWidth(i, 12*256);
            }
        }
        //添加样式和数据
        for(int i = 0; i < headerList.size(); i++){
            Cell cell = sheet.getRow(1).getCell(i);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(headerList.get(i));
            cell.setCellStyle(cellStyleList.get(0));
        }

        //创建表内容信息
        for(int i = 0; i < bodyList.size(); i++){
            Row createRow2 = sheet.createRow(i + 2);
            for(int j = 0; j < headerList.size(); j++){
                createRow2.createCell(j);
            }
            //设置行高
            //sheet.getRow(i + 2).setHeightInPoints((float) 15.8);
            //添加样式和数据
            for(int k = 0; k < headerList.size(); k++){
                Cell cell = sheet.getRow(i + 2).getCell(k);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(bodyList.size() <= 0 ? "" : bodyList.get(i).get(k));
                cell.setCellStyle(cellStyleList.get(1));
            }
        }

        return workbook;
    }

    //获取品质信息Sheet页
    private XSSFWorkbook getQualitySheet(XSSFWorkbook workbook, List<Map<String, Object>> list2, List<XSSFCellStyle> cellStyleList){
        Sheet sheet2 = workbook.createSheet("品质信息");
        List<String> headerList = new ArrayList<String>(); //初始化
        List<List<String>> bodyList = new ArrayList<>();//初始化

        if(list2.size() > 0){
            for(int i = 0; i < list2.size(); i++){
                Map<String, Object> map = list2.get(i);
                List<String> body = new ArrayList<>();
                if(map != null){
                    body.add(String.valueOf(i + 1));//序号
                    body.add(map.get("LOT_NO")!=null ? map.get("LOT_NO").toString() : "");//批次号
                    body.add(map.get("PROC_NO")!=null ? map.get("PROC_NO").toString() : "");//工序编号
                    body.add(map.get("PROC_NAME")!=null ? map.get("PROC_NAME").toString() : "");//工序
                    body.add(map.get("FCHECK_ITEM")!=null ? map.get("FCHECK_ITEM").toString() : "");//首件项目
                    body.add(map.get("FSTAND")!=null ? map.get("FSTAND").toString() : "");//标准值
                    body.add(map.get("FDOWN_ALLOW")!=null ? map.get("FDOWN_ALLOW").toString() : "");//下公差
                    body.add(map.get("FUP_ALLOW")!=null ? map.get("FUP_ALLOW").toString() : "");//上公差
                    body.add(map.get("FSPEC_REQU")!=null ? map.get("FSPEC_REQU").toString() : "");//单位
                    body.add(map.get("VCHECK_RESU")!=null ? map.get("VCHECK_RESU").toString() : "");//检验值
                    body.add(map.get("FSECOND_RESU")!=null ? map.get("FSECOND_RESU").toString() : "");//检验结果
                    body.add(map.get("FCHECK_BY")!=null ? map.get("FCHECK_BY").toString() : "");//检验人员
                    body.add(map.get("FCHECK_DATE")!=null ? map.get("FCHECK_DATE").toString() : "");//检验时间
                    bodyList.add(body);
                }
            }
        }

        //创建表头信息
        headerList.add("序号");//1
        headerList.add("批次号");//2
        headerList.add("工序编号");//3
        headerList.add("工序");//4
        headerList.add("首件项目");//5
        headerList.add("标准值");//6
        headerList.add("下公差");//7
        headerList.add("上公差");//8
        headerList.add("单位");//9
        headerList.add("检验值");//10
        headerList.add("检验结果");//11
        headerList.add("检验人员");//12
        headerList.add("检验时间");//13

        //创建行（标题）
        Row createRow = sheet2.createRow(0);
        for(int i = 0; i < headerList.size(); i++){
            createRow.createCell(i);
        }
        //设置行高
        sheet2.getRow(0).setHeightInPoints((float) 25);
        //添加样式和数据
        for(int i = 0; i < 1; i++){
            Cell cell = sheet2.getRow(0).getCell(0);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("品质信息");
            cell.setCellStyle(cellStyleList.get(2));
        }
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(0,0,0,headerList.size()-1);
        sheet2.addMergedRegion(region1);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region1, sheet2, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region1, sheet2, workbook);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region1, sheet2, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region1, sheet2, workbook);
        //创建行（表头）
        Row createRow1 = sheet2.createRow(1);
        for(int i = 0; i < headerList.size(); i++){
            createRow1.createCell(i);
        }
        //设置列宽
        for(int i = 0; i < headerList.size(); i++){
            if(headerList.get(i).equals("批次号") || headerList.get(i).equals("检验时间")){
                sheet2.setColumnWidth(i, 20*256);
            }else{
                sheet2.setColumnWidth(i, 12*256);
            }
        }
        //添加样式和数据
        for(int i = 0; i < headerList.size(); i++){
            Cell cell = sheet2.getRow(1).getCell(i);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(headerList.get(i));
            cell.setCellStyle(cellStyleList.get(0));
        }
        //创建表内容信息
        for(int i = 0; i < bodyList.size(); i++){
            Row createRow2 = sheet2.createRow(i + 2);
            for(int j = 0; j < headerList.size(); j++){
                createRow2.createCell(j);
            }
            //设置行高
            //sheet2.getRow(i + 2).setHeightInPoints((float) 15.8);
            //添加样式和数据
            for(int k = 0; k < headerList.size(); k++){
                Cell cell = sheet2.getRow(i + 2).getCell(k);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(bodyList.size() <= 0 ? "" : bodyList.get(i).get(k));
                cell.setCellStyle(cellStyleList.get(1));
            }
        }

        return workbook;
    }

    //获取人员设备信息Sheet页
    private XSSFWorkbook getDeviceSheet(XSSFWorkbook workbook, List<Map<String, Object>> list3, List<XSSFCellStyle> cellStyleList){
        Sheet sheet3 = workbook.createSheet("人员设备信息");
        List<String> headerList = new ArrayList<String>(); //初始化
        List<List<String>> bodyList = new ArrayList<>();//初始化

        if(list3.size() > 0){
            for(int i = 0; i < list3.size(); i++){
                Map<String, Object> map = list3.get(i);
                List<String> body = new ArrayList<>();
                if(map != null){
                    body.add(String.valueOf(i + 1));//序号
                    body.add(map.get("PROC_NAME")!=null ? map.get("PROC_NAME").toString() : "");//工序
                    body.add(map.get("PROC_NO")!=null ? map.get("PROC_NO").toString() : "");//工序编号
                    body.add(map.get("MACHINE_CODE")!=null ? map.get("MACHINE_CODE").toString() : "");//设备编号
                    body.add(map.get("EQ_NAME")!=null ? map.get("EQ_NAME").toString() : "");//设备名称
                    body.add(map.get("CREATE_BY")!=null ? map.get("CREATE_BY").toString() : "");//作业员
                    body.add(map.get("CREATE_DATE")!=null ? map.get("CREATE_DATE").toString() : "");//生产时间
                    bodyList.add(body);
                }
            }
        }

        //创建表头信息
        headerList.add("序号");//1
        headerList.add("工序");//2
        headerList.add("工序编号");//3
        headerList.add("设备编号");//4
        headerList.add("设备名称");//5
        headerList.add("作业员");//6
        headerList.add("生产时间");//7

        //创建行（标题）
        Row createRow = sheet3.createRow(0);
        for(int i = 0; i < headerList.size(); i++){
            createRow.createCell(i);
        }
        //设置行高
        sheet3.getRow(0).setHeightInPoints((float) 25);
        //添加样式和数据
        for(int i = 0; i < 1; i++){
            Cell cell = sheet3.getRow(0).getCell(0);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("人员设备信息");
            cell.setCellStyle(cellStyleList.get(2));
        }
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(0,0,0,headerList.size()-1);
        sheet3.addMergedRegion(region1);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region1, sheet3, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region1, sheet3, workbook);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region1, sheet3, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region1, sheet3, workbook);
        //创建行（表头）
        Row createRow1 = sheet3.createRow(1);
        for(int i = 0; i < headerList.size(); i++){
            createRow1.createCell(i);
        }
        //设置列宽
        for(int i = 0; i < headerList.size(); i++){
            if(headerList.get(i).equals("设备名称") || headerList.get(i).equals("生产时间")){
                sheet3.setColumnWidth(i, 20*256);
            }else if(headerList.get(i).equals("工序") || headerList.get(i).equals("设备编号")){
                sheet3.setColumnWidth(i, 15*256);
            }else{
                sheet3.setColumnWidth(i, 12*256);
            }
        }
        //添加样式和数据
        for(int i = 0; i < headerList.size(); i++){
            Cell cell = sheet3.getRow(1).getCell(i);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(headerList.get(i));
            cell.setCellStyle(cellStyleList.get(0));
        }
        //创建表内容信息
        for(int i = 0; i < bodyList.size(); i++){
            Row createRow2 = sheet3.createRow(i + 2);
            for(int j = 0; j < headerList.size(); j++){
                createRow2.createCell(j);
            }
            //设置行高
            //sheet2.getRow(i + 2).setHeightInPoints((float) 15.8);
            //添加样式和数据
            for(int k = 0; k < headerList.size(); k++){
                Cell cell = sheet3.getRow(i + 2).getCell(k);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(bodyList.size() <= 0 ? "" : bodyList.get(i).get(k));
                cell.setCellStyle(cellStyleList.get(1));
            }
        }

        return workbook;
    }

    //获取工装夹具信息Sheet页
    private XSSFWorkbook getFixtureSheet(XSSFWorkbook workbook, List<Map<String, Object>> list4, List<XSSFCellStyle> cellStyleList){
        Sheet sheet4 = workbook.createSheet("工装夹具信息");
        List<String> headerList = new ArrayList<String>(); //初始化
        List<List<String>> bodyList = new ArrayList<>();//初始化

        if(list4.size() > 0){
            for(int i = 0; i < list4.size(); i++){
                Map<String, Object> map = list4.get(i);
                List<String> body = new ArrayList<>();
                if(map != null){
                    body.add(String.valueOf(i + 1));//序号
                    body.add(map.get("CREATE_DATE")!=null ? map.get("CREATE_DATE").toString() : "");//上机时间
                    body.add(map.get("CREATE_BY")!=null ? map.get("CREATE_BY").toString() : "");//上机操作人
                    body.add(map.get("M_CODE")!=null ? map.get("M_CODE").toString() : "");//设备编号
                    body.add(map.get("EQ_NAME")!=null ? map.get("EQ_NAME").toString() : "");//设备名称
                    body.add(map.get("S_CODE")!=null ? map.get("S_CODE").toString() : "");//工装编号
                    body.add(map.get("CUT_NAME")!=null ? map.get("CUT_NAME").toString() : "");//工装名称
                    body.add(map.get("DOWN_DATE")!=null ? map.get("DOWN_DATE").toString() : "");//下机时间
                    body.add(map.get("DOWN_USER_BY")!=null ? map.get("DOWN_USER_BY").toString() : "");//下机操作人
                    body.add(map.get("NOW_TIME")!=null ? map.get("NOW_TIME").toString() : "");//本次使用次数/米数
                    body.add(map.get("AVAILABLE_TIME")!=null ? map.get("AVAILABLE_TIME").toString() : "");//可用次/米数
                    body.add(map.get("TOTAL_TIME")!=null ? map.get("TOTAL_TIME").toString() : "");//总使用次/米数
                    body.add(map.get("LOTNO")!=null ? map.get("LOTNO").toString() : "");//批次号
                    body.add(map.get("PROC_NO")!=null ? map.get("PROC_NO").toString() : "");//工序编号
                    body.add(map.get("PROC_NAME")!=null ? map.get("PROC_NAME").toString() : "");//工序名称
                    bodyList.add(body);
                }
            }
        }

        //创建表头信息
        headerList.add("序号");//1
        headerList.add("上机时间");//2
        headerList.add("上机操作人");//3
        headerList.add("设备编号");//4
        headerList.add("设备名称");//5
        headerList.add("工装编号");//6
        headerList.add("工装名称");//7
        headerList.add("下机时间");//8
        headerList.add("下机操作人");//9
        headerList.add("本次使用次数/米数");//10
        headerList.add("可用次/米数");//11
        headerList.add("总使用次/米数");//12
        headerList.add("批次号");//13
        headerList.add("工序编号");//14
        headerList.add("工序名称");//15

        //创建行（标题）
        Row createRow = sheet4.createRow(0);
        for(int i = 0; i < headerList.size(); i++){
            createRow.createCell(i);
        }
        //设置行高
        sheet4.getRow(0).setHeightInPoints((float) 25);
        //添加样式和数据
        for(int i = 0; i < 1; i++){
            Cell cell = sheet4.getRow(0).getCell(0);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue("工装夹具信息");
            cell.setCellStyle(cellStyleList.get(2));
        }
        //合并单元格
        CellRangeAddress region1 = new CellRangeAddress(0,0,0,headerList.size()-1);
        sheet4.addMergedRegion(region1);
        RegionUtil.setBorderTop(CellStyle.BORDER_THIN, region1, sheet4, workbook);
        RegionUtil.setBorderRight(CellStyle.BORDER_THIN, region1, sheet4, workbook);
        RegionUtil.setBorderBottom(CellStyle.BORDER_THIN, region1, sheet4, workbook);
        RegionUtil.setBorderLeft(CellStyle.BORDER_THIN, region1, sheet4, workbook);

        //创建行（表头）
        Row createRow1 = sheet4.createRow(1);
        for(int i = 0; i < headerList.size(); i++){
            createRow1.createCell(i);
        }
        //设置列宽
        for(int i = 0; i < headerList.size(); i++){
            if(headerList.get(i).equals("上机时间") || headerList.get(i).equals("下机时间") || headerList.get(i).equals("本次使用次数/米数")){
                sheet4.setColumnWidth(i, 20*256);
            }else if(headerList.get(i).equals("上机操作人") || headerList.get(i).equals("下机操作人") || headerList.get(i).equals("可用次/米数") || headerList.get(i).equals("总使用次/米数")){
                sheet4.setColumnWidth(i, 15*256);
            }else{
                sheet4.setColumnWidth(i, 12*256);
            }
        }
        //添加样式和数据
        for(int i = 0; i < headerList.size(); i++){
            Cell cell = sheet4.getRow(1).getCell(i);
            cell.setCellType(XSSFCell.CELL_TYPE_STRING);
            cell.setCellValue(headerList.get(i));
            cell.setCellStyle(cellStyleList.get(0));
        }

        //创建表内容信息
        for(int i = 0; i < bodyList.size(); i++){
            Row createRow2 = sheet4.createRow(i + 2);
            for(int j = 0; j < headerList.size(); j++){
                createRow2.createCell(j);
            }
            //设置行高
            //sheet.getRow(i + 2).setHeightInPoints((float) 15.8);
            //添加样式和数据
            for(int k = 0; k < headerList.size(); k++){
                Cell cell = sheet4.getRow(i + 2).getCell(k);
                cell.setCellType(XSSFCell.CELL_TYPE_STRING);
                cell.setCellValue(bodyList.size() <= 0 ? "" : bodyList.get(i).get(k));
                cell.setCellStyle(cellStyleList.get(1));
            }
        }

        return workbook;
    }

    //Excel样式
    public List<XSSFCellStyle> getStyle(XSSFWorkbook workbook) {
        List<XSSFCellStyle> cellStyleList = new ArrayList<XSSFCellStyle>();

        //添加字体
        //0.
        XSSFFont font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 12);
        font.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);  //字体加粗

        //1.
        XSSFFont font1 = workbook.createFont();
        font1.setFontName("宋体");
        font1.setFontHeightInPoints((short) 11);

        //2.
        XSSFFont font2 = workbook.createFont();
        font2.setFontName("宋体");
        font2.setFontHeightInPoints((short) 18);
        font2.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);  //字体加粗
        font2.setColor(HSSFColor.WHITE.index);//设置excel数据字体颜色

        //添加样式
        //0.实线边框 + 宋体 + 加粗 + 左对齐 + 垂直居中
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setBorderTop(CellStyle.BORDER_THIN);  //上边框
        cellStyle.setBorderRight(CellStyle.BORDER_THIN);  //右边框
        cellStyle.setBorderBottom(CellStyle.BORDER_THIN);  //下边框
        cellStyle.setBorderLeft(CellStyle.BORDER_THIN);  //左边框
        cellStyle.setAlignment(CellStyle.ALIGN_LEFT);  //左对齐
        cellStyle.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  //垂直居中
        cellStyle.setWrapText(true);  //自动换行
        cellStyle.setFillForegroundColor(new XSSFColor(new Color(184, 204, 228)));//背景颜色
        //cellStyle.setFillForegroundColor(HSSFColor.PALE_BLUE.index);//背景颜色
        cellStyle.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyleList.add(cellStyle);

        //1.实线边框 + 宋体 + 左对齐 + 垂直居中
        XSSFCellStyle cellStyle1 = workbook.createCellStyle();
        cellStyle1.setFont(font1);
        cellStyle1.setBorderTop(CellStyle.BORDER_THIN);  //上边框
        cellStyle1.setBorderRight(CellStyle.BORDER_THIN);  //右边框
        cellStyle1.setBorderBottom(CellStyle.BORDER_THIN);  //下边框
        cellStyle1.setBorderLeft(CellStyle.BORDER_THIN);  //左边框
        cellStyle1.setAlignment(CellStyle.ALIGN_LEFT);  //左对齐
        cellStyle1.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  //垂直居中
        cellStyle1.setWrapText(true);  //自动换行
        cellStyleList.add(cellStyle1);

        //2.实线边框 + 宋体 + 水平居中 + 垂直居中
        XSSFCellStyle cellStyle2 = workbook.createCellStyle();
        cellStyle2.setFont(font2);
        cellStyle2.setBorderTop(CellStyle.BORDER_THIN);  //上边框
        cellStyle2.setBorderRight(CellStyle.BORDER_THIN);  //右边框
        cellStyle2.setBorderBottom(CellStyle.BORDER_THIN);  //下边框
        cellStyle2.setBorderLeft(CellStyle.BORDER_THIN);  //左边框
        cellStyle2.setAlignment(CellStyle.ALIGN_CENTER);  //水平居中
        cellStyle2.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  //垂直居中
        cellStyle2.setWrapText(true);  //自动换行
        cellStyle2.setFillForegroundColor(new XSSFColor(new Color(31, 73, 125)));//背景颜色
        //cellStyle2.setFillForegroundColor(HSSFColor.DARK_TEAL.index);//背景颜色
        cellStyle2.setFillPattern(CellStyle.SOLID_FOREGROUND);
        cellStyleList.add(cellStyle2);

        return cellStyleList;
    }
}
