package gehos.ensayo.ensayo_conduccion.gestionarNewCronograma;

public class MomentoSeguimientoProgramadoNewCronograma {

	private String nombreMomentoGeneral; 
	private String fechaCreacion;
	
	public MomentoSeguimientoProgramadoNewCronograma(
			String nombreMomentoGeneral, String fechaCreacion) {
		super();
		this.nombreMomentoGeneral = nombreMomentoGeneral;
		this.fechaCreacion = fechaCreacion;
	}  
	
	public String getNombreMomentoGeneral() {
		return nombreMomentoGeneral;
	}

	public void setNombreMomentoGeneral(String nombreMomentoGeneral) {
		this.nombreMomentoGeneral = nombreMomentoGeneral;
	}

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
}
