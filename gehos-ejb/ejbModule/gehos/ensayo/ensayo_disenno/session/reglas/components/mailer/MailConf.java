package gehos.ensayo.ensayo_disenno.session.reglas.components.mailer;

public class MailConf
{
	//ByRafa
	String email;
	String password;
	String server;
	int port;
	
	String trustStorePath, trustStorePass;
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getServer() {
		return server;
	}
	public void setServer(String server) {
		this.server = server;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getTrustStorePath() {
		return trustStorePath;
	}
	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}
	public String getTrustStorePass() {
		return trustStorePass;
	}
	public void setTrustStorePass(String trustStorePass) {
		this.trustStorePass = trustStorePass;
	}	

}