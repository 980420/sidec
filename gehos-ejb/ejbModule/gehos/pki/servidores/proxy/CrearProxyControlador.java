package gehos.pki.servidores.proxy;



import gehos.pki.entity.Proxy_pki;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

import javax.faces.application.FacesMessage;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearProxyControlador")
@Scope(ScopeType.CONVERSATION)
public class CrearProxyControlador {

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	FacesMessage facesMessage;

	private boolean error = false;
	private Proxy_pki proxy = new Proxy_pki();
	private String from = "menu";

	private boolean initConv = true;

	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void initConversation() {
		this.initConv = false;
	}

	public void crear() {
		try {
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
								"^(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])(.(25[0-5]|2[0-4][0-9]|[01]?[0-9]?[0-9])){3}$") == false) {
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
								"^(6553[0-5]|655[0-2][0-9]|65[0-4][0-9]{2}|6[0-4][0-9]{3}|[0-5]?([0-9]){0,3}[0-9])$") == false) {
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

		} catch (Exception e) {
			this.error = true;
			e.printStackTrace();
		}
	}

	@Create
	public void init() {
		System.out.println("El objeto es agregado al contexto:::" + "["
				+ this.toString() + "]");
	}

	@Remove
	@Destroy
	public void clean() {
		System.out.println("El objeto es removido del contexto:::" + "["
				+ this.toString() + "]");
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Proxy_pki getProxy() {
		return proxy;
	}

	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public boolean isInitConv() {
		return initConv;
	}

	public void setInitConv(boolean initConv) {
		this.initConv = initConv;
	}

}
