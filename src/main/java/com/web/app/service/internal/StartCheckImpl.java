package com.web.app.service.internal;

import com.app.base.data.ApiResponseResult;
import com.web.app.service.StartCheckService;
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

@Service(value = "StartService")
@Transactional(propagation = Propagation.REQUIRED)
public class StartCheckImpl implements StartCheckService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

	@Override
	public ApiResponseResult getStartList(String usercode) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.getStartListPrc(usercode, "PRC_Produce_GetWOInfo");
        
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		List<Map<String, Object>> l = (List<Map<String, Object>>) list.get(2);
		//处理数据
		//去掉重复的WORL_SINGNUM的记录
		List<Map<String, Object>> l_new = new ArrayList<Map<String, Object>>();
		l_new.add(l.get(0));
		for(int i=1;i<l.size();i++){
			Map<String, Object> map0 = l.get(i-1);
			Map<String, Object> map1 = l.get(i);
			String task_no0 = map0.get("WORL_SINGNUM").toString();
			String task_no1 = map1.get("WORL_SINGNUM").toString();
			if(!task_no0.equals(task_no1)){
				l_new.add(map1);
			}
		}
		//挨个获取新的WORL_SINGNUM的字数据
		List<Map<String, Object>> l_last= new ArrayList<Map<String, Object>>();
		for(int j=0;j<l_new.size();j++){
			List<Map<String, Object>> child = new ArrayList<Map<String, Object>>();
			for(int k=0;k<l.size();k++){
				if(l_new.get(j).get("WORL_SINGNUM").toString().equals(l.get(k).get("WORL_SINGNUM").toString())){
					Map<String, Object> m = l.get(k);
					Map<String, Object> m_new = new HashMap<String, Object>();
					m_new.put("WORKSHOP_CENTER_CODE", m.get("WORKSHOP_CENTER_CODE").toString());
					m_new.put("WORPROC_CODE", m.get("WORPROC_CODE").toString());
					m_new.put("WORPROC_NAME", m.get("WORPROC_NAME").toString());
					m_new.put("EQU_CODE", m.get("EQU_CODE").toString());
					m_new.put("EQU_NAME", m.get("EQU_NAME").toString());
					m_new.put("STATUS", m.get("STATUS").toString());
					child.add(m_new);
				}
			}
			Map<String, Object> m = l_new.get(j);
			Map<String, Object> m_new = new HashMap<String, Object>();
			m_new.put("WORL_SINGNUM", m.get("WORL_SINGNUM").toString());
			m_new.put("PRO_NAME", m.get("PRO_NAME").toString());
			m_new.put("PRO_CODE", m.get("PRO_CODE").toString());
			m_new.put("PROD_DATE_END", m.get("PROD_DATE_END").toString());
			m_new.put("PROD_DATE", m.get("PROD_DATE").toString());
			m_new.put("WORL_QTY", m.get("WORL_QTY").toString());
			m_new.put("COMPLETE_QTY", m.get("COMPLETE_QTY").toString());
			m_new.put("Child", child);
			l_last.add(m_new);
		}
		return ApiResponseResult.success().data(l_last);//返回数据集
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
	public ApiResponseResult addStartCheck( //开工授权
			String usercode,
    		String proc,
    		String workCenter,
    		String taskNo,
    		String eq_code,
    		String staffNoInfo,
    		String eq_id_Info
			) throws Exception {
		// TODO Auto-generated method stub
		List<Object> list = this.addStartCheckPrc(usercode,proc,workCenter,taskNo,eq_code,
				staffNoInfo,eq_id_Info, "PRC_Produce_Authorization");  
		if(!list.get(0).toString().equals("0")){
            return ApiResponseResult.failure(list.get(1).toString());
        }
		return ApiResponseResult.success(list.get(1).toString());
	}
    //执行开工授权存储过程
    private List addStartCheckPrc(
    		String usercode,
    		String proc,
    		String workCenter,
    		String taskNo,
    		String eq_code,
    		String staffNoInfo,
    		String eq_id_Info,String prc_name)throws Exception{
        List resultList = (List) jdbcTemplate.execute(new CallableStatementCreator() {
            @Override
            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                String storedProc = "{call "+prc_name+"(?,?,?,?,?,?,?,?,?)}";// 调用的sql
                CallableStatement cs = con.prepareCall(storedProc);
                cs.setString(1, usercode);// 账号
                cs.setString(2, proc);// 工序
                cs.setString(3, workCenter);// 工作中心
                cs.setString(4, taskNo);// 工单
                cs.setString(5, eq_code);// 设备编号
                cs.setString(6, staffNoInfo);// 工号信息
                cs.setString(7, eq_id_Info);// 设备ID
                cs.registerOutParameter(8,java.sql.Types.INTEGER);// 返回标识
                cs.registerOutParameter(9,java.sql.Types.VARCHAR);// 输出参数 
                return cs;
            }
        }, new CallableStatementCallback() {
            public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                List<Object> result = new ArrayList<>();
//                List<Map<String, Object>> l = new ArrayList();
                cs.execute();
                result.add(cs.getString(8));
                result.add(cs.getString(9));
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
