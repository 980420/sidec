package gehos.configuracion.management.gestionarEntidad;

import java.util.ArrayList;
import java.util.List;
import gehos.comun.shell.ModSelectorController;
import gehos.configuracion.management.entity.AsignacionFinancieraHospital_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.PartidaPresupuestariaHospital_configuracion;
import gehos.configuracion.management.entity.PoblacionAreaInfluencia_configuracion;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("entidadVerDetallesControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadVerDetallesControlador {

	// Entidad
	private Entidad_configuracion entidad = new Entidad_configuracion();
	private Long entidadId = -1l;
	private boolean existLogo = false;
	private String caracter = "";

	// otras funcionalidades
	private String from = "";
	private String provinciaEtic = "Estado";
	private String municipioEtic = "Municipio";
	private String localidadEtic = "Parroquia";
	List<String> naciones = new ArrayList<String>();

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	// METODOS---------------------------------------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setEntidadId(Long entidadId) {
		naciones = naciones();

//		String[] provincias = new String[naciones.size()];
//		provincias[0] = "Provincia:";
//		provincias[1] = "Provincia:";
//		provincias[2] = "Estado:";
//
//		String[] municipios = new String[naciones.size()];
//		municipios[0] = "Municipio:";
//		municipios[1] = "Municipio:";
//		municipios[2] = "Municipio:";
//
//		String[] localidades = new String[naciones.size()];
//		localidades[0] = "Localidad:";
//		localidades[1] = "Localidad:";
//		localidades[2] = "Parroquia:";	
		
		this.entidadId = entidadId;
		entidad = entityManager.find(Entidad_configuracion.class, entidadId);
		int pos = naciones.indexOf(this.entidad.getEstado().getNacion().getValor());
		// provinciaEtic = provincias[pos];
		// municipioEtic = municipios[pos];
		// localidadEtic = localidades[pos];
		
		if (entidad.isEsPublico()) {
			caracter = SeamResourceBundle.getBundle()
					.getString("caracter1");
		} else {
			caracter = SeamResourceBundle.getBundle()
					.getString("caracter2");
		}
	}
	
	// carga lista de naciones
	@SuppressWarnings("unchecked")
	public List<String> naciones() {
		return entityManager.createQuery(
				"select n.valor from Nacion_configuracion n order by n.valor")
				.getResultList();
	}
	

	// debuelva la poblacion de área de influencia
	public Integer poblacionAreaInfluencia() {
	

		Long ultimaPoblacion = (Long) entityManager
				.createQuery(
						"select max (p.id) from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId")
				.setParameter("entidadId", entidadId).getSingleResult();

		if (ultimaPoblacion != null) {
			Integer valor = (Integer) entityManager
					.createQuery(
							"select p.valor from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId and p.id =:ultimaPoblacion")
					.setParameter("ultimaPoblacion", ultimaPoblacion)
					.setParameter("entidadId", entidadId).getSingleResult();
			return valor;
		}
		return 0;
	}

	// eliminar la entidad seleccionada
	@SuppressWarnings("unchecked")
	public String eliminarEntidad() {
		try {
			Entidad_configuracion ent = (Entidad_configuracion) entityManager
					.createQuery(
							"select e from Entidad_configuracion e where e.id = :idEntidad")
					.setParameter("idEntidad", this.entidad.getId())
					.getSingleResult();

			// asingacion financiera
			List<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList = entityManager
					.createQuery(
							"select a from AsignacionFinancieraHospital_configuracion a join a.entidad ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", ent.getId()).getResultList();

			for (int i = 0; i < asignacionFinancieraList.size(); i++) {
				entityManager.remove(asignacionFinancieraList.get(i));
			}

			// partida presupuestaria
			List<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList = entityManager
					.createQuery(
							"select p from PartidaPresupuestariaHospital_configuracion p join p.entidad ent where ent.id = :idEntidad and p.eliminado = false")
					.setParameter("idEntidad", ent.getId()).getResultList();

			for (int i = 0; i < partidaPresupuestariaList.size(); i++) {
				entityManager.remove(partidaPresupuestariaList.get(i));
			}

			// eliminando poblacion de area de influencia
			List<PoblacionAreaInfluencia_configuracion> p = entityManager
					.createQuery(
							"select p from PoblacionAreaInfluencia_configuracion p where p.entidad.id = :idEntidad")
					.setParameter("idEntidad", this.entidad.getId())
					.getResultList();
			for (int i = 0; i < p.size(); i++) {
				entityManager.remove(p.get(i));
			}

			entityManager.remove(ent);
			entityManager.flush();
			ModSelectorController.reloadsEntities();
			return "gotoList";
		} catch (Exception exc) {
			facesMessages
					.addToControlFromResourceBundle("btnSi", Severity.ERROR,SeamResourceBundle.getBundle()
							.getString("msjEliminar"));
			return "fail";
		}
	}

	// PROPIEDADES-----------------------------------------------------------------------------
	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public boolean isExistLogo() {
		return existLogo;
	}

	public void setExistLogo(boolean existLogo) {
		this.existLogo = existLogo;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getCaracter() {
		return caracter;
	}

	public void setCaracter(String caracter) {
		this.caracter = caracter;
	}

	public String getProvinciaEtic() {
		return provinciaEtic;
	}

	public void setProvinciaEtic(String provinciaEtic) {
		this.provinciaEtic = provinciaEtic;
	}

	public String getMunicipioEtic() {
		return municipioEtic;
	}

	public void setMunicipioEtic(String municipioEtic) {
		this.municipioEtic = municipioEtic;
	}

	public String getLocalidadEtic() {
		return localidadEtic;
	}

	public void setLocalidadEtic(String localidadEtic) {
		this.localidadEtic = localidadEtic;
	}
}
