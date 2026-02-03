package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma;

import java.util.List;

public interface ExportInterface {
	public void exportReportToFileFormat();
	public String getFileformatToExport() ;
	public void setFileformatToExport(String fileformatToExport);
	public List<String> getFilesFormatCombo();
	public void setFilesFormatCombo(List<String> filesFormatCombo) ;
	public String getPathExportedReport() ;
	public void setPathExportedReport(String pathExportedReport) ;
}
