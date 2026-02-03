package gehos.pki.servidores.ldap;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;
import gehos.pki.entity.Ldap;

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
import org.jboss.seam.transaction.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("buscarLDAPControlador")
public class BuscarLDAPControlador extends EntityQuery<Ldap> {

	private static final String EJBQL = "select ldap_host from Ldap ldap_host";

	private static final String[] RESTRICTIONS = {
			"lower(ldap_host.host) like concat('%',lower(#{buscarLDAPControlador.hostLDAP.trim()}),'%')"
			 };

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	
	// search criteria
	private String hostLDAP= "", domain, activo = "";
	private Ldap ldap = new Ldap();	
	

	
	// other functiones 
	private int idEliminar = -1;
	private boolean state = true;//state of singletuglepanel
	private Parameters parametros = new Parameters();  //codec and decodec parameters

	private int error = 0;
	

	public BuscarLDAPControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("ldap_host.id desc");
	}
	
	public List<String> activoList(){
		List<String> list = new ArrayList<String>(3);
		list.add(SeamResourceBundle.getBundle().getString("seleccione"));
		list.add(SeamResourceBundle.getBundle().getString("si"));
		list.add(SeamResourceBundle.getBundle().getString("no"));
		return list;
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {		
//		this.refresh();
		
//		if (getResultList().size() == 0 && getFirstResult() != 0)
//			setFirstResult(getFirstResult() - getMaxResults());
	}

	// change the state of singleTugglePanel (open/close)
	public void cambiarEstadoSimpleTugglePanel() {
		this.state = !state;
	}
	
	public boolean active (){		
		return SeamResourceBundle.getBundle().getString("si").toLowerCase().trim().equals("sí");
	}

	// search
	public void buscar() {		
		this.ldap = new Ldap();
		ldap.setHost(hostLDAP);
		ldap.setActive(active());			
		setFirstResult(0);		
	}

	// select item for removal
	public void seleccionarEliminar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			Ldap ldap = (Ldap) 
				  entityManager.createQuery("select t from Ldap t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("camaSearch", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
	}
	
	// selecciona serv para modificar
	
	public void seleccionarModificar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			Ldap ldap = (Ldap) 
				  entityManager.createQuery("select t from Ldap t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}
				
	}
	
	public void seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error   = 0;
			@SuppressWarnings("unused")
			Ldap ldap = (Ldap) 
				  entityManager.createQuery("select t from Ldap t where t.id =:id")
			          .setParameter("id", idEliminar)
			          .getSingleResult();
			
		} catch (NoResultException e) {			
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			error  = 1;
			Transaction.instance().rollback();
		}	
	}

	//remove item
	public void eliminar() {
		try {
			bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
						
			Ldap host_ldap = (Ldap) 
					   						  entityManager.createQuery("select t from Ldap t where t.id =:id")
					   				          .setParameter("id", idEliminar)
					   				          .getSingleResult();			
			entityManager.remove(host_ldap);
			entityManager.flush();
			

		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("form", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	
	
	//Properties--------------------------------------------------------
	

	public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public int getIdEliminar() {
		return idEliminar;
	}

	public void setIdEliminar(int idEliminar) {
		this.idEliminar = idEliminar;
	}

	public Parameters getParametros() {
		return parametros;
	}

	public void setParametros(Parameters parametros) {
		this.parametros = parametros;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getHostLDAP() {
		return hostLDAP;
	}

	public void setHostLDAP(String hostLDAP) {
		this.hostLDAP = hostLDAP;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getActivo() {
		return activo;
	}

	public void setActivo(String activo) {
		this.activo = activo;
		
		if(this.activo.equals(SeamResourceBundle.getBundle().getString("si"))){
			this.setEjbql(EJBQL + " where ldap_host.active = true");
		}
			
		else if(this.activo.equals(SeamResourceBundle.getBundle().getString("no"))){
			this.setEjbql(EJBQL + " where ldap_host.active = false");
		}
		else this.setEjbql(EJBQL);
	}
}
