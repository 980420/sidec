package gehos.configuracion.management.gestionarServiciosClinicos;

import java.util.ArrayList;
import java.util.List;

public class SubCartegorias {

	private String padre = "";
	private int pos = 0;	
	private List<String> categorias = new ArrayList<String>();
	
	public String getPadre() {
		return padre;
	}
	public void setPadre(String padre) {
		this.padre = padre;
	}
	public List<String> getCategorias() {
		return categorias;
	}
	public void setCategorias(List<String> categorias) {
		this.categorias = categorias;
	}
	
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	
	
}
