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

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.util.ClassUtils;

public class ReportPrcUtils {

    private JdbcTemplate jdbcTemplate;
	
	@Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
	//查询数据,使用RowCallbackHandler处理结果集
    public InputStream getUserImg(String fcode){
        String sql = "select u.EXP_FIELD3 from sys_user u where u.fcode=?";
        InputStream inn = null;
        //将结果集数据行中的数据抽取到forum对象中
        jdbcTemplate.query(sql, new Object[]{fcode}, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
            	InputStream in = rs.getBinaryStream("EXP_FIELD3");
            	saveImg(in,fcode);
            }
        });
        return inn;
    }
    
    public InputStream getFaImg(String itemno){
        String sql = "Select s.Fj_Dz SOP From MES_BOARD_SOP_FJ s  join MES_BOARD_BASE_INFO t on s.Mid = t.id where t.board_item=?";
        InputStream inn = null;
        //将结果集数据行中的数据抽取到forum对象中
        jdbcTemplate.query(sql, new Object[]{itemno}, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
            	InputStream in = rs.getBinaryStream("SOP");
            	saveImg(in,itemno);
            }
        });
        return inn;
    }
    
    public InputStream getHuanImg(String area){
        String sql = "Select s.fj_dz  AREA From Mes_Area_Fj s JOIN  Mes_Area T ON s.Mid =T.ID Where t.Fcode=?";
        InputStream inn = null;
        //将结果集数据行中的数据抽取到forum对象中
        jdbcTemplate.query(sql, new Object[]{area}, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
            	InputStream in = rs.getBinaryStream("AREA");
            	saveImg(in,area);
            }
        });
        return inn;
    }
    
    public void saveImg(InputStream inputStream,String fcode){
    	String location = System.getProperty("user.dir") ;
		//获取项目classes/static的地址
        //String staticPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        System.out.println(File.separator);
     // 图片保存路径
        //String savePath = new String("src"+ File.separator+"main"+ File.separator+"resources"+ File.separator+"static"+ File.separator+"downImages" + File.separator+fcode+ ".png");
      //获取项目classes/static的地址
        String staticPath = ClassUtils.getDefaultClassLoader().getResource("static").getPath();
        String fileName = fcode+ ".png";  //获取文件名
        
        // 图片存储目录及图片名称
        String url_path = "downImages" + File.separator + fileName;
        //图片保存路径
        String savePath = staticPath + File.separator + url_path;
        
        //System.out.println("图片保存地址："+savePath);

        File saveFile = new File(savePath);
        
        try {
			FileUtils.copyInputStreamToFile(inputStream, saveFile);
		} catch (IOException e) {
			System.out.println(e.toString());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
