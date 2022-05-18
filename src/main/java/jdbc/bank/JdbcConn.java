package jdbc.bank;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcConn {
	//注册驱动
	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	//数据库地址


	private static Connection conn = null;

	static {
		try {
			Class.forName(JDBC_DRIVER).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Connection getConnection(String ip) throws SQLException {
		conn = DriverManager.getConnection("jdbc:mysql://" + ip + ":3306/atm?user=root&password=cym131452000&useUnicode=true&characterEncoding=utf8");
		return conn;
	}
}
