package task.mail.c3p0;


import java.io.InputStreamReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.apache.log4j.Logger;

public class DatabaseMysqlImpl {
	private static final Logger log = Logger.getLogger(DatabaseMysqlImpl.class);

	public void executeSQL(String sqlStatement) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = ConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(true);
			statement = connection.prepareStatement(sqlStatement);
			statement.execute();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("exit execute sql statement method");
		return;
	}

	public void executeSQL(String sqlStatement, Object parameters[]) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = ConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(true);
			statement = connection.prepareStatement(sqlStatement);
			// 获取执行数据
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}
			statement.execute();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("exit execute sql statement method");
		return;
	}

	@SuppressWarnings("resource")
	public String executeSQLReturnLastnewid(String sqlStatement,
			Object parameters[]) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		String lastinsertid = null;
		try {
			connection = ConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(true);
			statement = connection.prepareStatement(sqlStatement);
			// 获取执行数据
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}
			statement.execute();

			// -----------------------------------------------------------主表id
			statement = connection.prepareStatement("select LAST_INSERT_ID()"); // 获取statement对象
			// 获取查询参数

			statement.execute();

			rs = statement.getResultSet(); // 获取查询记录集

			if (rs.next())
				lastinsertid = rs.getString(1);
			// -----------------------------------------------------------主表id结束
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute sql method");
			}
			
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}

		}
//		log.debug("exit execute sql statement method");
		return lastinsertid;
	}

	/**
	 * 批量插入 主从 外键
	 * 
	 * @param sqlStatement
	 *            sql的list
	 * @param parameters
	 *            param的list
	 * @throws SQLException
	 * @param flag
	 *            1为添加 0为修改
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "resource" })
	public void executeSQL(List sqlStatement, List parameters, int flag)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(false);

			// ------------------------------------------------------------主表
			statement = connection.prepareStatement(sqlStatement.get(0).toString());
			// 获取执行数据
			List list0 = (List) parameters.get(0);
			int listsize = list0.size();
			for (int i = 0; i < listsize; i++) {
				statement.setObject(i + 1, list0.get(i));
			}
			statement.execute();
			// -----------------------------------------------------------主表结束
			String lastinsertid = null;
			if (flag == 1) {
				//-----------------------------------------------------------主表id
				statement = connection.prepareStatement("select LAST_INSERT_ID()"); // 获取statement对象
				// 获取查询参数

				statement.execute();

				rs = statement.getResultSet(); // 获取查询记录集

				if (rs.next())
					lastinsertid = rs.getString(1);
				// -----------------------------------------------------------
				// 主表id结束
			}
			List templist = new ArrayList();

			// ------------------------------------------------------------从表
			for (int n = 1; n < sqlStatement.size(); n++) {
				statement = connection.prepareStatement(sqlStatement.get(n).toString());
				// 获取执行数据
				templist = (List) parameters.get(n);
				if (flag == 1) {
					templist.add(lastinsertid);
				}

				if (templist != null && templist.size() > 0) {
					for (int i = 0; i < templist.size(); i++) {
						statement.setObject(i + 1, templist.get(i));
					}
				}
				templist.clear();
				statement.execute();
			}
			// -------------------------------------------------------------从表结束
			rs.close();
			statement.close();
			connection.commit();
			connection.close();
		} catch (SQLException ex) {
			if (connection != null) {
				try {
					connection.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if (rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if (statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute sql method");
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("exit execute sql statement method");
		return;
	}

	@SuppressWarnings("rawtypes")
	public void executeSQL(String sqlStatement, List parameters)
			throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = ConnectionManager.getInstance().getConnection();
			connection.setAutoCommit(true);
			statement = connection.prepareStatement(sqlStatement);
			// 获取执行数据
			int listsize = parameters.size();
			for (int i = 0; i < listsize; i++) {
				statement.setObject(i + 1, parameters.get(i));
			}
			statement.execute();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("exit execute sql statement method");
		return;
	}

	public Vector<HashMap<Object, Object>> executeQuerySQL(String sqlStatement) throws SQLException {
		Vector<HashMap<Object, Object>> resultVector = new Vector<HashMap<Object, Object>>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getConnection(); // 获取数据库连接对象
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sqlStatement); // 获取statement对象
			statement.execute();
			rs = statement.getResultSet(); // 获取查询记录集
			HashMap<Object, Object> rowItem;
			for (; rs.next(); resultVector.add(rowItem)) {
				rowItem = new HashMap<Object, Object>();
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					Object cell = rs.getObject(i);
					if (cell == null) {
						cell = "";
					} else if (cell instanceof Blob) {
						try {
							InputStreamReader in = new InputStreamReader(((Blob) cell).getBinaryStream());
							char bufferString[] = new char[5000];
							int readCharCount = in.read(bufferString);
							StringBuffer bolbStringBuffer = new StringBuffer("");
							for (; readCharCount == 5000; readCharCount = in.read(bufferString)) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							if (readCharCount != -1) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							cell = bolbStringBuffer.toString();
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}
					rowItem.put(rs.getMetaData().getColumnName(i), cell);
				}
			}
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute query sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("return executeQuerySQL vector size:" + resultVector.size());
//		log.debug("exit execute query sql statement method");
		return resultVector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector<HashMap> executeQuerySQL(String sqlStatement, Object parameters[])
			throws SQLException {
		Vector<HashMap> resultVector = new Vector<HashMap>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		try {
			connection = ConnectionManager.getInstance().getConnection(); // 获取数据库连接对象
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sqlStatement); // 获取statement对象
			// 获取查询参数
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}
			statement.execute();
			rs = statement.getResultSet(); // 获取查询记录集
			HashMap rowItem;
			for (; rs.next(); resultVector.add(rowItem)) {
				rowItem = new HashMap();
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					Object cell = rs.getObject(i);
					if (cell == null) {
						cell = "";
					} else if (cell instanceof Blob) {
						try {
							InputStreamReader in = new InputStreamReader(((Blob) cell).getBinaryStream());
							char bufferString[] = new char[5000];
							int readCharCount = in.read(bufferString);
							StringBuffer bolbStringBuffer = new StringBuffer("");
							for (; readCharCount == 5000; readCharCount = in.read(bufferString)) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							if (readCharCount != -1) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							cell = bolbStringBuffer.toString();
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}
					rowItem.put(rs.getMetaData().getColumnName(i), cell);
				}
			}
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute query sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("return vector size:" + resultVector.size());
//		log.debug("exit execute query sql statement method");
		return resultVector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector<HashMap> executeQuerySQL(String sqlStatement, Object parameters[],
			String sql) throws SQLException {
		Vector<HashMap> resultVector = new Vector<HashMap>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;
		PreparedStatement statementcount = null;
		ResultSet rscount = null;
		Vector vector = new Vector();
		try {
			connection = ConnectionManager.getInstance().getConnection(); // 获取数据库连接对象
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sqlStatement); // 获取statement对象
			// 获取查询参数
			if (parameters != null && parameters.length > 0) {
				for (int i = 0; i < parameters.length; i++) {
					statement.setObject(i + 1, parameters[i]);
				}
			}
			statement.execute();
			rs = statement.getResultSet(); // 获取查询记录集
			HashMap rowItem;
			for (; rs.next(); resultVector.add(rowItem)) {
				rowItem = new HashMap();
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					Object cell = rs.getObject(i);
					if (cell == null) {
						cell = "";
					} else if (cell instanceof Blob) {
						try {
							InputStreamReader in = new InputStreamReader(((Blob) cell).getBinaryStream());
							char bufferString[] = new char[5000];
							int readCharCount = in.read(bufferString);
							StringBuffer bolbStringBuffer = new StringBuffer("");
							for (; readCharCount == 5000; readCharCount = in.read(bufferString)) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							if (readCharCount != -1) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							cell = bolbStringBuffer.toString();
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}
					rowItem.put(rs.getMetaData().getColumnName(i), cell);
				}
			}
			rs.close();
			statement.close();
			statementcount = connection.prepareStatement(sql);
			statementcount.execute();
			rscount = statementcount.getResultSet();
			Vector count = new Vector();
			HashMap rowItemcount;
			for (; rscount.next(); count.add(rowItemcount)) {
				rowItemcount = new HashMap();
				int columnCount = rscount.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					Object cell = rscount.getObject(i);
					if (cell == null) {
						cell = "";
					} else if (cell instanceof Blob) {
						try {
							InputStreamReader in = new InputStreamReader(((Blob) cell).getBinaryStream());
							char bufferString[] = new char[5000];
							int readCharCount = in.read(bufferString);
							StringBuffer bolbStringBuffer = new StringBuffer("");
							for (; readCharCount == 5000; readCharCount = in.read(bufferString)) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							if (readCharCount != -1) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							cell = bolbStringBuffer.toString();
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}
					rowItemcount.put(rscount.getMetaData().getColumnName(i),cell);
				}
			}
			
			vector.add(resultVector);
			vector.add(count);
			rscount.close();
			statementcount.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
				if(rscount != null)
					rscount.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if(statement != null)
					statement.close();
				if(statementcount != null)
					statementcount.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute query sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
//		log.debug("return vector size:" + vector.size());
//		log.debug("exit execute query sql statement method");
		return vector;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Vector<HashMap> executeQuerySQL(String sqlStatement, List parameters)
			throws SQLException {
		Vector<HashMap> resultVector = new Vector<HashMap>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet rs = null;

//		log.debug("step into execute query sql statement method");
//		log.debug("query sql statement:" + sqlStatement);

		try {
			connection = ConnectionManager.getInstance().getConnection(); // 获取数据库连接对象
			connection.setAutoCommit(false);
			statement = connection.prepareStatement(sqlStatement); // 获取statement对象
			// 获取查询数据
			int listsize = parameters.size();
			for (int i = 0; i < listsize; i++) {
				statement.setObject(i + 1, parameters.get(i));
			}
			statement.execute();
			rs = statement.getResultSet(); // 获取查询记录集
			HashMap rowItem;
			for (; rs.next(); resultVector.add(rowItem)) {
				rowItem = new HashMap();
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					Object cell = rs.getObject(i);
					if (cell == null) {
						cell = "";
					} else if (cell instanceof Blob) {
						try {
							InputStreamReader in = new InputStreamReader(((Blob) cell).getBinaryStream());
							char bufferString[] = new char[5000];
							int readCharCount = in.read(bufferString);
							StringBuffer bolbStringBuffer = new StringBuffer("");
							for (; readCharCount == 5000; readCharCount = in.read(bufferString)) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							if (readCharCount != -1) {
								bolbStringBuffer.append(bufferString, 0,readCharCount);
							}
							cell = bolbStringBuffer.toString();
							in.close();
						} catch (Exception ex) {
							ex.printStackTrace();
							log.error(ex.getMessage());
						}
					}
					rowItem.put(rs.getMetaData().getColumnName(i), cell);
				}
			}
			rs.close();
			statement.close();
			connection.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
			throw ex;
		} catch (Exception ex) {
			ex.printStackTrace();
			log.error(ex.getMessage());
		} finally {
			try {
				if(rs != null)
					rs.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close result set exception in execute query sql method");
			}
			try {
				if(statement != null)
					statement.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error("close statement exception in execute query sql method");
			}
			try {
				if(connection != null)
					connection.close();
			} catch (SQLException ex) {
				ex.printStackTrace();
				log.error(ex.getMessage());
				throw ex;
			}
		}
		return resultVector;
	}
}