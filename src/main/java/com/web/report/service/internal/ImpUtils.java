package com.web.report.service.internal;

import com.sun.mail.util.MailSSLSocketFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.security.GeneralSecurityException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 根据存储过程名称获取需要返回的map
 * @author fyx
 * @ClassName 
 * @Description
 */
@Component
public class ImpUtils {

    private static final Logger logger = LoggerFactory.getLogger(ImpUtils.class);
    
    /**
     * 发送邮件
     * @param subject 主题
     * @param toUsers 收件人
     * @param ccUsers 抄送人
     * @param content 邮件内容
     * @param attachfiles 附件列表  List<Map<String, String>> key: name && file
     * @throws SQLException 
     */
   public static Map getMapByPro(String pro_name,ResultSet rs) throws SQLException{
	   Map m = new HashMap();
	   switch(pro_name){
	   case "APP_ZHICHENGQM_HZ":
		   m.put("RELEASE_NO", getEmpty(rs.getString("RELEASE_NO")));//检验单号
		   m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));//批次
	       m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));//工序
	       m.put("PROC_NAME", getEmpty(rs.getString("PROC_NAME")));//工序名称
	       m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));//产品编码
	       m.put("ITEM_NAME", getEmpty(rs.getString("ITEM_NAME")));//
	       m.put("FCHECK_RESU", getEmpty(rs.getString("FCHECK_RESU")));
	       m.put("FCHECK_ITEM", getEmpty(rs.getString("FCHECK_ITEM")));
	       m.put("FSTAND", getEmpty(rs.getString("FSTAND")));
	       m.put("FUP_ALLOW", getEmpty(rs.getString("FUP_ALLOW")));
	       m.put("FDOWN_ALLOW", getEmpty(rs.getString("FDOWN_ALLOW")));
	       m.put("CHECK_QTY", getEmpty(rs.getString("CHECK_QTY")));
	       m.put("FSPEC_REQU", getEmpty(rs.getString("FSPEC_REQU")));

	       break;
	   case "APP_BANCHENGPEING_INOUT":
		   m.put("TASK_NO", getEmpty(rs.getString("TASK_NO")));
		   m.put("BOARD_ITEM", getEmpty(rs.getString("BOARD_ITEM")));
		   m.put("BOARD_NAME", getEmpty(rs.getString("BOARD_NAME")));
		   m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
		   m.put("LINE_NAME", getEmpty(rs.getString("LINE_NAME")));
		   m.put("TR", getEmpty(rs.getString("TR")));
		   m.put("CC", getEmpty(rs.getString("CC")));
		   m.put("UNIT", getEmpty(rs.getString("UNIT")));
		   m.put("RQ", getEmpty(rs.getString("RQ")));
		   
	       break;
	   case "APP_BANCHENGPING_CCMX":
		   m.put("TASK_NO", getEmpty(rs.getString("TASK_NO")));
		   m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));
		   m.put("ITEM_NO", getEmpty(rs.getString("ITEM_NO")));
		   m.put("LOT_NO", getEmpty(rs.getString("LOT_NO")));
		   m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));
		   m.put("CREATE_BY", getEmpty(rs.getString("CREATE_BY")));
		   m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));
		   m.put("FBZ", getEmpty(rs.getString("FBZ")));
		   m.put("LINE_NO", getEmpty(rs.getString("LINE_NO")));
		   m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
		   m.put("UNIT", getEmpty(rs.getString("UNIT")));
	       break;
	   case "APP_BANCHENGPING_TRMX":
		   m.put("TASK_NO", getEmpty(rs.getString("TASK_NO")));
		   m.put("LINE_NO", getEmpty(rs.getString("LINE_NO")));
		   m.put("PROC_NO", getEmpty(rs.getString("PROC_NO")));
		   m.put("MACHINE_CODE", getEmpty(rs.getString("MACHINE_CODE")));
		   m.put("ITEM_BARCODE", getEmpty(rs.getString("ITEM_BARCODE")));
		   m.put("ITEM_CODE", getEmpty(rs.getString("ITEM_CODE")));
		   m.put("QUANTITY", getEmpty(rs.getString("QUANTITY")));
		   m.put("UNIT", getEmpty(rs.getString("UNIT")));
		   m.put("ITEM_LOT", getEmpty(rs.getString("ITEM_LOT")));
		   m.put("FBZ", getEmpty(rs.getString("FBZ")));
		   m.put("CREATE_DATE", getEmpty(rs.getString("CREATE_DATE")));
	       break;  
	   case "APP_ZY_DATA":
		   m.put("BARCODE", getEmpty(rs.getString("BARCODE")));
		   m.put("SCANTIME", getEmpty(rs.getString("SCANTIME")));
		   m.put("RESULT", getEmpty(rs.getString("RESULT")));
		   m.put("FWEIGHT", getEmpty(rs.getString("FWEIGHT")));
		   m.put("FW_TIME", getEmpty(rs.getString("FW_TIME")));
		   m.put("FW_RESULT", getEmpty(rs.getString("FW_RESULT")));
		   m.put("LWEIGHT", getEmpty(rs.getString("LWEIGHT")));
		   m.put("LW_TIME", getEmpty(rs.getString("LW_TIME")));
		   m.put("FILLINGNUM_PV", getEmpty(rs.getString("FILLINGNUM_PV")));
		   m.put("FILLINGNUM_SV", getEmpty(rs.getString("FILLINGNUM_SV")));
		   m.put("DEVIATION_PV", getEmpty(rs.getString("DEVIATION_PV")));
		   m.put("DEVIATION_SV", getEmpty(rs.getString("DEVIATION_SV")));
		   m.put("LOTNO", getEmpty(rs.getString("LOTNO")));
		   m.put("LINENO", getEmpty(rs.getString("LINENO")));
		   m.put("TAG", getEmpty(rs.getString("TAG")));
		   m.put("ROWNUMS", getEmpty(rs.getString("ROWNUMS")));
		   m.put("COLUMNNUMS", getEmpty(rs.getString("COLUMNNUMS")));
	       break;    
	   }
       
       return m;
   }
 //值为"null"或者null转换成""
   private static String getEmpty(String str){
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
}

