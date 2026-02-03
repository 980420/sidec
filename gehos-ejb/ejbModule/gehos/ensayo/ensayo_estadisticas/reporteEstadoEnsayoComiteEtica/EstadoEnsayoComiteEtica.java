package gehos.ensayo.ensayo_estadisticas.reporteEstadoEnsayoComiteEtica;

public class EstadoEnsayoComiteEtica {
	private String nombreGrupo;
	private int enEvaluacion;	
	private int incluidos;
	private int noIncluidos;
	private int cantInterrupciones;
	private int noEAG;
	private String style;
	
	
	
	public EstadoEnsayoComiteEtica(String nombreGrupo, int enEvaluacion,
			int incluidos, int noIncluidos, int cantInterrupciones, int noEAG,
			String style) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.enEvaluacion = enEvaluacion;
		this.incluidos = incluidos;
		this.noIncluidos = noIncluidos;
		this.cantInterrupciones = cantInterrupciones;
		this.noEAG = noEAG;
		this.style = style;
	}
	
	public EstadoEnsayoComiteEtica(String nombreGrupo, int enEvaluacion,
			int incluidos, int noIncluidos, int cantInterrupciones, int noEAG) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.enEvaluacion = enEvaluacion;
		this.incluidos = incluidos;
		this.noIncluidos = noIncluidos;
		this.cantInterrupciones = cantInterrupciones;
		this.noEAG = noEAG;
	}

	public String getNombreGrupo() {
		return nombreGrupo;
	}

	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}

	public int getEnEvaluacion() {
		return enEvaluacion;
	}

	public void setEnEvaluacion(int enEvaluacion) {
		this.enEvaluacion = enEvaluacion;
	}

	public int getIncluidos() {
		return incluidos;
	}

	public void setIncluidos(int incluidos) {
		this.incluidos = incluidos;
	}

	public int getNoIncluidos() {
		return noIncluidos;
	}

	public void setNoIncluidos(int noIncluidos) {
		this.noIncluidos = noIncluidos;
	}

	public int getCantInterrupciones() {
		return cantInterrupciones;
	}

	public void setCantInterrupciones(int cantInterrupciones) {
		this.cantInterrupciones = cantInterrupciones;
	}

	public int getNoEAG() {
		return noEAG;
	}

	public void setNoEAG(int noEAG) {
		this.noEAG = noEAG;
	}

	
	
}
