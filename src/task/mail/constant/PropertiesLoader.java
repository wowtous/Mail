package task.mail.constant;

import task.mail.properties.MyXMLProperties;
import task.mail.properties.PropertiesInstance;


/**
 * 加载配置文件的信息，每次循环加载一次
 * @author fisher
 *
 */
public class PropertiesLoader {
	private PropertiesInstance propertiesInstance;
	private MyXMLProperties properties;	
	
	// c3p0的配置
	public static String jdbcUrl;
	public static String driverClass;
	public static String user;
	public static String password;
	public static String initialPoolSize;
	public static String maxPoolSize;
	public static String autoCommitOnClose;
	
	public void init(String projectConfigProperties){
		propertiesInstance = new PropertiesInstance(projectConfigProperties);
		properties = propertiesInstance.getProperties();
		
		//c3p0的配置
		jdbcUrl = properties.getProperty("jdbcUrl");
		driverClass = properties.getProperty("driverClass");
		user = properties.getProperty("user");
		password = properties.getProperty("password");
		initialPoolSize = properties.getProperty("initialPoolSize");
		maxPoolSize = properties.getProperty("maxPoolSize");
		autoCommitOnClose = properties.getProperty("autoCommitOnClose");
	}
	
	public void destroy(){
		propertiesInstance = null;
		properties = null;
	}
}
