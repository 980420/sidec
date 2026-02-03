package gehos.ensayo.ensayo_estadisticas.entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class SujetoGeneral {
	
	private String nombreSujeto;
	private Integer momentosIniciados;
	private Integer momentosCompletados;
	private Integer momentosFirmados;
	private Integer momentosAtrasados;
	private Integer momentosTotal;
	private Integer crdNoIniciadas;
	private Integer crdIniciadas;
	private Integer crdCompletadas;
	private Integer crdFirmada;
	private Integer crdTotal;
	private Integer momentosMonNoIniciados;
	private Integer momentosMonIniciados;
	private Integer momentosMonCompletados;
	private Integer completadosSinFirmar;
	private Integer notasSinResolver;
	private String nombrePais;
	private String nombreProvincia;
	private String nombreEntidad;
	private String nombreGrupo;
	private String style;
	
	
	public SujetoGeneral(String nombreSujeto, Integer momentosIniciados,
			Integer momentosCompletados, Integer momentosFirmados,
			Integer momentosAtrasados, Integer momentosTotal,
			Integer crdNoIniciadas, Integer crdIniciadas, Integer crdCompletadas, Integer crdFirmada,
			Integer crdTotal, Integer momentosMonNoIniciados, Integer momentosMonIniciados,
			Integer momentosMonCompletados, Integer completadosSinFirmar, Integer notasSinResolver,
			String nombrePais, String nombreProvincia, String nombreEntidad,
			String nombreGrupo, String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.momentosIniciados = momentosIniciados;
		this.momentosCompletados = momentosCompletados;
		this.momentosFirmados = momentosFirmados;
		this.momentosAtrasados = momentosAtrasados;
		this.momentosTotal = momentosTotal;
		this.crdNoIniciadas = crdNoIniciadas;
		this.crdIniciadas = crdIniciadas;
		this.crdCompletadas = crdCompletadas;
		this.crdFirmada = crdFirmada;
		this.crdTotal = crdTotal;
		this.momentosMonNoIniciados = momentosMonNoIniciados;
		this.momentosMonIniciados = momentosMonIniciados;
		this.momentosMonCompletados = momentosMonCompletados;
		this.completadosSinFirmar=completadosSinFirmar;
		this.notasSinResolver = notasSinResolver;
		this.nombrePais = nombrePais;
		this.nombreProvincia = nombreProvincia;
		this.nombreEntidad = nombreEntidad;
		this.nombreGrupo = nombreGrupo;
		this.style = style;
	}

	public SujetoGeneral(String nombreSujeto, Integer momentosIniciados,
			Integer momentosCompletados, Integer momentosFirmados,
			Integer momentosAtrasados, Integer momentosTotal,
			Integer crdIniciadas, Integer crdCompletadas, Integer crdFirmada,
			Integer crdTotal, Integer momentosMonIniciados,
			Integer momentosMonCompletados, Integer notasSinResolver
			) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.momentosIniciados = momentosIniciados;
		this.momentosCompletados = momentosCompletados;
		this.momentosFirmados = momentosFirmados;
		this.momentosAtrasados = momentosAtrasados;
		this.momentosTotal = momentosTotal;
		this.crdIniciadas = crdIniciadas;
		this.crdCompletadas = crdCompletadas;
		this.crdFirmada = crdFirmada;
		this.crdTotal = crdTotal;
		this.momentosMonIniciados = momentosMonIniciados;
		this.momentosMonCompletados = momentosMonCompletados;
		this.notasSinResolver = notasSinResolver;
	}
	
	public String getNombreSujeto() {
		return nombreSujeto;
	}
	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}
	public Integer getMomentosIniciados() {
		return momentosIniciados;
	}
	public void setMomentosIniciados(Integer momentosIniciados) {
		this.momentosIniciados = momentosIniciados;
	}
	public Integer getMomentosCompletados() {
		return momentosCompletados;
	}
	public void setMomentosCompletados(Integer momentosCompletados) {
		this.momentosCompletados = momentosCompletados;
	}
	public Integer getMomentosTotal() {
		return momentosTotal;
	}
	public void setMomentosTotal(Integer momentosTotal) {
		this.momentosTotal = momentosTotal;
	}
	public Integer getMomentosAtrasados() {
		return momentosAtrasados;
	}
	public void setMomentosAtrasados(Integer momentosAtrasados) {
		this.momentosAtrasados = momentosAtrasados;
	}
	public Integer getCrdIniciadas() {
		return crdIniciadas;
	}
	public void setCrdIniciadas(Integer crdIniciadas) {
		this.crdIniciadas = crdIniciadas;
	}
	public Integer getCrdCompletadas() {
		return crdCompletadas;
	}
	public void setCrdCompletadas(Integer crdCompletadas) {
		this.crdCompletadas = crdCompletadas;
	}
	public Integer getCrdTotal() {
		return crdTotal;
	}
	public void setCrdTotal(Integer crdTotal) {
		this.crdTotal = crdTotal;
	}
	public Integer getNotasSinResolver() {
		return notasSinResolver;
	}
	public void setNotasSinResolver(Integer notasSinResolver) {
		this.notasSinResolver = notasSinResolver;
	}
	public Integer getMomentosFirmados() {
		return momentosFirmados;
	}
	public void setMomentosFirmados(Integer momentosFirmados) {
		this.momentosFirmados = momentosFirmados;
	}
	public Integer getCrdFirmada() {
		return crdFirmada;
	}
	public void setCrdFirmada(Integer crdFirmada) {
		this.crdFirmada = crdFirmada;
	}
	public Integer getMomentosMonIniciados() {
		return momentosMonIniciados;
	}
	public void setMomentosMonIniciados(Integer momentosMonIniciados) {
		this.momentosMonIniciados = momentosMonIniciados;
	}
	public Integer getMomentosMonCompletados() {
		return momentosMonCompletados;
	}
	public void setMomentosMonCompletados(Integer momentosMonCompletados) {
		this.momentosMonCompletados = momentosMonCompletados;
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
	public String getNombreGrupo() {
		return nombreGrupo;
	}
	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}


	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}
	
	public Integer getcompletadosSinFirmar(){
		return completadosSinFirmar;
	}
	
	public void setcompletadosSinFirmar(Integer v){
		this.completadosSinFirmar=v;
	}

	public Integer getCrdNoIniciadas() {
		return crdNoIniciadas;
	}

	public void setCrdNoIniciadas(Integer crdNoIniciadas) {
		this.crdNoIniciadas = crdNoIniciadas;
	}

	public Integer getMomentosMonNoIniciados() {
		return momentosMonNoIniciados;
	}

	public void setMomentosMonNoIniciados(Integer momentosMonNoIniciados) {
		this.momentosMonNoIniciados = momentosMonNoIniciados;
	}

	public Integer getCompletadosSinFirmar() {
		return completadosSinFirmar;
	}

	public void setCompletadosSinFirmar(Integer completadosSinFirmar) {
		this.completadosSinFirmar = completadosSinFirmar;
	}
	
	
}
