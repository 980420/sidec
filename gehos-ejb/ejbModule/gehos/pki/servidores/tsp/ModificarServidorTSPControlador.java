package gehos.pki.servidores.tsp;

import gehos.pki.entity.Proxy_pki;
import gehos.pki.entity.ServidorSelloTiempo_pki;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarServidorTSPControlador")
@Scope(ScopeType.CONVERSATION)
public class ModificarServidorTSPControlador {

	@In
	EntityManager entityManager;
	@In 
	FacesMessages facesMessages;



	private ServidorSelloTiempo_pki server;
	private Proxy_pki proxy;

	private Integer id;
	private boolean error = false;
	private boolean activo;


	// Methods ---------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Integer id) {

		try {
			this.id = id;
			this.server = entityManager.find(ServidorSelloTiempo_pki.class, id);
			this.activo = this.server.getActivo();
			this.proxy = this.server.getProxy();

		} catch (NoResultException e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "msg_noExist_modConfig");
			e.printStackTrace();
			return;
		}
		catch (Exception e) {
			this.error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "Otro error Inesperado en setId(Integer id)" +
					"[modificarServidorControlador]");
			e.printStackTrace();
			return;
		}
	}

	public void cambiarActivo(){
		this.activo = !this.activo;
	}

	public void seleccionarProxy(Proxy_pki pro){
		this.proxy = pro;

	}

	public void eliminarSeleccionarProxy(){

		this.proxy = null;

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
	// Validacion de espacio blanco
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
		/*
	public void descripcionEnBlanco(FacesContext context, UIComponent component,
			Object value) {
		if (context.getExternalContext().getRequestParameterMap()
				.containsKey("form:buttonAceptar")) {
		if (!value.toString().equals("")) {
			if (value.toString().matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ¿?.,0-9]+\\s*)+$")) {
				this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
						.getString("datosIncorrectos"), null);
				this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(facesMessage);
			}

		}}
	}*/
	@Transactional
	public void modificar() {
		try {

			
			this.server.setProxy(this.proxy);




			/*if (this.activo) {
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
			this.error = true;
			e.printStackTrace(); 
		}
	}






	//Properties---------------------------------------------------

	public ServidorSelloTiempo_pki getServer() {
		return server;
	}

	public void setServer(ServidorSelloTiempo_pki server) {
		this.server = server;
	}



	public Integer getId() {
		return id;
	}

	public boolean isError() {
		return error;
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

	public Proxy_pki getProxy() {
		return proxy;
	}

	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}


}