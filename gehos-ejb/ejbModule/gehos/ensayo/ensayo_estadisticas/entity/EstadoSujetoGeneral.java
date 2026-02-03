package gehos.ensayo.ensayo_estadisticas.entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class EstadoSujetoGeneral {
	
	private String nombreGrupo;
	private Integer evaluados;
	private Integer noIncluidos;
	private Integer incluidos;
	private Integer malIncluidos;
	private Integer interrumpidos;
	private String nombrePais;
	private String nombreProvincia;
	private String nombreEntidad;
	private String style;
	
	
	public EstadoSujetoGeneral(String nombreGrupo, Integer evaluados,
			Integer noIncluidos, Integer incluidos, Integer malIncluidos,
			Integer interrumpidos, String nombrePais, String nombreProvincia,
			String nombreEntidad, String style) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.evaluados = evaluados;
		this.noIncluidos = noIncluidos;
		this.incluidos = incluidos;
		this.malIncluidos = malIncluidos;
		this.interrumpidos = interrumpidos;
		this.nombrePais = nombrePais;
		this.nombreProvincia = nombreProvincia;
		this.nombreEntidad = nombreEntidad;
		this.style = style;
	}
	public String getNombreGrupo() {
		return nombreGrupo;
	}
	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}
	public Integer getEvaluados() {
		return evaluados;
	}
	public void setEvaluados(Integer evaluados) {
		this.evaluados = evaluados;
	}
	public Integer getNoIncluidos() {
		return noIncluidos;
	}
	public void setNoIncluidos(Integer noIncluidos) {
		this.noIncluidos = noIncluidos;
	}
	public Integer getIncluidos() {
		return incluidos;
	}
	public void setIncluidos(Integer incluidos) {
		this.incluidos = incluidos;
	}
	public Integer getMalIncluidos() {
		return malIncluidos;
	}
	public void setMalIncluidos(Integer malIncluidos) {
		this.malIncluidos = malIncluidos;
	}
	public Integer getInterrumpidos() {
		return interrumpidos;
	}
	public void setInterrumpidos(Integer interrumpidos) {
		this.interrumpidos = interrumpidos;
	}
	public String getNombrePais() {
		return nombrePais;
	}
	public void setNombrePais(String nombrePais) {
		this.nombrePais = nombrePais;
	}
	public String getNombreProvincia() {
		return nombreProvincia;
	}
	public void setNombreProvincia(String nombreProvincia) {
		this.nombreProvincia = nombreProvincia;
	}
	public String getNombreEntidad() {
		return nombreEntidad;
	}
	public void setNombreEntidad(String nombreEntidad) {
		this.nombreEntidad = nombreEntidad;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	
}