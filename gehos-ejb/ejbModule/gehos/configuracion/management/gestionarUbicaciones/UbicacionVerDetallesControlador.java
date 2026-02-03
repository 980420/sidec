package gehos.configuracion.management.gestionarUbicaciones;
import java.util.ArrayList;
import java.util.List;

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;

import gehos.configuracion.management.entity.Ubicacion_configuracion;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Transactional;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("ubicacionVerDetallesControlador")
public class UbicacionVerDetallesControlador {	
	
	@In EntityManager entityManager;	
	@In IBitacora bitacora;
	@In	FacesMessages facesMessages;
	@In LocaleSelector localeSelector;
	@In RulesParser rulesParser;
		 
	private Long ubicacionId;	
	private String location;
	private Ubicacion_configuracion ubicacion = new Ubicacion_configuracion();
	
	//other functions
	private String from;
	private int error;
	private boolean errorLoadData;

	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;
	RuleBase ruleBase;	
	
	//Methods-----------------------------------------------------	
	public void setUbicacionId(Long ubicacionId) {
		errorLoadData = false;
		this.ubicacionId = ubicacionId;
		ubicacion = new Ubicacion_configuracion();
		
		try {			
			ubicacion = (Ubicacion_configuracion) 
						entityManager.createQuery("select u from Ubicacion_configuracion u where u.id =:id " +
												  "and (u.eliminado = false or u.eliminado = null)")
									 .setParameter("id", ubicacionId)
									 .getSingleResult();
			
			if (this.ubicacion.getUbicacion() == null) 
				location = "";
			 else // cargando el valor de la ubicacion a mostrar
				location = this.getUbicationIdfromRules(this.ubicacion.getUbicacion().getId());
			
		} catch (NoResultException e) {
			errorLoadData = true;
		} 
		catch (Exception e) {
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
			bitacora.registrarInicioDeAccion("Eliminando ubicacion");
						
			ubicacion.setEliminado(true);
			entityManager.persist(ubicacion);
			entityManager.flush();
			
		} catch (Exception e) {			
			error = 1;
			facesMessages.addToControlFromResourceBundle("error", Severity.ERROR, "msjEliminar");
		}		
	}

	//Properties-------------------------------------------------
	public Ubicacion_configuracion getUbicacion() {
		return ubicacion;
	}


	public void setUbicacion(Ubicacion_configuracion ubicacion) {
		this.ubicacion = ubicacion;
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

	public Long getUbicacionId() {
		return ubicacionId;
	}
}