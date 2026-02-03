//CU 32 Visualizar detalles de nota de sitio
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import java.util.ArrayList;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

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

@Name("verNotaMonitoreoListado")
@Scope(ScopeType.CONVERSATION)
public class VerNotaMonitoreoListado {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	@In
	private Usuario user;
	
	
	private Nota_ensayo notaMonitoreo;
	private String from = "";
	private Long idSujeto, idNota, idcrd, idMS,idGrupo;

	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void inicializarNota(){
		this.notaMonitoreo = entityManager.find(Nota_ensayo.class, idNota);
		
	}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> DevolverNotaMonitoreoAsociadas(Nota_ensayo Nota){
		List<Nota_ensayo> listaHijas = new ArrayList<Nota_ensayo>();
		listaHijas = (List<Nota_ensayo>) entityManager
					.createQuery(
							"select nota from Nota_ensayo nota where nota.notaPadre=:notaPadre and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE' ORDER BY nota.id DESC ")
					.setParameter("notaPadre", Nota).getResultList();
		
		
		
		return listaHijas;
	}
	
	public VariableDato_ensayo ObtenerValorVariable(){
		VariableDato_ensayo variableDato = new VariableDato_ensayo();
		try {
			variableDato = (VariableDato_ensayo)entityManager
					.createQuery(
							"select variableD from VariableDato_ensayo variableD where variableD.variable=:Variable and variableD.crdEspecifico=:Hoja")
					.setParameter("Variable", notaMonitoreo.getVariable())
					.setParameter("Hoja", notaMonitoreo.getCrdEspecifico()).getSingleResult();
			
		} catch (Exception e) {
			return null;
		}
		
		
		return variableDato;
	}
	
	
	public Long getIdSujeto() {
		return idSujeto;
	}
	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
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

	public Long getIdMS() {
		return idMS;
	}

	public void setIdMS(Long idMS) {
		this.idMS = idMS;
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

	public Long getIdcrd() {
		return idcrd;
	}

	public Nota_ensayo getNotaMonitoreo() {
		return notaMonitoreo;
	}

	public void setNotaMonitoreo(Nota_ensayo notaMonitoreo) {
		this.notaMonitoreo = notaMonitoreo;
	}

	public void setIdcrd(Long idcrd) {
		this.idcrd = idcrd;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
}