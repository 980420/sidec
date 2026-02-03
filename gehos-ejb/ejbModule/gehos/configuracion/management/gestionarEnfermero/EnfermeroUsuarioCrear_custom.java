package gehos.configuracion.management.gestionarEnfermero;

import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.EnfermeraInEntidad;
import gehos.configuracion.management.entity.Enfermera_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("enfermeroUsuarioCrear_custom")
@Scope(ScopeType.CONVERSATION)
public class EnfermeroUsuarioCrear_custom {

	// user data
	private String contrasenna = "";
	private String nombre = "";
	private String primerApellido = "";
	private String segundoApellido = "";
	private String cedula = "";
	private String pasaporte = "";
	private String direccionParticular = "";
	private String telefono = "";
	private String url_foto = "";
	private Date fechaNacimiento;

	// enfermero
	private String matriculaColegioEnfermeria = "";

	// validations
	private Boolean user_used = false;// validar que el nombre de usuario no
										// exista
	private Boolean pass_desigual = false;// validar que las constrasenas sean
											// iguales

	private String tabSelect = "usuario";
	private String oldPassword = ""; // valida si cambio la contrasena
	private String verificarPassword = ""; // repetir la contrasena

	// depurar
	List<ServicioInEntidad_configuracion> listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
	List<DepartamentoInEntidad_configuracion> listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
	List<Entidad_configuracion> listaE = new ArrayList<Entidad_configuracion>();

	// Servicios
	Entidad_configuracion entidad;
	List<Entidad_configuracion> listaEntidadSource = new ArrayList<Entidad_configuracion>();
	List<Entidad_configuracion> listaEntidadTarget = new ArrayList<Entidad_configuracion>();

	List<DepartamentoInEntidad_configuracion> listaDepartamentoSource = new ArrayList<DepartamentoInEntidad_configuracion>();
	List<DepartamentoInEntidad_configuracion> listaDepartamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();

	private List<ServicioInEntidad_configuracion> listaServicioInEntidadSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioInEntidadTarget = new ArrayList<ServicioInEntidad_configuracion>();

	// Roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// Tipos funcionario
	private TipoFuncionario_configuracion tipoFuncionario;
	private List<TipoFuncionario_configuracion> tipoFuncionarioTarget = new ArrayList<TipoFuncionario_configuracion>();
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();

	// Cargos funcionadio
	private int posCargo;
	private Long idCargo;
	private List<CargoFuncionario_configuracion> cargoTarget = new ArrayList<CargoFuncionario_configuracion>();
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

	// Usuario
	private Long cid = -1l;
	private String nick;
	private String nombrePhoto = "";
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private boolean nom, ced, fnac, username, pass, verpass,
			func_referenciado = false;
	private Usuario_configuracion usuario = new Usuario_configuracion();

	// validaciones
	private boolean userExist = false; // valida si nombre de usuario (username)
										// ya existe
	private boolean passwordCheck = false; // valida que la contrasena y el
											// repetir contrasena coincidan
	private boolean matriculaNotUnic = false; // valida que la matricula no se
												// repita en la base de datos
	private String userPaswordRepit = "";
	private String userPasword = "";
	private Enfermera_configuracion enfermero = new Enfermera_configuracion();
	private boolean clean = false;

	@In(create = true)
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	// METODOS--------------------------------------------------
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		entidades();
		if (!clean)
			clean = true;
	}

	@SuppressWarnings("unchecked")
	@Create
	public void Source() {
		cultura();
		tipoFuncionarioSource = entityManager.createQuery(
				"select t from TipoFuncionario_configuracion t")
				.getResultList();
		rolsSource = entityManager.createQuery(
				"select r from Role_configuracion r where r.eliminado = false or r.eliminado = null order by r.name").getResultList();

		entidades();

		listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
		listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
		listaE = new ArrayList<Entidad_configuracion>();

		this.nom = false;
		this.ced = false;
		this.fnac = false;
		this.username = false;
		this.pass = false;
		this.verpass = false;
		this.user_used = false;
		this.pass_desigual = true;

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

	public boolean cleanCampos() {
		return clean;
	}

	public void verificarCargos() {
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

	// carga las entidades
	@SuppressWarnings("unchecked")
	public void entidades() {
		
		/**
		 * @author yurien 28/03/2014
		 * Se agrega la nueva restriccion para que muestre las entidades 
		 * que pertenecen al anillo configurado
		 * **/
		listaEntidadSource = entityManager.createQuery(
				"select ent from Entidad_configuracion ent "
				+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				+ "where ent.perteneceARhio = true "
				+ "order by ent.nombre asc").getResultList();
	}

	// carga los departamentos a partir de las entidades seleccionadas
	@SuppressWarnings("unchecked")
	public void departamentos() {
		listaDepartamentoSource.clear();
		for (int i = 0; i < listaEntidadTarget.size(); i++) {
			List<DepartamentoInEntidad_configuracion> l = entityManager
					.createQuery(
							"select e from Departamento_configuracion d "
									+ "join d.departamentoInEntidads e "
									+ "where e.entidad.id =:idEntidad")
					.setParameter("idEntidad",
							listaEntidadTarget.get(i).getId()).getResultList();
			listaDepartamentoSource.addAll(l);
		}
		departamentoTargetValidation();
	}

	// validacion de los departamentos seleccionadas
	public void departamentoTargetValidation() {
		boolean departamentoAsignado = false;
		for (int j = 0; j < listaDepartamentoTarget.size(); j++) {
			for (int i = 0; i < listaDepartamentoSource.size(); i++) {
				if (listaDepartamentoSource.get(i).equals(
						listaDepartamentoTarget.get(j))) {
					listaDepartamentoSource.remove(i);
					i--;
					departamentoAsignado = true;
					break;
				}

			}
			if (departamentoAsignado == false) {
				listaDepartamentoTarget.remove(j);
				j--;
			}

			else
				departamentoAsignado = false;
		}
		servicios();
	}

	// carga los servicios a partir de los departamentos seleccionados
	@SuppressWarnings("unchecked")
	public void servicios() {
		listaServicioInEntidadSource.clear();
		for (int j = 0; j < listaDepartamentoTarget.size(); j++) {
			List<ServicioInEntidad_configuracion> ls = entityManager
					.createQuery(
							"select s from ServicioInEntidad_configuracion s "
									+ "where s.servicio.departamento.id =:idDepartamento "
									+ "and s.entidad.id =:idEntidad and s.servicio.servicioFisico = true")
					.setParameter(
							"idDepartamento",
							listaDepartamentoTarget.get(j).getDepartamento()
									.getId())
					.setParameter("idEntidad",
							listaDepartamentoTarget.get(j).getEntidad().getId())
					.getResultList();
			listaServicioInEntidadSource.addAll(ls);
		}
		servicioTargetValidation();
	}

	// valida los servicios seleccionados
	public void servicioTargetValidation() {
		boolean servicioAsignado = false;
		for (int j = 0; j < listaServicioInEntidadTarget.size(); j++) {
			for (int i = 0; i < listaServicioInEntidadSource.size(); i++) {
				if (listaServicioInEntidadSource.get(i).equals(
						listaServicioInEntidadTarget.get(j))) {
					listaServicioInEntidadSource.remove(i);
					i--;
					servicioAsignado = true;
					break;
				}
			}
			if (servicioAsignado == false) {
				listaServicioInEntidadTarget.remove(j);
				j--;
			} else
				servicioAsignado = false;
		}
	}

	// actualizar cambios de los listshuttle en el servidor
	public void subirServicios() {
	}

	// validacion de campos
	@SuppressWarnings("unchecked")
	public boolean validacionErrorExist() {
		passwordCheck = false;
		userExist = false;
		matriculaNotUnic = false;

		List<String> matricula = new ArrayList<String>();
		if (!this.matriculaColegioEnfermeria.equals("")) {
			matricula = entityManager
					.createQuery(
							"select e.matriculaColegioEnfermeria from Enfermera_configuracion e where e.matriculaColegioEnfermeria =:matricula")
					.setParameter("matricula", this.matriculaColegioEnfermeria)
					.getResultList();
		}
		if (!matricula.isEmpty())
			matriculaNotUnic = true;

		// valida si el usuario no exista
		List<Usuario_configuracion> usuariosEstisting = entityManager
				.createQuery(
						"select u from Usuario_configuracion u where u.username = :usuarioUsername")
				.setParameter("usuarioUsername", nick).getResultList();

		if (usuariosEstisting.size() != 0)
			userExist = true;

		if (!userPaswordRepit.isEmpty() && !userPasword.isEmpty()
				&& !userPaswordRepit.equals(userPasword)) {
			passwordCheck = true;
			this.userPasword = "";
			this.userPaswordRepit = "";
		}

		// verificha si existe algun error
		if (userExist || passwordCheck || matriculaNotUnic)
			return true;
		return false;
	}

	public void entidadTargetValidation() {
		boolean entidadAsignada = false;
		for (int j = 0; j < listaEntidadTarget.size(); j++) {
			for (int i = 0; i < listaEntidadSource.size(); i++) {
				if (listaEntidadSource.get(i).equals(listaEntidadTarget.get(j))) {
					listaEntidadSource.remove(i);
					i--;
					entidadAsignada = true;
					break;
				}

			}
			if (entidadAsignada == false) {
				listaEntidadTarget.remove(j);
				j--;
			}

			else
				entidadAsignada = false;
		}
	}

	public void subirPhoto() {
		// para acceder a la direccion deseada
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath("resources/modCommon/userphotos");
		rootpath += "/" + this.usuario.getUsername() + ".png";

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

	public void depurar() {

		listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
		listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
		listaE = new ArrayList<Entidad_configuracion>();

		// validacion departamentos
		for (int i = 0; i < listaServicioInEntidadTarget.size(); i++) {
			List<DepartamentoInEntidad_configuracion> ld = new ArrayList<DepartamentoInEntidad_configuracion>();
			ld.addAll(entityManager
					.createQuery(
							"select distinct die from ServicioInEntidad_configuracion sie "
									+ "join sie.servicio.departamento.departamentoInEntidads die "
									+ "where die.entidad = sie.entidad and sie.id =:servicioId")
					.setParameter("servicioId",
							listaServicioInEntidadTarget.get(i).getId())
					.getResultList());
			// buscar dptos repetidos
			for (int j = 0; j < ld.size(); j++) {
				for (int k = 0; k < listaDIE.size(); k++)
					if (ld.get(j).equals(listaDIE.get(k))) {
						ld.remove(j);
						break;
					}
			}
			// adicionando dptos
			listaDIE.addAll(ld);
		}
		listaDepartamentoTarget = listaDIE;

		// validacion entidades
		for (int i = 0; i < listaDepartamentoTarget.size(); i++) {
			List<Entidad_configuracion> le = new ArrayList<Entidad_configuracion>();
			le.addAll(entityManager
					.createQuery(
							"select distinct e from Entidad_configuracion e "
									+ "join e.departamentoInEntidads die where die.id =:dieId")
					.setParameter("dieId",
							listaDepartamentoTarget.get(i).getId())
					.getResultList());
			// buscar entidades repetidas
			for (int j = 0; j < le.size(); j++) {
				for (int k = 0; k < listaE.size(); k++)
					if (le.get(j).equals(listaE.get(k))) {
						le.remove(j);
						break;
					}
			}
			// adicionando entidades
			listaE.addAll(le);
		}
		listaEntidadTarget = listaE;

		entidades();
		departamentos();
		entidadTargetValidation();
	}

	// crea un enfermero
	@SuppressWarnings("deprecation")
	@End
	public String crear() {

		// Validaciones
		boolean failValidation = false;

		if (nick.equals("")) {
			facesMessages.addToControlFromResourceBundle("userName",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!nick.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("userName",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (nick.length() > 25) {
			facesMessages.addToControlFromResourceBundle("userName",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (contrasenna.equals("")) {
			facesMessages.addToControlFromResourceBundle("password",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		}

		if (contrasenna.length() > 25) {
			facesMessages.addToControlFromResourceBundle("password",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (verificarPassword.equals("")) {
			facesMessages.addToControlFromResourceBundle("repitPassword",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		}

		if (verificarPassword.length() > 25) {
			facesMessages.addToControlFromResourceBundle("repitPassword",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (culturaSelec.equals("")) {
			facesMessages.addToControlFromResourceBundle("cultura",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		}

		if (nombre.equals("")) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!nombre.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("name",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (nombre.length() > 25) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (primerApellido.equals("")) {
			facesMessages.addToControlFromResourceBundle("primerApellido",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!primerApellido.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("primerApellido",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (primerApellido.length() > 25) {
			facesMessages.addToControlFromResourceBundle("primerApellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		// if (segundoApellido.equals("")) {
		// facesMessages.addToControlFromResourceBundle("segundoApellido",
		// Severity.ERROR, "Valor requerido");
		// failValidation = true;
		// } else {
		if (!segundoApellido.equals("")
				&& !segundoApellido.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("segundoApellido",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}
		// }

		if (segundoApellido.length() > 25) {
			facesMessages.addToControlFromResourceBundle("segundoApellido",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (cedula.equals("")) {
			facesMessages.addToControlFromResourceBundle("cedula",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!cedula.toString().matches("^(\\s*[A-Za-z0123456789-]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("cedula",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (cedula.length() > 25) {
			facesMessages.addToControlFromResourceBundle("cedula",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (!pasaporte.toString().matches("^(\\s*[A-Za-z0123456789]+\\s*)+$")
				&& !pasaporte.equals("")) {
			facesMessages.addToControlFromResourceBundle("pasaporte",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}

		if (pasaporte.length() > 25) {
			facesMessages.addToControlFromResourceBundle("pasaporte",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}

		if (fechaNacimiento == null) {
			facesMessages.addToControlFromResourceBundle("fecha",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		}

		if (!direccionParticular.toString().matches(
				"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")
				&& !direccionParticular.equals("")) {
			facesMessages.addToControlFromResourceBundle("direccionParticular",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}

		if (!telefono.toString().matches("^(\\s*[0123456789-]+\\s*)+$")
				&& !telefono.equals("")) {
			facesMessages.addToControlFromResourceBundle("telefono",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}

		// if (matriculaColegioEnfermeria.equals("")) {
		// facesMessages.addToControlFromResourceBundle("matricula",
		// Severity.ERROR, "Valor requerido");
		// failValidation = true;
		// } else {
		if (!matriculaColegioEnfermeria.equals("")
				&& !matriculaColegioEnfermeria.toString().matches(
						"^(\\s*[A-Za-z0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matricula",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}
		// }

		if (matriculaColegioEnfermeria.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matricula",
					Severity.ERROR, "El máximo de caracteres es: 25");
			failValidation = true;
		}
		if (listaServicioInEntidadTarget.size() ==0) {
			facesMessages.addToControlFromResourceBundle("ListShuttleServicios2",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		}
		

		if (failValidation) {
			return "";
		}

		try {
			if (validacionErrorExist()) {
				facesMessages.add(new FacesMessage(
						"Existen campos vacios que son requeridos."));
				return "fail";
			}

			listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
			listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
			listaE = new ArrayList<Entidad_configuracion>();

			// datos del usuario
			usuario.setUsername(nick);
			usuario.setNombre(this.nombre);
			usuario.setPrimerApellido(this.primerApellido);
			usuario.setSegundoApellido(this.segundoApellido);
			usuario.setCedula(this.cedula);
			usuario.setPasaporte(this.pasaporte);
			usuario.setDireccionParticular(this.direccionParticular);
			usuario.setTelefono(this.telefono);
			usuario.setFechaNacimiento(this.fechaNacimiento);

			List<String> allnick = entityManager.createQuery(
					"select s.username from Usuario_configuracion s")
					.getResultList();

			this.user_used = allnick.contains(nick);

			if (user_used) {
				return "valorRequerido";
			}

			usuario.setUsername(nick);

			if (!this.nombrePhoto.equals("")) {
				this.subirPhoto();
			}

			this.pass_desigual = this.contrasenna.equals(verificarPassword);

			if (!pass_desigual) {

				return "valorRequerido";
			}

			usuario.setCid(this.cid);
			usuario.setEliminado(false);

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(contrasenna.getBytes());
			String md5pass = new String(Hex.encodeHex(digest.digest()));
			usuario.setPassword(md5pass);

			// Tipo funcionario
			Set<TipoFuncionario_configuracion> listaFuncionario = new HashSet<TipoFuncionario_configuracion>();
			listaFuncionario.addAll(tipoFuncionarioTarget);

			Set<CargoFuncionario_configuracion> listaCargoFuncionario = new HashSet<CargoFuncionario_configuracion>();
			listaCargoFuncionario.addAll(cargoTarget);

			Set<Role_configuracion> listaRol = new HashSet<Role_configuracion>();
			listaRol.addAll(rolsTarget);

			// usuario_conf.setServicios(listaServicio);--------------------

			usuario.setTipoFuncionarios(listaFuncionario);
			usuario.setCargoFuncionarios(listaCargoFuncionario);
			usuario.setRoles(listaRol);

			Profile_configuracion perfil = new Profile_configuracion();
			perfil.setEliminado(false);
			//perfil.setLocaleString("es");
			perfil.setTheme("alas-verde");
			perfil.setTreeLikeMenu(true);
			perfil.setUsuario(usuario);
			usuario.setProfile(perfil);

			// SERVICIO IN ENTIDAD
			listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
			listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
			listaE = new ArrayList<Entidad_configuracion>();
			// validacion departamentos
			for (int i = 0; i < listaServicioInEntidadTarget.size(); i++) {
				List<DepartamentoInEntidad_configuracion> ld = new ArrayList<DepartamentoInEntidad_configuracion>();
				ld.addAll(entityManager
						.createQuery(
								"select distinct die from ServicioInEntidad_configuracion sie "
										+ "join sie.servicio.departamento.departamentoInEntidads die "
										+ "where die.entidad = sie.entidad and sie.id =:servicioId")
						.setParameter("servicioId",
								listaServicioInEntidadTarget.get(i).getId())
						.getResultList());
				// buscar dptos repetidos
				for (int j = 0; j < ld.size(); j++) {
					for (int k = 0; k < listaDIE.size(); k++)
						if (ld.get(j).equals(listaDIE.get(k))) {
							ld.remove(j);
							break;
						}
				}
				// adicionando dptos
				listaDIE.addAll(ld);
			}

			/*
			 * if(listaDepartamentoTarget.size() != listaDIE.size()){
			 * showModalPanel = true; tabSelect = "servicios"; return "fail"; }
			 */

			// validacion entidades
			for (int i = 0; i < listaDepartamentoTarget.size(); i++) {
				List<Entidad_configuracion> le = new ArrayList<Entidad_configuracion>();
				le.addAll(entityManager
						.createQuery(
								"select distinct e from Entidad_configuracion e "
										+ "join e.departamentoInEntidads die where die.id =:dieId")
						.setParameter("dieId",
								listaDepartamentoTarget.get(i).getId())
						.getResultList());
				// buscar entidades repetidas
				for (int j = 0; j < le.size(); j++) {
					for (int k = 0; k < listaE.size(); k++)
						if (le.get(j).equals(listaE.get(k))) {
							le.remove(j);
							break;
						}
				}
				// adicionando entidades
				listaE.addAll(le);
			}

			/*
			 * if(listaEntidadTarget.size() != listaE.size()){ showModalPanel =
			 * true; tabSelect = "servicios"; return "fail"; }
			 */

			// persistiendo servicios en la bd
			for (ServicioInEntidad_configuracion servicio : listaServicioInEntidadTarget) {
				servicio = entityManager.merge(servicio);
			}
			usuario.getServicioInEntidads()
					.addAll(listaServicioInEntidadTarget);

			// CULTURA
			for (int i = 0; i < culturaSource.size(); i++) {
				if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
					perfil.setLocaleString(culturaSource.get(i)
							.getLocalString());
				}
			}

			enfermero
					.setMatriculaColegioEnfermeria(this.matriculaColegioEnfermeria);

			enfermero.setUsuario(usuario);
			enfermero.setEliminado(false);
			entityManager.persist(usuario);
			entityManager.persist(perfil);
			entityManager.persist(enfermero);

			for (int i = 0; i < listaEntidadTarget.size(); i++) {
				EnfermeraInEntidad aux = new EnfermeraInEntidad();
				aux.setEliminado(false);
				aux.setEnfermera(enfermero);
				aux.setEntidad(listaEntidadTarget.get(i));
				entityManager.persist(aux);
			}

			entityManager.flush();

			return "gotodetails";
		} catch (Exception error) {
			facesMessages.add(new FacesMessage("Problema"));
			return "";
		}
	}

	// PROPIEDADES-----------------------------------------------------------

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick.trim();
	}

	public String getContrasenna() {
		return contrasenna;
	}

	public void setContrasenna(String contrasenna) {
		this.contrasenna = contrasenna;
	}

	public String getVerificarPassword() {
		return verificarPassword;
	}

	public void setVerificarPassword(String verificarPassword) {
		this.verificarPassword = verificarPassword;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre.trim();
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido.trim();
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido.trim();
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getPasaporte() {
		return pasaporte;
	}

	public void setPasaporte(String pasaporte) {
		this.pasaporte = pasaporte.trim();
	}

	public String getDireccionParticular() {
		return direccionParticular;
	}

	public void setDireccionParticular(String direccionParticular) {
		this.direccionParticular = direccionParticular.trim();
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public String getMatriculaColegioEnfermeria() {
		return matriculaColegioEnfermeria;
	}

	public void setMatriculaColegioEnfermeria(String matriculaColegioEnfermeria) {
		this.matriculaColegioEnfermeria = matriculaColegioEnfermeria.trim();
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public List<Entidad_configuracion> getListaEntidadSource() {
		return listaEntidadSource;
	}

	public void setListaEntidadSource(
			List<Entidad_configuracion> listaEntidadSource) {
		this.listaEntidadSource = listaEntidadSource;
	}

	public List<Entidad_configuracion> getListaEntidadTarget() {
		return listaEntidadTarget;
	}

	public void setListaEntidadTarget(
			List<Entidad_configuracion> listaEntidadTarget) {
		this.listaEntidadTarget = listaEntidadTarget;
	}

	public List<DepartamentoInEntidad_configuracion> getListaDepartamentoSource() {
		return listaDepartamentoSource;
	}

	public void setListaDepartamentoSource(
			List<DepartamentoInEntidad_configuracion> listaDepartamentoSource) {
		this.listaDepartamentoSource = listaDepartamentoSource;
	}

	public List<DepartamentoInEntidad_configuracion> getListaDepartamentoTarget() {
		return listaDepartamentoTarget;
	}

	public void setListaDepartamentoTarget(
			List<DepartamentoInEntidad_configuracion> listaDepartamentoTarget) {
		this.listaDepartamentoTarget = listaDepartamentoTarget;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioInEntidadSource() {
		return listaServicioInEntidadSource;
	}

	public void setListaServicioInEntidadSource(
			List<ServicioInEntidad_configuracion> listaServicioInEntidadSource) {
		this.listaServicioInEntidadSource = listaServicioInEntidadSource;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioInEntidadTarget() {
		return listaServicioInEntidadTarget;
	}

	public void setListaServicioInEntidadTarget(
			List<ServicioInEntidad_configuracion> listaServicioInEntidadTarget) {
		this.listaServicioInEntidadTarget = listaServicioInEntidadTarget;
	}

	public boolean isUserExist() {
		return userExist;
	}

	public void setUserExist(boolean userExist) {
		this.userExist = userExist;
	}

	public boolean isPasswordCheck() {
		return passwordCheck;
	}

	public void setPasswordCheck(boolean passwordCheck) {
		this.passwordCheck = passwordCheck;
	}

	public String getUserPaswordRepit() {
		return userPaswordRepit;
	}

	public void setUserPaswordRepit(String userPaswordRepit) {
		this.userPaswordRepit = userPaswordRepit;
	}

	public String getUserPasword() {
		return userPasword;
	}

	public void setUserPasword(String userPasword) {
		this.userPasword = userPasword;
	}

	public Enfermera_configuracion getEnfermero() {
		return enfermero;
	}

	public void setEnfermero(Enfermera_configuracion enfermero) {
		this.enfermero = enfermero;
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public boolean isMatriculaNotUnic() {
		return matriculaNotUnic;
	}

	public void setMatriculaNotUnic(boolean matriculaNotUnic) {
		this.matriculaNotUnic = matriculaNotUnic;
	}

	public String getUrl_foto() {
		return url_foto;
	}

	public void setUrl_foto(String url_foto) {
		this.url_foto = url_foto;
	}

	public Boolean getUser_used() {
		return user_used;
	}

	public void setUser_used(Boolean user_used) {
		this.user_used = user_used;
	}

	public Boolean getPass_desigual() {
		return pass_desigual;
	}

	public void setPass_desigual(Boolean pass_desigual) {
		this.pass_desigual = pass_desigual;
	}

	public String getTabSelect() {
		return tabSelect;
	}

	public void setTabSelect(String tabSelect) {
		this.tabSelect = tabSelect;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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

	public Long getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(Long idCargo) {
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

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
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

	public boolean isNom() {
		return nom;
	}

	public void setNom(boolean nom) {
		this.nom = nom;
	}

	public boolean isCed() {
		return ced;
	}

	public void setCed(boolean ced) {
		this.ced = ced;
	}

	public boolean isFnac() {
		return fnac;
	}

	public void setFnac(boolean fnac) {
		this.fnac = fnac;
	}

	public boolean isUsername() {
		return username;
	}

	public void setUsername(boolean username) {
		this.username = username;
	}

	public boolean isPass() {
		return pass;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public boolean isVerpass() {
		return verpass;
	}

	public void setVerpass(boolean verpass) {
		this.verpass = verpass;
	}

	public boolean isFunc_referenciado() {
		return func_referenciado;
	}

	public void setFunc_referenciado(boolean func_referenciado) {
		this.func_referenciado = func_referenciado;
	}

	public boolean isClean() {
		return clean;
	}

	public void setClean(boolean clean) {
		this.clean = clean;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
	}

}
