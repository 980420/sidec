//CU 26 Visualizar notas de sitio por sujeto
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarMS.WrapperMomento;
import gehos.ensayo.ensayo_conduccion.session.custom.NotaCustomList;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.reporteListadoNotasMonitoreo.NotasMonitoreo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoNota_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("listarNotasMonitoreo")
@Scope(ScopeType.CONVERSATION)
public class ListarNotasMonitoreo extends NotaCustomList {
	
	private static final String EJBQL = "select nota from Nota_ensayo nota "
			+ "where nota.eliminado = false and nota.notaPadre = null and nota.notaSitio = false and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = #{listarNotasMonitoreo.idSujeto}";

	private static final String[] RESTRICTIONS = {
			"lower(nota.estadoNota.nombre) like concat(lower(#{listarNotasMonitoreo.estadoN.trim()}),'%')",
			"#{listarNotasMonitoreo.idNota} <> nota.id" };

	
	@In(create = true)
	WrapperMomento wrapperMomento;
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;
	
	@In ReportManager reportManager; 

	private boolean existResultados = true;
	
	private Integer style;
	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private int pagina;
	
	private List<String> filesFormatCombo; 
	private String fileformatToExport; 
	private String pathExportedReport; 
	private Map pars; 
	private List<Nota_ensayo> listaNotaMonitoreo;
	private List<NotasMonitoreo> listaNotaMonitoreoExportar;
	private String estadoN = "";
	private Nota_ensayo notaEliminar = new Nota_ensayo();
	private Long idSujeto, idGrupo, idNota, ideliminarNota;
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	public ListarNotasMonitoreo() {
    	//result.add("CSV");
		this.filesFormatCombo =new ArrayList<String>(); 
    	this.filesFormatCombo.add("PDF");
    	this.filesFormatCombo.add("WORD");
    	//result.add("TXT");
    	this.filesFormatCombo.add("EXCEL");
    	//result.add("XML");  
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("nota.id desc");
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listEstadoN() {
		List<EstadoNota_ensayo> listarEstados = (List<EstadoNota_ensayo>) entityManager
				.createQuery("select e from EstadoNota_ensayo e order by e.codigo ASC")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		nombreEst.add("<Seleccione>");
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}
	
	public Entidad_ensayo EntidadActiva(){
		return entityManager.find(Entidad_ensayo.class , this.activeModule.getActiveModule().getEntidad().getId());
	}
	
	public VariableDato_ensayo ObtenerValorVariable(Nota_ensayo notaSitio){
		VariableDato_ensayo variableDato = new VariableDato_ensayo();
		try {
			variableDato = (VariableDato_ensayo)entityManager
					.createQuery(
							"select variableD from VariableDato_ensayo variableD where variableD.variable=:Variable and variableD.crdEspecifico=:Hoja")
					.setParameter("Variable", notaSitio.getVariable())
					.setParameter("Hoja", notaSitio.getCrdEspecifico()).getSingleResult();
			
		} catch (Exception e) {
			return null;
		}
		
		
		return variableDato;
	}
	
	@SuppressWarnings("unchecked")
	// Exportar notas de monitoreo
	public void exportarNotaMonitoreo(long idNotaMonitoreo){
		this.listaNotaMonitoreo=new ArrayList<Nota_ensayo>();
		this.listaNotaMonitoreoExportar=new ArrayList<NotasMonitoreo>();
		this.pars = new HashMap(); 
		this.filesFormatCombo = this.reportManager.fileFormatsToExport(); 
		for(Nota_ensayo nota: this.getResultList()){
			if(nota.getId()==idNotaMonitoreo){
				this.listaNotaMonitoreo.add(nota);
				break;
			}
		}
		
		if (!this.listaNotaMonitoreo.isEmpty()){
			this.pars.put("nombreEstudio", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getNombre());
			this.pars.put("nombreEntidad", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getEntidad().getNombre());
			//los de arriba son valores
			
			//estos de abajo son los label
			this.pars.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
			this.pars.put("fechaCreacionNota", SeamResourceBundle.getBundle().getString("fechaCreacionNota"));
			this.pars.put("fechaActualizacionNota", SeamResourceBundle.getBundle().getString("fechaActualizacionNota"));
			this.pars.put("fechaMS", SeamResourceBundle.getBundle().getString("fechaMS"));
			this.pars.put("estadoMS", SeamResourceBundle.getBundle().getString("estadoMS"));
			this.pars.put("nombreCRD", SeamResourceBundle.getBundle().getString("nombreCRD"));
			this.pars.put("nombreVariable", SeamResourceBundle.getBundle().getString("nombreVariable"));
			this.pars.put("valorVariable", SeamResourceBundle.getBundle().getString("valorVariable"));
			this.pars.put("descripcionNota", SeamResourceBundle.getBundle().getString("descripcionNota"));
			this.pars.put("detallesNota", SeamResourceBundle.getBundle().getString("detallesNota"));
			this.pars.put("estadoNota", SeamResourceBundle.getBundle().getString("estadoNota"));
			this.pars.put("numeroNotas", SeamResourceBundle.getBundle().getString("numeroNotas"));
			
			String fechaActualizacion=""; 
			String fechaCreacion=""; 
			
			String codigoSujeto=this.listaNotaMonitoreo.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
			if(this.listaNotaMonitoreo.get(0).getFechaCreacion()!=null){ 
				fechaCreacion=formatter.format(this.listaNotaMonitoreo.get(0).getFechaCreacion());
			}
			if(this.listaNotaMonitoreo.get(0).getFechaActualizacion()!=null){ 
				fechaActualizacion=formatter.format(this.listaNotaMonitoreo.get(0).getFechaActualizacion());
			}
			String fechaInicioMs=formatter.format(this.listaNotaMonitoreo.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getFechaInicio());
			
			String msNombre=this.listaNotaMonitoreo.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre();
			String msEstado=this.listaNotaMonitoreo.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getEstadoMomentoSeguimiento().getNombre();
			
			String ms_estado=msNombre+" "+"("+msEstado+")";
			
			String crd=this.listaNotaMonitoreo.get(0).getCrdEspecifico().getHojaCrd().getNombreHoja();
			
			String nombreVariable=this.listaNotaMonitoreo.get(0).getVariable().getNombreVariable();
			
			String valorVariable="";
			if(this.listaNotaMonitoreo.get(0).getVariableDato()==null){ 
				if(ObtenerValorVariable(this.listaNotaMonitoreo.get(0)) != null){ 
					valorVariable=ObtenerValorVariable(this.listaNotaMonitoreo.get(0)).getValor();
				}
			}else{ 
				valorVariable= this.listaNotaMonitoreo.get(0).getVariableDato().getValor();
				valorVariable= valorVariable == null ? "" : valorVariable;
			}
			
		
				
			
			
			
			String notaDescripcion=this.listaNotaMonitoreo.get(0).getDescripcion();
			
			String estadoNota=this.listaNotaMonitoreo.get(0).getEstadoNota().getNombre();
			
			String detallesNota=this.listaNotaMonitoreo.get(0).getDetallesNota();
			
			int numeroNotas=this.DevolverNotaMonitoreoAsociadas(this.listaNotaMonitoreo.get(0)).size();
			
			NotasMonitoreo notaMonitoreoSuj = new NotasMonitoreo(codigoSujeto, fechaCreacion, fechaActualizacion, fechaInicioMs, ms_estado, crd, nombreVariable, valorVariable, notaDescripcion, detallesNota, estadoNota, numeroNotas, "1");
			this.listaNotaMonitoreoExportar.add(notaMonitoreoSuj);
		}
		
		
		
	}
	
	public void exportReportToFileFormat(){ 
		this.pathExportedReport = ""; 
		if (this.fileformatToExport.equals(this.filesFormatCombo.get(0))) 
			this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasConduccion", this.pars, this.listaNotaMonitoreoExportar, FileType.PDF_FILE); 
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(1))) 
			this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasConduccion", this.pars, this.listaNotaMonitoreoExportar, FileType.RTF_FILE); 
		else if (this.fileformatToExport.equals(this.filesFormatCombo.get(2))) 
			this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasConduccion", this.pars, this.listaNotaMonitoreoExportar, FileType.EXCEL_FILE); 
	} 
	
	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> DevolverNotaMonitoreoAsociadas(Nota_ensayo Nota){
		List<Nota_ensayo> listaHijas = new ArrayList<Nota_ensayo>();
		listaHijas = (List<Nota_ensayo>) entityManager
					.createQuery(
							"select nota from Nota_ensayo nota where nota.notaPadre=:notaPadre and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE'")
					.setParameter("notaPadre", Nota).getResultList();
		
		/*List<Nota_ensayo> listaDevolver = new ArrayList<Nota_ensayo>();
		for (int i = 0; i < listaHijas.size(); i++) {
			listaDevolver.add(listaHijas.get(i));
			List<Nota_ensayo> listaRecursiva = DevolverNotaMonitoreoAsociadas(listaHijas.get(i));
			listaDevolver.addAll(listaRecursiva);
		}*/
		
		return listaHijas;
	}
	
	public void buscar() {
		this.refresh();
		this.setFirstResult(0);
		this.getResultList();
		this.existResultados = (this.getResultCount() != 0);
		setOrder("nota.id desc");
	}
	
	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		//this.filesFormatCombo = this.reportManager.fileFormatsToExport(); 
		this.idSujeto = idSujeto;
	}
	
	public void SeleccionarInstanciaNota(Nota_ensayo notaE) {
		this.setNotaEliminar(notaE);
	}
	
	// CU 34 Eliminar nota de monitoreo
		@SuppressWarnings("unchecked")
		public void EliminarInstanciaNota() {
			
			this.notaEliminar.setEliminado(true);
			entityManager.persist(notaEliminar);
			entityManager.flush();
			
			List<Nota_ensayo> lista = new ArrayList<Nota_ensayo>();
			
			lista = (List<Nota_ensayo>) entityManager
					.createQuery(
							"select nota from Nota_ensayo nota where nota.crdEspecifico=:Hoj and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE' and nota.notaPadre = null")
					.setParameter("Hoj", this.notaEliminar.getCrdEspecifico()).getResultList();
		
		long idEstadoSeguimientoInic = 1;
		long idEstadoSeguimientoNoInic = 2;
		EstadoMonitoreo_ensayo estadoMonitoreo = entityManager.find(
				EstadoMonitoreo_ensayo.class, idEstadoSeguimientoInic);
		EstadoMonitoreo_ensayo estadoMonitoreoNoInic = entityManager.find(
				EstadoMonitoreo_ensayo.class, idEstadoSeguimientoNoInic);
		if(lista.size() != 0){
			this.notaEliminar.getCrdEspecifico().setEstadoMonitoreo(estadoMonitoreo);
			entityManager.persist(this.notaEliminar.getCrdEspecifico());
			wrapperMomento.cambiarEstadoMonitoreoAIniciado(this.notaEliminar.getCrdEspecifico().getMomentoSeguimientoEspecifico());	
		}else{
			this.notaEliminar.getCrdEspecifico().setEstadoMonitoreo(estadoMonitoreoNoInic);
			entityManager.persist(this.notaEliminar.getCrdEspecifico());
			wrapperMomento.cambiarEstadoMonitoreoANoIniciado(this.notaEliminar.getCrdEspecifico().getMomentoSeguimientoEspecifico());			
		}
		entityManager.flush();
				
		}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua",user.getId())
				.getSingleResult();
		
		return rol;
	}

	public String getDisplayBA() {
		return displayBA;
	}

	public void setDisplayBA(String displayBA) {
		this.displayBA = displayBA;
	}

	public String getDisplayBN() {
		return displayBN;
	}

	public void setDisplayBN(String displayBN) {
		this.displayBN = displayBN;
	}

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
	}
	
	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Long getIdNota() {
		return idNota;
	}

	public void setIdNota(Long idNota) {
		this.idNota = idNota;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	

	public Long getIdeliminarNota() {
		return ideliminarNota;
	}

	public void setIdeliminarNota(Long ideliminarNota) {
		this.ideliminarNota = ideliminarNota;
	}

	public Nota_ensayo getNotaEliminar() {
		return notaEliminar;
	}

	public void setNotaEliminar(Nota_ensayo notaEliminar) {
		this.notaEliminar = notaEliminar;
	}

	public String getEstadoN() {
		return estadoN;
	}

	public void setEstadoN(String estadoN) {
		this.estadoN = estadoN;
		if(this.estadoN.equals("<Seleccione>")){
			this.estadoN = "";
		}
	}
	

	public List<String> getFilesFormatCombo() { 
		return filesFormatCombo; 
	} 
 
	public void setFilesFormatCombo(List<String> filesFormatCombo) { 
		this.filesFormatCombo = filesFormatCombo; 
	} 
 
	public ReportManager getReportManager() { 
		return reportManager; 
	} 
 
	public void setReportManager(ReportManager reportManager) { 
		this.reportManager = reportManager; 
	} 
 
	public String getFileformatToExport() { 
		return fileformatToExport; 
	} 
 
	public void setFileformatToExport(String fileformatToExport) { 
		this.fileformatToExport = fileformatToExport; 
	} 
 
	public String getPathExportedReport() { 
		return pathExportedReport; 
	} 
 
	public void setPathExportedReport(String pathExportedReport) { 
		this.pathExportedReport = pathExportedReport; 
	} 
 
	public Map getPars() { 
		return pars; 
	} 
 
	public void setPars(Map pars) { 
		this.pars = pars; 
	}

	public List<Nota_ensayo> getListaNotaMonitoreo() {
		return listaNotaMonitoreo;
	}

	public void setListaNotaMonitoreo(List<Nota_ensayo> listaNotaMonitoreo) {
		this.listaNotaMonitoreo = listaNotaMonitoreo;
	}

	public List<NotasMonitoreo> getListaNotaMonitoreoExportar() {
		return listaNotaMonitoreoExportar;
	}

	public void setListaNotaMonitoreoExportar(
			List<NotasMonitoreo> listaNotaMonitoreoExportar) {
		this.listaNotaMonitoreoExportar = listaNotaMonitoreoExportar;
	} 
	
	public Long notasAbiertas() {
		
		try {
					
			BigInteger result = (BigInteger) entityManager
					.createNativeQuery("SELECT	count(*) FROM ensayo.nota "
					+ "JOIN	ensayo.crd_especifico ON ensayo.nota.id_crd_esp = ensayo.crd_especifico.id "
					+ "JOIN ensayo.momento_seguimiento_especifico ON ensayo.crd_especifico.id_momento_seg_especifico = ensayo.momento_seguimiento_especifico.id "
					+ "JOIN ensayo.momento_seguimiento_general ON ensayo.momento_seguimiento_especifico.id_momento_seguimiento_g = ensayo.momento_seguimiento_general.id "
					+ "JOIN ensayo.cronograma ON ensayo.momento_seguimiento_general.id_cronograma = ensayo.cronograma.id "
					+ "JOIN ensayo.grupo_sujetos ON ensayo.cronograma.id_grupo_sujetos = ensayo.grupo_sujetos.id "
					+ "JOIN	ensayo.estudio ON ensayo.grupo_sujetos.id_estudio = ensayo.estudio.id "
					+ "WHERE ensayo.nota.eliminado = false AND ensayo.nota.nota_sitio = false "
					+ "AND ensayo.nota.id_nota_padre IS NULL AND (ensayo.nota.id_estado_nota = 1 OR ensayo.nota.id_estado_nota = 2) "
					+ "AND ensayo.estudio.id = :idEstudioActivo "
					+ "AND ensayo.grupo_sujetos.nombre_grupo <> 'Grupo Validación'")					
					.setParameter("idEstudioActivo", seguridadEstudio.idEstudioActivo)
					.getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error notasAbiertas: "
					+ e.getMessage());
			return null;
		}
	}

}
