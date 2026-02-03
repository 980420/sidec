package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.VariableWrapper;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Nomenclador_ensayo;
import gehos.ensayo.entity.PresentacionFormulario_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.TipoDato_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("crearHojaCrd")
@Scope(ScopeType.CONVERSATION)
public class CrearHojaCrd {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;
	@In
	Usuario user;

	FacesMessage facesMessage;
	// Variables de la Hoja
	private HojaCrd_ensayo hojaCrd = new HojaCrd_ensayo();
	private long cid;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	private List<Seccion_ensayo> secciones = new ArrayList<Seccion_ensayo>();
	// Variables de la seccion
	private Seccion_ensayo seccion = new Seccion_ensayo();
	// Variables del Grupo
	private List<GrupoVariables_ensayo> grupos = new ArrayList<GrupoVariables_ensayo>();
	private GrupoVariables_ensayo grupo = new GrupoVariables_ensayo();
	private List<String> listaEstadosGrupo;
	// Variables de la variable
	private List<Variable_ensayo> variables = new ArrayList<Variable_ensayo>();
	private Variable_ensayo variable = new Variable_ensayo();
	private String seccionVariable = "";
	private String seccionGrupo = "";
	private String grupoVariable = "";
	private String nombreTipoDato = "";
	private String nombrePresentacionFormulario = "";
	private Boolean deshabilitarPresentacionFormulario = true;
	private Boolean habilitarNomenclador = false;
	private Boolean inicioConversacion = false;
	private boolean submitAttempted = false;

	private Nomenclador_ensayo nomencladorVariable = new Nomenclador_ensayo();
	// private List<NomencladorValor_ensayo> listadoNomencladorValor = new
	// ArrayList<NomencladorValor_ensayo>();
	private String valoresTexto = "";
	private List<String> listaUbicacionRespuesta;
	private String valorPorDefecto = "";

	// Inicializar hoja CRD.
	public void inicializarHojaCrd() {
		this.usuario = (Usuario_ensayo) entityManager.find(
				Usuario_ensayo.class, user.getId());
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("prm_bitacoraCrear_ens"));
		listaEstadosGrupo = new ArrayList<String>();
		listaEstadosGrupo.add(SeamResourceBundle.getBundle().getString(
				"prm_show_ens"));
		listaEstadosGrupo.add(SeamResourceBundle.getBundle().getString(
				"prm_hide_ens"));
		listaUbicacionRespuesta = new ArrayList<String>();
		listaUbicacionRespuesta.add(SeamResourceBundle.getBundle().getString(
				"prm_horizontal_ens"));
		listaUbicacionRespuesta.add(SeamResourceBundle.getBundle().getString(
				"prm_vertical_ens"));
		inicioConversacion=true;

	}



	// Metodo que devuelve el titÃƒÂºlo de las secciones
	public List<String> listadoSecciones() {
		List<String> listadoSecciones = new ArrayList<String>();
		for (int i = 0; i < secciones.size(); i++)
			listadoSecciones.add(secciones.get(i).getEtiquetaSeccion());
		return listadoSecciones;
	}

	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");
	// Metodo que devuelve el encabezado de los grupos
	public List<String> listadoGrupos() {
		List<String> listadoGrupos = new ArrayList<String>();
		for (int i = 0; i < grupos.size(); i++) {
			if (grupos.get(i).getSeccion().getEtiquetaSeccion()
					.equals(seccionVariable))
				listadoGrupos.add(grupos.get(i).getEtiquetaGrupo());
		}
		listadoGrupos.add(0, seleccione);
		return listadoGrupos;
	}

	// Metodo que devuelve si una variable es de tipo nomenclador
	public Boolean existeVariablesNomenclador(Variable_ensayo variable) {
		try {
			if (!variable.getNomenclador().equals(null))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}

	}
	//add validar secciones con variables
	public Seccion_ensayo seccionSinVariable() {
		try {			
			for (int i = 0; i < secciones.size(); i++){
				if(!secciones.get(i).getEliminado()){
					boolean tieneVariable = false;
					for (int j = 0; j < variables.size(); j++){
						if(!variables.get(j).getEliminado() && variables.get(j).getSeccion().getEtiquetaSeccion().equals(secciones.get(i).getEtiquetaSeccion()) ){
							tieneVariable = true;
							break;
						}
					}
					if(!tieneVariable)
						return secciones.get(i);
				}
			}

			return null;
		} catch (Exception e) {
			return null;
		}

	}

	//add validar grupos con variables
	public GrupoVariables_ensayo grupoSinVariable() {
		try {
			for (int i = 0; i < grupos.size(); i++){
				if(!grupos.get(i).getEliminado()){
					boolean tieneVariable = false;
					for (int j = 0; j < variables.size(); j++){
						if(variables.get(j).getGrupoVariables()!=null){
							if(!variables.get(j).getEliminado() && variables.get(j).getGrupoVariables().getEtiquetaGrupo().equals(grupos.get(i).getEtiquetaGrupo()) ){
								tieneVariable = true;
								break;
							}
						}
					}
					if(!tieneVariable)
						return grupos.get(i);
				}

			}
			return null;
		} catch (Exception e) {
			return null;
		}

	}
	// Metodo para crear la hoja CRD.
	public String insertarHojaCrd() {
		try {
			long count = (Long) entityManager
					.createQuery(
							"select count(hojaCrd) from HojaCrd_ensayo hojaCrd "
									+ "where hojaCrd.eliminado <> true "
									+ "and hojaCrd.nombreHoja = :nombreHoja "
									+ "and hojaCrd.estudio.id = :idEstudio")
									.setParameter("nombreHoja", hojaCrd.getNombreHoja())
									.setParameter("idEstudio",
											seguridadEstudio.EstudioActivo().getId())
											.getSingleResult();
			if (count > 0) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionHojaCrd_ens");
				return "error";
			}
			if (secciones.size() == 0) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionSecciones_ens");
				return "error";
			}
			if (variables.size() == 0) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionVariables_ens");
				return "error";
			}
			
			for (GrupoVariables_ensayo grupo : grupos) {
				if(grupo.getVariables().size()==1){
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("El grupo "+grupo.getEtiquetaGrupo()+" no puede tener una sola variable");
					return "error";
				}
			}
			Seccion_ensayo seccionAux = seccionSinVariable();
			if(seccionAux!=null){			
				facesMessages.clear();
				facesMessages.add(new FacesMessage(
						SeamResourceBundle.getBundle().getString(
								"msg_validacionSeccionesSinVariableUno_ens")
								+ " " + seccionAux.getEtiquetaSeccion()+" "+SeamResourceBundle.getBundle().getString(
										"msg_validacionSeccionesSinVariableDos_ens"),
										null));
				return "error";

			}
			GrupoVariables_ensayo grupoAux = grupoSinVariable();
			if(grupoAux!=null){
				facesMessages.clear();
				facesMessages.add(new FacesMessage(
						SeamResourceBundle.getBundle().getString(
								"msg_validacionGruposSinVariableUno_ens")
								+ " " + grupoAux.getEtiquetaGrupo()+" "+SeamResourceBundle.getBundle().getString(
										"msg_validacionGruposSinVariableDos_ens"),
										null));
				return "error";
			}
			
			for (GrupoVariables_ensayo grupo : grupos) {
				int countVariablesNoEliminadasGrupo=0;
				for (Variable_ensayo variable_ensayo : this.variables) {
					if(!variable_ensayo.getEliminado() && variable_ensayo.getGrupoVariables()==grupo) {
						countVariablesNoEliminadasGrupo++;
					}
				}
				if(countVariablesNoEliminadasGrupo==1){
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("El grupo "+grupo.getEtiquetaGrupo()+" no puede tener una sola variable");
					return "error";
				}
			}
			
			hojaCrd.setEliminado(false);
			hojaCrd.setFechaCreacion(Calendar.getInstance().getTime());
			hojaCrd.setEstudio(seguridadEstudio.getEstudioEntidadActivo()
					.getEstudio());
			;
			hojaCrd.setUsuario(usuario);
			hojaCrd.setCid(cid);
			entityManager.persist(hojaCrd);
			for (int i = 0; i < secciones.size(); i++)
				entityManager.persist(secciones.get(i));
			for (int i = 0; i < grupos.size(); i++)
				entityManager.persist(grupos.get(i));
			for (int i = 0; i < variables.size(); i++) {
				if (existeVariablesNomenclador(variables.get(i))) {
					entityManager.persist(variables.get(i).getNomenclador());

					Set<NomencladorValor_ensayo> lista = variables.get(i).getNomenclador().getNomencladorValors();
					for (NomencladorValor_ensayo nomencladorValor_ensayo : lista) {

						entityManager.persist(nomencladorValor_ensayo);

					}


				}
				entityManager.persist(variables.get(i));
			}

			entityManager.flush();

			return "ok";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";

		}
	}

	// Validar que el titulo no comience con numero
	public void numeroEnteroPositivo(FacesContext context,
			UIComponent component, Object value) {

		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString(

							"msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
			// tenga caracteres extrannos

		{
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString("msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());

		} catch (Exception e) {
			this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
					.getString("msg_validacionNumero_ens"), null);
			this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ValidatorException(facesMessage);
		}

	}

	// Metodo que devuelve la secciÃƒÂ³n dado la etiqueta
	public Seccion_ensayo seccionListado(String etiqueta) {
		for (int i = 0; i < secciones.size(); i++) {
			if (etiqueta.equals(secciones.get(i).getEtiquetaSeccion()))
				return secciones.get(i);

		}
		return null;

	}

	// Metodo que devuelve el grupo dado la etiqueta
	public GrupoVariables_ensayo grupoListado(String etiqueta) {
		for (int i = 0; i < grupos.size(); i++) {
			if (etiqueta.equals(grupos.get(i).getEtiquetaGrupo()))
				return grupos.get(i);

		}
		return null;

	}

	// Metodo que devuelve la variable dado el nombre
	public Variable_ensayo variableListado(String nombre) {
		for (int i = 0; i < variables.size(); i++) {
			if (nombre.equals(variables.get(i).getNombreVariable()))
				return variables.get(i);

		}
		return null;

	}

	// Metodo para eliminar caracteres especiales
	public String eliminarCaracteresEspeciales(String cadena) {
		String caracteresEspeciales = "\u00E1\u00E0\u00E4\u00E9\u00E8\u00EB\u00ED\u00EC\u00EF\u00F3\u00F2\u00F6\u00FA\u00F9\u00FC\u00F1\u00C1\u00C0\u00C4\u00C9\u00C8\u00CB\u00CD\u00CC\u00CF\u00D3\u00D2\u00D6\u00DA\u00D9\u00DC\u00D1\u00E7\u00C7";
		String caracteresNormales = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
		String result = cadena;
		for (int i = 0; i < caracteresEspeciales.length(); i++) {
			result = result.replace(caracteresEspeciales.charAt(i),
					caracteresNormales.charAt(i));
		}
		return result;
	}

	// Metodo para generar el identificador
	// lo dejo pero no se debe usar
	public String generarIdentificador(String original) {
		String result = eliminarCaracteresEspeciales(original);
		result = result.replace(" ", "_");
		return result.toUpperCase();
	}

	// Metodo que devuelve si una secciÃƒÂ³n esta asociada a una variable
	public Boolean seccionAsociadaVariable(String etiqueta) {
		for (int i = 0; i < variables.size(); i++) {
			if (etiqueta.equals(variables.get(i).getSeccion()
					.getEtiquetaSeccion())) {
				return true;
			}
		}
		return false;
	}

	// Metodo que devuelve si una secciÃƒÂ³n esta asociada a un grupo
	public Boolean seccionAsociadaGrupo(String etiqueta) {
		for (int i = 0; i < grupos.size(); i++) {
			if (etiqueta
					.equals(grupos.get(i).getSeccion().getEtiquetaSeccion())) {
				return true;
			}
		}
		return false;
	}

	// Metodo para eliminar la secciÃƒÂ³n del listado de secciones
	public void eliminarSeccion(Seccion_ensayo seccion) {
		try {
			if (seccionAsociadaVariable(seccion.getEtiquetaSeccion())) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionSeccionAsociadaVariable_ens");
			} else if (seccionAsociadaGrupo(seccion.getEtiquetaSeccion())) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionSeccionAsociadaGrupo_ens");
			} else {
				secciones.remove(seccion);
				if (seccion.getTituloSeccion().equals(seccionVariable))
					seccionVariable = "";
			}

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	// Metodo para saber si el grupo tiene una variable tiene grupo
	public boolean variableTieneGrupo(Variable_ensayo variable) {
		try {
			if (!variable.getGrupoVariables().equals(null))
				return true;

			return false;
		} catch (Exception e) {
			return false;
		}
	}

	// Metodo que devuelve si una grupo esta asociado a una variable
	public Boolean grupoAsociado(String etiqueta) {
		try {
			for (int i = 0; i < variables.size(); i++) {
				if (variableTieneGrupo(variables.get(i))) {
					if (etiqueta.equals(variables.get(i).getGrupoVariables()
							.getEtiquetaGrupo())) {
						return true;
					}
				}
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	// Metodo para eliminar el grupo del listado de grupos
	public void eliminarGrupo(GrupoVariables_ensayo grupo) {
		try {
			if (grupoAsociado(grupo.getEtiquetaGrupo())) {
				facesMessages.clear();
				facesMessages
				.addFromResourceBundle("msg_validacionGrupoAsociado_ens");
			} else {
				grupos.remove(grupo);
				if (grupo.getEncabezado().equals(grupoVariable))
					grupoVariable = SeamResourceBundle.getBundle().getString(
							"lbl_seleccione_ens");
			}

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	// Metodo que devuelve los tipos de datos
	public List<String> listadoTipoDato() {
		List<String> listadoTipoDato = (ArrayList<String>) entityManager
				.createQuery("select td.nombre from TipoDato_ensayo td")
				.getResultList();
		return listadoTipoDato;

	}

	// Metodo que devuelve el tipo de dato por el nombre
	public TipoDato_ensayo tipoDato() {
		TipoDato_ensayo tipoDato = (TipoDato_ensayo) entityManager
				.createQuery(
						"select td from TipoDato_ensayo td "
								+ "where td.nombre = :nombre")
								.setParameter("nombre", nombreTipoDato).getSingleResult();
		return tipoDato;
	}

	// Metodo que devuelve las presentaciones del formulario dado el tipo de
	// dato
	@SuppressWarnings("unchecked")
	public List<String> listadoPresentacionFormulario() {
		List<String> listadoPresentacionFormulario = new ArrayList<String>();
		if (!deshabilitarPresentacionFormulario) {
			listadoPresentacionFormulario = (ArrayList<String>) entityManager
					.createQuery(
							"select pf.nombre from PresentacionFormulario_ensayo pf "
									+ "where pf.tipoDato.id = :idTipoDato")
									.setParameter("idTipoDato", tipoDato().getId())
									.getResultList();
		}
		return listadoPresentacionFormulario;
	}

	// Metodo que devuelve la presentacion del formulario por nombre y dado el
	// tipo de dato
	public PresentacionFormulario_ensayo presentacionFormulario() {
		PresentacionFormulario_ensayo presentacionFormulario = (PresentacionFormulario_ensayo) entityManager
				.createQuery(
						"select pf from PresentacionFormulario_ensayo pf "
								+ "where pf.nombre = :nombre "
								+ "and pf.tipoDato.id = :idTipoDato")
								.setParameter("nombre", nombrePresentacionFormulario)
								.setParameter("idTipoDato", tipoDato().getId())
								.getSingleResult();
		return presentacionFormulario;
	}

	// Metodo para eliminar la variable
	public void eliminarVariable(Variable_ensayo variable) {
		try {
			variables.remove(variable);
			// if (variable.getTipoDato().getCodigo().equals("NOM"))
			// eliminarNomencladorValor(variable);

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	// Metodo para eliminar el valor del nomenclador dado la variable
	/*
	 * public void eliminarNomencladorValor(Variable_ensayo variable) { try {
	 * List<NomencladorValor_ensayo> listadoAuxNomencladorValor = new
	 * ArrayList<NomencladorValor_ensayo>(); for (int i = 0; i <
	 * listadoNomencladorValor.size(); i++) { if
	 * (variable.getNomenclador().equals(
	 * listadoNomencladorValor.get(i).getNomenclador()))
	 * listadoAuxNomencladorValor.add(listadoNomencladorValor .get(i)); } for
	 * (int i = 0; i < listadoAuxNomencladorValor.size(); i++)
	 * listadoNomencladorValor.remove(listadoAuxNomencladorValor .get(i));
	 * 
	 * } catch (Exception e) { facesMessages.clear();
	 * facesMessages.add(e.getMessage());
	 * 
	 * } }
	 */

	/**
	 * Metodos para los modificar de seccion. grupos y variables
	 */
	// Metodo para modificar la seccion del listado de secciones
	boolean modificandoSeccion = false;
	int posSeccionModificar = -1;

	public void modificarSeccion(Seccion_ensayo seccion) {
		try {

			this.posSeccionModificar = secciones.indexOf(seccion);
			this.seccion = new Seccion_ensayo();

			this.seccion.setEliminado(seccion.getEliminado());
			this.seccion.setEtiquetaSeccion(seccion.getEtiquetaSeccion());
			this.seccion.setTituloSeccion(seccion.getTituloSeccion());
			this.seccion.setSubtitulo(seccion.getSubtitulo());
			this.seccion.setInstrucciones(seccion.getInstrucciones());
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());
			this.seccion.setUsuario(usuario);

			modificandoSeccion = true;

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	public void adicionarSeccion() {
		try {
			facesMessages.clear();
			Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
					user.getId());

			if (modificandoSeccion) {
				boolean error = false;
				// Se cambio la etiqueta
				if (secciones.get(posSeccionModificar).getEtiquetaSeccion() == seccion
						.getEtiquetaSeccion()) {
					String etiquetaSeccion = seccion.getEtiquetaSeccion();
					if (seccionListado(etiquetaSeccion) != null) {
						facesMessages.clear();
						facesMessages
						.addFromResourceBundle("msg_validacionExisteSeccion_ens");
						error = true;
					}
				}

				if (!error) {

					this.secciones.get(posSeccionModificar).setHojaCrd(hojaCrd);
					this.secciones.get(posSeccionModificar).setEtiquetaSeccion(
							seccion.getEtiquetaSeccion());
					this.secciones.get(posSeccionModificar).setTituloSeccion(
							seccion.getTituloSeccion());
					this.secciones.get(posSeccionModificar).setSubtitulo(
							seccion.getSubtitulo());
					this.secciones.get(posSeccionModificar).setEliminado(false);
					this.secciones.get(posSeccionModificar).setInstrucciones(
							seccion.getInstrucciones());
					this.secciones.get(posSeccionModificar).setUsuario(usuario);
					seccion = new Seccion_ensayo();

					modificandoSeccion = false;
					posSeccionModificar = -1;

				}

			} else {
				String etiquetaSeccion = seccion.getEtiquetaSeccion();
				if (seccionListado(etiquetaSeccion) != null) {
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("msg_validacionExisteSeccion_ens");

				} else {
					seccion.setHojaCrd(hojaCrd);
					seccion.setEtiquetaSeccion(etiquetaSeccion);
					seccion.setTituloSeccion(seccion.getTituloSeccion());
					seccion.setSubtitulo(seccion.getSubtitulo());
					seccion.setEliminado(false);
					seccion.setUsuario(usuario);
					secciones.add(seccion);
					seccion = new Seccion_ensayo();
				}
			}

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	// Metodos para adicionar y modificar los grupos
	boolean modificandoGrupo = false;
	int posGrupoModificar = -1;

	public void modificarGrupo(GrupoVariables_ensayo grupo) {
		try {

			this.posGrupoModificar = grupos.indexOf(grupo);
			this.grupo = new GrupoVariables_ensayo();

			this.grupo.setEliminado(grupo.getEliminado());
			this.grupo.setEtiquetaGrupo(grupo.getEtiquetaGrupo());
			this.grupo.setDescripcionGrupo(grupo.getDescripcionGrupo());
			this.grupo.setEncabezado(grupo.getEncabezado());
			this.grupo.setNumMaxRepeticiones(grupo.getNumMaxRepeticiones());
			this.grupo.setNumRepeticiones(grupo.getNumRepeticiones());
			this.grupo.setSeccion(grupo.getSeccion());
			this.grupo.setEstadoGrupo(grupo.getEstadoGrupo());

			modificandoGrupo = true;
			seccionGrupo = grupo.getSeccion().getEtiquetaSeccion();

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	public void adicionarGrupo() {
		try {
			facesMessages.clear();

			if (modificandoGrupo) {
				boolean error = false;
				// Se cambio la etiqueta
				if (grupos.get(posGrupoModificar).getEtiquetaGrupo() == grupo
						.getEtiquetaGrupo()) {
					String etiquetaGrupo = grupo.getEtiquetaGrupo();
					if (grupoListado(etiquetaGrupo) != null) {
						facesMessages.clear();
						facesMessages
						.addFromResourceBundle("msg_validacionExisteGrupo_ens");
						error = true;
					}
				}

				if (!error) {

					this.grupos.get(posGrupoModificar).setEtiquetaGrupo(
							grupo.getEtiquetaGrupo());
					this.grupos.get(posGrupoModificar).setDescripcionGrupo(
							grupo.getDescripcionGrupo());
					this.grupos.get(posGrupoModificar).setEncabezado(
							grupo.getEncabezado());
					this.grupos.get(posGrupoModificar).setEliminado(false);
					this.grupos.get(posGrupoModificar).setNumMaxRepeticiones(
							grupo.getNumMaxRepeticiones());
					this.grupos.get(posGrupoModificar).setNumRepeticiones(
							grupo.getNumRepeticiones());
					this.grupos.get(posGrupoModificar).setSeccion(
							seccionListado(seccionGrupo));
					this.grupos.get(posGrupoModificar).setEstadoGrupo(
							grupo.getEstadoGrupo());

					grupo = new GrupoVariables_ensayo();
					seccionGrupo = "";
					modificandoGrupo = false;
					posGrupoModificar = -1;

				}

			} else {
				String etiquetaGrupo = grupo.getEtiquetaGrupo();
				if (grupo.getNumRepeticiones() == null)
					grupo.setNumRepeticiones(1);

				if (grupo.getNumMaxRepeticiones() == (null))
					grupo.setNumMaxRepeticiones(500);

				if (grupoListado(etiquetaGrupo) != null) {
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("msg_validacionExisteGrupo_ens");

				} else if (grupo.getNumRepeticiones() >= grupo
						.getNumMaxRepeticiones()) {
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("msg_validacionNumeroRepeticiones_ens");

				} else {

					if (grupo.getEstadoGrupo() == null)
						grupo.setEstadoGrupo(SeamResourceBundle.getBundle()
								.getString("prm_show_ens"));
					else
						grupo.setEstadoGrupo(grupo.getEstadoGrupo());

					grupo.setSeccion(seccionListado(seccionGrupo));
					grupo.setEtiquetaGrupo(etiquetaGrupo);
					grupo.setEncabezado(grupo.getEncabezado());
					grupo.setDescripcionGrupo(grupo.getDescripcionGrupo());
					grupo.setNumMaxRepeticiones(grupo.getNumMaxRepeticiones());
					grupo.setNumRepeticiones(grupo.getNumRepeticiones());
					grupo.setEliminado(false);
					grupos.add(grupo);

					grupo = new GrupoVariables_ensayo();
					seccionGrupo = "";

				}

			}

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}

	// Metodos ara adicionar y modificar las variables
	boolean modificandoVar = false;
	int posVarModificar = -1;


	public void modificarVariable(Variable_ensayo variable) {

		this.posVarModificar = variables.indexOf(variable);
		this.variable = new Variable_ensayo();

		this.variable.setEliminado(variable.getEliminado());
		this.variable.setNombreVariable(variable.getNombreVariable());
		this.variable.setTextoIzquierdaVariable(variable
				.getTextoIzquierdaVariable());
		this.variable.setTextoDerechaVariable(variable
				.getTextoDerechaVariable());
		this.variable.setUnidadesVariable(variable.getUnidadesVariable());
		this.variable.setDescripcionVariable(variable.getDescripcionVariable());
		this.variable.setEncabezadoVariable(variable.getEncabezadoVariable());
		this.variable.setSubencabezadoVariable(variable
				.getSubencabezadoVariable());
		this.variable.setNumeroColumna(variable.getNumeroColumna());
		this.variable.setNumeroPregunta(variable.getNumeroPregunta());
		this.variable.setRequerido(variable.getRequerido());
		this.variable.setNumeroColumna(variable.getNumeroColumna());
		this.variable.setNumeroPregunta(variable.getNumeroPregunta());
		this.variable.setRequerido(variable.getRequerido());
		this.variable.setUsuario(usuario);
		this.variable.setSeccion(seccionListado(seccionVariable));

		if (variable.getGrupoVariables() != null) {
			grupoVariable = variable.getGrupoVariables().getEtiquetaGrupo();
			this.variable.setGrupoVariables(variable.getGrupoVariables());
		}

		this.variable.setTipoDato(variable.getTipoDato());
		this.variable.setPresentacionFormulario(variable
				.getPresentacionFormulario());
		this.variable.setUbicacionRespuesta(variable.getUbicacionRespuesta());

		modificandoVar = true;
		seccionVariable = variable.getSeccion().getEtiquetaSeccion();
		nombrePresentacionFormulario = variable.getPresentacionFormulario()
				.getNombre();
		nombreTipoDato = variable.getTipoDato().getNombre();
		if (variable.getNomenclador() != null) {
			nomencladorVariable = variable.getNomenclador();

			Set<NomencladorValor_ensayo> valores = variable.getNomenclador()
					.getNomencladorValors();
			NomencladorValor_ensayo[] tak = new NomencladorValor_ensayo[valores.size()];
			tak = valores.toArray(tak);
			boolean flag = false;
			for (int i = 0; i < tak.length; i++) {
				for (int j = 0; j < tak.length; j++) {
					if (tak[i].getValorCalculado() < tak[j].getValorCalculado()) {
						NomencladorValor_ensayo aux = tak[i];
						tak[i] = tak[j];
						tak[j]=aux;
					}
				}
			}
			for (NomencladorValor_ensayo nomencladorValor_ensayo : tak) {
				if (flag) {

					valoresTexto = valoresTexto + ","
							+ nomencladorValor_ensayo.getValor();

				} else {
					valoresTexto = nomencladorValor_ensayo.getValor();
					flag = true;
				}

			}
		}

	}

	public void adicionarVariable() {
		try {
			facesMessages.clear();

			if (modificandoVar) {
				boolean error = false;
				// Se cambio la etiqueta
				if (variables.get(posVarModificar).getNombreVariable() == variable
						.getNombreVariable()) {
					String etiquetaGrupo = grupo.getEtiquetaGrupo();
					if (grupoListado(etiquetaGrupo) != null) {
						facesMessages.clear();
						facesMessages
						.addFromResourceBundle("msg_validacionExisteVariable_ens");
						error = true;
					}
				}

				if (!error) {

					this.variables.get(posVarModificar).setEliminado(
							variable.getEliminado());
					this.variables.get(posVarModificar).setNombreVariable(
							variable.getNombreVariable());
					this.variables.get(posVarModificar)
					.setTextoIzquierdaVariable(
							variable.getTextoIzquierdaVariable());
					this.variables.get(posVarModificar)
					.setTextoDerechaVariable(
							variable.getTextoDerechaVariable());
					this.variables.get(posVarModificar).setUnidadesVariable(
							variable.getUnidadesVariable());
					this.variables.get(posVarModificar).setDescripcionVariable(
							variable.getDescripcionVariable());
					this.variables.get(posVarModificar).setEncabezadoVariable(
							variable.getEncabezadoVariable());
					this.variables.get(posVarModificar)
					.setSubencabezadoVariable(
							variable.getSubencabezadoVariable());
					this.variables.get(posVarModificar).setNumeroColumna(
							variable.getNumeroColumna());
					this.variables.get(posVarModificar).setNumeroPregunta(
							variable.getNumeroPregunta());
					this.variables.get(posVarModificar).setRequerido(
							variable.getRequerido());
					this.variables.get(posVarModificar).setSeccion(
							seccionListado(seccionVariable));
					this.variables.get(posVarModificar).setGrupoVariables(
							grupoListado(grupoVariable));
					this.variables.get(posVarModificar).setTipoDato(tipoDato());
					this.variables
					.get(posVarModificar)
					.setPresentacionFormulario(presentacionFormulario());
					this.variables.get(posVarModificar).setUbicacionRespuesta(
							variable.getUbicacionRespuesta());
					this.variables.get(posVarModificar).setUsuario(usuario);

					if (habilitarNomenclador) {
						nomencladorVariable.setEliminado(false);
						this.variables.get(posVarModificar).setNomenclador(
								nomencladorVariable);

						this.variables.get(posVarModificar).getNomenclador()
						.getNomencladorValors().clear();

						// eliminar espacios.
						//valoresTexto = valoresTexto.replace(" ", "");

						String[] valoresEnTexto = valoresTexto.split(",");

						for (int i = 0; i < valoresEnTexto.length; i++) {
							Integer valorCalculado = i + 1;
							NomencladorValor_ensayo nomencladoValor = new NomencladorValor_ensayo();
							nomencladoValor.setEliminado(false);
							nomencladoValor.setValor(valoresEnTexto[i]);// valor
							// en
							// texto
							nomencladoValor.setValorCalculado(valorCalculado
									);// valor calculado
							nomencladoValor.setNomenclador(nomencladorVariable);
							nomencladorVariable.getNomencladorValors().add(
									nomencladoValor);
						}

					}else{
						this.variables.get(posVarModificar).setNomenclador(null);
					}

					variable = new Variable_ensayo();
					nomencladorVariable = new Nomenclador_ensayo();
					grupoVariable = SeamResourceBundle.getBundle().getString(
							"lbl_seleccione_ens");
					seccionVariable = "";
					nombreTipoDato = "";
					nombrePresentacionFormulario = "";
					valoresTexto = "";

					valorPorDefecto = "";
					deshabilitarPresentacionFormulario = true;
					habilitarNomenclador = false;
					modificandoVar = false;
					posVarModificar = -1;

				}

			} else {
				String nombreVariable = variable.getNombreVariable();
				if (variableListado(nombreVariable) != null) {
					facesMessages.clear();
					facesMessages
					.addFromResourceBundle("msg_validacionExisteVariable_ens");

				} else {
					variable.setSeccion(seccionListado(seccionVariable));
					if (!grupoVariable.equals(SeamResourceBundle.getBundle()
							.getString("lbl_seleccione_ens")))
						variable.setGrupoVariables(grupoListado(grupoVariable));
					variable.setNombreVariable(nombreVariable);
					variable.setEliminado(false);
					variable.setTipoDato(tipoDato());
					variable.setPresentacionFormulario(presentacionFormulario());
					variable.setUsuario(usuario);
					if (habilitarNomenclador) {
						nomencladorVariable.setEliminado(false);
						variable.setNomenclador(nomencladorVariable);

						// eliminar espacios.
						//valoresTexto = valoresTexto.replace(" ", "");

						String[] valoresEnTexto = valoresTexto.split(",");

						for (int i = 0; i < valoresEnTexto.length; i++) {
							Integer valorCalculado = i + 1;
							NomencladorValor_ensayo nomencladoValor = new NomencladorValor_ensayo();
							nomencladoValor.setEliminado(false);
							nomencladoValor.setValor(valoresEnTexto[i]);// valor
							// en
							// texto
							nomencladoValor.setValorCalculado(valorCalculado
									);// valor calculado
							nomencladoValor.setNomenclador(nomencladorVariable);
							nomencladorVariable.getNomencladorValors().add(
									nomencladoValor);
						}
					}
					variables.add(variable);

					variable = new Variable_ensayo();
					nomencladorVariable = new Nomenclador_ensayo();
					grupoVariable = SeamResourceBundle.getBundle().getString(
							"lbl_seleccione_ens");
					seccionVariable = "";
					nombreTipoDato = "";
					nombrePresentacionFormulario = "";
					valoresTexto = "";
					valorPorDefecto = "";
					deshabilitarPresentacionFormulario = true;
					habilitarNomenclador = false;
				}

			}
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());

		}
	}



	///Para ampliar comboBox

	public int sizeMaxString(List<String> listReturn){
		if(listReturn.size() == 0)
			return 150;
		int max = listReturn.get(0).length();
		for(int i = 1; i < listReturn.size(); i++){
			if(listReturn.get(i).length() > max)
				max = listReturn.get(i).length();
		}
		if ((max*5) > 150)
			return max*5;
		return 150;
	}	





	/**
	 * Getters and Setters
	 */
	// Getters and Setters
	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public HojaCrd_ensayo getHojaCrd() {
		return hojaCrd;
	}

	public void setHojaCrd(HojaCrd_ensayo hojaCrd) {
		this.hojaCrd = hojaCrd;
	}

	public Usuario_ensayo getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_ensayo usuario) {
		this.usuario = usuario;
	}

	public List<Seccion_ensayo> getSecciones() {
		return secciones;
	}

	public void setSecciones(List<Seccion_ensayo> secciones) {
		this.secciones = secciones;
	}

	public List<GrupoVariables_ensayo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<GrupoVariables_ensayo> grupos) {
		this.grupos = grupos;
	}

	public GrupoVariables_ensayo getGrupo() {
		return grupo;
	}

	public void setGrupo(GrupoVariables_ensayo grupo) {
		this.grupo = grupo;
	}

	public Seccion_ensayo getSeccion() {
		return seccion;
	}

	public void setSeccion(Seccion_ensayo seccion) {
		this.seccion = seccion;
	}

	public List<String> getListaEstadosGrupo() {
		return listaEstadosGrupo;
	}

	public void setListaEstadosGrupo(List<String> listaEstadosGrupo) {
		this.listaEstadosGrupo = listaEstadosGrupo;
	}

	public List<Variable_ensayo> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable_ensayo> variables) {
		this.variables = variables;
	}

	public Variable_ensayo getVariable() {
		return variable;
	}

	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}

	public String getSeccionVariable() {
		return seccionVariable;
	}

	public void setSeccionVariable(String seccionVariable) {
		this.grupoVariable = "";
		this.seccionVariable = seccionVariable;
	}

	public String getGrupoVariable() {
		return grupoVariable;
	}

	public void setGrupoVariable(String grupoVariable) {
		this.grupoVariable = grupoVariable;
	}

	public String getNombreTipoDato() {
		if (this.nombreTipoDato.equals("")) {
			deshabilitarPresentacionFormulario = true;
			habilitarNomenclador = false;
		} else {
			deshabilitarPresentacionFormulario = false;
			if (tipoDato().getCodigo().equals("NOM"))
				habilitarNomenclador = true;
			else
				habilitarNomenclador = false;
		}

		return nombreTipoDato;
	}

	public void setNombreTipoDato(String nombreTipoDato) {
		this.nombreTipoDato = nombreTipoDato;
		if (this.nombreTipoDato.equals("")) {
			deshabilitarPresentacionFormulario = true;
			habilitarNomenclador = false;
		} else {
			deshabilitarPresentacionFormulario = false;
			if (tipoDato().getCodigo().equals("NOM"))
				habilitarNomenclador = true;
			else
				habilitarNomenclador = false;
		}
	}

	public String getNombrePresentacionFormulario() {
		return nombrePresentacionFormulario;
	}

	public void setNombrePresentacionFormulario(
			String nombrePresentacionFormulario) {
		this.nombrePresentacionFormulario = nombrePresentacionFormulario;
	}

	public Boolean getDeshabilitarPresentacionMedicamento() {
		return deshabilitarPresentacionFormulario;
	}

	public void setDeshabilitarPresentacionMedicamento(
			Boolean deshabilitarPresentacionFormulario) {
		this.deshabilitarPresentacionFormulario = deshabilitarPresentacionFormulario;
	}

	public Nomenclador_ensayo getNomencladorVariable() {
		return nomencladorVariable;
	}

	public void setNomencladorVariable(Nomenclador_ensayo nomencladorVariable) {
		this.nomencladorVariable = nomencladorVariable;
	}

	public Boolean getHabilitarNomenclador() {

		if (this.getNombreTipoDato().equals("Nomenclador"))
			habilitarNomenclador = true;
		else
			habilitarNomenclador = false;

		return habilitarNomenclador;
	}

	public void setHabilitarNomenclador(Boolean habilitarNomenclador) {
		this.habilitarNomenclador = habilitarNomenclador;
	}

	public String getValoresTexto() {
		return valoresTexto;
	}

	public void setValoresTexto(String valoresTexto) {
		this.valoresTexto = valoresTexto;
	}

	public List<String> getListaUbicacionRespuesta() {
		return listaUbicacionRespuesta;
	}

	public void setListaUbicacionRespuesta(List<String> listaUbicacionRespuesta) {
		this.listaUbicacionRespuesta = listaUbicacionRespuesta;
	}

	public String getValorPorDefecto() {
		return valorPorDefecto;
	}

	public void setValorPorDefecto(String valorPorDefecto) {
		this.valorPorDefecto = valorPorDefecto;
	}

	public String getSeccionGrupo() {
		return seccionGrupo;
	}

	public void setSeccionGrupo(String seccionGrupo) {
		this.seccionGrupo = seccionGrupo;
	}



	public Boolean getInicioConversacion() {
		return inicioConversacion;
	}



	public void setInicioConversacion(Boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}

	/*
	 * public List<NomencladorValor_ensayo> getListadoNomencladorValor() {
	 * return listadoNomencladorValor; }
	 * 
	 * public void setListadoNomencladorValor( List<NomencladorValor_ensayo>
	 * listadoNomencladorValor) { this.listadoNomencladorValor =
	 * listadoNomencladorValor; }
	 */

}
