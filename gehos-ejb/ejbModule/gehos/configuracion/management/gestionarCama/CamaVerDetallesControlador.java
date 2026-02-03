package gehos.configuracion.management.gestionarCama;
import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;

import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import javax.persistence.EntityManager;

import org.drools.RuleBase;
import org.drools.StatefulSession;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("camaVerDetallesControlador")
public class CamaVerDetallesControlador {		
		
	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In LocaleSelector localeSelector;
	@In RulesParser rulesParser;
	@In IBitacora bitacora;
	
	private Long idCama;	
	private String location;
	private Cama_configuracion cama;
			
	//other functions
	private String from; 
	private int error;
	private boolean errorLoadData;
	
	RuleBase ruleBase;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;
	
	//Methods -----------------------------------------------------
	public void setIdCama(Long idCama) {
		cama = new Cama_configuracion();		
		errorLoadData = false;
		this.idCama = idCama;		
		
		try {
			this.cama = (Cama_configuracion) 
						entityManager.createQuery("select c from Cama_configuracion c where c.id = :id and " +
												  "(c.eliminado = false or c.eliminado = null)")
									 .setParameter("id", idCama)
									 .getSingleResult();			
			
			if (this.cama.getUbicacion() == null) 
				location = "";
			else // cargando el valor de la ubicacion a mostrar 
				location = this.getUbicationIdfromRules(this.cama.getUbicacion().getId());
		} catch (Exception e) {
			errorLoadData = true;
		}
	}
	
	public String getUbicationIdfromRules(Long ubicationId) {
		result = new UbicacionID();
		jerarquia = new ArrayList<Ubicacion_configuracion>();
		if (ruleBase == null)
			ruleBase = this.getParser();
		try {
			String id = this.getIdfromRules(ubicationId, ruleBase);
			return id;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public RuleBase getParser() {
		RuleBase ruleBase = null;
		try {
			ruleBase = rulesParser.readRule(
					"/configuracion/ubicaciones/id_generator.drl",
					RulesDirectoryBase.business_rules);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ruleBase;
	}
	
	public String getIdfromRules(Long id, RuleBase ruleBase) throws Exception {
		jerarquia = new ArrayList<Ubicacion_configuracion>();
		Ubicacion_configuracion temp = entityManager.find(Ubicacion_configuracion.class, id);
		
		while (temp != null) {
			jerarquia.add(0, temp);
			temp = temp.getUbicacion();
		}
		try {
			StatefulSession session = ruleBase.newStatefulSession();
			session.insert(result);
			session.insert(jerarquia);
			session.fireAllRules();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result.getId();
	}
	
	@Transactional 
	public void eliminar(){
		error = 0;
		try {
			bitacora.registrarInicioDeAccion("Eliminando cama");
			 
			cama.setEliminado(true);
			entityManager.flush();
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "msjEliminar");			
		}
	}

	//Priperties -------------------------------------------------
	public Cama_configuracion getCama() {
		return cama;
	}

	public void setCama(Cama_configuracion cama) {
		this.cama = cama;
	}

	public Long getIdCama() {
		return idCama;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
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
}