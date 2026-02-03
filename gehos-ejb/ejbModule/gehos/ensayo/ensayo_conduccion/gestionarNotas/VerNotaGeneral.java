//CU 32 Visualizar detalles de nota de sitio
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.entity.NotaGeneral_ensayo;

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

@Name("verNotaGeneral")
@Scope(ScopeType.CONVERSATION)
public class VerNotaGeneral {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	@In
	private Usuario user;
	
	
	private NotaGeneral_ensayo notaGeneral;
	private Long idNota, idGrupo;

	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void inicializarNota(){
		this.notaGeneral = entityManager.find(NotaGeneral_ensayo.class, idNota);
	}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	@SuppressWarnings("unchecked")
	public List<NotaGeneral_ensayo> DevolverNotaGeneralAsociadas(NotaGeneral_ensayo Nota){
		List<NotaGeneral_ensayo> listaHijas = new ArrayList<NotaGeneral_ensayo>();
		listaHijas = (List<NotaGeneral_ensayo>) entityManager
					.createQuery(
							"select nota from NotaGeneral_ensayo nota where nota.notaGeneralPadre=:notaPadre and nota.eliminado = 'FALSE' ")
					.setParameter("notaPadre", Nota).getResultList();
		
		
		
		return listaHijas;
	}
	

	public Long getIdNota() {
		return idNota;
	}

	public void setIdNota(Long idNota) {
		this.idNota = idNota;
	}

	public Usuario getUser() {
		return user;
	}


	public void setUser(Usuario user) {
		this.user = user;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public NotaGeneral_ensayo getNotaGeneral() {
		return notaGeneral;
	}

	public void setNotaGeneral(NotaGeneral_ensayo notaGeneral) {
		this.notaGeneral = notaGeneral;
	}

	
	
}
