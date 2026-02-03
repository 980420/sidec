package gehos.comun.procesos.treebuilders.model;

public class Proceso {

	private String nombre;
	private String modulo;

	public Proceso(String nombre, String modulo) {
		this.nombre = nombre;
		this.modulo = modulo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getModulo() {
		return modulo;
	}

	public void setModulo(String modulo) {
		this.modulo = modulo;
	}

}
