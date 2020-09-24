package com.web.app.service.internal;

import com.app.base.data.ApiResponseResult;
import com.web.app.service.DeviceInventoryService;
import com.web.report.service.internal.ImpUtils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service(value = "DeviceInventoryService")
@Transactional(propagation = Propagation.REQUIRED)
public class DeviceInventoryImpl implements DeviceInventoryService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult judgeUserPower(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.judgeUserPowerPrc(usercode, "PRC_EQ_Stocktaking_GetPower");
        System.out.println(list);
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		List<Map<String, Object>> l_last= new ArrayList<Map<String, Object>>();
		Map<String, Object> m_new = new HashMap<String, Object>();
		m_new.put("billNo", list.get(2).toString());
		m_new.put("area", list.get(3).toString());
		m_new.put("timeInterval",list.get(4).toString());
		l_last.add(m_new);
		
		return ApiResponseResult.success().data(l_last);//返回数据集
	}
	//执行存储获取数据
    private List judgeUserPowerPrc(String usercode,String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);
                cs.registerOutParameter(2,java.sql.Types.INTEGER);//返回标识
                cs.registerOutParameter(3,java.sql.Types.VARCHAR);// 
                cs.registerOutParameter(4,java.sql.Types.VARCHAR);// 
                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 
                cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 
                
                return cs;
            }
        }, new CallableStatementCallback() {
			public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
				List<Object> result = new ArrayList<>();
				cs.execute();
				result.add(cs.getInt(2));
				result.add(cs.getString(3));
				result.add(cs.getString(4));
				result.add(cs.getString(5));
				result.add(cs.getString(6));
				return result;
			}
        });
        return resultList;
    }
   //判断盘点区域是否正确
    @Override
	public ApiResponseResult judgeArea( 
			String usercode,
    		String area,
    		String right_area,//被授权的区域
    		String check_id//盘点单号
			) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.judgeAreaPrc(usercode,area,right_area,check_id,
				"PRC_EQ_Stocktaking_AreaJudge");  
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		return ApiResponseResult.success(list.toString());
	}
    private List judgeAreaPrc(
    		String usercode,
    		String area,
    		String right_area,
    		String check_id,String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.setString(2, area);// 区域
                cs.setString(3, right_area);// 授权的盘点区域
                cs.setString(4, check_id);// 盘点单号
                cs.registerOutParameter(5,java.sql.Types.INTEGER);// 返回标识
                cs.registerOutParameter(6,java.sql.Types.VARCHAR);// 输出参数 
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
//                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(5));
                result.add(cs.getString(6));
                return result;
            }
        });
        return resultList;
    }
  //判断盘点设备
    @Override
	public ApiResponseResult judgeDevice( 
			String usercode,
			String check_id,//盘点单号
			String eq_code,//设备编号
    		String area//当前区域
			) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.judgeDevicePrc(usercode,check_id,eq_code,area,
				"PRC_EQ_Stocktaking_Auto");
		List<Map<String, Object>> l_last= new ArrayList<Map<String, Object>>();
		Map<String, Object> m_new = new HashMap<String, Object>();
		m_new.put("result", list.get(1).toString());
		if(list.get(2)==null){
			m_new.put("old_location", "");
		}else{
			m_new.put("old_location", list.get(2).toString());
		}
		l_last.add(m_new);
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure().data(l_last);
        }
		
		return ApiResponseResult.success().data(l_last);
	}
    //执行
    private List judgeDevicePrc(
    		String usercode,
    		String check_id,//盘点单号
    		String eq_code,//设备编号
    		String area,//当前区域
    		String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.setString(2, check_id);// 盘点单号
                cs.setString(3, eq_code);// 设备
                cs.setString(4, area);// 区域
                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 输出参数  原来的区域
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 返回标识
                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 输出参数 
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
//                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(6));
                result.add(cs.getString(7));
                result.add(cs.getString(5));
                return result;
            }
        });
        return resultList;
    }
    
  //执行盘点
    @Override
	public ApiResponseResult sumbitInventory( 
			String usercode,
			String check_id,//盘点单号
			String eq_code,//设备编号
    		String area//当前区域
			) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.judgeDevicePrc(usercode,check_id,eq_code,area,
				"PRC_EQ_Stocktaking_Manual");  
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		return ApiResponseResult.success(list.get(1).toString());
	}
    //执行
    private List sumbitInventory(
    		String usercode,
    		String check_id,//盘点单号
    		String eq_code,//设备编号
    		String area, //当前区域 
    		String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.setString(2, check_id);// 
                cs.setString(3, eq_code);// 
                cs.setString(4, area);// 
                cs.registerOutParameter(5,java.sql.Types.VARCHAR);// 输出参数  原来的区域
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 返回标识
                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 输出参数 
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
//                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getInt(6));
                result.add(cs.getString(7));
                result.add(cs.getString(5));
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
