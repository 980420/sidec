package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EspecialidadInEntidad_configuracion;
import gehos.configuracion.management.entity.Especialidad_configuracion;
import gehos.configuracion.management.entity.MedicoInEntidad_configuracion;
import gehos.configuracion.management.entity.Medico_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
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
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.hibernate.validator.Past;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("crearMedicoUserExist_Controlador")
@Scope(ScopeType.CONVERSATION)
public class CrearMedicoUserExist_Controlador {

	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	private Long id;

	// Datos usuario
	private Usuario_configuracion usuario_conf;
	private Boolean userRep = false, desiguales = true;
	private String username, contrasennaString = new String(),
			verfpass = new String(), name = new String(), userold;
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private Boolean keepPassword = true; // no cambiar la contrasena

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

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

	// Medico
	private Medico_configuracion medico = new Medico_configuracion();
	private Boolean matriculaCRep = false, mariculaMRep = false;
	private String matriculaColegio = new String(),
			matriculaMinisterio = new String();
	private Date fechaGraduacion;

	// Entidad
	private List<Entidad_configuracion> listEntityUserSIE = new ArrayList<Entidad_configuracion>();
	private List<String> listaNomEntityUserSIE = new ArrayList<String>();
	private List<String> entidadesSource = new ArrayList<String>();
	private List<String> entidadesTarget = new ArrayList<String>();
	private int sizeEntityTarget;

	// Departamentos
	private List<String> listaNomDep = new ArrayList<String>();
	private List<String> listaNomDepTarget = new ArrayList<String>();
	private List<DepartamentoInEntidad_configuracion> departamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> departamentoSorce = new ArrayList<DepartamentoInEntidad_configuracion>();

	// Servicios
	private List<ServicioInEntidad_configuracion> serAux = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioTarget = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> serAuxRespaldo = new ArrayList<ServicioInEntidad_configuracion>();

	// Especialidades
	private List<Especialidad_configuracion> especialidades = new ArrayList<Especialidad_configuracion>();
	private List<Especialidad_configuracion> especialidadesTarget = new ArrayList<Especialidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> resultado = new ArrayList<EspecialidadInEntidad_configuracion>();
	private List<EspecialidadInEntidad_configuracion> especialidadesSeleccionadas = new ArrayList<EspecialidadInEntidad_configuracion>();

	// Metodos
	@SuppressWarnings("unchecked")
	public void datosSource() {
		cultura();

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getLocalString()
					.equals(usuario_conf.getProfile().getLocaleString())) {
				culturaSelec = culturaSource.get(i).getIdioma();
			}
		}

		especialidades = entityManager
				.createQuery(
						"select espec from Especialidad_configuracion espec order by espec.nombre asc")
				.getResultList();

		serAux = entityManager
				.createQuery(
						"select u.servicioInEntidads from Usuario_configuracion u where u.id =:userID")
				.setParameter("userID", usuario_conf.getId()).getResultList();
		serAuxRespaldo.addAll(serAux);

		for (ServicioInEntidad_configuracion servT : usuario_conf
				.getServicioInEntidads()) {
			listaServicioTarget.add(servT);
		}

		for (ServicioInEntidad_configuracion ser : serAux) {
			if (!buscarEntity(ser.getEntidad().getId())) {
				listEntityUserSIE.add(ser.getEntidad());
				listaNomEntityUserSIE.add(ser.getEntidad().getNombre());
			}
		}

		// Se cargan todas las entidades
		this.entidadesSource = entityManager.createQuery(
				"select ent.nombre from Entidad_configuracion ent where ent.perteneceARhio = true order by ent.nombre asc")
				.getResultList();

		// Se eliminan las que esten en las entidades de los servicionInEntidad
		// del user
		for (int i = 0; i < entidadesSource.size(); i++) {
			for (int j = 0; j < listaNomEntityUserSIE.size(); j++) {
				if (entidadesSource.get(i).equals(listaNomEntityUserSIE.get(j))) {
					entidadesSource.remove(i);
					i--;
				}
			}
		}

		// roles
		rolsTarget.clear();
		rolsTarget.addAll(usuario_conf.getRoles());
		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager.createQuery(
				"from Role_configuracion r order by r.name").getResultList();
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
				"from TipoFuncionario_configuracion t order by t.valor")
				.getResultList();
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

		departamentoTarget = entityManager
				.createQuery(
						"select distinct die from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.servicio.departamento.departamentoInEntidads die where u.id =:idUsuario and die.entidad.nombre = sie.entidad.nombre")
				.setParameter("idUsuario", usuario_conf.getId())
				.getResultList();

		// se cargan los departamentos source y target y se quitan de los source
		// los que estan en los target
		for (String nomEnt : listaNomEntityUserSIE) {

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

		cargarServiciosSource();
	}

	// Para cargar la cultura del usuario exixtente
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

	// Metodo auxiliar que se usa para la validacion del source
	private int estaTipoFincionario(Long id) {
		for (int i = 0; i < tipoFuncionarioTarget.size(); i++) {
			if (tipoFuncionarioTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	// Metodo auxiliar que se usa para la validacion del source
	private int estaRol(Long id) {
		for (int i = 0; i < rolsTarget.size(); i++) {
			if (rolsTarget.get(i).getId().equals(id))
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
	public void cargarDepartamentos() {
		// Se cargan todos los departamentos de la lista de las entidades

		if (listaNomEntityUserSIE.size() < sizeEntityTarget) {
			int cant = sizeEntityTarget - listaNomEntityUserSIE.size();
			List<String> entidadesChanged = new ArrayList<String>();

			int k = 1;
			while (cant > 0) {
				entidadesChanged.add(entidadesSource.get(entidadesSource.size()
						- k));
				k++;
				cant--;
			}

			for (int i = 0; i < serAux.size(); i++) {
				for (int j = 0; j < entidadesChanged.size(); j++) {
					if (serAux.get(i).getEntidad().getNombre()
							.equals(entidadesChanged.get(j))) {
						serAux.remove(i);
					}
				}
			}

			listaNomEntityUserSIE.removeAll(entidadesChanged);
			sizeEntityTarget -= cant;
		}

		if (listaNomEntityUserSIE.size() > sizeEntityTarget) {
			int cant = listaNomEntityUserSIE.size() - sizeEntityTarget;
			sizeEntityTarget += cant;
		}

		departamentoSorce.clear();
		for (String nomEnt : listaNomEntityUserSIE) {

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

	public void bajarServer() {

	}

	@SuppressWarnings("unchecked")
	public void CargarPosiblesEspecialidades() {

		resultado.clear();

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
			/*for (Especialidad_configuracion especialidad : especialidadesTarget) {
				encontrada = especialidadInEntidad.getEspecialidad().getId()
						.equals(especialidad.getId());
				if (encontrada)
					break;
			}*/
			if (!encontrada) {
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

	public boolean buscarEntity(Long idEnt) {
		boolean esta = false;
		for (Entidad_configuracion ent : listEntityUserSIE) {
			if (ent.getId().equals(idEnt)) {
				esta = true;
			}
		}
		return esta;
	}

	public void verificarEspecialidad() {

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

	@SuppressWarnings({ "unchecked", "deprecation" })
	@End
	public String crear() throws NoSuchAlgorithmException {

		List<Medico_configuracion> medicoAux = (List<Medico_configuracion>) entityManager
				.createQuery(
						"select medico from Medico_configuracion medico where medico.id=:id")
				.setParameter("id", usuario_conf.getId()).getResultList();

		if (medicoAux.size() != 0) {
			try {
				throw new Exception(
						"El usuario ha sido asociado a un médico previamente");
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle("buttonCrear",
						Severity.ERROR,
						"El usuario ha sido asociado a un médico previamente");
			}
		}

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
			if (!usuario_conf.getPrimerApellido().toString()
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
		// facesMessages.addToControlFromResourceBundle("matriculaMinisterio",
		// Severity.ERROR, "Valor requerido");
		// error = true;
		// } else {
		if (!matriculaMinisterio.equals("")
				&& !matriculaMinisterio.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matriculaMinisterio",
					Severity.ERROR, "Caracteres incorrectos");
			error = true;
		}
		// }

		if (matriculaMinisterio.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matriculaMinisterio",
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

		// if (fechaGraduacion == null) {
		// facesMessages.addToControlFromResourceBundle("fecha",
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
		}

		if (error) {
			return "";
		}

		this.userRep = false;
		mariculaMRep = false;
		matriculaCRep = false;

		desiguales = this.contrasennaString.equals(verfpass);
		/*
		 * if (!desiguales) { facesMessages.add(new
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

		if (!desiguales
				|| (!username.equals(usuario_conf.getUsername()) && aux.size() != 0)
				|| medAuxM.size() != 0 || medAux.size() != 0) {
			if (medAuxM.size() != 0) {
				mariculaMRep = true;
			}

			if (medAux.size() != 0) {
				matriculaCRep = true;
			}

			if (!username.equals(usuario_conf.getUsername())) {
				if (aux.size() != 0) {
					userRep = true;
				}
			}

			return "valor requerido";
		}

		if (!keepPassword) {
			if (!contrasennaString.equals(usuario_conf.getPassword())) {
				MessageDigest digest = MessageDigest.getInstance("MD5");
				digest.update(contrasennaString.getBytes());
				String md5pass = new String(Hex.encodeHex(digest.digest()));
				usuario_conf.setPassword(md5pass);
			}
		}

		medico.setEliminado(false);
		medico.setFechaGraduacion(fechaGraduacion);
		medico.setMatriculaMinisterio(this.matriculaMinisterio);
		medico.setMatriculaColegioMedico(this.matriculaColegio);
		this.usuario_conf.getServicioInEntidads().clear();
		for (ServicioInEntidad_configuracion serv : listaServicioTarget) {
			serv = entityManager.merge(serv);
		}
		this.usuario_conf.getServicioInEntidads().addAll(listaServicioTarget);
		this.usuario_conf.getRoles().clear();
		this.usuario_conf.getRoles().addAll(rolsTarget);
		this.usuario_conf.setUsername(username);
		this.usuario_conf.setNombre(name);
		this.usuario_conf.getTipoFuncionarios().clear();
		this.usuario_conf.getTipoFuncionarios().addAll(tipoFuncionarioTarget);
		this.usuario_conf.getCargoFuncionarios().clear();
		this.usuario_conf.getCargoFuncionarios().addAll(cargoTarget);
		this.usuario_conf.setPrimerApellido(this.usuario_conf
				.getPrimerApellido().trim());
		this.usuario_conf.setSegundoApellido(this.usuario_conf
				.getSegundoApellido().trim());

		if (!this.nombrePhoto.equals("")) {
			this.subirPhoto();
		}

		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
				usuario_conf.getProfile().setLocaleString(
						culturaSource.get(i).getLocalString());
			}
		}

		entityManager.merge(this.usuario_conf);
		medico.setUsuario(this.usuario_conf);

		for (Especialidad_configuracion esp : especialidadesTarget) {
			esp = entityManager.merge(esp);
		}

		medico.getEspecialidads().addAll(especialidadesTarget);
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
				entityManager.persist(old);
				entityManager.flush();
			}
		}

		entityManager.flush();

		return "crear";
	}

	// Propiedades
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

	public List<Role_configuracion> getRolsSource() {
		return rolsSource;
	}

	public void setRolsSource(List<Role_configuracion> rolsSource) {
		this.rolsSource = rolsSource;
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

	public List<String> getEntidadesSource() {
		return entidadesSource;
	}

	public void setEntidadesSource(List<String> entidadesSource) {
		this.entidadesSource = entidadesSource;
	}

	public List<String> get() {
		return entidadesTarget;
	}

	public void setEntidadesTarget(List<String> entidadesTarget) {
		this.entidadesTarget = entidadesTarget;
	}

	public List<ServicioInEntidad_configuracion> getSerAux() {
		return serAux;
	}

	public void setSerAux(List<ServicioInEntidad_configuracion> serAux) {
		this.serAux = serAux;
	}

	public List<Entidad_configuracion> getListEntityUserSIE() {
		return listEntityUserSIE;
	}

	public void setListEntityUserSIE(
			List<Entidad_configuracion> listEntityUserSIE) {
		this.listEntityUserSIE = listEntityUserSIE;
	}

	public List<String> getListaNomEntityUserSIE() {
		return listaNomEntityUserSIE;
	}

	public void setListaNomEntityUserSIE(List<String> listaNomEntityUserSIE) {
		this.listaNomEntityUserSIE = listaNomEntityUserSIE;
	}

	public List<String> getListaNomDep() {
		return listaNomDep;
	}

	public void setListaNomDep(List<String> listaNomDep) {
		this.listaNomDep = listaNomDep;
	}

	public List<String> getListaNomDepTarget() {
		return listaNomDepTarget;
	}

	public void setListaNomDepTarget(List<String> listaNomDepTarget) {
		this.listaNomDepTarget = listaNomDepTarget;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioTarget() {
		return listaServicioTarget;
	}

	public void setListaServicioTarget(
			List<ServicioInEntidad_configuracion> listaServicioTarget) {
		this.listaServicioTarget = listaServicioTarget;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioSource() {
		return listaServicioSource;
	}

	public void setListaServicioSource(
			List<ServicioInEntidad_configuracion> listaServicioSource) {
		this.listaServicioSource = listaServicioSource;
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

	public List<ServicioInEntidad_configuracion> getSerAuxRespaldo() {
		return serAuxRespaldo;
	}

	public void setSerAuxRespaldo(
			List<ServicioInEntidad_configuracion> serAuxRespaldo) {
		this.serAuxRespaldo = serAuxRespaldo;
	}

	public int getSizeEntityTarget() {
		return sizeEntityTarget;
	}

	public void setSizeEntityTarget(int sizeEntityTarget) {
		this.sizeEntityTarget = sizeEntityTarget;
	}

	public List<String> getEntidadesTarget() {
		return entidadesTarget;
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
			username = usuario_conf.getUsername();
			name = usuario_conf.getNombre();
			userold = username;
			/*
			 * contrasennaString = usuario_conf.getPassword(); verfpass =
			 * usuario_conf.getPassword();
			 */
			datosSource();

		}
	}

	public Boolean getMatriculaCRep() {
		return matriculaCRep;
	}

	public void setMatriculaCRep(Boolean matriculaCRep) {
		this.matriculaCRep = matriculaCRep;
	}

	public Boolean getMariculaMRep() {
		return mariculaMRep;
	}

	public void setMariculaMRep(Boolean mariculaMRep) {
		this.mariculaMRep = mariculaMRep;
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

	public List<Role_configuracion> getRolsTarget() {
		return rolsTarget;
	}

	public void setRolsTarget(List<Role_configuracion> rolsTarget) {
		this.rolsTarget = rolsTarget;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username.trim();
	}

	public String getContrasennaString() {
		return contrasennaString;
	}

	public void setContrasennaString(String contrasennaString) {
		this.contrasennaString = contrasennaString;
	}

	public String getVerfpass() {
		return verfpass;
	}

	public void setVerfpass(String verfpass) {
		this.verfpass = verfpass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name.trim();
	}

	public Boolean getUserRep() {
		return userRep;
	}

	public void setUserRep(Boolean userRep) {
		this.userRep = userRep;
	}

	public String getUserold() {
		return userold;
	}

	public void setUserold(String userold) {
		this.userold = userold;
	}

	public Boolean getDesiguales() {
		return desiguales;
	}

	public void setDesiguales(Boolean desiguales) {
		this.desiguales = desiguales;
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

	public TipoFuncionario_configuracion getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario_configuracion tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
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

	public String getNombrePhoto() {
		return nombrePhoto;
	}

	public void setNombrePhoto(String nombrePhoto) {
		this.nombrePhoto = nombrePhoto;
	}

	public Boolean getKeepPassword() {
		return keepPassword;
	}

	public void setKeepPassword(Boolean keepPassword) {
		this.keepPassword = keepPassword;
	}

	@Past
	public Date getFechaGraduacion() {
		return fechaGraduacion;
	}

	public void setFechaGraduacion(Date fechaGraduacion) {
		this.fechaGraduacion = fechaGraduacion;
	}

}
