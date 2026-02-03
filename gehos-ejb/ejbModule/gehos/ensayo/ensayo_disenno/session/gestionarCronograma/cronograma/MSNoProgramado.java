package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma;

public class MSNoProgramado {

	String nombre_MS;
	String ocurre;
	
	
	public MSNoProgramado(String nombre_MS,
	String ocurre
	){
		this.nombre_MS=nombre_MS;
		this.ocurre= ocurre;
		
		
	}
	public String getNombre_MS() {
		return nombre_MS;
	}
	public void setNombre_MS(String nombre_MS) {
		this.nombre_MS = nombre_MS;
	}
	public String getOcurre() {
		return ocurre;
	}
	public void setOcurre(String ocurre) {
		this.ocurre = ocurre;
	}
	
}
