package gehos.configuracion.management.gestionarEstados;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION) 
@Name("estadoBuscarControlador")
public class EstadoBuscarControlador extends EntityQuery<Estado_configuracion> {

	private static final String EJBQL = "select estado from Estado_configuracion estado where estado.nacion.eliminado = false";
	private static final String[] RESTRICTIONS = {"lower(estado.valor) like concat(lower(#{estadoBuscarControlador.estado.valor.trim()}),'%')",
												  "lower(estado.codigo) like concat(lower(#{estadoBuscarControlador.estado.codigo.trim()}),'%')",
												  "lower(estado.nacion.valor) like concat(lower(#{estadoBuscarControlador.estado.nacion.valor.trim()}),'%')"};
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search 
	private String codigo = "";
	private String valor = "";
	private String nacion = "";	
	
	// other fuctionalities
	private Long idEstadoEliminar;
	private boolean state = true;	
	private Long cid = -1l;

	private Estado_configuracion estado = new Estado_configuracion();	

	public EstadoBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("estado.id desc");
	}

	//Methods-----------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		this.refresh();
		
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// search 
	public void buscar() {
		Nacion_configuracion n = new Nacion_configuracion();
		n.setValor(nacion);
		
		estado.setCodigo(codigo);
		estado.setValor(valor);
		estado.setNacion(n);
		setFirstResult(0);
	}
		
	// select item to removal
	public void seleccionar(Long id) {
		this.idEstadoEliminar = id;
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
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		}
	}	
	
	// remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			Estado_configuracion estado = (Estado_configuracion) 
			  							   entityManager.createQuery("select e from Estado_configuracion e " +
			  							   							 "where e.id =:id")
			  							   				.setParameter("id", idEstadoEliminar)
			  							   				.getSingleResult();		
			
			entityManager.remove (estado);
			entityManager.flush();	
			
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	//change visibility
	public void cambiarVisibilidad(Long id) {
		try{
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
			
			Estado_configuracion estado = (Estado_configuracion) 
			   							  entityManager.createQuery("select e from Estado_configuracion e " +
			   							  							"where e.id =:id")
			   							  			   .setParameter("id", id)
			   							  			   .getSingleResult();		
			
			if(estado.getEliminado() == null || estado.getEliminado() == false)
				estado.setEliminado(true);
			else
				estado.setEliminado(false);	
			
			estado.setCid(cid);
			
			entityManager.persist(estado);
			entityManager.flush();
			
			this.refresh();
		}catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
	
	// Properties-------------------------------------------------------
	public void setNacion(String nacion) {
		if(nacion.equals(SeamResourceBundle.getBundle().getString("seleccione")))
			this.nacion = "";
		else
			this.nacion = nacion;
	}
	
	public Estado_configuracion getEstado() {
		return estado;
	}

	public void setEstado(Estado_configuracion estado) {
		this.estado = estado;
	}

	public Long getIdEstadoEliminar() {
		return idEstadoEliminar;
	}

	public void setIdEstadoEliminar(Long idEstadoEliminar) {
		this.idEstadoEliminar = idEstadoEliminar;
	}

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getNacion() {
		return nacion;
	}	

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
}