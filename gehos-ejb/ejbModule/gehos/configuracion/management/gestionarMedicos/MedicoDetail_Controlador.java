package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.Departamento_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.MedicoInEntidad_configuracion;
import gehos.configuracion.management.entity.Medico_configuracion;
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
@Name("medicoDetail_Controlador")
public class MedicoDetail_Controlador {
	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	// Medico
	private Long cid = -1l, id;
	private Medico_configuracion medico = new Medico_configuracion();

	// Medico en Entidad
	private List<MedicoInEntidad_configuracion> mediInEnt = new ArrayList<MedicoInEntidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> especialidades = new ArrayList<EspecialidadInEntidad_configuracion>();

	// Roles
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Tipo Funcionario
	private List<TipoFuncionario_configuracion> tipoFuncionarioTarget = new ArrayList<TipoFuncionario_configuracion>();

	// Cargos
	private List<CargoFuncionario_configuracion> cargoTarget = new ArrayList<CargoFuncionario_configuracion>();

	// Entidades
	private List<Entidad_configuracion> listaEntidades = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> listaEntidadServicios = new ArrayList<Entidad_configuracion>();

	// Departamentos
	private List<Departamento_configuracion> listaDepartamentos = new ArrayList<Departamento_configuracion>();
	private List<Departamento_configuracion> listaDepartamentoServicios = new ArrayList<Departamento_configuracion>();

	// Servicios
	private List<Servicio_configuracion> listaServ = new ArrayList<Servicio_configuracion>();

	// Especialidad
	List<Especialidad_configuracion> lista2Especialidad_configuracion = new ArrayList<Especialidad_configuracion>();
	private List<Especialidad_configuracion> listaEspecialidades = new ArrayList<Especialidad_configuracion>();

	// DepartamentoEntidad
	private List<DepartamentoEntidad> listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();

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

	public List<Especialidad_configuracion> convert2Especialidad_configuracion() {
		for (Especialidad_configuracion esp : medico.getEspecialidads()) {
			lista2Especialidad_configuracion.add(esp);
		}
		return lista2Especialidad_configuracion;
	}

	@SuppressWarnings("unchecked")
	public void Source() {
		// Cargando todas las entidades segun las especialidades seleccionadas
		// del medico
		listaEntidades = entityManager
				.createQuery(
						"select distinct ent from Entidad_configuracion ent "
								+ "join ent.servicioInEntidads sie "
								+ "join ent.medicoInEntidads mie "
								+ "join mie.medico m "
								+ "join m.especialidads esp "
								+ "join esp.especialidadInEntidads espIe "
								+ "join espIe.entidad e "
								+ "where m.id=:idMedico " + "and e.id=ent.id")
				.setParameter("idMedico", this.id).getResultList();

		// Cargando por cada entidad encontrada los departamentos,servicios y
		// especialidades seleccionadas
		for (Entidad_configuracion ent : listaEntidades) {

			listaDepartamentos = entityManager
					.createQuery(
							"select dep from Departamento_configuracion dep "
									+ "join dep.servicios ser "
									+ "join ser.servicioInEntidads sie "
									+ "join sie.entidad ent "
									+ "join ent.medicoInEntidads mie "
									+ "join mie.medico m "
									+ "join m.especialidads esp "
									+ "where m.id=:idMedico "
									+ "and ent.id=:idEntidad "
									+ "and dep.id in (select distinct dep.id from Departamento_configuracion dep "
									+ "join dep.servicios serv "
									+ "join serv.servicioInEntidads sie "
									+ "join sie.servicio.especialidads esp "
									+ "join esp.especialidadInEntidads eie "
									+ "join eie.entidad ent "
									+ "join ent.medicoInEntidads mie "
									+ "join mie.medico m "
									+ "join m.especialidads esp "
									+ "where m.id=:idMedico "
									+ "and ent.id=:idEntidad)")
					.setParameter("idMedico", this.id)
					.setParameter("idEntidad", ent.getId()).getResultList();

			// Cargando los ServiciosInEntidades segun el medico y la entidad
			listaServ = entityManager
					.createQuery(
							"select ser from Departamento_configuracion dep "
									+ "join dep.servicios ser "
									+ "join ser.servicioInEntidads sie "
									+ "join sie.entidad ent "
									+ "join ent.medicoInEntidads mie "
									+ "join mie.medico m "
									+ "where m.id=:idMedico "
									+ "and ent.id=:idEntidad "
									+ "and dep.id in (select distinct dep.id from Departamento_configuracion dep "
									+ "join dep.servicios serv "
									+ "join serv.servicioInEntidads sie "
									+ "join sie.servicio.especialidads esp "
									+ "join esp.especialidadInEntidads eie "
									+ "join eie.entidad ent "
									+ "join ent.medicoInEntidads mie "
									+ "join mie.medico m "
									+ "where m.id=:idMedico "
									+ "and ent.id=:idEntidad)")
					.setParameter("idMedico", this.id)
					.setParameter("idEntidad", ent.getId()).getResultList();

			Dep_Serv_Especialidades departamentoServicioEsp = new Dep_Serv_Especialidades();

			for (int i = 0; i < listaServ.size(); i++) {
				departamentoServicioEsp.setServicio(listaServ.get(i));
				departamentoServicioEsp.setDepartamento(listaDepartamentos
						.get(i));

				// Cargo todas las especialidades segun la
				// entidad,departamentos,y el servicio del medico
				listaEspecialidades = entityManager
						.createQuery(
								"select esp from Departamento_configuracion dep "
										+ "join dep.departamentoInEntidads die "
										+ "join die.entidad ent "
										+ "join ent.servicioInEntidads sie "
										+ "join sie.servicio serv "
										+ "join serv.especialidads esp "
										+ "join esp.medicos m "
										+ "where m.id=:idMedico "
										+ "and ent.id=:idEntidad "
										+ "and dep.id=:idDepartamento "
										+ "and serv.id=:idServicio")
						.setParameter("idMedico", this.id)
						.setParameter("idEntidad", ent.getId())
						.setParameter("idDepartamento",
								listaDepartamentos.get(i).getId())
						.setParameter("idServicio", listaServ.get(i).getId())
						.getResultList();

				String l = new String();
				for (int j = 0; j < listaEspecialidades.size() - 1; j++) {
					l += listaEspecialidades.get(j).getNombre() + ", ";
				}
				l += listaEspecialidades.get(listaEspecialidades.size() - 1)
						.getNombre() + ", ";

				departamentoServicioEsp.setEspecialidades(l);

			}

		}

	}

	@SuppressWarnings("unchecked")
	public void servicioInEntidadConfiguracion() {
		listaEntidadServicios = entityManager
				.createQuery(
						"select ent from Departamento_configuracion dep, Entidad_configuracion ent where (dep.id, ent.id) in(select d.id, e.id from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.entidad e join sie.servicio.departamento d where u.id =:idUsuario and (sie.eliminado = false or sie.eliminado=null))")
				.setParameter("idUsuario", this.id).getResultList();
		listaDepartamentoServicios = entityManager
				.createQuery(
						"select dep from Departamento_configuracion dep, Entidad_configuracion ent where (dep.id, ent.id) in(select d.id, e.id from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.entidad e join sie.servicio.departamento d where u.id =:idUsuario and (sie.eliminado = false or sie.eliminado=null))")
				.setParameter("idUsuario", this.id).getResultList();
		listaDepartamentoEntidad = new ArrayList<DepartamentoEntidad>();

		for (int i = 0; i < listaDepartamentoServicios.size(); i++) {
			listaDepartamentoEntidad.add(new DepartamentoEntidad(
					listaDepartamentoServicios.get(i), listaEntidadServicios
							.get(i)));
		}

		// listaDepartamentoEntidadControler = new
		// ListadoControler<DepartamentoEntidad>(listaDepartamentoEntidad);

	}

	public String convert2StringEspecialidades(MedicoInEntidad_configuracion med) {
		this.especialidades.clear();
		this.especialidades.addAll(med.getEspecialidads());

		String lista = "";
		if (especialidades.size() != 0) {
			for (int i = 0; i < especialidades.size() - 1; i++) {
				lista += especialidades.get(i).getEspecialidad().getNombre()
						+ ", ";
			}
			lista += especialidades.get(especialidades.size() - 1)
					.getEspecialidad().getNombre();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public String servicioConfiguracion(Long idDepartamento, Long idEntidad) {
		List<String> l = entityManager
				.createQuery(
						"select distinct sie.servicio.nombre from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario and sie.entidad.id =:idEntidad and sie.servicio.departamento.id =:idDepartamento")
				.setParameter("idUsuario", this.id)
				.setParameter("idEntidad", idEntidad)
				.setParameter("idDepartamento", idDepartamento).getResultList();
		String lista = "";
		for (int i = 0; i < l.size() - 1; i++) {
			lista += l.get(i) + ", ";
		}
		lista += l.get(l.size() - 1);
		return lista;
	}

	// Propiedades

	public Medico_configuracion getMedico() {
		return medico;
	}

	public void setMedico(Medico_configuracion medico) {
		this.medico = medico;
	}

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
	}

	public List<Especialidad_configuracion> getLista2Especialidad_configuracion() {
		return lista2Especialidad_configuracion;
	}

	public void setLista2Especialidad_configuracion(
			List<Especialidad_configuracion> lista2Especialidad_configuracion) {
		this.lista2Especialidad_configuracion = lista2Especialidad_configuracion;
	}

	public List<Entidad_configuracion> getListaEntidadServicios() {
		return listaEntidadServicios;
	}

	public void setListaEntidadServicios(
			List<Entidad_configuracion> listaEntidadServicios) {
		this.listaEntidadServicios = listaEntidadServicios;
	}

	public List<Departamento_configuracion> getListaDepartamentoServicios() {
		return listaDepartamentoServicios;
	}

	public void setListaDepartamentoServicios(
			List<Departamento_configuracion> listaDepartamentoServicios) {
		this.listaDepartamentoServicios = listaDepartamentoServicios;
	}

	public List<DepartamentoEntidad> getListaDepartamentoEntidad() {
		return listaDepartamentoEntidad;
	}

	public void setListaDepartamentoEntidad(
			List<DepartamentoEntidad> listaDepartamentoEntidad) {
		this.listaDepartamentoEntidad = listaDepartamentoEntidad;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public List<TipoFuncionario_configuracion> getTipoFuncionarioTarget() {
		return tipoFuncionarioTarget;
	}

	public void setTipoFuncionarioTarget(
			List<TipoFuncionario_configuracion> tipoFuncionarioTarget) {
		this.tipoFuncionarioTarget = tipoFuncionarioTarget;
	}

	public List<CargoFuncionario_configuracion> getCargoTarget() {
		return cargoTarget;
	}

	public void setCargoTarget(List<CargoFuncionario_configuracion> cargoTarget) {
		this.cargoTarget = cargoTarget;
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

	public void setId(Long id) {
		this.id = id;

		cultura();

		medico = entityManager.find(Medico_configuracion.class, this.id);
		servicioInEntidadConfiguracion();

		rolsSource.clear();
		rolsSource.addAll(medico.getUsuario().getRoles());

		tipoFuncionarioTarget.clear();
		tipoFuncionarioTarget.addAll(medico.getUsuario().getTipoFuncionarios());

		cargoTarget.clear();
		cargoTarget.addAll(medico.getUsuario().getCargoFuncionarios());

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(medico.getUsuario().getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
			}
		}

		this.mediInEnt = entityManager
				.createQuery(
						"select distinct m from MedicoInEntidad_configuracion m join m.especialidads esp where m.medico.id =:idMedico and esp <> null")
				.setParameter("idMedico", medico.getId()).getResultList();
	}

	public List<MedicoInEntidad_configuracion> getMediInEnt() {
		return mediInEnt;
	}

	public void setMediInEnt(List<MedicoInEntidad_configuracion> mediInEnt) {
		this.mediInEnt = mediInEnt;
	}

	public List<EspecialidadInEntidad_configuracion> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(
			List<EspecialidadInEntidad_configuracion> especialidades) {
		this.especialidades = especialidades;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

}
