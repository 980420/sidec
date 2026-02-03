package gehos.configuracion.componenteetl.auxiliares;

import java.io.File;

public class FXML extends Fichero{
	
	private String nombre;
	private byte[] data;
	
	public FXML(){
		super();
	}
	
	public FXML(String dir){
		super(dir);
	}
	
	public FXML(String name, byte[] data){
		super();
		this.nombre = name;
		this.data = data;
	}

	public String getNombre() {
		if(nombre == null){
			String[] aux = dir.split(File.separator);
			return aux[aux.length-1];
		}
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

}
