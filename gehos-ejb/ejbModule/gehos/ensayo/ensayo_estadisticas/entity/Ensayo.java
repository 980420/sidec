package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class Ensayo {
	
	
	private String nombreEstudio;

	private List<GrupoSujeto> grupos;
	private long totalMIEstudio;
	private long totalMCEstudio;
	private long totalMTEstudio;
	private long totalMAEstudio;
	private long totalMNIEstudio;
	private long totalCIEstudio;
	private long totalCCEstudio;
	private long totalCTEstudio;
	private long totalNSREstudio;
	private long totalMFEstudio;
	private long totalCFestudio;
	private long totalCNIestudio;
	private	long totalMomentosMonIniciados;
	private	long totalMomentosMonCompletados;
	private	long totalNotasSinResolver;
	private long totalMNoIEstudio;
	private long totalSujetoTotalIn;
	private long totalSujetoTotalTr;
	
	
	private long totalSujetoEvaluados;
	private long totalSujetosNoInc;
	private long totalSujetoInc;
	private long totalSujetoMalINc;
	private long totalSujetoInt;
	private long totalsujetoEvaluT;
	private long totalsujetoTratamiento;
	private long totalsujetoSeguimiento;
	private long totalNotiEst;
	
	
	public Ensayo(String nombreEstudio, List<GrupoSujeto> grupos, long totalMIEstudio,long totalMCEstudio, long totalMTEstudio, long totalMAEstudio, long totalCIEstudio, long totalCCEstudio, long totalCTEstudio, long totalMFEstudio, long totalCFestudio, long totalMomentosMonIniciados,long totalMomentosMonCompletados,long totalNotasSinResolver, long totalCNIestudio, long totalMNIEstudio)
	{
		super();
		this.nombreEstudio=nombreEstudio;
		this.grupos=grupos;
		this.totalMIEstudio=totalMIEstudio;
		this.totalMCEstudio=totalMCEstudio;
		this.totalMTEstudio = totalMTEstudio;
		this.totalMAEstudio = totalMAEstudio;
		this.totalCIEstudio = totalCIEstudio;
		this.totalCCEstudio = totalCCEstudio;
		this.totalCTEstudio = totalCTEstudio;
		this.totalMFEstudio=totalMFEstudio;
		this.totalCFestudio=totalCFestudio;
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
		this.totalNotasSinResolver = totalNotasSinResolver;
		this.totalMNIEstudio = totalMNIEstudio;
		this.totalCNIestudio = totalCNIestudio;
		
	}

	public Ensayo(String nombreEstudio, List<GrupoSujeto> grupos, long totalMIEstudio,long totalMCEstudio, long totalNSREstudio, long totalMNoIEstudio, long totalNotiEst)
	{
		super();
		this.nombreEstudio=nombreEstudio;
		this.grupos=grupos;
		this.totalMIEstudio=totalMIEstudio;
		this.totalMCEstudio=totalMCEstudio;
		this.totalNSREstudio = totalNSREstudio;
		this.totalMNoIEstudio=totalMNoIEstudio;
		this.totalNotiEst=totalNotiEst;
	}
	
	public Ensayo(String nombreEstudio, List<GrupoSujeto> grupos, long totalMIEstudio)
	{
		super();
		this.nombreEstudio=nombreEstudio;
		this.grupos=grupos;
		this.totalMIEstudio=totalMIEstudio;
	}
	
	
	public Ensayo(String nombreEstudio, List<GrupoSujeto> grupos, long totalSujetoEvaluados,long totalSujetosNoInc, long totalSujetoInc, long totalSujetoMalINc, long totalSujetoInt, long totalsujetoEvaluT, long totalsujetoTratamiento, long totalsujetoSeguimiento, long totalSujetoTotalIn, long totalSujetoTotalTr)
	{
		super();
		this.nombreEstudio=nombreEstudio;
		this.grupos=grupos;
		this.totalSujetoEvaluados=totalSujetoEvaluados;
		this.totalSujetosNoInc=totalSujetosNoInc;
		this.totalSujetoInc = totalSujetoInc;
		this.totalSujetoMalINc=totalSujetoMalINc;
		this.totalSujetoInt = totalSujetoInt;
		this.totalsujetoEvaluT=totalsujetoEvaluT;
		this.totalsujetoTratamiento=totalsujetoTratamiento;
		this.totalsujetoSeguimiento=totalsujetoSeguimiento;
		this.totalSujetoTotalIn=totalSujetoTotalIn;
		this.totalSujetoTotalTr=totalSujetoTotalTr;
	}
	
	
	public Ensayo()
	{
		super();
	}
	
	

	

	

	public String getNombreEstudio() {
		return nombreEstudio;
	}

	public void setNombreEstudio(String nombreEstudio) {
		this.nombreEstudio = nombreEstudio;
	}

	public List<GrupoSujeto> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoSujeto> grupos) {
		this.grupos = grupos;
	}

	public long getTotalMIEstudio() {
		return totalMIEstudio;
	}

	public void setTotalMIEstudio(long totalMIEstudio) {
		this.totalMIEstudio = totalMIEstudio;
	}

	public long getTotalMCEstudio() {
		return totalMCEstudio;
	}

	public void setTotalMCEstudio(long totalMCEstudio) {
		this.totalMCEstudio = totalMCEstudio;
	}

	public long getTotalMTEstudio() {
		return totalMTEstudio;
	}

	public void setTotalMTEstudio(long totalMTEstudio) {
		this.totalMTEstudio = totalMTEstudio;
	}

	public long getTotalMAEstudio() {
		return totalMAEstudio;
	}

	public void setTotalMAEstudio(long totalMAEstudio) {
		this.totalMAEstudio = totalMAEstudio;
	}

	public long getTotalCIEstudio() {
		return totalCIEstudio;
	}

	public void setTotalCIEstudio(long totalCIEstudio) {
		this.totalCIEstudio = totalCIEstudio;
	}

	public long getTotalCCEstudio() {
		return totalCCEstudio;
	}

	public void setTotalCCEstudio(long totalCCEstudio) {
		this.totalCCEstudio = totalCCEstudio;
	}

	public long getTotalCTEstudio() {
		return totalCTEstudio;
	}

	public void setTotalCTEstudio(long totalCTEstudio) {
		this.totalCTEstudio = totalCTEstudio;
	}

	public long getTotalNSREstudio() {
		return totalNSREstudio;
	}

	public void setTotalNSREstudio(long totalNSREstudio) {
		this.totalNSREstudio = totalNSREstudio;
	}

	public long getTotalSujetoEvaluados() {
		return totalSujetoEvaluados;
	}

	public void setTotalSujetoEvaluados(long totalSujetoEvaluados) {
		this.totalSujetoEvaluados = totalSujetoEvaluados;
	}

	public long getTotalSujetosNoInc() {
		return totalSujetosNoInc;
	}

	public void setTotalSujetosNoInc(long totalSujetosNoInc) {
		this.totalSujetosNoInc = totalSujetosNoInc;
	}

	public long getTotalSujetoInc() {
		return totalSujetoInc;
	}

	public void setTotalSujetoInc(long totalSujetoInc) {
		this.totalSujetoInc = totalSujetoInc;
	}

	public long getTotalSujetoMalINc() {
		return totalSujetoMalINc;
	}

	public void setTotalSujetoMalINc(long totalSujetoMalINc) {
		this.totalSujetoMalINc = totalSujetoMalINc;
	}

	public long getTotalSujetoInt() {
		return totalSujetoInt;
	}

	public void setTotalSujetoInt(long totalSujetoInt) {
		this.totalSujetoInt = totalSujetoInt;
	}
	
	public long getTotalMFEstudio() {
		return totalMFEstudio;
	}

	public void setTotalMFEstudio(long totalMFEstudio) {
		this.totalMFEstudio = totalMFEstudio;
	}

	public long getTotalCFestudio() {
		return totalCFestudio;
	}

	public void setTotalCFestudio(long totalCFestudio) {
		this.totalCFestudio = totalCFestudio;
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
	
	public long getTotalMNIEstudio() {
		return totalMNIEstudio;
	}

	public void setTotalMNIEstudio(long totalMNIEstudio) {
		this.totalMNIEstudio = totalMNIEstudio;
	}

	public long getTotalCNIestudio() {
		return totalCNIestudio;
	}

	public void setTotalCNIestudio(long totalCNIestudio) {
		this.totalCNIestudio = totalCNIestudio;
	}

	public long getTotalsujetoEvaluT() {
		return totalsujetoEvaluT;
	}

	public void setTotalsujetoEvaluT(long totalsujetoEvaluT) {
		this.totalsujetoEvaluT = totalsujetoEvaluT;
	}

	public long getTotalsujetoTratamiento() {
		return totalsujetoTratamiento;
	}

	public void setTotalsujetoTratamiento(long totalsujetoTratamiento) {
		this.totalsujetoTratamiento = totalsujetoTratamiento;
	}

	public long getTotalsujetoSeguimiento() {
		return totalsujetoSeguimiento;
	}

	public void setTotalsujetoSeguimiento(long totalsujetoSeguimiento) {
		this.totalsujetoSeguimiento = totalsujetoSeguimiento;
	}

	public long getTotalMNoIEstudio() {
		return totalMNoIEstudio;
	}

	public void setTotalMNoIEstudio(long totalMNoIEstudio) {
		this.totalMNoIEstudio = totalMNoIEstudio;
	}

	public long getTotalSujetoTotalIn() {
		return totalSujetoTotalIn;
	}

	public void setTotalSujetoTotalIn(long totalSujetoTotalIn) {
		this.totalSujetoTotalIn = totalSujetoTotalIn;
	}

	public long getTotalSujetoTotalTr() {
		return totalSujetoTotalTr;
	}

	public void setTotalSujetoTotalTr(long totalSujetoTotalTr) {
		this.totalSujetoTotalTr = totalSujetoTotalTr;
	}

	public long getTotalNotiEst() {
		return totalNotiEst;
	}

	public void setTotalNotiEst(long totalNotiEst) {
		this.totalNotiEst = totalNotiEst;
	}



}
