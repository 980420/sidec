package gehos.configuracion.management.gestionarEstados;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Nacion_configuracion;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("estadoCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class EstadoCrearControlador {	 
	
	@In IBitacora bitacora;
	@In FacesMessages facesMessages;	
	@In EntityManager entityManager;	
	
	private String codigo;
	private String valor;
	private String nacionSelecc;
	private Estado_configuracion estado;
		
	// other functionalities
	private Long cid = -1l;
	private int error;	
		
	// Methods----------------------------------------------------	
	@Begin(flushMode=FlushModeType.MANUAL, join = true)
	public void begin(){
		try{
			error = 0;
			
			codigo = "";
			valor = "";
			nacionSelecc = "";
			estado = new Estado_configuracion();
			
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
				estado.setCid(cid);
			}
		}catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}	
	
	// return a contry list
	@SuppressWarnings("unchecked")
	public List<String> listNaciones(){
		List<String> le = new ArrayList<String>();
		try {
			le = entityManager.createQuery("select n.valor from Nacion_configuracion n " +
										   "where n.eliminado = false " +
										    "order by n.valor")
							  .getResultList();			
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			return le;
		}
	}	
	
	@End
	public void end() {
	}
	
	@Transactional
	public void crear() {
		error = 0;
		
		try {
		Nacion_configuracion nacion = (Nacion_configuracion) 
									  entityManager.createQuery("select n from Nacion_configuracion n " +
									  							"where n.valor=:nacionSelecc " +
									  							"and n.eliminado = false")
									  							.setParameter("nacionSelecc", this.nacionSelecc)
									  							.getSingleResult();					
			estado.setCid(cid);
			estado.setCodigo(codigo.trim());
			estado.setValor(valor.trim());
			estado.setNacion(nacion);
			estado.setEliminado(false);
			entityManager.persist(estado);
			entityManager.flush();	
			this.end();
		}			
		catch (Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
		}
	}
	
	// Properties--------------------------------------------------	
	public Estado_configuracion getEstado() {
		return estado;
	}

	public void setEstado(Estado_configuracion estado) {
		this.estado = estado;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	public String getNacionSelecc() {
		return nacionSelecc;
	}
	public void setNacionSelecc(String nacionSelecc) {
		this.nacionSelecc = nacionSelecc;
	}
	
	public String getValor() {
		return valor;
	}
	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}