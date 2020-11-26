package com.web.app.service.internal;

import com.app.base.data.ApiResponseResult;
import com.web.app.service.ProduceSuspendService;
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

/**
 * 生产停工
 * */
@Service(value = "ProduceSuspendService")
@Transactional(propagation = Propagation.REQUIRED)
public class ProduceSuspendImpl implements ProduceSuspendService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getList(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getStartListPrc(usercode, "PRC_Produce_WO_GetInfo");
        
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		return ApiResponseResult.success().data(list.get(2));//返回数据集
	}
	private String getNull(Object o){
		if(o == null){
			return "";
		}
		return o.toString();
	}
	//执行存储获取数据
    private List getStartListPrc(String usercode,String prc_name)throws Exception{
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
	public ApiResponseResult changeStatus( //开工授权
			String usercode,
    		String taskNo,
    		String itemNo,
    		String plan_id,
    		String status
    		) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.changeStatusPrc(usercode,taskNo,itemNo,plan_id,status,
				"PRC_Produce_WO_ChangeStatus");  
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		return ApiResponseResult.success(list.get(1).toString());
	}
    //执行开工授权存储过程
    private List changeStatusPrc(
    		String usercode,
    		String taskNo,
    		String itemNo,
    		String plan_id,
    		String status,String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.setString(2, taskNo);// 工序
                cs.setString(3, itemNo);// 工作中心
                cs.setString(4, plan_id);// 工单
                cs.setString(5, status);//状态
                cs.registerOutParameter(6,java.sql.Types.INTEGER);// 返回标识
                cs.registerOutParameter(7,java.sql.Types.VARCHAR);// 输出参数 
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
//                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getString(6));
                result.add(cs.getString(7));
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
