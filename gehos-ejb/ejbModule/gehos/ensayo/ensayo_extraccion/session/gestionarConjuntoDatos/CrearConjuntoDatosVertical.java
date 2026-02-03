package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

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


@Name("crearConjuntoDatosVertical")
@Scope(ScopeType.CONVERSATION)
public class CrearConjuntoDatosVertical {
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
	private boolean filtradoVertical = false;
	private String textCambiarModo="F";

	private long cid;
	private ConjuntoDatos_ensayo conjuntoDatos;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	private List<Cronograma_ensayo> cronogramas = new ArrayList<Cronograma_ensayo>();
	private ListadoControler_ensayo<VariableConjuntoDatos> listadoVariables;
	private List<VariableConjuntoDatos> variables = new ArrayList<VariableConjuntoDatos>();
	private List<VariableConjuntoDatos> listadoVariablesEstudio = new ArrayList<VariableConjuntoDatos>();
	private Hashtable<Integer, VariableConjuntoDatos> listadoVariablesEstudioDisponibles = new Hashtable<Integer, VariableConjuntoDatos>();
	private List<VariableConjuntoDatos> listadoVariablesEstudioFiltradoVertical = new ArrayList<VariableConjuntoDatos>();
	private List<VariableConjuntoDatos> listadoVariablesEstudioAux = new ArrayList<VariableConjuntoDatos>();
	private Map<Integer,List<VariableConjuntoDatos>> listadoVariablesEstudioRepetidas = new TreeMap<Integer,List<VariableConjuntoDatos>>();
	private List<String> listadoMomentos = new ArrayList<String>();
	private List<String> listadoMomentosDisponibles = new ArrayList<String>();
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

	
	//Metodo para inicializar conjunto de datos.
	public void inicializarConjuntoDatos() {
		conjuntoDatos = new ConjuntoDatos_ensayo();
		estudio = seguridadEstudio.EstudioActivo();
		usuario = ((Usuario_ensayo)entityManager.find(Usuario_ensayo.class, user.getId()));
		cronogramas = entityManager.createQuery("select distinct cronograma from Cronograma_ensayo cronograma "
				+ "inner join cronograma.grupoSujetos grupoSujetos "
				+ "inner join cronograma.momentoSeguimientoGenerals momentoSeguimientoGenerals "
				+ "where grupoSujetos.estudio.id =:idEstudio and cronograma.eliminado<>true and momentoSeguimientoGenerals.eliminado<>true")
				.setParameter("idEstudio", Long.valueOf(estudio.getId())).getResultList();

		List<Object> variables = entityManager.createQuery("select variable, momentoSeguimientoGeneral from Variable_ensayo variable "
				+ "inner join variable.seccion seccion "
				+ "inner join seccion.hojaCrd hojaCrd "
				+ "inner join hojaCrd.momentoSeguimientoGeneralHojaCrds momentoSeguimientoGeneralHojaCrds "
				+ "inner join momentoSeguimientoGeneralHojaCrds.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoSeguimientoGeneral.cronograma cronograma "
				+ "where variable.eliminado <> true and momentoSeguimientoGeneralHojaCrds.eliminado <> true "
				+ "and momentoSeguimientoGeneral.eliminado <> true and cronograma in (#{crearConjuntoDatosVertical.cronogramas}) "
				+ "ORDER BY variable.id")
				.getResultList();
		for (int i = 0; i < variables.size(); i++) {
			Variable_ensayo variable = (Variable_ensayo)((Object[])variables.get(i))[0];

			MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = (MomentoSeguimientoGeneral_ensayo)((Object[])variables.get(i))[1];

			VariableConjuntoDatos variableConjuntoDatos = new VariableConjuntoDatos(i, variable, momentoSeguimientoGeneral);
			listadoVariablesEstudio.add(variableConjuntoDatos);
			
			if(listadoMomentos.indexOf(variableConjuntoDatos.getMomentoSeguimientoGeneral().getNombre())==-1&&variableConjuntoDatos.getMomentoSeguimientoGeneral().getNombre()!=null)
				listadoMomentos.add(variableConjuntoDatos.getMomentoSeguimientoGeneral().getNombre());
			
			boolean yaAgregadaVertical =false;
			for(VariableConjuntoDatos v: listadoVariablesEstudioFiltradoVertical){
				boolean mismaVariable = v.getVariable().getNombreVariable().equals(variableConjuntoDatos.getVariable().getNombreVariable());
				boolean mismaHoja = v.getVariable().getNombreVariable().equals(variableConjuntoDatos.getVariable().getNombreVariable());
				
				if(mismaVariable&&mismaHoja){
					
					if(!listadoVariablesEstudioRepetidas.containsKey(v.getId()))
						listadoVariablesEstudioRepetidas.put(v.getId(), new ArrayList());
					
					listadoVariablesEstudioRepetidas.get(v.getId()).add(variableConjuntoDatos);
					yaAgregadaVertical=true;
					break;
					
				}
							
			}
			/*
			System.out.println("Momento de Variable: "+i);
			System.out.println("Id: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getId());
			System.out.println("Etapa: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getEtapa());
			System.out.println("DiasTratamiento: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getDiasTratamiento());
			System.out.println("DiasSeguimiento: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getDiasSeguimiento());
			System.out.println("Dia: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getDia());
			System.out.println("DiasEvaluacion: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getDiasEvaluacion());
			System.out.println("Descripcion: "+variableConjuntoDatos.getMomentoSeguimientoGeneral().getDescripcion());
*/
			
			if(!yaAgregadaVertical){
				listadoVariablesEstudioFiltradoVertical.add(variableConjuntoDatos);
			}
			
		}

		listadoVariablesEstudioAux =listadoVariablesEstudio;
		//listadoVariablesEstudio = listadoVariablesEstudioFiltradoVertical;
		listadoMomentosDisponibles = listadoMomentos;
		listadoVariables = new ListadoControler_ensayo(
				listadoVariablesEstudio);
		//listadoVariables = new ListadoControler_ensayo(listadoVariablesEstudioFiltradoVertical);
		listadoVariablesConjuntoDatos = new ListadoControler_ensayo(listadoVariablesSeleccionadas);
		for(VariableConjuntoDatos v: listadoVariablesEstudio){
			listadoVariablesEstudioDisponibles.put(v.getId(), v);
		}
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
				
				for(VariableConjuntoDatos v: listadoVariablesEstudio){
					boolean mismaVariable = v.getVariable().getId()==variable.getVariable().getId();
					boolean mismaHoja = v.getVariable().getNombreVariable().equals(variable.getVariable().getNombreVariable());
					boolean momentoDisponible = listadoMomentosDisponibles.indexOf(v.getMomentoSeguimientoGeneral().getNombre())!=-1;
					
					if(mismaVariable&&mismaHoja&&momentoDisponible){
						listadoVariablesSeleccionadas.add(v);	
						variablesSeleccionadas.put(v.getId(), v);
					}
				}
				//eliminarVariableDeMomentosNoCompatibles(variable);		
								
				actualizarMomentosDisponibles();
				actualizarVariablesSeleccionadas();
				listadoVariablesConjuntoDatos.setElementos(listadoVariablesSeleccionadas);
				actualizarVariablesDisponibles();
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
	
	
	public void eliminarVariableDeMomentosNoCompatibles(VariableConjuntoDatos variable) { 
		
		ArrayList<String> nuevoListadoMomentosDisponibles = new ArrayList<String>();
		for(String etapa: listadoMomentosDisponibles){
			
			System.out.println("Etapa a revisar: "+etapa);
			boolean existe = false;
			if(variable.getMomentoSeguimientoGeneral().getNombre().equals(etapa))
				existe = true;
			
			if(!existe&&listadoVariablesEstudioRepetidas.containsKey(variable.getId())){
				for(VariableConjuntoDatos v: listadoVariablesEstudioRepetidas.get(variable.getId())){
					if(v.getMomentoSeguimientoGeneral().getNombre()!=null && v.getMomentoSeguimientoGeneral().getNombre().equals(etapa)){
						existe = true;
						break;
					}
				}
			}
			
			if(existe){
				nuevoListadoMomentosDisponibles.add(etapa);	
			}
			
		}
		
		listadoMomentosDisponibles = nuevoListadoMomentosDisponibles;
		
		ArrayList<VariableConjuntoDatos> nuevoListadoVariablesEstudio = new ArrayList<VariableConjuntoDatos>();
		for(VariableConjuntoDatos v: listadoVariablesEstudio){
			boolean coincide = false;
			if(listadoMomentosDisponibles.indexOf(v.getMomentoSeguimientoGeneral().getNombre())!=-1)
				coincide = true;
			
			if(!coincide&&listadoVariablesEstudioRepetidas.containsKey(v.getId()))
				for(VariableConjuntoDatos v2: listadoVariablesEstudioRepetidas.get(v.getId())){
					if(v2.getMomentoSeguimientoGeneral().getNombre()!=null && listadoMomentosDisponibles.indexOf(v2.getMomentoSeguimientoGeneral().getNombre())!=-1){
						coincide = true;
						break;
					}
				}
			
			if(coincide){
				nuevoListadoVariablesEstudio.add(v);
			}
			
			
		}
		listadoVariablesEstudio = nuevoListadoVariablesEstudio;
		
		listadoVariables.setElementos(listadoVariablesEstudio);
		
	}
	
	
	public void actualizarMomentosDisponibles(){
		
		ArrayList<String> nuevoListadoMomentosDisponibles = new ArrayList<String>();
		
		for(String etapa: listadoMomentos){
			boolean existeEnTodos = true;		
				for(VariableConjuntoDatos v: listadoVariablesSeleccionadas){
					ArrayList<String> momentosVariable = new ArrayList();
					for(VariableConjuntoDatos v2: listadoVariablesEstudio){
						boolean mismoNombre = v2.getVariable().getNombreVariable().equals(v.getVariable().getNombreVariable());
						boolean mismaHoja = v2.getVariable().getNombreVariable().equals(v.getVariable().getNombreVariable());
						
						if(mismoNombre&&mismaHoja){
							momentosVariable.add(v2.getMomentoSeguimientoGeneral().getNombre());	
						}
					}
					
					if(momentosVariable.indexOf(etapa)<0)
						existeEnTodos=false;	
			}	
			
			if(existeEnTodos)
				nuevoListadoMomentosDisponibles.add(etapa);	
		}
		
		listadoMomentosDisponibles = nuevoListadoMomentosDisponibles;

		/**for(String etapa: listadoMomentos){
			
			boolean existeEnTodos = true;
			
			for(VariableConjuntoDatos variable: listadoVariablesSeleccionadas){
				
				boolean existe = false;
				
				if(variable.getMomentoSeguimientoGeneral().getNombre().equals(etapa))
					existe = true;
				
				else if(listadoVariablesEstudioRepetidas.containsKey(variable.getId())){
					
					for(VariableConjuntoDatos v: listadoVariablesEstudioRepetidas.get(variable.getId())){
						
						if(v.getMomentoSeguimientoGeneral().getNombre()!=null && v.getMomentoSeguimientoGeneral().getNombre().equals(etapa)){
							
							existe = true;
							break;
							
						}
						
					}
					
				}
				
				
				if(!existe){
					existeEnTodos = false;
					break;
				}
				
			}
			
			if(existeEnTodos)
				nuevoListadoMomentosDisponibles.add(etapa);	
		}
		
		listadoMomentosDisponibles = nuevoListadoMomentosDisponibles;*/
		
	}
	
	
	public void actualizarVariablesDisponibles(){
		
		Hashtable<Integer, VariableConjuntoDatos> nuevoListadoVariablesEstudio = new Hashtable<Integer, VariableConjuntoDatos>();
		
		for(VariableConjuntoDatos v: listadoVariablesEstudio){
			System.out.println("Variable a revisar: "+v.getVariable().getNombreVariable());
			boolean momentoDisponible = listadoMomentosDisponibles.indexOf(v.getMomentoSeguimientoGeneral().getNombre())!=-1;
		
			if(momentoDisponible&&!variablesSeleccionadas.containsKey(v.getId())){
				
				System.out.println("Variable no eliminada: "+v.getVariable().getNombreVariable());
				nuevoListadoVariablesEstudio.put(v.getId(), v);
				
			}	
		}
		listadoVariablesEstudioDisponibles = nuevoListadoVariablesEstudio;
		
	}
	
public void actualizarVariablesSeleccionadas(){
		
		if(variablesSeleccionadas.keySet().contains(idVariable)){
			ArrayList<VariableConjuntoDatos> nuevoListadoVariablesEstudio = new ArrayList();
			Hashtable<Integer, VariableConjuntoDatos> nuevasVariablesSeleccionadas = new Hashtable<Integer, VariableConjuntoDatos>();

			for(VariableConjuntoDatos v: listadoVariablesSeleccionadas){
				if(listadoMomentosDisponibles.indexOf(v.getMomentoSeguimientoGeneral().getNombre())!=-1){
					nuevoListadoVariablesEstudio.add(v);
					nuevasVariablesSeleccionadas.put(v.getId(), v);
				}	
			}
			listadoVariablesSeleccionadas = nuevoListadoVariablesEstudio;
			variablesSeleccionadas = nuevasVariablesSeleccionadas;
		}
		else{
			List<VariableConjuntoDatos> listadoVariablesSeleccionadasAux =  new ArrayList();
			listadoVariablesSeleccionadasAux.addAll(listadoVariablesSeleccionadas);
			for(VariableConjuntoDatos v: listadoVariablesSeleccionadasAux){
				for(VariableConjuntoDatos v2: listadoVariablesEstudio){
					boolean mismoNombre = v2.getVariable().getNombreVariable().equals(v.getVariable().getNombreVariable());
					boolean mismaHoja = v2.getVariable().getNombreVariable().equals(v.getVariable().getNombreVariable());
					boolean momentoDisponible = listadoMomentosDisponibles.indexOf(v2.getMomentoSeguimientoGeneral().getNombre())!=-1;
					boolean noSeleccionada = listadoVariablesSeleccionadas.indexOf(v2)<0;
					
					if(mismoNombre&&mismaHoja&&momentoDisponible&&noSeleccionada){
						listadoVariablesSeleccionadas.add(v2);	
						variablesSeleccionadas.put(v2.getId(), v2);
					}
				}	
			}
		}
		
		
	}
	
	
	//Metodo para desmarcar o eliminar variable
	public void eliminarVariable(int idVariable) { 
		
		VariableConjuntoDatos variable = (VariableConjuntoDatos)variablesSeleccionadas.get(idVariable);
		for(VariableConjuntoDatos v: listadoVariablesEstudio){
			boolean mismaVariable = v.getVariable().getId()==variable.getId();
			boolean mismaHoja = v.getVariable().getNombreVariable().equals(variable.getVariable().getNombreVariable());
			boolean estaSeleccionada = variablesSeleccionadas.keySet().contains(v.getId());
			
			if(mismaVariable&&mismaHoja&&estaSeleccionada){
				variablesSeleccionadas.remove(v.getId());
				listadoVariablesSeleccionadas.remove(v);	
			}
		}
		actualizarMomentosDisponibles();
		actualizarVariablesSeleccionadas();
		actualizarVariablesDisponibles();
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
		
		List<VariableConjuntoDatos> variablesSeleccionadas = variablesSeleccionadas();
		
		try {
			if (variablesSeleccionadas.size() == 0) {
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
			conjuntoDatos.setVertical(true);
			conjuntoDatos.setDeclaracionHql(declaracionHql());
			entityManager.persist(conjuntoDatos);
			entityManager.flush();
			return "ok";
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]); }
		return "error";
	}

	public String declaracionHql()
	{
		SimpleDateFormat d = new SimpleDateFormat("yyyy-MM-dd");
		String fechaInicio=d.format(conjuntoDatos.getFechaInicio());
		String fechaFin=d.format(conjuntoDatos.getFechaFin());

		String hql = "select variableDato from VariableDato_ensayo variableDato "
				+ "inner join variableDato.variable variable inner join "
				+ "variableDato.crdEspecifico crdEspecifico inner join "
				+ "crdEspecifico.momentoSeguimientoEspecifico momentoSeguimientoEspecifico "
				+ "inner join momentoSeguimientoEspecifico.sujeto sujeto "
				+ "inner join momentoSeguimientoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "where crdEspecifico.estadoHojaCrd.codigo between 3 and 4 "
				+ "and crdEspecifico.hojaCrd.estudio.id = " + String.valueOf(estudio.getId()) + " " 
				+ "and sujeto.eliminado = false ";
		if(conjuntoDatos.getEntidad().getTipoEntidad().getId()!=7L)
			hql+="and sujeto.entidad.id = " + String.valueOf(conjuntoDatos.getEntidad().getId()) + " " ;

		List<VariableConjuntoDatos> variablesSeleccionadas = listadoVariablesSeleccionadas;
		String momentos = "";
		String variables = "";
		for (int i = 0; i < variablesSeleccionadas.size(); i++) {
			Long idVariavle = Long.valueOf(((VariableConjuntoDatos)variablesSeleccionadas.get(i)).getVariable().getId());
			Long idMomento = Long.valueOf(((VariableConjuntoDatos)variablesSeleccionadas.get(i)).getMomentoSeguimientoGeneral().getId());
			if (i == 0) {
				momentos = momentos + idMomento.toString();
				variables = variables + idVariavle.toString();
			}
			else {
				if (!momentos.contains(idMomento.toString()))
					momentos = momentos + "," + idMomento.toString();
				if (!variables.contains(idVariavle.toString())) {
					variables = variables + "," + idVariavle.toString();
				}
			}
		}
		hql = hql + "and momentoSeguimientoGeneral.id in (" + momentos + ") and variable.id in (" + variables + ") " 
				+ "order by momentoSeguimientoEspecifico.fechaInicio, variableDato.id";
		return hql;
	}
	
	public List<VariableConjuntoDatos> variablesSeleccionadas(){
		
		List<VariableConjuntoDatos> listadoVariablesSeleccionadasAux = new ArrayList<VariableConjuntoDatos>();
		
		
		for(VariableConjuntoDatos v: listadoVariablesSeleccionadas){
			
			listadoVariablesSeleccionadasAux.add(v);
			
			if(listadoVariablesEstudioRepetidas.containsKey(v.getId())){
				
				for(VariableConjuntoDatos v2: listadoVariablesEstudioRepetidas.get(v.getId())){
					
					if(listadoMomentosDisponibles.indexOf(v2.getMomentoSeguimientoGeneral().getNombre())!=-1)
						listadoVariablesSeleccionadasAux.add(v2);
					
				}
				
			}
			
		}
		
		return listadoVariablesSeleccionadasAux;
		
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
	
	public void filtrarVertical() {
		setFiltradoVertical(!filtradoVertical);
		if(filtradoVertical)
			setTest("Filtrar Horizontalmente");
		else
			setTest("Filtrar Verticalmente");
		
		desseleccionarTodoVariable();
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

	public void setFiltradoVertical(boolean seleccTodoCRD) {
		this.filtradoVertical = filtradoVertical;
	}
	
	public boolean isFiltradoVertical() {
		return filtradoVertical;
	}
	
	public void setTest(String test) {
		this.textCambiarModo = test;
	}
	
	public String getTest() {
		return textCambiarModo;
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
	public List<String> getListadoMomentosDisponibles() {
		return listadoMomentosDisponibles;
	}
	public void setListadoMomentosDisponibles(
			List<String> listadoMomentosDisponibles) {
		this.listadoMomentosDisponibles = listadoMomentosDisponibles;
	}
	public Hashtable<Integer, VariableConjuntoDatos> getListadoVariablesEstudioDisponibles() {
		return listadoVariablesEstudioDisponibles;
	}
	public void setListadoVariablesEstudioDisponibles(
			Hashtable<Integer, VariableConjuntoDatos> listadoVariablesEstudioDisponibles) {
		this.listadoVariablesEstudioDisponibles = listadoVariablesEstudioDisponibles;
	}
	
	
}

