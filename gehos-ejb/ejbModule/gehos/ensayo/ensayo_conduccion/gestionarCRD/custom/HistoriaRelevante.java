package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class HistoriaRelevante {
	
	private String resultadosHM;

	public HistoriaRelevante(String resultadosHM) {
		super();
		this.resultadosHM = resultadosHM;
	}

	public String getResultadosHM() {
		return resultadosHM;
	}

	public void setResultadosHM(String resultadosHM) {
		this.resultadosHM = resultadosHM;
	}
	
	
}
