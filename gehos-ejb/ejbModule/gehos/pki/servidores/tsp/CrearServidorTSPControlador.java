package gehos.pki.servidores.tsp;

import gehos.pki.entity.Proxy_pki;
import gehos.pki.entity.ServidorSelloTiempo_pki;

import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Remove;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Scope(ScopeType.CONVERSATION)
@Name("crearServidorTSPControlador")
public class CrearServidorTSPControlador {

	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;

	private ServidorSelloTiempo_pki server = new ServidorSelloTiempo_pki();

	private Proxy_pki proxy = null;
	private boolean error = false;
	private boolean activo = false;

	private boolean initConv = true;

	// Methods ------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void initConversation() {
		this.initConv = false;
	}

	@SuppressWarnings("unchecked")
	public boolean validacion() {
		boolean ret = false;
		List<ServidorSelloTiempo_pki> le = entityManager
				.createQuery(
						"select server from ServidorSelloTiempo_pki server "
								+ "where server.url = :url")
								.setParameter("url", this.server.getUrl().trim())
								.getResultList();
		if (le.size() != 0) {
			this.error = true;
			facesMessages.add(Severity.ERROR, SeamResourceBundle.getBundle()
					.getString("error1"));
			ret = true;
		}

		return ret;

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

	public void cambiarActivo() {
		this.activo = !this.activo;
	}

	//addEvelio
	FacesMessage facesMessage;

	public void validarUrl(FacesContext context, UIComponent component,
			Object value) {
		if (!value.toString().equals("")) {
			if (value.toString().matches(
					"^http(s?)://[0-9a-zA-Z]*[.][0-9a-zA-Z]([-.(0-9a-zA-Z)]*[0-9a-zA-Z])*(:(0-9)*)*?$") == false) {
				this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"), null);
				this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(facesMessage);

			}}
	}
	public void descripcionEnBlanco(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {
			int longitud = value.toString().length();
			if (!(longitud <= 250)) {
				
				this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
						.getString("maximoCaracteres250"), null);
				this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(facesMessage);
			}

			if (!value.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ¿?.,0-9]+\\s*)+$")) {
				this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"), null);
				this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(facesMessage);
			}

		}
	}
	@Transactional
	public void crear() {
		try {


			this.server.setProxy(this.proxy);


			/*
			if (this.activo) {
				ServidorSelloTiempo_pki serv = null;
				try {
					serv = (ServidorSelloTiempo_pki) entityManager
							.createQuery(
									"select s from ServidorSelloTiempo_pki s"
											+ " where s.activo = true")
											.getSingleResult();
					facesMessages.addToControlFromResourceBundle(
							"buttonAceptar",
							Severity.ERROR,
							SeamResourceBundle.getBundle().getString(
									"error2"));

					this.activo = false;
					return;
				} catch (NoResultException e) {
					// TODO: handle exception
				}
			}*/
			this.server.setActivo(this.activo);
			try {
				entityManager.persist(this.server);
				entityManager.flush();
			} catch (Exception e) {

				facesMessages.addToControlFromResourceBundle(
						"buttonAceptar", Severity.ERROR, e.getMessage());
				e.printStackTrace();
			}


		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	public void seleccionarProxy(Proxy_pki pro) {
		this.proxy = pro;

	}

	public void eliminarSeleccionarProxy() {

		this.proxy = null;

	}

	// Propiedades----------------------------------

	public Boolean getError() {
		return error;
	}

	public void setError(Boolean error) {
		this.error = error;
	}

	public Proxy_pki getProxy() {
		return proxy;
	}

	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public boolean isInitConv() {
		return initConv;
	}

	public void setInitConv(boolean initConv) {
		this.initConv = initConv;
	}

	public ServidorSelloTiempo_pki getServer() {
		return server;
	}

	public void setServer(ServidorSelloTiempo_pki server) {
		this.server = server;
	}

}
