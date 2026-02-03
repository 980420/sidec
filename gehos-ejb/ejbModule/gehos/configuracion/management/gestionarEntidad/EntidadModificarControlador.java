package gehos.configuracion.management.gestionarEntidad;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import java.util.Date;
import java.util.List;

import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Localidad_configuracion;
import gehos.configuracion.management.entity.Municipio_configuracion;
import gehos.configuracion.management.entity.PoblacionAreaInfluencia_configuracion;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;

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

@Name("entidadModificarControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadModificarControlador {

	// entity data
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
	private String poblacion;
	private String camasArquitectonicas;
	private String camasPresupuestadas;
	private Date fechaApertura;
	private String caracter = "";
	private String poblacionVal = "";
	private Entidad_configuracion entidad = new Entidad_configuracion();

	private Long entidadId = -1l;
	private byte[] logo;
	private String logoUrl;
	private static final int imgWidth = 89;
	private static final int imgHeight = 90;
	private String tipoEntidadSelect;

	// otras funcionalidades
	private String provinciaEtic = "Estado";
	private String municipioEtic = "Municipio";
	private String localidadEtic = "Parroquia";
	List<String> naciones = new ArrayList<String>();

	// validar tipo entidad seleccion
	private boolean gestionarTipoEntidad = false;

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

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

			if (entidad.isEsPublico()) {
				caracter = SeamResourceBundle.getBundle()
						.getString("caracter1");
			} else {
				caracter = SeamResourceBundle.getBundle()
						.getString("caracter2");
			}

			logoUrl = entidad.getLogo();

			// poblacion
			Long ultimaPoblacion = (Long) entityManager
					.createQuery(
							"select max (p.id) from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId")
					.setParameter("entidadId", entidadId).getSingleResult();

			Integer poblacion = (Integer) entityManager
					.createQuery(
							"select p.valor from PoblacionAreaInfluencia_configuracion p join p.entidad e where e.id =:entidadId and p.id =:ultimaPoblacion")
					.setParameter("ultimaPoblacion", ultimaPoblacion)
					.setParameter("entidadId", entidadId).getSingleResult();
			poblacionVal = poblacion.toString();
			this.poblacion = poblacionVal;

			nombre = entidad.getNombre();
			direccion = entidad.getDireccion();
			telefonos = entidad.getTelefonos();
			fax = entidad.getFax();
			correo = entidad.getCorreo();
			if (entidad.getCamasArquitectonicas() != null)
				camasArquitectonicas = entidad.getCamasArquitectonicas()
						.toString();
			if (entidad.getCamasPresupuestadas() != null)
				camasPresupuestadas = entidad.getCamasPresupuestadas()
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
			fechaApertura = entidad.getFechaApertura();
			if (entidad.getLocalidad() != null)
				localidad = entidad.getLocalidad().getValor();
		}

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
							"select l.valor from Localidad_configuracion l where l.municipio.valor =:municipioValor and l.municipio.estado.valor =:estadoValor order by l.valor")
					.setParameter("municipioValor", this.municipio)
					.setParameter("estadoValor", this.estado).getResultList();
			//ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
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

	// Crea la entidad
	@SuppressWarnings("unchecked")
	@End
	public String modificar() {

		// validaciones
		Validations_configuracion validations = new Validations_configuracion();
		boolean[] r = new boolean[17];
		r[0] = validations.textM(this.nombre, "nombre", facesMessages);
		r[1] = validations.addresM(this.direccion, "direccion", facesMessages);
		r[2] = validations.emailM(this.correo, "correo", facesMessages);

		r[3] = validations.phoneM(this.telefonos, "telefonos", facesMessages);
		r[4] = validations.phoneM(this.fax, "fax", facesMessages);

		r[5] = validations.numberM(this.camasArquitectonicas,
				"camasArquitectonicas", facesMessages);
		r[6] = validations.numberM(this.camasPresupuestadas,
				"camasPresupuestadas", facesMessages);
		r[7] = validations.numberM(this.poblacion, "areaInfluencia",
				facesMessages);

		r[8] = validations.requeridoM(this.nacion, "nacion", facesMessages);
		r[9] = validations.requeridoM(this.estado, "estado", facesMessages);
		r[10] = validations.requeridoM(this.municipio, "municipio",
				facesMessages);
		r[11] = false;
		r[12] = validations.requeridoM(this.poblacion, "areaInfluencia",
				facesMessages);
		r[13] = validations.requeridoM(this.nombre, "nombre", facesMessages);
		r[14] = validations.requeridoM(this.tipoEntidad, "tipoEntidad",
				facesMessages);
		r[15] = validations
				.requeridoM(this.caracter, "caracter", facesMessages);
		r[16] = false;
		// validations.text(this., "");

		for (int i = 0; i < r.length; i++) {
			if (r[i]) {
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

		Estado_configuracion estado = new Estado_configuracion();
		Municipio_configuracion municipio = new Municipio_configuracion();
		List<Localidad_configuracion> localidad = new ArrayList<Localidad_configuracion>();
		TipoEntidad_configuracion tipoEntidad = new TipoEntidad_configuracion();

		if (caracter.equals(SeamResourceBundle.getBundle().getString(
				"caracter1"))) {
			entidad.setEsPublico(true);
		} else
			entidad.setEsPublico(false);

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
			entidad.setFechaApertura(fechaApertura);

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

			entityManager.persist(this.entidad);

			PoblacionAreaInfluencia_configuracion poblacionNueva = new PoblacionAreaInfluencia_configuracion();
			if (poblacionVal != poblacion) {
				poblacionNueva.setEntidad(entidad);
				poblacionNueva.setValor(new Integer(poblacion));
				entityManager.persist(poblacionNueva);
			}

			entityManager.flush();
			entidadId = -1l;
			return "gotodetails";
		} catch (Exception e) {
			return null;
		}

	}

	// Cancelar editar entidad
	public void cancelar() {
		entidadId = -1l;
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

	public Date getFechaApertura() {
		return fechaApertura;
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

		// int pos = naciones.indexOf(nacion);
		// provinciaEtic = provincias[pos];
		// municipioEtic = municipios[pos];
		// localidadEtic = localidades[pos];

		this.setEstado("");
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

	public void setFechaApertura(Date fechaApertura) {
		this.fechaApertura = fechaApertura;
	}

	public String getCaracter() {
		return caracter;
	}

	public void setCaracter(String caracter) {
		this.caracter = caracter;
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

}
