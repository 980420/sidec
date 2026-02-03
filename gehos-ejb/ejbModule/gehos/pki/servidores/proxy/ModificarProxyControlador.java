package gehos.pki.servidores.proxy;

import gehos.pki.entity.Proxy_pki;

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

@Name("modificarProxyControlador")
@Scope(ScopeType.CONVERSATION)
public class ModificarProxyControlador {
	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;
	
	private Proxy_pki proxy = null;
	private String id;
	private String from;
	private boolean error = false;
	
	@Transactional
	@End
	public void modificar(){
		try{
			this.error = false;
			if (this.proxy.getDireccion() == "") {
				facesMessages.addToControlFromResourceBundle("direccion",
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("valorRequerido"));

				error = true;
			} else {				
				// !mat.find()
				if (this.proxy
						.getDireccion()
						.matches(
								"^(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])(.(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])){3}$")==false) {
					facesMessages.addToControlFromResourceBundle("direccion",
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("datosIncorrectos"));

					error = true;
				}
			}
			if (this.proxy.getPuerto() == "") {
				facesMessages.addToControlFromResourceBundle("puerto",
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("valorRequerido"));
			} else {
				if (this.proxy
						.getPuerto()						
						.matches(
								"^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[0-5]?([0-9]){0,3}[0-9])$")==false) {
					facesMessages.addToControlFromResourceBundle("puerto",
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("puertoIncorrecto"));

					error = true;
				}
			}

			if (!error) {
				entityManager.persist(this.proxy);
				entityManager.flush();
			} else {
				throw new Exception();
			}
		}catch (Exception e) {
			this.error = true;
			
			e.printStackTrace();
		}
	}
	
	public Proxy_pki getProxy() {
		return proxy;
	}
	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}
	public String getId() {
		return id;
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(String id) {
		this.id = id;
		try{
			this.proxy = (Proxy_pki) entityManager.find(Proxy_pki.class, Integer.parseInt(this.id));
		}catch (Exception e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "Error inesperado en modificarProxyControlador.setId()");
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
