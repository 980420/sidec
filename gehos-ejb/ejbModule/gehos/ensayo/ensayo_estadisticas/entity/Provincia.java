package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("provincia")
@Scope(ScopeType.CONVERSATION)
public class Provincia {
	
	private String valor;
	private List<Entidad> entidades;
	private long totalMIEstado;
	private long totalMCEstado;
	private long totalMTEstado;
	private long totalMAEstado;
	private long totalMNIEstado;
	private long totalCIEstado;
	private long totalCCEstado;
	private long totalCTEstado;
	private long totalNSREstado;
	private long totalMFEstado;
	private long totalCFEstado;
	private long totalCNIEstado;
	private	long totalMomentosMonIniciados;
	private	long totalMomentosMonCompletados;
	private	long totalNotasSinResolver;
	private long totalMNoIEstado;
	private long totalSujTTEstado;
	private long totalTIEstado;
	
	private long totalSEEstado;
	private long totalSNIEstado;
	private long totalSIEstado;
	private long totalSMIEstado;
	private long totalSujIntEstado;
	private long totalSujEvTEstado;
	private long totalSujTraEstado;
	private long totalSujSeEstado;
	private long totalNotiEstado;
	
	public Provincia(String valor, List<Entidad> entidades, long totalMIEstado,long totalMCEstado, long totalMTEstado, long totalMAEstado, long totalCIEstado, long totalCCEstado, long totalCTEstado, long totalMFEstado, long totalCFEstado,long totalMomentosMonIniciados,long totalMomentosMonCompletados,long totalNotasSinResolver, long totalCNIEstado, long totalMNIEstado)
	{
		super();
		this.valor=valor;
		this.entidades=entidades;
		this.totalMIEstado = totalMIEstado;
		this.totalMCEstado = totalMCEstado;
		this.totalMTEstado = totalMTEstado;
		this.totalMAEstado = totalMAEstado;
		this.totalCIEstado = totalCIEstado;
		this.totalCCEstado = totalCCEstado;
		this.totalCTEstado = totalCTEstado;
		this.totalMFEstado=totalMFEstado;
		this.totalCFEstado=totalCFEstado;
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
		this.totalNotasSinResolver = totalNotasSinResolver;
		this.totalMNIEstado = totalMNIEstado;
		this.totalCNIEstado = totalCNIEstado;
	}
	
	public Provincia(String valor, List<Entidad> entidades, long totalSEEstado,long totalSNIEstado, long totalSIEstado, long totalSMIEstado, long totalSujIntEstado, long totalSujEvTEstado, long totalSujTraEstado, long totalSujSeEstado, long totalSujTTEstado, long totalTIEstado)
	{
		super();
		this.valor=valor;
		this.entidades=entidades;
		this.totalSEEstado = totalSEEstado;
		this.totalSNIEstado = totalSNIEstado;
		this.totalSIEstado = totalSIEstado;
		this.totalSMIEstado = totalSMIEstado;
		this.totalSujIntEstado = totalSujIntEstado;
		this.totalSujEvTEstado=totalSujEvTEstado;
		this.totalSujTraEstado=totalSujTraEstado;
		this.totalSujSeEstado=totalSujSeEstado;
		this.totalSujTTEstado=totalSujTTEstado;
		this.totalTIEstado=totalTIEstado;
		
	}
	
	public Provincia(String valor, List<Entidad> entidades, long totalMIEstado,long totalMCEstado, long totalNSREstado, long totalMNoIEstado, long totalNotiEstado)
	{
		super();
		this.valor=valor;
		this.entidades=entidades;
		this.totalMIEstado = totalMIEstado;
		this.totalMCEstado = totalMCEstado;
		this.totalNSREstado = totalNSREstado;
		this.totalMNoIEstado=totalMNoIEstado;
		this.totalNotiEstado=totalNotiEstado;
		
	}
	
	public Provincia(String valor, List<Entidad> entidades, long totalMIEstado)
	{
		super();
		this.valor=valor;
		this.entidades=entidades;
		this.totalMIEstado = totalMIEstado;
		
	}
	
	public Provincia()
	{
		super();
	}
	
	
	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public List<Entidad> getEntidades() {
		return entidades;
	}

	public void setEntidades(List<Entidad> entidades) {
		this.entidades = entidades;
	}

	public long getTotalMIEstado() {
		return totalMIEstado;
	}

	public void setTotalMIEstado(long totalMIEstado) {
		this.totalMIEstado = totalMIEstado;
	}

	public long getTotalMCEstado() {
		return totalMCEstado;
	}

	public void setTotalMCEstado(long totalMCEstado) {
		this.totalMCEstado = totalMCEstado;
	}

	public long getTotalMTEstado() {
		return totalMTEstado;
	}

	public void setTotalMTEstado(long totalMTEstado) {
		this.totalMTEstado = totalMTEstado;
	}

	public long getTotalMAEstado() {
		return totalMAEstado;
	}

	public void setTotalMAEstado(long totalMAEstado) {
		this.totalMAEstado = totalMAEstado;
	}

	public long getTotalCIEstado() {
		return totalCIEstado;
	}

	public void setTotalCIEstado(long totalCIEstado) {
		this.totalCIEstado = totalCIEstado;
	}

	public long getTotalCCEstado() {
		return totalCCEstado;
	}

	public void setTotalCCEstado(long totalCCEstado) {
		this.totalCCEstado = totalCCEstado;
	}

	public long getTotalCTEstado() {
		return totalCTEstado;
	}

	public void setTotalCTEstado(long totalCTEstado) {
		this.totalCTEstado = totalCTEstado;
	}

	public long getTotalNSREstado() {
		return totalNSREstado;
	}

	public void setTotalNSREstado(long totalNSREstado) {
		this.totalNSREstado = totalNSREstado;
	}

	public long getTotalSEEstado() {
		return totalSEEstado;
	}

	public void setTotalSEEstado(long totalSEEstado) {
		this.totalSEEstado = totalSEEstado;
	}

	public long getTotalSNIEstado() {
		return totalSNIEstado;
	}

	public void setTotalSNIEstado(long totalSNIEstado) {
		this.totalSNIEstado = totalSNIEstado;
	}

	public long getTotalSIEstado() {
		return totalSIEstado;
	}

	public void setTotalSIEstado(long totalSIEstado) {
		this.totalSIEstado = totalSIEstado;
	}

	public long getTotalSMIEstado() {
		return totalSMIEstado;
	}

	public void setTotalSMIEstado(long totalSMIEstado) {
		this.totalSMIEstado = totalSMIEstado;
	}

	public long getTotalSujIntEstado() {
		return totalSujIntEstado;
	}

	public void setTotalSujIntEstado(long totalSujIntEstado) {
		this.totalSujIntEstado = totalSujIntEstado;
	}
	
	public long getTotalMFEstado() {
		return totalMFEstado;
	}

	public void setTotalMFEstado(long totalMFEstado) {
		this.totalMFEstado = totalMFEstado;
	}

	public long getTotalCFEstado() {
		return totalCFEstado;
	}

	public void setTotalCFEstado(long totalCFEstado) {
		this.totalCFEstado = totalCFEstado;
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
	
	public long getTotalMNIEstado() {
		return totalMNIEstado;
	}

	public void setTotalMNIEstado(long totalMNIEstado) {
		this.totalMNIEstado = totalMNIEstado;
	}

	public long getTotalCNIEstado() {
		return totalCNIEstado;
	}

	public void setTotalCNIEstado(long totalCNIEstado) {
		this.totalCNIEstado = totalCNIEstado;
	}

	public long getTotalSujEvTEstado() {
		return totalSujEvTEstado;
	}

	public void setTotalSujEvTEstado(long totalSujEvTEstado) {
		this.totalSujEvTEstado = totalSujEvTEstado;
	}

	public long getTotalSujTraEstado() {
		return totalSujTraEstado;
	}

	public void setTotalSujTraEstado(long totalSujTraEstado) {
		this.totalSujTraEstado = totalSujTraEstado;
	}

	public long getTotalSujSeEstado() {
		return totalSujSeEstado;
	}

	public void setTotalSujSeEstado(long totalSujSeEstado) {
		this.totalSujSeEstado = totalSujSeEstado;
	}

	public long getTotalMNoIEstado() {
		return totalMNoIEstado;
	}

	public void setTotalMNoIEstado(long totalMNoIEstado) {
		this.totalMNoIEstado = totalMNoIEstado;
	}

	public long getTotalSujTTEstado() {
		return totalSujTTEstado;
	}

	public void setTotalSujTTEstado(long totalSujTTEstado) {
		this.totalSujTTEstado = totalSujTTEstado;
	}

	public long getTotalTIEstado() {
		return totalTIEstado;
	}

	public void setTotalTIEstado(long totalTIEstado) {
		this.totalTIEstado = totalTIEstado;
	}

	public long getTotalNotiEstado() {
		return totalNotiEstado;
	}

	public void setTotalNotiEstado(long totalNotiEstado) {
		this.totalNotiEstado = totalNotiEstado;
	}

	

	

}
