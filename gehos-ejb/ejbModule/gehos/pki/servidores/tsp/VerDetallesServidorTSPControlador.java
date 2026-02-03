package gehos.pki.servidores.tsp;

import gehos.pki.entity.ServidorSelloTiempo_pki;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("verDetallesServidorTSPControlador")
public class VerDetallesServidorTSPControlador {
	
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	
	private Integer id;
	ServidorSelloTiempo_pki server;
	
	///
			
	//otras funcionalidades
	private String from = "";
	private boolean error = false;
	
	//Methos ---------------------------------------------------------------------		
	public void setId(Integer id) {
		
		try {
			this.id = id;
			this.server = (ServidorSelloTiempo_pki) 
			  			  entityManager.find(ServidorSelloTiempo_pki.class, this.id);
			
		} catch (Exception e) {
			error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado en setId");
			e.printStackTrace();
		}	
	}	
	
	@Transactional
	public void eliminar(){
		try {
			entityManager.remove(this.server);
			entityManager.flush();
			
		} catch (NoResultException e) {
			error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = true;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "msg_servidorEnUso_modConfig");
			e.printStackTrace();
		}
	}
	
	
	//Properties -------------------------------------------------

	

	public ServidorSelloTiempo_pki getServer() {
		return server;
	}

	public void setServer(ServidorSelloTiempo_pki server) {
		this.server = server;
	}

	
	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public Integer getId() {
		return id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	
}
