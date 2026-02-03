package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.exportarConjuntoDatos;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;










//import org.apache.batik.dom.util.ListNodeList;
//import org.apache.poi.hssf.usermodel.HSSFCellStyle;
//import org.apache.poi.hssf.usermodel.HSSFFont;
//import org.apache.poi.hssf.usermodel.HSSFWorkbook;
//import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import com.pmstation.spss.DataConstants;
import com.pmstation.spss.SPSSWriter;

import java.io.OutputStream;


@Name("exportarConjuntoDatosComun")
@Scope(ScopeType.CONVERSATION)
public class ExportarConjuntoDatosComun {

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	private List<CronogramaConjuntoDatos> listadoCronogramasConjuntoDatos = new ArrayList<CronogramaConjuntoDatos>();
	private List<SujetoConjuntoDatos> listadoSujetosConjuntoDatos = new ArrayList<SujetoConjuntoDatos>();
	private List<HojaCrd_ensayo> listadoHojasCRD = new ArrayList<HojaCrd_ensayo>();
	private List<Cronograma_ensayo> listadoCronogramas = new ArrayList<Cronograma_ensayo>();
	private List<Sujeto_ensayo> listadoSujetos = new ArrayList<Sujeto_ensayo>();
	private Hashtable<Long, Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>> momentosEspesificosPorSujeto = new Hashtable<Long, Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>>();
	private List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEspesificos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
	private List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEspesificosConGrupoVariable = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
	private List<Integer> listadoCantFilasMomentosEspesificosConGrupoVariable = new ArrayList<Integer>();
	private List<MomentoSeguimientoGeneral_ensayo> listadoMomentos = new ArrayList<MomentoSeguimientoGeneral_ensayo>();
	private List<CrdEspecifico_ensayo> listadoCrdEspesifico = new ArrayList<CrdEspecifico_ensayo>();
	private List<VariableDato_ensayo> listadoVariableDato = new ArrayList<VariableDato_ensayo>();
	private List<Variable_ensayo> listadoVariables = new ArrayList<Variable_ensayo>();
	private Hashtable<Long, List<GrupoVariables_ensayo>> gruposVariablesPorCrdEspesifico = new Hashtable<Long, List<GrupoVariables_ensayo>>();
	List<List<List<String>>> tuplasFiltradoVertical = new ArrayList();
	List<Integer> indicesTuplasVertical = new ArrayList();
	private boolean inicioConversacion = false;
	private boolean vertical = false;
	private int frozenColCount;
	private long idConjuntoDatos;

	private ConjuntoDatos_ensayo conjuntoDatos = new ConjuntoDatos_ensayo();

	private Estudio_ensayo estudio;
	private Date fechaExportar;
	private String path;

	// Metodo para inicializar el conjunto de datos a exportar.
	// Nueva forma de presentacion de los checkboxs en la extraccion
	public void inicializarConjuntoDatos() {
		try {
			
			/////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////
			/////////////////Cargar los Datos/////////////////////////
			/////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////
			conjuntoDatos = ((ConjuntoDatos_ensayo) entityManager.find(
					ConjuntoDatos_ensayo.class, idConjuntoDatos));
			vertical = conjuntoDatos.getVertical()!=null?conjuntoDatos.getVertical():false;
			
			
			
			fechaExportar = Calendar.getInstance().getTime();
			
			//Comparar si la fecha fin del conjunto de datos es null sustituir por la fecha actual
			Date fechaFin = new SimpleDateFormat("dd/MM/yyyy").parse("01/01/2000");			
			if(conjuntoDatos.getFechaFin().equals(fechaFin)){
				fechaFin = Calendar.getInstance().getTime();
			}
			else
				fechaFin = conjuntoDatos.getFechaFin();			
			//
						
			String consult = conjuntoDatos.getDeclaracionHql();

			int index = consult.indexOf("where");

			String newConsult = consult.substring(0, index + 5)
					+ " variable.eliminado=false and (variableDato.eliminado=false or variableDato.eliminado is null)  and"
					+ consult.substring(index + 5);

			listadoVariableDato = entityManager.createQuery(newConsult)
					.getResultList();
			
			
			
			/////////////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////PROCESAR LOS DATOS////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////
			//////////////////////////////////////////////////////////////////////////////////
			/////////////////////////////////////////////////////////////////////////////////
			////////////////////////////////////////////////////////////////////////////////
			
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			for (VariableDato_ensayo variableDato : listadoVariableDato) {
				
				//Saca el sujeto de la iteracion para una variable
				//y formatea la fecha de inclusion de dicho sujeto
				//y la guarda en una variable
				Sujeto_ensayo sujeto = variableDato.getCrdEspecifico()
						.getMomentoSeguimientoEspecifico().getSujeto();
				String fecha = sujeto.getFechaInclucion();
				boolean fechaInclucionValida = false;
				if (fecha != null) {
					Date fechaInclucionSujeto = formatter.parse(fecha);
					fechaInclucionValida = (conjuntoDatos.getFechaInicio()
							.compareTo(fechaInclucionSujeto) <= 0 && conjuntoDatos
							.getFechaFin().compareTo(fechaInclucionSujeto) >= 0);			
				}
				
				if(!fechaInclucionValida){
					continue;
				}

				
				
				
								
				if (conjuntoDatos.getPesquisaje()) {
					
					
					
					//si el conjunto es de pesquisaje y 
					//la fecha de inclusion del sujeto es valida
					//separa en variables la hoja crd 
					//el momento de seguimiento general a
					//la variable en si 
					//el momento de seguimiento especifico
					//el cronograma
					// y el crd especifico
					HojaCrd_ensayo hojaCrd = variableDato
							.getCrdEspecifico().getHojaCrd();
					MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = variableDato
							.getCrdEspecifico()
							.getMomentoSeguimientoEspecifico()
							.getMomentoSeguimientoGeneral();
					Variable_ensayo variable = variableDato.getVariable();
					MomentoSeguimientoEspecifico_ensayo momentoSeguimientoEspecifico = variableDato
							.getCrdEspecifico()
							.getMomentoSeguimientoEspecifico();
					if (sujeto.getFechaInterrupcion()!=null && 
							momentoSeguimientoEspecifico.getFechaInicio().getTime() <= sujeto.getFechaInterrupcion().getTime()
									|| !momentoSeguimientoEspecifico.getMomentoSeguimientoGeneral().getProgramado() 
							&&
							(fechaInclucionValida || fecha == null) || 
							sujeto.getFechaInterrupcion()==null && (fechaInclucionValida || fecha == null)
							) {
						
						Cronograma_ensayo cronograma = variableDato
								.getCrdEspecifico()
								.getMomentoSeguimientoEspecifico()
								.getMomentoSeguimientoGeneral().getCronograma();
						CrdEspecifico_ensayo crdEspesifico = variableDato
								.getCrdEspecifico();
						
						
						
						
						
						
						
						//agregan las valores de:
						//crdEspesifico,variable, momento de seguimiento especifico
						//y el cronograma
						//extraidos de la variable dato
						//a sus respectivas listas revisando primero si no existen ya en estas
						//dejando sin utilizar de momento
						//a: hojaCrd y momentoSeguimientoGeneral
						if (!listadoCrdEspesifico.contains(crdEspesifico)) {
							listadoCrdEspesifico.add(crdEspesifico);
						}

						if (!listadoVariables.contains(variable))
							listadoVariables.add(variable);
						if (!listadoMomentosEspesificos
								.contains(momentoSeguimientoEspecifico))
							listadoMomentosEspesificos
									.add(momentoSeguimientoEspecifico);
						if (!listadoCronogramas.contains(cronograma))
							listadoCronogramas.add(cronograma);
						
						
						
						//Agrega el sujeto a su listado correspondiente
						//si no existe ya en el
						//y annade a listadoMomentoSujeto(lo que parece ser una lista que enlaza al sujeto
						//y los momentos en los que existe) 
						//el moment de seguimiento especifico antes extraido
						//y luego enlaza esta lista de momentos especificos con su momento general
						//en la hashtable momentosEspesificosPorMomento
						//y agrega esta a la hashtable global
						//momentosEspesificosPorSujeto con el idSujeto como llave
						if (!listadoSujetos.contains(sujeto)) {
							listadoSujetos.add(sujeto);
							long idSujeto = sujeto.getId();
							long idMomentoGeneral = momentoSeguimientoGeneral
									.getId();
							List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
							listadoMomentosSujeto
									.add(momentoSeguimientoEspecifico);
							Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosPorMomento = new Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>();
							momentosEspesificosPorMomento.put(idMomentoGeneral,
									listadoMomentosSujeto);
							momentosEspesificosPorSujeto.put(idSujeto,
									momentosEspesificosPorMomento);

						} 
						// si el sujeto ya existia entonces:
						// revisa si el momento general de esta iteracion
						//ya se encontraba en sus momentos especificos globales
						//lo annade a la lista si no se encuentra
						//y si ya existia revisa si existia tambien
						//el momento especifico en la lista
						//y lo agrega en caso de que no
						else {
							long idSujeto = sujeto.getId();
							long idMomentoGeneral = momentoSeguimientoGeneral
									.getId();
							if (!momentosEspesificosPorSujeto.get(idSujeto)
									.containsKey(idMomentoGeneral)) {
								List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
								listadoMomentosSujeto
										.add(momentoSeguimientoEspecifico);
								Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosPorMomento = momentosEspesificosPorSujeto
										.get(idSujeto);
								momentosEspesificosPorMomento
										.put(idMomentoGeneral,
												listadoMomentosSujeto);
								momentosEspesificosPorSujeto.put(idSujeto,
										momentosEspesificosPorMomento);
							} else {
								if (!momentosEspesificosPorSujeto.get(idSujeto)
										.get(idMomentoGeneral)
										.contains(momentoSeguimientoEspecifico))
									momentosEspesificosPorSujeto.get(idSujeto)
											.get(idMomentoGeneral)
											.add(momentoSeguimientoEspecifico);
							}

						}
						
						
						
						
						//Agrega a sus respectivas listas el momento de seguimiento general
						//y la hoja crd que habia extraido antes siempre que no existieran ya
						if (!listadoMomentos
								.contains(momentoSeguimientoGeneral))
							listadoMomentos.add(momentoSeguimientoGeneral);
						if (!listadoHojasCRD.contains(hojaCrd)) {
							listadoHojasCRD.add(hojaCrd);
						}

						
						
						
						
						
						
						
						
						
						
						// add para saber la cantidad maxima de repeticiones de
						// un
						// momento especifico en dependencia de los grupos de
						// variable
						if (variablePerteneceGrupo(variableDato.getVariable())) {
							GrupoVariables_ensayo grupo = variableDato
									.getVariable().getGrupoVariables();
							if (gruposVariablesPorCrdEspesifico.size() == 0) {
								List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
								listadoGrupos.add(grupo);
								gruposVariablesPorCrdEspesifico.put(
										crdEspesifico.getId(), listadoGrupos);
								listadoMomentosEspesificosConGrupoVariable
										.add(momentoSeguimientoEspecifico);
								Long cantMax = 0L;
								cantMax = (Long) entityManager
										.createQuery(
												"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
														+ "where variableDato.variable.id =:idVariable "
														+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
										.setParameter("idVariable",
												variable.getId())
										.setParameter("idCrdEspesifico",
												crdEspesifico.getId())
										.getSingleResult();
								listadoCantFilasMomentosEspesificosConGrupoVariable
										.add(cantMax.intValue());

							} else if (!listadoMomentosEspesificosConGrupoVariable
									.contains(momentoSeguimientoEspecifico)) {
								List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
								listadoGrupos.add(grupo);
								gruposVariablesPorCrdEspesifico.put(
										crdEspesifico.getId(), listadoGrupos);
								listadoMomentosEspesificosConGrupoVariable
										.add(momentoSeguimientoEspecifico);
								Long cantMax = 0L;
								cantMax = (Long) entityManager
										.createQuery(
												"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
														+ "where variableDato.variable.id =:idVariable "
														+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
										.setParameter("idVariable",
												variable.getId())
										.setParameter("idCrdEspesifico",
												crdEspesifico.getId())
										.getSingleResult();
								listadoCantFilasMomentosEspesificosConGrupoVariable
										.add(cantMax.intValue());

							} else {
								if (!gruposVariablesPorCrdEspesifico
										.containsKey(crdEspesifico.getId())) {
									List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
									listadoGrupos.add(grupo);
									gruposVariablesPorCrdEspesifico.put(
											crdEspesifico.getId(),
											listadoGrupos);
									listadoMomentosEspesificosConGrupoVariable
											.add(momentoSeguimientoEspecifico);
									Long cantMax = 0L;
									cantMax = (Long) entityManager
											.createQuery(
													"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
															+ "where variableDato.variable.id =:idVariable "
															+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
											.setParameter("idVariable",
													variable.getId())
											.setParameter("idCrdEspesifico",
													crdEspesifico.getId())
											.getSingleResult();
									listadoCantFilasMomentosEspesificosConGrupoVariable
											.add(cantMax.intValue());
								} else {
									if (!gruposVariablesPorCrdEspesifico.get(
											crdEspesifico.getId()).contains(
											grupo)) {
										gruposVariablesPorCrdEspesifico.get(
												crdEspesifico.getId()).add(
												grupo);
										int posMsEspesificoConGrupoVariable = listadoMomentosEspesificosConGrupoVariable
												.indexOf(momentoSeguimientoEspecifico);
										int cantMaxAnterior = listadoCantFilasMomentosEspesificosConGrupoVariable
												.get(posMsEspesificoConGrupoVariable);
										Long cantMax = 0L;
										cantMax = (Long) entityManager
												.createQuery(
														"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
																+ "where variableDato.variable.id =:idVariable "
																+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
												.setParameter("idVariable",
														variable.getId())
												.setParameter(
														"idCrdEspesifico",
														crdEspesifico.getId())
												.getSingleResult();

										if (cantMax.intValue() > cantMaxAnterior)
											listadoCantFilasMomentosEspesificosConGrupoVariable
													.set(posMsEspesificoConGrupoVariable,
															cantMax.intValue());
									}
								}

							}

						}
					}
				} else{
					HojaCrd_ensayo hojaCrd = variableDato
							.getCrdEspecifico().getHojaCrd();
					MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = variableDato
							.getCrdEspecifico()
							.getMomentoSeguimientoEspecifico()
							.getMomentoSeguimientoGeneral();
					Variable_ensayo variable = variableDato
							.getVariable();
					MomentoSeguimientoEspecifico_ensayo momentoSeguimientoEspecifico = variableDato
							.getCrdEspecifico()
							.getMomentoSeguimientoEspecifico();
						if (sujeto.getFechaInterrupcion()!=null && 
								momentoSeguimientoEspecifico.getFechaInicio().getTime() <= sujeto.getFechaInterrupcion().getTime() 
								|| !momentoSeguimientoEspecifico.getMomentoSeguimientoGeneral().getProgramado() 
								&& fechaInclucionValida
								||
								(sujeto.getFechaInterrupcion()==null && fechaInclucionValida)) {

							Cronograma_ensayo cronograma = variableDato
									.getCrdEspecifico()
									.getMomentoSeguimientoEspecifico()
									.getMomentoSeguimientoGeneral()
									.getCronograma();
							CrdEspecifico_ensayo crdEspesifico = variableDato
									.getCrdEspecifico();
							if (!listadoCrdEspesifico.contains(crdEspesifico)) {
								listadoCrdEspesifico.add(crdEspesifico);
							}

							if (!listadoVariables.contains(variable))
								listadoVariables.add(variable);
							if (!listadoMomentosEspesificos
									.contains(momentoSeguimientoEspecifico))
								listadoMomentosEspesificos
										.add(momentoSeguimientoEspecifico);
							if (!listadoCronogramas.contains(cronograma))
								listadoCronogramas.add(cronograma);
							if (!listadoSujetos.contains(sujeto)) {
								listadoSujetos.add(sujeto);
								long idSujeto = sujeto.getId();
								long idMomentoGeneral = momentoSeguimientoGeneral
										.getId();
								List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
								listadoMomentosSujeto
										.add(momentoSeguimientoEspecifico);
								Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosPorMomento = new Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>();
								momentosEspesificosPorMomento
										.put(idMomentoGeneral,
												listadoMomentosSujeto);
								momentosEspesificosPorSujeto.put(idSujeto,
										momentosEspesificosPorMomento);

							} else {
								long idSujeto = sujeto.getId();
								long idMomentoGeneral = momentoSeguimientoGeneral
										.getId();
								if (!momentosEspesificosPorSujeto.get(idSujeto)
										.containsKey(idMomentoGeneral)) {
									List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosSujeto = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
									listadoMomentosSujeto
											.add(momentoSeguimientoEspecifico);
									Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosPorMomento = momentosEspesificosPorSujeto
											.get(idSujeto);
									momentosEspesificosPorMomento.put(
											idMomentoGeneral,
											listadoMomentosSujeto);
									momentosEspesificosPorSujeto.put(idSujeto,
											momentosEspesificosPorMomento);
								} else {
									if (!momentosEspesificosPorSujeto
											.get(idSujeto)
											.get(idMomentoGeneral)
											.contains(
													momentoSeguimientoEspecifico))
										momentosEspesificosPorSujeto
												.get(idSujeto)
												.get(idMomentoGeneral)
												.add(momentoSeguimientoEspecifico);
								}

							}
							if (!listadoMomentos
									.contains(momentoSeguimientoGeneral))
								listadoMomentos.add(momentoSeguimientoGeneral);
							if (!listadoHojasCRD.contains(hojaCrd)) {
								listadoHojasCRD.add(hojaCrd);
							}

							// add para saber la cantidad maxima de repeticiones
							// de un
							// momento espesifico en dependencia de los grupos
							// de
							// variable
							if (variablePerteneceGrupo(variableDato
									.getVariable())) {
								GrupoVariables_ensayo grupo = variableDato
										.getVariable().getGrupoVariables();
								if (gruposVariablesPorCrdEspesifico.size() == 0) {
									List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
									listadoGrupos.add(grupo);
									gruposVariablesPorCrdEspesifico.put(
											crdEspesifico.getId(),
											listadoGrupos);
									listadoMomentosEspesificosConGrupoVariable
											.add(momentoSeguimientoEspecifico);
									Long cantMax = 0L;
									cantMax = (Long) entityManager
											.createQuery(
													"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
															+ "where variableDato.variable.id =:idVariable "
															+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
											.setParameter("idVariable",
													variable.getId())
											.setParameter("idCrdEspesifico",
													crdEspesifico.getId())
											.getSingleResult();
									listadoCantFilasMomentosEspesificosConGrupoVariable
											.add(cantMax.intValue());

								} else if (!listadoMomentosEspesificosConGrupoVariable
										.contains(momentoSeguimientoEspecifico)) {
									List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
									listadoGrupos.add(grupo);
									gruposVariablesPorCrdEspesifico.put(
											crdEspesifico.getId(),
											listadoGrupos);
									listadoMomentosEspesificosConGrupoVariable
											.add(momentoSeguimientoEspecifico);
									Long cantMax = 0L;
									cantMax = (Long) entityManager
											.createQuery(
													"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
															+ "where variableDato.variable.id =:idVariable "
															+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
											.setParameter("idVariable",
													variable.getId())
											.setParameter("idCrdEspesifico",
													crdEspesifico.getId())
											.getSingleResult();
									listadoCantFilasMomentosEspesificosConGrupoVariable
											.add(cantMax.intValue());

								} else {
									if (!gruposVariablesPorCrdEspesifico
											.containsKey(crdEspesifico.getId())) {
										List<GrupoVariables_ensayo> listadoGrupos = new ArrayList<GrupoVariables_ensayo>();
										listadoGrupos.add(grupo);
										gruposVariablesPorCrdEspesifico.put(
												crdEspesifico.getId(),
												listadoGrupos);
										listadoMomentosEspesificosConGrupoVariable
												.add(momentoSeguimientoEspecifico);
										Long cantMax = 0L;
										cantMax = (Long) entityManager
												.createQuery(
														"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
																+ "where variableDato.variable.id =:idVariable "
																+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
												.setParameter("idVariable",
														variable.getId())
												.setParameter(
														"idCrdEspesifico",
														crdEspesifico.getId())
												.getSingleResult();
										listadoCantFilasMomentosEspesificosConGrupoVariable
												.add(cantMax.intValue());
									} else {
										if (!gruposVariablesPorCrdEspesifico
												.get(crdEspesifico.getId())
												.contains(grupo)) {
											gruposVariablesPorCrdEspesifico
													.get(crdEspesifico.getId())
													.add(grupo);
											int posMsEspesificoConGrupoVariable = listadoMomentosEspesificosConGrupoVariable
													.indexOf(momentoSeguimientoEspecifico);
											int cantMaxAnterior = listadoCantFilasMomentosEspesificosConGrupoVariable
													.get(posMsEspesificoConGrupoVariable);
											Long cantMax = 0L;
											cantMax = (Long) entityManager
													.createQuery(
															"select count(DISTINCT variableDato.contGrupo) from VariableDato_ensayo variableDato "
																	+ "where variableDato.variable.id =:idVariable "
																	+ "and variableDato.eliminado = false and variableDato.crdEspecifico.id =:idCrdEspesifico")
													.setParameter("idVariable",
															variable.getId())
													.setParameter(
															"idCrdEspesifico",
															crdEspesifico
																	.getId())
													.getSingleResult();

											if (cantMax.intValue() > cantMaxAnterior)
												listadoCantFilasMomentosEspesificosConGrupoVariable
														.set(posMsEspesificoConGrupoVariable,
																cantMax.intValue());
										}
									}

								}

							}
						}
					

				}

				/*
				 * else{ listadoVariableDato = new
				 * ArrayList<VariableDato_ensayo>(); }*/
				 
			}

			// Ciclo para crear el arbol de la leynda
			for (Integer i = 0; i < listadoCronogramas.size(); i++) {
				List<MomentoSeguimientoConjuntoDatos> listadoMs = new ArrayList<MomentoSeguimientoConjuntoDatos>();
				Integer contMomentos = 0;
				for (Integer j = 0; j < listadoMomentos.size(); j++) {
					List<MsHojaCrdConjuntoDatos> listadoMsHojasCrd = new ArrayList<MsHojaCrdConjuntoDatos>();
					Integer contHojas = 0;
					if (listadoMomentos.get(j).getCronograma()
							.equals(listadoCronogramas.get(i))) {
						List<HojaCrd_ensayo> listadoHojasMomentoAux = new ArrayList<HojaCrd_ensayo>();
						for (Integer k = 0; k < listadoCrdEspesifico.size(); k++) {
							HojaCrd_ensayo hojaCrd = listadoCrdEspesifico
									.get(k).getHojaCrd();
							if (!listadoHojasMomentoAux.contains(hojaCrd)
									&& listadoCrdEspesifico.get(k)
											.getMomentoSeguimientoEspecifico()
											.getMomentoSeguimientoGeneral()
											.equals(listadoMomentos.get(j))) {
								listadoHojasMomentoAux.add(hojaCrd);
								String idHoja = SeamResourceBundle.getBundle()
										.getString("prm_inicialHoja_ens")
										+ contHojas.toString();
								List<Variable_ensayo> listadoVariablesTotal = listadoVariablePorHoja(hojaCrd);
								listadoVariablesTotal
										.retainAll(listadoVariables);
								MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd = (MomentoSeguimientoGeneralHojaCrd_ensayo) entityManager
										.createQuery(
												"select msHojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd "
														+ "where msHojaCrd.momentoSeguimientoGeneral.id =:idMomento "
														+ "and msHojaCrd.hojaCrd.id =:idHoja")
										.setParameter("idMomento",
												listadoMomentos.get(j).getId())
										.setParameter("idHoja", hojaCrd.getId())
										.getSingleResult();
								MsHojaCrdConjuntoDatos hoja = new MsHojaCrdConjuntoDatos(
										idHoja, msHojaCrd,
										listadoVariablesTotal);
								listadoMsHojasCrd.add(hoja);
								contHojas++;
							}

						}
						String idMomento = SeamResourceBundle.getBundle()
								.getString("prm_inicialMomento_ens")
								+ contMomentos.toString();
						MomentoSeguimientoConjuntoDatos ms = new MomentoSeguimientoConjuntoDatos(
								idMomento, listadoMomentos.get(j),
								listadoMsHojasCrd);
						listadoMs.add(ms);
						contMomentos++;
					}

				}
				String idCronograma = SeamResourceBundle.getBundle().getString(
						"prm_inicialGrupo_ens")
						+ i.toString();
				CronogramaConjuntoDatos cronogramaConjuntoDatos = new CronogramaConjuntoDatos(
						idCronograma, listadoCronogramas.get(i), listadoMs);
				listadoCronogramasConjuntoDatos.add(cronogramaConjuntoDatos);
			}

			// Ciclo para crear el listado de sujetos y repetirlo segun los
			// momentos
			int id = 0;
			for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
				for (int i = 0; i < listadoSujetos.size(); i++) {
					int max = 0;
					if (listadoSujetos
							.get(i)
							.getGrupoSujetos()
							.equals(cronograma.getCronograma()
									.getGrupoSujetos())
							|| (!listadoSujetos
									.get(i)
									.getGrupoSujetos()
									.equals(cronograma.getCronograma()
											.getGrupoSujetos())
									&& cronograma.getCronograma()
											.getGrupoSujetos().getNombreGrupo()
											.equals("Grupo Pesquisaje") && listadoCronogramasConjuntoDatos
									.size() == 1)) {
						long idSujeto = listadoSujetos.get(i).getId();
						List<MomentoSeguimientoConjuntoDatos> listadoMsAux = cronograma
								.getListadoMs();
						Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosTotal = momentosEspesificosPorSujeto
								.get(idSujeto);
						for (MomentoSeguimientoConjuntoDatos ms : listadoMsAux) {
							// int cantPorMs = 0;
							long idMomentoGeneral = ms.getMs().getId();
							List<MomentoSeguimientoEspecifico_ensayo> listadoMomentos = momentosEspesificosTotal
									.get(idMomentoGeneral);
							if (listadoMomentos != null) {
								if (listadoMomentos.size() > max) {

									for (int j = max; j < listadoMomentos
											.size(); j++) {
										listadoSujetosConjuntoDatos
												.add(new SujetoConjuntoDatos(
														id, j, 0,
														listadoSujetos.get(i),
														momentosEspesificosTotal));
										id++;

									}
									max = listadoMomentos.size();

								}
							}

						}

					}
				}
			}

			// add para repetir los grupos de variable
			for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {

				for (int i = 0; i < listadoSujetosConjuntoDatos.size(); i++) {
					if (listadoSujetosConjuntoDatos
							.get(i)
							.getSujeto()
							.getGrupoSujetos()
							.equals(cronograma.getCronograma()
									.getGrupoSujetos())) {
						List<SujetoConjuntoDatos> listadoSujetosConjuntoDatosAux = new ArrayList<SujetoConjuntoDatos>();
						Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>> momentosEspesificosPorMomento = listadoSujetosConjuntoDatos
								.get(i).getMomentosEspecificos();
						List<MomentoSeguimientoConjuntoDatos> listadoMsAux = cronograma
								.getListadoMs();
						int max = 1;
						int cantConjuntoDatosAux = 0;

						for (MomentoSeguimientoConjuntoDatos ms : listadoMsAux) {
							if (momentosEspesificosPorMomento.containsKey(ms
									.getMs().getId())) {
								List<MomentoSeguimientoEspecifico_ensayo> msEspesificoList = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
								if (momentosEspesificosPorMomento.get(
										ms.getMs().getId()).size() != 0)
									msEspesificoList.addAll(momentosEspesificosPorMomento
											.get(ms.getMs().getId())); 
								else{
									if(momentosEspesificosPorMomento
											.get(ms.getMs().getId()).size()<listadoSujetosConjuntoDatos
											.get(i)
											.getPosMsEspesifico()){
										msEspesificoList.add(  momentosEspesificosPorMomento
												.get(ms.getMs().getId())
												.get(listadoSujetosConjuntoDatos
														.get(i)
														.getPosMsEspesifico()));
									}
								}
								
								for (MomentoSeguimientoEspecifico_ensayo msEspesifico : msEspesificoList) {
									if (listadoMomentosEspesificosConGrupoVariable
											.contains(msEspesifico)) { 

										int posMsEspesificoConGrupoVariable = listadoMomentosEspesificosConGrupoVariable
												.indexOf(msEspesifico);
										int cantFilas = listadoCantFilasMomentosEspesificosConGrupoVariable
												.get(posMsEspesificoConGrupoVariable);	
										
										if (cantFilas > max) {
											for (int j = max; j < cantFilas; j++) {
												listadoSujetosConjuntoDatosAux
														.add(new SujetoConjuntoDatos(
																j,
																listadoSujetosConjuntoDatos
																		.get(i)
																		.getPosMsEspesifico(),
																j,
																listadoSujetosConjuntoDatos
																		.get(i)
																		.getSujeto(),
																momentosEspesificosPorMomento));

											}
											max = cantFilas;
											cantConjuntoDatosAux += listadoSujetosConjuntoDatosAux
													.size();
											List<SujetoConjuntoDatos> listadoSujetosConjuntoDatosTmp = new ArrayList<SujetoConjuntoDatos>();
											for (int l = i + 1; l < listadoSujetosConjuntoDatos
													.size(); l++) {
												listadoSujetosConjuntoDatosTmp
														.add(listadoSujetosConjuntoDatos
																.get(l));
											}
											listadoSujetosConjuntoDatos
													.removeAll(listadoSujetosConjuntoDatosTmp);
											for (int l = 0; l < listadoSujetosConjuntoDatosAux
													.size(); l++) {
												listadoSujetosConjuntoDatos
														.add(listadoSujetosConjuntoDatosAux
																.get(l));

											}
											listadoSujetosConjuntoDatosAux = new ArrayList<SujetoConjuntoDatos>();
											for (int l = 0; l < listadoSujetosConjuntoDatosTmp
													.size(); l++) {
												listadoSujetosConjuntoDatos
														.add(listadoSujetosConjuntoDatosTmp
																.get(l));
											}

										}

									}
								}
								
							}

						}
						i += cantConjuntoDatosAux;
					}

				}

			}

			frozenColCount = 2;
			if (conjuntoDatos.getMostrarEstadoSujeto())
				frozenColCount++;
			if (conjuntoDatos.getMostrarFechaInclusionSujeto())
				frozenColCount++;
			if (conjuntoDatos.getMostrarInicialesSujeto())
				frozenColCount++;
			if (conjuntoDatos.getMostrarNumeroInclusionSujeto())
				frozenColCount++;
			if (conjuntoDatos.getMostrarSitioClinicaSujeto())
				frozenColCount++;
			
			if(vertical)
				filtrarVertical();
			inicioConversacion = true;
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage(), new Object[0]);
		}
		

	}

	public void repetirPorGrupoVariable() {

	}

	// add metodo de RFA
	public Date fechaInclucion(Sujeto_ensayo sujeto) throws ParseException {
		Date fechaInclusion = null;
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		String[] fechaArreglo = sujeto.getFechaInclucion().split("/");
		String fecha = "";
		if (fechaArreglo.length == 1) {
			fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
		} else if (fechaArreglo.length == 2) {
			fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
		} else if (fechaArreglo.length == 3) {
			fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/"
					+ fechaArreglo[0];
		}
		fechaInclusion = formatter.parse(fecha);

		return fechaInclusion;
	}

	// Metodo que devuelve si la variable pertenece a un grupo o no
	public boolean variablePerteneceGrupo(Variable_ensayo variable) {
		try {
			if (variable.getGrupoVariables() != null)
				return true;
			return false;
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return false;
		}
	}

	// Metodo que devuelve las hojas crd de un momento ordenadas
	public List<MomentoSeguimientoGeneralHojaCrd_ensayo> hojasMomentos(
			MomentoSeguimientoGeneral_ensayo momento) {
		List<MomentoSeguimientoGeneralHojaCrd_ensayo> hojas = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>();
		hojas = entityManager
				.createQuery(
						"select msHojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd "
								+ "where msHojaCrd.eliminado<>true and msHojaCrd.momentoSeguimientoGeneral.id =:idMomento "
								+ "order by msHojaCrd.id")
				.setParameter("idMomento", momento.getId()).getResultList();
		return hojas;
	}

	// Metodo que devuelve el listado de variables por hoja
	public List<Variable_ensayo> listadoVariablePorHoja(HojaCrd_ensayo hojaCrd) {
		List<Variable_ensayo> listadoVariables = entityManager
				.createQuery(
						"select variable from Variable_ensayo variable "
								+ "inner join variable.seccion seccion "
								+ "inner join seccion.hojaCrd hojaCrd "
								+ "where variable.eliminado<>true and seccion.eliminado <> true "
								+ "and hojaCrd.id =:idHoja "
								+ "order by variable.numeroPregunta, variable.idClinica")
				.setParameter("idHoja", hojaCrd.getId()).getResultList();
		return listadoVariables;
	}

	public MomentoSeguimientoEspecifico_ensayo momentoSegumientoEspesifico(
			long idMs, SujetoConjuntoDatos sujeto) {
		try {
			MomentoSeguimientoEspecifico_ensayo msEspesifico = new MomentoSeguimientoEspecifico_ensayo();

			if (sujeto.getMomentosEspecificos().get(idMs).size() == 1)
				msEspesifico = sujeto.getMomentosEspecificos().get(idMs).get(0);
			else
				msEspesifico = sujeto.getMomentosEspecificos().get(idMs)
						.get(sujeto.getPosMsEspesifico());

			return msEspesifico;
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return null;
		}
	}

	/*
	 * @SuppressWarnings("unchecked") public Object[]
	 * obtenerValores(Variable_ensayo var) { Object[] valores;
	 * List<VariableDato_ensayo> aux = new ArrayList<VariableDato_ensayo>(); if
	 * (var.getTipoDato().getCodigo().equals("NOM")) { try { aux =
	 * (List<VariableDato_ensayo>) entityManager .createQuery(
	 * "select NomVar from VariableDato_ensayo NomVar where NomVar.variable=:Variable and NomVar.crdEspecifico=:Hoja"
	 * ) .setParameter("Variable", var) .setParameter("Hoja",
	 * this.hoja).getResultList(); } catch (Exception e) { return null; } }
	 * 
	 * if (aux.size() == 0) { return null; }
	 * 
	 * valores = new Object[aux.size()];
	 * 
	 * for (int i = 0; i < aux.size(); i++) { valores[i] =
	 * aux.get(i).getValor().toString(); }
	 * 
	 * return valores;
	 * 
	 * 
	 * }
	 */

	public boolean isCheckboxNomenclador(Variable_ensayo variable) {
		return variable.getPresentacionFormulario().getId() == 8
				|| variable.getPresentacionFormulario().getId() == 10
				|| variable.getPresentacionFormulario().getId() == 11;
	}

	@SuppressWarnings("unchecked")
	public List<String> getNamesOfCheckBoxColums(Variable_ensayo variable,
			MomentoSeguimientoGeneral_ensayo momento) {
		List<NomencladorValor_ensayo> aux = new ArrayList<NomencladorValor_ensayo>();
		List<String> names = new ArrayList<String>();
		aux = (List<NomencladorValor_ensayo>) entityManager
				.createQuery(
						"select NomVar from NomencladorValor_ensayo NomVar where NomVar.nomenclador=:Variable")
				.setParameter("Variable", variable.getNomenclador())
				.getResultList();
		for (NomencladorValor_ensayo v : aux) {
			if (v.getValor() != null && !v.getValor().isEmpty()) {
				names.add(v.getValor());
			}
		}

		return names;
	}

	public String getValueForChechBoxs(Variable_ensayo variable,
			MomentoSeguimientoGeneral_ensayo momento,
			SujetoConjuntoDatos sujeto, String nameColumn, HojaCrd_ensayo crd) {
		MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
				momento.getId(), sujeto);

		/*
		 * busco en todas las variables que le correspondan al sujeto si alguna
		 * tiene el grupo distinto de null eso significa
		 */
		if (msEspesifico == null) {
			return " ";
		}
		List<String> posibles_valores = getNamesOfCheckBoxColums(variable,
				momento);
		boolean flag = false;
		for (VariableDato_ensayo v : listadoVariableDato) {
			if (v.getValor() != null
					&& v.getVariable().getId() == variable.getId()
					&& (v.getEliminado() == null || v.getEliminado() == false)
					&& v.getCrdEspecifico().getMomentoSeguimientoEspecifico()
							.getId() == msEspesifico.getId()
					&& v.getCrdEspecifico().getHojaCrd().getId() == crd.getId()) {
				if (v.getVariable().getGrupoVariables() != null
						&& v.getContGrupo() == sujeto.getPosFilaGrupo() + 1) {
					for (String d : posibles_valores) {// ver los grupos de esta
														// variable bien en la
														// bd a ver q hay para
														// ese sujeto
						if (v.getValor() != null
								&& v.getValor().equalsIgnoreCase(d)) {
							flag = true;
						}
					}
				} else {
					for (String d : posibles_valores) {
						if (v.getValor() != null
								&& v.getValor().equalsIgnoreCase(d)) {
							flag = true;
						}
					}
				}
			}
		}

		if (!flag) {
			return " ";
		}

		for (VariableDato_ensayo v : listadoVariableDato) {
			if (v.getValor() != null
					&& v.getVariable().getId() == variable.getId()
					&& (v.getEliminado() == null || v.getEliminado() == false)
					&& v.getCrdEspecifico().getMomentoSeguimientoEspecifico()
							.getId() == msEspesifico.getId()
					&& v.getCrdEspecifico().getHojaCrd().getId() == crd.getId()) {

				// PARA DEVOLVER NULL CUANDO LA VARIABLE TIENE NOMENCLADOR

				if (variable.getGrupoVariables() != null) {

					if (v.getContGrupo() == sujeto.getPosFilaGrupo() + 1) {
						if (v.getValor().equalsIgnoreCase(nameColumn)) {
							return "Si";
						}
					}

				} else {
					if (v.getValor().equalsIgnoreCase(nameColumn)) {
						return "Si";
					}
				}
			}
		}

		return "No";

	}

	// Metodo que devuelve el valor de la variable
	@SuppressWarnings("unchecked")
	public String valorVariablePorCrdEspecifico(Variable_ensayo variable,
			MomentoSeguimientoGeneral_ensayo momento,
			SujetoConjuntoDatos sujeto, HojaCrd_ensayo crd) {
		try {
			List<VariableDato_ensayo> variableDatos = new ArrayList<VariableDato_ensayo>();
			String valor = "";
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			if (msEspesifico == null)
				return " ";
			else if (!listadoMomentosEspesificosConGrupoVariable
					.contains(msEspesifico)) {
				for (VariableDato_ensayo variableDato : listadoVariableDato) {
					if (variableDato.getVariable().getId() == variable.getId()
							&& variableDato.getCrdEspecifico()
									.getMomentoSeguimientoEspecifico().getId() == msEspesifico
									.getId()
							&& variableDato.getCrdEspecifico().getHojaCrd()
									.getId() == crd.getId()) {
						return variableDato.getValor();
					}

				}
			} else {
				int pos = 0;
				for (VariableDato_ensayo variableDato : listadoVariableDato) {
					if (variableDato.getVariable().getId() == variable.getId()
							&& (variableDato.getEliminado() == null || variableDato
									.getEliminado() == false)
							&& variableDato.getCrdEspecifico()
									.getMomentoSeguimientoEspecifico().getId() == msEspesifico
									.getId()
							&& variableDato.getCrdEspecifico().getHojaCrd()
									.getId() == crd.getId()) {
						variableDatos.add(variableDato);
						if (pos == sujeto.getPosFilaGrupo()
								|| variableDato.getVariable()
										.getGrupoVariables() == null)
							return variableDatos.get(pos).getValor();
						pos++;
					}
				}
			}

			return valor;
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return "";
		}

	}

	// Metodo que devuelve la fecha de inicio del momento
	public Date fechaInicioMs(MomentoSeguimientoGeneral_ensayo momento,
			SujetoConjuntoDatos sujeto) {
		try {
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			return msEspesifico.getFechaInicio();
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return null;
		}
	}

	// Metodo que devuelve la fecha de fin del momento
	public Date fechaFinMs(MomentoSeguimientoGeneral_ensayo momento,
			SujetoConjuntoDatos sujeto) {
		try {
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			return msEspesifico.getFechaFin();
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return null;
		}
	}

	// Metodo que devuelve el estado del momento de seguimiento
	public String estadoMs(MomentoSeguimientoGeneral_ensayo momento,
			SujetoConjuntoDatos sujeto) {
		try {
			// String estado = "";
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			return msEspesifico.getEstadoMomentoSeguimiento().getNombre();

		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return "";
		}
	}

	// Metodo que devuelve el estado de la hoja CRD
	public String estadoCrd(HojaCrd_ensayo hojaCrd,
			MomentoSeguimientoGeneral_ensayo momento, SujetoConjuntoDatos sujeto) {
		try {
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			for (CrdEspecifico_ensayo crdEspecifico : listadoCrdEspesifico) {
				if (crdEspecifico.getHojaCrd().getId() == hojaCrd.getId()
						&& crdEspecifico.getMomentoSeguimientoEspecifico()
								.getId() == msEspesifico.getId())
					return crdEspecifico.getEstadoHojaCrd().getNombre();
			}

			return "";
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return "";
		}
	}

	// Metodo que devuelve el estado de monitoreo del CRD
	public String estadoMonitoreoCrd(HojaCrd_ensayo hojaCrd,
			MomentoSeguimientoGeneral_ensayo momento, SujetoConjuntoDatos sujeto) {
		try {
			MomentoSeguimientoEspecifico_ensayo msEspesifico = momentoSegumientoEspesifico(
					momento.getId(), sujeto);
			for (CrdEspecifico_ensayo crdEspecifico : listadoCrdEspesifico) {
				if (crdEspecifico.getHojaCrd().getId() == hojaCrd.getId()
						&& crdEspecifico.getMomentoSeguimientoEspecifico()
								.getId() == msEspesifico.getId())
					return crdEspecifico.getEstadoMonitoreo().getNombre();
			}
			return "";
		} catch (Exception e) {
			facesMessages.clear();
			// facesMessages.add(e.getMessage(), new Object[0]);
			return "";
		}
	}
	public static String convertirFecha(String fechaStr) {
        // Define el formato de entrada
        SimpleDateFormat formatoEntrada = new SimpleDateFormat("dd-MM-yyyy");
        // Define el formato de salida
        SimpleDateFormat formatoSalida = new SimpleDateFormat("dd/MM/yyyy");
        
        try {
            // Parsear la fecha de entrada
            Date fecha = formatoEntrada.parse(fechaStr);
            // Formatear la fecha en el nuevo formato
            return formatoSalida.format(fecha);
        } catch (ParseException e) {
            e.printStackTrace(); // Manejo de excepciones
            return null; // Retornar null en caso de error
        }
    }
	
	// metodo para parsear fechas 
	public static Date convertirStringADate(String fechaStr) {
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy"); // define el formato de la fecha
        try {
            return formato.parse(fechaStr); // parsea la cadena a un objeto Date
        } catch (ParseException e) {
            e.printStackTrace(); // maneja la excepción en caso de error de parseo
            return null;
        }
	}
	
	//metodo para parsear long a int
	public static int convertirLongAInt(long valorLong) {
        return (int) valorLong;
    }

	// Metodo para exportar a excel (*.xls)
	// RF: 11 Exportar conjunto de datos a Excel
	
	//
	FacesContext aFacesContext = FacesContext.getCurrentInstance();
	ServletContext context = (ServletContext) aFacesContext
			.getExternalContext().getContext();
	
	String rootpath = context.getRealPath("/resources")
			+ "/reports/tempPages/ensayo/";
	
	public String getRootpath() {
		return rootpath;
	}

	public void setRootpath(String rootpath) {
		this.rootpath = rootpath;
	}
	
	public void exportarExcel(String rootpath) {
		
		XSSFWorkbook libro = new XSSFWorkbook();
		// FileOutputStream archivo = new
		// FileOutputStream(SeamResourceBundle.getBundle().getString("msg_ruta_ens")+hoja.getNombreHoja()+".xls");
		// Para poner la letra del excel
		XSSFFont fuente = (XSSFFont) libro.createFont();
		fuente.setFontHeightInPoints((short) 10);
		// fuente.setFontName(fuente. FONT_ARIAL);
		fuente.setBoldweight(XSSFFont.BOLDWEIGHT_BOLD);
		
		//estilos de las celdas 
		CellStyle estiloCeldaFecha = libro.createCellStyle();
		CreationHelper createHelper = libro.getCreationHelper();
        estiloCeldaFecha.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy"));
        estiloCeldaFecha.setWrapText(false);
        estiloCeldaFecha.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
        estiloCeldaFecha.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
        
        CellStyle estiloCeldaFloat = libro.createCellStyle();
        estiloCeldaFloat.setWrapText(false);
        estiloCeldaFloat.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
        estiloCeldaFloat.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP); // Alinear verticalmente al centro

        // Formato numérico para mostrar decimales
        estiloCeldaFloat.setDataFormat(libro.createDataFormat().getFormat("0.0")); // Un decimal

      //Entero
        CellStyle estiloCeldaEntero = libro.createCellStyle();
        estiloCeldaEntero.setWrapText(false);
        estiloCeldaEntero.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
        estiloCeldaEntero.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP); // Alinear verticalmente al centro
        
        // Formato numérico para enteros
        estiloCeldaEntero.setDataFormat(libro.createDataFormat().getFormat("0")); // Sin decimales
        
        
		// Luego creamos el objeto que se encargará de aplicar el estilo a la
		// celda
		// celda encabezado
		XSSFCellStyle estiloCeldaEncabezado = (XSSFCellStyle) libro
				.createCellStyle();
		estiloCeldaEncabezado.setWrapText(false);
		estiloCeldaEncabezado.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
		estiloCeldaEncabezado.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		estiloCeldaEncabezado.setFont(fuente);
		// celda normal
		XSSFCellStyle estiloCelda = (XSSFCellStyle) libro.createCellStyle();
		estiloCelda.setWrapText(false);
		estiloCelda.setAlignment(XSSFCellStyle.ALIGN_JUSTIFY);
		estiloCelda.setVerticalAlignment(XSSFCellStyle.VERTICAL_TOP);
		// estiloCelda.setFont(fuente);

		// Para crear la hoja del Conjunto de datos y sus columnas
		Sheet hoja0 = libro.createSheet(SeamResourceBundle.getBundle()
				.getString("prm_conjuntoDatos_ens"));
		hoja0.setColumnWidth(0, 256 * 30);

		// Datos Generales
		// fila 1
		Row filaDatosGenerales1 = hoja0.createRow((short) 0);

		Cell celdaDatosGenerales = filaDatosGenerales1.createCell((short) 0);
		celdaDatosGenerales.setCellStyle(estiloCeldaEncabezado);
		celdaDatosGenerales.setCellValue(SeamResourceBundle.getBundle()
				.getString("lbl_datosGenerales_ens"));
		// fila 2
		Row filaDatosGenerales2 = hoja0.createRow((short) 1);
		Cell nombreConjunto = filaDatosGenerales2.createCell((short) 0);
		nombreConjunto.setCellStyle(estiloCelda);
		nombreConjunto.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nombreConjuntoDatos_ens"));
		Cell valorNombreConjunto = filaDatosGenerales2.createCell((short) 1);
		valorNombreConjunto.setCellStyle(estiloCelda);
		valorNombreConjunto.setCellValue(conjuntoDatos.getNombre());
		// fila 3
		Row filaDatosGenerales3 = hoja0.createRow((short) 2);
		Cell fechaExportarConjuto = filaDatosGenerales3.createCell((short) 0);
		fechaExportarConjuto.setCellStyle(estiloCelda);
		fechaExportarConjuto.setCellValue(SeamResourceBundle.getBundle()
				.getString("lbl_fechaExportar_ens"));
		Cell valorFechaExportar = filaDatosGenerales3.createCell((short) 1);
		valorFechaExportar.setCellStyle(estiloCelda);
		SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		String fechaActual = formato.format(fechaExportar);
		valorFechaExportar.setCellValue(fechaActual);
		// fila 4
		Row filaDatosGenerales4 = hoja0.createRow((short) 3);
		Cell idEstudio = filaDatosGenerales4.createCell((short) 0);
		idEstudio.setCellStyle(estiloCelda);
		idEstudio.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nombreIdEstudio_ens"));
		Cell valorIdEstudio = filaDatosGenerales4.createCell((short) 1);
		valorIdEstudio.setCellStyle(estiloCelda);
		valorIdEstudio.setCellValue(conjuntoDatos.getEstudio()
				.getIdentificador());
		// fila 5
		Row filaDatosGenerales5 = hoja0.createRow((short) 4);
		Cell nombreEstudio = filaDatosGenerales5.createCell((short) 0);
		nombreEstudio.setCellStyle(estiloCelda);
		nombreEstudio.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nombreEstudio_ens"));
		Cell valorNombreEstudio = filaDatosGenerales5.createCell((short) 1);
		valorNombreEstudio.setCellStyle(estiloCelda);
		valorNombreEstudio.setCellValue(conjuntoDatos.getEstudio().getNombre());
		// fila 6
		Row filaDatosGenerales6 = hoja0.createRow((short) 5);
		Cell cantGrupos = filaDatosGenerales6.createCell((short) 0);
		cantGrupos.setCellStyle(estiloCelda);
		cantGrupos.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_cantGrupos_ens"));
		Cell valorCantGrupos = filaDatosGenerales6.createCell((short) 1);
		valorCantGrupos.setCellStyle(estiloCelda);
		valorCantGrupos.setCellValue(listadoCronogramas.size());

		// fila 7
		Row filaDatosGenerales7 = hoja0.createRow((short) 6);
		Cell cantSujetos = filaDatosGenerales7.createCell((short) 0);
		cantSujetos.setCellStyle(estiloCelda);
		cantSujetos.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_cantSujetos_ens"));
		Cell valorCantSujetos = filaDatosGenerales7.createCell((short) 1);
		valorCantSujetos.setCellStyle(estiloCelda);
		valorCantSujetos.setCellValue(listadoSujetos.size());

		// fila 8
		Row filaDatosGenerales8 = hoja0.createRow((short) 7);
		Cell cantMomentos = filaDatosGenerales8.createCell((short) 0);
		cantMomentos.setCellStyle(estiloCelda);
		cantMomentos.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_cantMomentos_ens"));
		Cell valorCantMomentos = filaDatosGenerales8.createCell((short) 1);
		valorCantMomentos.setCellStyle(estiloCelda);
		valorCantMomentos.setCellValue(listadoMomentos.size());

		// Leyenda
		int posFila = 8;
		for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
			// fila Grupo
			List<MomentoSeguimientoConjuntoDatos> listadoMS = new ArrayList<MomentoSeguimientoConjuntoDatos>();
			Row filaDatosGeneralesGrupo = hoja0.createRow((short) posFila);
			Cell grupo = filaDatosGeneralesGrupo.createCell((short) 0);
			grupo.setCellStyle(estiloCelda);
			grupo.setCellValue(SeamResourceBundle.getBundle().getString(
					"lbl_nombreGrupo_ens"));
			Cell valorGrupo = filaDatosGeneralesGrupo.createCell((short) 1);
			valorGrupo.setCellStyle(estiloCelda);
			valorGrupo.setCellValue(cronograma.getCronograma()
					.getGrupoSujetos().getNombreGrupo());
			listadoMS = cronograma.getListadoMs();
			posFila++;
			for (MomentoSeguimientoConjuntoDatos ms : listadoMS) {
				// fila Momento
				List<MsHojaCrdConjuntoDatos> listadoMsHojasCrd = new ArrayList<MsHojaCrdConjuntoDatos>();
				Row filaDatosGeneralesMomento = hoja0
						.createRow((short) posFila);
				Cell momento = filaDatosGeneralesMomento.createCell((short) 0);
				momento.setCellStyle(estiloCelda);
				momento.setCellValue(SeamResourceBundle.getBundle().getString(
						"lbl_nombreMs_ens"));
				Cell valorMomento = filaDatosGeneralesMomento
						.createCell((short) 1);
				valorMomento.setCellStyle(estiloCelda);
				valorMomento.setCellValue(ms.getMs().getNombre());
				listadoMsHojasCrd = ms.getListadoMsHojasCrd();
				posFila++;
				for (MsHojaCrdConjuntoDatos msHojaCrd : listadoMsHojasCrd) {
					// fila Momento
					Row filaDatosGeneralesHojaCrd = hoja0
							.createRow((short) posFila);
					Cell hojaCrd = filaDatosGeneralesHojaCrd
							.createCell((short) 0);
					hojaCrd.setCellStyle(estiloCelda);
					hojaCrd.setCellValue(SeamResourceBundle.getBundle()
							.getString("lbl_nombreHojaCrd_ens"));
					Cell valorHojaCrd = filaDatosGeneralesHojaCrd
							.createCell((short) 1);
					valorHojaCrd.setCellStyle(estiloCelda);
					valorHojaCrd.setCellValue(msHojaCrd.getMsHojaCrd()
							.getHojaCrd().getNombreHoja());
					Cell leyendaHojaCrd = filaDatosGeneralesHojaCrd
							.createCell((short) 2);
					leyendaHojaCrd.setCellStyle(estiloCelda);
					String idGrupo = cronograma.getId();
					String idMomento = ms.getId();
					String idHojaCrd = msHojaCrd.getId();
					leyendaHojaCrd.setCellValue(idGrupo + "_" + idMomento + "_"
							+ idHojaCrd);

					posFila++;
				}
			}
		}
		// Fin leyenda
		// Fin Datos Generales
		// Datos del conjunto de datos
		Row filaDatos = hoja0.createRow((short) posFila);

		Cell celdaDatos = filaDatos.createCell((short) 0);
		celdaDatos.setCellStyle(estiloCeldaEncabezado);
		celdaDatos.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_datosConjuntoDatos_ens"));
		posFila++;

		int posCol = 2;
		if(vertical){
			
			Row filaEncabezadoDatos = hoja0.createRow((short) posFila);
			for(int i=0;i<tuplasFiltradoVertical.get(0).size();i++){
				
				Cell nuevaColumna = filaEncabezadoDatos.createCell((short) i);
				nuevaColumna.setCellStyle(estiloCeldaEncabezado);
				nuevaColumna.setCellValue(tuplasFiltradoVertical.get(0).get(i).get(0));
				
			}
			posFila++;
			for(int e=0;e<tuplasFiltradoVertical.size();e++,posFila++){
				
				Row fila = hoja0.createRow((short) posFila + 1);

				for(int i=0;i<tuplasFiltradoVertical.get(e).size();i++){
					Cell celda = fila.createCell((short) i);
					celda.setCellStyle(estiloCelda);
					celda.setCellValue(tuplasFiltradoVertical.get(e).get(i).get(1));
					
				}
			}
			
			
		}
		else{
			for (int i = 0; i < listadoSujetosConjuntoDatos.size(); i++) {
				if (i == 0) {
					Row filaEncabezadoDatos = hoja0.createRow((short) posFila);
					Row filaDatosSujeto = hoja0.createRow((short) posFila + 1);
	
					Cell grupo = filaEncabezadoDatos.createCell((short) 0);
					grupo.setCellStyle(estiloCeldaEncabezado);
					grupo.setCellValue(SeamResourceBundle.getBundle().getString(
							"lbl_nombreGrupo_ens"));
					Cell valorGrupo = filaDatosSujeto.createCell((short) 0);
					valorGrupo.setCellStyle(estiloCelda);
					valorGrupo.setCellValue(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getGrupoSujetos().getNombreGrupo());
	
					Cell sujeto = filaEncabezadoDatos.createCell((short) 1);
					sujeto.setCellStyle(estiloCeldaEncabezado);
					sujeto.setCellValue(SeamResourceBundle.getBundle().getString(
							"lbl_idUnicaSujeto_ens"));
					Cell valorSujeto = filaDatosSujeto.createCell((short) 1);
					valorSujeto.setCellStyle(estiloCelda);
					valorSujeto.setCellValue(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getCodigoPaciente());
	
					if (conjuntoDatos.getMostrarFechaInclusionSujeto()) {
						Cell fechaInclusionSujeto = filaEncabezadoDatos
								.createCell((short) posCol);
						fechaInclusionSujeto.setCellStyle(estiloCeldaEncabezado);
						fechaInclusionSujeto.setCellValue(SeamResourceBundle
								.getBundle().getString("lbl_fechaInclusion_ens"));
						Cell valorFechaInclusionSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorFechaInclusionSujeto.setCellStyle(estiloCeldaFecha);
						String fechaInclu = listadoSujetosConjuntoDatos.get(i)
								.getSujeto().getFechaInclucion();
						//llamo al metodo para parsear string a fecha 
						Date fechaInclusion = convertirStringADate(fechaInclu);
						valorFechaInclusionSujeto.setCellValue(fechaInclusion);
						posCol++;
					}
					if (conjuntoDatos.getMostrarEstadoSujeto()) {
						Cell estadoSujeto = filaEncabezadoDatos
								.createCell((short) posCol);
						estadoSujeto.setCellStyle(estiloCeldaEncabezado);
						estadoSujeto.setCellValue(SeamResourceBundle.getBundle()
								.getString("lbl_estadoSujeto_ens"));
						Cell valorEstadoSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorEstadoSujeto.setCellStyle(estiloCelda);
						valorEstadoSujeto.setCellValue(listadoSujetosConjuntoDatos
								.get(i).getSujeto().getEstadoInclusion()
								.getNombre());
						posCol++;
					}
					if (conjuntoDatos.getMostrarInicialesSujeto()) {
						Cell inicialesSujeto = filaEncabezadoDatos
								.createCell((short) posCol);
						inicialesSujeto.setCellStyle(estiloCeldaEncabezado);
						inicialesSujeto.setCellValue(SeamResourceBundle.getBundle()
								.getString("lbl_inicialesSujeto_ens"));
						Cell valorInicialesSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorInicialesSujeto.setCellStyle(estiloCelda);
						String iniciales = listadoSujetosConjuntoDatos.get(i)
								.getSujeto().getInicialesPaciente();
						valorInicialesSujeto.setCellValue(iniciales);
						posCol++;
					}
					if (conjuntoDatos.getMostrarNumeroInclusionSujeto()) {
						Cell numeroInclusion = filaEncabezadoDatos
								.createCell((short) posCol);
						numeroInclusion.setCellStyle(estiloCeldaEncabezado);
						numeroInclusion.setCellValue(SeamResourceBundle.getBundle()
								.getString("lbl_numeroInclusion_ens"));
						Cell valorNumeroInclusionSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorNumeroInclusionSujeto.setCellStyle(estiloCelda);
						long numeroInclucionLong = listadoSujetosConjuntoDatos.get(i).getSujeto().getNumeroInclucion();
						// uso del método para convertir de long a int
					    int numeroInclucionInt = convertirLongAInt(numeroInclucionLong);
					    // establecer el valor de la celda como int
					    valorNumeroInclusionSujeto.setCellValue(numeroInclucionInt);
						posCol++;
					}
					if (conjuntoDatos.getMostrarSitioClinicaSujeto()) {
						Cell sitioClinicaSujeto = filaEncabezadoDatos
								.createCell((short) posCol);
						sitioClinicaSujeto.setCellStyle(estiloCeldaEncabezado);
						sitioClinicaSujeto.setCellValue(SeamResourceBundle
								.getBundle().getString("lbl_sitioClinica_ens"));
						Cell valorSitioClinicaSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorSitioClinicaSujeto.setCellStyle(estiloCelda);
						valorSitioClinicaSujeto
								.setCellValue(listadoSujetosConjuntoDatos.get(i)
										.getSujeto().getInicialesCentro());
						posCol++;
					}
					for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
						List<MomentoSeguimientoConjuntoDatos> listadoMs = cronograma
								.getListadoMs();
						String idGrupo = cronograma.getId();
						for (MomentoSeguimientoConjuntoDatos ms : listadoMs) {
							String idMomento = ms.getId();
							if (conjuntoDatos.getMostrarFechaInicioMs()) {
								Cell fechaInicioMs = filaEncabezadoDatos
										.createCell((short) posCol);
								fechaInicioMs.setCellStyle(estiloCeldaEncabezado);
								fechaInicioMs.setCellValue(SeamResourceBundle
										.getBundle().getString(
												"lbl_fechaInicio_ens")
										+ "_" + idGrupo + "_" + idMomento);
								Cell valorFechaInicioMs = filaDatosSujeto
										.createCell((short) posCol);
								valorFechaInicioMs.setCellStyle(estiloCeldaFecha);
								if (fechaInicioMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)) != null) {
									String fechaIniMs = formato.format(fechaFinMs(
											ms.getMs(),
											listadoSujetosConjuntoDatos.get(i)));
									valorFechaInicioMs.setCellStyle(estiloCeldaFecha);
									String fechaIniMsString = convertirFecha(fechaIniMs);
									Date fechaIniMsDate =convertirStringADate(fechaIniMsString);
									valorFechaInicioMs.setCellValue(fechaIniMsDate);
								} else
									valorFechaInicioMs.setCellValue("");
								posCol++;
							}
							if (conjuntoDatos.getMostrarFechaFinMs()) {
								Cell fechaFinMs = filaEncabezadoDatos
										.createCell((short) posCol);
								fechaFinMs.setCellStyle(estiloCeldaEncabezado);
								fechaFinMs.setCellValue(SeamResourceBundle
										.getBundle().getString("lbl_fechaFin_ens")
										+ "_" + idGrupo + "_" + idMomento);
								Cell valorFechaFinMs = filaDatosSujeto
										.createCell((short) posCol);
								//estilo de celda fecha (para que la celda sepa que va a tener una fecha)
								valorFechaFinMs.setCellStyle(estiloCeldaFecha);
								if (fechaFinMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)) != null) {
									String fechaIniMs = formato.format(fechaFinMs(
											ms.getMs(),
											listadoSujetosConjuntoDatos.get(i)));
									//Parsear string a Fecha
									valorFechaFinMs.setCellStyle(estiloCeldaFecha);
									String fechaFinMsString = convertirFecha(fechaIniMs);
									Date fechaFinMsDate =convertirStringADate(fechaFinMsString);
									valorFechaFinMs.setCellValue(fechaFinMsDate);
								} else
									valorFechaFinMs.setCellValue("");
	
								posCol++;
							}
							if (conjuntoDatos.getMostrarEstadoMs()) {
								Cell estadoMs = filaEncabezadoDatos
										.createCell((short) posCol);
								estadoMs.setCellStyle(estiloCeldaEncabezado);
								estadoMs.setCellValue(SeamResourceBundle
										.getBundle().getString("lbl_estadoMs_ens")
										+ "_" + idGrupo + "_" + idMomento);
								Cell valorEstadoMs = filaDatosSujeto
										.createCell((short) posCol);
								valorEstadoMs.setCellStyle(estiloCelda);
								valorEstadoMs.setCellValue(estadoMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)));
	
								posCol++;
							}
							List<MsHojaCrdConjuntoDatos> listadoMsHojaCrd = ms
									.getListadoMsHojasCrd();
							for (MsHojaCrdConjuntoDatos msHojaCrd : listadoMsHojaCrd) {
								String idMsHojaCrd = msHojaCrd.getId();
								if (conjuntoDatos.getMostrarEstadoCrdEspecifico()) {
									Cell estadoHojaCrd = filaEncabezadoDatos
											.createCell((short) posCol);
									estadoHojaCrd
											.setCellStyle(estiloCeldaEncabezado);
									estadoHojaCrd.setCellValue(SeamResourceBundle
											.getBundle().getString(
													"lbl_estadoCrd_ens")
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_"
											+ idMsHojaCrd);
									Cell valorEstadoHojaCrd = filaDatosSujeto
											.createCell((short) posCol);
									valorEstadoHojaCrd.setCellStyle(estiloCelda);
									valorEstadoHojaCrd.setCellValue(estadoCrd(
											msHojaCrd.getMsHojaCrd().getHojaCrd(),
											ms.getMs(),
											listadoSujetosConjuntoDatos.get(i)));
									posCol++;
								}
								if (conjuntoDatos
										.getMostrarEstadoMonitoreoCrdEspecifico()) {
									Cell estadoHojaCrd = filaEncabezadoDatos
											.createCell((short) posCol);
									estadoHojaCrd
											.setCellStyle(estiloCeldaEncabezado);
									estadoHojaCrd.setCellValue(SeamResourceBundle
											.getBundle().getString(
													"lbl_estadoMonitoreoCrd_ens")
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_"
											+ idMsHojaCrd);
									Cell valorEstadoHojaCrd = filaDatosSujeto
											.createCell((short) posCol);
									valorEstadoHojaCrd.setCellStyle(estiloCelda);
									valorEstadoHojaCrd
											.setCellValue(estadoMonitoreoCrd(
													msHojaCrd.getMsHojaCrd()
															.getHojaCrd(), ms
															.getMs(),
													listadoSujetosConjuntoDatos
															.get(i)));
									posCol++;
								}
								List<Variable_ensayo> listadoVariables = msHojaCrd
										.getListadoVariables();
								for (Variable_ensayo variable : listadoVariables) {
									// para las variables que sus nomencladores son
									// checkboxs
									if (isCheckboxNomenclador(variable)) {
										for (String n : this
												.getNamesOfCheckBoxColums(variable,
														ms.getMs())) {
											Cell nombreVariable = filaEncabezadoDatos
													.createCell((short) posCol);
											nombreVariable
													.setCellStyle(estiloCeldaEncabezado);
											nombreVariable.setCellValue(n + "_"
													+ idGrupo + "_" + idMomento
													+ "_" + idMsHojaCrd);
	
											Cell valorNombreVariable = filaDatosSujeto
													.createCell((short) posCol);
											valorNombreVariable
													.setCellStyle(estiloCelda);
											valorNombreVariable.setCellValue(this
													.getValueForChechBoxs(variable,
															ms.getMs(),
															listadoSujetosConjuntoDatos
																	.get(i), n,
															msHojaCrd
																	.getMsHojaCrd()
																	.getHojaCrd()));
											posCol++;
										}
	
									} else {
										Cell nombreVariable = filaEncabezadoDatos
												.createCell((short) posCol);
										nombreVariable
												.setCellStyle(estiloCeldaEncabezado);
										nombreVariable.setCellValue(variable
												.getNombreVariable()
												+ "_"
												+ idGrupo
												+ "_"
												+ idMomento
												+ "_"
												+ idMsHojaCrd);
										Cell valorNombreVariable = filaDatosSujeto
												.createCell((short) posCol);
										valorNombreVariable
												.setCellStyle(estiloCelda);
										valorNombreVariable
												.setCellValue(valorVariablePorCrdEspecifico(
														variable, ms.getMs(),
														listadoSujetosConjuntoDatos
																.get(i), msHojaCrd
																.getMsHojaCrd()
																.getHojaCrd()));
										posCol++;
									}
	
								}
							}
						}
					}
					posFila += 2;
				} else {
	
					Row filaDatosSujeto = hoja0.createRow((short) posFila);
	
					Cell valorGrupo = filaDatosSujeto.createCell((short) 0);
					valorGrupo.setCellStyle(estiloCelda);
					valorGrupo.setCellValue(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getGrupoSujetos().getNombreGrupo());
	
					Cell valorSujeto = filaDatosSujeto.createCell((short) 1);
					valorSujeto.setCellStyle(estiloCelda);
					valorSujeto.setCellValue(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getCodigoPaciente());
					posCol = 2;
					if (conjuntoDatos.getMostrarFechaInclusionSujeto()) {
	
						Cell valorFechaInclusionSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorFechaInclusionSujeto.setCellStyle(estiloCeldaFecha);
						String fechaInclu = listadoSujetosConjuntoDatos.get(i)
								.getSujeto().getFechaInclucion();
						//llamo al metodo para parsear string a fecha 
						Date fechaInclusion = convertirStringADate(fechaInclu);
						valorFechaInclusionSujeto.setCellValue(fechaInclusion);
						posCol++;
					}
					if (conjuntoDatos.getMostrarEstadoSujeto()) {
	
						Cell valorEstadoSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorEstadoSujeto.setCellStyle(estiloCelda);
						valorEstadoSujeto.setCellValue(listadoSujetosConjuntoDatos
								.get(i).getSujeto().getEstadoInclusion()
								.getNombre());
						posCol++;
					}
					if (conjuntoDatos.getMostrarInicialesSujeto()) {
	
						Cell valorInicialesSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorInicialesSujeto.setCellStyle(estiloCelda);
						valorInicialesSujeto
								.setCellValue(listadoSujetosConjuntoDatos.get(i)
										.getSujeto().getInicialesPaciente());
						posCol++;
					}
					if (conjuntoDatos.getMostrarNumeroInclusionSujeto()) {
	
						Cell valorNumeroInclusionSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorNumeroInclusionSujeto.setCellStyle(estiloCelda);
						long numeroInclucionLong = listadoSujetosConjuntoDatos.get(i).getSujeto().getNumeroInclucion();
						// uso del método para convertir de long a int
					    int numeroInclucionInt = convertirLongAInt(numeroInclucionLong);
					    // establecer el valor de la celda como int
					    valorNumeroInclusionSujeto.setCellValue(numeroInclucionInt);
						posCol++;
					}
					if (conjuntoDatos.getMostrarSitioClinicaSujeto()) {
	
						Cell valorSitioClinicaSujeto = filaDatosSujeto
								.createCell((short) posCol);
						valorSitioClinicaSujeto.setCellStyle(estiloCelda);
						valorSitioClinicaSujeto
								.setCellValue(listadoSujetosConjuntoDatos.get(i)
										.getSujeto().getInicialesCentro());
						posCol++;
					}
					for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
						List<MomentoSeguimientoConjuntoDatos> listadoMs = cronograma
								.getListadoMs();
	
						for (MomentoSeguimientoConjuntoDatos ms : listadoMs) {
	
							if (conjuntoDatos.getMostrarFechaInicioMs()) {
	
								Cell valorFechaInicioMs = filaDatosSujeto
										.createCell((short) posCol);
								valorFechaInicioMs.setCellStyle(estiloCeldaFecha);
								if (fechaInicioMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)) != null) {
									String fechaIniMs = formato
											.format(fechaInicioMs(ms.getMs(),
													listadoSujetosConjuntoDatos
															.get(i)));
									valorFechaInicioMs.setCellStyle(estiloCeldaFecha);
									String fechaIniMsString = convertirFecha(fechaIniMs);
									Date fechaIniMsDate =convertirStringADate(fechaIniMsString);
									valorFechaInicioMs.setCellValue(fechaIniMsDate);
									
								} else
									valorFechaInicioMs.setCellValue("");
								posCol++;
							}
							if (conjuntoDatos.getMostrarFechaFinMs()) {
	
								Cell valorFechaFinMs = filaDatosSujeto
										.createCell((short) posCol);
								valorFechaFinMs.setCellStyle(estiloCeldaFecha);
								if (fechaFinMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)) != null) {
									String fechaIniMs = formato.format(fechaFinMs(
											ms.getMs(),
											listadoSujetosConjuntoDatos.get(i)));
									valorFechaFinMs.setCellStyle(estiloCeldaFecha);
									String fechaFinMsString = convertirFecha(fechaIniMs);
									Date fechaFinMsDate =convertirStringADate(fechaFinMsString);
									valorFechaFinMs.setCellValue(fechaFinMsDate);
								} else
									valorFechaFinMs.setCellValue("");
	
								posCol++;
							}
							if (conjuntoDatos.getMostrarEstadoMs()) {
	
								Cell valorEstadoMs = filaDatosSujeto
										.createCell((short) posCol);
								valorEstadoMs.setCellStyle(estiloCelda);
								valorEstadoMs.setCellValue(estadoMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)));
								posCol++;
							}
							List<MsHojaCrdConjuntoDatos> listadoMsHojaCrd = ms
									.getListadoMsHojasCrd();
							for (MsHojaCrdConjuntoDatos msHojaCrd : listadoMsHojaCrd) {
	
								if (conjuntoDatos.getMostrarEstadoCrdEspecifico()) {
	
									Cell valorEstadoHojaCrd = filaDatosSujeto
											.createCell((short) posCol);
									valorEstadoHojaCrd.setCellStyle(estiloCelda);
									valorEstadoHojaCrd.setCellValue(estadoCrd(
											msHojaCrd.getMsHojaCrd().getHojaCrd(),
											ms.getMs(),
											listadoSujetosConjuntoDatos.get(i)));
									posCol++;
								}
								if (conjuntoDatos
										.getMostrarEstadoMonitoreoCrdEspecifico()) {
	
									Cell valorEstadoHojaCrd = filaDatosSujeto
											.createCell((short) posCol);
									valorEstadoHojaCrd.setCellStyle(estiloCelda);
									valorEstadoHojaCrd
											.setCellValue(estadoMonitoreoCrd(
													msHojaCrd.getMsHojaCrd()
															.getHojaCrd(), ms
															.getMs(),
													listadoSujetosConjuntoDatos
															.get(i)));
									posCol++;
								}
								List<Variable_ensayo> listadoVariables = msHojaCrd
										.getListadoVariables();
								for (Variable_ensayo variable : listadoVariables) {
									if (isCheckboxNomenclador(variable)) {
										for (String n : this
												.getNamesOfCheckBoxColums(variable,
														ms.getMs())) {
											Cell valorNombreVariable = filaDatosSujeto
													.createCell((short) posCol);
											valorNombreVariable
													.setCellStyle(estiloCelda);
											valorNombreVariable
													.setCellStyle(estiloCelda);
											valorNombreVariable.setCellValue(this
													.getValueForChechBoxs(variable,
															ms.getMs(),
															listadoSujetosConjuntoDatos
																	.get(i), n,
															msHojaCrd
																	.getMsHojaCrd()
																	.getHojaCrd()));
											posCol++;
										}
	
									} else {
										Cell valorNombreVariable = filaDatosSujeto
												.createCell((short) posCol);
										valorNombreVariable
												.setCellStyle(estiloCelda);
										valorNombreVariable
												.setCellValue(valorVariablePorCrdEspecifico(
														variable, ms.getMs(),
														listadoSujetosConjuntoDatos
																.get(i), msHojaCrd
																.getMsHojaCrd()
																.getHojaCrd()));
										posCol++;
									}
								}
							}
						}
					}
					posFila++;
				}
	
			}
		}
		autoSizeColumns(hoja0, posCol);

		

		

		SimpleDateFormat format = new SimpleDateFormat(
				"dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");

		String name_crd = stripAccents(conjuntoDatos.getNombre());

		String zipName = name_crd + " " + format.format(new Date()) + ".xls";

		path = "/resources/reports/tempPages/ensayo/" + zipName;

		try {

			FileOutputStream archivo = new FileOutputStream(new File(rootpath,
					zipName));
			libro.write(archivo);
			// libro.close();
			archivo.close();

		} catch (Exception ex) {
			ex.printStackTrace();
			// return "";
		}

	}

	/**
	 * Metodos para eliminar los acentos del nombre de la hoja ppara expportarla
	 */
	private static final String ORIGINAL = "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00D1\u00F1\u00DC\u00FC";
	private static final String REPLACEMENT = "AaEeIiOoUuNnUu";

	public static String stripAccents(String str) {
		if (str == null) {
			return null;
		}
		char[] array = str.toCharArray();
		for (int index = 0; index < array.length; index++) {
			int pos = ORIGINAL.indexOf(array[index]);
			if (pos > -1) {
				array[index] = REPLACEMENT.charAt(pos);
			}
		}
		return new String(array);
	}

	// Metodo para dar tamanno automatico a las columnas.
	private void autoSizeColumns(Sheet sheetData, int maxColNum) {
		try {

			for (int col = 0; col < maxColNum; col++) {
				sheetData.autoSizeColumn(col);
				int cwidth = sheetData.getColumnWidth(col);
				cwidth += 500;
				sheetData.setColumnWidth(col, cwidth);

			}

		} catch (Exception ex) {
			ex.printStackTrace();
			// return "";
		}
	}

	// Metodo para exportar a spss (*.sav)
	// RF: 12 Exportar conjunto de datos a SPSS
	public void exportarSpss() {
		try {

			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();
			String rootpath = context.getRealPath("/resources")
					+ "/reports/tempPages/ensayo/";
			SimpleDateFormat format = new SimpleDateFormat(
					"dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");

			String name_crd = stripAccents(conjuntoDatos.getNombre());

			String zipName = name_crd + " " + format.format(new Date())
					+ ".sav";
			path = "/resources/reports/tempPages/ensayo/" + zipName;
			// Open file output stream with filename args[0]
			OutputStream out = new FileOutputStream(new File(rootpath, zipName));

			// Assign SPSS output to the file
			SPSSWriter outSPSS = new SPSSWriter(out, "utf-8");

			// Creating SPSS variable description table
			outSPSS.setCalculateNumberOfCases(false);
			outSPSS.addDictionarySection(-1);
			// Describing varible names and types
			outSPSS.addStringVar(
					funcionReemplazarEspacios(SeamResourceBundle.getBundle()
							.getString("lbl_nombreGrupo_ens")),
					255,
					SeamResourceBundle.getBundle().getString(
							"lbl_nombreGrupo_ens"));
			outSPSS.addStringVar(
					funcionReemplazarEspacios(SeamResourceBundle.getBundle()
							.getString("lbl_idUnicaSujeto_ens")),
					255,
					SeamResourceBundle.getBundle().getString(
							"lbl_idUnicaSujeto_ens"));
			// outSPSS.addNumericVar("countries", 8, 2, "Number of countries");

			if (conjuntoDatos.getMostrarFechaInclusionSujeto()) {
				outSPSS.addStringVar(
						funcionReemplazarEspacios(SeamResourceBundle
								.getBundle()
								.getString("lbl_fechaInclusion_ens")),
						255,
						SeamResourceBundle.getBundle().getString(
								"lbl_fechaInclusion_ens"));
			}
			if (conjuntoDatos.getMostrarEstadoSujeto()) {
				outSPSS.addStringVar(
						funcionReemplazarEspacios(SeamResourceBundle
								.getBundle().getString("lbl_estadoSujeto_ens")),
						255,
						SeamResourceBundle.getBundle().getString(
								"lbl_estadoSujeto_ens"));

			}
			if (conjuntoDatos.getMostrarInicialesSujeto()) {
				outSPSS.addStringVar(
						funcionReemplazarEspacios(SeamResourceBundle
								.getBundle().getString(
										"lbl_inicialesSujeto_ens")),
						255,
						SeamResourceBundle.getBundle().getString(
								"lbl_inicialesSujeto_ens"));

			}
			if (conjuntoDatos.getMostrarNumeroInclusionSujeto()) {
				outSPSS.addNumericVar(
						funcionReemplazarEspacios(SeamResourceBundle
								.getBundle().getString(
										"lbl_numeroInclusion_ens")),
						8,
						0,
						SeamResourceBundle.getBundle().getString(
								"lbl_numeroInclusion_ens"));
			}
			if (conjuntoDatos.getMostrarSitioClinicaSujeto()) {
				outSPSS.addStringVar(
						funcionReemplazarEspacios(SeamResourceBundle
								.getBundle().getString("lbl_sitioClinica_ens")),
						255,
						SeamResourceBundle.getBundle().getString(
								"lbl_sitioClinica_ens"));

			}

			for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
				List<MomentoSeguimientoConjuntoDatos> listadoMs = cronograma
						.getListadoMs();
				String idGrupo = cronograma.getId();

				for (MomentoSeguimientoConjuntoDatos ms : listadoMs) {
					String idMomento = ms.getId();

					if (conjuntoDatos.getMostrarFechaInicioMs()) {
						outSPSS.addDateVar(
								funcionReemplazarEspacios(SeamResourceBundle
										.getBundle().getString(
												"lbl_fechaInicio_ens"))
										+ "_" + idGrupo + "_" + idMomento + "_",
								DataConstants.DATE_TYPE_01,
								SeamResourceBundle.getBundle().getString(
										"lbl_fechaInicio_ens")
										+ "_" + idGrupo + "_" + idMomento + "_",
								null);

					}
					if (conjuntoDatos.getMostrarFechaFinMs()) {
						outSPSS.addDateVar(
								funcionReemplazarEspacios(SeamResourceBundle
										.getBundle().getString(
												"lbl_fechaFin_ens"))
										+ "_" + idGrupo + "_" + idMomento + "_",
								DataConstants.DATE_TYPE_01,
								SeamResourceBundle.getBundle().getString(
										"lbl_fechaFin_ens")
										+ "_" + idGrupo + "_" + idMomento + "_",
								null);

					}
					if (conjuntoDatos.getMostrarEstadoMs()) {
						outSPSS.addStringVar(
								funcionReemplazarEspacios(SeamResourceBundle
										.getBundle().getString(
												"lbl_estadoMs_ens"))
										+ "_" + idGrupo + "_" + idMomento + "_",
								255,
								SeamResourceBundle.getBundle().getString(
										"lbl_estadoMs_ens")
										+ "_" + idGrupo + "_" + idMomento + "_");

					}
					List<MsHojaCrdConjuntoDatos> listadoMsHojaCrd = ms
							.getListadoMsHojasCrd();
					for (MsHojaCrdConjuntoDatos msHojaCrd : listadoMsHojaCrd) {
						String idMsHojaCrd = msHojaCrd.getId();
						if (conjuntoDatos.getMostrarEstadoCrdEspecifico()) {
							outSPSS.addStringVar(
									funcionReemplazarEspacios(SeamResourceBundle
											.getBundle().getString(
													"lbl_estadoCrd_ens"))
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_" + idMsHojaCrd,
									255,
									SeamResourceBundle.getBundle().getString(
											"lbl_estadoCrd_ens")
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_" + idMsHojaCrd);

						}
						if (conjuntoDatos
								.getMostrarEstadoMonitoreoCrdEspecifico()) {
							outSPSS.addStringVar(
									funcionReemplazarEspacios(SeamResourceBundle
											.getBundle()
											.getString(
													"lbl_estadoMonitoreoCrd_ens"))
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_" + idMsHojaCrd,
									255,
									SeamResourceBundle.getBundle().getString(
											"lbl_estadoMonitoreoCrd_ens")
											+ "_"
											+ idGrupo
											+ "_"
											+ idMomento
											+ "_" + idMsHojaCrd);

						}
						List<Variable_ensayo> listadoVariables = msHojaCrd
								.getListadoVariables();
						String prueba = " ";

						// PARA PONER EL NOMBRE A LAS VARIABLES

						for (Variable_ensayo variable : listadoVariables) {

							if (isCheckboxNomenclador(variable)) {
								for (String n : this.getNamesOfCheckBoxColums(
										variable, ms.getMs())) {
									prueba = n.replace(" ", "_");

									if (variable.getTipoDato().getCodigo()
											.equals("INT")) {
										outSPSS.addNumericVar(prueba + "_"
												+ idGrupo + "_" + idMomento
												+ "_" + idMsHojaCrd, 8, 0,
												prueba + "_" + idGrupo + "_"
														+ idMomento + "_"
														+ idMsHojaCrd);
									} else if (variable.getTipoDato()
											.getCodigo().equals("REAL"))
										outSPSS.addNumericVar(prueba + "_"
												+ idGrupo + "_" + idMomento
												+ "_" + idMsHojaCrd, 8, 2,
												prueba + "_" + idGrupo + "_"
														+ idMomento + "_"
														+ idMsHojaCrd);
									else {
										outSPSS.addStringVar(prueba + "_"
												+ idGrupo + "_" + idMomento
												+ "_" + idMsHojaCrd, 255,
												prueba + "_" + idGrupo + "_"
														+ idMomento + "_"
														+ idMsHojaCrd);

									}

								}
							} else {

								prueba = variable.getNombreVariable().replace(
										" ", "_");
								if (variable.getTipoDato().getCodigo()
										.equals("INT")) {
									outSPSS.addNumericVar(prueba + "_"
											+ idGrupo + "_" + idMomento + "_"
											+ idMsHojaCrd, 8, 0, prueba + "_"
											+ idGrupo + "_" + idMomento + "_"
											+ idMsHojaCrd);
								} else if (variable.getTipoDato().getCodigo()
										.equals("REAL"))
									outSPSS.addNumericVar(prueba + "_"
											+ idGrupo + "_" + idMomento + "_"
											+ idMsHojaCrd, 8, 2, prueba + "_"
											+ idGrupo + "_" + idMomento + "_"
											+ idMsHojaCrd);
								/*
								 * else
								 * if(variable.getTipoDato().getCodigo().equals(
								 * "DATE"))
								 * outSPSS.addDateVar(variable.getNombreVariable
								 * ()+"_"+idGrupo+"_"+idMomento+"_"+idMsHojaCrd,
								 * DataConstants.DATE_TYPE_01,
								 * variable.getNombreVariable
								 * ()+"_"+idGrupo+"_"+idMomento
								 * +"_"+idMsHojaCrd,null);
								 */
								else {
									outSPSS.addStringVar(prueba + "_" + idGrupo
											+ "_" + idMomento + "_"
											+ idMsHojaCrd, 255, prueba + "_"
											+ idGrupo + "_" + idMomento + "_"
											+ idMsHojaCrd);

								}

							}

						}
					}
				}
			}

			// Create SPSS variable value define table
			outSPSS.addDataSection();
			for (int i = 0; i < listadoSujetosConjuntoDatos.size(); i++) {
				outSPSS.addData(listadoSujetosConjuntoDatos.get(i).getSujeto()
						.getGrupoSujetos().getNombreGrupo());
				outSPSS.addData(listadoSujetosConjuntoDatos.get(i).getSujeto()
						.getCodigoPaciente());
				// outSPSS.addNumericVar("countries", 8, 2,
				// "Number of countries");
				if (conjuntoDatos.getMostrarFechaInclusionSujeto()) {
					outSPSS.addData(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getFechaInclucion());
				}
				if (conjuntoDatos.getMostrarEstadoSujeto()) {
					outSPSS.addData(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getEstadoInclusion().getNombre());
				}
				if (conjuntoDatos.getMostrarInicialesSujeto()) {
					outSPSS.addData(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getInicialesPaciente());
				}
				if (conjuntoDatos.getMostrarNumeroInclusionSujeto()) {
					outSPSS.addData(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getNumeroInclucion());
				}
				if (conjuntoDatos.getMostrarSitioClinicaSujeto()) {
					outSPSS.addData(listadoSujetosConjuntoDatos.get(i)
							.getSujeto().getInicialesCentro());
				}
				for (CronogramaConjuntoDatos cronograma : listadoCronogramasConjuntoDatos) {
					List<MomentoSeguimientoConjuntoDatos> listadoMs = cronograma
							.getListadoMs();
					for (MomentoSeguimientoConjuntoDatos ms : listadoMs) {
						if (conjuntoDatos.getMostrarFechaInicioMs()) {
							if (fechaInicioMs(ms.getMs(),
									listadoSujetosConjuntoDatos.get(i)) != null) {
								outSPSS.addData(fechaInicioMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)));
							} else
								outSPSS.addData((Date) null);
						}
						if (conjuntoDatos.getMostrarFechaFinMs()) {
							if (fechaFinMs(ms.getMs(),
									listadoSujetosConjuntoDatos.get(i)) != null) {
								outSPSS.addData(fechaFinMs(ms.getMs(),
										listadoSujetosConjuntoDatos.get(i)));
							} else
								outSPSS.addData((Date) null);
						}
						if (conjuntoDatos.getMostrarEstadoMs()) {
							outSPSS.addData(estadoMs(ms.getMs(),
									listadoSujetosConjuntoDatos.get(i)));
						}
						List<MsHojaCrdConjuntoDatos> listadoMsHojaCrd = ms
								.getListadoMsHojasCrd();
						for (MsHojaCrdConjuntoDatos msHojaCrd : listadoMsHojaCrd) {
							if (conjuntoDatos.getMostrarEstadoCrdEspecifico()) {
								outSPSS.addData(estadoCrd(msHojaCrd
										.getMsHojaCrd().getHojaCrd(), ms
										.getMs(), listadoSujetosConjuntoDatos
										.get(i)));
							}
							if (conjuntoDatos
									.getMostrarEstadoMonitoreoCrdEspecifico()) {
								outSPSS.addData(estadoMonitoreoCrd(msHojaCrd
										.getMsHojaCrd().getHojaCrd(), ms
										.getMs(), listadoSujetosConjuntoDatos
										.get(i)));
							}
							List<Variable_ensayo> listadoVariables = msHojaCrd
									.getListadoVariables();
							for (Variable_ensayo variable : listadoVariables) {

								// PARA LOS VALORES

								if (isCheckboxNomenclador(variable)) {
									for (String n : this
											.getNamesOfCheckBoxColums(variable,
													ms.getMs())) {
										String valor = this
												.getValueForChechBoxs(variable,
														ms.getMs(),
														listadoSujetosConjuntoDatos
																.get(i), n,
														msHojaCrd
																.getMsHojaCrd()
																.getHojaCrd());
										outSPSS.addData(valor);
									}

								} else {

									String valor = valorVariablePorCrdEspecifico(
											variable, ms.getMs(),
											listadoSujetosConjuntoDatos.get(i),
											msHojaCrd.getMsHojaCrd()
													.getHojaCrd());
									if (variable.getTipoDato().getCodigo()
											.equals("INT")) { // Integer sale
																// como
																// Long
										Long dato = null;
										if (!valor.equals(""))
											dato = Long.parseLong(valor);
										outSPSS.addData(dato);
									} else if (variable.getTipoDato()
											.getCodigo().equals("REAL")) {
										Double dato = null;
										if (!valor.equals(""))
											dato = Double.parseDouble(valor);
										outSPSS.addData(dato);
									}/*
									 * else
									 * if(variable.getTipoDato().getCodigo()
									 * .equals ("DATE")){ SimpleDateFormat d =
									 * new SimpleDateFormat("yyyy/MM/dd"); Date
									 * dato = null; if(!valor.equals("")) dato =
									 * d.parse(valor); outSPSS.addData(dato); }
									 */
									else
										// todo lo demas es Cadena*/
										outSPSS.addData(valor);
								}

							}
						}
					}
				}

			}

			outSPSS.addFinishSection();

			// Close output stream
			out.close();
		} catch (Exception ex) {
			ex.printStackTrace();
			// return "";
		}
	}

	public String funcionReemplazarEspacios(String Valor) {
		return Valor.replace(" ", "_");
	}

	// Getters and Setters
	public List<CronogramaConjuntoDatos> getListadoCronogramasConjuntoDatos() {
		return listadoCronogramasConjuntoDatos;
	}

	public void setListadoCronogramasConjuntoDatos(
			List<CronogramaConjuntoDatos> listadoCronogramasConjuntoDatos) {
		this.listadoCronogramasConjuntoDatos = listadoCronogramasConjuntoDatos;
	}

	public List<HojaCrd_ensayo> getListadoHojasCRD() {
		return listadoHojasCRD;
	}

	public void setListadoHojasCRD(List<HojaCrd_ensayo> listadoHojasCRD) {
		this.listadoHojasCRD = listadoHojasCRD;
	}

	public List<Cronograma_ensayo> getListadoCronogramas() {
		return listadoCronogramas;
	}

	public void setListadoCronogramas(List<Cronograma_ensayo> listadoCronogramas) {
		this.listadoCronogramas = listadoCronogramas;
	}

	public List<Sujeto_ensayo> getListadoSujetos() {
		return listadoSujetos;
	}

	public void setListadoSujetos(List<Sujeto_ensayo> listadoSujetos) {
		this.listadoSujetos = listadoSujetos;
	}

	public List<MomentoSeguimientoGeneral_ensayo> getListadoMomentos() {
		return listadoMomentos;
	}

	public void setListadoMomentos(
			List<MomentoSeguimientoGeneral_ensayo> listadoMomentos) {
		this.listadoMomentos = listadoMomentos;
	}

	public List<VariableDato_ensayo> getListadoVariableDato() {
		return listadoVariableDato;
	}

	public void setListadoVariableDato(
			List<VariableDato_ensayo> listadoVariableDato) {
		this.listadoVariableDato = listadoVariableDato;
	}

	public boolean isInicioConversacion() {
		return inicioConversacion;
	}

	public void setInicioConversacion(boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}

	public long getIdConjuntoDatos() {
		return idConjuntoDatos;
	}

	public void setIdConjuntoDatos(long idConjuntoDatos) {
		this.idConjuntoDatos = idConjuntoDatos;
	}

	public ConjuntoDatos_ensayo getConjuntoDatos() {
		return conjuntoDatos;
	}

	public void setConjuntoDatos(ConjuntoDatos_ensayo conjuntoDatos) {
		this.conjuntoDatos = conjuntoDatos;
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}

	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}

	public Date getFechaExportar() {
		return fechaExportar;
	}

	public void setFechaExportar(Date fechaExportar) {
		this.fechaExportar = fechaExportar;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getFrozenColCount() {
		return frozenColCount;
	}

	public void setFrozenColCount(int frozenColCount) {
		this.frozenColCount = frozenColCount;
	}

	public List<SujetoConjuntoDatos> getListadoSujetosConjuntoDatos() {
		return listadoSujetosConjuntoDatos;
	}

	public void setListadoSujetosConjuntoDatos(
			List<SujetoConjuntoDatos> listadoSujetosConjuntoDatos) {
		this.listadoSujetosConjuntoDatos = listadoSujetosConjuntoDatos;
	}

	public List<MomentoSeguimientoEspecifico_ensayo> getListadoMomentosEspesificos() {
		return listadoMomentosEspesificos;
	}

	public void setListadoMomentosEspesificos(
			List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEspesificos) {
		this.listadoMomentosEspesificos = listadoMomentosEspesificos;
	}

	public List<MomentoSeguimientoEspecifico_ensayo> getListadoMomentosEspesificosConGrupoVariable() {
		return listadoMomentosEspesificosConGrupoVariable;
	}

	public void setListadoMomentosEspesificosConGrupoVariable(
			List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEspesificosConGrupoVariable) {
		this.listadoMomentosEspesificosConGrupoVariable = listadoMomentosEspesificosConGrupoVariable;
	}

	public List<Integer> getListadoCantFilasMomentosEspesificosConGrupoVariable() {
		return listadoCantFilasMomentosEspesificosConGrupoVariable;
	}

	public void setListadoCantFilasMomentosEspesificosConGrupoVariable(
			List<Integer> listadoCantFilasMomentosEspesificosConGrupoVariable) {
		this.listadoCantFilasMomentosEspesificosConGrupoVariable = listadoCantFilasMomentosEspesificosConGrupoVariable;
	}

	public List<CrdEspecifico_ensayo> getListadoCrdEspesifico() {
		return listadoCrdEspesifico;
	}

	public void setListadoCrdEspesifico(
			List<CrdEspecifico_ensayo> listadoCrdEspesifico) {
		this.listadoCrdEspesifico = listadoCrdEspesifico;
	}

	public List<Variable_ensayo> getListadoVariables() {
		return listadoVariables;
	}

	public void setListadoVariables(List<Variable_ensayo> listadoVariables) {
		this.listadoVariables = listadoVariables;
	}

	public Hashtable<Long, List<GrupoVariables_ensayo>> getGruposVariablesPorCrdEspesifico() {
		return gruposVariablesPorCrdEspesifico;
	}

	public void setGruposVariablesPorCrdEspesifico(
			Hashtable<Long, List<GrupoVariables_ensayo>> gruposVariablesPorCrdEspesifico) {
		this.gruposVariablesPorCrdEspesifico = gruposVariablesPorCrdEspesifico;
	}

	public Hashtable<Long, Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>> getMomentosEspesificosPorSujeto() {
		return momentosEspesificosPorSujeto;
	}

	public void setMomentosEspesificosPorSujeto(
			Hashtable<Long, Hashtable<Long, List<MomentoSeguimientoEspecifico_ensayo>>> momentosEspesificosPorSujeto) {
		this.momentosEspesificosPorSujeto = momentosEspesificosPorSujeto;
	}

	
	
	public List<List<List<String>>> getTuplasFiltradoVertical() {
		return tuplasFiltradoVertical;
	}

	public void setTuplasFiltradoVertical(
			List<List<List<String>>> tuplasFiltradoVertical) {
		this.tuplasFiltradoVertical = tuplasFiltradoVertical;
	}

	public boolean isVertical() {
		return vertical;
	}

	public void setVertical(boolean vertical) {
		this.vertical = vertical;
	}
	

	public List<Integer> getIndicesTuplasVertical() {
		return indicesTuplasVertical;
	}

	public void setIndicesTuplasVertical(List<Integer> indicesTuplasVertical) {
		this.indicesTuplasVertical = indicesTuplasVertical;
	}

	public void filtrarVertical(){
		
		
		try{
			

			List<VariableDato_ensayo> variables = listadoVariableDato;
			
			
			
			Map<String,Map<String,Map<Integer,Map<Long,List<VariableDato_ensayo>>>>> tuplasSujeto = new TreeMap();
			for(VariableDato_ensayo variable: variables){
				System.out.println(variable.getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre());
				String cid = variable.getCrdEspecifico().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
				String idMomento = variable.getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre();
				long idHoja = variable.getCrdEspecifico().getHojaCrd().getId();
				int momentoEspecifico = variable.getCrdEspecifico().getMomentoSeguimientoEspecifico().getDia();

				if(!tuplasSujeto.containsKey(cid))
					tuplasSujeto.put(cid, new TreeMap());
					
				if(!tuplasSujeto.get(cid).containsKey(idMomento))
					tuplasSujeto.get(cid).put(idMomento,new TreeMap());
				
				if(!tuplasSujeto.get(cid).get(idMomento).containsKey(momentoEspecifico))
					tuplasSujeto.get(cid).get(idMomento).put(momentoEspecifico,new TreeMap());
				
				if(!tuplasSujeto.get(cid).get(idMomento).get(momentoEspecifico).containsKey(idHoja))
					tuplasSujeto.get(cid).get(idMomento).get(momentoEspecifico).put(idHoja,new ArrayList());
				
				tuplasSujeto.get(cid).get(idMomento).get(momentoEspecifico).get(idHoja).add(variable);
				
						
			}

			//tuplasFiltradoVertical= tuplasSujeto;
			Map<Long,String> variablesRepetidas = new TreeMap();
			for(String cid: tuplasSujeto.keySet()){
				System.out.println("idMomento: "+cid);
				List<String> sujeto = new ArrayList();
				sujeto.add("Codigo del Sujeto");
				sujeto.add(cid);

				for(String nombreMomento: tuplasSujeto.get(cid).keySet() ){
					String linea = " nombreMomento: "+ nombreMomento;
					List<String> momento = new ArrayList();
					momento.add("Nombre del Momento");
					momento.add(nombreMomento+"");
					

					
					for(Integer dia: tuplasSujeto.get(cid).get(nombreMomento).keySet() ){
						List<List<String>> tupla = new ArrayList();
						tupla.add(sujeto);
						tupla.add(momento);
						
						MomentoSeguimientoEspecifico_ensayo ms = tuplasSujeto.get(cid).get(nombreMomento).get(dia).get(tuplasSujeto.get(cid).get(nombreMomento).get(dia).keySet().toArray()[0]).get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico();
						tupla.addAll(obtenerDatosDelSujeto(ms.getSujeto()));
						tupla.addAll(obtenerDatosDelMomento(ms));
						
						List<String> momentoEspecifico = new ArrayList();
						momentoEspecifico.add("Momento Especifico");
						momentoEspecifico.add(dia+"");
						momentoEspecifico.add("25");
						tupla.add(momentoEspecifico);

						for(long idHoja: tuplasSujeto.get(cid).get(nombreMomento).get(dia).keySet()) {
							tupla.addAll(obtenerDatosDelCRDEspecifico(tuplasSujeto.get(cid).get(nombreMomento).get(dia).get(idHoja).get(0).getCrdEspecifico()));

							for(VariableDato_ensayo v: tuplasSujeto.get(cid).get(nombreMomento).get(dia).get(idHoja) ){
								linea+= " " + v.getVariable().getNombreVariable() + ": " +v.getValor();
								boolean existe = false;
								
								if(variablesRepetidas.containsKey(v.getVariable().getId()) && !variablesRepetidas.get(v.getVariable().getId()).contains(v.getValor()+",,,") ){
									variablesRepetidas.put(v.getVariable().getId(),variablesRepetidas.get(v.getVariable().getId())+v.getValor()+",,,"); 
								}
								else if((v.getVariable().getPresentacionFormulario().getNombre().equals("checkbox")||v.getVariable().getPresentacionFormulario().getNombre().equals("radio"))&&!variablesRepetidas.containsKey(v.getVariable().getId())){
									variablesRepetidas.put(v.getVariable().getId(),v.getValor()+",,,");
								}
								
								for(List<String> dato: tupla){

									if(dato.size()>2&&dato.get(2).equals(v.getVariable().getId()+"")){
										
										if(v.getValor()!=null){
							
											if(!dato.get(1).contains(",,,"))
												dato.set(1, dato.get(1)+",,,");
											if(!dato.get(1).contains(v.getValor()+",,,"))
												dato.set(1, dato.get(1)+v.getValor()+",,,");
										}
										existe=true;
										break;
									}
								}
								if(!existe){
									List<String> variable = new ArrayList();
									variable.add(v.getVariable().getNombreVariable());
									if(!variablesRepetidas.containsKey(v.getVariable().getId()))
										variable.add(v.getValor()!=null?v.getValor()+"":"");
									else
										variable.add(v.getValor()!=null?v.getValor()+",,,":"");
									variable.add(v.getVariable().getId()+"");
									tupla.add(variable);
								}
							}
							
						}
						System.out.println(linea);
						tuplasFiltradoVertical.add(tupla);
					}
				}	
			}

			for(Long variable: variablesRepetidas.keySet()){
				for(List<List<String>> tupla: tuplasFiltradoVertical){
					
					for(int i=2;i<tupla.size();i++){
						if(tupla.get(i).get(2).equals(variable+"")){
							
							if(!tupla.get(i).get(1).contains(",,,"))
								tupla.get(i).set(1,tupla.get(i).get(1)+",,,");
							
							List<List<String>> nuevasColumnas = new ArrayList();
							for(String nuevaColumna: variablesRepetidas.get(variable).split(",,,")){
								List<String> dato = new ArrayList();
								dato.add(tupla.get(i).get(0)+nuevaColumna);
								dato.add(tupla.get(i).get(1).contains(nuevaColumna+",,,")?"Si":"No");
								dato.add(variable+"");
								nuevasColumnas.add(dato);
							}
							
							tupla.remove(i);
							tupla.addAll(i,nuevasColumnas);
							i+=nuevasColumnas.size();
							break;
						}
					}			
				}
			}
			
			
			List<Integer> indicesTuplasVerticalAux = new ArrayList();
			for(int i=0;i<tuplasFiltradoVertical.size();i++){
				indicesTuplasVerticalAux.add(i);
			}
			
			indicesTuplasVertical = indicesTuplasVerticalAux;
			
			
		}catch(Exception e){
			System.out.println("Errooooooooooooooooooooooo\noooooooooooooooooooooooooooo\noooooooooooooooooooooooooooooooooooooooooo\nooooooooooooooooooooooooooooooooooooooooooo\nooooooooooooooooooooooooooooooooooooooooooooooooooooo");
			System.out.println(e);
		}
		
		
	}
	
	public List<List<String>> obtenerTupla(int i){
		if(vertical&&tuplasFiltradoVertical.size()>0){
			return tuplasFiltradoVertical.get(i);
		}
		else 
			return new ArrayList();
	}
	
	public List<List<String>> obtenerDatosDelMomento(MomentoSeguimientoEspecifico_ensayo momento){
		List<List<String>> datos = new ArrayList();
		
		if(conjuntoDatos.getMostrarFechaInicioMs()){
			List<String> fechaInicio = new ArrayList();
			fechaInicio.add("Fecha de Inicio del MS");
			fechaInicio.add(momento.getFechaInicio().toString());
			fechaInicio.add("1");
			datos.add(fechaInicio);
		}
		if(conjuntoDatos.getMostrarFechaFinMs()){
			List<String> fechaFin = new ArrayList();
			fechaFin.add("Fecha de Fin del MS");
			fechaFin.add(momento.getFechaFin().toString());
			fechaFin.add("2");
			datos.add(fechaFin);
		}
		if(conjuntoDatos.getMostrarEstadoMs()){
			List<String> estado = new ArrayList();
			estado.add("Estado del MS");
			estado.add(momento.getEstadoMomentoSeguimiento().getNombre());
			estado.add("3");
			datos.add(estado);
		}
		
		return datos;
	}
	
	
	public List<List<String>> obtenerDatosDelSujeto(Sujeto_ensayo sujeto){
		List<List<String>> datos = new ArrayList();

		if(conjuntoDatos.getMostrarEstadoSujeto()){
			List<String> estado = new ArrayList();
			estado.add("Estado Del Sujeto");
			estado.add(sujeto.getEstadoSujeto().getNombre());
			estado.add("4");
			datos.add(estado);
		}
		if(conjuntoDatos.getMostrarFechaInclusionSujeto()){
			List<String> fechaInclusion = new ArrayList();
			fechaInclusion.add("Fecha de Inclusion");
			fechaInclusion.add(sujeto.getFechaInclucion().toString());
			fechaInclusion.add("5");
			datos.add(fechaInclusion);
		}
		if(conjuntoDatos.getMostrarInicialesSujeto()){
			List<String> iniciales = new ArrayList();
			iniciales.add("Iniciales del Sujeto");
			iniciales.add(sujeto.getInicialesPaciente());
			iniciales.add("6");
			datos.add(iniciales);
		}
		if(conjuntoDatos.getMostrarNumeroInclusionSujeto()){
			List<String> numeroInclusionSujeto = new ArrayList();
			numeroInclusionSujeto.add("Numero de Inclusion del Sujeto");
			numeroInclusionSujeto.add(sujeto.getNumeroInclucion()+"");
			numeroInclusionSujeto.add("7");
			datos.add(numeroInclusionSujeto);
		}
		if(conjuntoDatos.getMostrarSitioClinicaSujeto()){
			List<String> sitioClinicaSujeto = new ArrayList();
			sitioClinicaSujeto.add("Sitio Clinico del Sujeto");
			sitioClinicaSujeto.add(sujeto.getInicialesCentro());
			sitioClinicaSujeto.add("8");
			datos.add(sitioClinicaSujeto);
		}
		
		return datos;
	}
	
	
	public List<List<String>> obtenerDatosDelCRDEspecifico(CrdEspecifico_ensayo crd){
		List<List<String>> datos = new ArrayList();
		
		if(conjuntoDatos.getMostrarEstadoCrdEspecifico() || conjuntoDatos.getMostrarEstadoMonitoreoCrdEspecifico()){
			List<String> nombre = new ArrayList();
			nombre.add("Nombre del CRD");
			nombre.add(crd.getHojaCrd().getNombreHoja());
			nombre.add("10");
			datos.add(nombre);
		}
		if(conjuntoDatos.getMostrarEstadoCrdEspecifico()){
			List<String> estado = new ArrayList();
			estado.add("Estado del CRD");
			estado.add(crd.getEstadoHojaCrd().getNombre());
			estado.add("11");
			datos.add(estado);
		}
		if(conjuntoDatos.getMostrarEstadoMonitoreoCrdEspecifico()){
			List<String> estado = new ArrayList();
			estado.add("Estado de Monitoreo CRD");
			estado.add(crd.getEstadoMonitoreo().getNombre());
			estado.add("12");
			datos.add(estado);
		}
		
		return datos;
	}
	
	
	
	
}
