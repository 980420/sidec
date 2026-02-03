package gehos.ensayo.ensayo_disenno.componenteetl.controladoras;


import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.TransformacionDisenno;

import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("buscarTransformacionDisenno")
@Scope(ScopeType.SESSION)
public class BuscarTransformacionDisenno {

	@In(create = true)
	TransformacionManagerDisenno transfManager;

	private String nombreTr;
	private String ordenado;
	private TransformacionDisenno seleccionada;
	private String resultado;
	private int pagina;
	
	public BuscarTransformacionDisenno() {
		ordenado = "no";
		pagina = 1;
	}
	
	public int getPagina() {
		return pagina;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
	}

	public void setSeleccionada(TransformacionDisenno seleccionada){
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
		addLog("La transformaci\u00F3n '"+seleccionada.getNombre()+"' ha sido eliminada satisfactoriamente.", true);
	}
	
	public void ordenar(String orden){
		transfManager.ordenar(orden);
		ordenado = orden;
	}
	
	public String getOrdenado(){
		return ordenado;
	}
	
	public List<TransformacionDisenno> getListaTransf(){
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
