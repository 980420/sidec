package gehos.ensayo.ensayo_disenno.gestionarGruposujetos;

import gehos.ensayo.entity.GrupoSujetos_ensayo;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

@Scope(ScopeType.PAGE)
@Name("verGruposujetos")
public class VerGruposujetos {

	private GrupoSujetos_ensayo gruposujetos = new GrupoSujetos_ensayo();
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	int error = -1;
	Long idGruposujetos;
	String desde = "";

	public void cargarGruposujetos() {
		try {
			this.gruposujetos = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", this.idGruposujetos)
					.getSingleResult();
			error = 0;
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("viewGruposujetos",
					Severity.ERROR, "errorinesperado");
			error = 1;
		}
	}

	/**
	 * Elimina de la base de datos
	 */
	public void eliminar() throws IllegalStateException, SecurityException,
			SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager.createQuery(
					"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", this.idGruposujetos)
					.getSingleResult();
		
			gruposujetos.setEliminado(true);
			entityManager.persist(gruposujetos);
			entityManager.flush();			
			error = 0;
			
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("viewGruposujetos", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("viewGruposujetos", Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}		

	public GrupoSujetos_ensayo getGruposujetos() {
		return gruposujetos;
	}

	public void setGruposujetos(GrupoSujetos_ensayo gruposujetos) {
		this.gruposujetos = gruposujetos;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public Long getIdGruposujetos() {
		return idGruposujetos;
	}

	public void setIdGruposujetos(Long idGruposujetos) {
		this.idGruposujetos = idGruposujetos;
	}

	public String getDesde() {
		return desde;
	}

	public void setDesde(String desde) {
		this.desde = desde;
	}
}