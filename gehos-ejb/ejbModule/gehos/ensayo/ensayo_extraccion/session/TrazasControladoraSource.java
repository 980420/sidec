package gehos.ensayo.ensayo_extraccion.session;
import java.util.List;

public class TrazasControladoraSource {
	String no;
	String usuario;
	String fecha_inicio;
	String hora_inicio;
	String direccion_ip;
	String modulo;
	String accion_realizada;
	String id_accion;
	
	List<TrazasAcc> acciones;
	
	String rol_genera;
	
	public TrazasControladoraSource(String usuario,String no,
			String fecha_inicio, String hora_inicio, String direccion_ip, String modulo,
			String accion_realizada, String rol_genera, String id_accion, List<TrazasAcc> acciones) {
		super();
		this.no = no;
		this.modulo = modulo;
		this.usuario = usuario;
		this.fecha_inicio = fecha_inicio;
		this.hora_inicio = hora_inicio;
		this.direccion_ip = direccion_ip;
		this.accion_realizada = accion_realizada;
		this.id_accion = id_accion;
		this.rol_genera = rol_genera;
		this.acciones = acciones;
						
		}
	
	

	public String getNo() {
		return no;
	}
	public void setNo(String no) {
		this.no = no;
	}
	public String getRol_genera() {
		return rol_genera;
	}
	public void setRol_genera(String rol_genera) {
		this.rol_genera = rol_genera;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public TrazasControladoraSource() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}
	public String getFecha_inicio() {
		return fecha_inicio;
	}
	public void setFecha_inicio(String fecha_inicio) {
		this.fecha_inicio = fecha_inicio;
	}
	public String getHora_inicio() {
		return hora_inicio;
	}
	public void setHora_inicio(String hora_inicio) {
		this.hora_inicio = hora_inicio;
	}
	public String getDireccion_ip() {
		return direccion_ip;
	}
	public void setDireccion_ip(String direccion_ip) {
		this.direccion_ip = direccion_ip;
	}
	public String getAccion_realizada() {
		return accion_realizada;
	}
	public void setAccion_realizada(String accion_realizada) {
		this.accion_realizada = accion_realizada;
	}

	
	public String getId_accion() {
		return id_accion;
	}

	public void setId_accion(String id_accion) {
		this.id_accion = id_accion;
	}



	public List<TrazasAcc> getAcciones() {
		return acciones;
	}



	public void setAcciones(List<TrazasAcc> acciones) {
		this.acciones = acciones;
	}

	
	
	
}
