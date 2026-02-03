package gehos.ensayo.ensayo_estadisticas.entity;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
public class Sujeto {
	
	private String nombreSujeto;
	private long momentosIniciados;
	private long momentosCompletados;
	private long momentosTotal;
	private long momentosAtrasados;
	private long momentosNoIniciados;
	private long crdIniciadas;
	private long crdCompletadas;
	private long crdTotal;
	private long crdNoIniciadas;
	private long notasSinResolver;
	private long momentosFirmados;
	private long crdFirmada;
	private long momentosMonIniciados;
	private long momentosMonCompletados;
	private long momentosNoIni;
	private long notificaciones;
	private long cantidadReportesExpeditos;
	
	

	public  Sujeto(String nombreSujeto, long momentosIniciados,long momentosCompletados, long momentosTotal, long momentosAtrasados, long momentosNoIniciados, long crdIniciadas, long crdCompletadas, long crdTotal, long momentosFirmados, long crdFirmada,long momentosMonIniciados,long momentosMonCompletados,long notasSinResolver, long crdNoIniciadas) {
		super();
		this.nombreSujeto=nombreSujeto;
		this.momentosIniciados=momentosIniciados;
		this.momentosCompletados=momentosCompletados;
		this.momentosTotal=momentosTotal;
		this.momentosAtrasados=momentosAtrasados;
		this.crdIniciadas=crdIniciadas;
		this.crdCompletadas=crdCompletadas;
		this.crdTotal=crdTotal;
		this.momentosFirmados=momentosFirmados;
		this.crdFirmada= crdFirmada;
		this.momentosMonIniciados= momentosMonIniciados;
		this.momentosMonCompletados = momentosMonCompletados;	
		this.notasSinResolver=notasSinResolver;
		this.crdNoIniciadas=crdNoIniciadas;
		this.momentosNoIniciados=momentosNoIniciados;
		
	}
	public  Sujeto(String nombreSujeto, long momentosIniciados,long momentosCompletados, long notasSinResolver , long momentosNoIni, long notificaciones) {
		super();
		this.nombreSujeto=nombreSujeto;
		this.momentosIniciados=momentosIniciados;
		this.momentosCompletados=momentosCompletados;
		this.notasSinResolver=notasSinResolver;
		this.momentosNoIni=momentosNoIni;
		this.notificaciones=notificaciones;
	}


	
	public Sujeto(String nombreSujeto, long momentosNoIni) {
		super();
		this.nombreSujeto = nombreSujeto;
		this.momentosNoIni = momentosNoIni;
	}
	public String getNombreSujeto() {
		return nombreSujeto;
	}


	public void setNombreSujeto(String nombreSujeto) {
		this.nombreSujeto = nombreSujeto;
	}


	public long getMomentosIniciados() {
		return momentosIniciados;
	}


	public void setMomentosIniciados(long momentosIniciados) {
		this.momentosIniciados = momentosIniciados;
	}


	public long getMomentosCompletados() {
		return momentosCompletados;
	}


	public void setMomentosCompletados(long momentosCompletados) {
		this.momentosCompletados = momentosCompletados;
	}


	public long getMomentosTotal() {
		return momentosTotal;
	}


	public void setMomentosTotal(long momentosTotal) {
		this.momentosTotal = momentosTotal;
	}


	public long getCrdIniciadas() {
		return crdIniciadas;
	}


	public void setCrdIniciadas(long crdIniciadas) {
		this.crdIniciadas = crdIniciadas;
	}


	public long getCrdCompletadas() {
		return crdCompletadas;
	}


	public void setCrdCompletadas(long crdCompletadas) {
		this.crdCompletadas = crdCompletadas;
	}


	public long getCrdTotal() {
		return crdTotal;
	}


	public void setCrdTotal(long crdTotal) {
		this.crdTotal = crdTotal;
	}


	public long getMomentosAtrasados() {
		return momentosAtrasados;
	}


	public void setMomentosAtrasados(long momentosAtrasados) {
		this.momentosAtrasados = momentosAtrasados;
	}


	public long getNotasSinResolver() {
		return notasSinResolver;
	}


	public void setNotasSinResolver(long notasSinResolver) {
		this.notasSinResolver = notasSinResolver;
	}
	
	public long getMomentosFirmados() {
		return momentosFirmados;
	}
	public void setMomentosFirmados(long momentosFirmados) {
		this.momentosFirmados = momentosFirmados;
	}
	public long getCrdFirmada() {
		return crdFirmada;
	}
	public void setCrdFirmada(long crdFirmada) {
		this.crdFirmada = crdFirmada;
	}
	public long getMomentosMonIniciados() {
		return momentosMonIniciados;
	}
	public void setMomentosMonIniciados(long momentosMonIniciados) {
		this.momentosMonIniciados = momentosMonIniciados;
	}
	public long getMomentosMonCompletados() {
		return momentosMonCompletados;
	}
	public void setMomentosMonCompletados(long momentosMonCompletados) {
		this.momentosMonCompletados = momentosMonCompletados;
	}
	
	public long getMomentosNoIniciados() {
		return momentosNoIniciados;
	}
	public void setMomentosNoIniciados(long momentosNoIniciados) {
		this.momentosNoIniciados = momentosNoIniciados;
	}
	public long getCrdNoIniciadas() {
		return crdNoIniciadas;
	}
	public void setCrdNoIniciadas(long crdNoIniciadas) {
		this.crdNoIniciadas = crdNoIniciadas;
	}
	public long getMomentosNoIni() {
		return momentosNoIni;
	}
	public void setMomentosNoIni(long momentosNoIni) {
		this.momentosNoIni = momentosNoIni;
	}
	public long getCantidadReportesExpeditos() {
		return cantidadReportesExpeditos;
	}
	public void setCantidadReportesExpeditos(long cantidadReportesExpeditos) {
		this.cantidadReportesExpeditos = cantidadReportesExpeditos;
	}
	public long getNotificaciones() {
		return notificaciones;
	}
	public void setNotificaciones(long notificaciones) {
		this.notificaciones = notificaciones;
	}




	
	
	





}
