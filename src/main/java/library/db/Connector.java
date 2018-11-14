package library.db;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;


@Component
@PropertySource("classpath:db.properties")
public class Connector {

	@Value("${DB_DRIVER_CLASS}")
	private String DB_DRIVER_CLASS;

	@Value("${DB_URL}")
	private String DB_URL;
	
	@Value("${DB_USERNAME}")
	private String DB_USERNAME;
	
	@Value("${DB_PASSWORD}")
	private String DB_PASSWORD;
	
	private Connection connection;
	
	public Connector() {
	}
	
	@Override
	public String toString() {
		return "Connector [DB_DRIVER_CLASS=" + DB_DRIVER_CLASS + ", DB_URL=" + DB_URL + ", DB_USERNAME=" + DB_USERNAME
				+ ", DB_PASSWORD=" + DB_PASSWORD + "]";
	}

	public Connection getConnection() throws SQLException {
		BasicDataSource dbcpDs = new BasicDataSource();
		dbcpDs.setDriverClassName(DB_DRIVER_CLASS);
		dbcpDs.setUrl(DB_URL);
		dbcpDs.setUsername(DB_USERNAME);
		dbcpDs.setPassword(DB_PASSWORD);
		connection = dbcpDs.getConnection();
		return connection;
	}
	
	public void closeConnection() throws SQLException {
		connection.close();
	}
}
