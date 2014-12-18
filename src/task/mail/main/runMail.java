package task.mail.main;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.log4j.Logger;

import task.mail.c3p0.DatabaseMysqlImpl;
import task.mail.constant.PropertiesLoader;
import task.mail.utils.DateUtils;
import task.mail.utils.MailUtils;

public class runMail {
	private static final Logger logger = Logger.getLogger(runMail.class);
	
	public static void main(String[] args) {
		String path = "jdbc.xml";
		if (args.length > 0) {
			path = args[0];
		}

		PropertiesLoader propertiesLoader = new PropertiesLoader();
		logger.debug("加载配置文件.....");
		propertiesLoader.init(path);
		HashMap<String, String> configMap = MailUtils.getMailConfig("mail.xml");
		String body = "<style type='text/css'>table {border-collapse: collapse;border-spacing: 0;width:450px;}.table-bordered th,.table-bordered td {border: 1px solid #ddd!important;border: 1px solid #ddd;text-align:center;}.table-hover>tbody>tr:hover>td,.table-hover>tbody>tr:hover>th {background-color: #f5f5f5;}.th_row {background-color: #F5F5F5;}</style>";
		body += "<table class='table table-bordered table-hover'><tbody><tr class='th_row'><th>销售</th><th>总间夜</th><th>总营收</th></tr>";
		
		DatabaseMysqlImpl aDatabaseMysqlImpl = new DatabaseMysqlImpl();
		try {
			String sql = "exec sp_report024 '销售业绩排行','','','确认','"+DateUtils.getLastMonthBefore()+"','','','','','','sta'";
			Vector<HashMap<Object, Object>> list = aDatabaseMysqlImpl.executeQuerySQL(sql);
			for (HashMap<Object, Object> hMap : list) {
				body += "<tr><td>"+hMap.get("sales").toString()+"</td><td>"+(int)Float.parseFloat(hMap.get("roomNight").toString())+"</td><td>"+(int)Float.parseFloat(hMap.get("inTotal").toString())+"</td></tr>";
			}
			body += "</tbody></table><hr><font color='red'>如有疑问，请及时沟通，谢谢！</font>";
			MailUtils.returnMessage(configMap, body,true); 
		} catch (SQLException e) {
			logger.error("邮件发送失败！");
		}
	}
}
