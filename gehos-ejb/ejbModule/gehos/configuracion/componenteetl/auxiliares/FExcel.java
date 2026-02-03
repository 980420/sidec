package gehos.configuracion.componenteetl.auxiliares;


public class FExcel extends Fichero{
	
	private String nombre;
	
	public FExcel(){
		super();
	}
	
	public FExcel(String dir){
		super(dir);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}
