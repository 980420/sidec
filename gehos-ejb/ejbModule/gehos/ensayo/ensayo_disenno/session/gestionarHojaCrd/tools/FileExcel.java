package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.tools;

public class FileExcel extends EnsFile {

	private String nombre;

	public FileExcel() {
		super();
	}

	public FileExcel(String dir) {
		super(dir);
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

}
