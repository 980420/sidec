package gehos.pki.servidores.ldap;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.pki.entity.Ldap;

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
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarLDAPControlador")
@Scope(ScopeType.CONVERSATION)
public class ModificarLDAPControlador {

	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	private String hostLDAP, domain, description;
	
	private Integer id;
	private String active_string;
	private Ldap ldap = new Ldap();

	private int error;



	@End
	public void end() {
	}
	
	
	
	
	
	@Transactional
	public String modificar() {

		try {
			this.ldap.setHost(hostLDAP);
			this.ldap.setDomain(domain);
			this.ldap.setActive(isActive());
			this.ldap.setDescription(description);

			entityManager.merge(ldap);
			entityManager.flush();

		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
			e.printStackTrace();

		}

		return "editar";
	}

	// Properties--------------------------------------------------
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Integer getId() {
		return id;
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Integer id) {

	

			try {
				error = 0;				
				this.id = id;
				this.ldap = entityManager.find(Ldap.class, this.id);
				this.hostLDAP = this.ldap.getHost();
				this.domain = this.ldap.getDomain();
				if (this.ldap.getActive()) {
					this.active_string = SeamResourceBundle.getBundle().getString("si");
					
				} else {
					this.active_string = SeamResourceBundle.getBundle().getString("no");
					
					
					
				}
				
				this.description = this.ldap.getDescription();

			} catch (NoResultException e) {
				error = 1;
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "eliminado");
				e.printStackTrace();
			} catch (Exception e) {
				error = 1;
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "errorInesperado");
				e.printStackTrace();
			}

		

	}
	
	public List<String> activoList(){
		List<String> list = new ArrayList<String>(3);
		list.add(SeamResourceBundle.getBundle().getString("seleccione"));
		list.add(SeamResourceBundle.getBundle().getString("si"));
		list.add(SeamResourceBundle.getBundle().getString("no"));
		return list;
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	

	public Ldap getLdap() {
		return ldap;
	}

	public void setLdap(Ldap ldap) {
		this.ldap = ldap;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getActive_string() {
		return active_string;
	}

	public void setActive_string(String active_string) {
		this.active_string = active_string;
	}
	
	public boolean isActive() {
		return getActive_string().toLowerCase().trim().equals(SeamResourceBundle.getBundle().getString("si").toLowerCase()
				.trim());
	}
}
