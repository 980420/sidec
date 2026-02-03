package gehos.configuracion.management.gestionarEntidad;

import gehos.comun.shell.ModSelectorController;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.configuracion.management.entity.Estado_configuracion;
import gehos.configuracion.management.entity.Localidad_configuracion;
import gehos.configuracion.management.entity.Municipio_configuracion;
import gehos.configuracion.management.entity.PoblacionAreaInfluencia_configuracion;
import gehos.configuracion.management.entity.TipoEntidad_configuracion;
import gehos.configuracion.management.utilidades.Validations_configuracion;

import javax.persistence.EntityManager;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Name("entidadCrearControlador")
@Scope(ScopeType.CONVERSATION)
public class EntidadCrearControlador {

	// entity data
	private String nombre = "";
	private String direccion = "";
	private String correo = "";
	private String telefonos = "";
	private String fax = "";
	private String nacion = "Venezuela";
	private String estado = "";
	private String municipio = "";
	private String localidad = "";
	private String tipoEntidad;
	private String tipoEstablecimiento = "";
	private String poblacion;
	private String camasArquitectonicas;
	private String camasPresupuestadas;
	private Date fechaApertura;
	private String caracter = "";
	private Entidad_configuracion entidad = new Entidad_configuracion();

	// otras funcionalidades
	private String provinciaEtic = "";
	private String municipioEtic = "";
	private String localidadEtic = "";

	// validation
	private boolean gestionarTipoEntidad = false;
	private boolean existLogo = false;

	@In
	EntityManager entityManager;

	@In(create = true)
	FacesMessages facesMessages;

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
		if (!nacion.equals("")) {
			List<String> naciones = naciones();

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

			int pos = naciones.indexOf(nacion);
			provinciaEtic = provincias[pos];
			municipioEtic = municipios[pos];
			localidadEtic = localidades[pos];
		}
	}

	// METODOS---------------------------------------------------------------------------------

	// cargar tipos entidades
	@SuppressWarnings("unchecked")
	public List<String> tiposEntidades() {
		return entityManager
				.createQuery(
						"select t.valor from TipoEntidad_configuracion t order by t.valor")
				.getResultList();
	}

	// devuelve lista de naciones
	@SuppressWarnings("unchecked")
	public List<String> naciones() {
		return entityManager.createQuery(
				"select n.valor from Nacion_configuracion n order by n.valor")
				.getResultList();
	}

	// devuelve lista de estados
	@SuppressWarnings("unchecked")
	public List<String> estados() {
		if (!nacion.equals(""))
			return entityManager
					.createQuery(
							"select e.valor from Estado_configuracion e where e.nacion.valor =:nacion order by e.valor")
					.setParameter("nacion", nacion).getResultList();
		return new ArrayList<String>();
	}

	// devuelve municipios a partir del estado selecionado
	@SuppressWarnings("unchecked")
	public List<String> municipios() {
		if (this.estado != "")
			return entityManager
					.createQuery(
							"select m.valor from Municipio_configuracion m "
									+ "where m.estado.valor = :estadoValor order by m.valor")
					.setParameter("estadoValor", this.estado).getResultList();
		return new ArrayList<String>();

	}

	// devuelve localidades a partir del estado y municipio seleccionado
	@SuppressWarnings("unchecked")
	public List<String> localidades() {
		List<String> ll = new ArrayList();
		if (municipio != "") {
			ll = entityManager
					.createQuery(
							"select l.valor from Localidad_configuracion l where l.municipio.valor = :municipioValor and l.municipio.estado.valor = :estadoValor order by l.valor")
					.setParameter("municipioValor", this.municipio)
					.setParameter("estadoValor", this.estado).getResultList();
			//ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
			return ll;
		}

		//ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
		return ll;
	}

	// crea la entidad
	@SuppressWarnings("unchecked")
	@End
	public String crear() {
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
						"select e from Entidad_configuracion e where e.nombre =:nombreEntidad")
				.setParameter("nombreEntidad", this.nombre.trim()).getResultList();

		if (le.size() != 0) {
			facesMessages.addToControlFromResourceBundle("buttonAceptar",
					Severity.ERROR, SeamResourceBundle.getBundle().getString(
							"entidadExistente"));
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

			// tipo de entidad
			tipoEntidad = (TipoEntidad_configuracion) entityManager
					.createQuery(
							"select t from TipoEntidad_configuracion t "
									+ "where t.valor = :tipoEntidadValor")
					.setParameter("tipoEntidadValor", this.tipoEntidad)
					.getSingleResult();
			entidad.setTipoEntidad(tipoEntidad);

			// estado
			estado = (Estado_configuracion) entityManager.createQuery(
					"select e from Estado_configuracion e "
							+ "where e.valor = :estadoValor").setParameter(
					"estadoValor", this.estado).getSingleResult();
			entidad.setEstado(estado);

			// municipio
			municipio = (Municipio_configuracion) entityManager.createQuery(
					"select m from Municipio_configuracion m "
							+ "where m.estado.id = :estadoId "
							+ "and m.valor = :municipioValor").setParameter(
					"estadoId", estado.getId()).setParameter("municipioValor",
					this.municipio).getSingleResult();
			entidad.setMunicipio(municipio);

			// localidad
			if (!this.localidad.equals("")) {
				localidad = entityManager.createQuery(
						"select l from Localidad_configuracion l "
								+ "where l.municipio.id = :municipioId "
								+ "and l.municipio.estado.id = :estadoId "
								+ "and l.valor = :localidadValor")
						.setParameter("estadoId", estado.getId()).setParameter(
								"municipioId", municipio.getId()).setParameter(
								"localidadValor", this.localidad)
						.getResultList();
				Localidad_configuracion l = localidad.get(0);
				entidad.setLocalidad(l);
			}

			entidad.setLogo("generic.png");
			entidad.setPerteneceARhio(false);// entidad asociada
			entidad.setEliminado(false);
			entityManager.persist(this.entidad);

			PoblacionAreaInfluencia_configuracion poblacionNueva = new PoblacionAreaInfluencia_configuracion();
			poblacionNueva.setEntidad(entidad);
			poblacionNueva.setValor(new Integer(poblacion));
			entityManager.persist(poblacionNueva);

			entityManager.flush();
			ModSelectorController.reloadsEntities();
			return "gotodetails";
		} catch (Exception e) {
			return null;
		}
	}

	// PROPIEDADES-----------------------------------------------------
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

	public String getNacion() {
		return nacion;
	}

	public Entidad_configuracion getEntidad() {
		return entidad;
	}

	public void setEntidad(Entidad_configuracion entidad) {
		this.entidad = entidad;
	}

	public String getLocalidad() {
		return localidad;
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

	public void setTipoEntidad(String tipoEntidad) {
		this.tipoEntidad = tipoEntidad;
	}

	public void setTipoEstablecimiento(String tipoEstablecimiento) {
		this.tipoEstablecimiento = tipoEstablecimiento;
	}

	public boolean isGestionarTipoEntidad() {
		return gestionarTipoEntidad;
	}

	public void setGestionarTipoEntidad(boolean gestionarTipoEntidad) {
		this.gestionarTipoEntidad = gestionarTipoEntidad;
	}

	public boolean isExistLogo() {
		return existLogo;
	}

	public void setExistLogo(boolean existLogo) {
		this.existLogo = existLogo;
	}

	public void setNacion(String nacion) {
		this.nacion = nacion;
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
