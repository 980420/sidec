package gehos.configuracion.management.gestionarNaciones;

import java.util.Arrays;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

@SuppressWarnings("serial") 
@Scope(ScopeType.CONVERSATION)
@Name("nacionBuscarControlador")
public class NacionBuscarControlador extends EntityQuery<Nacion_configuracion> {

	private static final String EJBQL = "select nacion from Nacion_configuracion nacion";
			
	private static final String[] RESTRICTIONS = { 
		"lower(nacion.codigo) like concat(lower(#{nacionBuscarControlador.nacion.codigo.trim()}),'%')",
		"lower(nacion.valor) like concat(lower(#{nacionBuscarControlador.nacion.valor.trim()}),'%')", 
		"lower(nacion.nacionalidad) like concat(lower(#{nacionBuscarControlador.nacion.nacionalidad.trim()}),'%')" };
	
	@In EntityManager entityManager;	
	@In(create = true) FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search criteria
	private String codigo = "";
	private String valor = "";
	private String nacionalidad = "";
	
	private Nacion_configuracion nacion = new Nacion_configuracion();
	
	// other functions
	private Long nacionId = -1l;
	private boolean state = true;
	private Long cid = -1l;	

	public NacionBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("nacion.id desc");
	}	
		
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin(){
		this.refresh();
		
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults()); 	
	}
	
	// search
	public void buscar(){	
		nacion.setCodigo(codigo);
		nacion.setValor(valor);
		nacion.setNacionalidad(nacionalidad);
		setFirstResult(0);
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// seleccionar to removal
	public void seleccionar(Long idcama){
		this.nacionId = idcama;
	}
	
	// remove	
	public void eliminar(){
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
			
			Nacion_configuracion nacion = (Nacion_configuracion) 
										  entityManager.createQuery("select n from Nacion_configuracion n where n.id =:id")
										               .setParameter("id", nacionId)
										               .getSingleResult();			
			
			entityManager.remove(nacion);
			entityManager.flush();			
		
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}
	
	// change visibility
	@Transactional
	public void cambiarVisibilidad(Long id){		
		try{
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitVisibilidad"));
		
			Nacion_configuracion aux = (Nacion_configuracion) 
									   entityManager.createQuery("select n from Nacion_configuracion n " +
																 "where n.id =:id")
												 	.setParameter("id", id)
												 	.getSingleResult();
			
			
			aux.setCid(cid);			
			if(aux.getEliminado() == null || aux.getEliminado() == false)
				aux.setEliminado(true);
			else
				aux.setEliminado(false);
			entityManager.persist(aux);
			entityManager.flush();
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
	}
		
	// Properties ------------------------------------------------
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

	public String getNacionalidad() {
		return nacionalidad;
	}

	public void setNacionalidad(String nacionalidad) {
		this.nacionalidad = nacionalidad;
	}

	public Nacion_configuracion getNacion() {
		return nacion;
	}

	public void setNacion(Nacion_configuracion nacion) {
		this.nacion = nacion;
	}

	public Long getNacionId() {
		return nacionId;
	}

	public void setNacionId(Long nacionId) {
		this.nacionId = nacionId;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}	
}