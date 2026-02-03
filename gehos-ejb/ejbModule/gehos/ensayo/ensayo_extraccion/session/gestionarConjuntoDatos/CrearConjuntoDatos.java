package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.drools.ruleflow.core.Split;
import org.hibernate.mapping.Array;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;



@Name("crearConjuntoDatos")
@Scope(ScopeType.CONVERSATION)
public class CrearConjuntoDatos
{
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
	private boolean inicioConversacion = false;
	private boolean seleccEstudio = true;
	private boolean seleccCronograma = false;
	private boolean seleccMomento = false;
	private boolean seleccCRD = false;
	private boolean seleccTodoEstudio = false;
	private boolean seleccTodoCronograma = false;
	private boolean seleccTodoMomento = false;
	private boolean seleccTodoCRD = false;

	private long cid;
	private ConjuntoDatos_ensayo conjuntoDatos;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	private List<Cronograma_ensayo> cronogramas = new ArrayList<Cronograma_ensayo>();
	private ListadoControler_ensayo<VariableConjuntoDatos> listadoVariables;
	private List<VariableConjuntoDatos> variables = new ArrayList<VariableConjuntoDatos>();
	private List<VariableConjuntoDatos> listadoVariablesEstudio = new ArrayList<VariableConjuntoDatos>();
	private Hashtable<Integer, VariableConjuntoDatos> variablesSeleccionadas = new Hashtable<Integer, VariableConjuntoDatos>();
	private ListadoControler_ensayo<VariableConjuntoDatos> listadoVariablesConjuntoDatos;
	private List<VariableConjuntoDatos> listadoVariablesSeleccionadas = new ArrayList<VariableConjuntoDatos>();
	private int idVariable;
	private List<MomentoSeguimientoGeneral_ensayo> momentos;
	private Estudio_ensayo estudio;
	private Cronograma_ensayo cronograma;
	private MomentoSeguimientoGeneral_ensayo momento;
	private MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCRD;
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");
	
	// variables que definen si los campos son obligatorios o no
		protected boolean nombreRequired;
	
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		this.nombreRequired = true;
	}
	
	//Metodo para inicializar conjunto de datos.
	public void inicializarConjuntoDatos() {
		conjuntoDatos = new ConjuntoDatos_ensayo();
		estudio = seguridadEstudio.EstudioActivo();
		usuario = ((Usuario_ensayo)entityManager.find(Usuario_ensayo.class, user.getId()));
		cronogramas = entityManager.createQuery("select distinct cronograma from Cronograma_ensayo cronograma "
				+ "inner join cronograma.grupoSujetos grupoSujetos "
				+ "inner join cronograma.momentoSeguimientoGenerals momentoSeguimientoGenerals "
				+ "where grupoSujetos.nombreGrupo<>'Grupo Validación' and grupoSujetos.estudio.id =:idEstudio and cronograma.eliminado<>true and momentoSeguimientoGenerals.eliminado<>true")
				.setParameter("idEstudio", Long.valueOf(estudio.getId())).getResultList();

		List<Object> variables = entityManager.createQuery("select variable, momentoSeguimientoGeneral from Variable_ensayo variable "
				+ "inner join variable.seccion seccion "
				+ "inner join seccion.hojaCrd hojaCrd "
				+ "inner join hojaCrd.momentoSeguimientoGeneralHojaCrds momentoSeguimientoGeneralHojaCrds "
				+ "inner join momentoSeguimientoGeneralHojaCrds.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoSeguimientoGeneral.cronograma cronograma "
				+ "where variable.eliminado <> true and momentoSeguimientoGeneralHojaCrds.eliminado <> true "
				+ "and momentoSeguimientoGeneral.eliminado <> true and cronograma in (#{crearConjuntoDatos.cronogramas}) "
				+ "ORDER BY variable.id")
				.getResultList();
		for (int i = 0; i < variables.size(); i++) {
			Variable_ensayo variable = (Variable_ensayo)((Object[])variables.get(i))[0];

			MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = (MomentoSeguimientoGeneral_ensayo)((Object[])variables.get(i))[1];

			VariableConjuntoDatos variableConjuntoDatos = new VariableConjuntoDatos(i, variable, momentoSeguimientoGeneral);
			listadoVariablesEstudio.add(variableConjuntoDatos);
		}

		listadoVariables = new ListadoControler_ensayo(
				listadoVariablesEstudio);
		listadoVariablesConjuntoDatos = new ListadoControler_ensayo(listadoVariablesSeleccionadas);

		inicioConversacion = true;
	}
	//add
	//Metodo que devuelve los momentos de un cronograma ordenados
	public List<MomentoSeguimientoGeneral_ensayo> momentosCronograma(Cronograma_ensayo cronograma) {
		List<MomentoSeguimientoGeneral_ensayo> momentos = new ArrayList<MomentoSeguimientoGeneral_ensayo>();
		momentos = entityManager.createQuery("select momento from MomentoSeguimientoGeneral_ensayo momento "
				+ "where momento.eliminado<>true and momento.cronograma.id =:idCronograma "
				+ "order by momento.id").setParameter("idCronograma", cronograma.getId()).getResultList();
		return momentos;
	}
	//Metodo que devuelve las hojas crd de un momento ordenadas
	public List<MomentoSeguimientoGeneralHojaCrd_ensayo> hojasMomentos(MomentoSeguimientoGeneral_ensayo momento) {
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> hojas = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>();
		hojas = entityManager.createQuery("select msHojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd "
				+ "where msHojaCrd.eliminado<>true and msHojaCrd.momentoSeguimientoGeneral.id =:idMomento "
				+ "order by msHojaCrd.hojaCrd.nombreHoja").setParameter("idMomento", momento.getId()).getResultList();
		return hojas;
	}
	//listado de variablesConjuntoDatos segun el nodo seleccionado
	public ListadoControler_ensayo<VariableConjuntoDatos> listadoVariables()
	{
		variables = new ArrayList();
		if (seleccEstudio) {
			variables = listadoVariablesEstudio;

		}
		else if (seleccCronograma) {
			for (int i = 0; i < listadoVariablesEstudio.size(); i++) {
				if (((VariableConjuntoDatos)listadoVariablesEstudio.get(i)).getMomentoSeguimientoGeneral().getCronograma().equals(cronograma)) {
					variables.add((VariableConjuntoDatos)listadoVariablesEstudio.get(i));
				}
			}
		} else if (seleccMomento) {
			for (int i = 0; i < listadoVariablesEstudio.size(); i++) {
				if (((VariableConjuntoDatos)listadoVariablesEstudio.get(i)).getMomentoSeguimientoGeneral().equals(momento)) {
					variables.add((VariableConjuntoDatos)listadoVariablesEstudio.get(i));
				}
			}
		} else {
			for (int i = 0; i < listadoVariablesEstudio.size(); i++) {
				if ((((VariableConjuntoDatos)listadoVariablesEstudio.get(i)).getMomentoSeguimientoGeneral().equals(msHojaCRD.getMomentoSeguimientoGeneral())) && 
						(((VariableConjuntoDatos)listadoVariablesEstudio.get(i)).getVariable().getSeccion().getHojaCrd().equals(msHojaCRD.getHojaCrd()))) {
					variables.add((VariableConjuntoDatos)listadoVariablesEstudio.get(i));
				}
			}
		}
		listadoVariables.setElementos(variables);
		return listadoVariables;
	}
	//Metodo para seleccionar estudio 
	public void seleccionarEstudio()
	{
		seleccEstudio = true;
		seleccCronograma = false;
		seleccMomento = false;
		seleccCRD = false;
		listadoVariables.setFirstResult(0);
	}
	//Metodo para seleccionar cronograma
	public void seleccionarCronograma(Cronograma_ensayo cronograma) { this.cronograma = new Cronograma_ensayo();
	this.cronograma = cronograma;
	seleccEstudio = false;
	seleccCronograma = true;
	seleccMomento = false;
	seleccCRD = false;
	listadoVariables.setFirstResult(0);
	}
	//Metodo para seleccionar momento de seguimiento
	public void seleccionarMomento(MomentoSeguimientoGeneral_ensayo momento) { 
		this.momento = new MomentoSeguimientoGeneral_ensayo();
		this.momento = momento;
		seleccEstudio = false;
		seleccCronograma = false;
		seleccMomento = true;
		seleccCRD = false;
		listadoVariables.setFirstResult(0);
	}
	//Metodo para seleccionar Hoja CRD
	public void seleccionarHojaCRD(MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCRD) { 
		this.msHojaCRD = new MomentoSeguimientoGeneralHojaCrd_ensayo();
		this.msHojaCRD = msHojaCRD;
		seleccEstudio = false;
		seleccCronograma = false;
		seleccMomento = false;
		seleccCRD = true;
		listadoVariables.setFirstResult(0);
	}
	//Metodo para seleccionar una variable
	public void seleccionarVariable()
	{
		try
		{
			if (!variablesSeleccionadas.containsKey(idVariable)) {
				VariableConjuntoDatos variable = (VariableConjuntoDatos)listadoVariablesEstudio.get(idVariable);
				listadoVariablesSeleccionadas.add(variable);
				listadoVariablesConjuntoDatos.setElementos(listadoVariablesSeleccionadas);
				variablesSeleccionadas.put(idVariable, variable);
			}
			else {
				eliminarVariable(idVariable);
			}
		}
		catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]);
		}
	}
	//Metodo para desmarcar o eliminar variable
	public void eliminarVariable(int idVariable) { VariableConjuntoDatos variable = (VariableConjuntoDatos)variablesSeleccionadas.get(idVariable);
	variablesSeleccionadas.remove(idVariable);
	listadoVariablesSeleccionadas.remove(variable);
	listadoVariablesConjuntoDatos.setElementos(listadoVariablesSeleccionadas);
	}
	//Metodo para saber si todas las variables estan marcadas
	public boolean todoMarcado() {
		for (int i = 0; i < variables.size(); i++) {
			if (!variablesSeleccionadas.containsKey(((VariableConjuntoDatos)variables.get(i)).getId())) {
				return false;
			}
		}
		return true;
	}

	public void marcarDesmarcarTodo()
	{
		if (!todoMarcado()) {
			seleccionarTodoVariable();
		} else
			desseleccionarTodoVariable();
	}

	public void seleccionarTodoVariable() {
		try {
			for (int i = 0; i < variables.size(); i++) {
				if (!variablesSeleccionadas.containsKey(((VariableConjuntoDatos)variables.get(i)).getId())) {
					VariableConjuntoDatos variable = (VariableConjuntoDatos)variables.get(i);
					listadoVariablesSeleccionadas.add(variable);
					variablesSeleccionadas.put(variable.getId(), variable);
				}
			}


			listadoVariablesConjuntoDatos.setElementos(listadoVariablesSeleccionadas);
		}
		catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]);
		}
	}

	public void desseleccionarTodoVariable() {
		try {
			if(seleccEstudio){
				variablesSeleccionadas = new Hashtable();
				listadoVariablesSeleccionadas = new ArrayList();
				listadoVariablesConjuntoDatos = new ListadoControler_ensayo(listadoVariablesSeleccionadas);
			}
			else{
				for (int i = 0; i < variables.size(); i++) {
					if (variablesSeleccionadas.containsKey(Integer.valueOf(((VariableConjuntoDatos)variables.get(i)).getId()))) {
						VariableConjuntoDatos variable = (VariableConjuntoDatos)variables.get(i);						
						eliminarVariable(variable.getId());					

					}
				}
			}


		}
		catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]);
		}
	}
	// RF: 5 Crear conjunto de datos
	public String insertarConjuntoDatos() {
		try {
			if (listadoVariablesSeleccionadas.size() == 0) {
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionVariableConjuntoDatos_ens", new Object[0]);
				return "error";
			}
			long count = ((Long)entityManager.createQuery("select count(cd) from ConjuntoDatos_ensayo cd where cd.eliminado <> true and cd.nombre = :nombre and cd.estudio.id = :idEstudio")
					.setParameter("nombre", conjuntoDatos.getNombre())
					.setParameter("idEstudio", estudio.getId())
					.getSingleResult()).longValue();

			if (count > 0L) {
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionConjuntoDatos_ens", new Object[0]);
				return "error";
			}
			
			if(conjuntoDatos.getFechaInicio() == null) {
				//conjuntoDatos.setFechaInicio(estudio.getFechaInicio());
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				conjuntoDatos.setFechaInicio(formatter.parse("01/01/2000"));	
			}			
			
			//Cambiar el comportamiento para que al exportar siempre coja la fecha actual
			if(conjuntoDatos.getFechaFin() == null) {
				SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
				conjuntoDatos.setFechaFin(formatter.parse("01/01/2000"));	
			}
			
			cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraCrear_ens")).longValue();			
			conjuntoDatos.setEliminado(false);
			conjuntoDatos.setFechaCreacion(Calendar.getInstance().getTime());
			conjuntoDatos.setEntidad(seguridadEstudio.getEstudioEntidadActivo().getEntidad());
			conjuntoDatos.setEstudio(seguridadEstudio.getEstudioEntidadActivo().getEstudio());      
			conjuntoDatos.setUsuario(usuario);
			conjuntoDatos.setCid(cid);
			conjuntoDatos.setPesquisaje(false);
			conjuntoDatos.setDeclaracionHql(UtilConjuntoDatos.declaracionHql(conjuntoDatos, estudio, listadoVariablesSeleccionadas));
			entityManager.persist(conjuntoDatos);
			entityManager.flush();

			return "ok";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]); }
		return "error";
	}

	

	public void textNumberGuionBajo(FacesContext context,
			UIComponent component, Object value) {
		if (!value.toString().equals("")) {
			if (!value.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9_]+\\s*)++$")) {
				this.facesMessage = new FacesMessage(SeamResourceBundle.getBundle()
						.getString(

								"msg_validacionNombreConjunto_ens"), null);
				this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
				throw new ValidatorException(facesMessage);

			}
		}
		/*
			if (value.toString().length() > 25 || value.toString()== " ") {
				facesMessages.addToControlFromResourceBundle(componente,
						Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("maximoCaracteres"));
				return true;
			}*/
	}


	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public List<Cronograma_ensayo> getCronogramas()
	{
		return cronogramas;
	}

	public void setCronogramas(List<Cronograma_ensayo> cronogramas)
	{
		this.cronogramas = cronogramas;
	}


	public Estudio_ensayo getEstudio()
	{
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio)
	{
		this.estudio = estudio;
	}

	public boolean isInicioConversacion()
	{
		return inicioConversacion;
	}

	public void setInicioConversacion(boolean inicioConversacion)
	{
		this.inicioConversacion = inicioConversacion;
	}

	public boolean isSeleccEstudio()
	{
		return seleccEstudio;
	}

	public void setSeleccEstudio(boolean seleccEstudio)
	{
		this.seleccEstudio = seleccEstudio;
	}

	public boolean isSeleccCronograma()
	{
		return seleccCronograma;
	}

	public void setSeleccCronograma(boolean seleccCronograma)
	{
		this.seleccCronograma = seleccCronograma;
	}

	public boolean isSeleccMomento()
	{
		return seleccMomento;
	}

	public void setSeleccMomento(boolean seleccMomento)
	{
		this.seleccMomento = seleccMomento;
	}

	public boolean isSeleccCRD()
	{
		return seleccCRD;
	}

	public void setSeleccCRD(boolean seleccCRD)
	{
		this.seleccCRD = seleccCRD;
	}

	public List<MomentoSeguimientoGeneral_ensayo> getMomentos()
	{
		return momentos;
	}

	public void setMomentos(List<MomentoSeguimientoGeneral_ensayo> momentos)
	{
		this.momentos = momentos;
	}

	public Cronograma_ensayo getCronograma()
	{
		return cronograma;
	}

	public void setCronograma(Cronograma_ensayo cronograma) {
		this.cronograma = cronograma;
	}

	public MomentoSeguimientoGeneral_ensayo getMomento() {
		return momento;
	}

	public void setMomento(MomentoSeguimientoGeneral_ensayo momento) {
		this.momento = momento;
	}


	public MomentoSeguimientoGeneralHojaCrd_ensayo getMsHojaCRD()
	{
		return msHojaCRD;
	}

	public void setMsHojaCRD(MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCRD) {
		this.msHojaCRD = msHojaCRD;
	}


	public boolean isSeleccTodoEstudio()
	{
		return seleccTodoEstudio;
	}

	public void setSeleccTodoEstudio(boolean seleccTodoEstudio) {
		this.seleccTodoEstudio = seleccTodoEstudio;
	}

	public boolean isSeleccTodoCronograma() {
		return seleccTodoCronograma;
	}

	public void setSeleccTodoCronograma(boolean seleccTodoCronograma) {
		this.seleccTodoCronograma = seleccTodoCronograma;
	}

	public boolean isSeleccTodoMomento() {
		return seleccTodoMomento;
	}

	public void setSeleccTodoMomento(boolean seleccTodoMomento) {
		this.seleccTodoMomento = seleccTodoMomento;
	}

	public boolean isSeleccTodoCRD() {
		return seleccTodoCRD;
	}

	public void setSeleccTodoCRD(boolean seleccTodoCRD) {
		this.seleccTodoCRD = seleccTodoCRD;
	}

	public ListadoControler_ensayo<VariableConjuntoDatos> getListadoVariables() {
		return listadoVariables;
	}

	public void setListadoVariables(ListadoControler_ensayo<VariableConjuntoDatos> listadoVariables)
	{
		this.listadoVariables = listadoVariables;
	}

	public List<VariableConjuntoDatos> getVariables() {
		return variables;
	}

	public void setVariables(List<VariableConjuntoDatos> variables) {
		this.variables = variables;
	}

	public List<VariableConjuntoDatos> getListadoVariablesEstudio() {
		return listadoVariablesEstudio;
	}

	public void setListadoVariablesEstudio(List<VariableConjuntoDatos> listadoVariablesEstudio)
	{
		this.listadoVariablesEstudio = listadoVariablesEstudio;
	}

	public Hashtable<Integer, VariableConjuntoDatos> getVariablesSeleccionadas() {
		return variablesSeleccionadas;
	}

	public void setVariablesSeleccionadas(Hashtable<Integer, VariableConjuntoDatos> variablesSeleccionadas)
	{
		this.variablesSeleccionadas = variablesSeleccionadas;
	}

	public List<VariableConjuntoDatos> getListadoVariablesSeleccionadas() {
		return listadoVariablesSeleccionadas;
	}

	public void setListadoVariablesSeleccionadas(List<VariableConjuntoDatos> listadoVariablesSeleccionadas)
	{
		this.listadoVariablesSeleccionadas = listadoVariablesSeleccionadas;
	}

	public ConjuntoDatos_ensayo getConjuntoDatos() {
		return conjuntoDatos;
	}

	public void setConjuntoDatos(ConjuntoDatos_ensayo conjuntoDatos) {
		this.conjuntoDatos = conjuntoDatos;
	}

	public int getIdVariable() {
		return idVariable;
	}

	public void setIdVariable(int idVariable) {
		this.idVariable = idVariable;
	}

	public ListadoControler_ensayo<VariableConjuntoDatos> getListadoVariablesConjuntoDatos() {
		return listadoVariablesConjuntoDatos;
	}

	public void setListadoVariablesConjuntoDatos(ListadoControler_ensayo<VariableConjuntoDatos> listadoVariablesConjuntoDatos)
	{
		this.listadoVariablesConjuntoDatos = listadoVariablesConjuntoDatos;
	}

	public Usuario_ensayo getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario_ensayo usuario) {
		this.usuario = usuario;
	}
	public boolean isNombreRequired() {
		return nombreRequired;
	}
	public void setNombreRequired(boolean nombreRequired) {
		this.nombreRequired = nombreRequired;
	}
}
