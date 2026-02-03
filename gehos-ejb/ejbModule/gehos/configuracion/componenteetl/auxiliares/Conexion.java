package gehos.configuracion.componenteetl.auxiliares;

public class Conexion {
	
	private String nombreServer;
	private int puerto;
	private String nombreBD;
	private String user;
	private String pass;
	
	public Conexion() {
	}
	
	public Conexion(String nombreServer, int puerto, String nombreBD, String user, String pass){
		this.nombreServer = nombreServer;
		this.nombreBD = nombreBD;
		this.puerto = puerto;
		this.pass = pass;
		this.user = user;
	}

	public String getNombreServer() {
		return nombreServer;
	}

	public void setNombreServer(String nombreServer) {
		this.nombreServer = nombreServer;
	}

	public int getPuerto() {
		return puerto;
	}

	public void setPuerto(int puerto) {
		this.puerto = puerto;
	}

	public String getNombreBD() {
		return nombreBD;
	}

	public void setNombreBD(String nombreBD) {
		this.nombreBD = nombreBD;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}
}
