package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;
//CU 18 Ver cronograma especifico
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import gehos.bitacora.session.traces.Bitacora;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.Etapa_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import javax.ejb.Remove;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;






import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
@SuppressWarnings("unchecked")
@Name("verCronogramaEspecifico")
@Scope(ScopeType.CONVERSATION)
public class VerCronogramaEspecifico {

	protected @In EntityManager entityManager;
	protected @In FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	
	
	private Sujeto_ensayo sujeto;
	private Long idSujeto;
	List<MomentoSeguimientoEspecifico_ensayo> momentos;
	private List<Integer> tamannoEtapas = new ArrayList<Integer>(3);
	
	//Cronnogrma Especifico by Eiler
	private List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral;
	private Date fechainicio;
	
	
	@In ReportManager reportManager;
	
	private Map reporteCronogramaEspecifico;
	private String nombreReport;
	private String pathExportedReport = "";
	
	private Boolean flag=false;
	
	private String fileformatToExport;
	private List<String> filesFormatCombo;


	List<CronogramaEspecificoExportar> cronograma=new ArrayList<CronogramaEspecificoExportar>();
	
	
	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	public void loadData(){
		this.sujeto = (Sujeto_ensayo)entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:id").setParameter("id", idSujeto).getSingleResult();
		filesFormatCombo =  reportManager.fileFormatsToExport();
		this.momentosSujeto();
	}
	
	
	
	// Listado de momentos de seguimiento especificos by Eiler
		public List<MomentoSeguimientoEspecifico_ensayo> momentosSujeto() {

			momentos = new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
			try {
				momentos = entityManager
						.createQuery(
								"select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.eliminado = FALSE and mse.sujeto.id=:id order by  mse.dia, mse.fechaInicio asc")
						.setParameter("id", sujeto.getId()).getResultList();

				int contEva = 0;
				int contTra = 0;
				int contSeg = 0;

				Cronograma_ensayo crono = (Cronograma_ensayo) entityManager
						.createQuery(
								"select e from Cronograma_ensayo e "
										+ "where e.grupoSujetos.id = :idG ")
						.setParameter("idG", this.sujeto.getGrupoSujetos().getId())
						.getSingleResult();

				List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
						.createQuery(
								"select e from Etapa_ensayo e "
										+ "where e.cronograma.id = :idCrono "
										+ "order by e.id")
						.setParameter("idCrono", crono.getId()).getResultList();

				List<String> diaEva = new ArrayList<String>();
				List<String> diaTra = new ArrayList<String>();
				List<String> diaSeg = new ArrayList<String>();

				List<String> nombres = new ArrayList<String>();
				for (int i = 0; i < momentos.size(); i++) {
					int cont = 0;
					for (int j = 0; j < nombres.size(); j++) {
						if (momentos.get(i).getMomentoSeguimientoGeneral()
								.getNombre().equals(nombres.get(j))) {
							cont++;
						}
					}

					if (!momentos.get(i).getMomentoSeguimientoGeneral()
							.getProgramado()) {
						
						if (momentos.get(i).getDia()!=null && momentos.get(i).getDia() >= 1 && momentos.get(i).getDia() <= etapas.get(1).getFinEtapa()){
							contTra++;
						}else if (momentos.get(i).getDia()!=null && momentos.get(i).getDia() >= etapas.get(2).getInicioEtapa()
								&& momentos.get(i).getDia() <= etapas.get(2).getFinEtapa()
								){
							contSeg++;
						}else{
							contEva++;
						}
					} else {
						if (cont == 0) {

							// me quede no llena bienk
							diaEva = diasEtapa("evaluacion", momentos.get(i)
									.getMomentoSeguimientoGeneral().getDia());
							diaTra = diasEtapa("tratamiento", momentos.get(i)
									.getMomentoSeguimientoGeneral().getDia());
							diaSeg = diasEtapa("seguimiento", momentos.get(i)
									.getMomentoSeguimientoGeneral().getDia());

							if (diaEva.size() > 0) {
								contEva += diaEva.size();
							}
							if (diaTra.size() > 0) {
								contTra += diaTra.size();
							}
							if (diaSeg.size() > 0) {
								contSeg += diaSeg.size();
							}
							nombres.add(momentos.get(i)
									.getMomentoSeguimientoGeneral().getNombre());
						}
					}

				}

				tamannoEtapas.add(0, contEva);
				tamannoEtapas.add(1, contTra);
				tamannoEtapas.add(2, contSeg);

			} catch (Exception e) {
				// TODO: handle exception
			}
			return momentos;
		}

		
		

		// Listado de nombres de los momentos de Seguimiento by Eiler
		public List<String> nombresMomentos() {
			List<MomentoSeguimientoEspecifico_ensayo> momentos = this.momentos;
			List<String> nombres = new ArrayList<String>();
			for (int i = 0; i < momentos.size(); i++) {
				int cont = 0;
				for (int j = 0; j < nombres.size(); j++) {
					if (momentos.get(i).getMomentoSeguimientoGeneral().getNombre()
							.equals(nombres.get(j))) {
						cont++;
					}
				}
				if (cont == 0) {
					nombres.add(momentos.get(i).getMomentoSeguimientoGeneral()
							.getNombre());
				}

			}
			return nombres;
		}
	
	//Compruebo si el nombre del momentopertenece al momento de seguimiento by Eiler
	public Boolean PerteneceMomento(MomentoSeguimientoEspecifico_ensayo momento, String nombre,Date fecha)
	{
		String nombreMomento=momento.getMomentoSeguimientoGeneral().getNombre();
		Date fechaMomento=momento.getFechaInicio();
		if ((nombre.equals(nombreMomento))&&(fecha.equals(fechaMomento)))
		{
			return true;
		}
		return false;
	}
	
	public void exportReportToFileFormat()
	{
		List<MomentoSeguimientoEspecifico_ensayo> momentos=new ArrayList<MomentoSeguimientoEspecifico_ensayo>();
		List<MomentosEspecificos> momentosExportar=new ArrayList<MomentosEspecificos>();
		
		momentos.addAll(momentosSujeto());
		Long grupoId= sujeto.getGrupoSujetos().getId();
		String grupo= grupoId.toString();

		String estadoInclucion=sujeto.getEstadoInclusion().getNombre();
		for(int i=0;i<momentos.size();i++)
		{
			MomentosEspecificos momen=new MomentosEspecificos(momentos.get(i).getMomentoSeguimientoGeneral().getNombre(), momentos.get(i).getFechaInicio().toString(), momentos.get(i).getMomentoSeguimientoGeneral().getEtapa());
			momentosExportar.add(momen);
		}
		CronogramaEspecificoExportar crono = new CronogramaEspecificoExportar(sujeto.getInicialesPaciente(), sujeto.getInicialesCentro(), sujeto.getFechaInclucion().toString(), sujeto.getNumeroInclucion().toString(), sujeto.getCodigoPaciente().toString(), sujeto.getEstadoInclusion().getNombre(), sujeto.getGrupoSujetos().getNombreGrupo(), momentosExportar);
		cronograma.add(crono);
		
		reporteCronogramaEspecifico=new HashMap();

		reporteCronogramaEspecifico.put("P_TITULO", SeamResourceBundle.getBundle().getString("P_TITULO"));
		reporteCronogramaEspecifico.put("detallesSujeto", SeamResourceBundle.getBundle().getString("detallesSujeto"));
		reporteCronogramaEspecifico.put("iniciales", SeamResourceBundle.getBundle().getString("iniciales"));
		reporteCronogramaEspecifico.put("inicialesCentro", SeamResourceBundle.getBundle().getString("inicialesCentro"));
		reporteCronogramaEspecifico.put("fechaInclucion", SeamResourceBundle.getBundle().getString("fechaInclucion"));
		reporteCronogramaEspecifico.put("numeroInclucion", SeamResourceBundle.getBundle().getString("numeroInclucion"));
		reporteCronogramaEspecifico.put("codigoSujeto", SeamResourceBundle.getBundle().getString("codigoSuj"));
		reporteCronogramaEspecifico.put("estadoInclucion", SeamResourceBundle.getBundle().getString("estadoInclucion"));
		reporteCronogramaEspecifico.put("grupo", SeamResourceBundle.getBundle().getString("grupo"));
		reporteCronogramaEspecifico.put("listadoMS", SeamResourceBundle.getBundle().getString("listadoMS"));
		reporteCronogramaEspecifico.put("nombreMomento", SeamResourceBundle.getBundle().getString("nombreMomento"));
		reporteCronogramaEspecifico.put("fechaMomento", SeamResourceBundle.getBundle().getString("fechaMomento"));
		reporteCronogramaEspecifico.put("etapaMomento", SeamResourceBundle.getBundle().getString("etapaMomento"));
		
		//nombreReport=reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma, FileType.PDF_FILE);
		
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.PDF_FILE);
			cronograma=new ArrayList<CronogramaEspecificoExportar>();
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.RTF_FILE);
			cronograma=new ArrayList<CronogramaEspecificoExportar>();
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.EXCEL_FILE);
			cronograma=new ArrayList<CronogramaEspecificoExportar>();
		}
		flag=true;
		
	}
	
	
	
			
			
			// Metodo para construir la cadena con los dias de visita.
			@SuppressWarnings("unchecked")
			public List<String> diasEtapa(String etapa, String dias) {
				List<String> listaDias=new ArrayList<String>();
				List<String> listaDiasEtapa = new ArrayList<String>();
				if(dias != null){
					listaDias=listaDias(dias);
					
					Cronograma_ensayo crono = (Cronograma_ensayo) entityManager
							.createQuery(
									"select e from Cronograma_ensayo e "
											+ "where e.grupoSujetos.id = :idG ")
							.setParameter("idG", this.sujeto.getGrupoSujetos().getId()).getSingleResult();
					List<Etapa_ensayo> etapas = (List<Etapa_ensayo>) entityManager
							.createQuery(
									"select e from Etapa_ensayo e "
											+ "where e.cronograma.id = :idCrono "
											+ "order by e.id")
							.setParameter("idCrono", crono.getId()).getResultList();
					for (int i = 0; i < listaDias.size(); i++) {
						Integer dia = Integer.parseInt(listaDias.get(i));
						if (dia == 0 && etapa.equals("evaluacion")) {
							listaDiasEtapa.add(dia.toString());
							return listaDiasEtapa;
							/**
							 * @author Tania
							 */
						} else if (dia >= 1 && dia <= etapas.get(1).getFinEtapa()
								&& etapa.equals("tratamiento"))
							listaDiasEtapa.add(dia.toString());
						else if (dia >= etapas.get(2).getInicioEtapa()
								&& dia <= etapas.get(2).getFinEtapa()
								&& etapa.equals("seguimiento"))
							listaDiasEtapa.add(dia.toString());
					}
				}
				
				return listaDiasEtapa;
			}
			
			public List<String> listaDias(String dias){
				
				List<String> listaDias= new ArrayList<String>();
				String[] listaDiasSelecc;
				String[] listaUltDiaSelecc;
				String y = "y";
				if (dias.contains(",")) {
					listaDiasSelecc = dias.split(", ");
					listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
							.split(" " + y + " ");
					for (int i = 0; i < listaDiasSelecc.length - 1; i++) {
						listaDias.add(listaDiasSelecc[i]);
						
					}
					listaDias.add(listaUltDiaSelecc[0]);
					listaDias.add(listaUltDiaSelecc[1]);
					

				} else if (dias.contains(y)) {
					listaUltDiaSelecc = dias.split(" " + y + " ");
					listaDias.add(listaUltDiaSelecc[0]);
					listaDias.add(listaUltDiaSelecc[1]);			

				} else {
					listaDias.add(dias);			

				}
				return listaDias;
			}
			
	
	
	/*public void exportReportToFileFormat(){
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.PDF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.RTF_FILE);
		} 
		else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("cronogramaEspecifico", reporteCronogramaEspecifico, cronograma,FileType.EXCEL_FILE);
		}
	
	}*/
	
	
	/*
	 * Devuelve la url del icono para estado pasado por parametro.
	 * @parametro: estado, estado del cual vamos a obtener el icono.
	 */
		public String estadoIconMomento(EstadoMomentoSeguimiento_ensayo estado) {
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String path = "/resources/modEnsayo/estadosIcon/"
					+ estado.getClass().getSimpleName().split("_")[0] + "/"
					+ estado.getCodigo() + ".png";

			String rootpath = context.getRealPath(path);
			java.io.File dir = new java.io.File(rootpath);
			if (dir.exists())
				return path;
			else
				return "/resources/modEnsayo/estados/" + "generic.png";

		}
	
	
	
	
	public void salir(){		
		
	}
	
	
	public void destroy(){}
	
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}
	public Long getIdSujeto() {
		return idSujeto;
	}
	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}
	public Date getFechainicio() {
		return fechainicio;
	}

	public void setFechainicio(Date fechainicio) {
		this.fechainicio = fechainicio;
	}



	public Boolean getFlag() {
		return flag;
	}



	public void setFlag(Boolean flag) {
		this.flag = flag;
	}



	public Map getReporteCronogramaEspecifico() {
		return reporteCronogramaEspecifico;
	}



	public void setReporteCronogramaEspecifico(Map reporteCronogramaEspecifico) {
		this.reporteCronogramaEspecifico = reporteCronogramaEspecifico;
	}



	public String getNombreReport() {
		return nombreReport;
	}



	public void setNombreReport(String nombreReport) {
		this.nombreReport = nombreReport;
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
	public List<Integer> getTamannoEtapas() {
		return tamannoEtapas;
	}

	public void setTamannoEtapas(List<Integer> tamannoEtapas) {
		this.tamannoEtapas = tamannoEtapas;
	}



	public List<MomentoSeguimientoEspecifico_ensayo> getMomentos() {
		return momentos;
	}



	public void setMomentos(List<MomentoSeguimientoEspecifico_ensayo> momentos) {
		this.momentos = momentos;
	}
	
	
}
