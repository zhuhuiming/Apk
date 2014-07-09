package cn.redarmy.action;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.Action;

final class GetConnection {
	/**
	 * Uses DriverManager.
	 */
	static Connection getSimpleConnection() throws SQLException {
		Connection conn = null;
		// See your driver documentation for the proper format of this string :
		String DB_CONN_STRING = "jdbc:mysql://localhost:3306/renpindatabase";
		// Provided by your driver documentation. In this case, a MySql driver
		// is used :
		String DRIVER_CLASS_NAME = "com.mysql.jdbc.Driver";
		String USER_NAME = "root";
		String PASSWORD = "P%mysql154";
		conn = DriverManager.getConnection(DB_CONN_STRING, USER_NAME, PASSWORD);

		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception ex) {
			// log("Check classpath. Cannot load db driver: " +
			// DRIVER_CLASS_NAME);
		}

		try {
			conn = DriverManager.getConnection(DB_CONN_STRING, USER_NAME,
					PASSWORD);
		} catch (SQLException e) {

			// log("Driver loaded, but cannot connect to db: " +
			// DB_CONN_STRING);
			// e.printStackTrace();
		}
		return conn;
	}

	private static void log(Object aObject) {
		// System.out.println(aObject);
	}
}

public class FileDownloadAction implements Action {

	private String fileName;
	private InputStream inputStream;

	public void setFileName(String fileName) {
		if (fileName.equals("RenPinCustomer")) {
			this.fileName = "RenPinCustomer" + ".apk";
		} else {
			this.fileName = fileName;
		}
	}

	public String getFileName() {
		return this.fileName;
	}

	/**
	 * 
	 * 设置返回的文件的大小
	 * 
	 * @return
	 */
	public int getFileSize() {
		// java.io.File f = new java.io.File("/" + fileName);
		InputStream inputbytes = ServletActionContext.getServletContext()
				.getResourceAsStream("/" + fileName);
		int bytes = 0;
		try {
			bytes = inputbytes.available();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bytes;
	}

	public InputStream getInputStream() {
		inputStream = ServletActionContext.getServletContext()
				.getResourceAsStream("/" + fileName);
		// 获取系统当前时间
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String strCurrentTime = formatter.format(curDate);
		if (fileName.equals("RenPinCustomer.apk")) {
			SaveDownLoadTimeAndCount(strCurrentTime);
		}

		if (inputStream == null) {
		}
		return inputStream;
	}

	@Override
	public String execute() throws Exception {
		return SUCCESS;
	}

	// 更新下载次数,保存下载时间
	private void SaveDownLoadTimeAndCount(String strTime) {
		Connection connect = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			connect = GetConnection.getSimpleConnection();
			stmt = connect.createStatement();
			int nDownCount = 1;
			// 先获取当前下载时间
			String strSQL = "select * from renpin_downloadinfo_table";
			rs = stmt.executeQuery(strSQL);
			while (rs.next()) {
				rs.last();
				nDownCount = rs.getRow();
				nDownCount++;
				break;
			}
			strSQL = "insert into renpin_downloadinfo_table values('";
			strSQL += nDownCount;
			strSQL += "','";
			strSQL += strTime;
			strSQL += "')";
			stmt.execute(strSQL);
		} catch (Exception e) {
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (stmt != null) {
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (connect != null) {
				try {
					connect.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
