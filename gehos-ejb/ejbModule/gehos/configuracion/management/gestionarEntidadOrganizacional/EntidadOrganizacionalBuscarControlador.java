package gehos.configuracion.management.gestionarEntidadOrganizacional;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.Arrays;

import javax.persistence.EntityManager;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("entidadOrganizacionalBuscarControlador")
public class EntidadOrganizacionalBuscarControlador extends EntityQuery<EntidadOrganizacional_configuracion> {

	private static final String EJBQL = "select entidadOrganizacional from EntidadOrganizacional_configuracion entidadOrganizacional";
	private static final String[] RESTRICTIONS = {"lower(entidadOrganizacional.valor) like concat(lower(#{entidadOrganizacionalBuscarControlador.entidadOrganizacional.valor.trim()}),'%')" };
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	// search 
	private String valor;
	
	// other fuctionalities
	private Long idEntidadOrganizacionalEliminar;
	private boolean state = true;	
	private Long cid = -1l;

	private EntidadOrganizacional_configuracion entidadOrganizacional = new EntidadOrganizacional_configuracion();	

	public EntidadOrganizacionalBuscarControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("entidadOrganizacional.id desc");
	}

	//Methods-----------------------------------------------------------
	public void clean() {	
		valor = "";
		state = true;
		setOrder("entidadOrganizacional.id desc");		
		entidadOrganizacional = new EntidadOrganizacional_configuracion();
		setFirstResult(0);
	}
	
	// change state of search simpleTogglePanel
	public void changeSimpleTogglePanelState() {
		this.state = !state;
	}
	
	// search 
	public void buscar() {
		entidadOrganizacional.setValor(valor);
		setFirstResult(0);
	}
		
	// select item to removal
	public void seleccionar(Long id) {
		this.idEntidadOrganizacionalEliminar = id;
	}
	
	// remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion("Eliminando entidad organizacional");
			
			EntidadOrganizacional_configuracion ent = (EntidadOrganizacional_configuracion) 
													  entityManager.createQuery("select e from EntidadOrganizacional_configuracion e " +
															  					"where e.id = :id")
															  	   .setParameter("id", idEntidadOrganizacionalEliminar)
															  	   .getSingleResult();	
			
			entityManager.remove(ent);
			entityManager.flush();

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());
			
			this.refresh();

		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "msjEliminar");
		}
	}

	//change visibility
	public void cambiarVisibilidad(Long id) {
		try{
			cid = bitacora.registrarInicioDeAccion("Cambiando visibilidad entidad organizacional");
			
			EntidadOrganizacional_configuracion ent = (EntidadOrganizacional_configuracion) 
			  										  entityManager.createQuery("select e from EntidadOrganizacional_configuracion e " +
			  										  							"where e.id = :id")
			  										  			   .setParameter("id", id)
			  										  			   .getSingleResult();				
			
			if(ent.getEliminado() == null || ent.getEliminado() == false)
				ent.setEliminado(true);
			else
				ent.setEliminado(false);
			ent.setCid(cid);
			
			entityManager.persist(ent);
			entityManager.flush();
			
			this.refresh();
		} catch(Exception e){
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "errorInesperado");
		}
	}
	
	// Properties-------------------------------------------------------r
	public EntidadOrganizacional_configuracion getEntidadOrganizacional() {
		return entidadOrganizacional;
	}

	public void setEntidadOrganizacional(EntidadOrganizacional_configuracion entidadOrganizacional) {
		this.entidadOrganizacional =entidadOrganizacional;
	}

	public Long getIdEntidadOrganizacionalEliminar() {
		return idEntidadOrganizacionalEliminar;
	}

	public void setIdEntidadOrganizacionalEliminar(Long idEntidadOrganizacionalEliminar) {
		this.idEntidadOrganizacionalEliminar = idEntidadOrganizacionalEliminar;
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
}
