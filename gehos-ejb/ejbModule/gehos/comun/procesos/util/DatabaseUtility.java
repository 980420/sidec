package gehos.comun.procesos.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class DatabaseUtility {
	public static void cleanDeployedProcesses() throws SQLException {
		DataSource datasource = null;
		try {
			InitialContext context = new InitialContext();
			datasource = (DataSource) context.lookup("java:/gehosDatasource");
		} catch (NamingException e) {
			assert false : "Unable to create directory context!";
		}
		Connection connection = datasource.getConnection();
		CallableStatement statement = connection
				.prepareCall("{call public.clean_processes()}");
		try {
			statement.executeUpdate();
		} catch (SQLException e) {
			System.out.println("sql error " + e);
		}

		finally {
			if (statement != null) {
				statement.close();
			}
			if (connection != null) {
				connection.close();
			}
		}

	}
}
