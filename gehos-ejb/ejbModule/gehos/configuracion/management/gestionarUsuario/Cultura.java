package gehos.configuracion.management.gestionarUsuario;


public class Cultura {
	private int id;
	private String idioma;
	private String siglas;
	private String localString;//Text to show
	
	public Cultura(int id, String idioma, String localString){
		this.id = id;
		this.idioma = idioma;
		this.siglas = "";
		this.localString = localString;
	}
	
	public String cultura(){
		return idioma;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}

	public String getSiglas() {
		return siglas;
	}

	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	public String getLocalString() {
		return localString;
	}

	public void setLocalString(String localString) {
		this.localString = localString;
	}
}
