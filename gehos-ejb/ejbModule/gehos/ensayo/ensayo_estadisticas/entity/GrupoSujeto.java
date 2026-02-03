package gehos.ensayo.ensayo_estadisticas.entity;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;
import org.omg.CosConcurrencyControl.lock_modeHelper;

@Scope(ScopeType.CONVERSATION)
public class GrupoSujeto {
	
	private String nombreGrupo;
	
	private long sujEvaluados;
	private long sujNoIncluidos;
	private long sujIncluidos;
	private long sujMalIncluidos;
	private long sujEvaluadoT;
	private long sujTratamiento;
	private long sujSeguimiento;
	private long sujInterrumpidos;
	private long totalMomentosIniciados;
	private	long totalMomentosCompletados;
	private	long totalMomentosTotal;
	private	long totalMomentosAtrasados;
	private	long totalMomentosNoIniciados;
	private	long totalCrdIniciadas;
	private	long totalCrdCompletadas;
	private	long totalCrdTotal;
	private long totalMomentosFirmados;
	private long totalCrdFirmada;
	private long totalCrdNoIniciadas;
	private long totalIn;
	private long totalTr;
	private long totalNoti;
	
	private	long totalMomentosMonIniciados;
	private	long totalMomentosMonCompletados;
	private	long totalNotasSinResolver;
	private long totalMomentosNoIni;
	private List<Sujeto> sujetos;
	
	public GrupoSujeto(String nombreGrupo, List<Sujeto> sujetos, long totalMomentosIniciados, long totalMomentosCompletados, long totalMomentosTotal, long totalMomentosAtrasados, long totalCrdIniciadas, long totalCrdCompletadas, long totalCrdTotal, long totalMomentosFirmados, long totalCrdFirmada,long totalMomentosMonIniciados,long totalMomentosMonCompletados,long totalNotasSinResolver, long totalCrdNoIniciadas, long totalMomentosNoIniciados)
	{
		super();
		this.nombreGrupo=nombreGrupo;
		this.sujetos=sujetos;
		this.totalMomentosIniciados=totalMomentosIniciados;
		this.totalMomentosCompletados=totalMomentosCompletados;
		this.totalMomentosTotal=totalMomentosTotal;
		this.totalCrdIniciadas=totalCrdIniciadas;
		this.totalCrdCompletadas=totalCrdCompletadas;
		this.totalCrdTotal=totalCrdTotal;
		this.totalMomentosFirmados=totalMomentosFirmados;
		this.totalCrdFirmada=totalCrdFirmada;
		this.totalMomentosMonIniciados = totalMomentosMonIniciados;
		this.totalMomentosMonCompletados = totalMomentosMonCompletados;
		this.totalNotasSinResolver = totalNotasSinResolver;
		this.totalCrdNoIniciadas = totalCrdNoIniciadas;
		this.totalMomentosNoIniciados = totalMomentosNoIniciados;
	}
	
	public GrupoSujeto(String nombreGrupo, List<Sujeto> sujetos,long totalMomentosIniciados, long totalMomentosCompletados, long totalNotasSinResolver, long totalMomentosNoIni, long totalNoti)
	{
		super();
		this.nombreGrupo=nombreGrupo;
		this.sujetos=sujetos;
		this.totalMomentosIniciados=totalMomentosIniciados;
		this.totalMomentosCompletados=totalMomentosCompletados;
		this.totalNotasSinResolver=totalNotasSinResolver;
		this.totalMomentosNoIni=totalMomentosNoIni;
		this.totalNoti=totalNoti;
		
	}

	

	public GrupoSujeto(String nombreGrupo, 
			List<Sujeto> sujetos,long totalMomentosNoIniciados) {
		super();
		this.nombreGrupo = nombreGrupo;
		this.totalMomentosNoIni = totalMomentosNoIniciados;
		this.sujetos = sujetos;
	}

	public  GrupoSujeto(String nombreGrupo, long sujEvaluados,long sujNoIncluidos, long sujIncluidos, long sujMalIncluidos, long sujInterrumpidos, long sujEvaluadoT, long sujTratamiento, long sujSeguimiento, long totalIn, long totalTr) {
		super();
		this.nombreGrupo=nombreGrupo;
		this.sujEvaluados=sujEvaluados;
		this.sujNoIncluidos=sujNoIncluidos;
		this.sujIncluidos=sujIncluidos;
		this.sujMalIncluidos=sujMalIncluidos;
		this.sujInterrumpidos=sujInterrumpidos;
		this.sujEvaluadoT=sujEvaluadoT;
		this.sujTratamiento=sujTratamiento;
		this.sujSeguimiento=sujSeguimiento;
		this.totalIn=totalIn;
		this.totalTr=totalTr;
	
	}
	
	

	public String getNombreGrupo() {
		return nombreGrupo;
	}



	public void setNombreGrupo(String nombreGrupo) {
		this.nombreGrupo = nombreGrupo;
	}



	public long getSujEvaluados() {
		return sujEvaluados;
	}

	public void setSujEvaluados(long sujEvaluados) {
		this.sujEvaluados = sujEvaluados;
	}

	public long getSujNoIncluidos() {
		return sujNoIncluidos;
	}

	public void setSujNoIncluidos(long sujNoIncluidos) {
		this.sujNoIncluidos = sujNoIncluidos;
	}

	public long getSujIncluidos() {
		return sujIncluidos;
	}

	public void setSujIncluidos(long sujIncluidos) {
		this.sujIncluidos = sujIncluidos;
	}

	public long getSujMalIncluidos() {
		return sujMalIncluidos;
	}

	public void setSujMalIncluidos(long sujMalIncluidos) {
		this.sujMalIncluidos = sujMalIncluidos;
	}

	public long getSujInterrumpidos() {
		return sujInterrumpidos;
	}

	public void setSujInterrumpidos(long sujInterrumpidos) {
		this.sujInterrumpidos = sujInterrumpidos;
	}

	public List<Sujeto> getSujetos() {
		return sujetos;
	}

	public void setSujetos(List<Sujeto> sujetos) {
		this.sujetos = sujetos;
	}

	public long getTotalMomentosIniciados() {
		return totalMomentosIniciados;
	}

	public void setTotalMomentosIniciados(long totalMomentosIniciados) {
		this.totalMomentosIniciados = totalMomentosIniciados;
	}

	public long getTotalMomentosCompletados() {
		return totalMomentosCompletados;
	}

	public void setTotalMomentosCompletados(long totalMomentosCompletados) {
		this.totalMomentosCompletados = totalMomentosCompletados;
	}

	public long getTotalMomentosTotal() {
		return totalMomentosTotal;
	}

	public void setTotalMomentosTotal(long totalMomentosTotal) {
		this.totalMomentosTotal = totalMomentosTotal;
	}

	public long getTotalMomentosAtrasados() {
		return totalMomentosAtrasados;
	}

	public void setTotalMomentosAtrasados(long totalMomentosAtrasados) {
		this.totalMomentosAtrasados = totalMomentosAtrasados;
	}

	public long getTotalCrdIniciadas() {
		return totalCrdIniciadas;
	}

	public void setTotalCrdIniciadas(long totalCrdIniciadas) {
		this.totalCrdIniciadas = totalCrdIniciadas;
	}

	public long getTotalCrdCompletadas() {
		return totalCrdCompletadas;
	}

	public void setTotalCrdCompletadas(long totalCrdCompletadas) {
		this.totalCrdCompletadas = totalCrdCompletadas;
	}

	public long getTotalCrdTotal() {
		return totalCrdTotal;
	}

	public void setTotalCrdTotal(long totalCrdTotal) {
		this.totalCrdTotal = totalCrdTotal;
	}

	public long getTotalNotasSinResolver() {
		return totalNotasSinResolver;
	}

	public void setTotalNotasSinResolver(long totalNotasSinResolver) {
		this.totalNotasSinResolver = totalNotasSinResolver;
	}
	
	public long getTotalMomentosFirmados() {
		return totalMomentosFirmados;
	}

	public void setTotalMomentosFirmados(long totalMomentosFirmados) {
		this.totalMomentosFirmados = totalMomentosFirmados;
	}

	public long getTotalCrdFirmada() {
		return totalCrdFirmada;
	}

	public void setTotalCrdFirmada(long totalCrdFirmada) {
		this.totalCrdFirmada = totalCrdFirmada;
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
	
	public long getTotalMomentosNoIniciados() {
		return totalMomentosNoIniciados;
	}

	public void setTotalMomentosNoIniciados(long totalMomentosNoIniciados) {
		this.totalMomentosNoIniciados = totalMomentosNoIniciados;
	}

	public long getTotalCrdNoIniciadas() {
		return totalCrdNoIniciadas;
	}

	public void setTotalCrdNoIniciadas(long totalCrdNoIniciadas) {
		this.totalCrdNoIniciadas = totalCrdNoIniciadas;
	}

	public long getSujEvaluadoT() {
		return sujEvaluadoT;
	}

	public void setSujEvaluadoT(long sujEvaluadoT) {
		this.sujEvaluadoT = sujEvaluadoT;
	}

	public long getSujTratamiento() {
		return sujTratamiento;
	}

	public void setSujTratamiento(long sujTratamiento) {
		this.sujTratamiento = sujTratamiento;
	}

	public long getSujSeguimiento() {
		return sujSeguimiento;
	}

	public void setSujSeguimiento(long sujSeguimiento) {
		this.sujSeguimiento = sujSeguimiento;
	}

	public long getTotalMomentosNoIni() {
		return totalMomentosNoIni;
	}

	public void setTotalMomentosNoIni(long totalMomentosNoIni) {
		this.totalMomentosNoIni = totalMomentosNoIni;
	}

	public long getTotalIn() {
		return totalIn;
	}

	public void setTotalIn(long totalIn) {
		this.totalIn = totalIn;
	}

	public long getTotalTr() {
		return totalTr;
	}

	public void setTotalTr(long totalTr) {
		this.totalTr = totalTr;
	}

	public long getTotalNoti() {
		return totalNoti;
	}

	public void setTotalNoti(long totalNoti) {
		this.totalNoti = totalNoti;
	}

}
