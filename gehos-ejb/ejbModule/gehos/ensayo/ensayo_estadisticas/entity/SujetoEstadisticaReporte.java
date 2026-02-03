package gehos.ensayo.ensayo_estadisticas.entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class SujetoEstadisticaReporte {
    private String nombre;
    private String entidad;
    private Integer momMomNoIniciados;
    private Integer momMomIniciados;
    private Integer momMomCompletados;
    private Integer notasNuevas;
    private Integer notasSinResolver;
    private Integer notasResueltas;
    private Integer notasCerradas;
    private String style;
    
    
    
	public SujetoEstadisticaReporte(String nombre, String entidad,
			Integer momMomNoIniciados, Integer momMomIniciados,
			Integer momMomCompletados, Integer notasNuevas,
			Integer notasSinResolver, Integer notasResueltas, Integer notasCerradas, String style) {
		super();
		this.nombre = nombre;
		this.entidad = entidad;
		this.momMomNoIniciados = momMomNoIniciados;
		this.momMomIniciados = momMomIniciados;
		this.momMomCompletados = momMomCompletados;
		this.notasNuevas = notasNuevas;
		this.notasSinResolver = notasSinResolver;
		this.notasResueltas = notasResueltas;
		this.notasCerradas = notasCerradas;
		this.style=style;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getEntidad() {
		return entidad;
	}
	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}
	public Integer getMomMomNoIniciados() {
		return momMomNoIniciados;
	}
	public void setMomMomNoIniciados(Integer momMomNoIniciados) {
		this.momMomNoIniciados = momMomNoIniciados;
	}
	public Integer getMomMomIniciados() {
		return momMomIniciados;
	}
	public void setMomMomIniciados(Integer momMomIniciados) {
		this.momMomIniciados = momMomIniciados;
	}
	public Integer getMomMomCompletados() {
		return momMomCompletados;
	}
	public void setMomMomCompletados(Integer momMomCompletados) {
		this.momMomCompletados = momMomCompletados;
	}
	public Integer getNotasNuevas() {
		return notasNuevas;
	}
	public void setNotasNuevas(Integer notasNuevas) {
		this.notasNuevas = notasNuevas;
	}
	public Integer getNotasSinResolver() {
		return notasSinResolver;
	}
	public void setNotasSinResolver(Integer notasSinResolver) {
		this.notasSinResolver = notasSinResolver;
	}
	public Integer getNotasResueltas() {
		return notasResueltas;
	}
	public void setNotasResueltas(Integer notasResueltas) {
		this.notasResueltas = notasResueltas;
	}
	public Integer getNotasCerradas() {
		return notasCerradas;
	}
	public void setNotasCerradas(Integer notasCerradas) {
		this.notasCerradas = notasCerradas;
	}
    
    
    
}
