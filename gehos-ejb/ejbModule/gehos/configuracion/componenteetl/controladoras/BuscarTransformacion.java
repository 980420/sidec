package gehos.configuracion.componenteetl.controladoras;

import gehos.configuracion.componenteetl.auxiliares.Transformacion;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("buscarTransformacion")
@Scope(ScopeType.SESSION)
public class BuscarTransformacion {

	@In(create = true)
	TransformacionManager transfManager;

	private String nombreTr;
	private String ordenado;
	private Transformacion seleccionada;
	private String resultado;
	private int pagina;
	
	public BuscarTransformacion() {
		ordenado = "no";
		pagina = 1;
	}
	
	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public void setSeleccionada(Transformacion seleccionada){
		this.seleccionada = seleccionada;
	}
	
	public void setNombreTr(String nombreTr) {
		this.nombreTr = nombreTr;
	}
	
	public String getNombreTr() {
		return nombreTr;
	}

	public String getResultado() {
		return resultado;
	}
	
	public void cargaInicial(){
		try {
			transfManager.cargaInicial();
		} catch (Exception e) {
			addLog(e.getMessage(), true);
		}
	}
	
	public void eliminarTransformacion() {
		transfManager.eliminar(seleccionada);
		addLog("La transformación '"+seleccionada.getNombre()+"' ha sido eliminada satisfactoriamente.", true);
	}
	
	public void ordenar(String orden){
		transfManager.ordenar(orden);
		ordenado = orden;
	}
	
	public String getOrdenado(){
		return ordenado;
	}
	
	public List<Transformacion> getListaTransf(){
		return transfManager.getListadetransfs();
	}
	
	public int getCantidadTransf(){
		return transfManager.longitud();
	}
	
	public void buscar(){
		if(!nombreTr.equals("")){
			transfManager.buscar(nombreTr);
			setPagina(1);
		}
		else {
			cancelar();
		}
	}
	
	public void cancelar(){
		transfManager.cancelar();
		nombreTr = "";
	}

	private void addLog(String log, Boolean time) {
		transfManager.addLog(log, time);
	}
}
