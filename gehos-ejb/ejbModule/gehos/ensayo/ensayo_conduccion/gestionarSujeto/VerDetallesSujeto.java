//CU 3 Ver detalles de sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import java.util.List;

import gehos.ensayo.entity.Sujeto_ensayo;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("verDetallesSujeto")
@Scope(ScopeType.CONVERSATION)
public class VerDetallesSujeto {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	
	
	private Sujeto_ensayo sujeto;
	private Long idSujeto;

	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void loadData(){
		this.sujeto = (Sujeto_ensayo)entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:id").setParameter("id", idSujeto).getSingleResult();
		
	}
	
	//CU 9 Eliminar sujeto
		@SuppressWarnings("unchecked")
		public void EliminarInstanciaSuj() {

			List<Sujeto_ensayo> sujEliminar = entityManager
					.createQuery(
							"select suj from Sujeto_ensayo suj where suj.id =:id and suj.eliminado = false")
					.setParameter("id", idSujeto).getResultList();
			Sujeto_ensayo nuevo = sujEliminar.get(0);
			nuevo.setEliminado(true);
			entityManager.persist(nuevo);
			entityManager.flush();

		}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	public Long getIdSujeto() {
		return idSujeto;
	}
	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}
	
}
