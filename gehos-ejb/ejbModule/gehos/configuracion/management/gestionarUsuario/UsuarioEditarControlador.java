package gehos.configuracion.management.gestionarUsuario;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.PasswordStrength;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.datoslab.entity.DatosLaborales;
import gehos.comun.datoslab.entity.Division;
import gehos.comun.datoslab.entity.DivisionPersonal;
import gehos.comun.datoslab.entity.EstadoLaboral;
import gehos.comun.datoslab.entity.Formacion;
import gehos.comun.datoslab.entity.GrupoPersonal;
import gehos.comun.datoslab.entity.ManoDominante;
import gehos.comun.datoslab.entity.Persona;
import gehos.comun.datoslab.entity.Posicion;
import gehos.comun.datoslab.entity.Sociedad;
import gehos.comun.datoslab.entity.SubdivisionPersonal;
import gehos.comun.datoslab.entity.TipoNomina;
import gehos.comun.datoslab.entity.TipoZonaLaboral;
import gehos.comun.datoslab.entity.UnidadOrganizativa;
import gehos.comun.datoslab.entity.UnidadTiempo;
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.PasswordHistory_configuracion;
import gehos.configuracion.management.entity.Role_configuracion;
import gehos.configuracion.management.entity.ServicioInEntidad_configuracion;
import gehos.configuracion.management.entity.TipoFuncionario_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;
import gehos.pki.ra.RaFunctions;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import gehos.comun.reglas.parser.RulesDirectoryBase; 
import gehos.comun.reglas.parser.RulesParser;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import org.drools.RuleBase; 
import org.drools.StatefulSession; 
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("usuarioEditarControlador")
@Scope(ScopeType.CONVERSATION)
public class UsuarioEditarControlador {

	// datos laborales
	private String formacion;
	private String grupoPersonal;
	private String estadoLaboral;
	private String tipoNomina;
	private String divisionPersonal;
	private String division;
	private String subdivisionPersonal;
	private String sociedad;
	private String unidadOrganizativa;
	private String telefonoOficina;
	private String posicion;
	private Integer tiempoServicio;
	private String unidadTiempo;
	private String manoDominante;
	private String numeroPersonal;
	private Boolean aseguradoIvss;
	private String tipoZonaLaboral;
	private Integer numHijos;

	// user data
	private Long idUsuario;
	private String contrasenna = "";
	private String nombre = "";
	private String primerApellido = "";
	private String segundoApellido = "";
	private String cedula = "";
	private String pasaporte = "";
	private String direccionParticular = "";
	private String telefono = "";
	private String url_foto = "";
	private String url_signature = "";
	private Date fechaNacimiento;
	private Long cid = -1l;
	private String userName = "";
	private byte[] data;
	private byte[] signature;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private boolean nom, ced, fnac, username, pass, verpass,
			func_referenciado = false;
	private String verificarPassword = "";
	private String email = "";

	private Usuario_configuracion usuario = new Usuario_configuracion();

	// Culturas
	private String culturaSelec = "";
	List<Cultura> culturaSource = new ArrayList<Cultura>();

	// Servicios
	private List<Entidad_configuracion> listaEntidadSource = new ArrayList<Entidad_configuracion>();
	private List<Entidad_configuracion> listaEntidadTarget = new ArrayList<Entidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> listaDepartamentoSource = new ArrayList<DepartamentoInEntidad_configuracion>();
	private List<DepartamentoInEntidad_configuracion> listaDepartamentoTarget = new ArrayList<DepartamentoInEntidad_configuracion>();
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

	// depurar
	List<ServicioInEntidad_configuracion> listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
	List<DepartamentoInEntidad_configuracion> listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
	List<Entidad_configuracion> listaE = new ArrayList<Entidad_configuracion>();

	// otras funcionalidades
	private String selectedTab = "tabUsuario";

	// validations
	private Boolean showModalPanel = false;// muestra el modalpanel de eliminar
	// departamento y entidades
	private Boolean keepPassword = true; // no cambiar la contrasena
	private Usuario_configuracion usuarioValidacion = new Usuario_configuracion();
	private boolean contrasenaConValor = false;

	FacesMessage facesMessage;
	private String message = "";

	@In
	Usuario user;

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;
	@In
	IBitacora bitacora;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	RaFunctions raFunctions;
	
	@In 
	private RulesParser rulesParser;

	// METODOS-----------------------------------------------------
	// cargar usuario
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setIdUsuario(Long id) {
		if (this.idUsuario == null) {
			this.idUsuario = id;

			usuario = (Usuario_configuracion) entityManager.find(
					Usuario_configuracion.class, this.idUsuario);
			source();
			if (cid.equals(-1l)) {
				cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
						.getBundle().getString("bitModificar"));
				usuario.setCid(cid);
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
		email = usuario.getEmail();

		// cultura del perfil
		cultura();// carga lista de culturas
		for (int i = 0; i < culturaSource.size(); i++) {
			if (culturaSource.get(i).getLocalString()
					.equals(usuario.getProfile().getLocaleString())) {
				culturaSelec = culturaSource.get(i).getIdioma();
			}
		}

		// servicios del usuario
		entidades();
		listaServicioInEntidadTarget = entityManager
				.createQuery(
						"select sie from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario order by sie.servicio.nombre")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		listaDepartamentoTarget = entityManager
				.createQuery(
						"select distinct die from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.servicio.departamento.departamentoInEntidads die where u.id =:idUsuario and die.entidad = sie.entidad order by die")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		listaEntidadTarget = entityManager
				.createQuery(
						"select distinct e from ServicioInEntidad_configuracion sie join sie.usuarios u join sie.entidad e where u.id =:idUsuario order by e")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();
		departamentos();
		entidadTargetValidation();

		// funcionario
		tipoFuncionarioTarget.clear();
		tipoFuncionarioTarget = entityManager
				.createQuery(
						"select t from TipoFuncionario_configuracion t join t.usuarios u where u.id =:idUsuario order by t.valor")
				.setParameter("idUsuario", usuario.getId()).getResultList();

		cargoTarget = new ArrayList<CargoFuncionario_configuracion>();
		cargoTarget.clear();
		cargoTarget = entityManager
				.createQuery(
						"select c from CargoFuncionario_configuracion c join c.usuarios u where u.id =:idUsuario order by c.valor")
				.setParameter("idUsuario", usuario.getId()).getResultList();

		List<TipoFuncionario_configuracion> tipo_func = new ArrayList<TipoFuncionario_configuracion>();
		tipo_func.clear();
		tipo_func = entityManager
				.createQuery(
						"select t from TipoFuncionario_configuracion t order by t.valor")
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

		// roles
		rolsTarget.clear();
		rolsTarget = entityManager
				.createQuery(
						"select r from Role_configuracion r join r.usuarios u where u.id =:idUsuario and (r.eliminado = false or r.eliminado = null) order by r.name")
				.setParameter("idUsuario", usuario.getId()).getResultList();

		// validacion de roles
		List<Role_configuracion> roles = new ArrayList<Role_configuracion>();
		roles.clear();
		roles = entityManager
				.createQuery(
						"select r from Role_configuracion r where (r.eliminado = false or r.eliminado = null) order by r.name")
				.getResultList();
		for (int i = 0; i < roles.size(); i++) {
			if (estaRol(roles.get(i).getId()) == -1)
				rolsSource.add(roles.get(i));
		}

		usuarioValidacion = entityManager.find(Usuario_configuracion.class,
				user.getId());

		if (this.usuario.getPersona() != null
				&& this.usuario.getPersona().getDatosLaborales() != null) {
			DatosLaborales laborales = this.usuario.getPersona()
					.getDatosLaborales();
			this.formacion = laborales.getFormacion() != null ? laborales
					.getFormacion().getValor() : null;
			this.grupoPersonal = laborales.getGrupoPersonal() != null ? laborales
					.getGrupoPersonal().getValor() : null;
			this.estadoLaboral = laborales.getEstadoLaboral() != null ? laborales
					.getEstadoLaboral().getValor() : null;
			this.tipoNomina = laborales.getTipoNomina() != null ? laborales
					.getTipoNomina().getValor() : null;
			this.divisionPersonal = laborales.getDivisionPersonal() != null ? laborales
					.getDivisionPersonal().getValor() : null;
			this.division = laborales.getDivision() != null ? laborales
					.getDivision().getValor() : null;
			this.subdivisionPersonal = laborales.getSubdivisionPersonal() != null ? laborales
					.getSubdivisionPersonal().getValor() : null;
			this.sociedad = laborales.getSociedad() != null ? laborales
					.getSociedad().getValor() : null;
			this.unidadOrganizativa = laborales.getUnidadOrganizativa() != null ? laborales
					.getUnidadOrganizativa().getValor() : null;
			this.telefonoOficina = laborales.getTelefonoOficina();
			this.posicion = laborales.getPosicion() != null ? laborales
					.getPosicion().getValor() : null;
			// this.tiempoServicio = laborales.getTiempoServicio();
			// this.unidadTiempo = laborales.getUnidadTiempo() != null ?
			// laborales
			// .getUnidadTiempo().getValor() : null;
			this.manoDominante = laborales.getManoDominante() != null ? laborales
					.getManoDominante().getValor() : null;
			this.numeroPersonal = laborales.getNumeroPersonal();
			this.aseguradoIvss = laborales.getAseguradoIvss();
			this.tipoZonaLaboral = laborales.getTipoZonaLaboral() != null ? laborales
					.getTipoZonaLaboral().getValor() : null;
			this.numHijos = laborales.getNumHijos();
		}
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

	public List<String> cultura() {
		List<SelectItem> listaSelectItem = localeSelector.getSupportedLocales();
		List<String> lista = new ArrayList<String>();
		culturaSource = new ArrayList<Cultura>();
		for (int i = 0; i < listaSelectItem.size(); i++) {
			/** puse un if para eliminar de momento los otros idiomas**/
			if(listaSelectItem.get(i).getLabel().equals(SeamResourceBundle
					.getBundle().getString("idiomaEsp"))){
			Cultura c = new Cultura(i, listaSelectItem.get(i).getLabel(),
					listaSelectItem.get(i).getValue().toString());
			culturaSource.add(c);
			lista.add(c.cultura());
			}
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public void entidades() {

		/**
		 * @author yurien 24/04/2014 Se agrega la nueva restriccion para que
		 *         muestre las entidades que pertenecen al anillo configurado
		 * **/
		listaEntidadSource = entityManager
				.createQuery(
						"select ent from Entidad_configuracion ent "
								+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
								// + "ent.perteneceARhio = true "
								+ "order by ent.nombre").getResultList();
	}

	@SuppressWarnings("unchecked")
	public void departamentos() {
		listaDepartamentoSource.clear();
		for (int i = 0; i < listaEntidadTarget.size(); i++) {
			List<DepartamentoInEntidad_configuracion> l = entityManager
					.createQuery(
							"select e from Departamento_configuracion d join d.departamentoInEntidads e where e.entidad.id =:idEntidad order by e.departamento.nombre")
					.setParameter("idEntidad",
							listaEntidadTarget.get(i).getId()).getResultList();
			listaDepartamentoSource.addAll(l);
		}
		departamentoTargetValidation();
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

	@SuppressWarnings("unchecked")
	public void servicios() {
		listaServicioInEntidadSource.clear();
		for (int j = 0; j < listaDepartamentoTarget.size(); j++) {
			List<ServicioInEntidad_configuracion> ls = entityManager
					.createQuery(
							"select s from ServicioInEntidad_configuracion s "
									+ "where s.servicio.departamento.id =:idDepartamento "
									+ "and s.entidad.id =:idEntidad and s.servicio.servicioFisico = true order by s.servicio.nombre")
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

	public void subirServicios() {

	}

	// calcula la edad actual del usuario
	public int calcularEdad(Date fechaNacimiento) {
		try {
			Date fechaAct = Calendar.getInstance().getTime();
			SimpleDateFormat dfa = new SimpleDateFormat("yyyy");
			SimpleDateFormat dfm = new SimpleDateFormat("MM");
			SimpleDateFormat dfd = new SimpleDateFormat("dd");
			int anoNac = Integer.parseInt(dfa.format(fechaNacimiento));
			int mesNac = Integer.parseInt(dfm.format(fechaNacimiento));
			int diaNac = Integer.parseInt(dfd.format(fechaNacimiento));
			int anoAct = Integer.parseInt(dfa.format(fechaAct));
			int mesAct = Integer.parseInt(dfm.format(fechaAct));
			int diaAct = Integer.parseInt(dfd.format(fechaAct));
			int edad = anoAct - anoNac;
			if (edad <= 0)
				return 0;
			if (mesAct < mesNac)
				edad--;
			else {
				if (diaAct < diaNac)
					edad--;
			}
			return edad;
		} catch (Exception e) {
			return 0;// TODO: handle exception
		}
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

			// le cambio de tamaño
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

	public void subirSignature() {
		// para acceder a la direccion deseada
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context
				.getRealPath("resources/modCommon/usersignatures");
		rootpath += "/" + this.usuario.getUsername() + ".png";

		try {
			// copiando el fichero
			File file = new File(rootpath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(this.signature);

			// le hago el procesamiento
			BufferedImage originalImage = ImageIO.read(file);
			int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
					: originalImage.getType();

			// le cambio de tamaño
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
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();

		return resizedImage;
	}
	
	private PasswordStrength getPasswordStrength() { 
	    RuleBase ruleBase = null; 
	    try { 
	 
	      ruleBase = rulesParser.readRule("/comun/passwordStrength.drl", 
	          RulesDirectoryBase.business_rules); 
	    } catch (Exception e) { 
	      e.printStackTrace(); 
	    } 
	    PasswordStrength passwordStrength = new PasswordStrength(); 
	     
	    StatefulSession session = ruleBase.newStatefulSession(); 
	    session.insert(passwordStrength); 
	    session.fireAllRules();     
	    session = ruleBase.newStatefulSession(); 
	    session.insert(passwordStrength); 
	    session.fireAllRules(); 
	    return passwordStrength; 
	  } 

	@SuppressWarnings("unchecked")
	@Transactional
	@End()
	public String modificar() throws Exception {

		PasswordStrength passwordStrength = this.getPasswordStrength(); 
		// validaciones
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[25];
		r[9] = validations.requeridoM(this.userName, "userName", facesMessages);
		if (!keepPassword) {
			r[10] = validations.requeridoM(this.contrasenna, "password",
					facesMessages);
			r[11] = validations.requeridoM(this.verificarPassword,
					"repitPassword", facesMessages);
			if (!r[10] && !r[11]) { 
		        r[24]= validations.passwordStrength(this.contrasenna, passwordStrength, "password", facesMessages); 
		    } 
		}
		r[12] = validations.requeridoM(this.culturaSelec, "cultura",
				facesMessages);
		r[13] = validations.requeridoM(this.nombre, "name", facesMessages);
		r[14] = validations.requeridoM(this.primerApellido, "primerApellido",
				facesMessages);
		r[15] = validations.imagen(this.url_foto, "addPhoto", facesMessages);
		//r[16] = validations.requeridoM(this.cedula, "cedula", facesMessages);
		//r[17] = validations.requeridoM(this.fechaNacimiento, "fecha",
		//		facesMessages);

		r[18] = validations.longitudM(this.contrasenna, "password",
				facesMessages);
		r[19] = validations.longitudM(this.verificarPassword, "repitPassword",
				facesMessages);

		String us = this.userName.toLowerCase();
		if (us.endsWith("@pdvsa.com")) {
			us = us.substring(0, us.length() - 10);
		}
		r[0] = validations.textnumberlowercase(us, "userName", facesMessages);
		r[1] = validations.textM(this.nombre, "name", facesMessages);
		r[2] = validations.textM(this.primerApellido, "primerApellido",
				facesMessages);
		r[3] = validations.textM(this.segundoApellido, "segundoApellido",
				facesMessages);

		r[7] = validations.cedulaM(this.cedula, user.getProfile(), "cedula",
				facesMessages);
		r[5] = validations.pasaporteM(this.pasaporte, "pasaporte",
				facesMessages);

		r[6] = validations.addresM(this.direccionParticular,
				"direccionParticular", facesMessages);
		r[4] = validations.phoneM(this.telefono, "telefonopersonal", facesMessages);
		r[8] = validations.passwordsM(contrasenna, verificarPassword,
				"repitPassword", facesMessages);

		r[20] = validations.requeridoList(this.listaServicioInEntidadTarget,
				"ListShuttleServicios2", facesMessages);
		r[21] = validations.requeridoList(this.listaDepartamentoTarget,
				"ListShuttleDepartamentos", facesMessages);
		r[22] = validations.requeridoList(this.listaEntidadTarget,
				"ListShuttleEntidades", facesMessages);
		r[23] = validations.imagen(this.url_signature, "addSignature",
				facesMessages);
		
		// validate email
		r[16] = validations.emailM(this.email, "email", facesMessages);

		
		// Senalar item tab 'usuario' si algunos de sus elementos son invalidos
		if (r[0] || r[1] || r[2] || r[3] || r[4] || r[5] || r[6] || r[7]
				|| r[8] || r[9] || r[10] || r[11] || r[12] || r[13] || r[14]
				|| r[15] || r[16] || r[18] || r[19] || r[24]) {
			facesMessages.addToControlFromResourceBundle("usuario",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("datosIncorrectos"));
		}
		// Senalar item tab 'servicios' si algunos de sus elementos son
		// invalidos
		if (r[20] || r[21] || r[22]) {
			facesMessages.addToControlFromResourceBundle("servicios",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("valorRequerido"));
		}
		if (r[23]) {
			facesMessages.addToControlFromResourceBundle("firma",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("datosIncorrectos"));
		}

		for (int i = 23; i < 24; i++) {
			if (r[i]) {
				selectedTab = "firma";
				return null;
			}
		}
		for (int i = 20; i < 23; i++) {
			if (r[i]) {
				selectedTab = "tabservicios";
				return null;
			}
		}
		for (int i = 0; i < 19; i++) {
			if (r[i]) {
				selectedTab = "usuario";
				return null;
			}
		}
		
		if (r[24]) {
			selectedTab = "usuario";
			return null;
		}

		// validacion existe usuario
		List<String> allnick = entityManager
				.createQuery(
						"select s.username from Usuario_configuracion s where s.id <>:id and s.eliminado = false")
				.setParameter("id", this.usuario.getId()).getResultList();

		if (allnick.contains(userName.toString().trim())) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("usuarioExistente"));
			selectedTab = "usuario";
			return null;
		}
		List<String> allpasaporte = entityManager.createQuery(
				"select s.pasaporte from Usuario_configuracion s where s.id <>:id and s.pasaporte <> ''")
				.setParameter("id", this.usuario.getId()).getResultList();

		if (allpasaporte.contains(pasaporte.toString().trim()) && !pasaporte.equals("")) {
			facesMessages.addToControlFromResourceBundle(
					"buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"pasaporteExistente"));
			selectedTab = "usuario";
			return null;
		}
		List<String> allcedula = entityManager.createQuery(
				"select s.cedula from Usuario_configuracion s where s.id <>:id")
				.setParameter("id", this.usuario.getId()).getResultList();

		if (allcedula.contains(cedula.toString().trim()) && !cedula.equals("")) {
			facesMessages
					.addToControlFromResourceBundle("buttonAceptar",
							Severity.ERROR, SeamResourceBundle.getBundle()
									.getString("cedulaExistente"));
			selectedTab = "usuario";
			return null;
		}
		
		// password 
	    if (!keepPassword) { 
	      MessageDigest digest = MessageDigest.getInstance("MD5"); 
	      digest.update(contrasenna.getBytes()); 
	      String md5pass = new String(Hex.encodeHex(digest.digest())); 
	       
	      /*Comprobando si el password no esta ya en las 24 contrasenas anteriores*/ 
	      List<PasswordHistory_configuracion> phl = entityManager.createQuery( 
	          "select ph from PasswordHistory_configuracion ph where ph.usuario.id = :id and ph.password = :password") 
	          .setParameter("id", this.usuario.getId()) 
	          .setParameter("password", md5pass) 
	          .getResultList(); 
	      if(phl.size() > 0){ 
	        facesMessages.addToControlFromResourceBundle("password", 
	          Severity.ERROR, SeamResourceBundle.getBundle() 
	              .getString("passwordHistoryError")); 
	        selectedTab = "usuario"; 
	        return null; 
	      } 
	      PasswordHistory_configuracion passwordHistory = new PasswordHistory_configuracion(); 
	      passwordHistory.setUsuario(usuario); 
	      passwordHistory.setPassword(md5pass); 
	      entityManager.persist(passwordHistory); 
	       
	      usuario.setPassword(md5pass); 
	      usuario.setPasswordDate(new Date()); 
	    }

		// datos del usuario
		usuario.setUsername(userName.trim());
		usuario.setNombre(this.nombre.trim());
		usuario.setPrimerApellido(this.primerApellido.trim());
		usuario.setSegundoApellido(this.segundoApellido.trim());
		usuario.setCedula(this.cedula.trim());
		usuario.setPasaporte(this.pasaporte.trim());
		usuario.setDireccionParticular(this.direccionParticular.trim());
		usuario.setTelefono(this.telefono.trim());
		usuario.setEmail(this.email.trim());
		usuario.setFechaNacimiento(this.fechaNacimiento);
		if (usuario.getPersona() == null) {
			usuario.setPersona(new Persona());
		}
		usuario.getPersona().setNombres(usuario.getNombre());
		usuario.getPersona().setApellido1(usuario.getPrimerApellido());
		usuario.getPersona().setApellido2(usuario.getSegundoApellido());
		usuario.getPersona().setCedula(usuario.getCedula());
		usuario.getPersona().setFechaNacimiento(usuario.getFechaNacimiento());

		// Se pone esta linea aca porque cuando se intentaba modificar un
		// usuario y este no tenia asociado
		// una persona,a la hora de crear el certificado raiz,daba un error de
		// transient entity,ya que la
		// persona nunca se habia puesto en el contexto de persistencia
		entityManager.persist(usuario.getPersona());
		
		// foto
		if (!url_foto.isEmpty()) {
			try {
				this.subirPhoto();
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "errorfoto");
				return null;
			}
		}
		// signature
		if (!url_signature.isEmpty()) {
			try {
				this.subirSignature();
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "errorfoto");
				return null;
			}
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

		listaSIE = entityManager
				.createQuery(
						"select sie from ServicioInEntidad_configuracion sie join sie.usuarios u where u.id =:idUsuario")
				.setParameter("idUsuario", this.usuario.getId())
				.getResultList();

		// persistiendo servicios en la bd
		for (ServicioInEntidad_configuracion servicio : listaSIE) {
			if (listaServicioInEntidadTarget.contains(servicio))
				listaServicioInEntidadTarget.remove(servicio);
			else
				usuario.getServicioInEntidads().remove(servicio);
		}

		for (ServicioInEntidad_configuracion servicio : listaServicioInEntidadTarget) {
			entityManager.persist(servicio);
		}

		usuario.getServicioInEntidads().addAll(listaServicioInEntidadTarget);

		if (!listaServicioInEntidadTarget.isEmpty())

			// cultura
			for (int i = 0; i < culturaSource.size(); i++) {
				if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
					usuario.getProfile().setLocaleString(
							culturaSource.get(i).getLocalString());
				}
			}

		editarDatosLaborales(usuario);

		// modificando certificado o generando uno nuevo en caso de no tener
		boolean res = raFunctions.modUserKeyStore(usuario);
		if (!res) {
			facesMessages.add("Error al  modificar el keystore del usuario.");
			selectedTab = "usuario";
			return null;
		}
		// fin modificando certificado o generando uno nuevo en caso de no tener

		entityManager.merge(usuario);
		entityManager.flush();

		return "verDetalles";
	}

	

	@SuppressWarnings("unchecked")
	private void editarDatosLaborales(Usuario_configuracion usuario) {
		DatosLaborales datosLaborales = null;
		if (usuario.getPersona() == null) {
			Persona userpersona = new Persona();
			datosLaborales = new DatosLaborales();
			entityManager.persist(datosLaborales);
			userpersona.setDatosLaborales(datosLaborales);
			userpersona.setDatosLaborales(datosLaborales);
			userpersona.setApellido1(usuario.getPrimerApellido());
			userpersona.setApellido2(usuario.getSegundoApellido());
			userpersona.setCedula(usuario.getCedula());
			userpersona.setCid(this.cid);
			userpersona.setEliminado(false);
			userpersona.setFechaNacimiento(usuario.getFechaNacimiento());
			userpersona.setNombres(usuario.getNombre());
			// userpersona.setIdSexo(3L);
			entityManager.persist(userpersona);
			usuario.setPersona(userpersona);
		} else
			datosLaborales = usuario.getPersona().getDatosLaborales();

		if (datosLaborales == null) {
			return;
		}

		List<Formacion> formaciones = entityManager
				.createQuery(
						"select for from Formacion for where for.valor = :form")
				.setParameter("form", formacion).getResultList();
		if (formaciones.size() > 0)
			datosLaborales.setFormacion(formaciones.get(0));
		List<GrupoPersonal> grupoPersonal = entityManager
				.createQuery(
						"select gr from GrupoPersonal gr where gr.valor = :grupopers")
				.setParameter("grupopers", this.grupoPersonal).getResultList();
		if (grupoPersonal.size() > 0)
			datosLaborales.setGrupoPersonal(grupoPersonal.get(0));
		List<EstadoLaboral> ocupaciones = entityManager
				.createQuery(
						"select es from EstadoLaboral es where es.valor = :estlab")
				.setParameter("estlab", estadoLaboral).getResultList();
		if (ocupaciones.size() > 0)
			datosLaborales.setEstadoLaboral(ocupaciones.get(0));
		List<TipoNomina> areas = entityManager
				.createQuery(
						"select ti from TipoNomina ti where ti.valor = :tiponom")
				.setParameter("tiponom", tipoNomina).getResultList();
		if (areas.size() > 0)
			datosLaborales.setTipoNomina(areas.get(0));
		List<DivisionPersonal> divisionesp = entityManager
				.createQuery(
						"select di from DivisionPersonal di where di.valor = :divper")
				.setParameter("divper", divisionPersonal).getResultList();
		if (divisionesp.size() > 0)
			datosLaborales.setDivisionPersonal(divisionesp.get(0));
		List<Division> divisiones = entityManager
				.createQuery("select di from Division di where di.valor = :div")
				.setParameter("div", division).getResultList();
		if (divisiones.size() > 0)
			datosLaborales.setDivision(divisiones.get(0));
		List<SubdivisionPersonal> subdivisiones = entityManager
				.createQuery(
						"select sub from SubdivisionPersonal sub where sub.valor = :subdiv")
				.setParameter("subdiv", subdivisionPersonal).getResultList();
		if (subdivisiones.size() > 0)
			datosLaborales.setSubdivisionPersonal(subdivisiones.get(0));
		List<Sociedad> sociedades = entityManager
				.createQuery(
						"select so from Sociedad so where so.valor = :socied")
				.setParameter("socied", this.sociedad).getResultList();
		if (sociedades.size() > 0)
			datosLaborales.setSociedad(sociedades.get(0));
		List<UnidadOrganizativa> unidades = entityManager
				.createQuery(
						"select un from UnidadOrganizativa un where un.valor = :unidadorg")
				.setParameter("unidadorg", unidadOrganizativa).getResultList();
		if (unidades.size() > 0)
			datosLaborales.setUnidadOrganizativa(unidades.get(0));
		datosLaborales.setTelefonoOficina(telefonoOficina);
		List<Posicion> posicioness = entityManager
				.createQuery("select po from Posicion po where po.valor = :pos")
				.setParameter("pos", this.posicion).getResultList();
		if (posicioness.size() > 0)
			datosLaborales.setPosicion(posicioness.get(0));
		// datosLaborales.setTiempoServicio(this.tiempoServicio);
		List<UnidadTiempo> unidads = entityManager
				.createQuery(
						"select un from UnidadTiempo un where un.valor = :unidadt")
				.setParameter("unidadt", this.unidadTiempo).getResultList();
		// if (unidads.size() > 0)
		// datosLaborales.setUnidadTiempo(unidads.get(0));
		// datosLaborales.setUnidadTiempo(unidads.get(0));
		List<ManoDominante> manos = entityManager
				.createQuery(
						"select ma from ManoDominante ma where ma.valor = :mno")
				.setParameter("mno", this.manoDominante).getResultList();
		if (manos.size() > 0)
			datosLaborales.setManoDominante(manos.get(0));
		datosLaborales.setNumeroPersonal(this.numeroPersonal);
		datosLaborales.setAseguradoIvss(aseguradoIvss);
		List<TipoZonaLaboral> tiposzonas = entityManager
				.createQuery(
						"select tipo from TipoZonaLaboral tipo where tipo.valor = :tipozonal")
				.setParameter("tipozonal", this.tipoZonaLaboral)
				.getResultList();
		if (tiposzonas.size() > 0)
			datosLaborales.setTipoZonaLaboral(tiposzonas.get(0));
		datosLaborales.setNumHijos(this.numHijos);
		entityManager.persist(datosLaborales);
		entityManager.flush();
	}

	// PROPIEDADES-------------------------------------------------
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getContrasenna() {
		return contrasenna;
	}

	public void setContrasenna(String contrasenna) {
		this.contrasenna = contrasenna;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getPrimerApellido() {
		return primerApellido;
	}

	public void setPrimerApellido(String primerApellido) {
		this.primerApellido = primerApellido;
	}

	public String getSegundoApellido() {
		return segundoApellido;
	}

	public void setSegundoApellido(String segundoApellido) {
		this.segundoApellido = segundoApellido;
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
		this.pasaporte = pasaporte;
	}

	public String getDireccionParticular() {
		return direccionParticular;
	}

	public void setDireccionParticular(String direccionParticular) {
		this.direccionParticular = direccionParticular;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	public Boolean getUsername() {
		return username;
	}

	public void setUsername(Boolean username) {
		this.username = username;
	}

	public void verificarCargos() {

	}

	public void validarServicio() {

	}

	public Long getIdCargo() {
		return idCargo;
	}

	public void setIdCargo(Long idCargo) {
		this.idCargo = idCargo;
	}

	public Boolean getFunc_referenciado() {
		return func_referenciado;
	}

	public void setFunc_referenciado(Boolean func_referenciado) {
		this.func_referenciado = func_referenciado;
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

	public Boolean getNom() {
		return nom;
	}

	public void setNom(Boolean nom) {
		this.nom = nom;
	}

	public Boolean getCed() {
		return ced;
	}

	public void setCed(Boolean ced) {
		this.ced = ced;
	}

	public Boolean getFnac() {
		return fnac;
	}

	public void setFnac(Boolean fnac) {
		this.fnac = fnac;
	}

	public Boolean getPass() {
		return pass;
	}

	public void setPass(Boolean pass) {
		this.pass = pass;
	}

	public Boolean getVerpass() {
		return verpass;
	}

	public void setVerpass(Boolean verpass) {
		this.verpass = verpass;
	}

	public int getPosCargo() {
		return posCargo;
	}

	public void setPosCargo(int posCargo) {
		this.posCargo = posCargo;
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

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
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

	public TipoFuncionario_configuracion getTipoFuncionario() {
		return tipoFuncionario;
	}

	public void setTipoFuncionario(TipoFuncionario_configuracion tipoFuncionario) {
		this.tipoFuncionario = tipoFuncionario;
	}

	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
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

	public Boolean getShowModalPanel() {
		return showModalPanel;
	}

	public void setShowModalPanel(Boolean showModalPanel) {
		this.showModalPanel = showModalPanel;
	}

	public Boolean getKeepPassword() {
		return keepPassword;
	}

	public void setKeepPassword(Boolean keepPassword) {
		this.keepPassword = keepPassword;
		if (this.keepPassword) {
			contrasenna = "";
		}
	}

	public String getVerificarPassword() {
		return verificarPassword;
	}

	public void setVerificarPassword(String verificarPassword) {
		this.verificarPassword = verificarPassword;
	}

	public Usuario_configuracion getUsuarioValidacion() {
		return usuarioValidacion;
	}

	public void setUsuarioValidacion(Usuario_configuracion usuarioValidacion) {
		this.usuarioValidacion = usuarioValidacion;
	}

	public boolean isContrasenaConValor() {
		return contrasenaConValor;
	}

	public void setContrasenaConValor(boolean contrasenaConValor) {
		this.contrasenaConValor = contrasenaConValor;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setNom(boolean nom) {
		this.nom = nom;
	}

	public void setCed(boolean ced) {
		this.ced = ced;
	}

	public void setFnac(boolean fnac) {
		this.fnac = fnac;
	}

	public void setUsername(boolean username) {
		this.username = username;
	}

	public void setPass(boolean pass) {
		this.pass = pass;
	}

	public void setVerpass(boolean verpass) {
		this.verpass = verpass;
	}

	public void setFunc_referenciado(boolean func_referenciado) {
		this.func_referenciado = func_referenciado;
	}

	public String getFormacion() {
		return formacion;
	}

	public void setFormacion(String formacion) {
		this.formacion = formacion;
	}

	public String getGrupoPersonal() {
		return grupoPersonal;
	}

	public void setGrupoPersonal(String grupoPersonal) {
		this.grupoPersonal = grupoPersonal;
	}

	public String getEstadoLaboral() {
		return estadoLaboral;
	}

	public void setEstadoLaboral(String estadoLaboral) {
		this.estadoLaboral = estadoLaboral;
	}

	public String getTipoNomina() {
		return tipoNomina;
	}

	public void setTipoNomina(String tipoNomina) {
		this.tipoNomina = tipoNomina;
	}

	public String getDivisionPersonal() {
		return divisionPersonal;
	}

	public void setDivisionPersonal(String divisionPersonal) {
		this.divisionPersonal = divisionPersonal;
	}

	public String getDivision() {
		return division;
	}

	public void setDivision(String division) {
		this.division = division;
	}

	public String getSubdivisionPersonal() {
		return subdivisionPersonal;
	}

	public void setSubdivisionPersonal(String subdivisionPersonal) {
		this.subdivisionPersonal = subdivisionPersonal;
	}

	public String getSociedad() {
		return sociedad;
	}

	public void setSociedad(String sociedad) {
		this.sociedad = sociedad;
	}

	public String getUnidadOrganizativa() {
		return unidadOrganizativa;
	}

	public void setUnidadOrganizativa(String unidadOrganizativa) {
		this.unidadOrganizativa = unidadOrganizativa;
	}

	public String getTelefonoOficina() {
		return telefonoOficina;
	}

	public void setTelefonoOficina(String telefonoOficina) {
		this.telefonoOficina = telefonoOficina;
	}

	public String getPosicion() {
		return posicion;
	}

	public void setPosicion(String posicion) {
		this.posicion = posicion;
	}

	public Integer getTiempoServicio() {
		return tiempoServicio;
	}

	public void setTiempoServicio(Integer tiempoServicio) {
		this.tiempoServicio = tiempoServicio;
	}

	public String getUnidadTiempo() {
		return unidadTiempo;
	}

	public void setUnidadTiempo(String unidadTiempo) {
		this.unidadTiempo = unidadTiempo;
	}

	public String getManoDominante() {
		return manoDominante;
	}

	public void setManoDominante(String manoDominante) {
		this.manoDominante = manoDominante;
	}

	public String getNumeroPersonal() {
		return numeroPersonal;
	}

	public void setNumeroPersonal(String numeroPersonal) {
		this.numeroPersonal = numeroPersonal;
	}

	public Boolean getAseguradoIvss() {
		return aseguradoIvss;
	}

	public void setAseguradoIvss(Boolean aseguradoIvss) {
		this.aseguradoIvss = aseguradoIvss;
	}

	public String getTipoZonaLaboral() {
		return tipoZonaLaboral;
	}

	public void setTipoZonaLaboral(String tipoZonaLaboral) {
		this.tipoZonaLaboral = tipoZonaLaboral;
	}

	public Integer getNumHijos() {
		return numHijos;
	}

	public void setNumHijos(Integer numHijos) {
		this.numHijos = numHijos;
	}

	@SuppressWarnings("unchecked")
	private DatosLaborales crearDatosLaborales() {
		DatosLaborales datosLaborales = new DatosLaborales();
		List<Formacion> formaciones = entityManager
				.createQuery(
						"select for from Formacion for where for.valor = :form")
				.setParameter("form", formacion).getResultList();
		if (formaciones.size() > 0)
			datosLaborales.setFormacion(formaciones.get(0));
		List<GrupoPersonal> grupoPersonal = entityManager
				.createQuery(
						"select gr from GrupoPersonal gr where gr.valor = :grupopers")
				.setParameter("grupopers", this.grupoPersonal).getResultList();
		if (grupoPersonal.size() > 0)
			datosLaborales.setGrupoPersonal(grupoPersonal.get(0));
		List<EstadoLaboral> ocupaciones = entityManager
				.createQuery(
						"select es from EstadoLaboral es where es.valor = :estlab")
				.setParameter("estlab", estadoLaboral).getResultList();
		if (ocupaciones.size() > 0)
			datosLaborales.setEstadoLaboral(ocupaciones.get(0));
		List<TipoNomina> areas = entityManager
				.createQuery(
						"select ti from TipoNomina ti where ti.valor = :tiponom")
				.setParameter("tiponom", tipoNomina).getResultList();
		if (areas.size() > 0)
			datosLaborales.setTipoNomina(areas.get(0));
		List<DivisionPersonal> divisionesp = entityManager
				.createQuery(
						"select di from DivisionPersonal di where di.valor = :divper")
				.setParameter("divper", divisionPersonal).getResultList();
		if (divisionesp.size() > 0)
			datosLaborales.setDivisionPersonal(divisionesp.get(0));
		List<Division> divisiones = entityManager
				.createQuery("select di from Division di where di.valor = :div")
				.setParameter("div", division).getResultList();
		if (divisiones.size() > 0)
			datosLaborales.setDivision(divisiones.get(0));
		List<SubdivisionPersonal> subdivisiones = entityManager
				.createQuery(
						"select sub from SubdivisionPersonal sub where sub.valor = :subdiv")
				.setParameter("subdiv", subdivisionPersonal).getResultList();
		if (subdivisiones.size() > 0)
			datosLaborales.setSubdivisionPersonal(subdivisiones.get(0));
		List<Sociedad> sociedades = entityManager
				.createQuery(
						"select so from Sociedad so where so.valor = :socied")
				.setParameter("socied", this.sociedad).getResultList();
		datosLaborales.setSociedad(sociedades.get(0));
		List<UnidadOrganizativa> unidades = entityManager
				.createQuery(
						"select un from UnidadOrganizativa un where un.valor = :unidadorg")
				.setParameter("unidadorg", unidadOrganizativa).getResultList();
		if (unidades.size() > 0)
			datosLaborales.setUnidadOrganizativa(unidades.get(0));
		datosLaborales.setTelefonoOficina(telefonoOficina);
		List<Posicion> posicioness = entityManager
				.createQuery("select po from Posicion po where po.valor = :pos")
				.setParameter("pos", this.posicion).getResultList();
		if (posicioness.size() > 0)
			datosLaborales.setPosicion(posicioness.get(0));
		// datosLaborales.setTiempoServicio(this.tiempoServicio);
		List<UnidadTiempo> unidads = entityManager
				.createQuery(
						"select un from UnidadTiempo un where un.valor = :unidadt")
				.setParameter("unidadt", this.unidadTiempo).getResultList();
		// if (unidads.size() > 0)
		// datosLaborales.setUnidadTiempo(unidads.get(0));
		// datosLaborales.setUnidadTiempo(unidads.get(0));
		List<ManoDominante> manos = entityManager
				.createQuery(
						"select ma from ManoDominante ma where ma.valor = :mno")
				.setParameter("mno", this.manoDominante).getResultList();
		if (manos.size() > 0)
			datosLaborales.setManoDominante(manos.get(0));
		datosLaborales.setNumeroPersonal(this.numeroPersonal);
		datosLaborales.setAseguradoIvss(aseguradoIvss);
		List<TipoZonaLaboral> tiposzonas = entityManager
				.createQuery(
						"select tipo from TipoZonaLaboral tipo where tipo.valor = :tipozonal")
				.setParameter("tipozonal", this.tipoZonaLaboral)
				.getResultList();
		if (tiposzonas.size() > 0)
			datosLaborales.setTipoZonaLaboral(tiposzonas.get(0));
		datosLaborales.setNumHijos(this.numHijos);
		return datosLaborales;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarTipoZonaLaboral() {
		List<String> manos = (List<String>) entityManager
				.createQuery(
						"select tipo.valor from TipoZonaLaboral tipo where tipo.eliminado!=true order by tipo.valor asc")
				.getResultList();
		return manos;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarManoDominante() {
		List<String> manos = (List<String>) entityManager
				.createQuery(
						"select ma.valor from ManoDominante ma where ma.eliminado!=true order by ma.valor asc")
				.getResultList();
		return manos;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarUnidadTiempo() {
		List<String> unidad = (List<String>) entityManager
				.createQuery(
						"select un.valor from UnidadTiempo un where un.eliminado!=true")
				.getResultList();
		return unidad;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarPosicion() {
		List<String> posicion = (List<String>) entityManager.createQuery(
				"select po.valor from Posicion po where po.eliminado!=true")
				.getResultList();
		return posicion;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarUnidadOrganizativa() {
		List<String> unidad = (List<String>) entityManager
				.createQuery(
						"select un.valor from UnidadOrganizativa un where un.eliminado!=true")
				.getResultList();
		return unidad;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarSociedad() {
		List<String> sociedad = (List<String>) entityManager.createQuery(
				"select so.valor from Sociedad so where so.eliminado!=true")
				.getResultList();
		return sociedad;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarSubdivisionPersonal() {
		List<String> subdivisiones = (List<String>) entityManager
				.createQuery(
						"select sub.valor from SubdivisionPersonal sub where sub.eliminado!=true")
				.getResultList();
		return subdivisiones;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarDivision() {
		List<String> divisiones = (List<String>) entityManager.createQuery(
				"select di.valor from Division di where di.eliminado!=true")
				.getResultList();
		return divisiones;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarDivisionPersonal() {
		List<String> divisiones = (List<String>) entityManager
				.createQuery(
						"select di.valor from DivisionPersonal di where di.eliminado!=true")
				.getResultList();
		return divisiones;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarTipoNomina() {
		List<String> areas = (List<String>) entityManager.createQuery(
				"select ti.valor from TipoNomina ti where ti.eliminado!=true")
				.getResultList();
		return areas;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarFormacion() {
		List<String> formaciones = (List<String>) entityManager
				.createQuery(
						"select for.valor from Formacion for where for.eliminado!=true")
				.getResultList();
		return formaciones;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarGrupoPersonal() {
		List<String> grupoPersonal = (List<String>) entityManager
				.createQuery(
						"select gr.valor from GrupoPersonal gr where gr.eliminado!=true")
				.getResultList();
		return grupoPersonal;
	}

	@SuppressWarnings("unchecked")
	public List<String> listarEstadoLaboral() {
		List<String> ocupaciones = (List<String>) entityManager
				.createQuery(
						"select es.valor from EstadoLaboral es where es.eliminado!=true")
				.getResultList();
		return ocupaciones;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	public String getUrl_signature() {
		return url_signature;
	}

	public void setUrl_signature(String url_signature) {
		this.url_signature = url_signature;
	}

}
