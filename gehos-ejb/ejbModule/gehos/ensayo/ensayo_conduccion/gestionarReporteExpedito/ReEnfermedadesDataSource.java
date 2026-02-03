package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

public class ReEnfermedadesDataSource {
	
	private String enfermedad;
	private String si;
	private String no;
	
	
	public ReEnfermedadesDataSource(String enfermedad, String si, String no) {
		super();
		this.enfermedad = enfermedad;
		this.si = si;
		this.no = no;
	}


	public ReEnfermedadesDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getEnfermedad() {
		return enfermedad;
	}


	public void setEnfermedad(String enfermedad) {
		this.enfermedad = enfermedad;
	}


	public String getSi() {
		return si;
	}


	public void setSi(String si) {
		this.si = si;
	}


	public String getNo() {
		return no;
	}


	public void setNo(String no) {
		this.no = no;
	}

}
