package gehos.configuracion.management.gestionarBioanalista;

import gehos.configuracion.management.entity.BioanalistaInEntidad_configuracion;
import gehos.configuracion.management.entity.Bioanalista_configuracion;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.MedicoInEntidad_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.Servicio_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.gestionarUsuario.Cultura;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Scope(ScopeType.CONVERSATION)
@Name("bioanalistaDetail_Controlador")
public class BioanalistaDetail_Controlador {
	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	// Medico
	private Long cid = -1l, id;
	private Bioanalista_configuracion medico = new Bioanalista_configuracion();

	// Medico en Entidad
	private List<BioanalistaInEntidad_configuracion> mediInEnt = new ArrayList<BioanalistaInEntidad_configuracion>();

	// Roles
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// otras funcionalidades
	private String from = "";

	@In(create = true)
	FacesMessages facesMessages;

	// Metodos
	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		listaCultura = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			listaCultura.add(c);
			lista.add(c.cultura());
		}
		return lista;
	}

	public String eliminar() {
		try {

			medico.setEliminado(true);
			entityManager.flush();
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "Este médico no puede ser eliminado.");
			return "fail";
		}
		return "eliminar";

	}

	@SuppressWarnings("unchecked")
	public void Source() {
		// mediInEnt = new ArrayList<BioanalistaInEntidad_configuracion>(
		// this.medico.getBioanalistaInEntidads());
		mediInEnt = entityManager
				.createQuery(
						"from BioanalistaInEntidad_configuracion b where b.bioanalista.id = :id")
				.setParameter("id", this.medico.getId()).getResultList();
	}

	// Propiedades

	public Bioanalista_configuracion getMedico() {
		return medico;
	}

	public void setMedico(Bioanalista_configuracion medico) {
		this.medico = medico;
	}

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public String getCulturaSelec() {
		return culturaSelec;
	}

	public void setCulturaSelec(String culturaSelec) {
		this.culturaSelec = culturaSelec;
	}

	public Long getId() {
		return id;
	}

	@SuppressWarnings("unchecked")
	public void setId(Long id) {
		this.id = id;

		cultura();

		medico = entityManager.find(Bioanalista_configuracion.class, this.id);

		this.Source();

		rolsSource.clear();
		rolsSource.addAll(medico.getUsuario().getRoles());

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(medico.getUsuario().getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
			}
		}
	}

	public List<BioanalistaInEntidad_configuracion> getMediInEnt() {
		return mediInEnt;
	}

	public void setMediInEnt(List<BioanalistaInEntidad_configuracion> mediInEnt) {
		this.mediInEnt = mediInEnt;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
