package gehos.configuracion.management.gestionarEnfermero;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.EnfermeraInEntidad;
import gehos.configuracion.management.entity.Enfermera_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Hex;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

//LISTO
@Name("enfermeroEdit_custom")
@Scope(ScopeType.CONVERSATION)
public class EnfermeroEdit_custom {

	// enfermero
	private Enfermera_configuracion enfermero = new Enfermera_configuracion();
	private String matriculaColegioEnfermeria = "";
	private Long enfermeroId = -1l;
	private List<EnfermeraInEntidad> listEnfermeroinEntity = new ArrayList<EnfermeraInEntidad>();

	// usuario
	private Long idUsuario;
	private String userName = "";
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
	private byte[] data;
	private static final int img_width = 74;
	private static final int img_height = 74;
	private Usuario_configuracion usuario = new Usuario_configuracion();

	// cultura
	private String culturaSelec = "";
	List<Cultura> listaCultura = new ArrayList<Cultura>();

	// servicios del usuario
	private Entidad_configuracion entidad;
	private List<Entidad_configuracion> listaEntidadSource = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> listaEntidadTarget = new ArrayList<Entidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> listaDepartamentoSource = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> listaDepartamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioInEntidadSource = new ArrayList<ServicioInEntidad_configuracion>();
	private List<ServicioInEntidad_configuracion> listaServicioInEntidadTarget = new ArrayList<ServicioInEntidad_configuracion>();

	// funcionarios
	private List<TipoFuncionario_configuracion> tipoFuncionarioTarget = new ArrayList<TipoFuncionario_configuracion>();
	private List<TipoFuncionario_configuracion> tipoFuncionarioSource = new ArrayList<TipoFuncionario_configuracion>();

	// cargos
	private List<CargoFuncionario_configuracion> cargoTarget;
	private List<CargoFuncionario_configuracion> cargoSource = new ArrayList<CargoFuncionario_configuracion>();

	// roles
	private List<Role_configuracion> rolsTarget = new ArrayList<Role_configuracion>();
	private List<Role_configuracion> rolsSource = new ArrayList<Role_configuracion>();

	// validaciones
	private Boolean user_used = false;// validar que el nombre de usuario no
										// exista
	private Boolean pass_desigual = false;// validar que las constrasenas sean
											// iguales

	private Boolean keepPassword = true; // no cambiar la contrasena
	private String tabSelect = "tabUsuario";
	private String oldPassword = ""; // valida si cambio la contrasena
	private String verificarPassword = ""; // repetir la contrasena
	private boolean matriculaNotUnic = false;

	// depurar
	List<ServicioInEntidad_configuracion> listaSIE;
	List<DepartamentoInEntidad_configuracion> listaDIE;
	List<Entidad_configuracion> listaE;

	// otras funciones
	private String selectedTab = "tabUsuario";

	@In
	EntityManager entityManager;

	@In
	IBitacora bitacora;

	@In(create = true)
	FacesMessages facesMessages;

	@In
	LocaleSelector localeSelector;

	Integer cid = -1;

	// METODOS--------------------------------------------------------------
	@SuppressWarnings("unchecked")
	// carga el enfermero a editar
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setEnfermeroId(Long enfermeroId) {
		if (cid == -1) {
			cid = 1;
			if (this.enfermeroId != enfermeroId) {
				this.enfermeroId = enfermeroId;

				enfermero = entityManager.find(Enfermera_configuracion.class,
						this.enfermeroId);
				this.matriculaColegioEnfermeria = enfermero
						.getMatriculaColegioEnfermeria();

				usuario = (Usuario_configuracion) entityManager.find(
						Usuario_configuracion.class, enfermero.getUsuario()
								.getId());

				listEnfermeroinEntity = entityManager
						.createQuery(
								"select enferInEntity from EnfermeraInEntidad enferInEntity where enferInEntity.enfermera.id =:id")
						.setParameter("id", enfermeroId).getResultList();

				source();
			}
		}
	}

	// cargar datos del usuario
	@SuppressWarnings("unchecked")
	private void source() {
		// datos del usuario
		userName = usuario.getUsername();
		nombre = usuario.getNombre();
		primerApellido = usuario.getPrimerApellido();
		segundoApellido = usuario.getSegundoApellido();
		cedula = usuario.getCedula();
		pasaporte = usuario.getPasaporte();
		direccionParticular = usuario.getDireccionParticular();
		fechaNacimiento = usuario.getFechaNacimiento();
		telefono = usuario.getTelefono();

		// cultura del perfil
		cultura();// carga lista de culturas
		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getLocalString()
					.equals(usuario.getProfile().getLocaleString())) {
				culturaSelec = listaCultura.get(i).getIdioma();
			}
		}

		// servicios del usuario
		entidades();
		listaServicioInEntidadTarget = entityManager
				.createQuery(
						"select sie from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		listaDepartamentoTarget = entityManager
				.createQuery(
						"select distinct die from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.servicio.departamento.departamentoInEntidads die where u.id =:idUsuario and die.entidad = sie.entidad")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		listaEntidadTarget = entityManager
				.createQuery(
						"select distinct sie.entidad from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		departamentos();
		entidadTargetValidation();

		if (listaServicioInEntidadTarget.size() == 0) {
			listaEntidadTarget = entityManager
					.createQuery(
							"select enferInEntity.entidad from EnfermeraInEntidad enferInEntity where enferInEntity.enfermera.id =:id")
					.setParameter("id", enfermeroId).getResultList();
			entidadTargetValidation();
			departamentos();
			servicios();
		}

		// funcionario
		tipoFuncionarioTarget.clear();
		tipoFuncionarioTarget.addAll(usuario.getTipoFuncionarios());
		if (cargoTarget == null) {
			cargoTarget = new ArrayList<CargoFuncionario_configuracion>();
			cargoTarget.addAll(usuario.getCargoFuncionarios());
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

		// roles
		rolsTarget.clear();
		rolsTarget.addAll(usuario.getRoles());
		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager.createQuery("from Role_configuracion r where r.eliminado = false or r.eliminado = null order by r.name")
				.getResultList();
		for (int i = 0; i < roles.size(); i++) {
			if (estaRol(roles.get(i).getId()) == -1)
				rolsSource.add(roles.get(i));
		}
	}

	// validacion rol
	private int estaRol(Long id) {
		for (int i = 0; i < rolsTarget.size(); i++) {
			if (rolsTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	// validacion tipofuncionario
	private int estaTipoFincionario(Long id) {
		for (int i = 0; i < tipoFuncionarioTarget.size(); i++) {
			if (tipoFuncionarioTarget.get(i).getId().equals(id))
				return i;
		}
		return -1;
	}

	// devuelve la lista de culturas
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

	// carga lista de entidades
	@SuppressWarnings("unchecked")
	public void entidades() {
		/**
		 * @author yurien 04//06/2014
		 * Se cambia la forma en que se muestran las entidades que pertenecen al anillo
		 * **/
		listaEntidadSource = entityManager.createQuery(
				"select ent from Entidad_configuracion ent where "
				+ "ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
//				+ "ent.perteneceARhio = true "
				+ "order by ent.nombre asc").getResultList();
	}

	// validar entidades
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

	// carga lista de departamentos
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

	// validar departamentos
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

	// carga lista de servicios
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

	// validacion de servicios
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
			}

			else
				servicioAsignado = false;
		}
	}

	// actualiza lista de servicios en el servidor
	public void subirServicios() {
	}

	// carga los cargos posibles a partir de los tipos de funcionarios
	// seleccionados
	public void listaCargosPosibles() {
		cargoSource.clear();
		TipoFuncionario_configuracion t = new TipoFuncionario_configuracion();
		for (int i = 0; i < tipoFuncionarioTarget.size(); i++) {
			t = entityManager.find(TipoFuncionario_configuracion.class,
					tipoFuncionarioTarget.get(i).getId());
			cargoSource.addAll(t.getCargoFuncionarios());
		}
		validarPosiblesCargo();
	}

	// validacion de cargos
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

	// con el se logra actualizar los cambios realizados en las listas
	public void verificarCargos() {
	}

	public void subirPhoto() {
		// para acceder a la direccion deseada
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath("resources/modCommon/userphotos");
		rootpath += "/" + this.usuario.getUsername() + ".png";

		try {
			// copiando el fichero
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
			ImageIO.write(risizeImagePng, "png", new File(rootpath));

			dataOutputStream.flush();
			dataOutputStream.close();
			fileOutputStream.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}

	}

	private static BufferedImage resizeImage(BufferedImage originalImage,
			int type) {
		BufferedImage resizedImage = new BufferedImage(img_width, img_height,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, img_width, img_height, null);
		g.dispose();

		return resizedImage;
	}

	// valida campos
	@SuppressWarnings("unchecked")
	public boolean validation() {
		cleanValidation();

		List<String> matricula = entityManager
				.createQuery(
						"select e.matriculaColegioEnfermeria from Enfermera_configuracion e where e.matriculaColegioEnfermeria =:matricula"
								+ " and e.id <>:idEnfermero")
				.setParameter("idEnfermero", enfermero.getId())
				.setParameter("matricula",
						enfermero.getMatriculaColegioEnfermeria())
				.getResultList();

		if (!matricula.isEmpty())
			matriculaNotUnic = true;

		// usuario existente
		List<String> allUserName = entityManager
				.createQuery(
						"select s.username from Usuario_configuracion s where s.username <>:userName")
				.setParameter("userName", userName).getResultList();

		if (allUserName.contains(userName)) {
			this.user_used = true;
			return true;
		}

		// contrasenas iguales
		this.pass_desigual = !this.contrasenna.equals(verificarPassword);
		if (pass_desigual)
			return true;

		return false;
	}

	// cancela todas las validaciones
	public void cleanValidation() {
		matriculaNotUnic = false;
		user_used = false;
		pass_desigual = false;
	}

	@SuppressWarnings("unchecked")
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

	// crea un
	// enfermero----------------------------------------------------------
	@SuppressWarnings("unchecked")
	@End
	public String modificar() throws NoSuchAlgorithmException {

		// Validaciones
		boolean failValidation = false;
		String us = userName.toLowerCase();
		if(us.endsWith("@pdvsa.com")){
			us = us.substring(0, us.length() - 10);
		}
		
		if (us.equals("")) {
			facesMessages.addToControlFromResourceBundle("userName",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!us.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("userName",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (userName.length() > 25) {
			facesMessages.addToControlFromResourceBundle("userName",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			failValidation = true;
		}

		if (!keepPassword) {
			if (contrasenna.equals("")) {
				facesMessages.addToControlFromResourceBundle("password",
						Severity.ERROR, "Valor requerido");
				failValidation = true;
			}

			if (contrasenna.length() > 25) {
				facesMessages.addToControlFromResourceBundle("password",
						Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
				failValidation = true;
			}

			if (verificarPassword.equals("")) {
				facesMessages.addToControlFromResourceBundle("repitPassword",
						Severity.ERROR, "Valor requerido");
				failValidation = true;
			}

			if (verificarPassword.length() > 25) {
				facesMessages.addToControlFromResourceBundle("repitPassword",
						Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
				failValidation = true;
			}
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
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("name",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (nombre.length() > 25) {
			facesMessages.addToControlFromResourceBundle("name",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			failValidation = true;
		}

		if (primerApellido.equals("")) {
			facesMessages.addToControlFromResourceBundle("primerApellido",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!primerApellido.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("primerApellido",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (primerApellido.length() > 25) {
			facesMessages.addToControlFromResourceBundle("primerApellido",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			failValidation = true;
		}

		// if (segundoApellido.equals("")) {
		// facesMessages.addToControlFromResourceBundle("segundoApellido",
		// Severity.ERROR, "Valor requerido");
		// failValidation = true;
		// } else {
		if (!segundoApellido.equals("")
				&& !segundoApellido.toString().matches(
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789.]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("segundoApellido",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}
		// }

		if (segundoApellido.length() > 25) {
			facesMessages.addToControlFromResourceBundle("segundoApellido",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
			failValidation = true;
		}

		if (cedula.equals("")) {
			facesMessages.addToControlFromResourceBundle("cedula",
					Severity.ERROR, "Valor requerido");
			failValidation = true;
		} else {
			if (!cedula.toString().matches(
					"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
				facesMessages.addToControlFromResourceBundle("cedula",
						Severity.ERROR, "Caracteres incorrectos");
				failValidation = true;
			}
		}

		if (cedula.length() > 25) {
			facesMessages.addToControlFromResourceBundle("cedula",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
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
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
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
						"^(\\s*[A-Za-záéíóúÁÉÍÓÚñÑüÜ0123456789]+\\s*)+$")) {
			facesMessages.addToControlFromResourceBundle("matricula",
					Severity.ERROR, "Caracteres incorrectos");
			failValidation = true;
		}
		// }

		if (matriculaColegioEnfermeria.length() > 25) {
			facesMessages.addToControlFromResourceBundle("matricula",
					Severity.ERROR, "Cantidad de caracteres entre 1 y 25");
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

		if (validation())
			return "fail";

		listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
		listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
		listaE = new ArrayList<Entidad_configuracion>();

		// cabiar tab selected
		tabSelect = "tabUsuario";

		// datos del usuario
		usuario.setUsername(userName);
		usuario.setNombre(this.nombre);
		usuario.setPrimerApellido(this.primerApellido);
		usuario.setSegundoApellido(this.segundoApellido);
		usuario.setCedula(this.cedula);
		usuario.setPasaporte(this.pasaporte);
		usuario.setDireccionParticular(this.direccionParticular);
		usuario.setTelefono(this.telefono);
		usuario.setFechaNacimiento(this.fechaNacimiento);

		// password
		if (!keepPassword) {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(contrasenna.getBytes());
			String md5pass = new String(Hex.encodeHex(digest.digest()));
			usuario.setPassword(md5pass);
		}

		// foto
		if (!url_foto.isEmpty()) {
			this.subirPhoto();
		}

		// datos como funcionario
		Set<TipoFuncionario_configuracion> listaFuncionario = new HashSet<TipoFuncionario_configuracion>();
		listaFuncionario.addAll(tipoFuncionarioTarget);
		usuario.setTipoFuncionarios(listaFuncionario);

		// datos de los cargos por funcionario
		Set<CargoFuncionario_configuracion> listaCargoFuncionario = new HashSet<CargoFuncionario_configuracion>();
		listaCargoFuncionario.addAll(cargoTarget);
		usuario.setCargoFuncionarios(listaCargoFuncionario);

		// datos del roles
		Set<Role_configuracion> listaRol = new HashSet<Role_configuracion>();
		listaRol.addAll(rolsTarget);
		usuario.setRoles(listaRol);

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

		// persistiendo servicios en la bd
		for (ServicioInEntidad_configuracion servicio : listaServicioInEntidadTarget) {
			servicio = entityManager.merge(servicio);
		}
		usuario.getServicioInEntidads().clear();
		usuario.getServicioInEntidads().addAll(listaServicioInEntidadTarget);

		// cultura
		for (int i = 0; i < listaCultura.size(); i++) {
			if (listaCultura.get(i).getIdioma().equals(culturaSelec)) {
				usuario.getProfile().setLocaleString(
						listaCultura.get(i).getLocalString());
			}
		}

		// datos del enfermero

		Long cid = bitacora.registrarInicioDeAccion("Modificando enfermero.");
		usuario.setCid(cid);

		entityManager.merge(usuario);
		enfermero.setUsuario(usuario);
		enfermero
				.setMatriculaColegioEnfermeria(this.matriculaColegioEnfermeria);
		enfermero.setCid(cid);
		entityManager.persist(enfermero);

		// Adicionar los nuevos enfermeros In entidades
		for (int i = 0; i < listaEntidadTarget.size(); i++) {
			boolean esta = false;

			for (int j = 0; j < listEnfermeroinEntity.size(); j++) {
				if (listaEntidadTarget
						.get(i)
						.getId()
						.equals(listEnfermeroinEntity.get(j).getEntidad()
								.getId())) {
					esta = true;
				}
			}

			if (!esta) {
				EnfermeraInEntidad aux = new EnfermeraInEntidad();
				aux.setEliminado(false);
				aux.setEnfermera(enfermero);
				aux.setEntidad(listaEntidadTarget.get(i));
				aux.setCid(cid);
				entityManager.persist(aux);
			}
		}

		// Elimino los que no estan
		for (int i = 0; i < listEnfermeroinEntity.size(); i++) {
			boolean esta = false;

			for (int j = 0; j < listaEntidadTarget.size(); j++) {
				if (listaEntidadTarget
						.get(j)
						.getId()
						.equals(listEnfermeroinEntity.get(i).getEntidad()
								.getId())) {
					esta = true;
				}
			}

			if (!esta) {
				entityManager.remove(listEnfermeroinEntity.get(i));
			}

		}

		entityManager.flush();
		return "gotodetails";
	}

	// PROPIEDADES--------------------------------------------------------

	public String getMatriculaColegioEnfermeria() {
		return matriculaColegioEnfermeria;
	}

	public void setMatriculaColegioEnfermeria(String matriculaColegioEnfermeria) {
		this.matriculaColegioEnfermeria = matriculaColegioEnfermeria.trim();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName.trim();
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

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public String getUrl_foto() {
		return url_foto;
	}

	public void setUrl_foto(String url_foto) {
		this.url_foto = url_foto;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public static int getIMG_WIDTH() {
		return img_width;
	}

	public static int getIMG_HEIGHT() {
		return img_height;
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

	public Long getIdUsuario() {
		return idUsuario;
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

	public List<ServicioInEntidad_configuracion> getListaServicioInEntidadTarget() {
		return listaServicioInEntidadTarget;
	}

	public void setListaServicioInEntidadTarget(
			List<ServicioInEntidad_configuracion> listaServicioInEntidadTarget) {
		this.listaServicioInEntidadTarget = listaServicioInEntidadTarget;
	}

	public List<ServicioInEntidad_configuracion> getListaServicioInEntidadSource() {
		return listaServicioInEntidadSource;
	}

	public void setListaServicioInEntidadSource(
			List<ServicioInEntidad_configuracion> listaServicioInEntidadSource) {
		this.listaServicioInEntidadSource = listaServicioInEntidadSource;
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
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

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		if (!selectedTab.equals("") && selectedTab != null)
			this.selectedTab = selectedTab;
	}

	public String getTabSelect() {
		return tabSelect;
	}

	public void setTabSelect(String tabSelect) {
		this.tabSelect = tabSelect;
	}

	public Boolean getKeepPassword() {
		return keepPassword;
	}

	public void setKeepPassword(Boolean keepPassword) {
		this.keepPassword = keepPassword;
		if (this.keepPassword) {
			contrasenna = "";
			verificarPassword = "";
		}
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public boolean isMatriculaNotUnic() {
		return matriculaNotUnic;
	}

	public void setMatriculaNotUnic(boolean matriculaNotUnic) {
		this.matriculaNotUnic = matriculaNotUnic;
	}

	public Enfermera_configuracion getEnfermero() {
		return enfermero;
	}

	public void setEnfermero(Enfermera_configuracion enfermero) {
		this.enfermero = enfermero;
	}

	public Long getEnfermeroId() {
		return enfermeroId;
	}

	public void setIdUsuario(Long idUsuario) {
		this.idUsuario = idUsuario;
	}

	public List<EnfermeraInEntidad> getListEnfermeroinEntity() {
		return listEnfermeroinEntity;
	}

	public void setListEnfermeroinEntity(
			List<EnfermeraInEntidad> listEnfermeroinEntity) {
		this.listEnfermeroinEntity = listEnfermeroinEntity;
	}

}
