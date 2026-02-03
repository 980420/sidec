package gehos.configuracion.management.gestionarEntidadesSistema;

public class LogoPorTema {
	private String tema = "";
	private String logo = "";
	private String color = "";
	private String nombreTema = "";
	
	public LogoPorTema(){
		
	}
	
	public LogoPorTema(String tema, String logo, String nombreTema, String color){
		this.tema = tema;
		this.logo = logo;
		this.nombreTema = nombreTema;
		this.color = color;
	}
	
	public String getTema() {
		return tema;
	}
	public void setTema(String tema) {
		this.tema = tema;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getNombreTema() {
		return nombreTema;
	}

	public void setNombreTema(String nombreTema) {
		this.nombreTema = nombreTema;
	}
}
