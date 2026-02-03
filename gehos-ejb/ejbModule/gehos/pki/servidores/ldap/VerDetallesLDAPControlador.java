package gehos.pki.servidores.ldap;

import gehos.bitacora.session.traces.IBitacora;
import gehos.pki.entity.Ldap;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("verDetallesLDAPControlador")
public class VerDetallesLDAPControlador {
	
	@In IBitacora bitacora;
	@In EntityManager entityManager;	
	@In FacesMessages facesMessages;
	
	private int id;
	private Ldap ldap = new Ldap();
	
	// other functions
	private int error;	
	private String from;
	
	//Methods-----------------------------------------------------------	
	public void setId(int id) {		
		try {	
			error = 0;	
			this.id = id;		
			this.ldap = new Ldap();
			
			this.ldap = (Ldap) entityManager
							.createQuery("select t from Ldap t where t.id =:id")
							.setParameter("id", this.id).getSingleResult();				
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
		
	public void eliminar(){
		
		try {
			error = 0;			
			entityManager.remove(this.ldap);
			entityManager.flush();
			
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "enuso");
			e.printStackTrace();
		}
	}

	//Properties----------------------------------------------------
	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}



	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public Ldap getLdap() {
		return ldap;
	}

	public void setLdap(Ldap ldap) {
		this.ldap = ldap;
	}

	public int getId() {
		return id;
	}
}
