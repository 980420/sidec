package gehos.ensayo.ensayo_estadisticas.session.reportes;

import gehos.comun.reportes.session.FileType;
import gehos.ensayo.ensayo_estadisticas.session.comun.ReportsGeneric;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Name("notasMonitoreoEstadoSujeto")
@Scope(ScopeType.CONVERSATION)
public class NotasMonitoreoEstadoSujeto extends ReportsGeneric {
	
	private Date fechaIni;
	private Date fechaFin;
	
	private String tituloMessage = SeamResourceBundle.getBundle().getString("nombreReport");
	private String rangoFechaMessage = SeamResourceBundle.getBundle().getString("rangoFecha");
	private String paginaMessage = SeamResourceBundle.getBundle().getString("page");
	private String estadosMessage = SeamResourceBundle.getBundle().getString("estados");
	private String sujetosMessage = SeamResourceBundle.getBundle().getString("sujetos");
	private String servicioEstadisticasMessage = SeamResourceBundle.getBundle().getString("servicioEstadisticas");
	
	@Create
	public void init(){
		this.template="Rpt_NotasMonitoreoEstadoSujeto";
		filesFormatCombo = reportManager.fileFormatsToExport();
		lista=new ArrayList<Object>();
	}
	
	@Override
	public void GenerarReporte() {
		try {
			llenarListaDatos();
			
			String subtitulo=rangoFechaMessage + ": "+ utiles_estadisticas.formatearFecha(fechaIni) + " - " + utiles_estadisticas.formatearFecha(fechaFin); 
			
			parametros=new HashMap();
			parametros.put("P_TITULO", this.tituloMessage);
			parametros.put("P_SUBTITULO", subtitulo);
			parametros.put("PAGINA", this.paginaMessage);
			parametros.put("INF_TIPO_INSTITUCION", ubicacionesManager.currentEntity());
			parametros.put("INF_NOMBRE_INSTITUCION", ubicacionesManager.currentEntity());
			parametros.put("INF_SERVICIO", this.servicioEstadisticasMessage);
			parametros.put("CROSSTABHEADERCELLVALUER", this.sujetosMessage);
			
			pathToReport=reportManager.ExportReport(this.template, parametros, lista, FileType.HTML_FILE);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	private void llenarListaDatos(){
		String query="select new gehos.ensayo.ensayo_estadisticas.session.reportes.wrappers.NotasMonitoreoEstadoSujetoWrapper('"+estadosMessage+"',s.nombre, e.nombre, count(*)) from Sujeto s "+ 
				"inner join s.grupoSujetos gs "+
				"inner join gs.cronogramas cr "+
				"inner join cr.momentoSeguimientoGenerals msg "+
				"inner join msg.momentoSeguimientoEspecificos mse "+
				"inner join mse.versionHojaCrds vhcr "+
				"inner join vhcr.estadoByFkEstadoMonitoreo e "+
				"inner join vhcr.variables v "+
				"inner join v.notas n "+
				"where n.fechaCreacion >=:fechaIni and n.fechaCreacion<=:fechaFin "+
				"group by s.nombre,e.nombre "+
				"order by s.nombre,e.nombre";
		lista=entityManager.createQuery(query)
				.setParameter("fechaIni", fechaIni)
				.setParameter("fechaFin", fechaFin)
				.getResultList();
	}		

	public void generarGrafico(){
		
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

	@Override
	public void buildColumns() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void buildQuery() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> getResultadosFormateados(List<Object> resultados) {
		// TODO Auto-generated method stub
		return null;
	}
}
