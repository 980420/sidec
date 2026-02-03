package gehos.autenticacion.session.custom;

public class ConectionLimit {
	
	private int maxConections;
	private String user;

	public int getMaxConections() {
		return maxConections;
	}

	public void setMaxConections(int maxConections) {
		this.maxConections = maxConections;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user.toLowerCase();
	}	
	
}
