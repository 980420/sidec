package gehos.ensayo.ensayo_conduccion.gestionarNewCronograma;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)

///documentacion
public class SujetoGeneralNewCronograma {
	
	private String nombreSujeto; //codigoSujeto
	private String nombreMSP; 
	private String fechaCMSP; //fechaCreacionMSP
	private String nombreEntidad; //entidad
	private String nombreGrupo; //grupo
	private String style;
	public SujetoGeneralNewCronograma(String nombreSujeto, String nombreMSP,
			String fechaCMSP, String nombreEntidad, String nombreGrupo,
			String style) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.nombreMSP = nombreMSP;
		this.fechaCMSP = fechaCMSP;
		this.nombreEntidad = nombreEntidad;
		this.nombreGrupo = nombreGrupo;
		this.style = style;
	}
	public String getNombreSujeto() {
		return nombreSujeto;
	}
	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}
	public String getNombreMSP() {
		return nombreMSP;
	}
	public void setNombreMSP(String nombreMSP) {
		this.nombreMSP = nombreMSP;
	}
	public String getFechaCMSP() {
		return fechaCMSP;
	}
	public void setFechaCMSP(String fechaCMSP) {
		this.fechaCMSP = fechaCMSP;
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
	
	
	
}
