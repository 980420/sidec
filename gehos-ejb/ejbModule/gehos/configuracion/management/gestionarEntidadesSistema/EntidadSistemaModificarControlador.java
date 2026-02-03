package gehos.configuracion.management.gestionarEntidadesSistema;

import gehos.autenticacion.entity.Usuario;
import gehos.configuracion.management.entity.ActividadInvestigacionDocencia_configuracion;
import gehos.configuracion.management.entity.AsignacionFinancieraHospital_configuracion;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.EstablecimientoAreaInfluenciaHospital_configuracion;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Localidad_configuracion;
import gehos.configuracion.management.entity.Municipio_configuracion;
import gehos.configuracion.management.entity.Organismo_configuracion;
import gehos.configuracion.management.entity.PartidaPresupuestariaHospital_configuracion;
import gehos.configuracion.management.entity.PoblacionAreaInfluencia_configuracion;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.theme.Theme;
import org.jboss.seam.theme.ThemeSelector;

@Name("entidadSistemaModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadSistemaModificarControlador {

	// datos de la entidad
	private String nombre = "";
	private String direccion = "";
	private String correo = "";
	private String telefonos = "";
	private String fax = "";
	private String nacion = "";
	private String estado = "";
	private String municipio = "";
	private String localidad = "";
	private String tipoEntidad = "";
	private String tipoEstablecimiento = "";
	private String caracter = "";
	private String poblacion;
	private String camasArquitectonicas;
	private String camasPresupuestadas;
	private String camasFuncionales;
	private String cantidadMedicos;
	private String cantidadEnfermeros;
	private String cantidadTrabajadores;
	private String poblacionVal = "";
	private String tipoEntidadSelect;
	private String codigoReeup = "";
	private String organismo = "";
	private String siglas = "";
	private String observaciones = "";
	private Entidad_configuracion entidad = new Entidad_configuracion();

	// otras funcionalidades
	private String tabSeleccionado = "tabEntidad";
	private String redirec = "";
	private Long entidadId = -1l;
	private String provinciaEtic = "Estado";
	private String municipioEtic = "Municipio";
	private String localidadEtic = "Parroquia";
	List<String> naciones = new ArrayList<String>();

	// validar tipo entidad seleccion
	private boolean gestionarTipoEntidad = false;
	private boolean salir = true;

	// todos los establemcimientos que existen
	private ListadoControler<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosAreaInfluenciaHosp_controler;
	private List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosAreaInfluenciaHosp = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();
	private EstablecimientoAreaInfluenciaHospital_configuracion establecimiento = new EstablecimientoAreaInfluenciaHospital_configuracion();

	// asignaciones financieras
	private ListadoControler<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList_controler;
	private List<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList = new ArrayList<AsignacionFinancieraHospital_configuracion>();
	private AsignacionFinancieraHospital_configuracion asignacionFinanciera = new AsignacionFinancieraHospital_configuracion();

	// partida presupuestaria
	private ListadoControler<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList_controler = new ListadoControler<PartidaPresupuestariaHospital_configuracion>(
			new ArrayList<PartidaPresupuestariaHospital_configuracion>());
	private List<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList = new ArrayList<PartidaPresupuestariaHospital_configuracion>();
	private PartidaPresupuestariaHospital_configuracion partidaPresupuestaria = new PartidaPresupuestariaHospital_configuracion();

	// actividades investigativas
	private ListadoControler<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocencia_controler = new ListadoControler<ActividadInvestigacionDocencia_configuracion>(
			new ArrayList<ActividadInvestigacionDocencia_configuracion>());
	private List<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocenciaList = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
	private ActividadInvestigacionDocencia_configuracion actividadInvestigacionDocencia = new ActividadInvestigacionDocencia_configuracion();

	// Logo
	private String logoSeleccionado = "generic.png";
	private Integer cantLogos = 0;
	private byte[] logo;
	private String logoUrl;
	private static final int imgWidth = 89;
	private static final int imgHeight = 90;

	@In(create = true)
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	@In
	ThemeSelector themeSelector;

	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;

	@In
	Usuario user;

	@SuppressWarnings({ "unchecked" })
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setEntidadId(Long entidadId) {
		naciones = naciones();

		// String[] provincias = new String[naciones.size()];
		// provincias[0] = "Provincia:";
		// provincias[1] = "Provincia:";
		// provincias[2] = "Estado:";
		//
		// String[] municipios = new String[naciones.size()];
		// municipios[0] = "Municipio:";
		// municipios[1] = "Municipio:";
		// municipios[2] = "Municipio:";
		//
		// String[] localidades = new String[naciones.size()];
		// localidades[0] = "Localidad:";
		// localidades[1] = "Localidad:";
		// localidades[2] = "Parroquia:";
		//
		// if (!this.nacion.equals("")) {
		// int pos = naciones.indexOf(nacion);
		// provinciaEtic = provincias[pos];
		// municipioEtic = municipios[pos];
		// localidadEtic = localidades[pos];
		// }

		if (this.entidadId.equals(-1l)) {
			this.entidadId = entidadId;

			entidad = entityManager.find(Entidad_configuracion.class,
					this.entidadId);

			logoUrl = entidad.getLogo();

			// establecimientos
			establecimientosAreaInfluenciaHosp = entityManager
					.createQuery(
							"select e from EstablecimientoAreaInfluenciaHospital_configuracion e join e.entidads ent where ent.id = :idEntidad and e.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();
			establecimientosAreaInfluenciaHosp_controler = new ListadoControler<EstablecimientoAreaInfluenciaHospital_configuracion>(
					establecimientosAreaInfluenciaHosp);

			// asignaciones financieras
			asignacionFinancieraList = entityManager
					.createQuery(
							"select a from AsignacionFinancieraHospital_configuracion a join a.entidad ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			asignacionFinancieraList_controler = new ListadoControler<AsignacionFinancieraHospital_configuracion>(
					asignacionFinancieraList);

			// partidas presupuestarias
			partidaPresupuestariaList = entityManager
					.createQuery(
							"select p from PartidaPresupuestariaHospital_configuracion p join p.entidad ent where ent.id = :idEntidad and p.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			partidaPresupuestariaList_controler = new ListadoControler<PartidaPresupuestariaHospital_configuracion>(
					partidaPresupuestariaList);

			// actividades
			actividadInvestigacionDocenciaList = entityManager
					.createQuery(
							"select a from ActividadInvestigacionDocencia_configuracion a join a.entidads ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();
			actividadInvestigacionDocencia_controler = new ListadoControler<ActividadInvestigacionDocencia_configuracion>(
					actividadInvestigacionDocenciaList);

			// poblacion
			Long ultimaPoblacion = (Long) entityManager
					.createQuery(
							"select max (p.id) from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId")
					.setParameter("entidadId", entidadId).getSingleResult();

			if (ultimaPoblacion != null) {
				Integer poblacion = (Integer) entityManager
						.createQuery(
								"select p.valor from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId and p.id =:ultimaPoblacion")
						.setParameter("ultimaPoblacion", ultimaPoblacion)
						.setParameter("entidadId", entidadId).getSingleResult();
				poblacionVal = poblacion.toString();
				this.poblacion = poblacionVal;
			} else {
				poblacion = "";
				poblacionVal = poblacion;
				this.poblacion = poblacionVal;
			}

			nombre = entidad.getNombre();
			direccion = entidad.getDireccion();
			telefonos = entidad.getTelefonos();
			correo = entidad.getCorreo();
			fax = entidad.getFax();
			if (entidad.getCamasArquitectonicas() != null)
				camasArquitectonicas = entidad.getCamasArquitectonicas()
						.toString();
			if (entidad.getCamasPresupuestadas() != null)
				camasPresupuestadas = entidad.getCamasPresupuestadas()
						.toString();
			if (entidad.getCantidadMedicos() != null)
				cantidadMedicos = entidad.getCantidadMedicos().toString();
			if (entidad.getCantidadEnfermeros() != null)
				cantidadEnfermeros = entidad.getCantidadEnfermeros().toString();
			if (entidad.getCantidadTrabajadores() != null)
				cantidadTrabajadores = entidad.getCantidadTrabajadores()
						.toString();
			tipoEntidad = entidad.getTipoEntidad().getValor();
			tipoEstablecimiento = entidad.getTipoEntidad().getValor();
			estado = entidad.getEstado().getValor();
			nacion = entidad.getEstado().getNacion().getValor();

			@SuppressWarnings("unused")
			int pos = naciones.indexOf(nacion);
			// provinciaEtic = provincias[pos];
			// municipioEtic = municipios[pos];
			// localidadEtic = localidades[pos];

			municipio = entidad.getMunicipio().getValor();

			if (entidad.getLocalidad() != null)
				localidad = entidad.getLocalidad().getValor();
			logoSeleccionado = entidad.getLogo();
			if (entidad.isEsPublico()) {
				caracter = SeamResourceBundle.getBundle()
						.getString("caracter1");
			} else {
				caracter = SeamResourceBundle.getBundle()
						.getString("caracter2");
			}

			this.codigoReeup = this.entidad.getCodigoReeup();
			this.organismo = ((this.entidad.getOrganismo() != null && this.entidad
					.getOrganismo().getValor() != null) ? this.entidad
					.getOrganismo().getValor() : "");
			this.siglas = this.entidad.getSiglas();
			this.observaciones = this.entidad.getObservaciones();
		}
		if (this.redirec.equals("establecimiento")) {
			establecimientosAreaInfluenciaHosp_controler = new ListadoControler<EstablecimientoAreaInfluenciaHospital_configuracion>(
					establecimientosAreaInfluenciaHosp);
		}
	}

	public boolean isSelected(String icon) {
		try {
			if (this.logoSeleccionado.equals(icon))
				return true;
		} catch (Exception exc) {
			this.logoSeleccionado = "generic.png";
		}

		return false;
	}

	// devuelve lista de organismos
	@SuppressWarnings("unchecked")
	public List<String> organismos() {
		List<String> result = new ArrayList<String>();
		result = this.entityManager
				.createQuery(
						"select o.valor from Organismo_configuracion o where (o.eliminado is null or o.eliminado = false) order by o.valor")
				.getResultList();
		//result = this.addSeleccione(result);
		return result;
	}

	

	public String[] getExistingModuleIcons() {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String rootpath = context
				.getRealPath("/resources/modCommon/entidades_logos/"
						+ theme.getTheme().get("name") + "/"
						+ theme.getTheme().get("color"));

		File file = new File(rootpath);
		cantLogos = file.list().length;
		return file.list();
	}

	// guarda el tab seleccionado
	public void cambiarTabSeleccionado(String tabSeleccionado) {
		this.tabSeleccionado = tabSeleccionado;
	}

	public String subirDatos() {
		salir = true;
		return tabSeleccionado;
	}

	public boolean logoExist() {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context
				.getRealPath("/resources/modCommon/entidades_logos/"
						+ entidad.getLogo());
		java.io.File dir = new java.io.File(rootpath);
		return dir.exists();
	}

	// sube logo entidad a servidor
	public void subirPhoto() {
		if (!logoUrl.isEmpty()) {

			// acceder a direccion deseada
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();
			String rootpath = context
					.getRealPath("resources/modCommon/entidades_logos");
			rootpath += "/" + this.entidad.getId() + ".png";

			try {
				// copiar fichero logo
				File file = new File(rootpath);
				FileOutputStream fileOutputStream = new FileOutputStream(file);
				DataOutputStream dataOutputStream = new DataOutputStream(
						fileOutputStream);
				dataOutputStream.write(this.logo);

				// procesar
				BufferedImage originalImage = ImageIO.read(file);
				int type = originalImage.getType() == 0 ? BufferedImage.TYPE_INT_ARGB
						: originalImage.getType();

				// cambiar formato logo
				BufferedImage risizeImagePng = resizeImage(originalImage, type);

				// sobreescribir logo
				ImageIO.write(risizeImagePng, "png", new File(rootpath));

				dataOutputStream.flush();
				dataOutputStream.close();
				fileOutputStream.close();
				entidad.setLogo(new Long(entidad.getId()).toString() + ".png");
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}

	}

	// cambiar formato logo
	private static BufferedImage resizeImage(BufferedImage originalImage,
			int type) {
		BufferedImage resizedImage = new BufferedImage(imgWidth, imgHeight,
				type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, imgWidth, imgHeight, null);
		g.dispose();

		return resizedImage;
	}

	// Validar gestionar tipo entidad
	public void validadTipoEntidad() {
		if (tipoEntidad.equals("<Otro>")) {
			gestionarTipoEntidad = true;
			tipoEntidad = "";
		} else
			gestionarTipoEntidad = false;
	}

	// carga lista de naciones
	@SuppressWarnings("unchecked")
	public List<String> naciones() {
		return entityManager.createQuery(
				"select n.valor from Nacion_configuracion n order by n.valor")
				.getResultList();
	}

	// cargando lista de estados
	@SuppressWarnings("unchecked")
	public List<String> estados() {
		if (!nacion.equals(""))
			return entityManager
					.createQuery(
							"select e.valor from Estado_configuracion e where e.nacion.valor =:nacion order by e.valor")
					.setParameter("nacion", nacion).getResultList();
		return new ArrayList<String>();
	}

	// cargando lista de municipios a partir del estado selecionado
	@SuppressWarnings("unchecked")
	public List<String> municipios() {
		if (this.estado != "") {
			return entityManager
					.createQuery(
							"select m.valor from Municipio_configuracion m "
									+ "where m.estado.valor = :estadoValor order by m.valor")
					.setParameter("estadoValor", this.estado).getResultList();
		}
		return new ArrayList<String>();
	}

	// cargando listado de localidades a partir del estado y municipio
	// seleccionado
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> localidades() {
		List<String> ll = new ArrayList();
		if (municipio != "") {
			ll = entityManager
					.createQuery(
							"select l.valor from Localidad_configuracion l "
									+ "where l.municipio.valor = :municipioValor "
									+ "and l.municipio.estado.valor = :estadoValor "
									+ "and l.eliminado = false order by l.valor")
					.setParameter("municipioValor", this.municipio)
					.setParameter("estadoValor", this.estado).getResultList();
			// ll.add(0,
			// SeamResourceBundle.getBundle().getString("seleccione"));
			return ll;
		}

		// ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
		return ll;
	}

	// cargar tipos entidades
	@SuppressWarnings("unchecked")
	public List<String> tiposEntidades() {
		return entityManager
				.createQuery(
						"select t.valor from TipoEntidad_configuracion t order by t.valor")
				.getResultList();
	}

	// cargar tipos establecimientos
	@SuppressWarnings("unchecked")
	public List<String> tiposEstablecimientos() {
		return entityManager.createQuery(
				"select t.valor from TipoEstablecimientoSalud_configuracion t")
				.getResultList();
	}

	public String redirecc(String pag) {
		if (pag.equals("establecimiento")) {
			this.redirec = "establecimiento";
			return "establecimiento";
		}
		return "fail";
	}

	// ESTABLECIMIENTOS
	public void seleccionarEstablecimientoEliminar(
			EstablecimientoAreaInfluenciaHospital_configuracion establecimiento) {
		this.establecimiento = establecimiento;

	}

	public void eliminarEstablecimiento() {
		establecimientosAreaInfluenciaHosp.remove(establecimiento);
		establecimientosAreaInfluenciaHosp_controler.getElementos().remove(
				establecimiento);

		if (establecimientosAreaInfluenciaHosp_controler.getResultList().size() == 0
				&& establecimientosAreaInfluenciaHosp_controler
						.getFirstResult() != 0)
			establecimientosAreaInfluenciaHosp_controler
					.setFirstResult(establecimientosAreaInfluenciaHosp_controler
							.getFirstResult()
							- establecimientosAreaInfluenciaHosp_controler
									.getMaxResults());
	}

	// ASIGNACIONES FINANCIERAS
	// eliminar asignacion financiera del hospital
	public void eliminarAsignacionFinanciera() {
		asignacionFinancieraList.remove(asignacionFinanciera);
		asignacionFinancieraList_controler.getElementos().remove(
				asignacionFinanciera);

		entidad.getAsignacionFinancieraHospitals().remove(asignacionFinanciera);

		if (asignacionFinancieraList_controler.getResultList().size() == 0
				&& asignacionFinancieraList_controler.getFirstResult() != 0)
			asignacionFinancieraList_controler
					.setFirstResult(asignacionFinancieraList_controler
							.getFirstResult()
							- asignacionFinancieraList_controler
									.getMaxResults());
	}

	public void seleccionarAsignacionFinancieraEliminar(
			AsignacionFinancieraHospital_configuracion a) {
		asignacionFinanciera = a;
	}

	// PARTIDAS PRESUPUESTARIAS
	// eliminar asignacion financiera del hospital
	public void eliminarPartidaPresupuestaria() {
		partidaPresupuestariaList.remove(partidaPresupuestaria);
		partidaPresupuestariaList_controler.getElementos().remove(
				partidaPresupuestaria);

		if (partidaPresupuestariaList_controler.getResultList().size() == 0
				&& partidaPresupuestariaList_controler.getFirstResult() != 0)
			partidaPresupuestariaList_controler
					.setFirstResult(partidaPresupuestariaList_controler
							.getFirstResult()
							- partidaPresupuestariaList_controler
									.getMaxResults());
	}

	public void seleccionarPartidaPresupuestariaEliminar(
			PartidaPresupuestariaHospital_configuracion a) {
		partidaPresupuestaria = a;
	}

	// ACTIVIDADES INVESTIGACION DOCENCIA
	// eliminar partidas presupuestarias financiera del hospital
	public void eliminarActividadesInvestigativasDocencia() {
		actividadInvestigacionDocenciaList
				.remove(actividadInvestigacionDocencia);
		actividadInvestigacionDocencia_controler.getElementos().remove(
				actividadInvestigacionDocencia);

		if (actividadInvestigacionDocencia_controler.getResultList().size() == 0
				&& actividadInvestigacionDocencia_controler.getFirstResult() != 0)
			actividadInvestigacionDocencia_controler
					.setFirstResult(actividadInvestigacionDocencia_controler
							.getFirstResult()
							- actividadInvestigacionDocencia_controler
									.getMaxResults());
	}

	public void seleccionarActividadInvestigacionDocenciaEliminar(
			ActividadInvestigacionDocencia_configuracion a) {
		actividadInvestigacionDocencia = a;
		entidad.getActividadInvestigacionDocencias().remove(a);
	}

	// Crea la entidad
	@SuppressWarnings("unchecked")
	@End
	public String modificar() {

		// validaciones
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[19];
		r[0] = validations.textM250(this.nombre, "nombre", this.facesMessages);
		r[1] = validations.addresM(this.direccion, "direccion",
				this.facesMessages);
		r[2] = validations.numberM(this.poblacion, "areaInfluencia",
				this.facesMessages);
		r[3] = validations.emailM(this.correo, "correo", this.facesMessages);
		r[4] = validations.phoneM(this.telefonos, "telefonos",
				this.facesMessages);
		r[5] = validations.textM75(this.fax, "fax", this.facesMessages);
		r[6] = validations.requeridoM(this.tipoEntidad, "tipoEntidad",
				this.facesMessages);
		r[7] = validations.requeridoM(this.caracter, "caracter",
				this.facesMessages);
		r[8] = validations.numberM(this.camasArquitectonicas,
				"camasArquitectonicas", this.facesMessages);
		r[9] = validations.numberM(this.camasPresupuestadas,
				"camasPresupuestadas", this.facesMessages);
		r[10] = validations.numberM(this.getCamasFuncionales(),
				"inputcamasfunc", this.facesMessages);
		r[11] = validations.requeridoM(this.nacion, "nacion",
				this.facesMessages);
		r[12] = validations.requeridoM(this.estado, "estado",
				this.facesMessages);
		r[13] = validations.requeridoM(this.municipio, "municipio",
				this.facesMessages);
		r[14] = validations.numberM(this.getCantidadTrabajadores(),
				"inputcantidadtrab", this.facesMessages);
		r[15] = validations.numberM(this.getCantidadMedicos(), "inputcantmed",
				this.facesMessages);
		r[16] = validations.numberM(this.getCantidadEnfermeros(),
				"inputcantenf", this.facesMessages);
		r[17] = validations.requeridoM(this.nombre, "nombre",
				this.facesMessages);
		r[18] = validations.requeridoM(this.poblacion, "areaInfluencia",
				this.facesMessages);
		for (int i = 0; i < r.length; i++) {
			if (r[i]) {
				this.tabSeleccionado = "tabEntidad";
				return null;
			}
		}

		List<Entidad_configuracion> le = entityManager
				.createQuery(
						"select e from Entidad_configuracion e where e.nombre =:nombreEntidad and e.id <>:entidadId")
				.setParameter("nombreEntidad", this.nombre.trim())
				.setParameter("entidadId", this.entidad.getId())
				.getResultList();

		if (le.size() != 0) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle()
							.getString("entidadExistente"));
			return "fail";
		}
		List<String> faxs = entityManager
				.createQuery(
						"select e from Entidad_configuracion e where e.fax =:faxEntidad and e.id <>:entidadId")
				.setParameter("faxEntidad", this.fax.trim())
				.setParameter("entidadId", this.entidad.getId())
				.getResultList();
		if (faxs.size() > 0) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString("faxExistente"));
			return "fail";
		}

		Estado_configuracion estado = new Estado_configuracion();
		Municipio_configuracion municipio = new Municipio_configuracion();
		List<Localidad_configuracion> localidad = new ArrayList<Localidad_configuracion>();

		TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();
		Organismo_configuracion organismoObject = new Organismo_configuracion();

		try {

			entidad.setNombre(this.nombre.trim());
			entidad.setDireccion(this.direccion.trim());
			entidad.setCorreo(this.correo.trim());
			entidad.setTelefonos(this.telefonos.trim());
			entidad.setFax(this.fax.trim());
			if (!camasArquitectonicas.equals(""))
				entidad.setCamasArquitectonicas(new Integer(
						camasArquitectonicas));
			else
				entidad.setCamasArquitectonicas(null);
			if (!camasPresupuestadas.equals(""))
				entidad.setCamasPresupuestadas(new Integer(
						this.camasPresupuestadas));
			else
				entidad.setCamasPresupuestadas(null);
			entidad.setLogo(logoSeleccionado);
			if (!cantidadTrabajadores.equals(""))
				entidad.setCantidadTrabajadores(new Integer(
						cantidadTrabajadores));
			else
				entidad.setCantidadTrabajadores(null);

			if (!cantidadEnfermeros.equals(""))
				entidad.setCantidadEnfermeros(new Integer(cantidadEnfermeros));
			else
				entidad.setCantidadEnfermeros(null);

			if (!cantidadMedicos.equals(""))
				entidad.setCantidadMedicos(new Integer(cantidadMedicos));
			else
				entidad.setCantidadMedicos(null);

			if (!camasFuncionales.equals(""))
				entidad.setCamasFuncionales(new Integer(camasFuncionales));
			else
				entidad.setCamasFuncionales(null);
			if (caracter.equals(SeamResourceBundle.getBundle().getString(
					"caracter1"))) {
				entidad.setEsPublico(true);
			} else
				entidad.setEsPublico(false);

			// cargando el tipo de entidad
			tipoEntidad = (TipoEntidad_configuracion) entityManager
					.createQuery(
							"select t from TipoEntidad_configuracion t "
									+ "where t.valor = :tipoEntidadValor")
					.setParameter("tipoEntidadValor", this.tipoEntidad)
					.getSingleResult();
			entidad.setTipoEntidad(tipoEntidad);

			// cargando el estado
			estado = (Estado_configuracion) entityManager
					.createQuery(
							"select e from Estado_configuracion e "
									+ "where e.valor = :estadoValor")
					.setParameter("estadoValor", this.estado).getSingleResult();
			entidad.setEstado(estado);

			// cargando el municipio
			municipio = (Municipio_configuracion) entityManager
					.createQuery(
							"select m from Municipio_configuracion m "
									+ "where m.estado.id = :estadoId "
									+ "and m.valor = :municipioValor")
					.setParameter("estadoId", estado.getId())
					.setParameter("municipioValor", this.municipio)
					.getSingleResult();
			entidad.setMunicipio(municipio);

			// cargando la localidad
			if (!this.localidad.equals("")) {
				localidad = entityManager
						.createQuery(
								"select l from Localidad_configuracion l "
										+ "where l.municipio.id = :municipioId "
										+ "and l.municipio.estado.id = :estadoId "
										+ "and l.valor = :localidadValor")
						.setParameter("estadoId", estado.getId())
						.setParameter("municipioId", municipio.getId())
						.setParameter("localidadValor", this.localidad)
						.getResultList();
				Localidad_configuracion l = localidad.get(0);
				entidad.setLocalidad(l);

			} else {
				entidad.setLocalidad(null);
			}

			this.entidad
					.setCodigoReeup(this.codigoReeup != null ? this.codigoReeup
							.trim() : this.codigoReeup);
			if (!this.organismo.equals("")) {
				organismoObject = (Organismo_configuracion) this.entityManager
						.createQuery(
								"select o from Organismo_configuracion o where o.valor = :organismoValor")
						.setParameter("organismoValor", this.organismo)
						.getSingleResult();
				this.entidad.setOrganismo(organismoObject);
			} else
				this.entidad.setOrganismo(null);
			this.entidad.setSiglas(this.siglas != null ? this.siglas.trim()
					: this.siglas);
			this.entidad.setObservaciones(this.observaciones);

			// establecimiento area de influencia
			@SuppressWarnings("unused")
			List<EstablecimientoAreaInfluenciaHospital_configuracion> listAuxEst = entityManager
					.createQuery(
							"select e from EstablecimientoAreaInfluenciaHospital_configuracion e join e.entidads ent where ent.id = :idEntidad and e.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			@SuppressWarnings("unused")
			boolean existeEstablecimineto = false;

			entidad.getEstablecimientoAreaInfluenciaHospitals().clear();

			entidad.getEstablecimientoAreaInfluenciaHospitals().addAll(
					establecimientosAreaInfluenciaHosp);

			// asignaciones financieras
			List<AsignacionFinancieraHospital_configuracion> listAsignacionesAux = entityManager
					.createQuery(
							"select a from AsignacionFinancieraHospital_configuracion a join a.entidad ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			boolean existeAsignacion = false;
			for (int j2 = 0; j2 < listAsignacionesAux.size(); j2++) {
				for (int j = 0; j < asignacionFinancieraList.size(); j++) {
					if (asignacionFinancieraList.get(j).getId()
							.equals(listAsignacionesAux.get(j2).getId())) {
						existeAsignacion = true;
						break;
					}
				}
				if (!existeAsignacion) {
					entityManager.remove(listAsignacionesAux.get(j2));
					entidad.getAsignacionFinancieraHospitals().remove(
							listAsignacionesAux.get(j2));
					listAsignacionesAux.remove(j2);
					j2--;
				}
			}

			for (int i = 0; i < asignacionFinancieraList.size(); i++) {
				asignacionFinancieraList.get(i).setEntidad(entidad);
				entityManager.persist(asignacionFinancieraList.get(i));
			}

			entidad.getAsignacionFinancieraHospitals().addAll(
					asignacionFinancieraList);

			// partidas presupuestarias
			List<PartidaPresupuestariaHospital_configuracion> listPartidaAux = entityManager
					.createQuery(
							"select p from PartidaPresupuestariaHospital_configuracion p join p.entidad ent where ent.id = :idEntidad and p.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			boolean existePartida = false;
			for (int j2 = 0; j2 < listPartidaAux.size(); j2++) {
				existePartida = false;
				for (int j = 0; j < partidaPresupuestariaList.size(); j++) {
					if (partidaPresupuestariaList.get(j).getId()
							.equals(listPartidaAux.get(j2).getId())) {
						existePartida = true;
						break;
					}
				}
				if (!existePartida) {
					entityManager.remove(listPartidaAux.get(j2));
					entidad.getPartidaPresupuestariaHospitals().remove(
							listPartidaAux.get(j2));
					listPartidaAux.remove(j2);
					j2--;
				}
			}

			for (int i = 0; i < partidaPresupuestariaList.size(); i++) {
				partidaPresupuestariaList.get(i).setEntidad(entidad);
				entityManager.persist(partidaPresupuestariaList.get(i));
			}

			entidad.getPartidaPresupuestariaHospitals().addAll(
					partidaPresupuestariaList);

			// actividades investigacion docencia
			List<ActividadInvestigacionDocencia_configuracion> actividadesAuxList = entityManager
					.createQuery(
							"select a from ActividadInvestigacionDocencia_configuracion a join a.entidads ent where ent.id = :idEntidad and a.eliminado = false")
					.setParameter("idEntidad", entidad.getId()).getResultList();

			boolean existeActividad = false;
			for (int j2 = 0; j2 < actividadesAuxList.size(); j2++) {
				for (int j = 0; j < actividadInvestigacionDocenciaList.size(); j++) {
					if (actividadInvestigacionDocenciaList.get(j).getId()
							.equals(actividadesAuxList.get(j2).getId())) {
						existeActividad = true;
						break;
					}
				}
				if (!existeActividad) {
					entidad.getActividadInvestigacionDocencias().remove(
							actividadesAuxList.get(j2));
					actividadesAuxList.remove(j2);
					j2--;
				}
			}

			entidad.getActividadInvestigacionDocencias().addAll(
					actividadInvestigacionDocenciaList);
			entityManager.persist(this.entidad);

			// asgingando la poblacion
			PoblacionAreaInfluencia_configuracion poblacionNueva = new PoblacionAreaInfluencia_configuracion();
			if (poblacionVal != poblacion) {
				poblacionNueva.setEntidad(entidad);
				poblacionNueva.setValor(new Integer(poblacion));
				entityManager.persist(poblacionNueva);
			}

			entityManager.flush();
			cancelar();
			return "gotodetails";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";

	}

	// Cancelar editar entidad
	public void cancelar() {
		gestionarTipoEntidad = false;
	}

	// Adiciona los establecimientos seleccionados
	public void AdicionarEstablecimiento(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> list) {
		this.establecimientosAreaInfluenciaHosp.addAll(list);
	}

	// PROPIEDADES----------------------------------------------
	public String getTelefonos() {
		return telefonos;
	}

	public String getNombre() {
		return nombre;
	}

	public String getDireccion() {
		return direccion;
	}

	public String getCorreo() {
		return correo;
	}

	public String getFax() {
		return fax;
	}

	public String getTipoEntidad() {
		return tipoEntidad;
	}

	public String getTipoEstablecimiento() {
		return tipoEstablecimiento;
	}

	public String getEstado() {
		return estado;
	}

	public String getMunicipio() {
		return municipio;
	}

	public String getLocalidad() {
		return localidad;
	}

	public String getNacion() {
		return nacion;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public void setTipoEntidad(String tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public void setTipoEstablecimiento(String tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}

	public void setEstado(String estado) {
		this.estado = estado;
		this.setMunicipio("");
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
		this.setLocalidad("");
	}

	public void setLocalidad(String localidad) {
		if (localidad.equals(SeamResourceBundle.getBundle().getString(
				"seleccione")))
			this.localidad = "";
		else
			this.localidad = localidad;
	}

	public EntityManager getEntityManager() {
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Long getEntidadId() {
		return entidadId;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

	public String getLogoUrl() {
		return logoUrl;
	}

	public void setLogoUrl(String logoUrl) {
		this.logoUrl = logoUrl;
	}

	public String getTipoEntidadSelect() {
		return tipoEntidadSelect;
	}

	public void setTipoEntidadSelect(String tipoEntidadSelect) {
		this.tipoEntidadSelect = tipoEntidadSelect;
	}

	public boolean isGestionarTipoEntidad() {
		return gestionarTipoEntidad;
	}

	public void setGestionarTipoEntidad(boolean gestionarTipoEntidad) {
		this.gestionarTipoEntidad = gestionarTipoEntidad;
	}

	public List<EstablecimientoAreaInfluenciaHospital_configuracion> getEstablecimientosAreaInfluenciaHosp() {
		return establecimientosAreaInfluenciaHosp;
	}

	public void setEstablecimientosAreaInfluenciaHosp(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosAreaInfluenciaHosp) {
		this.establecimientosAreaInfluenciaHosp = establecimientosAreaInfluenciaHosp;
	}

	public ListadoControler<EstablecimientoAreaInfluenciaHospital_configuracion> getEstablecimientosAreaInfluenciaHosp_controler() {
		return establecimientosAreaInfluenciaHosp_controler;
	}

	public void setEstablecimientosAreaInfluenciaHosp_controler(
			ListadoControler<EstablecimientoAreaInfluenciaHospital_configuracion> establecimientosAreaInfluenciaHosp_controler) {
		this.establecimientosAreaInfluenciaHosp_controler = establecimientosAreaInfluenciaHosp_controler;
	}

	public void setNacion(String nacion) {
		this.nacion = nacion;
		String[] provincias = new String[naciones.size()];
		provincias[0] = "Provincia:";
		provincias[1] = "Provincia:";
		provincias[2] = "Estado:";

		String[] municipios = new String[naciones.size()];
		municipios[0] = "Municipio:";
		municipios[1] = "Municipio:";
		municipios[2] = "Municipio:";

		String[] localidades = new String[naciones.size()];
		localidades[0] = "Localidad:";
		localidades[1] = "Localidad:";
		localidades[2] = "Parroquia:";

		@SuppressWarnings("unused")
		int pos = naciones.indexOf(nacion);
		// provinciaEtic = provincias[pos];
		// municipioEtic = municipios[pos];
		// localidadEtic = localidades[pos];

		this.setEstado("");
	}

	public boolean isSalir() {
		return salir;
	}

	public void setSalir(boolean salir) {
		this.salir = salir;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public void setCorreo(String correo) {
		this.correo = correo;
	}

	public void setTelefonos(String telefonos) {
		this.telefonos = telefonos;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getTabSeleccionado() {
		return tabSeleccionado;
	}

	public void setTabSeleccionado(String tabSeleccionado) {
		this.tabSeleccionado = tabSeleccionado;
	}

	public String getRedirec() {
		return redirec;
	}

	public void setRedirec(String redirec) {
		this.redirec = redirec;
	}

	public ListadoControler<AsignacionFinancieraHospital_configuracion> getAsignacionFinancieraList_controler() {
		return asignacionFinancieraList_controler;
	}

	public void setAsignacionFinancieraList_controler(
			ListadoControler<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList_controler) {
		this.asignacionFinancieraList_controler = asignacionFinancieraList_controler;
	}

	public List<AsignacionFinancieraHospital_configuracion> getAsignacionFinancieraList() {
		return asignacionFinancieraList;
	}

	public void setAsignacionFinancieraList(
			List<AsignacionFinancieraHospital_configuracion> asignacionFinancieraList) {
		this.asignacionFinancieraList = asignacionFinancieraList;
	}

	public static int getImgWidth() {
		return imgWidth;
	}

	public static int getImgHeight() {
		return imgHeight;
	}

	public AsignacionFinancieraHospital_configuracion getAsignacionFinanciera() {
		return asignacionFinanciera;
	}

	public void setAsignacionFinanciera(
			AsignacionFinancieraHospital_configuracion asignacionFinanciera) {
		this.asignacionFinanciera = asignacionFinanciera;
	}

	public EstablecimientoAreaInfluenciaHospital_configuracion getEstablecimiento() {
		return establecimiento;
	}

	public void setEstablecimiento(
			EstablecimientoAreaInfluenciaHospital_configuracion establecimiento) {
		this.establecimiento = establecimiento;
	}

	public ListadoControler<PartidaPresupuestariaHospital_configuracion> getPartidaPresupuestariaList_controler() {
		return partidaPresupuestariaList_controler;
	}

	public void setPartidaPresupuestariaList_controler(
			ListadoControler<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList_controler) {
		this.partidaPresupuestariaList_controler = partidaPresupuestariaList_controler;
	}

	public List<PartidaPresupuestariaHospital_configuracion> getPartidaPresupuestariaList() {
		return partidaPresupuestariaList;
	}

	public void setPartidaPresupuestariaList(
			List<PartidaPresupuestariaHospital_configuracion> partidaPresupuestariaList) {
		this.partidaPresupuestariaList = partidaPresupuestariaList;
	}

	public PartidaPresupuestariaHospital_configuracion getPartidaPresupuestaria() {
		return partidaPresupuestaria;
	}

	public void setPartidaPresupuestaria(
			PartidaPresupuestariaHospital_configuracion partidaPresupuestaria) {
		this.partidaPresupuestaria = partidaPresupuestaria;
	}

	public ListadoControler<ActividadInvestigacionDocencia_configuracion> getActividadInvestigacionDocencia_controler() {
		return actividadInvestigacionDocencia_controler;
	}

	public void setActividadInvestigacionDocencia_controler(
			ListadoControler<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocencia_controler) {
		this.actividadInvestigacionDocencia_controler = actividadInvestigacionDocencia_controler;
	}

	public List<ActividadInvestigacionDocencia_configuracion> getActividadInvestigacionDocenciaList() {
		return actividadInvestigacionDocenciaList;
	}

	public void setActividadInvestigacionDocenciaList(
			List<ActividadInvestigacionDocencia_configuracion> actividadInvestigacionDocenciaList) {
		this.actividadInvestigacionDocenciaList = actividadInvestigacionDocenciaList;
	}

	public ActividadInvestigacionDocencia_configuracion getActividadInvestigacionDocencia() {
		return actividadInvestigacionDocencia;
	}

	public void setActividadInvestigacionDocencia(
			ActividadInvestigacionDocencia_configuracion actividadInvestigacionDocencia) {
		this.actividadInvestigacionDocencia = actividadInvestigacionDocencia;
	}

	public String getLogoSeleccionado() {
		return logoSeleccionado;
	}

	public void setLogoSeleccionado(String logoSeleccionado) {
		this.logoSeleccionado = logoSeleccionado;
	}

	public String getCaracter() {
		return caracter;
	}

	public void setCaracter(String caracter) {
		this.caracter = caracter;
	}

	public Integer getCantLogos() {
		return cantLogos;
	}

	public void setCantLogos(Integer cantLogos) {
		this.cantLogos = cantLogos;
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

	public String getPoblacion() {
		return poblacion;
	}

	public void setPoblacion(String poblacion) {
		this.poblacion = poblacion;
	}

	public String getCamasArquitectonicas() {
		return camasArquitectonicas;
	}

	public void setCamasArquitectonicas(String camasArquitectonicas) {
		this.camasArquitectonicas = camasArquitectonicas;
	}

	public String getCamasPresupuestadas() {
		return camasPresupuestadas;
	}

	public void setCamasPresupuestadas(String camasPresupuestadas) {
		this.camasPresupuestadas = camasPresupuestadas;
	}

	public String getPoblacionVal() {
		return poblacionVal;
	}

	public void setPoblacionVal(String poblacionVal) {
		this.poblacionVal = poblacionVal;
	}

	public String getCamasFuncionales() {
		return camasFuncionales;
	}

	public void setCamasFuncionales(String camasFuncionales) {
		this.camasFuncionales = camasFuncionales;
	}

	public String getCantidadMedicos() {
		return cantidadMedicos;
	}

	public void setCantidadMedicos(String cantidadMedicos) {
		this.cantidadMedicos = cantidadMedicos;
	}

	public String getCantidadEnfermeros() {
		return cantidadEnfermeros;
	}

	public void setCantidadEnfermeros(String cantidadEnfermeros) {
		this.cantidadEnfermeros = cantidadEnfermeros;
	}

	public String getCantidadTrabajadores() {
		return cantidadTrabajadores;
	}

	public void setCantidadTrabajadores(String cantidadTrabajadores) {
		this.cantidadTrabajadores = cantidadTrabajadores;
	}

	public String getCodigoReeup(){
		return codigoReeup;
	}
	
	public void setCodigoReeup(String codigoReeup){
		this.codigoReeup = codigoReeup;
	}
	
	public String getOrganismo(){
		return organismo;
	}
	
	public void setOrganismo(String organismo){
		if(organismo != null && organismo.equalsIgnoreCase(SeamResourceBundle.getBundle().getString("seleccione")))
			organismo = "";
		this.organismo = organismo;
	}
	
	public String getSiglas(){
		return siglas;
	}
	
	public void setSiglas(String siglas){
		this.siglas = siglas;
	}
	
	public String getObservaciones(){
		return observaciones;
	}
	
	public void setObservaciones(String observaciones){
		this.observaciones = observaciones;
	}
}
