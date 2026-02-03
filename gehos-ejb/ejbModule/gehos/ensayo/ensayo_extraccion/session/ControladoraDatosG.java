package gehos.ensayo.ensayo_extraccion.session;

import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.theme.Theme;

@Scope(ScopeType.CONVERSATION)
@Name("controladoraDatosG")
public class ControladoraDatosG {

	@In
	private EntityManager entityManager;

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;

	@In
	gehos.autenticacion.entity.Usuario user;
	@In
	SeguridadEstudio seguridadEstudio;
	
	@In("org.jboss.seam.theme.themeFactory")
	Theme theme;
	// Criterios de busqueda
	
	private String sujeto = "";
	private String usuario = "";
	private String msGeneral = "";
	private String crdSpe = "";
	private String variable = "";
	private Map parsPedro;
	private Boolean flag = false;
	private Boolean flag2 = true;
	private String pathExportedReport = "";
	private Date fechaIni;
	private Date fechaFin;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	private String gruSujetos;
	private String pathToReport;
	private List<ControladoraDatosGSource> listReport = new ArrayList<ControladoraDatosGSource>();
	private List<ControladoraDatosGSource> auxListReport = new ArrayList<ControladoraDatosGSource>();
	
	private String fileformatToExport;
	private List<String> filesFormatCombo;
	
	
	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");
	
	public String getSeleccione() {
		return seleccione;
	}



	private String consulta;
	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void cargar()
	{
		filesFormatCombo =  reportManager.fileFormatsToExport();
	}
	

	public Date getFechaIni() {
		return fechaIni;
	}

	public void setFechaIni(Date fechaIni) {
		this.fechaIni = fechaIni;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getMsGeneral() {
		return msGeneral;
	}

	public void setMsGeneral(String msGeneral) {
		this.msGeneral = msGeneral;
	}

	public String getSujeto() {
		return sujeto;
	}

	public void setSujeto(String sujeto) {
		this.sujeto = sujeto;
	}

	public String getCrdSpe() {
		return crdSpe;
	}

	public void setCrdSpe(String crdSpe) {
		this.crdSpe = crdSpe;
	}
	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}
	
	
	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
	}
	
	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}
	
	public String getGruSujetos() {
		return gruSujetos;
	}

	public void setGruSujetos(String gruSujetos) {
		this.gruSujetos = gruSujetos;
	}
	
	public String getPathToReport() {
		return pathToReport;
	}

	public void setPathToReport(String pathToReport) {
		this.pathToReport = pathToReport;
	}
	
	public List<ControladoraDatosGSource> getListReport() {
		return listReport;
	}

	public void setListReport(List<ControladoraDatosGSource> listReport) {
		this.listReport = listReport;
	}

	public List<ControladoraDatosGSource> getAuxListReport() {
		return auxListReport;
	}

	public void setAuxListReport(List<ControladoraDatosGSource> auxListReport) {
		this.auxListReport = auxListReport;
	}
	
	public String getConsulta() {
		return consulta;
	}

	public void setConsulta(String consulta) {
		this.consulta = consulta;
	}
	
	public String getNoResult() {
		return noResult;
	}

	public void setNoResult(String noResult) {
		this.noResult = noResult;
	}
	
	public String getFileformatToExport() {
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport) {
		this.fileformatToExport = fileformatToExport;
	}

	public List<String> getFilesFormatCombo() {
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}
	

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	

	// Listado de los usuarios por los que se puede buscar
	private List<String> usuariosG = new ArrayList<String>();

	// llenar usuarios que se puede buscar
	public List<String> llenarUsersG() 
	{
		usuariosG = entityManager
				.createQuery(
						"select distinct u.usuario.username from UsuarioEstudio_ensayo u where u.estudioEntidad.estudio.id =:idEst order by u.usuario.username ")
				.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
		usuariosG.add(0, seleccione);
		return usuariosG;
	}
 
	@SuppressWarnings("rawtypes")
	public List getUsuariosG() {
		return llenarUsersG();
	}

	// Listado de los msGenerales por los que se puede buscar
	private List<String> momentGeneG = new ArrayList<String>();

	// llenar msGeneral que se puede buscar
	public List<String> llenarMsGeneG() {
		this.crdSpe="";
		momentGeneG = entityManager
				.createQuery(
						"select distinct ms.nombre from MomentoSeguimientoGeneral_ensayo ms where ms.cronograma.grupoSujetos.estudio.id =:idEst order by ms.nombre ")
				.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
		momentGeneG.add(0, seleccione);
		return momentGeneG;
	}

	@SuppressWarnings("rawtypes")
	public List getMomentGeneG() {
		return llenarMsGeneG();
	}

	// Listado de los crd por los que se puede buscar
	private List<String> crdEspecificosG = new ArrayList<String>();

	// llenar crd que se puede buscar
	public List<String> llenarCRDEspG() {
		crdEspecificosG = entityManager
				.createQuery(
						"select distinct c.nombreHoja from HojaCrd_ensayo c where c.estudio.id =:idEst order by c.nombreHoja ")
				.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
		crdEspecificosG.add(0, seleccione);
		return crdEspecificosG;
	}

	@SuppressWarnings("rawtypes")
	public List getCRDEspG() {
		return llenarCRDEspG();
	}
	// Listado de las variables por los que se puede buscar
		private List<Seccion_ensayo> variableDisp = new ArrayList<Seccion_ensayo>();
		private List<Variable_ensayo> variableDisp1 = new ArrayList<Variable_ensayo>();
		private List<String> variableDispNomb = new ArrayList<String>();

		// llenar variables que se puede buscar
		public List<String> llenarVariables() {
			variableDispNomb = entityManager
					.createQuery(
							"select distinct v.nombreVariable from Variable_ensayo v where v.seccion.hojaCrd.estudio.id =:idEst and v.eliminado != True order by v.nombreVariable ")
					.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
			
			
			
			
			variableDispNomb.add(0, seleccione);
			return variableDispNomb;
		}

		@SuppressWarnings("rawtypes")
		public List getVariables() {
			return llenarVariables();
		}

	
	// Listado de los grupo sujetos por los que se puede buscar
			private List<String> gSujetosDisp = new ArrayList<String>();

			// llenar grupo sujetos que se puede buscar
			public List<String> llenarGSujetos() {
				gSujetosDisp = entityManager
						.createQuery(
								"select distinct cr.nombreGrupo from GrupoSujetos_ensayo cr where cr.estudio.id =:idEst order by cr.nombreGrupo ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
				gSujetosDisp.add(0, seleccione);
				return gSujetosDisp;
			}

			@SuppressWarnings("rawtypes")
			public List getGSujetos() {
				return llenarGSujetos();
			}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// lo que hace falta
			private List<Long> momentGeneEspecifico = new ArrayList<Long>();
			private List<String> crdEspecificosM = new ArrayList<String>();
			private List<Long> crdMomento = new ArrayList<Long>();
			private List<String> crdHoja = new ArrayList<String>();
			// Lista de momento seguimiento especifico
			public void llenarMsGeneEspecifico(){
				momentGeneEspecifico = new ArrayList<Long>();
				momentGeneEspecifico.clear();
				this.variable="";
				nombreVariable.clear();
				if(msGeneral != null && !msGeneral.isEmpty() ){
					momentGeneEspecifico = entityManager
							.createQuery(
									"select distinct ms.id from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.grupoSujetos.estudio.id =:idEst and ms.momentoSeguimientoGeneral.nombre =:MomentoSeguimiento")
							.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
							.setParameter("MomentoSeguimiento", msGeneral)
							.getResultList();
				}
				
		             
			}
			
			// Lista de crd especifico
			public void crdEspecifico(){
				llenarMsGeneEspecifico();
				crdMomento.clear();
				for(int x = 0; x < momentGeneEspecifico.size();x++){
					Long momento = null;
					try {
						momento = momentGeneEspecifico.get(x);;
					} catch (Exception e) {
						momento = null;
					}		
							
					if( momento != null ){
						
					     crdMomento.addAll(entityManager
									.createQuery(
											"select crd.id from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.hojaCrd.estudio.id =:idEst and crd.eliminado = false")
									.setParameter("idMomento", momento)
									.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())	
									.getResultList());
					     }
				}
			}
			// Lista hoja crd
			public void hojacrdName(){
				crdEspecifico();
				crdHoja.clear();
				if(crdMomento.size() >0)
					crdHoja.addAll(
							entityManager
							.createQuery(
										"select distinct hoja.nombreHoja from HojaCrd_ensayo hoja WHERE hoja.id IN (select crd.hojaCrd.id from CrdEspecifico_ensayo crd where crd.hojaCrd.estudio.id = :idEst and crd.eliminado = false"
										+ " and hoja.eliminado = false and crd.id in (:idcrd))")
								.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
								.setParameter("idcrd", crdMomento)
								.getResultList());
				crdHoja.add(0, seleccione);
			}
			
			public List<String> getCrdHoja() {
				hojacrdName();
				return crdHoja;
			}
			private Long hojaId;
			private List<Long> idCrdespecifico = new ArrayList<Long>();
			private List<Long> idVariable = new ArrayList<Long>();
			private List<String> nombreVariable = new ArrayList<String>();
			
			public List<Long> getIdVariable() {
				return idVariable;
			}


			public void setIdVariable(List<Long> idVariable) {
				this.idVariable = idVariable;
			}


			public Long getHojaId() {
				return hojaId;
			}


			public void setHojaId(Long hojaId) {
				this.hojaId = hojaId;
			}


			public List<Long> getIdCrdespecifico() {
				return idCrdespecifico;
			}


			public void setIdCrdespecifico(List<Long> idCrdespecifico) {
				this.idCrdespecifico = idCrdespecifico;
			}


			public void idHjaName(){
//				crdEspecifico();
					hojaId = (Long) entityManager
							.createQuery(
									"select distinct hoja.id from HojaCrd_ensayo hoja WHERE hoja.nombreHoja=:nombreHoja and hoja.estudio.id =:idEst and hoja.eliminado = false ")
					        .setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
					        .setParameter("nombreHoja", crdSpe)
					        .getSingleResult();
			}
			
			public void idCrdHoja(){
				idHjaName();
				idCrdespecifico.clear();
				this.crdSpe="";
				idCrdespecifico = entityManager
						.createQuery(
								"select distinct crd.id from CrdEspecifico_ensayo crd WHERE crd.hojaCrd.id=:idhoja and crd.hojaCrd.estudio.id =:idEst and crd.eliminado = false ")
								.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
								.setParameter("idhoja",hojaId )
								.getResultList();
			}
			
			public List<Long> idVariabled(){
				idCrdHoja();
				idVariable.clear();
				idVariable.addAll(entityManager
						.createQuery(
								"select distinct vd.variable.id from VariableDato_ensayo vd Where vd.crdEspecifico.id in (:IdCrdEsp) and vd.crdEspecifico.hojaCrd.estudio.id =:idEst ")
								.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
								.setParameter("IdCrdEsp", idCrdespecifico )
								.getResultList());
//				for(int x = 0; x < idCrdespecifico.size();x++){
//					Long crdespecifico = idCrdespecifico.get(x);
//					if( crdespecifico != null )
//						idVariable.addAll(entityManager
//								.createQuery(
//										"select vd.variable.id from VariableDato_ensayo vd Where vd.crdEspecifico.id=:IdCrdEsp and vd.crdEspecifico.hojaCrd.estudio.id =:idEst ")
//										.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
//										.setParameter("IdCrdEsp",crdespecifico )
//										.getResultList());
//				}
				return idVariable;
			}
			
			public List<String> idVariable(){
				nombreVariable.clear();
				if(crdSpe.equals("<Seleccione>")){
					crdSpe = "";
					}
				else if(crdSpe != null && !crdSpe.isEmpty() ){
					idVariabled();
					nombreVariable.clear();
					nombreVariable.addAll(entityManager
							.createQuery(
									"select distinct v.nombreVariable from Variable_ensayo v WHERE v.id in (:idVar)")
									.setParameter("idVar", idVariable )
									.getResultList());
					
				}
				nombreVariable.add(0, seleccione);
				return nombreVariable;
			}
			public List<String> getNombreVariable() {
				return nombreVariable;
			}


			public void setNombreVariable(List<String> nombreVariable) {
				this.nombreVariable = nombreVariable;
			}


			@SuppressWarnings("rawtypes")
			public List getVariablesNombre() {
				return idVariable();
			}
//			public long IdGrupodeSujetos(String gruSujetos){
//				long idGrupodeSujeto = 0;
//				idGrupodeSujeto = (Long) entityManager
//						.createQuery(
//								"select gs.id from GrupoSujetos_ensayo gs where gs.estudio.id =:idEst and gs.nombreGrupo =:gruSujetos order by gs.nombreGrupo ")
//						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
//				        .setParameter("gruSujetos",gruSujetos).getSingleResult();
//				return idGrupodeSujeto;
//			}
			
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
			// Aqui se implementa el requisito funcional 2:Visualizar trazas de Disenno
			public void Generar()
	{
		
		if(fechaIni!= null | fechaFin!= null | usuario!= "" | msGeneral!= "" | crdSpe!= "" | gruSujetos!= ""| variable!="")
		{
			

			if(usuario.equals("<Seleccione>"))
				usuario = "";
			
			if(msGeneral.equals("<Seleccione>"))
				msGeneral = "";
			if(crdSpe.equals("<Seleccione>"))
				crdSpe = "";
			
			if(gruSujetos.equals("<Seleccione>"))
				gruSujetos = "";
			
			if(variable.equals("<Seleccione>"))
				variable = "";
			
			
			pathToReport = "";
			listReport.clear();
			auxListReport.clear();
			
			ArrayList<Object[]> trazas = new ArrayList();
			consulta = "select l.fechaAuditoria, l.hora, l.nombreAplicacion,l.tablaAuditada, l.atributoModificado, l.oldValue, l.newValue, l.usuarioNombre,  l.estudioNombre, l.gsNombre, l.etapa, l.msgNombre, l.hojaCrd, l.seccion, l.variableNombre, l.regla "      
					+ "from LogEventosDisenno_ensayo l ";
			
			
			
					
			//fechaI 
			if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.fechaAuditoria >= :fechaIAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni)).getResultList();
			
			//fechaF
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" &&  crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin)).getResultList();
			
			//usuario
			if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.usuarioNombre = :userAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario)).getResultList();
			
			//msGeneral
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral)).getResultList();
			
			//hojaCRD
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("hojaAux", crdSpe)).getResultList();
				
			//grupoSujeto
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("gsujetoAux", gruSujetos)).getResultList();
			//variable 
			if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("variableAux", variable)).getResultList();	
			
			
			
			
			
			
			//fechaI variable
			if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.fechaAuditoria >= :fechaIAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("variableAux", variable)).getResultList();
			
			
			//fechaF variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.fechaAuditoria <= :fechaFAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("variableAux", variable)).getResultList();
			
			
			//user variable
			if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.usuarioNombre = :userAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("variableAux", variable)).getResultList();
			
			
			//msg  variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery( consulta + 
						"where l.msgNombre = :msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
							
			
			//hojaCrd variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto==  "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//gSujeto variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			
			
			
			//fechaI fechaF
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin)).getResultList();
			
			//fechaI user
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux  and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario)).getResultList();
			
			//fechaI MSG
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral)).getResultList();
			
						
			//fechaI CRD
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("hojaAux", crdSpe)).getResultList();
			
			//fechaF user
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario)).getResultList();
			
			//fechaF MSG
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral)).getResultList();
			
			//fechaF CRD
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe)).getResultList();
			
			//user MSG
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral)).getResultList();
			
			//user CRD
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("hojaAux", crdSpe)).getResultList();
			
			//MSG CRD 
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
			
			//fechaI GS
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("gsujetoAux", gruSujetos)).getResultList();
			
			//fechaF GS
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("gsujetoAux", gruSujetos)).getResultList();
			
			//user GS
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
				
			//MSG GS
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
			
			//CRD GS
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
			
			
				
			
			
			
			
			//fechaI fechaF variable 
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("variableAux", variable)).getResultList();
			
			//fechaI user variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("variableAux", variable)).getResultList();
			
			//fechaI MSG variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
			
				
			//fechaI CRD variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
			
			//fechaF user variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("variableAux", variable)).getResultList();
			
			//fechaF MSG variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
			
			
			//fechaF CRD variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
			
			//user MSG variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
			
			
			//user CRD variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
			
			
			//MSG CRD  variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
			
			
			//fechaI GS variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			//fechaF GS variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			//user GS variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
				
			//MSG GS variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			//sujeto GS variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto!= "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.sujeto=:sujetoAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("sujetoAux", sujeto).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			//CRD GS variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			
			
			//fechaI  fechaF  user
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario)).getResultList();
				
			
			//fechaI  fechaF  MSG
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral)).getResultList();
				
			
			//fechaI  fechaF  CRD
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaI  user MSG
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral)).getResultList();
				
			
			//fechaI  user CRD
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaI  MSG CRD
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaF user MSG
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral)).getResultList();
				
			
			//fechaF user CRD
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaF MSG CRD
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//user MSG CRD
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaI fechaF GS
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF MSG GS
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//user MSG GS
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI user GS
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI CRD GS
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//MSG CRD GS
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI MSG GS
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF CRD GS
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF user GS
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//user CRD GS
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			
			
			
			
			
			
			
			
			
			//fechaI  fechaF  user variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  fechaF  MSG variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  fechaF  CRD variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  user MSG variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
				
			
			
			//fechaI  user CRD variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  MSG CRD variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user MSG variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user CRD variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
		
			
			//fechaF MSG CRD variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//user MSG sujeto variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("variableAux", variable)).getResultList();
				
			
			//user MSG CRD variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
		
			
			//fechaI fechaF GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF MSG GS variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//user MSG GS variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			
			//fechaI user GS variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI CRD GS variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
			
			//MSG CRD GS variable
			else if(fechaIni== null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI MSG GS variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF CRD GS variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user GS variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//user CRD GS variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			
			//fechaI  fechaF  user  GS
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI  fechaF  MSG GS
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
           	//fechaI  fechaF  CRD GS
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI  user MSG GS
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI  user CRD GS
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI  MSG CRD GS
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAuxn and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF user MSG GS
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF user CRD GS
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF MSG CRD GS
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//user MSG CRD GS
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI fechaF user MSG
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral)).getResultList();
				
			
			//fechaI fechaF user CRD
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe)).getResultList();
				
			//fechaI fechaF MSG CRD
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaI user MSG CRD
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux  and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			//fechaF user MSG CRD
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
			
			
			
			//fechaI  fechaF  user  GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  fechaF  MSG GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
					
			
			//fechaI  fechaF  CRD GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  user MSG GS variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			
			//fechaI  user CRD GS variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI  MSG CRD GS variable
			else if(fechaIni!= null && fechaFin== null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.msgNombre=:msGAuxn and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user MSG GS variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user CRD GS variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			//fechaF MSG CRD GS variable
			else if(fechaIni== null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//user MSG CRD GS variable
			else if(fechaIni== null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF user MSG variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF user sujeto variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto!= "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.sujeto=:sujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("sujetoAux", sujeto).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF user CRD variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF MSG sujeto variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto!= "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF MSG CRD variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI user MSG CRD variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux  and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user MSG CRD variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe== "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
						
			
			
			
			
			//fechaI fechaF user MSG GS
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe== "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI fechaF user CRD GS
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI fechaF MSG CRD GS
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaI user MSG CRD GS
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//fechaF user MSG CRD GS
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
			
			//CRD fechaI fechaF user MSG
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && crdSpe!= "" && gruSujetos== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe)).getResultList();
				
				
			//fechaI fechaF user MSG GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF user CRD GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaI fechaF MSG CRD GS variable
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			
			//fechaI user MSG CRD GS variable
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user MSG CRD GS variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//CRD fechaI fechaF user MSG variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
						
						
			//CRD GS fechaI fechaF user MSG
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
		
							
			//fechaI fechaF user MSG sujeto CRD variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe!= "" && gruSujetos== "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("variableAux", variable)).getResultList();
				
			
			//fechaF user MSG sujeto CRD GS variable
			else if(fechaIni== null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//user MSG sujeto CRD GS fechaI variable	 
			else if(fechaIni!= null && fechaFin== null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//MSG sujeto CRD GS fechaI fechaF variable	
			else if(fechaIni!= null && fechaFin!= null && usuario== "" && msGeneral!= "" && sujeto!= "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//sujeto CRD GS fechaI fechaF user variable	
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral== "" && sujeto!= "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//CRD GS fechaI fechaF user MSG variable
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto== "" && crdSpe!= "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
			
			//GS fechaI fechaF user MSG sujeto variable	 
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe== "" && gruSujetos!= "" && variable!= "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.gsNombre=:gsujetoAux and l.variableNombre =:variableAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("gsujetoAux", gruSujetos).setParameter("variableAux", variable)).getResultList();
				
						
			//fechaI fechaF user msge  sujeto hojaCrd gSujeto
			else if(fechaIni!= null && fechaFin!= null && usuario!= "" && msGeneral!= "" && sujeto!= "" && crdSpe!= "" && gruSujetos!= "" && variable== "")
				trazas = (ArrayList<Object[]>) (entityManager.createQuery(consulta + 
						"where l.fechaAuditoria>=:fechaIAux and l.fechaAuditoria<=:fechaFAux and l.usuarioNombre=:userAux and l.msgNombre=:msGAux and l.sujeto=:sujetoAux and l.hojaCrd=:hojaAux and l.gsNombre=:gsujetoAux and l.idEst =:idEst order by l.fechaAuditoria ")
						.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
						.setParameter("fechaIAux", fechaIni).setParameter("fechaFAux", fechaFin).setParameter("userAux", usuario).setParameter("msGAux", msGeneral).setParameter("sujetoAux", sujeto).setParameter("hojaAux", crdSpe).setParameter("gsujetoAux", gruSujetos)).getResultList();
				
				
			
			
			
			
			
			
			if(trazas.size()>0)
			{
				
				
				for(int i=0; i<trazas.size();i++)
				{
					ControladoraDatosGSource temp = new ControladoraDatosGSource();					
					
					 String auxFecha = "";
					 String auxHora = "";
					 //String auxApp = "";
					 String auxTabla = "";
					 String auxAtrMod = "";
					 String auxVViejo = "";
					 String auxVNuevo = "";					 
					 String auxUsuario = "";
					 String auxEstudio = "";					 
					 String auxGS = "";
					 String auxEtapa = "";					 
					 String auxMSG = "";
					 String auxHCRD = "";
					 String auxSecc = "";
					 String auxNVariable = "";
					 String auxRegla = "";
					 
					 try{
						 SimpleDateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
						 					 
						  auxFecha = formato.format(trazas.get(i)[0]).toString();
						  auxHora = trazas.get(i)[1].toString();
						  //auxApp = trazas.get(i)[2].toString();
						  auxTabla = trazas.get(i)[3].toString();
						  auxAtrMod = trazas.get(i)[4].toString();
						  auxVViejo = trazas.get(i)[5].toString();
						  auxVNuevo = trazas.get(i)[6].toString();					 
						  auxUsuario = trazas.get(i)[7].toString();
						  auxEstudio = trazas.get(i)[8].toString();					 
						  auxGS = trazas.get(i)[9].toString();
						  auxEtapa = trazas.get(i)[10].toString();					 
						  auxMSG = trazas.get(i)[11].toString();
						  auxHCRD = trazas.get(i)[12].toString();
						  auxSecc = trazas.get(i)[13].toString();
						  auxNVariable = trazas.get(i)[14].toString();
						  auxRegla = trazas.get(i)[15].toString();
						 						
							}
							catch (Exception e){
							System.out.println(e);
							}
					 temp.setNo(String.valueOf(i+1));
					 temp.setFecha(auxFecha);
					
					if(auxHora.equals("0"))
						{
							auxHora="";
							temp.setHora(auxHora);
							
						}else temp.setHora(auxHora); 
					
					
					//if(auxApp.equals("0"))
					//{
					//	auxApp = "";
					//	temp.setAppAcc(auxApp);	
					//}
					//else temp.setAppAcc(auxApp);
					
					if(auxTabla.equals("0"))
					{
						auxTabla="";
						temp.setTabMod(auxTabla);
						
					}else temp.setTabMod(auxTabla);
					
					if(auxAtrMod.equals("0"))
					{
						auxAtrMod="";
						temp.setAtribtoMod(auxAtrMod);
						
					}else temp.setAtribtoMod(auxAtrMod);
					 
					if(auxVViejo.equals("0"))
					{
						auxVViejo="";
						temp.setvAntes(auxVViejo);
						
					}else temp.setvAntes(auxVViejo);
					
					if(auxVNuevo.equals("0"))
					{
						auxVNuevo="";
						temp.setvDespues(auxVNuevo);
						
					}else temp.setvDespues(auxVNuevo);
					
					if(auxUsuario.equals("0"))
					{
						auxUsuario = "";
						temp.setUsuario(auxUsuario);
						
					}else temp.setUsuario(auxUsuario);
					 
					if(auxEstudio.equals("0"))
					{
						auxEstudio = "";
						temp.setEstudio(auxEstudio);
						
					}else temp.setEstudio(auxEstudio);
					
					if(auxGS.equals("0"))
					{
						auxGS="";
						temp.setGruSujeto(auxGS);
						
					}else  temp.setGruSujeto(auxGS);					
					
					
					if(auxEtapa.equals("0"))
					{
						auxEtapa="";
						temp.setEtapa(auxEtapa);
						
					}else  temp.setEtapa(auxEtapa);
					 
					if(auxMSG.equals("0"))
					{
						auxMSG="";
						temp.setMsG(auxMSG);
						
					}else temp.setMsG(auxMSG);
					
					if(auxHCRD.equals("0"))
					{
						auxHCRD="";
						temp.setCrd(auxHCRD);
						
					}else temp.setCrd(auxHCRD);
					
					if(auxSecc.equals("0"))
					{
						auxSecc="";
						temp.setSeccion(auxSecc);
						
					}else temp.setSeccion(auxSecc);					
					
					if(auxNVariable.equals("0"))
					{
						auxNVariable="";
						temp.setnVariable(auxNVariable);
						
					}else temp.setnVariable(auxNVariable);
					
					if(auxRegla.equals("0"))
					{
						auxRegla="";
						temp.setRegla(auxRegla);
						
					}else temp.setRegla(auxRegla);					 
					 
					 
					 String rol = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
					 temp.setRolGenera(rol);
					 
					 listReport.add(temp);
					 auxListReport.add(temp);					 
				}
				 parsPedro = new HashMap();
				 parsPedro.put("num", SeamResourceBundle.getBundle().getString("num"));
				 parsPedro.put("fecha", SeamResourceBundle.getBundle().getString("fecha"));
				 parsPedro.put("hora", SeamResourceBundle.getBundle().getString("hora"));
				 //parsPedro.put("app", SeamResourceBundle.getBundle().getString("app"));
				 parsPedro.put("tablaMod", SeamResourceBundle.getBundle().getString("tablaMod"));
				 parsPedro.put("atributoMod", SeamResourceBundle.getBundle().getString("atributoMod"));
				 parsPedro.put("valorA", SeamResourceBundle.getBundle().getString("valorA"));
				 parsPedro.put("valorD", SeamResourceBundle.getBundle().getString("valorD"));					 
				 parsPedro.put("usuario", SeamResourceBundle.getBundle().getString("usuario"));
				 parsPedro.put("estudio", SeamResourceBundle.getBundle().getString("estudio"));
				 parsPedro.put("grupoSuj", SeamResourceBundle.getBundle().getString("grupoSuj"));
				 parsPedro.put("etapa", SeamResourceBundle.getBundle().getString("etapa"));				 
				 parsPedro.put("msGeneral", SeamResourceBundle.getBundle().getString("msGeneral"));				 
				 parsPedro.put("hojaCRD", SeamResourceBundle.getBundle().getString("hojaCRD"));
				 parsPedro.put("seccion", SeamResourceBundle.getBundle().getString("seccion"));
				 parsPedro.put("nVar", SeamResourceBundle.getBundle().getString("nVar"));
				 parsPedro.put("regla", SeamResourceBundle.getBundle().getString("regla"));
				 parsPedro.put("trazasSis", SeamResourceBundle.getBundle().getString("trazasSis"));
				 
				 
				 pathToReport = reportManager.ExportReport("reportTrazasDiseno", parsPedro, listReport, FileType.HTML_FILE);
				 
				 
				 flag=true;
				 flag2=false; 
			}
			else
			{
				noResult = SeamResourceBundle.getBundle().getString(
						"noResult2");
			    flag = false;
			    flag2 = true;
		    }
			
		}
		else{
			 noResult = SeamResourceBundle.getBundle().getString(
						"noResult3");
			 flag2 = true;
			 flag = false;
		 }
		
	}
	public int sizeMaxString(List<String> listReturn){
        if(listReturn.size() == 0)
            return 150;
        int max = listReturn.get(0).length();
        for(int i = 1; i < listReturn.size(); i++){
            if(listReturn.get(i).length() > max)
                max = listReturn.get(i).length();
        }
        if ((max*6) > 150)
            return max*7;
        return 150;
    }
	
	// Se implementa el requisito funcional 4: Permite exportar las trazas
	// generadas en el sistema al formato WORD, EXCEL y PDF.
	public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportTrazasDiseno", parsPedro, listReport,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportTrazasDiseno", parsPedro, listReport,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportTrazasDiseno", parsPedro, listReport,FileType.EXCEL_FILE);
		}
	
	}


	

	

	
	
}