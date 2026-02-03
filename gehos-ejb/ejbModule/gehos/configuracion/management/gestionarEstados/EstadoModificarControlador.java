package gehos.configuracion.management.gestionarEstados;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Nacion_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("estadoModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class EstadoModificarControlador { 

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private String codigo;
	private String valor;
	private String nacionSelecc;
	private Estado_configuracion estado;
	
	//other functions
	private int error;
	private boolean errorLoadData;
	private Long cid = -1l;
	
	// Methods------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {	
		error = 0;
		this.id = id;
		
		try {
			this.estado = (Estado_configuracion) 
						  entityManager.createQuery("select r from Estado_configuracion r " +
						  							"where r.id =:id and r.nacion.eliminado = false")
						  			   .setParameter("id", id)
						  			   .getSingleResult();
			codigo = estado.getCodigo();
			valor = estado.getValor();
			nacionSelecc = estado.getNacion().getValor();			
			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));			
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
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
			return le;
		}
	}	
	
	@End
	public void end() {		
	}
	
	@Transactional
	public void modificar() {
		error = 0;
		
		try {			
			Nacion_configuracion nacion = (Nacion_configuracion) 
										  entityManager.createQuery("select nacion from Nacion_configuracion nacion " +
										  							"where nacion.valor = :nacionSelecc and nacion.eliminado = false")
										  			   .setParameter("nacionSelecc", this.nacionSelecc)
										  			   .getSingleResult();			
			this.estado.setCodigo(codigo.trim());
			this.estado.setValor(valor.trim());
			this.estado.setNacion(nacion);
			estado.setCid(cid);
			entityManager.merge(estado);
			entityManager.flush();	
			
			this.end();
		}
		catch(Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
		}	
	}

	// Properties-----------------------------------------------------
	public Long getId() {
		return id;
	}
	
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

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
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