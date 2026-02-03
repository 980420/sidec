package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class ReporteExpeditoUno {
	
	private String estudio;
	private String fase;
	private String nomNot;
	private String emailTele;
	private String institucion;
	private String provincia;
	private String idPaciente;
	private String edad;
	private String sexo;
	private String peso;
	private String raza;
	private String ea;
	private String intensidad;
	private String descripEA;
	private String conscP;
	private String relacionC;
	
	
	public ReporteExpeditoUno(String estudio, String fase, String nomNot,
			String emailTele, String institucion, String provincia,
			String idPaciente, String edad, String sexo, String peso,
			String raza, String ea, String intensidad, String descripEA,
			String conscP, String relacionC) {
		super();
		this.estudio = estudio;
		this.fase = fase;
		this.nomNot = nomNot;
		this.emailTele = emailTele;
		this.institucion = institucion;
		this.provincia = provincia;
		this.idPaciente = idPaciente;
		this.edad = edad;
		this.sexo = sexo;
		this.peso = peso;
		this.raza = raza;
		this.ea = ea;
		this.intensidad = intensidad;
		this.descripEA = descripEA;
		this.conscP = conscP;
		this.relacionC = relacionC;
	}
	public String getEstudio() {
		return estudio;
	}
	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}
	public String getFase() {
		return fase;
	}
	public void setFase(String fase) {
		this.fase = fase;
	}
	public String getNomNot() {
		return nomNot;
	}
	public void setNomNot(String nomNot) {
		this.nomNot = nomNot;
	}
	public String getEmailTele() {
		return emailTele;
	}
	public void setEmailTele(String emailTele) {
		this.emailTele = emailTele;
	}
	public String getInstitucion() {
		return institucion;
	}
	public void setInstitucion(String institucion) {
		this.institucion = institucion;
	}
	public String getProvincia() {
		return provincia;
	}
	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}
	public String getIdPaciente() {
		return idPaciente;
	}
	public void setIdPaciente(String idPaciente) {
		this.idPaciente = idPaciente;
	}
	public String getEdad() {
		return edad;
	}
	public void setEdad(String edad) {
		this.edad = edad;
	}
	public String getSexo() {
		return sexo;
	}
	public void setSexo(String sexo) {
		this.sexo = sexo;
	}
	public String getPeso() {
		return peso;
	}
	public void setPeso(String peso) {
		this.peso = peso;
	}
	public String getRaza() {
		return raza;
	}
	public void setRaza(String raza) {
		this.raza = raza;
	}
	public String getEa() {
		return ea;
	}
	public void setEa(String ea) {
		this.ea = ea;
	}
	public String getIntensidad() {
		return intensidad;
	}
	public void setIntensidad(String intensidad) {
		this.intensidad = intensidad;
	}
	public String getDescripEA() {
		return descripEA;
	}
	public void setDescripEA(String descripEA) {
		this.descripEA = descripEA;
	}
	public String getConscP() {
		return conscP;
	}
	public void setConscP(String conscP) {
		this.conscP = conscP;
	}
	public String getRelacionC() {
		return relacionC;
	}
	public void setRelacionC(String relacionC) {
		this.relacionC = relacionC;
	}
	
	
	
}
