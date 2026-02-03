package gehos.comun.reportes.session;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporter;
import net.sf.jasperreports.engine.export.JRCsvExporter;
import net.sf.jasperreports.engine.export.JRCsvExporterParameter;
import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRRtfExporter;
import net.sf.jasperreports.engine.export.JRTextExporter;
import net.sf.jasperreports.engine.export.JRTextExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXmlExporter;
import net.sf.jasperreports.engine.export.JRXmlExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import org.jboss.seam.annotations.Name;

@Name("hisExportManager")
public class HISExportManager {

	public HISExportManager() {
		super();
		// TODO Auto-generated constructor stub
	}
	// EXPORTING TO RTF FORMAT
	/**
	 * Exports the generated report file specified by the parameter into Rtf format.
	 * The resulting Rtf file has the same name as the report object inside the source file,
	 * plus the <code>*.rft</code> extension and it is located in the same directory as the source file.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @return resulting Rtf file name
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static String exportReportToRtfFile(String sourceFileName) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".rtf");
		String destFileName = destFile.toString();
		
		exportReportToRtfFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into Rtf format,
	 * the result being placed in the second file parameter.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the Rtf content into
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static void exportReportToRtfFile(
		String sourceFileName, 
		String destFileName
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToRtfFile(jasperPrint, destFileName);
	}

	
	/**
	 * Exports the generated report file specified by the first parameter into Rtf format,
	 * the result being placed in the second file parameter.
	 *
	 * @param jasperPrint  report object to export 
	 * @param destFileName file name to place the Rtf content into
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static void exportReportToRtfFile(
		JasperPrint jasperPrint, 
		String destFileName
		) throws JRException
	{
		/*   */
		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report read from the supplied input stream into Rtf format and
	 * writes the results to the output stream specified by the second parameter.
	 *
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting Rtf content to
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static void exportReportToRtfStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToRtfStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object received as first parameter into Rtf format and
	 * writes the results to the output stream specified by the second parameter.
	 * 
	 * @param jasperPrint  report object to export 
	 * @param outputStream output stream to write the resulting Rtf content to
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static void exportReportToRtfStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object received as parameter into Rtf format and
	 * returns the binary content as a byte array.
	 * 
	 * @param jasperPrint report object to export 
	 * @return byte array representing the resulting Rtf content 
	 * @see net.sf.jasperreports.engine.export.JRRtfExporter
	 */
	public static byte[] exportReportToRtf(JasperPrint jasperPrint) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRRtfExporter exporter = new JRRtfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
		
		return baos.toByteArray();
	}


	// EXPORTING TO EXCEL
	/**
	 * Exports the generated report file specified by the parameter into EXCEL format.
	 * The resulting EXCEL file has the same name as the report object inside the source file,
	 * plus the <code>*.EXCEL</code> extension and it is located in the same directory as the source file.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @return resulting EXCEL file name
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static String exportReportToExcelFile(String sourceFileName) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".xls");
		String destFileName = destFile.toString();
		
		exportReportToExcelFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into EXCEL format,
	 * the result being placed in the second file parameter.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the EXCEL content into
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static void exportReportToExcelFile(
		String sourceFileName, 
		String destFileName
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToExcelFile(jasperPrint, destFileName);
	}

	
	/**
	 * Exports the generated report file specified by the first parameter into EXCEL format,
	 * the result being placed in the second file parameter.
	 *
	 * @param jasperPrint  report object to export 
	 * @param destFileName file name to place the EXCEL content into
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static void exportReportToExcelFile(
		JasperPrint jasperPrint, 
		String destFileName
		) throws JRException
	{
		/*   */
		
		JExcelApiExporter exporter  = new JExcelApiExporter();
		
		//JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report read from the supplied input stream into EXCEL format and
	 * writes the results to the output stream specified by the second parameter.
	 *
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting EXCEL content to
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static void exportReportToExcelStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToExcelStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object received as first parameter into EXCEL format and
	 * writes the results to the output stream specified by the second parameter.
	 * 
	 * @param jasperPrint  report object to export 
	 * @param outputStream output stream to write the resulting EXCEL content to
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static void exportReportToExcelStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JExcelApiExporter exporter = new JExcelApiExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object received as parameter into EXCEL format and
	 * returns the binary content as a byte array.
	 * 
	 * @param jasperPrint report object to export 
	 * @return byte array representing the resulting EXCEL content 
	 * @see net.sf.jasperreports.engine.export.JREXCELExporter
	 */
	public static byte[] exportReportToExcelStream(JasperPrint jasperPrint) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JExcelApiExporter exporter = new JExcelApiExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		exporter.setParameter(JRExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
		
		return baos.toByteArray();
	}

	
	// EXPORTING TO CSV
	/**
	 * Exports the generated report file specified by the parameter into CSV format.
	 * The resulting CSV file has the same name as the report object inside the source file,
	 * plus the <code>*.CSV</code> extension and it is located in the same directory as the source file.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @return resulting CSV file name
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static String exportReportToCsvFile(String sourceFileName) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".csv");
		String destFileName = destFile.toString();
		
		exportReportToCsvFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into CSV format,
	 * the result being placed in the second file parameter.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the CSV content into
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static void exportReportToCsvFile(
		String sourceFileName, 
		String destFileName
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToCsvFile(jasperPrint, destFileName);
	}

	
	/**
	 * Exports the generated report file specified by the first parameter into CSV format,
	 * the result being placed in the second file parameter.
	 *
	 * @param jasperPrint  report object to export 
	 * @param destFileName file name to place the CSV content into
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static void exportReportToCsvFile(
		JasperPrint jasperPrint, 
		String destFileName
		) throws JRException
	{
		/*   */
		
		JRCsvExporter exporter  = new JRCsvExporter();
		
		//JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report read from the supplied input stream into CSV format and
	 * writes the results to the output stream specified by the second parameter.
	 *
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting CSV content to
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static void exportReportToCsvStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToCsvStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object received as first parameter into CSV format and
	 * writes the results to the output stream specified by the second parameter.
	 * 
	 * @param jasperPrint  report object to export 
	 * @param outputStream output stream to write the resulting CSV content to
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static void exportReportToCsvStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JRCsvExporter exporter = new JRCsvExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object received as parameter into CSV format and
	 * returns the binary content as a byte array.
	 * 
	 * @param jasperPrint report object to export 
	 * @return byte array representing the resulting CSV content 
	 * @see net.sf.jasperreports.engine.export.JRCSVExporter
	 */
	public static byte[] exportReportToCSVStream(JasperPrint jasperPrint) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRCsvExporter exporter = new JRCsvExporter();
		exporter.setParameter(JRCsvExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		
		exporter.exportReport();
		
		return baos.toByteArray();
	}

	// EXPORTING TO PLAIN TEXT
	/**
	 * Exports the generated report file specified by the parameter into PlainText format.
	 * The resulting PlainText file has the same name as the report object inside the source file,
	 * plus the <code>*.PlainText</code> extension and it is located in the same directory as the source file.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @return resulting PlainText file name
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static String exportReportToPlainTextFile(String sourceFileName) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".txt");
		String destFileName = destFile.toString();
		
		exportReportToPlainTextFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into PlainText format,
	 * the result being placed in the second file parameter.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the PlainText content into
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static void exportReportToPlainTextFile(
		String sourceFileName, 
		String destFileName
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToPlainTextFile(jasperPrint, destFileName);
	}

	
	/**
	 * Exports the generated report file specified by the first parameter into PlainText format,
	 * the result being placed in the second file parameter.
	 *
	 * @param jasperPrint  report object to export 
	 * @param destFileName file name to place the PlainText content into
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static void exportReportToPlainTextFile(
		JasperPrint jasperPrint, 
		String destFileName
		) throws JRException
	{
		/*   */
		
		JRTextExporter exporter  = new JRTextExporter();
		
		//JRXlsExporter exporter = new JRXlsExporter();
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
		exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
		//exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		
		exporter.exportReport();
	}


	/**
	 * Exports the generated report read from the supplied input stream into PlainText format and
	 * writes the results to the output stream specified by the second parameter.
	 *
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting PlainText content to
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static void exportReportToPlainTextStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToPlainTextStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object received as first parameter into PlainText format and
	 * writes the results to the output stream specified by the second parameter.
	 * 
	 * @param jasperPrint  report object to export 
	 * @param outputStream output stream to write the resulting PlainText content to
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static void exportReportToPlainTextStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JRTextExporter exporter  = new JRTextExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
		exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
		//exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object received as parameter into PlainText format and
	 * returns the binary content as a byte array.
	 * 
	 * @param jasperPrint report object to export 
	 * @return byte array representing the resulting PlainText content 
	 * @see net.sf.jasperreports.engine.export.JRPlainTextExporter
	 */
	public static byte[] exportReportToPlainTextStream(JasperPrint jasperPrint) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRTextExporter exporter  = new JRTextExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		exporter.setParameter(JRTextExporterParameter.CHARACTER_WIDTH, new Integer(10));
		exporter.setParameter(JRTextExporterParameter.CHARACTER_HEIGHT, new Integer(10));
		//exporter.setParameter(JRTextExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
		
		return baos.toByteArray();
	}

	
	/**
	 * Exports the generated report file specified by the parameter into PDF format.
	 * The resulting PDF file has the same name as the report object inside the source file,
	 * plus the <code>*.pdf</code> extension and it is located in the same directory as the source file.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @return resulting PDF file name
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static String exportReportToPdfFile(String sourceFileName) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".pdf");
		String destFileName = destFile.toString();
		
		exportReportToPdfFile(jasperPrint, destFileName);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into PDF format,
	 * the result being placed in the second file parameter.
	 *  
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the PDF content into
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToPdfFile(
		String sourceFileName, 
		String destFileName
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToPdfFile(jasperPrint, destFileName);
	}

	
	/**
	 * Exports the generated report file specified by the first parameter into PDF format,
	 * the result being placed in the second file parameter.
	 *
	 * @param jasperPrint  report object to export 
	 * @param destFileName file name to place the PDF content into
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToPdfFile(
		JasperPrint jasperPrint, 
		String destFileName
		) throws JRException
	{
		/*   */
		JRPdfExporter exporter = new JRPdfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report read from the supplied input stream into PDF format and
	 * writes the results to the output stream specified by the second parameter.
	 *
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting PDF content to
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToPdfStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToPdfStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object received as first parameter into PDF format and
	 * writes the results to the output stream specified by the second parameter.
	 * 
	 * @param jasperPrint  report object to export 
	 * @param outputStream output stream to write the resulting PDF content to
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToPdfStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JRPdfExporter exporter = new JRPdfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object received as parameter into PDF format and
	 * returns the binary content as a byte array.
	 * 
	 * @param jasperPrint report object to export 
	 * @return byte array representing the resulting PDF content 
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static byte[] exportReportToPdfStream(JasperPrint jasperPrint) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRPdfExporter exporter = new JRPdfExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		exporter.setParameter(JRPdfExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
		
		return baos.toByteArray();
	}

	
	/**
	 * Exports the generated report file specified by the parameter into XML format.
	 * The resulting XML file has the same name as the report object inside the source file,
	 * plus the <code>*.jrpxml</code> extension and it is located in the same directory as the source file.
	 * <p>
	 * When exporting to XML format, the images can be either embedded in the XML content
	 * itself using the Base64 encoder or be referenced as external resources.
	 * If not embedded, the images are placed as distinct files inside a directory
	 * having the same name as the XML destination file, plus the "_files" suffix. 
	 * 
	 * @param sourceFileName    source file containing the generated report
	 * @param isEmbeddingImages flag that indicates whether the images should be embedded in the
	 *                          XML content itself using the Base64 encoder or be referenced as external resources
	 * @return XML representation of the generated report
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static String exportReportToXmlFile(
		String sourceFileName, 
		boolean isEmbeddingImages
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".jrpxml");
		String destFileName = destFile.toString();
		
		exportReportToXmlFile(
			jasperPrint, 
			destFileName,
			isEmbeddingImages
			);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into XML format,
	 * placing the result into the second file parameter.
	 * <p>
	 * If not embedded into the XML content itself using the Base64 encoder, 
	 * the images are placed as distinct files inside a directory having the same name 
	 * as the XML destination file, plus the "_files" suffix. 
	 * 
	 * @param sourceFileName    source file containing the generated report
	 * @param destFileName      file name to place the XML representation into
	 * @param isEmbeddingImages flag that indicates whether the images should be embedded in the
	 *                          XML content itself using the Base64 encoder or be referenced as external resources
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToXmlFile(
		String sourceFileName, 
		String destFileName,
		boolean isEmbeddingImages
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToXmlFile(
			jasperPrint, 
			destFileName,
			isEmbeddingImages
			);
	}

	
	/**
	 * Exports the generated report object received as parameter into XML format,
	 * placing the result into the second file parameter.
	 * <p>
	 * If not embedded into the XML content itself using the Base64 encoder, 
	 * the images are placed as distinct files inside a directory having the same name 
	 * as the XML destination file, plus the "_files" suffix. 
	 * 
	 * @param jasperPrint       report object to export
	 * @param destFileName      file name to place the XML representation into
	 * @param isEmbeddingImages flag that indicates whether the images should be embedded in the
	 *                          XML content itself using the Base64 encoder or be referenced as external resources
	 *  
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToXmlFile(
		JasperPrint jasperPrint, 
		String destFileName,
		boolean isEmbeddingImages
		) throws JRException
	{
		JRXmlExporter exporter = new JRXmlExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRXmlExporterParameter.IS_EMBEDDING_IMAGES,
			isEmbeddingImages ? Boolean.TRUE : Boolean.FALSE);
		exporter.setParameter(JRXmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}


	/**
	 * Exports the generated report object read from the supplied input stream into XML format,
	 * and writes the result to the output stream specified by the second parameter.
	 * The images are embedded into the XML content itself using the Base64 encoder. 
	 * 
	 * @param inputStream  input stream to read the generated report object from
	 * @param outputStream output stream to write the resulting XML representation to
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToXmlStream(
		InputStream inputStream, 
		OutputStream outputStream
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(inputStream);

		exportReportToXmlStream(jasperPrint, outputStream);
	}

	
	/**
	 * Exports the generated report object supplied as the first parameter into XML format,
	 * and writes the result to the output stream specified by the second parameter.
	 * The images are embedded into the XML content itself using the Base64 encoder. 
	 * 
	 * @param jasperPrint  report object to export
	 * @param outputStream output stream to write the resulting XML representation to
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToXmlStream(
		JasperPrint jasperPrint, 
		OutputStream outputStream
		) throws JRException
	{
		JRXmlExporter exporter = new JRXmlExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
		exporter.setParameter(JRXmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
	}
	
	public static byte[] exportReportToXmlStream(
			JasperPrint jasperPrint
			) throws JRException
		{
			JRXmlExporter exporter = new JRXmlExporter();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
			exporter.setParameter(JRXmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
			exporter.exportReport();
			
			return baos.toByteArray();
			
			
		}


	/**
	 * Exports the generated report object supplied as parameter into XML format
	 * and returns the result as String.
	 * The images are embedded into the XML content itself using the Base64 encoder. 
	 * 
	 * @param jasperPrint report object to export
	 * @return XML representation of the generated report
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static String exportReportToXml(JasperPrint jasperPrint) throws JRException
	{
		StringBuffer sbuffer = new StringBuffer();

		JRXmlExporter exporter = new JRXmlExporter();
		
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STRING_BUFFER, sbuffer);
		exporter.setParameter(JRXmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.exportReport();
		
		return sbuffer.toString();
	}


	/**
	 * Exports the generated report file specified by the parameter into HTML format.
	 * The resulting HTML file has the same name as the report object inside the source file,
	 * plus the <code>*.html</code> extension and it is located in the same directory as the source file.
	 * The images are placed as distinct files inside a directory having the same name 
	 * as the HTML destination file, plus the "_files" suffix. 
	 * 
	 * @param sourceFileName source file containing the generated report
	 * @return resulting HTML file name
	 * @see net.sf.jasperreports.engine.export.JRHtmlExporter
	 */
	public static String exportReportToHtmlFile(
		String sourceFileName, String imagesURI
		) throws JRException
	{
		File sourceFile = new File(sourceFileName);

		/* We need the report name. */
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFile);

		File destFile = new File(sourceFile.getParent(), jasperPrint.getName() + ".html");
		String destFileName = destFile.toString();
		
		exportReportToHtmlFile(
			jasperPrint, 
			destFileName, imagesURI
			);
		
		return destFileName;
	}


	/**
	 * Exports the generated report file specified by the first parameter into HTML format,
	 * placing the result into the second file parameter.
	 * <p>
	 * The images are placed as distinct files inside a directory having the same name 
	 * as the HTML destination file, plus the "_files" suffix. 
	 * 
	 * @param sourceFileName source file containing the generated report
	 * @param destFileName   file name to place the HTML content into
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToHtmlFile(
		String sourceFileName, 
		String destFileName, String imagesURI
		) throws JRException
	{
		JasperPrint jasperPrint = (JasperPrint)JRLoader.loadObject(sourceFileName);

		exportReportToHtmlFile(
			jasperPrint, 
			destFileName, imagesURI
			);
	}

	
	/**
	 * Exports the generated report object received as parameter into HTML format,
	 * placing the result into the second file parameter.
	 * <p>
	 * The images are placed as distinct files inside a directory having the same name 
	 * as the HTML destination file, plus the "_files" suffix. 
	 * 
	 * @param jasperPrint  report object to export
	 * @param destFileName file name to place the HTML content into
	 * @see net.sf.jasperreports.engine.export.JRPdfExporter
	 */
	public static void exportReportToHtmlFile(
		JasperPrint jasperPrint, 
		String destFileName, String imagesURI
		) throws JRException
	{
		JRHtmlExporter exporter = new JRHtmlExporter();
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
        HttpServletRequest request = (HttpServletRequest)   aFacesContext.getExternalContext().getRequest();
        Map imagesMap = new HashMap();
        request.getSession().setAttribute("IMAGES_MAP", imagesMap);
        
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_FILE_NAME, destFileName);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);

		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING , "ISO-8859-1");
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imagesURI);
	//	exporter.setParameter(JRHtmlExporterParameter.IS_OUTPUT_IMAGES_TO_DIR, false);
		//exporter.setParameter(JRHtmlExporterParameter.IMAGES_DIR, new File(imagesURI));
		exporter.exportReport();
	}
	
	public static void exportReportToHtmlStream(
			JasperPrint jasperPrint, 
			OutputStream outputStream, String imagesURI
			) throws JRException
		{
			JRHtmlExporter exporter = new JRHtmlExporter();
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
	        ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
	        HttpServletRequest request = (HttpServletRequest)   aFacesContext.getExternalContext().getRequest();
	        Map imagesMap = new HashMap();
	        request.getSession().setAttribute("IMAGES_MAP", imagesMap);
			exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
			exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, outputStream);
			exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
			exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imagesURI);
			exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
			exporter.exportReport();
		}
	
	public static byte[] exportReportToHtmlStream(JasperPrint jasperPrint, String imagesURI) throws JRException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		JRHtmlExporter exporter = new JRHtmlExporter();
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
        ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
        HttpServletRequest request = (HttpServletRequest)   aFacesContext.getExternalContext().getRequest();
        Map imagesMap = new HashMap();
        request.getSession().setAttribute("IMAGES_MAP", imagesMap);
		exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
		exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
		exporter.setParameter(JRHtmlExporterParameter.CHARACTER_ENCODING, "ISO-8859-1");
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_MAP, imagesMap);
		exporter.setParameter(JRHtmlExporterParameter.IMAGES_URI, imagesURI);
		
		exporter.exportReport();
		
		return baos.toByteArray();
	}
	
}
