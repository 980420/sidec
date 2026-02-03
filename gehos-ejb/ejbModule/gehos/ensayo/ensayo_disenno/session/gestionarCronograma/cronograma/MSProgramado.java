package gehos.ensayo.ensayo_disenno.session.gestionarCronograma.cronograma;

public class MSProgramado {

	String nombre_MS;
	String evaluacion;
	String tratamiento;
	String seguimiento;
	
	public MSProgramado(String nombre_MS,
	String evaluacion,
	String tratamiento,
	String seguimiento){
		this.nombre_MS=nombre_MS;
		this.evaluacion= evaluacion;
		this.tratamiento= tratamiento;
		this.seguimiento= seguimiento;
		
	}
	public String getNombre_MS() {
		return nombre_MS;
	}
	public void setNombre_MS(String nombre_MS) {
		this.nombre_MS = nombre_MS;
	}
	public String getEvaluacion() {
		return evaluacion;
	}
	public void setEvaluacion(String evaluacion) {
		this.evaluacion = evaluacion;
	}
	public String getTratamiento() {
		return tratamiento;
	}
	public void setTratamiento(String tratamiento) {
		this.tratamiento = tratamiento;
	}
	public String getSeguimiento() {
		return seguimiento;
	}
	public void setSeguimiento(String seguimiento) {
		this.seguimiento = seguimiento;
	}
}
