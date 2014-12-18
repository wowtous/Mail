package task.mail.c3p0;

import java.sql.Connection;
import java.sql.SQLException;

import task.mail.constant.PropertiesLoader;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;
/**
 * system数据库的服务
 */
public final class ConnectionManager {
	private static ConnectionManager instance;

	public ComboPooledDataSource ds;

	private ConnectionManager() throws Exception {
		ds = new ComboPooledDataSource();
		ds.setJdbcUrl(PropertiesLoader.jdbcUrl);
		ds.setDriverClass(PropertiesLoader.driverClass);
		ds.setUser(PropertiesLoader.user);
		ds.setPassword(PropertiesLoader.password);
		ds.setInitialPoolSize(Integer.parseInt(PropertiesLoader.initialPoolSize));
		ds.setMaxPoolSize(Integer.parseInt(PropertiesLoader.maxPoolSize));
		if("true".equals(PropertiesLoader.autoCommitOnClose))
			ds.setAutoCommitOnClose(true);
		if("false".equals(PropertiesLoader.autoCommitOnClose))
			ds.setAutoCommitOnClose(false);
	}

	public static final ConnectionManager getInstance() {
		if (instance == null) {
			try {
				instance = new ConnectionManager();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return instance;
	}

	public synchronized final Connection getConnection() {
		try {
			return ds.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	protected void finalize() throws Throwable {
		DataSources.destroy(ds); // 关闭datasource
		super.finalize();
	}

}
