package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

public class VariableMomento {
	private Long idVariable;
	private Long idMomento;
	
	public VariableMomento(Long idVariable, Long idMomento) {
		this.idVariable = idVariable;
		this.idMomento = idMomento;
	}
	public Long getIdVariable() {
		return idVariable;
	}
	public void setIdVariable(Long idVariable) {
		this.idVariable = idVariable;
	}
	public Long getIdMomento() {
		return idMomento;
	}
	public void setIdMomento(Long idMomento) {
		this.idMomento = idMomento;
	}
	
}
