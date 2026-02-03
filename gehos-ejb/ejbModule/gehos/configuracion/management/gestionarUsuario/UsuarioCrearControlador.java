package gehos.configuracion.management.gestionarUsuario;

import gehos.autenticacion.entity.Usuario;
import gehos.autenticacion.session.custom.MaxFailedAccessAttempts; 
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
import gehos.comun.reglas.parser.RulesDirectoryBase; 
import gehos.comun.reglas.parser.RulesParser; 
import gehos.configuracion.management.entity.CargoFuncionario_configuracion;
import gehos.configuracion.management.entity.DepartamentoInEntidad_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.PasswordHistory_configuracion;
import gehos.configuracion.management.entity.Profile_configuracion;
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
import org.drools.RuleBase; 
import org.drools.StatefulSession;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
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

@Name("usuarioCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class UsuarioCrearControlador {

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
	private byte[] signature;
	private String url_signature = "";
	@In 
	private RulesParser rulesParser; 

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

	// datos del usuario
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
	private Long cid = -1l;
	private String nick;
	private String nombrePhoto;
	private byte[] data;
	private static final int imgWidth = 74;
	private static final int imgHeight = 74;
	private boolean nom, ced, fnac, username, pass, verpass,
			func_referenciado = false;
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private String email;

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

	// departamento y entidades
	private String tabSelect = "usuario";
	private String oldPassword = ""; // valida si cambio la contrasena
	private String verificarPassword = ""; // repetir la contrasena

	FacesMessage facesMessage;

	// @In(create = true)
	// AdministradorFirmas administradorFirmas;
	@In(create = true)
	RaFunctions raFunctions;
	@In
	Usuario user;
	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	@In
	LocaleSelector localeSelector;

	@In
	IBitacora bitacora;
	
	

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		if (cid.equals(-1l)) {
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
					.getBundle().getString("bitCrear"));
		}

	}

	@SuppressWarnings("unchecked")
	@Create
	public void Source() {
		cultura();
		tipoFuncionarioSource = entityManager
				.createQuery(
						"select t from TipoFuncionario_configuracion t order by t.valor")
				.getResultList();
		rolsSource = entityManager
				.createQuery(
						"select r from Role_configuracion r where r.eliminado = false or r.eliminado = null order by r.name")
				.getResultList();

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
		 * @author yurien 28/03/2014 Se agrega la nueva restriccion para que
		 *         muestre las entidades que pertenecen al anillo configurado
		 * **/
		listaEntidadSource = entityManager
				.createQuery(
						"select ent from Entidad_configuracion ent "
								+ "where ent.instanciaHis.id = #{anilloHisConfig.hisInstanceNumber} "
								// + "where ent.perteneceARhio = true "
								+ "order by ent.nombre").getResultList();

	}

	@SuppressWarnings("unchecked")
	public void departamentos() {
		listaDepartamentoSource.clear();
		for (int i = 0; i < listaEntidadTarget.size(); i++) {
			List<DepartamentoInEntidad_configuracion> l = entityManager
					.createQuery(
							"select e from Departamento_configuracion d "
									+ "join d.departamentoInEntidads e "
									+ "where e.entidad.id =:idEntidad order by d.nombre")
					.setParameter("idEntidad",
							listaEntidadTarget.get(i).getId()).getResultList();
			listaDepartamentoSource.addAll(l);
		}
		departamentoTargetValidation();
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

	public void subirCargos() {
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

			// le cambio de tamano
			BufferedImage risizeImagePng = resizeImage(originalImage, type);

			// la sobreescribo
			// RenderedImage renderedImage = new RenderedImage();
			ImageIO.write(risizeImagePng, "png", new File(rootpath));

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
	@End
	public String crear() throws Exception {
		
		PasswordStrength passwordStrength = this.getPasswordStrength(); 
		// validaciones
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[25];
		r[9] = validations.requeridoM(this.nick, "userName", facesMessages);
		r[10] = validations.requeridoM(this.contrasenna, "password",
				facesMessages);
		r[11] = validations.requeridoM(this.verificarPassword, "repitPassword",
				facesMessages);
		r[12] = validations.requeridoM(this.culturaSelec, "cultura",
				facesMessages);
		r[13] = validations.requeridoM(this.nombre, "name", facesMessages);
		r[14] = validations.requeridoM(this.primerApellido, "primerApellido",
				facesMessages);
		r[15] = validations.imagen(this.nombrePhoto,"addPhoto", facesMessages);
		//r[16] = validations.requeridoM(this.cedula, "cedula", facesMessages);
		//r[17] = validations.requeridoM(this.fechaNacimiento, "fecha",
			//	facesMessages);

		r[18] = validations.longitudM(this.contrasenna, "password",
				facesMessages);
		r[19] = validations.longitudM(this.verificarPassword, "repitPassword",
				facesMessages);

		r[0] = validations.textnumberlowercase(this.nick, "userName",
				facesMessages);
		r[1] = validations.textM(this.nombre, "name", facesMessages);
		r[2] = validations.textM(this.primerApellido, "primerApellido",
				facesMessages);
		r[3] = validations.textM(this.segundoApellido, "segundoApellido",
				facesMessages);

		r[4] = validations.cedulaM(this.cedula, user.getProfile(), "cedula",
			facesMessages);
		r[5] = validations.pasaporteM(this.pasaporte, "pasaporte",
			facesMessages);

		r[6] = validations.addresM(this.direccionParticular,
				"direccionParticular", facesMessages);
		r[7] = validations.phoneM(this.telefono, "telefonopers", facesMessages);
		r[8] = validations.passwordsM(contrasenna, verificarPassword,
				"repitPassword", facesMessages);

		r[20] = validations.requeridoList(this.listaServicioInEntidadTarget,
				"ListShuttleServicios2", facesMessages);
		r[21] = validations.requeridoList(this.listaDepartamentoTarget,
				"ListShuttleDepartamentos", facesMessages);
		r[22] = validations.requeridoList(this.listaEntidadTarget,
				"ListShuttleEntidades", facesMessages);
		r[23] = validations.imagen(this.url_signature,"addSignature", facesMessages);
		r[24]= validations.passwordStrength(this.contrasenna, passwordStrength, "password", facesMessages); 
		
		// validate email Required and is Email
		r[16] = validations.emailM(this.email, "email", facesMessages);

		
		// Senalar item tab 'usuario' si algunos de sus elementos son invalidos
		if (r[0] || r[1] || r[2] || r[3]|| r[4]  || r[5] || r[6] || r[7]
				|| r[8] || r[9] || r[10] || r[11] || r[12] || r[13] || r[14]
				|| r[15]  || r[16] || r[18] || r[19] || r[24]){
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
				tabSelect = "firma";
				return null;
			}
		}
		for (int i = 20; i < 23; i++) {
			if (r[i]) {
				tabSelect = "servicios";
				return null;
			}
		}
		for (int i = 0; i < 19; i++) {
			if (r[i]) {
				tabSelect = "usuario";
				return null;
			}
		}
		
		if (r[24]) {
			tabSelect = "usuario";
			return null;
		}
	

		// validacion existe usuario
		List<String> allnick = entityManager.createQuery(
				"select s.username from Usuario_configuracion s where s.eliminado = false")
				.getResultList();

		if (allnick.contains(nick.toString().trim())) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("usuarioExistente"));
			tabSelect = "usuario";
			return null;
		}
		List<String> allpasaporte = entityManager.createQuery(
				"select s.pasaporte from Usuario_configuracion s")
				.getResultList();

		if (allpasaporte.contains(pasaporte.toString().trim()) && !pasaporte.equals("")) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("pasaporteExistente"));
			tabSelect = "usuario";
			return null;
		}
		List<String> allcedula = entityManager.createQuery(
				"select s.cedula from Usuario_configuracion s")
				.getResultList();

		if (allcedula.contains(cedula.toString().trim()) && !cedula.equals("")) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("cedulaExistente"));
			tabSelect = "usuario";
			return null;
		}

		try {
			listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
			listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
			listaE = new ArrayList<Entidad_configuracion>();

			// datos del usuario
			usuario.setUsername(nick.trim());
			usuario.setNombre(this.nombre.trim());
			usuario.setPrimerApellido(this.primerApellido.trim());
			usuario.setSegundoApellido(this.segundoApellido.trim());
			usuario.setCedula(this.cedula.trim());
			usuario.setPasaporte(this.pasaporte.trim());
			usuario.setDireccionParticular(this.direccionParticular.trim());
			usuario.setTelefono(this.telefono.trim());
			usuario.setFechaNacimiento(this.fechaNacimiento);
			usuario.setEmail(this.email);

			try {
				if (!this.nombrePhoto.equals("")) {
					this.subirPhoto();
				} else {
					facesMessages.addToControlFromResourceBundle("message",
							Severity.ERROR, "errorfoto");

				}
			} catch (Exception e) {
				facesMessages.addToControlFromResourceBundle("message",
						Severity.ERROR, "errorfoto");
				return null;
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

			usuario.setCid(this.cid);
			usuario.setEliminado(false);

			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(contrasenna.getBytes());
			String md5pass = new String(Hex.encodeHex(digest.digest()));
			usuario.setPassword(md5pass);
			usuario.setPasswordDate(new Date());

			// Tipo funcionario
			Set<TipoFuncionario_configuracion> listaFuncionario = new HashSet<TipoFuncionario_configuracion>();
			listaFuncionario.addAll(tipoFuncionarioTarget);

			Set<CargoFuncionario_configuracion> listaCargoFuncionario = new HashSet<CargoFuncionario_configuracion>();
			listaCargoFuncionario.addAll(cargoTarget);

			Set<Role_configuracion> listaRol = new HashSet<Role_configuracion>();
			listaRol.addAll(rolsTarget);

			usuario.setTipoFuncionarios(listaFuncionario);
			usuario.setCargoFuncionarios(listaCargoFuncionario);
			usuario.setRoles(listaRol);

			Profile_configuracion perfil = new Profile_configuracion();
			perfil.setEliminado(false);
			perfil.setLocaleString("es");
			perfil.setTheme("alas-verde");
			perfil.setTreeLikeMenu(true);
			perfil.setUsuario(usuario);
			usuario.setProfile(perfil);

			// SERVICIO IN ENTIDAD
			listaSIE = new ArrayList<ServicioInEntidad_configuracion>();
			listaDIE = new ArrayList<DepartamentoInEntidad_configuracion>();
			listaE = new ArrayList<Entidad_configuracion>();

			// persistiendo servicios en la bd
			for (ServicioInEntidad_configuracion servicio : listaServicioInEntidadTarget) {
				servicio = entityManager.merge(servicio);
			}
			usuario.getServicioInEntidads()
					.addAll(listaServicioInEntidadTarget);

			Persona userpersona = new Persona();
			DatosLaborales datosLaborales = crearDatosLaborales();
			entityManager.persist(datosLaborales);
			userpersona.setDatosLaborales(datosLaborales);
			userpersona.setApellido1(usuario.getPrimerApellido());
			userpersona.setApellido2(usuario.getSegundoApellido());
			userpersona.setCedula(usuario.getCedula());
			userpersona.setCid(this.cid);
			userpersona.setEliminado(false);
			userpersona.setFechaNacimiento(usuario.getFechaNacimiento());
			userpersona.setNombres(usuario.getNombre());
			userpersona.setCorreoElectronico(this.email);
			// userpersona.setIdSexo(3L);
			entityManager.persist(userpersona);
			usuario.setPersona(userpersona);

			entityManager.persist(usuario);
			PasswordHistory_configuracion passwordHistory = new PasswordHistory_configuracion(); 
		    passwordHistory.setUsuario(usuario); 
		    passwordHistory.setPassword(md5pass); 
		    entityManager.persist(passwordHistory); 

			// CULTURA
			for (int i = 0; i < culturaSource.size(); i++) {
				if (culturaSource.get(i).getIdioma().equals(culturaSelec)) {
					perfil.setLocaleString(culturaSource.get(i)
							.getLocalString());
				}
			}

			entityManager.persist(perfil);

			// generando llaves publica y privada
			boolean res = raFunctions.createUserKeyStore(usuario);
			if (!res) {
				facesMessages.add("Error al crear el keystore del usuario.");
				tabSelect = "usuario";
				return null;
			}
			// fin generando llaves publica y privada

			entityManager.flush();
			


			return "verDetalles";
		} catch (Exception error) {
			error.printStackTrace();
			return null;
		}

	}

	// PROPIEDADES-------------------------------------------------
	public Date getFechaNacimiento() {
		return fechaNacimiento;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
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

	public void setFechaNacimiento(Date fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
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

	public List<Cultura> getCulturaSource() {
		return culturaSource;
	}

	public void setCulturaSource(List<Cultura> culturaSource) {
		this.culturaSource = culturaSource;
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

	public Usuario_configuracion getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public FacesMessages getFacesMessages() {
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages) {
		this.facesMessages = facesMessages;
	}

	public LocaleSelector getLocaleSelector() {
		return localeSelector;
	}

	public void setLocaleSelector(LocaleSelector localeSelector) {
		this.localeSelector = localeSelector;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
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

	public String getTabSelect() {
		return tabSelect;
	}

	public void setTabSelect(String tabSelect) {
		this.tabSelect = tabSelect;
	}

	public String getUrl_foto() {
		return url_foto;
	}

	public void setUrl_foto(String url_foto) {
		this.url_foto = url_foto;
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
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
	

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}


}
