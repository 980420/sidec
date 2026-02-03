package gehos.pki.servidores.ldap;

import java.util.ArrayList;
import java.util.List;
import gehos.bitacora.session.traces.IBitacora;
import gehos.pki.entity.Ldap;
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

@Name("crearLDAPControlador")
@Scope(ScopeType.CONVERSATION)
public class CrearLDAPControlador {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	private String hostLDAP, domain, description;
	private String active_string;
	private Ldap ldap = new Ldap();

	// other functions
	private int error;

	// Methods------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public String begin() {
		error = 0;

		try {
			this.active_string = "";
			this.hostLDAP = "";
			this.domain = "";
			this.description = "";
			this.ldap = new Ldap();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}
		return "go";
	}

	public boolean isActive() {
		return getActive_string().toLowerCase().trim().equals(SeamResourceBundle.getBundle().getString("si").toLowerCase()
				.trim());
	}

	@End
	public void end() {
	}

	public List<String> activoList() {
		List<String> list = new ArrayList<String>(3);
		list.add(SeamResourceBundle.getBundle().getString("seleccione"));
		list.add(SeamResourceBundle.getBundle().getString("si"));
		list.add(SeamResourceBundle.getBundle().getString("no"));
		return list;
	}

	@Transactional
	public void crear() {
		error = 0;
		try {
			this.ldap.setHost(hostLDAP);
			this.ldap.setDomain(domain);
			this.ldap.setActive(isActive());
			this.ldap.setDescription(description);
			entityManager.persist(ldap);
			entityManager.flush();
			this.end();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message",
					Severity.ERROR, "errorInesperado");
		}
	}

	// Properties--------------------------------------------------
	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
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

	public String getActive_string() {
		return active_string;
	}

	public void setActive_string(String active_string) {
		this.active_string = active_string;
	}
}
