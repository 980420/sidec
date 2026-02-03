package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import java.util.List;

import gehos.ensayo.entity.Estado_ensayo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("cronogramaEspecificoExportar")
@Scope(ScopeType.CONVERSATION)
public class CronogramaEspecificoExportar {
	
	private String iniciales;
	private String inicialesCentro;
	private String fechaInclucion;
	private String numeroInclucion;
	private String codigoSujeto;
	private String estadoInclucion;
	private String grupoSujeto;
	
	private List<MomentosEspecificos> momentos;
	
	public CronogramaEspecificoExportar(String iniciales, String inicialesCentro, String fechaInclucion, 
			String numeroInclucion, String codigoSujeto, String estadoInclucion, String grupoSujeto, List<MomentosEspecificos> momentos)
	{
		super();
		this.iniciales=iniciales;
		this.inicialesCentro=inicialesCentro;
		this.fechaInclucion=fechaInclucion;
		this.numeroInclucion=numeroInclucion;
		this.codigoSujeto=codigoSujeto;
		this.estadoInclucion=estadoInclucion;
		this.grupoSujeto=grupoSujeto;
		this.momentos=momentos;
	}
	
	public CronogramaEspecificoExportar()
	{
		super();
	}

	public String getIniciales() {
		return iniciales;
	}

	public void setIniciales(String iniciales) {
		this.iniciales = iniciales;
	}

	public String getInicialesCentro() {
		return inicialesCentro;
	}

	public void setInicialesCentro(String inicialesCentro) {
		this.inicialesCentro = inicialesCentro;
	}

	public String getFechaInclucion() {
		return fechaInclucion;
	}

	public void setFechaInclucion(String fechaInclucion) {
		this.fechaInclucion = fechaInclucion;
	}

	public String getNumeroInclucion() {
		return numeroInclucion;
	}

	public void setNumeroInclucion(String numeroInclucion) {
		this.numeroInclucion = numeroInclucion;
	}

	public String getCodigoSujeto() {
		return codigoSujeto;
	}

	public void setCodigoSujeto(String codigoSujeto) {
		this.codigoSujeto = codigoSujeto;
	}

	public String getEstadoInclucion() {
		return estadoInclucion;
	}

	public void setEstadoInclucion(String estadoInclucion) {
		this.estadoInclucion = estadoInclucion;
	}

	public String getGrupoSujeto() {
		return grupoSujeto;
	}

	public void setGrupoSujeto(String grupoSujeto) {
		this.grupoSujeto = grupoSujeto;
	}

	public List<MomentosEspecificos> getMomentos() {
		return momentos;
	}

	public void setMomentos(List<MomentosEspecificos> momentos) {
		this.momentos = momentos;
	}
	
}
