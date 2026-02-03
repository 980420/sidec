package gehos.configuracion.management.gestionarMunicipios;

import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Municipio_configuracion;

import javax.persistence.EntityManager;

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
import org.jboss.seam.international.StatusMessage.Severity;

@Name("municipioCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class MunicipioCrearControlador {	 
	
	@In IBitacora bitacora;
	@In FacesMessages facesMessages;	
	@In EntityManager entityManager;	
	
	private String codigo;
	private String valor;
	private String nacionSelecc;
	private String estadoSelecc;
	private Municipio_configuracion municipio;
		
	// other functionalities
	private Long cid = -1l;
	private int error;	
		
	// Methods----------------------------------------------------	
	@Begin(flushMode=FlushModeType.MANUAL, join = true)
	public void begin(){
		try{
			error = 0;
			
			codigo = "";
			valor = "";
			nacionSelecc = "";
			estadoSelecc = "";
			municipio = new Municipio_configuracion();
			
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrear"));
				municipio.setCid(cid);
			}
		}catch (Exception e) {
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
			e.printStackTrace();
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
	public void crear() {
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
		catch (Exception e) {
			error = 1;
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("message", Severity.ERROR, "errorInesperado");
		}
	}
	
	// Properties--------------------------------------------------	
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
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

	public String getNacionSelecc() {
		return nacionSelecc;
	}

	public void setNacionSelecc(String nacionSelecc) {
		this.nacionSelecc = nacionSelecc;
	}
}