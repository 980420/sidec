//CU 32 Visualizar detalles de nota de sitio
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

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

@Name("verNotaSitioListado")
@Scope(ScopeType.CONVERSATION)
public class VerNotaSitioListado {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	@In
	private Usuario user;
	
	
	private Nota_ensayo notaSitio;
	private String from = "";
	private Long idSujeto, idNota, idcrd, idMS, idGrupo;

	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void inicializarNota(){
		this.notaSitio = entityManager.find(Nota_ensayo.class, idNota);
	}
	
	@End 
	public void salir(){		
		
	}
	
	@Remove @Destroy
	public void destroy(){}
	
	public VariableDato_ensayo ObtenerValorVariable(){
		VariableDato_ensayo variableDato = new VariableDato_ensayo();
		try {
			variableDato = (VariableDato_ensayo)entityManager
					.createQuery(
							"select variableD from VariableDato_ensayo variableD where variableD.variable=:Variable and variableD.crdEspecifico=:Hoja")
					.setParameter("Variable", notaSitio.getVariable())
					.setParameter("Hoja", notaSitio.getCrdEspecifico()).getSingleResult();
			
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

	public Nota_ensayo getNotaSitio() {
		return notaSitio;
	}

	public void setNotaSitio(Nota_ensayo notaSitio) {
		this.notaSitio = notaSitio;
	}

	public Long getIdNota() {
		return idNota;
	}

	public Long getIdMS() {
		return idMS;
	}

	public void setIdMS(Long idMS) {
		this.idMS = idMS;
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

	public Long getIdcrd() {
		return idcrd;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
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