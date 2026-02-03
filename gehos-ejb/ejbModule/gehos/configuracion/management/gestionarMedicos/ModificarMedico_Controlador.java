package gehos.configuracion.management.gestionarMedicos;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.MedicoInEntidad_configuracion;
import gehos.configuracion.management.entity.MedicoRecipeEntidad_Configuracion;
import gehos.configuracion.management.entity.Medico_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.TipoRecipe_Configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarReferencias.ListadoControler;
import gehos.configuracion.management.gestionarUsuario.Cultura;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarMedico_Controlador")
@Scope(ScopeType.CONVERSATION)
public class ModificarMedico_Controlador {

	@In
	EntityManager entityManager;
	@In
	IBitacora bitacora;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	// Usuario
	private Usuario_configuracion usuario_conf = new Usuario_configuracion();
	private Boolean desiguales = true, userRep = false;
	private String username, verfpass = new String(), userold,
			contrasennaString = new String();
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Date fechaGraduacion;
	private Boolean keepPassword = true; // no cambiar la contrasena

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Tipo Funcionario
	private TipoFuncionario_configuracion tipoFuncionario;
	private List<TipoFuncionario_configuracion> tipoFuncionarioTarget = new ArrayList<TipoFuncionario_configuracion>();
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();

	// Cargos
	private List<CargoFuncionario_configuracion> cargoTarget;
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// Medico
	private Long id;
	private Medico_configuracion medico = new Medico_configuracion();
	private String name = new String();
	private String matriculaColegio = new String(),
			matriculaMinisterio = new String(), matriculaMold, matriculaCold;
	private Boolean matriculaC = false, mariculaM = false;
	private Date fechaold;

	// Entidades
	private List<Entidad_configuracion> listEntMIE = new ArrayList<Entidad_configuracion>();
	private List<String> listaNomEntMedInEnt = new ArrayList<String>();
	private List<String> listaComboxEntity = new ArrayList<String>();
	private int sizeEntitySource, sizeEntityTarget;

	// Departamentos
	private List<DepartamentoInEntidad_configuracion> departamentoSorce = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> departamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();

	// Servicios
	private List<ServicioInEntidad_configuracion> serAux = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> serAuxRespaldo = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioTarget = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> serviciosRemoved = new ArrayList<ServicioInEntidad_configuracion>();

	// Especialidades
	private int posEspInEn;
	private Long idEspInEn;
	private List<EspecialidadInEntidad_configuracion> resultado = new ArrayList<EspecialidadInEntidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadas = new ArrayList<EspecialidadInEntidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadasRespaldo = new ArrayList<EspecialidadInEntidad_configuracion>();
	private List<Especialidad_configuracion> especialidadesTarget = new ArrayList<Especialidad_configuracion>();
	private List<Especialidad_configuracion> especialidades = new ArrayList<Especialidad_configuracion>();
	
	//Lista que contiene la copia de las especialidades de graduacion del medico
	private List<Especialidad_configuracion> especialidadesTargetCopy = new ArrayList<Especialidad_configuracion>();
	
	private List<MedicoInEntidad_configuracion> listMIE = new ArrayList<MedicoInEntidad_configuracion>();

	private List<Entidad_configuracion> entidadesOrigen = new ArrayList<Entidad_configuracion>();
	private ListadoControler<Entidad_configuracion> listaControlerEntityOrigen;
	// private List<TipoRecipe_Configuracion> recipesTarget = new
	// ArrayList<TipoRecipe_Configuracion>();
	// private List<TipoRecipe_Configuracion> recipesSource = new
	// ArrayList<TipoRecipe_Configuracion>();

	private Long entidadSeleccionada;
	private Hashtable<String, MedicoRecipeEntidad_Configuracion> originales = new Hashtable<String, MedicoRecipeEntidad_Configuracion>();
	private Hashtable<Long, List<TipoRecipe_Configuracion>> recipesPorEntidad = new Hashtable<Long, List<TipoRecipe_Configuracion>>();

	public void seleccionarEntidad(Long id) {
		entidadSeleccionada = id;
	}

	public List<TipoRecipe_Configuracion> getRecipesTarget() {
		return recipesPorEntidad.get(this.entidadSeleccionada);
	}

	public void setRecipesTarget(List<TipoRecipe_Configuracion> recipesTarget) {
		this.recipesPorEntidad.remove(this.entidadSeleccionada);
		this.recipesPorEntidad.put(entidadSeleccionada, recipesTarget);
	}

	@SuppressWarnings("unchecked")
	public List<TipoRecipe_Configuracion> getRecipesSource() {
		List<TipoRecipe_Configuracion> list = entityManager.createQuery(
				"from TipoRecipe_Configuracion").getResultList();
		if (this.getRecipesTarget() != null)
			list.removeAll(this.getRecipesTarget());
		return list;
	}

	public void setRecipesSource(List<TipoRecipe_Configuracion> recipesSource) {
		// this.recipesSource = recipesSource;
	}

	public void actualizarRecipes() {
		// if (recipesPorEntidad.containsKey(entidadSeleccionada))
		// recipesPorEntidad.remove(entidadSeleccionada);
		// recipesPorEntidad.put(entidadSeleccionada, this.getRecipesTarget());
	}

	private List<TipoRecipe_Configuracion> allrecipestypesList;
	private Hashtable<Long, TipoRecipe_Configuracion> allrecipestypes = new Hashtable<Long, TipoRecipe_Configuracion>();

	// Metodos
	@SuppressWarnings("unchecked")
	public void source() {
		allrecipestypesList = entityManager.createQuery(
				"from TipoRecipe_Configuracion").getResultList();
		allrecipestypes.clear();
		for (TipoRecipe_Configuracion trp : allrecipestypesList) {
			allrecipestypes.put(trp.getId(), trp);
		}

		List<MedicoRecipeEntidad_Configuracion> configs = entityManager
				.createQuery(
						"from MedicoRecipeEntidad_Configuracion mr where mr.id_medico = :idmed")
				.setParameter("idmed", medico.getId()).getResultList();
		recipesPorEntidad.clear();
		originales.clear();
		for (MedicoRecipeEntidad_Configuracion mre : configs) {
			String key = mre.getId_entidad() + "-" + mre.getId_medico() + "-"
					+ mre.getId_recipe();
			originales.put(key, mre);

			if (!recipesPorEntidad.containsKey(mre.getId_entidad()))
				recipesPorEntidad.put(mre.getId_entidad(),
						new ArrayList<TipoRecipe_Configuracion>());
			recipesPorEntidad.get(mre.getId_entidad()).add(
					this.allrecipestypes.get(mre.getId_recipe()));
		}		

		cultura();

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(medico.getUsuario().getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
			}
		}

		for (Especialidad_configuracion espT : medico.getEspecialidads()) {
			especialidadesTarget.add(espT);
			
			//se hace la copia de las especialidades de graduacion del medicos
			especialidadesTargetCopy.add(espT);
		}
		

		especialidades.clear();
		especialidades = entityManager
				.createQuery(
						"select espec from Especialidad_configuracion espec where espec.eliminado=false or espec.eliminado=null")
				.getResultList();
		for (int i = 0; i < especialidades.size(); i++) {
			boolean encontrada = false;
			for (Especialidad_configuracion esp : especialidadesTarget) {
				if (esp.getId().equals(especialidades.get(i).getId())) {
					encontrada = true;
					break;
				}
			}
			if (encontrada) {
				especialidades.remove(i);
				i--;
			}
		}

		for (ServicioInEntidad_configuracion servT : medico.getUsuario()
				.getServicioInEntidads()) {
			listaServicioTarget.add(servT);
		}

		serAux = entityManager
				.createQuery(
						"select m.usuario.servicioInEntidads from Medico_configuracion m where m.id =:medicoID")
				.setParameter("medicoID", medico.getId()).getResultList();
		serAuxRespaldo.addAll(serAux);

		cargarListaEspecialidadesSelecc();
		CargarPosiblesEspecialidades();

		/**
		 * @author yurien 24/04/2014
		 * Se agrega la restriccion para que busque las entidades
		 * configuradas para el anillo actual de la instancia
		 * **/
		// Se cargan todas las entidades
		listaComboxEntity = entityManager.createQuery(
				"select ent.nombre from Entidad_configuracion ent where "
				+ "ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				+ "ent.perteneceARhio = true "
				+ "order by ent.nombre asc")
				.getResultList();

		// Se eliminan las que esten en las entidades de los servicionInEntidad
		// del medico
		for (int i = 0; i < listaComboxEntity.size(); i++) {
			for (int j = 0; j < listaNomEntMedInEnt.size(); j++) {
				if (listaComboxEntity.get(i).equals(listaNomEntMedInEnt.get(j))) {
					listaComboxEntity.remove(i);
					i--;
					break;
				}
			}
		}

		this.sizeEntitySource = listaComboxEntity.size();
		this.sizeEntityTarget = listaNomEntMedInEnt.size();

		departamentoTarget = entityManager
				.createQuery(
						"select distinct die from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.servicio.departamento.departamentoInEntidads die where u.id =:idUsuario and die.entidad.nombre = sie.entidad.nombre")
				.setParameter("idUsuario", usuario_conf.getId())
				.getResultList();

		// se cargan los departamentos source y target y se quitan de los source
		// los que estan en los target
		for (String nomEnt : listaNomEntMedInEnt) {

			List<DepartamentoInEntidad_configuracion> l = entityManager
					.createQuery(
							"select e from Departamento_configuracion d "
									+ "join d.departamentoInEntidads e "
									+ "where e.entidad.nombre =:nombreEntity and d.esClinico=true")
					.setParameter("nombreEntity", nomEnt).getResultList();

			// agrego los depInEnt de cada entidad
			departamentoSorce.addAll(l);
		}

		for (int i = 0; i < departamentoSorce.size(); i++) {
			for (DepartamentoInEntidad_configuracion depT : departamentoTarget) {
				if (departamentoSorce.get(i).equals(depT)) {
					departamentoSorce.remove(i);
					i--;
					break;
				}
			}
		}

		// roles
		rolsTarget.clear();
		rolsTarget.addAll(usuario_conf.getRoles());
		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager
				.createQuery(
						"from Role_configuracion r where r.eliminado=false or r.eliminado=null")
				.getResultList();
		for (int i = 0; i < roles.size(); i++) {
			if (estaRol(roles.get(i).getId()) == -1)
				rolsSource.add(roles.get(i));
		}

		// funcionario
		tipoFuncionarioTarget.clear();
		tipoFuncionarioTarget.addAll(usuario_conf.getTipoFuncionarios());
		if (cargoTarget == null) {
			cargoTarget = new ArrayList<CargoFuncionario_configuracion>();
			cargoTarget.clear();
			cargoTarget.addAll(usuario_conf.getCargoFuncionarios());
		}

		List<TipoFuncionario_configuracion> tipo_func = new ArrayList<TipoFuncionario_configuracion>();
		tipo_func.clear();
		tipo_func = entityManager.createQuery(
				"from TipoFuncionario_configuracion t").getResultList();
		tipoFuncionarioSource.clear();

		// validacion de tipo funcionario
		for (int i = 0; i < tipo_func.size(); i++) {
			if (estaTipoFincionario(tipo_func.get(i).getId()) == -1)
				tipoFuncionarioSource.add(tipo_func.get(i));
		}
		// validacion cargo
		cargoSource.clear();
		for (TipoFuncionario_configuracion tipo_func_it : tipoFuncionarioTarget) {
			cargoSource.addAll(tipo_func_it.getCargoFuncionarios());
		}
		cargoSource.removeAll(cargoTarget);

		cargarServiciosSource();
		
		/**
		 * @author yurien 24/04/2014
		 * Se agrega la restriccion para que busque las entidades
		 * configuradas para el anillo actual de la instancia
		 * **/
		String query = "select entity from Entidad_configuracion entity where "
				+ "entity.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} ";
//				+ "entity.perteneceARhio = true ";
		
		boolean first = true;
		for (String name : listaNomEntMedInEnt) {
			if(first){
				query +="and (";
				query += "entity.nombre = '" + name + "' ";
				first = false;
			}
			else{
				query += "or entity.nombre = '" + name + "' ";
			}
		}
		if(!listaNomEntMedInEnt.isEmpty())
			query+=") ";
		
		query += "order by entity.nombre asc";
		
		
		this.entidadesOrigen = entityManager.createQuery(query)
						.getResultList();
		

		if (this.listaControlerEntityOrigen == null)
			this.listaControlerEntityOrigen = new ListadoControler<Entidad_configuracion>(
					this.entidadesOrigen);

	}

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

	private int estaRol(Long id) {
		for (int i = 0; i < rolsTarget.size(); i++) {
			if (rolsTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	private int estaTipoFincionario(Long id) {
		for (int i = 0; i < tipoFuncionarioTarget.size(); i++) {
			if (tipoFuncionarioTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	public void listaCargosPosibles() {
		cargoSource.clear();

		for (int i = 0; i < tipoFuncionarioTarget.size(); i++) {
			tipoFuncionario = entityManager.find(
					TipoFuncionario_configuracion.class, tipoFuncionarioTarget
							.get(i).getId());
			cargoSource.addAll(tipoFuncionario.getCargoFuncionarios());
		}
		validarPosiblesCargo();
	}

	public void validarPosiblesCargo() {
		boolean cargoAsignado = false;
		for (int j = 0; j < cargoTarget.size(); j++) {
			for (int i = 0; i < cargoSource.size(); i++) {
				if (cargoSource.get(i).equals(cargoTarget.get(j))) {
					cargoSource.remove(i);
					i--;
					cargoAsignado = true;
					break;
				}

			}
			if (cargoAsignado == false) {
				cargoTarget.remove(j);
				j--;
			}

			else
				cargoAsignado = false;
		}
	}

	public void verificarCargos() {
	}

	@SuppressWarnings("unchecked")
	public void cargarDepartamentos() {// falta cuando quito una entidad, tengo
										// que buscar todos los servicios in
										// Entidad que tengan ese entidad y
										// borrarlos, ademas de modificar la
										// consulta y listar solo los q tengan
										// eliminado = false
		// Se cargan todos los departamentos de la lista de las entidades

		if (listaNomEntMedInEnt.size() < sizeEntityTarget) {
			int cant = sizeEntityTarget - listaNomEntMedInEnt.size();
			List<String> entidadesChanged = new ArrayList<String>();

			int k = 1;
			while (cant > 0) {
				entidadesChanged.add(listaComboxEntity.get(listaComboxEntity
						.size() - k));
				k++;
				cant--;
			}

			for (int i = 0; i < serAux.size(); i++) {
				for (int j = 0; j < entidadesChanged.size(); j++) {
					if (serAux.get(i).getEntidad().getNombre()
							.equals(entidadesChanged.get(j))) {
						serviciosRemoved.add(serAux.get(i));
						serAux.remove(i);
					}
				}
			}

			listaNomEntMedInEnt.removeAll(entidadesChanged);
			sizeEntityTarget--;

		}

		if (listaNomEntMedInEnt.size() > sizeEntityTarget) {
			sizeEntityTarget++;
		}

		departamentoSorce.clear();
		for (String nomEnt : listaNomEntMedInEnt) {

			List<DepartamentoInEntidad_configuracion> l = entityManager
					.createQuery(
							"select e from Departamento_configuracion d "
									+ "join d.departamentoInEntidads e "
									+ "where e.entidad.nombre =:nombreEntity and d.esClinico=true")
					.setParameter("nombreEntity", nomEnt).getResultList();

			// agrego los depInEnt de cada entidad
			departamentoSorce.addAll(l);
		}

		// validacion de los source y target dep

		for (int i = 0; i < departamentoTarget.size(); i++) {
			boolean esta = false;
			for (int j = 0; j < departamentoSorce.size(); j++) {
				if (departamentoTarget.get(i).equals(departamentoSorce.get(j))) {
					esta = true;
					departamentoSorce.remove(j);
					j--;
					break;
				}
			}
			if (!esta) {
				departamentoTarget.remove(i);
				i--;
			}
		}
		/**
		 * @author yurien 24/04/2014
		 * Se agrega la restriccion para que busque las entidades
		 * configuradas para el anillo actual de la instancia
		 * **/
		String query = "select entity from Entidad_configuracion entity where "
				+ "entity.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} ";
//				+ "entity.perteneceARhio = true ";
		boolean first = true;
		for (String name : listaNomEntMedInEnt) {
			if(first){
				query+= "and (";
				query += "entity.nombre = '" + name + "' ";
				first = false;
			}
			else{
				query += "or entity.nombre = '" + name + "' ";
			}
		}
		if(!listaNomEntMedInEnt.isEmpty())
			query +=") ";
		
		query += "order by entity.nombre asc";
		
		
		this.entidadesOrigen = entityManager.createQuery(query)
						.getResultList();
		

		this.listaControlerEntityOrigen = new ListadoControler<Entidad_configuracion>(
				this.entidadesOrigen);

		cargarServiciosSource();
	}

	public boolean buscarEntity(Long idEnt) {
		boolean esta = false;
		for (Entidad_configuracion ent : listEntMIE) {
			if (ent.getId().equals(idEnt)) {
				esta = true;
			}
		}
		return esta;
	}

	@SuppressWarnings("unchecked")
	public void cargarListaEspecialidadesSelecc() {

		for (ServicioInEntidad_configuracion ser : serAux) {
			if (!buscarEntity(ser.getEntidad().getId())) {
				listEntMIE.add(ser.getEntidad());
				listaNomEntMedInEnt.add(ser.getEntidad().getNombre());
			}
		}

		for (Entidad_configuracion ent : listEntMIE) {
			List<MedicoInEntidad_configuracion> aux = entityManager
					.createQuery(
							"select mie from MedicoInEntidad_configuracion mie where mie.medico.id =:idMed and mie.entidad.id =:idEnt")
					.setParameter("idMed", this.medico.getId())
					.setParameter("idEnt", ent.getId()).getResultList();
			if (aux.size() != 0) {
				listMIE.add(aux.get(0));
			}
		}

		for (MedicoInEntidad_configuracion medIE : listMIE) {
			for (EspecialidadInEntidad_configuracion esp : medIE
					.getEspecialidads()) {
				especialidadesSeleccionadas.add(esp);
				especialidadesSeleccionadasRespaldo.add(esp);
			}
		}
	}

	public void verificarEspecialidad() {

	}

	@SuppressWarnings("unchecked")
	public void CargarPosiblesEspecialidades() {
		resultado.clear();
		@SuppressWarnings("unused")
		List<Especialidad_configuracion> especialidadesDadoServicio = new ArrayList<Especialidad_configuracion>();

		List<EspecialidadInEntidad_configuracion> preresultado = new ArrayList<EspecialidadInEntidad_configuracion>();

		for (ServicioInEntidad_configuracion ser : listaServicioTarget) {
			preresultado
					.addAll(entityManager
							.createQuery(
									"select eie from ServicioInEntidad_configuracion sie join sie.servicio s join s.especialidads e join e.especialidadInEntidads eie where eie.entidad.id =:idEntidad and sie.entidad.id =:idEntidad and sie.id=:idServicio")
							.setParameter("idEntidad", ser.getEntidad().getId())
							.setParameter("idServicio", ser.getId())
							.getResultList());
		}

		for (EspecialidadInEntidad_configuracion especialidadInEntidad : preresultado) {
			boolean encontrada = false;
			for (Especialidad_configuracion especialidad : especialidadesTarget) {
				encontrada = especialidadInEntidad.getEspecialidad().getId()
						.equals(especialidad.getId());
				if (encontrada)
					break;
			}
			if (encontrada) {
				resultado.add(especialidadInEntidad);
			}
		}

		boolean asignada = false;
		for (int i = 0; i < especialidadesSeleccionadas.size(); i++) {
			for (int j = 0; j < resultado.size(); j++) {
				if (resultado.get(j).equals(especialidadesSeleccionadas.get(i))) {
					resultado.remove(j);
					j--;
					asignada = true;
					break;
				}
			}
			if (!asignada) {
				especialidadesSeleccionadas.remove(i);
				i--;
			} else {
				asignada = false;
			}
		}
	}

	public void bajarServer() {

	}

	@SuppressWarnings("unchecked")
	public void cargarServiciosSource() {

		listaServicioSource.clear();
		List<ServicioInEntidad_configuracion> auxiliar = new ArrayList<ServicioInEntidad_configuracion>();

		for (int i = 0; i < departamentoTarget.size(); i++) {
			auxiliar = entityManager
					.createQuery(
							"select s from ServicioInEntidad_configuracion s "
									+ "where s.servicio.departamento.id =:idDepartamento "
									+ "and s.entidad.id =:idEntidad and s.servicio.servicioFisico = true and s.servicio.departamento.esClinico = true ")
					.setParameter("idDepartamento",
							departamentoTarget.get(i).getDepartamento().getId())
					.setParameter("idEntidad",
							departamentoTarget.get(i).getEntidad().getId())
					.getResultList();

			if (auxiliar.size() != 0) {
				listaServicioSource.addAll(auxiliar);
			}
		}

		for (int i = 0; i < listaServicioTarget.size(); i++) {
			boolean esta = false;
			for (int j = 0; j < listaServicioSource.size(); j++) {
				if (listaServicioTarget.get(i).getServicio()
						.equals(listaServicioSource.get(j).getServicio())) {
					listaServicioSource.remove(j);
					j--;
					esta = true;
					break;
				}
			}
			if (!esta) {
				listaServicioTarget.remove(i);
				i--;
			}
		}
		CargarPosiblesEspecialidades();
	}

	public boolean buscarEspecialidadInResultado(Long idEsp) {
		for (int i = 0; i < especialidadesSeleccionadas.size(); i++) {
			if (especialidadesSeleccionadas.get(i).getId().equals(idEsp)) {
				posEspInEn = i;
				return true;
			}
		}
		return false;
	}

	public void asignarEspecialidades() {
		if (buscarEspecialidadInResultado(idEspInEn)) {
			especialidadesSeleccionadas.remove(posEspInEn);

			EspecialidadInEntidad_configuracion espAux = (EspecialidadInEntidad_configuracion) entityManager
					.createQuery(
							"select e from EspecialidadInEntidad_configuracion e where e.id =:idEsp")
					.setParameter("idEsp", idEspInEn).getSingleResult();
			MedicoInEntidad_configuracion medAux = (MedicoInEntidad_configuracion) entityManager
					.createQuery(
							"select m from MedicoInEntidad_configuracion m where m.medico.id =:idMed and m.entidad.id =:idEnt")
					.setParameter("idMed", this.medico.getId())
					.setParameter("idEnt", espAux.getEntidad().getId())
					.getSingleResult();
			medAux.getEspecialidads().remove(espAux);

			medAux = entityManager.merge(medAux);
			entityManager.flush();
		} else {
			EspecialidadInEntidad_configuracion aux = entityManager.find(
					EspecialidadInEntidad_configuracion.class, idEspInEn);
			especialidadesSeleccionadas.add(aux);
			entityManager.flush();
		}
	}

	@SuppressWarnings("unchecked")
	public List<String> cargarListaEntity() {
		return entityManager.createQuery(
				"select ent.nombre from Entidad_configuracion ent")
				.getResultList();
	}

	public void eliminarEspecialidad(Long idEspec) {
		Especialidad_configuracion aux = entityManager.find(
				Especialidad_configuracion.class, idEspec);
		especialidadesTarget.remove(aux);
		especialidades.add(aux);

	}

	public void subirPhoto() {
		// para acceder a la direccion deseada
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath("resources/modCommon/userphotos");
		rootpath += "/" + this.usuario_conf.getUsername() + ".png";

		try {
			// escribo el fichero primero
			File file = new File(rootpath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(this.data);

			// le hago el procesamiento
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: originalImage.getType();

			// le cambio de tamaï¿½o
			BufferedImage risizeImagePng = resizeImage(originalImage, type);

			// la sobreescribo
			// RenderedImage renderedImage = new RenderedImage();
			ImageIO.write(risizeImagePng, "png", new File(rootpath));

		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage resizeImage(BufferedImage originalImage,
			int type) {
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();

		return resizedImage;
	}

	public void modificarConfRecipes() {
		Hashtable<String, Long> finales = new Hashtable<String, Long>();
		Long idmedico = medico.getId();
		for (Entry<Long, List<TipoRecipe_Configuracion>> entry : recipesPorEntidad
				.entrySet()) {
			Long identidad = entry.getKey();
			for (TipoRecipe_Configuracion tiporec : entry.getValue()) {
				Long idtiporecipe = tiporec.getId();
				String hashKey = identidad + "-" + idmedico + "-"
						+ idtiporecipe;
				finales.put(hashKey, idtiporecipe);
				if (!originales.containsKey(hashKey)) {
					MedicoRecipeEntidad_Configuracion medicoRecipeEntidad = new MedicoRecipeEntidad_Configuracion();
					medicoRecipeEntidad.setId_entidad(identidad);
					medicoRecipeEntidad.setId_medico(idmedico);
					medicoRecipeEntidad.setId_recipe(idtiporecipe);
					entityManager.persist(medicoRecipeEntidad);
					entityManager.flush();
				}
			}
		}
		for (Entry<String, MedicoRecipeEntidad_Configuracion> entry : originales
				.entrySet()) {
			if (!finales.containsKey(entry.getKey())) {
				MedicoRecipeEntidad_Configuracion mre = entityManager
						.merge(entry.getValue());
				entityManager.remove(mre);
				entityManager.flush();
			}
		}
	}

	@SuppressWarnings({ "unchecked" })
	public String modificar() throws NoSuchAlgorithmException {

		// Validacion------------(los campos requeridos, caracteres incorrectosy
		// longitud, se hace akki enel server pq se marea la pag cuando
		// refresca)

		boolean error = false;

		if (username.equals("")) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			String us = username.toLowerCase();
			if(us.endsWith("@pdvsa.com")){
				us = us.substring(0, us.length() - 10);
			}
			if (!us.toString().matches("^(\\s*[a-z0-9]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("nick",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (username.length() > 25) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		if (culturaSelec.equals("")) {
			facesMessages.addToControlFromResourceBundle("cultura",
					Severity.ERROR, "Valor requerido");
			error = true;
		}

		if (name.equals("")) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (!name.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("name",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (name.length() > 25) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		if (usuario_conf.getPrimerApellido().length() > 25) {
			facesMessages.addToControlFromResourceBundle("papellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		if (!usuario_conf.getPrimerApellido().equals("")) {
			if (!usuario_conf.getPrimerApellido().toString()
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("papellido",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (!usuario_conf.getSegundoApellido().equals("")) {
			if (!usuario_conf.getSegundoApellido().toString()
					.matches("^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("sapellido",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (usuario_conf.getSegundoApellido().length() > 25) {
			facesMessages.addToControlFromResourceBundle("sapellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		// if (matriculaMinisterio.equals("")) {
		// facesMessages.addToControlFromResourceBundle("matriculaMinist",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// } else {
		if (!matriculaMinisterio.equals("")
				&& !matriculaMinisterio.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matriculaMinist",
					Severity.ERROR, "Caracteres incorrectos");
			error = true;
		}
		// }

		if (matriculaMinisterio.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matriculaMinist",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		// if (matriculaColegio.equals("")) {
		// facesMessages.addToControlFromResourceBundle("matriculaColegio",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// } else {
		if (!matriculaColegio.equals("")
				&& !matriculaColegio.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matriculaColegio",
					Severity.ERROR, "Caracteres incorrectos");
			error = true;
		}
		// }

		if (matriculaColegio.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matriculaColegio",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			error = true;
		}

		// if (fechaGraduacion == null) {
		// facesMessages.addToControlFromResourceBundle("fechagrad",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// }

		if (!keepPassword) {
			if (contrasennaString.equals("")) {
				facesMessages.addToControlFromResourceBundle("pass",
						Severity.ERROR, "Valor requerido");
				error = true;
			} else {
				if (contrasennaString.length() > 25) {
					facesMessages.addToControlFromResourceBundle("pass",
							Severity.ERROR,
							"Cantidad de caracteres entre 1 y 25");
					error = true;
				}
			}

			if (verfpass.equals("")) {
				facesMessages.addToControlFromResourceBundle("verfpass",
						Severity.ERROR, "Valor requerido");
				error = true;
			} else {
				if (verfpass.length() > 25) {
					facesMessages.addToControlFromResourceBundle("verfpass",
							Severity.ERROR,
							"Cantidad de caracteres entre 1 y 25");
					error = true;
				}
			}
		}
		
		if (especialidadesTarget.size() == 0) {
			facesMessages.addToControlFromResourceBundle("ListShuttleEsp",
					Severity.ERROR, "Valor requerido");
			error = true;
		}

		if (error) {
			return "";
		}

		this.userRep = false;
		this.mariculaM = false;
		this.matriculaC = false;

		userRep = false;
		desiguales = this.contrasennaString.equals(verfpass);

		/*
		 * if (!keepPassword) {
		 * 
		 * if (!desiguales) { facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; } }
		 */

		List<Usuario_configuracion> aux = entityManager
				.createQuery(
						"select usuario from Usuario_configuracion usuario where usuario.username =:username")
				.setParameter("username", username).getResultList();
		/*
		 * if (!username.equals(usuario_conf.getUsername())) { if (aux.size() !=
		 * 0) { userRep = true; facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; } }
		 */

		List<Medico_configuracion> medAuxM = entityManager
				.createQuery(
						"select medico from Medico_configuracion medico where medico.matriculaMinisterio =:matriculaMinisterio")
				.setParameter("matriculaMinisterio", matriculaMinisterio)
				.getResultList();

		/*
		 * if (!matriculaMinisterio.equals(matriculaMold)) { if (medAuxM.size()
		 * != 0) { mariculaM = true; facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; } }
		 */

		List<Medico_configuracion> medAux = entityManager
				.createQuery(
						"select medico from Medico_configuracion medico where medico.matriculaColegioMedico =:matriculaColegio")
				.setParameter("matriculaColegio", matriculaColegio)
				.getResultList();

		/*
		 * if (!matriculaColegio.equals(matriculaCold)) { if (medAux.size() !=
		 * 0) { matriculaC = true; facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; } }
		 */

		if ((!keepPassword && !desiguales)
				|| (!username.equals(usuario_conf.getUsername()) && aux.size() != 0)
				|| (!matriculaMinisterio.equals(matriculaMold) && medAuxM
						.size() != 0)
				|| (!matriculaColegio.equals(matriculaCold) && medAux.size() != 0)) {
			if (!username.equals(usuario_conf.getUsername())) {
				if (aux.size() != 0) {
					userRep = true;
				}
			}

			if (!matriculaMinisterio.equals(matriculaMold)) {
				if (medAuxM.size() != 0) {
					mariculaM = true;
				}
			}

			if (!matriculaColegio.equals(matriculaCold)) {
				if (medAux.size() != 0) {
					matriculaC = true;
				}
			}

			return "valor requerido";

		}

		usuario_conf.setPrimerApellido(usuario_conf.getPrimerApellido().trim());
		usuario_conf.setSegundoApellido(usuario_conf.getSegundoApellido()
				.trim());
		usuario_conf.setEliminado(false);
		usuario_conf.setNombre(name);
		usuario_conf.getRoles().clear();
		usuario_conf.getRoles().addAll(rolsTarget);
		this.usuario_conf.getTipoFuncionarios().clear();
		this.usuario_conf.getTipoFuncionarios().addAll(tipoFuncionarioTarget);
		this.usuario_conf.getCargoFuncionarios().clear();
		this.usuario_conf.getCargoFuncionarios().addAll(cargoTarget);

		if (!keepPassword) {
			if (!contrasennaString.equals(usuario_conf.getPassword())) {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(contrasennaString.getBytes());
				String md5pass = new String(Hex.encodeHex(digest.digest()));
				usuario_conf.setPassword(md5pass);
			}
		}

		Long cid = bitacora.registrarInicioDeAccion("Modificando médico..");

		usuario_conf.getServicioInEntidads().clear();
		// usuario_conf.getServicioInEntidads().addAll(serAux);
		usuario_conf.getServicioInEntidads().addAll(listaServicioTarget);
		usuario_conf.setEliminado(false);
		usuario_conf.setUsername(username);
		usuario_conf.setCid(cid);
		entityManager.persist(usuario_conf);

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getIdioma().equals(culturaSelec)) {
				usuario_conf.getProfile().setLocaleString(
						listaCultura.get(i).getLocalString());
			}
		}

		medico.setUsuario(usuario_conf);
		medico.setFechaGraduacion(fechaGraduacion);
		medico.setMatriculaColegioMedico(matriculaColegio);
		medico.setMatriculaMinisterio(matriculaMinisterio);
		medico.setEliminado(false);

		modificarConfRecipes();

		List<EspecialidadInEntidad_configuracion> diferencia = new ArrayList<EspecialidadInEntidad_configuracion>();
		for (EspecialidadInEntidad_configuracion espcDif : especialidadesSeleccionadasRespaldo) {
			boolean esta = false;
			for (EspecialidadInEntidad_configuracion espcSel : especialidadesSeleccionadas) {
				if (espcDif.equals(espcSel)) {
					esta = true;
				}
			}
			if (!esta) {
				diferencia.add(espcDif);
			}
		}

		for (EspecialidadInEntidad_configuracion dif : diferencia) {

			EspecialidadInEntidad_configuracion espAux = (EspecialidadInEntidad_configuracion) entityManager
					.createQuery(
							"select e from EspecialidadInEntidad_configuracion e where e.id =:idEsp")
					.setParameter("idEsp", dif.getId()).getSingleResult();
			MedicoInEntidad_configuracion medicoAux = (MedicoInEntidad_configuracion) entityManager
					.createQuery(
							"select m from MedicoInEntidad_configuracion m where m.medico.id =:idMed and m.entidad.id =:idEnt")
					.setParameter("idMed", this.medico.getId())
					.setParameter("idEnt", espAux.getEntidad().getId())
					.getSingleResult();
			medicoAux.getEspecialidads().remove(espAux);
			medicoAux.setCid(cid);
			medicoAux = entityManager.merge(medicoAux);
			medicoAux.setCid(cid);
			entityManager.flush();
		}

		for (Especialidad_configuracion esp : especialidadesTarget) {
			esp = entityManager.merge(esp);
			esp.setCid(cid);
		}


		/**
		 * @author yurien 07/07/2014 Se agrega esta linea para que cuando se
		 *         modifiquen las especialidades de graduacion del se actualice
		 *         la bd.
		 * 
		 * **/
		medico.getEspecialidads().removeAll(especialidadesTargetCopy);
		
		medico.getEspecialidads().addAll(especialidadesTarget);

		for (ServicioInEntidad_configuracion serv : listaServicioTarget) {
			serv = entityManager.merge(serv);
		}
		medico.getUsuario().getServicioInEntidads().addAll(listaServicioTarget);

		medico.setCid(cid);
		entityManager.persist(medico);

		for (EspecialidadInEntidad_configuracion espcInEnt : especialidadesSeleccionadas) {

			List<MedicoInEntidad_configuracion> mediInEnt = entityManager
					.createQuery(
							"select m from MedicoInEntidad_configuracion m where m.medico.id =:idMedico  and m.entidad.id =:idEntidad")
					.setParameter("idMedico", medico.getId())
					.setParameter("idEntidad", espcInEnt.getEntidad().getId())
					.getResultList();

			if (mediInEnt.size() == 0) {

				MedicoInEntidad_configuracion medicoInEntidad = new MedicoInEntidad_configuracion();
				medicoInEntidad.setMedico(medico);
				medicoInEntidad.setEntidad(espcInEnt.getEntidad());

				List<EspecialidadInEntidad_configuracion> espcInEntidad = new ArrayList<EspecialidadInEntidad_configuracion>();
				espcInEntidad.add(espcInEnt);
				medicoInEntidad.getEspecialidads().clear();
				medicoInEntidad.getEspecialidads().addAll(espcInEntidad);
				medicoInEntidad.setCid(cid);
				entityManager.persist(medicoInEntidad);
				entityManager.flush();
			} else {
				boolean esta = false;
				int pos = -1;
				List<EspecialidadInEntidad_configuracion> espcInEntidad = new ArrayList<EspecialidadInEntidad_configuracion>();
				MedicoInEntidad_configuracion old = (MedicoInEntidad_configuracion) entityManager
						.createQuery(
								"select m from MedicoInEntidad_configuracion m where m.medico.id =:idMedico  and m.entidad.id =:idEntidad ")
						.setParameter("idMedico", medico.getId())
						.setParameter("idEntidad",
								espcInEnt.getEntidad().getId())
						.getSingleResult();

				for (EspecialidadInEntidad_configuracion espIEinMIE : old
						.getEspecialidads()) {
					espcInEntidad.add(espIEinMIE);
				}

				for (int i = 0; i < espcInEntidad.size(); i++) {
					if (espcInEntidad.get(i).getId().equals(espcInEnt.getId())) {
						esta = true;
					}
				}

				if (!esta) {
					espcInEntidad.add(espcInEnt);
					old.getEspecialidads().addAll(espcInEntidad);
					old = entityManager.merge(old);
					entityManager.flush();
				}

			}

		}

		entityManager.flush();

		return "modificar";

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (this.id == null) {

			this.id = id;
			userRep = false;
			usuario_conf = entityManager.find(Usuario_configuracion.class,
					this.id);
			medico = entityManager.find(Medico_configuracion.class, this.id);
			matriculaMinisterio = medico.getMatriculaMinisterio();
			matriculaColegio = medico.getMatriculaColegioMedico();
			username = usuario_conf.getUsername();
			name = usuario_conf.getNombre();
			userold = username;
			// contrasennaString = usuario_conf.getPassword();
			// verfpass = usuario_conf.getPassword();
			matriculaCold = medico.getMatriculaColegioMedico();
			matriculaMold = medico.getMatriculaMinisterio();
			fechaold = medico.getFechaGraduacion();
			this.fechaGraduacion = medico.getFechaGraduacion();
			source();
		}

		// Propiedades

	}

	public Usuario_configuracion getUsuario_conf() {
		return usuario_conf;
	}

	public void setUsuario_conf(Usuario_configuracion usuario_conf) {
		this.usuario_conf = usuario_conf;
	}

	public Medico_configuracion getMedico() {
		return medico;
	}

	public void setMedico(Medico_configuracion medico) {
		this.medico = medico;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getMatriculaColegio() {
		return matriculaColegio;
	}

	public void setMatriculaColegio(String matriculaColegio) {
		this.matriculaColegio = matriculaColegio.trim();
	}

	public String getMatriculaMinisterio() {
		return matriculaMinisterio;
	}

	public void setMatriculaMinisterio(String matriculaMinisterio) {
		this.matriculaMinisterio = matriculaMinisterio.trim();
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public Boolean getMatriculaC() {
		return matriculaC;
	}

	public void setMatriculaC(Boolean matriculaC) {
		this.matriculaC = matriculaC;
	}

	public Boolean getMariculaM() {
		return mariculaM;
	}

	public void setMariculaM(Boolean mariculaM) {
		this.mariculaM = mariculaM;
	}

	public String getUserold() {
		return userold;
	}

	public void setUserold(String userold) {
		this.userold = userold;
	}

	public String getMatriculaMold() {
		return matriculaMold;
	}

	public void setMatriculaMold(String matriculaMold) {
		this.matriculaMold = matriculaMold;
	}

	public String getMatriculaCold() {
		return matriculaCold;
	}

	public void setMatriculaCold(String matriculaCold) {
		this.matriculaCold = matriculaCold;
	}

	public Date getFechaold() {
		return fechaold;
	}

	public void setFechaold(Date fechaold) {
		this.fechaold = fechaold;
	}

	public List<EspecialidadInEntidad_configuracion> getResultado() {
		return resultado;
	}

	public void setResultado(List<EspecialidadInEntidad_configuracion> resultado) {
		this.resultado = resultado;
	}

	public List<EspecialidadInEntidad_configuracion> getEspecialidadesSeleccionadas() {
		return especialidadesSeleccionadas;
	}

	public void setEspecialidadesSeleccionadas(
			List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadas) {
		this.especialidadesSeleccionadas = especialidadesSeleccionadas;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioSource() {
		return listaServicioSource;
	}

	public void setListaServicioSource(
			List<ServicioInEntidad_configuracion> listaServicioSource) {
		this.listaServicioSource = listaServicioSource;
	}

	public List<Especialidad_configuracion> getEspecialidadesTarget() {
		return especialidadesTarget;
	}

	public void setEspecialidadesTarget(
			List<Especialidad_configuracion> especialidadesTarget) {
		this.especialidadesTarget = especialidadesTarget;
	}

	public List<Especialidad_configuracion> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(
			List<Especialidad_configuracion> especialidades) {
		this.especialidades = especialidades;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioTarget() {
		return listaServicioTarget;
	}

	public void setListaServicioTarget(
			List<ServicioInEntidad_configuracion> listaServicioTarget) {
		this.listaServicioTarget = listaServicioTarget;
	}

	public Long getIdEspInEn() {
		return idEspInEn;
	}

	public void setIdEspInEn(Long idEspInEn) {
		this.idEspInEn = idEspInEn;
	}

	public int getPosEspInEn() {
		return posEspInEn;
	}

	public void setPosEspInEn(int posEspInEn) {
		this.posEspInEn = posEspInEn;
	}

	public List<EspecialidadInEntidad_configuracion> getEspecialidadesSeleccionadasRespaldo() {
		return especialidadesSeleccionadasRespaldo;
	}

	public void setEspecialidadesSeleccionadasRespaldo(
			List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadasRespaldo) {
		this.especialidadesSeleccionadasRespaldo = especialidadesSeleccionadasRespaldo;
	}

	public String getContrasennaString() {
		return contrasennaString;
	}

	public void setContrasennaString(String contrasennaString) {
		this.contrasennaString = contrasennaString;
	}

	public List<Entidad_configuracion> getListEntMIE() {
		return listEntMIE;
	}

	public void setListEntMIE(List<Entidad_configuracion> listEntMIE) {
		this.listEntMIE = listEntMIE;
	}

	public List<MedicoInEntidad_configuracion> getListMIE() {
		return listMIE;
	}

	public void setListMIE(List<MedicoInEntidad_configuracion> listMIE) {
		this.listMIE = listMIE;
	}

	public List<ServicioInEntidad_configuracion> getSerAux() {
		return serAux;
	}

	public void setSerAux(List<ServicioInEntidad_configuracion> serAux) {
		this.serAux = serAux;
	}

	public List<String> getListaNomEntMedInEnt() {
		return listaNomEntMedInEnt;
	}

	public void setListaNomEntMedInEnt(List<String> listaNomEntMedInEnt) {
		this.listaNomEntMedInEnt = listaNomEntMedInEnt;
	}

	public List<String> getListaComboxEntity() {
		return listaComboxEntity;
	}

	public void setListaComboxEntity(List<String> listaComboxEntity) {
		this.listaComboxEntity = listaComboxEntity;
	}

	public List<ServicioInEntidad_configuracion> getSerAuxRespaldo() {
		return serAuxRespaldo;
	}

	public void setSerAuxRespaldo(
			List<ServicioInEntidad_configuracion> serAuxRespaldo) {
		this.serAuxRespaldo = serAuxRespaldo;
	}

	public int getSizeEntitySource() {
		return sizeEntitySource;
	}

	public void setSizeEntitySource(int sizeEntitySource) {
		this.sizeEntitySource = sizeEntitySource;
	}

	public int getSizeEntityTarget() {
		return sizeEntityTarget;
	}

	public void setSizeEntityTarget(int sizeEntityTarget) {
		this.sizeEntityTarget = sizeEntityTarget;
	}

	public List<ServicioInEntidad_configuracion> getServiciosRemoved() {
		return serviciosRemoved;
	}

	public void setServiciosRemoved(
			List<ServicioInEntidad_configuracion> serviciosRemoved) {
		this.serviciosRemoved = serviciosRemoved;
	}

	public List<DepartamentoInEntidad_configuracion> getDepartamentoSorce() {
		return departamentoSorce;
	}

	public void setDepartamentoSorce(
			List<DepartamentoInEntidad_configuracion> departamentoSorce) {
		this.departamentoSorce = departamentoSorce;
	}

	public List<DepartamentoInEntidad_configuracion> getDepartamentoTarget() {
		return departamentoTarget;
	}

	public void setDepartamentoTarget(
			List<DepartamentoInEntidad_configuracion> departamentoTarget) {
		this.departamentoTarget = departamentoTarget;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public List<Role_configuracion> getRolsTarget() {
		return rolsTarget;
	}

	public void setRolsTarget(List<Role_configuracion> rolsTarget) {
		this.rolsTarget = rolsTarget;
	}

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
	}

	public TipoFuncionario_configuracion getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario_configuracion tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

	public List<TipoFuncionario_configuracion> getTipoFuncionarioTarget() {
		return tipoFuncionarioTarget;
	}

	public void setTipoFuncionarioTarget(
			List<TipoFuncionario_configuracion> tipoFuncionarioTarget) {
		this.tipoFuncionarioTarget = tipoFuncionarioTarget;
	}

	public List<TipoFuncionario_configuracion> getTipoFuncionarioSource() {
		return tipoFuncionarioSource;
	}

	public void setTipoFuncionarioSource(
			List<TipoFuncionario_configuracion> tipoFuncionarioSource) {
		this.tipoFuncionarioSource = tipoFuncionarioSource;
	}

	public List<CargoFuncionario_configuracion> getCargoTarget() {
		return cargoTarget;
	}

	public void setCargoTarget(List<CargoFuncionario_configuracion> cargoTarget) {
		this.cargoTarget = cargoTarget;
	}

	public List<CargoFuncionario_configuracion> getCargoSource() {
		return cargoSource;
	}

	public void setCargoSource(List<CargoFuncionario_configuracion> cargoSource) {
		this.cargoSource = cargoSource;
	}

	public String getCulturaSelec() {
		return culturaSelec;
	}

	public void setCulturaSelec(String culturaSelec) {
		this.culturaSelec = culturaSelec;
	}

	public List<Cultura> getListaCultura() {
		return listaCultura;
	}

	public void setListaCultura(List<Cultura> listaCultura) {
		this.listaCultura = listaCultura;
	}

	public String getNombrePhoto() {
		return nombrePhoto;
	}

	public void setNombrePhoto(String nombrePhoto) {
		this.nombrePhoto = nombrePhoto;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
	}

	public Date getFechaGraduacion() {
		return fechaGraduacion;
	}

	public void setFechaGraduacion(Date fechaGraduacion) {
		this.fechaGraduacion = fechaGraduacion;
	}

	public Boolean getKeepPassword() {
		return keepPassword;
	}

	public void setKeepPassword(Boolean keepPassword) {
		this.keepPassword = keepPassword;
	}

	public ListadoControler<Entidad_configuracion> getListaControlerEntityOrigen() {
		return listaControlerEntityOrigen;
	}

	public void setListaControlerEntityOrigen(
			ListadoControler<Entidad_configuracion> listaControlerEntityOrigen) {
		this.listaControlerEntityOrigen = listaControlerEntityOrigen;
	}

	public Long getEntidadSeleccionada() {
		return entidadSeleccionada;
	}

	public void setEntidadSeleccionada(Long entidadSeleccionada) {
		this.entidadSeleccionada = entidadSeleccionada;
	}

}
