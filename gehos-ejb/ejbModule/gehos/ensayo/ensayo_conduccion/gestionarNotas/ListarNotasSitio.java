//CU 26 Visualizar notas de sitio por sujeto
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.session.custom.NotaCustomList;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.reporteListadoNotasMonitoreo.NotasMonitoreo;
import gehos.ensayo.ensayo_estadisticas.reporteListadoNotasSitio.NotasSitio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
@Name("listarNotasSitio")
@Scope(ScopeType.CONVERSATION)
public class ListarNotasSitio extends NotaCustomList {
	
	private static final String EJBQL = "select nota from Nota_ensayo nota "
			+ "where nota.eliminado = false and nota.notaSitio = true and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = #{listarNotasSitio.idSujeto}";

	private static final String[] RESTRICTIONS = {
			"lower(nota.descripcion) like concat(lower(#{listarNotasSitio.descripcion.trim()}),'%')",
			"#{listarNotasSitio.idNota} <> nota.id" };

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
	
	
	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private int pagina;
	
	
	private String descripcion = "";
	private String tabSeleccionado = "";
	
	private Nota_ensayo nota = new Nota_ensayo();
	private Long idSujeto, idGrupo, idNota, ideliminarNota;
	
	private List<String> filesFormatCombo; 
	private String fileformatToExport; 
	private String pathExportedReport; 
	
	private List<Nota_ensayo> listaNotaSitio;
	private List<NotasSitio> listaNotaSitioExportar;
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
	
	private Map pars; 
	
	public ListarNotasSitio() {
		this.filesFormatCombo =new ArrayList<String>(); 
    	this.filesFormatCombo.add("PDF");
    	this.filesFormatCombo.add("WORD");
    	this.filesFormatCombo.add("EXCEL");
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("nota.id desc");
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
		this.idSujeto = idSujeto;
	}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua",user.getId())
				.getSingleResult();
		
		return rol;
	}
	
	public void SeleccionarInstanciaNota(long id) {
		this.setIdeliminarNota(id);
	}
	
	// CU 34 Eliminar nota de sitio
		public void EliminarInstanciaNota() {

			Nota_ensayo notaEliminar = entityManager
					.find(Nota_ensayo.class,
							this.ideliminarNota);
			notaEliminar.setEliminado(true);
			entityManager.persist(notaEliminar);
			entityManager.flush();

		}
//exportar notas del sitio
		@SuppressWarnings("unchecked")
		public void exportarNotaSitio(long idNotaSitio){
			this.listaNotaSitio=new ArrayList<Nota_ensayo>();
			this.listaNotaSitioExportar=new ArrayList<NotasSitio>();
			this.pars = new HashMap(); 
			this.filesFormatCombo = this.reportManager.fileFormatsToExport(); 
			for(Nota_ensayo nota: this.getResultList()){
				if(nota.getId()==idNotaSitio){
					this.listaNotaSitio.add(nota);
					break;
				}
			}
			
			if (!this.listaNotaSitio.isEmpty()){
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
				
				String codigoSujeto=this.listaNotaSitio.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
				if(this.listaNotaSitio.get(0).getFechaCreacion()!=null){ 
					fechaCreacion=formatter.format(this.listaNotaSitio.get(0).getFechaCreacion());
				}
				if(this.listaNotaSitio.get(0).getFechaActualizacion()!=null){ 
					fechaActualizacion=formatter.format(this.listaNotaSitio.get(0).getFechaActualizacion());
				}
				String fechaInicioMs=formatter.format(this.listaNotaSitio.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getFechaInicio());
				
				String msNombre=this.listaNotaSitio.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getNombre();
				String msEstado=this.listaNotaSitio.get(0).getCrdEspecifico().getMomentoSeguimientoEspecifico().getEstadoMomentoSeguimiento().getNombre();
				
				String ms_estado=msNombre+" "+"("+msEstado+")";
				
				String crd=this.listaNotaSitio.get(0).getCrdEspecifico().getHojaCrd().getNombreHoja();
				
				String nombreVariable=this.listaNotaSitio.get(0).getVariable().getNombreVariable();
				
				String valorVariable="";
				if(this.listaNotaSitio.get(0).getVariableDato()==null){ 
					if(ObtenerValorVariable(this.listaNotaSitio.get(0)) != null){ 
						valorVariable=ObtenerValorVariable(this.listaNotaSitio.get(0)).getValor();
					}
				}else{ 
					valorVariable= this.listaNotaSitio.get(0).getVariableDato().getValor();
					valorVariable= valorVariable == null ? "" : valorVariable;
				}
				
			
					
				
				
				
				String notaDescripcion=this.listaNotaSitio.get(0).getDescripcion();
				
				String estadoNota=this.listaNotaSitio.get(0).getEstadoNota().getNombre();
				
				String detallesNota=this.listaNotaSitio.get(0).getDetallesNota();
				
				int numeroNotas=this.DevolverNotaSitioAsociadas(this.listaNotaSitio.get(0)).size();
				
				NotasSitio notaSitioSuj = new NotasSitio(codigoSujeto, fechaCreacion, crd, nombreVariable, valorVariable, notaDescripcion,  "1");
				this.listaNotaSitioExportar.add(notaSitioSuj);
			}	
		}
		
		public void exportReportToFileFormat(){ 
			this.pathExportedReport = ""; 
			if (this.fileformatToExport.equals(this.filesFormatCombo.get(0))) 
				this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasSitioConduccion", this.pars, this.listaNotaSitioExportar, FileType.PDF_FILE); 
			else if (this.fileformatToExport.equals(this.filesFormatCombo.get(1))) 
				this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasSitioConduccion", this.pars, this.listaNotaSitioExportar, FileType.RTF_FILE); 
			else if (this.fileformatToExport.equals(this.filesFormatCombo.get(2))) 
				this.pathExportedReport = this.reportManager.ExportReport("reportListadoNotasSitioConduccion", this.pars, this.listaNotaSitioExportar, FileType.EXCEL_FILE); 
		} 
			
		@SuppressWarnings("unchecked")
		public List<Nota_ensayo> DevolverNotaSitioAsociadas(Nota_ensayo Nota){
			List<Nota_ensayo> listaHijas = new ArrayList<Nota_ensayo>();
			listaHijas = (List<Nota_ensayo>) entityManager
						.createQuery(
								"select nota from Nota_ensayo nota where nota.notaPadre=:notaPadre and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE'")
						.setParameter("notaPadre", Nota).getResultList();
			
			return listaHijas;
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

	public Nota_ensayo getNotaEnsayo() {
		return nota;
	}

	public void setNotaEnsayo(Nota_ensayo notaEnsayo) {
		this.nota = notaEnsayo;
	}
	
	public Long getIdeliminarNota() {
		return ideliminarNota;
	}
	public List<String> getFilesFormatCombo() { 
		return filesFormatCombo; 
	} 
 
	public void setFilesFormatCombo(List<String> filesFormatCombo) { 
		this.filesFormatCombo = filesFormatCombo; 
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
	public List<Nota_ensayo> getListaNotaSitio() {
		return listaNotaSitio;
	}

	public void setListaNotaSitio(List<Nota_ensayo> listaNotaSitio) {
		this.listaNotaSitio = listaNotaSitio;
	}

	public List<NotasSitio> getListaNotaSitioExportar() {
		return listaNotaSitioExportar;
	}

	public void setListaNotaSitioExportar(
			List<NotasSitio> listaNotaSitioExportar) {
		this.listaNotaSitioExportar = listaNotaSitioExportar;
	} 
	public void setIdeliminarNota(Long ideliminarNota) {
		this.ideliminarNota = ideliminarNota;
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

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getTabSeleccionado() {
		return tabSeleccionado;
	}

	public void setTabSeleccionado(String tabSeleccionado) {
		this.tabSeleccionado = tabSeleccionado;
	}
}
