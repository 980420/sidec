package gehos.comun.mantenimiento.funcionalidades.dataaccess;

import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.sql.DataSource;

import org.jboss.seam.annotations.Name;

@Name("connectionSQL")
@Stateless
public class ConnectionSQL implements IConnection {

	@Resource(mappedName = "java:/gehosDatasource")
	DataSource gehosDatasource;

	public Connection createConnection() {
		try {
			return gehosDatasource.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
