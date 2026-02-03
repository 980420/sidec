package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;


@Scope(ScopeType.CONVERSATION)
public class Entidad {
	
	private String nombre;
	private List<Ensayo> estudios;
	private long totalMIEntidad;
	private long totalMCEntidad;
	private long totalMTEntidad;
	private long totalMAEntidad;
	private long totalMNIEntidad;
	private long totalCIEntidad;
	private long totalCCEntidad;
	private long totalCTEntidad;
	private long totalNSREntidad;
	private long totalMFEntidad;
	private long totalCFEntidad;
	private long totalCNIEntidad;
	private	long totalMomentosMonIniciados;
	private	long totalMomentosMonCompletados;
	private	long totalNotasSinResolver;
	private long totalMNoIEntidad;
	private long totalITEntidad;
	private long totalSujTTentidad;
	
	
	private long totalSEEntidad;
	private long totalSNIEntidad;
	private long totalSIEntidad;
	private long totalSMIEntidad;
	private long totalSujIntEntidad;
	private long totalSujEvTEntidad;
	private long totalSujTraEntidad;
	private long totalSujSeEntidad;
	private long totalNotiEntidad;
	 
	public Entidad(String nombre, List<Ensayo> estudios, long totalMIEntidad,long totalMCEntidad, long totalMTEntidad, long totalMAEntidad, long totalCIEntidad, long totalCCEntidad, long totalCTEntidad, long totalMFEntidad, long totalCFEntidad,long totalMomentosMonIniciados,long totalMomentosMonCompletados,long totalNotasSinResolver, long totalCNIEntidad, long totalMNIEntidad)
	{
		super();
		this.nombre=nombre;
		this.estudios=estudios;
		this.totalMIEntidad = totalMIEntidad;
		this.totalMCEntidad = totalMCEntidad;
		this.totalMTEntidad = totalMTEntidad;
		this.totalMAEntidad = totalMAEntidad;
		this.totalCIEntidad = totalCIEntidad;
		this.totalCCEntidad = totalCCEntidad;
		this.totalCTEntidad = totalCTEntidad;
		this.totalMFEntidad=totalMFEntidad;
		this.totalCFEntidad=totalCFEntidad;
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
		this.totalNotasSinResolver = totalNotasSinResolver;
		this.totalMNIEntidad = totalMNIEntidad;
		this.totalCNIEntidad = totalCNIEntidad;
	}
	
	public Entidad(String nombre, List<Ensayo> estudios, long totalSEEntidad,long totalSNIEntidad, long totalSIEntidad, long totalSMIEntidad, long totalSujIntEntidad, long totalSujEvTEntidad, long totalSujTraEntidad, long totalSujSeEntidad, long totalITEntidad, long totalSujTTentidad)
	{
		super();
		this.nombre=nombre;
		this.estudios=estudios;
		this.totalSEEntidad = totalSEEntidad;
		this.totalSNIEntidad = totalSNIEntidad;
		this.totalSIEntidad = totalSIEntidad;
		this.totalSMIEntidad = totalSMIEntidad;
		this.totalSujIntEntidad = totalSujIntEntidad;
		this.totalSujEvTEntidad=totalSujEvTEntidad;
		this.totalSujTraEntidad=totalSujTraEntidad;
		this.totalSujSeEntidad=totalSujSeEntidad;
		this.totalITEntidad=totalITEntidad;
		this.totalSujTTentidad=totalSujTTentidad;
	}
	
	public Entidad(String nombre, List<Ensayo> estudios, long totalMIEntidad,long totalMCEntidad, long totalNSREntidad, long totalMNoIEntidad, long totalNotiEntidad)
	{
		super();
		this.nombre=nombre;
		this.estudios=estudios;
		this.totalMIEntidad = totalMIEntidad;
		this.totalMCEntidad = totalMCEntidad;
		this.totalNSREntidad = totalNSREntidad;
		this.totalMNoIEntidad=totalMNoIEntidad;
		this.totalNotiEntidad=totalNotiEntidad;
		
	}
	
	public Entidad(String nombre, List<Ensayo> estudios, long totalMIEntidad)
	{
		super();
		this.nombre=nombre;
		this.estudios=estudios;
		this.totalMIEntidad = totalMIEntidad;
		
	}
	
	public Entidad()
	{
		super();
	}
	
	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Ensayo> getEstudios() {
		return estudios;
	}

	public void setEstudios(List<Ensayo> estudios) {
		this.estudios = estudios;
	}

	public long getTotalMIEntidad() {
		return totalMIEntidad;
	}

	public void setTotalMIEntidad(long totalMIEntidad) {
		this.totalMIEntidad = totalMIEntidad;
	}

	public long getTotalMCEntidad() {
		return totalMCEntidad;
	}

	public void setTotalMCEntidad(long totalMCEntidad) {
		this.totalMCEntidad = totalMCEntidad;
	}

	public long getTotalMTEntidad() {
		return totalMTEntidad;
	}

	public void setTotalMTEntidad(long totalMTEntidad) {
		this.totalMTEntidad = totalMTEntidad;
	}

	public long getTotalMAEntidad() {
		return totalMAEntidad;
	}

	public void setTotalMAEntidad(long totalMAEntidad) {
		this.totalMAEntidad = totalMAEntidad;
	}

	public long getTotalCIEntidad() {
		return totalCIEntidad;
	}

	public void setTotalCIEntidad(long totalCIEntidad) {
		this.totalCIEntidad = totalCIEntidad;
	}

	public long getTotalCCEntidad() {
		return totalCCEntidad;
	}

	public void setTotalCCEntidad(long totalCCEntidad) {
		this.totalCCEntidad = totalCCEntidad;
	}

	public long getTotalCTEntidad() {
		return totalCTEntidad;
	}

	public void setTotalCTEntidad(long totalCTEntidad) {
		this.totalCTEntidad = totalCTEntidad;
	}

	public long getTotalNSREntidad() {
		return totalNSREntidad;
	}

	public void setTotalNSREntidad(long totalNSREntidad) {
		this.totalNSREntidad = totalNSREntidad;
	}

	public long getTotalSEEntidad() {
		return totalSEEntidad;
	}

	public void setTotalSEEntidad(long totalSEEntidad) {
		this.totalSEEntidad = totalSEEntidad;
	}

	public long getTotalSNIEntidad() {
		return totalSNIEntidad;
	}

	public void setTotalSNIEntidad(long totalSNIEntidad) {
		this.totalSNIEntidad = totalSNIEntidad;
	}

	public long getTotalSIEntidad() {
		return totalSIEntidad;
	}

	public void setTotalSIEntidad(long totalSIEntidad) {
		this.totalSIEntidad = totalSIEntidad;
	}

	public long getTotalSMIEntidad() {
		return totalSMIEntidad;
	}

	public void setTotalSMIEntidad(long totalSMIEntidad) {
		this.totalSMIEntidad = totalSMIEntidad;
	}

	public long getTotalSujIntEntidad() {
		return totalSujIntEntidad;
	}

	public void setTotalSujIntEntidad(long totalSujIntEntidad) {
		this.totalSujIntEntidad = totalSujIntEntidad;
	}
	
	public long getTotalMFEntidad() {
		return totalMFEntidad;
	}

	public void setTotalMFEntidad(long totalMFEntidad) {
		this.totalMFEntidad = totalMFEntidad;
	}

	public long getTotalCFEntidad() {
		return totalCFEntidad;
	}

	public void setTotalCFEntidad(long totalCFEntidad) {
		this.totalCFEntidad = totalCFEntidad;
	}

	public long getTotalMomentosMonIniciados() {
		return totalMomentosMonIniciados;
	}

	public void setTotalMomentosMonIniciados(long totalMomentosMonIniciados) {
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
	}

	public long getTotalMomentosMonCompletados() {
		return totalMomentosMonCompletados;
	}

	public void setTotalMomentosMonCompletados(long totalMomentosMonCompletados) {
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
	}

	public long getTotalNotasSinResolver() {
		return totalNotasSinResolver;
	}

	public void setTotalNotasSinResolver(long totalNotasSinResolver) {
		this.totalNotasSinResolver = totalNotasSinResolver;
	}
	
	public long getTotalMNIEntidad() {
		return totalMNIEntidad;
	}

	public void setTotalMNIEntidad(long totalMNIEntidad) {
		this.totalMNIEntidad = totalMNIEntidad;
	}

	public long getTotalCNIEntidad() {
		return totalCNIEntidad;
	}

	public void setTotalCNIEntidad(long totalCNIEntidad) {
		this.totalCNIEntidad = totalCNIEntidad;
	}

	public long getTotalSujEvTEntidad() {
		return totalSujEvTEntidad;
	}

	public void setTotalSujEvTEntidad(long totalSujEvTEntidad) {
		this.totalSujEvTEntidad = totalSujEvTEntidad;
	}

	public long getTotalSujTraEntidad() {
		return totalSujTraEntidad;
	}

	public void setTotalSujTraEntidad(long totalSujTraEntidad) {
		this.totalSujTraEntidad = totalSujTraEntidad;
	}

	public long getTotalSujSeEntidad() {
		return totalSujSeEntidad;
	}

	public void setTotalSujSeEntidad(long totalSujSeEntidad) {
		this.totalSujSeEntidad = totalSujSeEntidad;
	}

	public long getTotalMNoIEntidad() {
		return totalMNoIEntidad;
	}

	public void setTotalMNoIEntidad(long totalMNoIEntidad) {
		this.totalMNoIEntidad = totalMNoIEntidad;
	}

	public long getTotalITEntidad() {
		return totalITEntidad;
	}

	public void setTotalITEntidad(long totalITEntidad) {
		this.totalITEntidad = totalITEntidad;
	}

	public long getTotalSujTTentidad() {
		return totalSujTTentidad;
	}

	public void setTotalSujTTentidad(long totalSujTTentidad) {
		this.totalSujTTentidad = totalSujTTentidad;
	}

	public long getTotalNotiEntidad() {
		return totalNotiEntidad;
	}

	public void setTotalNotiEntidad(long totalNotiEntidad) {
		this.totalNotiEntidad = totalNotiEntidad;
	}
	
	


}
