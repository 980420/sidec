package gehos.ensayo.ensayo_disenno.componenteetl.auxiliares;



public class FExcelDisenno extends FicheroDisenno{
	
	private String nombre;
	
	public FExcelDisenno(){
		super();
	}
	
	public FExcelDisenno(String dir){
		super(dir);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	

}
