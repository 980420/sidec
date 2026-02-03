package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("pais")
@Scope(ScopeType.CONVERSATION)
public class Pais {
	
	private String valorpais;
	private List<Provincia> estados;
	private long totalMINacion;
	private long totalMCNacion;
	private long totalMTNacion;
	private long totalMANacion;
	private long totalMNINacion;
	private long totalCINacion;
	private long totalCCNacion;
	private long totalCTNacion;
	private long totalNSRNacion;
	private long totalMFNacion;
	private long totalCFNacion;
	private long totalCNINacion;
	private	long totalMomentosMonIniciados;
	private	long totalMomentosMonCompletados;
	private	long totalNotasSinResolver;
	private long totalMNoINacion;
	private long totalSENacion;
	private long totalSNINacion;
	private long totalSINacion;
	private long totalSMINacion;
	private long totalSujIntNacion;
	private long totalSujEvTNacion;
	private long totalSujTraNacion;
	private long totalSujSeNacion;
	private long totalSujTTNacion;
	private long totalTINacion;
	private long totalNotiNacion;
	
	public Pais(String valorpais, List<Provincia> estados, long totalMINacion,long totalMCNacion, long totalMTNacion, long totalMANacion, long totalCINacion, long totalCCNacion, long totalCTNacion, long totalMFNacion, long totalCFNacion,long totalMomentosMonIniciados,long totalMomentosMonCompletados,long totalNotasSinResolver, long totalCNINacion, long totalMNINacion)
	{
		super();
		this.valorpais=valorpais;
		this.estados=estados;
		this.totalMINacion = totalMINacion;
		this.totalMCNacion = totalMCNacion;
		this.totalMTNacion = totalMTNacion;
		this.totalMANacion = totalMANacion;
		this.totalCINacion = totalCINacion;
		this.totalCCNacion = totalCCNacion;
		this.totalCTNacion = totalCTNacion;
		this.totalMFNacion=totalMFNacion;
		this.totalCFNacion=totalCFNacion;
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
		this.totalNotasSinResolver = totalNotasSinResolver;
		this.totalCNINacion = totalCNINacion;
		this.totalMNINacion = totalMNINacion;
	
		
	}
	public Pais(String valorpais, List<Provincia> estados, long totalSENacion,long totalSNINacion, long totalSINacion, long totalSMINacion, long totalSujIntNacion, long totalSujEvTNacion, long totalSujTraNacion, long totalSujSeNacion, long totalSujTTNacion, long totalTINacion)
	{
		super();
		this.valorpais=valorpais;
		this.estados=estados;
		this.totalSENacion = totalSENacion;
		this.totalSNINacion = totalSNINacion;
		this.totalSINacion = totalSINacion;
		this.totalSMINacion = totalSMINacion;
		this.totalSujIntNacion = totalSujIntNacion;
		this.totalSujEvTNacion=totalSujEvTNacion;
		this.totalSujTraNacion=totalSujTraNacion;
		this.totalSujSeNacion=totalSujSeNacion;
		this.totalSujTTNacion=totalSujTTNacion;
		this.totalTINacion=totalTINacion;
	}
	
	public Pais(String valorpais, List<Provincia> estados, long totalMINacion,long totalMCNacion, long totalNSRNacion, long totalMNoINacion, long totalNotiNacion)
	{
		super();
		this.valorpais=valorpais;
		this.estados=estados;
		this.totalMINacion = totalMINacion;
		this.totalMCNacion = totalMCNacion;
		this.totalNSRNacion = totalNSRNacion;
		this.totalMNoINacion=totalMNoINacion;
		this.totalNotiNacion=totalNotiNacion;
		
	
		
	}
	
	public Pais(String valorpais, List<Provincia> estados, long totalMINacion)
	{
		super();
		this.valorpais=valorpais;
		this.estados=estados;
		this.totalMINacion = totalMINacion;
	
		
	}
	public Pais()
	{
		super();
	}
	


	public List<Provincia> getEstados() {
		return estados;
	}

	public void setEstados(List<Provincia> estados) {
		this.estados = estados;
	}

	public String getValorpais() {
		return valorpais;
	}

	public void setValorpais(String valorpais) {
		this.valorpais = valorpais;
	}

	public long getTotalMINacion() {
		return totalMINacion;
	}

	public void setTotalMINacion(long totalMINacion) {
		this.totalMINacion = totalMINacion;
	}

	public long getTotalMCNacion() {
		return totalMCNacion;
	}

	public void setTotalMCNacion(long totalMCNacion) {
		this.totalMCNacion = totalMCNacion;
	}

	public long getTotalMTNacion() {
		return totalMTNacion;
	}

	public void setTotalMTNacion(long totalMTNacion) {
		this.totalMTNacion = totalMTNacion;
	}

	public long getTotalMANacion() {
		return totalMANacion;
	}

	public void setTotalMANacion(long totalMANacion) {
		this.totalMANacion = totalMANacion;
	}

	public long getTotalCINacion() {
		return totalCINacion;
	}

	public void setTotalCINacion(long totalCINacion) {
		this.totalCINacion = totalCINacion;
	}

	public long getTotalCCNacion() {
		return totalCCNacion;
	}

	public void setTotalCCNacion(long totalCCNacion) {
		this.totalCCNacion = totalCCNacion;
	}

	public long getTotalCTNacion() {
		return totalCTNacion;
	}

	public void setTotalCTNacion(long totalCTNacion) {
		this.totalCTNacion = totalCTNacion;
	}

	public long getTotalNSRNacion() {
		return totalNSRNacion;
	}

	public void setTotalNSRNacion(long totalNSRNacion) {
		this.totalNSRNacion = totalNSRNacion;
	}

	public long getTotalSENacion() {
		return totalSENacion;
	}

	public void setTotalSENacion(long totalSENacion) {
		this.totalSENacion = totalSENacion;
	}

	public long getTotalSNINacion() {
		return totalSNINacion;
	}

	public void setTotalSNINacion(long totalSNINacion) {
		this.totalSNINacion = totalSNINacion;
	}

	public long getTotalSINacion() {
		return totalSINacion;
	}

	public void setTotalSINacion(long totalSINacion) {
		this.totalSINacion = totalSINacion;
	}

	public long getTotalSMINacion() {
		return totalSMINacion;
	}

	public void setTotalSMINacion(long totalSMINacion) {
		this.totalSMINacion = totalSMINacion;
	}

	public long getTotalSujIntNacion() {
		return totalSujIntNacion;
	}

	public void setTotalSujIntNacion(long totalSujIntNacion) {
		this.totalSujIntNacion = totalSujIntNacion;
	}
	
	public long getTotalMFNacion() {
		return totalMFNacion;
	}
	public void setTotalMFNacion(long totalMFNacion) {
		this.totalMFNacion = totalMFNacion;
	}
	public long getTotalCFNacion() {
		return totalCFNacion;
	}
	public void setTotalCFNacion(long totalCFNacion) {
		this.totalCFNacion = totalCFNacion;
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
	public long getTotalMNINacion() {
		return totalMNINacion;
	}
	public void setTotalMNINacion(long totalMNINacion) {
		this.totalMNINacion = totalMNINacion;
	}
	public long getTotalCNINacion() {
		return totalCNINacion;
	}
	public void setTotalCNINacion(long totalCNINacion) {
		this.totalCNINacion = totalCNINacion;
	}
	public long getTotalSujEvTNacion() {
		return totalSujEvTNacion;
	}
	public void setTotalSujEvTNacion(long totalSujEvTNacion) {
		this.totalSujEvTNacion = totalSujEvTNacion;
	}
	public long getTotalSujTraNacion() {
		return totalSujTraNacion;
	}
	public void setTotalSujTraNacion(long totalSujTraNacion) {
		this.totalSujTraNacion = totalSujTraNacion;
	}
	public long getTotalSujSeNacion() {
		return totalSujSeNacion;
	}
	public void setTotalSujSeNacion(long totalSujSeNacion) {
		this.totalSujSeNacion = totalSujSeNacion;
	}
	public long getTotalMNoINacion() {
		return totalMNoINacion;
	}
	public void setTotalMNoINacion(long totalMNoINacion) {
		this.totalMNoINacion = totalMNoINacion;
	}
	public long getTotalSujTTNacion() {
		return totalSujTTNacion;
	}
	public void setTotalSujTTNacion(long totalSujTTNacion) {
		this.totalSujTTNacion = totalSujTTNacion;
	}
	public long getTotalTINacion() {
		return totalTINacion;
	}
	public void setTotalTINacion(long totalTINacion) {
		this.totalTINacion = totalTINacion;
	}
	public long getTotalNotiNacion() {
		return totalNotiNacion;
	}
	public void setTotalNotiNacion(long totalNotiNacion) {
		this.totalNotiNacion = totalNotiNacion;
	}
	
	
	

}
