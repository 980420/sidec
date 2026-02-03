package gehos.configuracion.management.gestionarMunicipios;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Municipio_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

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
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("municipioModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class MunicipioModificarControlador { 

	@In IBitacora bitacora;
	@In EntityManager entityManager;
	@In LocaleSelector localeSelector;
	@In FacesMessages facesMessages;

	private Long id;
	private String codigo;
	private String valor;
	private String nacionSelecc;
	private String estadoSelecc;
	private Municipio_configuracion municipio;
	
	//other functions
	private int error;
	private boolean errorLoadData;
	private Long cid = -1l;
	
	// Methods------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, nested = true)
	public void setId(Long id) {	
		error = 0;
		this.id = id;
		
		try {
			this.municipio = (Municipio_configuracion) 
						  entityManager.createQuery("select m from Municipio_configuracion m " +
						  							"where m.id =:id " +
						  							"and m.eliminado = false " +
						  							"and m.estado.eliminado = fase " +
						  							"and m.estado.nacion.eliminado = false")
						  			   .setParameter("id", id)
						  			   .getSingleResult();
			
			codigo = municipio.getCodigo();
			valor = municipio.getValor();
			
			estadoSelecc = municipio.getEstado().getValor();	
			nacionSelecc = municipio.getEstado().getNacion().getValor();
			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitModificar"));			
		} catch (NoResultException e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "eliminado");
			e.printStackTrace();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
			e.printStackTrace();
		}								
	}	
	
	// return a contry list
	@SuppressWarnings("unchecked")
	public List<String> listNaciones(){
		List<String> le = new ArrayList<String>();
		try {
			le = entityManager.createQuery("select n.valor from Nacion_configuracion n " +
										   "where n.eliminado = false " +
										    "order by n.valor")
							  .getResultList();			
			return le;
		} catch (Exception e) {			
			return le;
		}
	}	
	
	@SuppressWarnings("unchecked")
	public List<String> listEstados(){
		List<String> le = new ArrayList<String>();
		try {
			le = entityManager.createQuery("select e.valor from Estado_configuracion e " +
										   "where e.nacion.valor = :nacionSelecc " +
										   "and e.eliminado = false " +
										   "and e.nacion.eliminado = false " +
										   "order by e.valor")
							  .setParameter("nacionSelecc", nacionSelecc)
							  .getResultList();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		} catch (Exception e) {
			e.printStackTrace();
			le.add(0,SeamResourceBundle.getBundle().getString("seleccione"));
			return le;
		}
	}	
	
	@End
	public void end() {		
	}
	
	@Transactional
	public void modificar() {
		error = 0;
		
		try {			
			Estado_configuracion estado = (Estado_configuracion) 
			  							  entityManager.createQuery("select e from Estado_configuracion e " +
			  									                    "where e.valor=:estadoSelecc " +
			  									                    "and e.eliminado = false " +
			  							  							"and e.nacion.eliminado = false")
			  							  			   .setParameter("estadoSelecc", this.estadoSelecc)
			  							  			   .getSingleResult();		

			municipio.setCid(cid);
			municipio.setCodigo(codigo.trim());
			municipio.setValor(valor.trim());
			municipio.setEstado(estado);
			municipio.setEliminado(false);		
			
			entityManager.persist(municipio);
			entityManager.flush();	
			
			this.end();
		}
		catch(Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
		}	
	}

	// Properties-----------------------------------------------------
	public Long getId() {
		return id;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public boolean isErrorLoadData() {
		return errorLoadData;
	}

	public void setErrorLoadData(boolean errorLoadData) {
		this.errorLoadData = errorLoadData;
	}
	
	public String getNacionSelecc() {
		return nacionSelecc;
	}

	public void setNacionSelecc(String nacionSelecc) {
		this.nacionSelecc = nacionSelecc;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getEstadoSelecc() {
		return estadoSelecc;
	}

	public void setEstadoSelecc(String estadoSelecc) {
		this.estadoSelecc = estadoSelecc;
	}

	public Municipio_configuracion getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio_configuracion municipio) {
		this.municipio = municipio;
	}
}