package task.mail.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.log4j.Logger;

import task.mail.properties.MyXMLProperties;

public class MailUtils {
	private static Logger logger =  Logger.getLogger(MailUtils.class);
	
	public static HashMap<String,String> getMailConfig(String filepath) {
		HashMap<String,String> map = new HashMap<String,String>();
		File file = new File(filepath);
		if(file.exists()){
			map = new MyXMLProperties().getPropertys(filepath, "config", "key", "value");
			String v = DESUtils.decode(map.get("Email_Pswd").toString());
			map.put("Email_Pswd", v.substring(0, v.length()-3));
		}else{
			logger.error("邮件配置文件未找到");
		}
		return map;
	}
	
	/**
	 * 发送邮件信息配置
	 * @return boolean
	 */
	public static boolean sendMail(HashMap<String, String> map,String Email_Text,boolean html){
		boolean sendFlag = true;
		final String username = map.get("Email_User").toString();
		final String password = map.get("Email_Pswd").toString();
		String[] filepath = isNotNode(map, "FilePath")? null : map.get("FilePath").toString().split(";");
		String[] attachName = isNotNode(map, "AttachName") ? null : map.get("AttachName").toString().split(";");
		String[] to = isNotNode(map, "Email_To") ? null : map.get("Email_To").toString().split(";");	//多个接受者
		String[] cc = isNotNode(map, "Email_Cc") ? null : map.get("Email_Cc").toString().split(";"); 	//多个抄送者
		String Email_Subject = isNotNode(map, "Email_Subject") ? null : map.get("Email_Subject").toString();
		String Email_Host = isNotNode(map, "Email_Host") ? null : map.get("Email_Host").toString();
		String Email_Port = isNotNode(map, "Email_Port") ? null : map.get("Email_Port").toString();
		String Email_From = isNotNode(map, "Email_From") ? null : map.get("Email_From").toString();
		String Email_Sender = isNotNode(map, "Email_Sender") ? null : map.get("Email_Sender").toString();
		
		Session session;
		//获得是否使用代理发送的标识
		String Email_Proxy_Flag = map.get("Email_Proxy_Flag").toString();
		if("1".equalsIgnoreCase(Email_Proxy_Flag)){
			//若Email_Proxy_Flag==1则使用代理发送
			try {
				Properties props = System.getProperties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.host", Email_Host);
				props.put("mail.port", Email_Port);
				props.put("mail.smtp.user", username);
				props.put("mail.smtp.password", password);
				props.put("proxySet", "true");
				props.put("http.proxyhost",map.get("Email_Proxy_Host").toString());
				props.put("http.proxyport",map.get("Email_Proxy_Port").toString());
				
				session = Session.getDefaultInstance(props, new Authenticator() { protected javax.mail.PasswordAuthentication getPasswordAuthentication() { return new javax.mail.PasswordAuthentication(username, password); }});
				MimeMessage message = new MimeMessage(session);
				message.setFrom(new InternetAddress(Email_From,Email_Sender));
				
				//多个接受者
				if (to != null) {
					InternetAddress[] toAddress = new InternetAddress[to.length];
					for (int i = 0; i < to.length; i++) {
						toAddress[i] = new InternetAddress(to[i]);
					}
					message.addRecipients(Message.RecipientType.TO, toAddress);
				}
				//多个抄送者
				if (null != cc) {
					InternetAddress[] ccAddress = new InternetAddress[cc.length];
					for (int j = 0; j < cc.length; j++) {
						ccAddress[j] = new InternetAddress(cc[j]);
					}
					message.addRecipients(Message.RecipientType.CC, ccAddress);
				}
				message.setSubject(Email_Subject);
				//附件功能
				Multipart multipart = new MimeMultipart();
				if (null != filepath) {
					for (int k = 0; k < filepath.length; k++) {
						addMultipart(multipart, filepath[k], attachName[k],Email_Text, k);
					}
				} else if (null == filepath) {
					message.setText(Email_Text);
				}
				message.setContent(multipart);
				if (html) {
					message.setContent(Email_Text,"text/html;charset=utf8");
				} else {
					message.setContent(Email_Text,"text/plain;charset=utf8");
				}
				
				Transport transport = session.getTransport("smtp");
				transport.connect(Email_Host,username,password);
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
				} catch (Exception e) {
					sendFlag = false;
					logger.error("使用代理发送邮件出错" + e.getMessage());
					e.printStackTrace();
				}
		}else{
			//若Email_Proxy_Flag!=1则不使用代理发送
			try {
				Properties props = new Properties();
				props.put("mail.smtp.auth", "true");
				props.put("mail.host", Email_Host);
				props.put("mail.port", Email_Port);
				props.put("mail.smtp.user", username);
				props.put("mail.smtp.password", password);

				session = Session.getDefaultInstance(props, new Authenticator() { protected javax.mail.PasswordAuthentication getPasswordAuthentication() { return new javax.mail.PasswordAuthentication(username, password); }});
				MimeMessage message = new MimeMessage(session);
				
				message.setFrom(new InternetAddress(Email_From,Email_Sender));
			
				//多个接收者
				if (to != null) {
					InternetAddress[] toAddress = new InternetAddress[to.length];
					for (int i = 0; i < to.length; i++) {
						toAddress[i] = new InternetAddress(to[i]);
					}
					message.addRecipients(Message.RecipientType.TO, toAddress);
				}
				
				//多个抄送者
				if (null != cc && cc.length > 0) {
					InternetAddress[] ccAddress = new InternetAddress[cc.length];
					for (int j = 0; j < cc.length; j++) {
						ccAddress[j] = new InternetAddress(cc[j]);
					}
					message.addRecipients(Message.RecipientType.CC, ccAddress);
				}
				message.setSubject(Email_Subject);
				
				//附件功能
				Multipart multipart = new MimeMultipart();
				if (null != filepath && filepath.length > 0 && attachName.length > 0 && filepath.length == attachName.length) {
					for (int k = 0; k < filepath.length; k++) {
						addMultipart(multipart, filepath[k], attachName[k],Email_Text, k);
					}
				} else if (null == filepath) {
					message.setText(Email_Text);
				}
				message.setContent(multipart);
				if (html) {
					message.setContent(Email_Text,"text/html;charset=utf8");
				} else {
					message.setContent(Email_Text,"text/plain;charset=utf8");
				}
				
				Transport transport = session.getTransport("smtp");
				transport.connect(Email_Host,username,password);// 2
				transport.sendMessage(message, message.getAllRecipients());
				transport.close();
			} catch (Exception e) {
				sendFlag = false;
				logger.error("发送邮件出错" + e.getMessage());
				e.printStackTrace();
			}
		}
		return sendFlag;
	}
	
	/**
	 * 邮件附件的添加
	 * @param multipart
	 * @param filePath
	 * @param fileName
	 * @param k
	 * @return boolean
	 */
	private static boolean addMultipart(Multipart multipart, String filePath,String fileName,String Email_Text, int k) {
		boolean flag = true;
		try {
			MimeBodyPart messageBodyPart = new MimeBodyPart();
			if (k == 0) {
				messageBodyPart.setText(Email_Text);
				multipart.addBodyPart(messageBodyPart);
			}
			messageBodyPart = new MimeBodyPart();
			DataSource source = new FileDataSource(filePath);
			messageBodyPart.setDataHandler(new DataHandler(source));
			messageBodyPart.setFileName(fileName);
			multipart.addBodyPart(messageBodyPart);
		} catch (Exception e) {
			flag = false;
			logger.error("邮件附件的添加失败" + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}
	
	public static String returnMessage(HashMap<String, String> map,String Email_Text,boolean html){
		boolean flag = sendMail(map,Email_Text,html); 
		StringBuffer sb = new StringBuffer();
		String Email_To = isNotNode(map, "Email_To") ? null : map.get("Email_To").toString();	//多个接受者
		String Email_Cc = isNotNode(map, "Email_Cc") ? null : map.get("Email_Cc").toString(); 	//多个抄送者
		String Email_From = isNotNode(map, "Email_From") ? null : map.get("Email_From").toString();
		if(flag){
			sb.append("\n").append("邮件发送已成功").append("\t").append("\n");
			sb.append("邮件发送者：" + Email_From).append("\t").append("\n");
			sb.append("邮件发送至：" + Email_To).append("\t").append("\n");
			sb.append("邮件抄送给：" + Email_Cc).append("\t").append("\n");
//			sb.append("邮件正文为：" + map.get("Email_Text").toString()).append("\t").append("\n");
			sb.append("发送时间是：" + DateUtils.getNow()).append("\t").append("\n");
		}else{
			sb.append("\n").append("邮件发送失败").append("\t").append("\n");
			sb.append("发送时间是：" + DateUtils.getNow()).append("\t").append("\n");
		}
		return sb.toString();
	}
	
	private static boolean isNotNode(HashMap<String, String> hMap ,String key){
		Object[] arrays = hMap.keySet().toArray();
		for (Object object : arrays) {
			if (object.toString().equals(key)) {
				return false;
			}
		}
		return true;
	}
}
