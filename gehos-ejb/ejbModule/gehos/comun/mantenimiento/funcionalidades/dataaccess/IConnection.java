package gehos.comun.mantenimiento.funcionalidades.dataaccess;

import java.sql.Connection;

public interface IConnection {

	public abstract Connection createConnection();

}