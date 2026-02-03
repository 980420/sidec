package gehos.pki.servidores.proxy;

import gehos.pki.entity.Proxy_pki;

import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("verDetallesProxyControlador")
public class VerDetallesProxyControlador {
	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;
	
	private Proxy_pki proxy = null;
	private int id;
	private String from;
	private boolean error = false;
	
	public void eliminar(){
		try {
			this.proxy = entityManager.find(Proxy_pki.class, this.id);	
			this.id = -1;
			entityManager.remove(this.proxy);
			entityManager.flush();
			 			
		
		} catch (Exception e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString("error1"));
			e.printStackTrace();
		}
		
	}
	
	public Proxy_pki getProxy() {
		return proxy;
	}
	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
		try{
			this.proxy = (Proxy_pki) entityManager.find(Proxy_pki.class, this.id);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}
	
	
}
