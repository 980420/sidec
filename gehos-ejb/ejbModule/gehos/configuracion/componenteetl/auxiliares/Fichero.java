package gehos.configuracion.componenteetl.auxiliares;

public class Fichero {
	
	protected String dir;
	
	public Fichero(){
		
	}
	
	public Fichero(String dir){
		this.dir = dir;
	}	
	
	public String getDir() {
		return dir;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}
}
