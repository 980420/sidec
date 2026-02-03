package gehos.configuracion.management.gestionarCama;
import java.util.ArrayList;
import java.util.List;

import gehos.comun.reglas.parser.RulesDirectoryBase;
import gehos.comun.reglas.parser.RulesParser;
import gehos.configuracion.clinicaldata.ubicaciones.management.UbicacionID;

import gehos.configuracion.management.entity.Cama_configuracion;
import gehos.configuracion.management.entity.Ubicacion_configuracion;

import javax.persistence.EntityManager;

import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;

import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("verDetallesCamaControlador")
@Scope(ScopeType.CONVERSATION)
public class VerDetallesCamaControlador {		
		
	//otras funcionalidades
	private String from = "";
	
	@In 
	EntityManager entityManager;	         
	
	@In(create = true)
	FacesMessages facesMessages;
	
	@In
	LocaleSelector localeSelector;	
	
	//Atributos
	@In
	RulesParser rulesParser;
	UbicacionID result = new UbicacionID();
	List<Ubicacion_configuracion> jerarquia;
	RuleBase ruleBase;
	
	private Long idCama;
	
	private Ubicacion_configuracion newLocation = new Ubicacion_configuracion();
	private String ubicacionEnModificacion = "";		
	
	private Cama_configuracion cama = new Cama_configuracion();
	
	
	
	//Metodos-----------------------------------------------------	
	public void putNewLocation() {		
		this.ubicacionEnModificacion = this
				.getUbicationIdfromRules(this.newLocation.getId());
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
	
	public String getIdfromRules(Long id, RuleBase ruleBase)
	throws Exception {
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

	//PROPIEDADES-------------------------------------------------
	public Ubicacion_configuracion getUbicacion() {
		return newLocation;
	}


	public void setUbicacion(Ubicacion_configuracion ubicacion) {
		this.newLocation = ubicacion;
	}


	public String getUbicacionName() {
		return ubicacionEnModificacion;
	}


	public void setUbicacionName(String ubicacionName) {
		this.ubicacionEnModificacion = ubicacionName;
	}

	public Cama_configuracion getCama() {
		return cama;
	}

	public void setCama(Cama_configuracion cama) {
		this.cama = cama;
	}

	public Long getIdCama() {
		return idCama;
	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setIdCama(Long idCama) {
		this.idCama = idCama;
		
		this.cama = entityManager.find(Cama_configuracion.class, this.idCama);
		
		this.newLocation = this.cama.getUbicacion();
						
		putNewLocation();
		
		
	}
	
	public String eliminar(){
		try {
			
			cama.setEliminado(true);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "Esta cama no puede ser eliminado.");
			return "fail";
		}
		return "eliminar";
		
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	

}
