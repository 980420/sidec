package gehos.configuracion.management.gestionarMedicos;

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

import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.datoslab.entity.DatosLaborales;
import gehos.comun.datoslab.entity.Persona;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;

import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.MedicoInEntidad_configuracion;
import gehos.configuracion.management.entity.MedicoRecipeEntidad_Configuracion;
import gehos.configuracion.management.entity.Medico_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoRecipe_Configuracion;

import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.gestionarReferencias.ListadoControler;
import gehos.configuracion.management.gestionarUsuario.Cultura;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.Length;
import org.hibernate.validator.NotEmpty;
import org.hibernate.validator.Past;
import org.hibernate.validator.Pattern;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("medicoUserCreate_Cotrolador")
@Scope(ScopeType.CONVERSATION)
public class MedicoUserCreate_Cotrolador {

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
	private String username = new String(), pass = new String(),
			verfpass = new String(), name = new String();
	private Boolean desiguales = true, userRep = false;
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Date fechaGaduacion;

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Tipos funcionario
	private TipoFuncionario_configuracion tipoFuncionario;
	private List<TipoFuncionario_configuracion> tipoFuncionarioTarget = new ArrayList<TipoFuncionario_configuracion>();
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();

	// Cargos funcionadio
	private long idCargo;
	private int posCargo;
	private List<CargoFuncionario_configuracion> cargoTarget = new ArrayList<CargoFuncionario_configuracion>();
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

	// Medico
	private Medico_configuracion medico = new Medico_configuracion();
	private String matriculaColegio = new String(),
			matriculaMinisterio = new String();
	private Boolean matriculaC = false, mariculaM = false;
	private Long id, idEspInEn;
	private int posEspInEn;

	// Entidad
	private String entidadSelecc;
	private List<String> entidadesSource = new ArrayList<String>();
	private List<String> entidadesTarget = new ArrayList<String>();
	
	// Departamentos
	private List<String> listaDep = new ArrayList<String>();
	private List<DepartamentoInEntidad_configuracion> departamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> departamentoSorce = new ArrayList<DepartamentoInEntidad_configuracion>();

	// Servicios
	private List<ServicioInEntidad_configuracion> listaServicioSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioTarget = new ArrayList<ServicioInEntidad_configuracion>();

	// Especialidades
	private List<Especialidad_configuracion> especialidades = new ArrayList<Especialidad_configuracion>();
	private List<Especialidad_configuracion> especialidadesTarget = new ArrayList<Especialidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> resultado = new ArrayList<EspecialidadInEntidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadas = new ArrayList<EspecialidadInEntidad_configuracion>();
	
	private Long entidadSeleccionada;
	private List<Entidad_configuracion> entidadesOrigen = new ArrayList<Entidad_configuracion>();
	private Hashtable<Long, List<TipoRecipe_Configuracion>> recipesPorEntidad = new Hashtable<Long, List<TipoRecipe_Configuracion>>();
	private ListadoControler<Entidad_configuracion> listaControlerEntityOrigen;
	
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

	// Metodos
	@SuppressWarnings("unchecked")
	@Create
	public void source() {
		cultura();
		especialidades = entityManager
				.createQuery(
						"select espec from Especialidad_configuracion espec where espec.eliminado=false or espec.eliminado=null"
								+ " order by espec.nombre asc").getResultList();

		/**
		 * @author yurien 28/03/2014
		 * Se agrega la nueva restriccion para que muestre las entidades 
		 * que pertenecen al anillo configurado
		 * **/
		this.entidadesSource = entityManager
				.createQuery(
						"select ent.nombre from Entidad_configuracion ent "
						+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//						+ "where ent.perteneceARhio = true "
						+ "order by ent.nombre asc")
				.getResultList();

		this.rolsSource = entityManager
				.createQuery(
						"select r from Role_configuracion r "
								+ "where r.eliminado = false or r.eliminado = null order by r.name asc")
				.getResultList();

		tipoFuncionarioSource = entityManager
				.createQuery(
						"select t from TipoFuncionario_configuracion t order by t.valor asc")
				.getResultList();
		
		/**
		 * @author yurien 28/03/2014
		 * Se agrega la nueva restriccion para que muestre las entidades 
		 * que pertenecen al anillo configurado
		 * **/
		this.entidadesOrigen = entityManager
		.createQuery(
				"select ent from Entidad_configuracion ent "
				+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				+ "where ent.perteneceARhio = true "
				+ "order by ent.nombre asc")
		.getResultList();
		

		if (this.listaControlerEntityOrigen == null)
			this.listaControlerEntityOrigen = new ListadoControler<Entidad_configuracion>(
					this.entidadesOrigen);
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
	public void cargarPosiblesEspec() {
		resultado.clear();
		List<EspecialidadInEntidad_configuracion> preresultado = new ArrayList<EspecialidadInEntidad_configuracion>();

		for (ServicioInEntidad_configuracion ser : listaServicioTarget) {
			preresultado
					.addAll(entityManager
							.createQuery(
									"select eie from ServicioInEntidad_configuracion sie join sie.servicio s join s.especialidads e join e.especialidadInEntidads eie where eie.entidad.id =:idEntidad and sie.entidad.id =:idEntidad and sie.id=:idServicio and e.eliminado=false")
							.setParameter("idEntidad", ser.getEntidad().getId())
							.setParameter("idServicio", ser.getId())
							.getResultList());
		}

		for (EspecialidadInEntidad_configuracion especialidadInEntidad : preresultado) {
			boolean encontrada = false;
			for (Especialidad_configuracion especialidad : especialidadesTarget) {
				encontrada = especialidadInEntidad.getEspecialidad().getId() == especialidad
						.getId();
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

	@SuppressWarnings("unchecked")
	public void cargarDepartamentosSource() {

		departamentoSorce.clear();
		for (String nomEnt : entidadesTarget) {

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

		cargarServiciosSource();
	}

	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		culturaSource = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			culturaSource.add(c);
			lista.add(c.cultura());
		}
		return lista;
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

		cargarPosiblesEspec();
	}

	public boolean buscarEspecialidadInResultado(Long idEsp) {
		for (int i = 0; i < especialidadesSeleccionadas.size(); i++) {
			if (especialidadesSeleccionadas.get(i).getId() == idEsp) {
				posEspInEn = i;
				return true;
			}
		}
		return false;
	}

	public void asignarEspecialidades() {
		if (buscarEspecialidadInResultado(idEspInEn)) {
			especialidadesSeleccionadas.remove(posEspInEn);
			entityManager.flush();
		} else {
			EspecialidadInEntidad_configuracion aux = entityManager.find(
					EspecialidadInEntidad_configuracion.class, idEspInEn);
			especialidadesSeleccionadas.add(aux);
			entityManager.flush();
		}
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
	
	public void SalvarRecipes() {
		for(Entry<Long, List<TipoRecipe_Configuracion>> entry: recipesPorEntidad.entrySet()){
			Long identidad = entry.getKey();
			for(TipoRecipe_Configuracion tiporec : entry.getValue()){
				MedicoRecipeEntidad_Configuracion medicoRecipeEntidad = new MedicoRecipeEntidad_Configuracion();
				medicoRecipeEntidad.setId_entidad(identidad);
				medicoRecipeEntidad.setId_medico(medico.getId());
				medicoRecipeEntidad.setId_recipe(tiporec.getId());
				entityManager.persist(medicoRecipeEntidad);				
			}			
		}
		entityManager.flush();
	}
		

	@SuppressWarnings({ "unchecked", "deprecation" })
	public String crear() throws NoSuchAlgorithmException {

		// Validacion

		boolean error = false;

		if (username.equals("")) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (!username.toString().toLowerCase().matches("^(\\s*[a-z0-9]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("nick",
						Severity.ERROR, "Caracteres incorrectos");
				error = true;
			}
		}

		if (username.length() > 25) {
			facesMessages.addToControlFromResourceBundle("nick",
					Severity.ERROR, "El máximo de caracteres es: 25");
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
					Severity.ERROR, "El máximo de caracteres es: 25");
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
		} else {
			facesMessages.addToControlFromResourceBundle("papellido",
					Severity.ERROR, "Valor requerido");
			error = true;
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
					Severity.ERROR, "El máximo de caracteres es: 25");
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
					Severity.ERROR, "El máximo de caracteres es: 25");
			error = true;
		}

		// if (fechaGaduacion == null) {
		// facesMessages.addToControlFromResourceBundle("fechagrad",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// }

		if (pass.equals("")) {
			facesMessages.addToControlFromResourceBundle("pass",
					Severity.ERROR, "Valor requerido");
			error = true;
		} else {
			if (pass.length() > 25) {
				facesMessages.addToControlFromResourceBundle("pass",
						Severity.ERROR, "El máximo de caracteres es: 25");
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
						Severity.ERROR, "El máximo de caracteres es: 25");
				error = true;
			}
		}
		
		if (especialidadesTarget.size() == 0) {
			facesMessages.addToControlFromResourceBundle("ListShuttleEspc",
					Severity.ERROR, "Valor requerido");
			error = true;
		}
		
		
		
		if (error) {
			return "";
		}

		userRep = false;
		mariculaM = false;
		matriculaC = false;
		desiguales = this.pass.equals(verfpass);
		/*
		 * if (!desiguales) { facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; }
		 */

		List<Usuario_configuracion> aux = entityManager
				.createQuery(
						"select usuario from Usuario_configuracion usuario where usuario.username =:username")
				.setParameter("username", username).getResultList();

		/*
		 * if (aux.size() != 0) { userRep = true; facesMessages.add(new
		 * FacesMessage("Existen datos vacíos o incorrectos")); return
		 * "valor requerido"; }
		 */

		List<Medico_configuracion> medAuxM = new ArrayList<Medico_configuracion>();
		if (!matriculaMinisterio.equals("")) {
			medAuxM = entityManager
					.createQuery(
							"select medico from Medico_configuracion medico where medico.matriculaMinisterio =:matriculaMinisterio")
					.setParameter("matriculaMinisterio", matriculaMinisterio)
					.getResultList();
		}
		List<Medico_configuracion> medAux = new ArrayList<Medico_configuracion>();
		if (!matriculaColegio.equals("")) {
			medAux = entityManager
					.createQuery(
							"select medico from Medico_configuracion medico where medico.matriculaColegioMedico =:matriculaColegio")
					.setParameter("matriculaColegio", matriculaColegio)
					.getResultList();
		}
		if (!desiguales || aux.size() != 0 || medAuxM.size() != 0
				|| medAux.size() != 0) {
			if (aux.size() != 0) {
				userRep = true;
			}

			if (medAuxM.size() != 0) {
				mariculaM = true;
			}

			if (medAux.size() != 0) {
				matriculaC = true;
			}

			return "valor requerido";

		}

		usuario_conf.setUsername(username);
		usuario_conf.setPrimerApellido(usuario_conf.getPrimerApellido().trim());
		usuario_conf.setSegundoApellido(usuario_conf.getSegundoApellido()
				.trim());
		usuario_conf.setNombre(name);
		usuario_conf.getRoles().addAll(rolsTarget);
		usuario_conf.getTipoFuncionarios().clear();
		usuario_conf.getTipoFuncionarios().addAll(tipoFuncionarioTarget);
		usuario_conf.getCargoFuncionarios().addAll(cargoTarget);
		usuario_conf.setEliminado(false);

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		Long cid = bitacora.registrarInicioDeAccion("Creando nuevo médico");

		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(pass.getBytes());
		String md5pass = new String(Hex.encodeHex(digest.digest()));
		usuario_conf.setPassword(md5pass);

		Profile_configuracion perfil = new Profile_configuracion();
		perfil.setEliminado(false);
		//perfil.setLocaleString("es");
		perfil.setTheme("alas-verde");
		perfil.setTreeLikeMenu(true);
		perfil.setUsuario(usuario_conf);
		usuario_conf.setProfile(perfil);
		usuario_conf.setCid(cid);
		
		Persona userpersona = new Persona();
		userpersona.setApellido1(usuario_conf.getPrimerApellido());
		userpersona.setApellido2(usuario_conf.getSegundoApellido());
		userpersona.setCedula(usuario_conf.getCedula());
		userpersona.setCid(cid);
		userpersona.setEliminado(false);
		userpersona.setFechaNacimiento(usuario_conf.getFechaNacimiento());
		userpersona.setNombres(usuario_conf.getNombre());
		//userpersona.setIdSexo(3L);
		entityManager.persist(userpersona);
		usuario_conf.setPersona(userpersona);

		entityManager.persist(usuario_conf);

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
				perfil.setLocaleString(culturaSource.get(i).getLocalString());
			}
		}

		perfil.setCid(cid);
		entityManager.persist(perfil);

		medico.setFechaGraduacion(this.fechaGaduacion);
		medico.setUsuario(usuario_conf);
		medico.setId(usuario_conf.getId());
		medico.setMatriculaColegioMedico(matriculaColegio);
		medico.setMatriculaMinisterio(matriculaMinisterio);

		for (Especialidad_configuracion esp : especialidadesTarget) {
			esp = entityManager.merge(esp);
		}

		medico.getEspecialidads().addAll(especialidadesTarget);

		for (ServicioInEntidad_configuracion serv : listaServicioTarget) {
			serv = entityManager.merge(serv);
		}

		medico.getUsuario().getServicioInEntidads().addAll(listaServicioTarget);
		medico.setEliminado(false);
		
		

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
				medicoInEntidad.setEliminado(false);

				List<EspecialidadInEntidad_configuracion> espcInEntidad = new ArrayList<EspecialidadInEntidad_configuracion>();
				espcInEntidad.add(espcInEnt);
				medicoInEntidad.getEspecialidads().clear();
				medicoInEntidad.getEspecialidads().addAll(espcInEntidad);
				medicoInEntidad.setCid(cid);
				entityManager.persist(medicoInEntidad);
				entityManager.flush();
			} else {
				List<EspecialidadInEntidad_configuracion> espcInEntidad = new ArrayList<EspecialidadInEntidad_configuracion>();
				espcInEntidad.add(espcInEnt);
				MedicoInEntidad_configuracion old = (MedicoInEntidad_configuracion) entityManager
						.createQuery(
								"select m from MedicoInEntidad_configuracion m where m.medico.id =:idMedico  and m.entidad.id =:idEntidad ")
						.setParameter("idMedico", medico.getId())
						.setParameter("idEntidad",
								espcInEnt.getEntidad().getId())
						.getSingleResult();
				old.getEspecialidads().addAll(espcInEntidad);
				old.setEliminado(false);
				old.setCid(cid);
				entityManager.persist(old);
				entityManager.flush();
			}
		}

		entityManager.flush();
		SalvarRecipes();

		return "crear";

	}

	// Propiedades

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
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

	public List<Especialidad_configuracion> getEspecialidades() {
		return especialidades;
	}

	public void setEspecialidades(
			List<Especialidad_configuracion> especialidades) {
		this.especialidades = especialidades;
	}

	public List<Especialidad_configuracion> getEspecialidadesTarget() {
		return especialidadesTarget;
	}

	public void setEspecialidadesTarget(
			List<Especialidad_configuracion> especialidadesTarget) {
		this.especialidadesTarget = especialidadesTarget;
	}

	public String getEntidadSelecc() {
		return entidadSelecc;
	}

	public void setEntidadSelecc(String entidadSelecc) {
		this.entidadSelecc = entidadSelecc;
	}

	public List<String> getListaDep() {
		return listaDep;
	}

	public void setListaDep(List<String> listaDep) {
		this.listaDep = listaDep;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioSource() {
		return listaServicioSource;
	}

	public void setListaServicioSource(
			List<ServicioInEntidad_configuracion> listaServicioSource) {
		this.listaServicioSource = listaServicioSource;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioTarget() {
		return listaServicioTarget;
	}

	public void setListaServicioTarget(
			List<ServicioInEntidad_configuracion> listaServicioTarget) {
		this.listaServicioTarget = listaServicioTarget;
	}

	public List<EspecialidadInEntidad_configuracion> getResultado() {
		return resultado;
	}

	public void setResultado(List<EspecialidadInEntidad_configuracion> resultado) {
		this.resultado = resultado;
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

	public List<EspecialidadInEntidad_configuracion> getEspecialidadesSeleccionadas() {
		return especialidadesSeleccionadas;
	}

	public void setEspecialidadesSeleccionadas(
			List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadas) {
		this.especialidadesSeleccionadas = especialidadesSeleccionadas;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public List<String> getEntidadesSource() {
		return entidadesSource;
	}

	public void setEntidadesSource(List<String> entidadesSource) {
		this.entidadesSource = entidadesSource;
	}

	public List<String> getEntidadesTarget() {
		return entidadesTarget;
	}

	public void setEntidadesTarget(List<String> entidadesTarget) {
		this.entidadesTarget = entidadesTarget;
	}

	public List<DepartamentoInEntidad_configuracion> getDepartamentoTarget() {
		return departamentoTarget;
	}

	public void setDepartamentoTarget(
			List<DepartamentoInEntidad_configuracion> departamentoTarget) {
		this.departamentoTarget = departamentoTarget;
	}

	public List<DepartamentoInEntidad_configuracion> getDepartamentoSorce() {
		return departamentoSorce;
	}

	public void setDepartamentoSorce(
			List<DepartamentoInEntidad_configuracion> departamentoSorce) {
		this.departamentoSorce = departamentoSorce;
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

	public long getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(int idCargo) {
		this.idCargo = idCargo;
	}

	public int getPosCargo() {
		return posCargo;
	}

	public void setPosCargo(int posCargo) {
		this.posCargo = posCargo;
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

	public List<Cultura> getCulturaSource() {
		return culturaSource;
	}

	public void setCulturaSource(List<Cultura> culturaSource) {
		this.culturaSource = culturaSource;
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

	public Date getFechaGaduacion() {
		return fechaGaduacion;
	}

	public void setFechaGaduacion(Date fechaGaduacion) {
		this.fechaGaduacion = fechaGaduacion;
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
