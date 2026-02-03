package gehos.comun.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.hibernate.stat.Statistics;

import com.sun.org.apache.bcel.internal.generic.NEW;



/**
 * @author Yurien
 * @version 1 GENERA LOS LOGS DE LAS ESTADISTICAS DE HIBERNATE
 */
public final class GenerateStatisticsLog {

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
	private static final SimpleDateFormat DATE_FORMAT_FILE = new SimpleDateFormat("dd/MM/yyyy");
		
	// Camino dentro del servidor donde se guardan los logs
	private static final String PATH_TO_SERVER = System.getProperty("jboss.server.home.url");

	// Para escribir en el fichero de logs
	private static BufferedWriter bw;
	private FileWriter fw;

	//Archivo para el log
	private static File file;

	private static String actualFilePath = new String();
	

	public static void generateLog(Statistics stats) throws IOException, Exception {
		String pathToLogFile = getCaminoBaseAplicacion() + DATE_FORMAT_FILE.format(new Date()).replace("/", "-") + ".log";
		
		if(!actualFilePath.equals(pathToLogFile));
			file = new File(pathToLogFile);
		
		if(!file.exists())
		{
			if(!file.createNewFile())
				throw new Exception("No se pudo crear el fichero en el camino especificado");
			
		}
		writeStatistics(stats);
		actualFilePath = pathToLogFile;
	
	}
	
	private static void writeStatistics(Statistics stats) throws IOException
	{
		
		bw = new BufferedWriter(new FileWriter(file,true));
		
		bw.write(DATE_FORMAT.format(new Date()) + "\n");
		bw.write("Connection count: " + stats.getConnectCount() + "\n");
		bw.write("Transactions count: " + stats.getTransactionCount() + "\n");
		bw.write("Succeed transactions count: " + stats.getSuccessfulTransactionCount() + "\n");
		bw.write("Opened sessions: " + stats.getSessionOpenCount() + "\n");
		bw.write("Closed sessions: " + stats.getSessionCloseCount() + "\n");
		bw.write("No. queries: " + stats.getQueryExecutionCount() + "\n");
		bw.write("Optimistick lock exception count: " + stats.getOptimisticFailureCount() + "\n");
		bw.write("Prepared Statements count: " + stats.getPrepareStatementCount() + "\n");
		bw.newLine();
		
		bw.flush();
		bw.close();
		
		System.out.println(DATE_FORMAT.format(new Date()) + "\n");
		System.out.println("Connection count: " + stats.getConnectCount() + "\n");
		System.out.println("Transactions count: " + stats.getTransactionCount() + "\n");
		System.out.println("Succeed transactions count: " + stats.getSuccessfulTransactionCount() + "\n");
		System.out.println("Opened sessions: " + stats.getSessionOpenCount() + "\n");
		System.out.println("Closed sessions: " + stats.getSessionCloseCount() + "\n");
		System.out.println("No. queries: " + stats.getQueryExecutionCount() + "\n");
		System.out.println("Optimistick lock exception count: " + stats.getOptimisticFailureCount() + "\n");
		System.out.println("Prepared Statements count: " + stats.getPrepareStatementCount() + "\n");
		
	}

	private static String getCaminoBaseAplicacion() {
		return System.getProperty("jboss.server.home.dir").replace("file:","") 
				                + System.getProperty("file.separator") + "log" 
				                + System.getProperty("file.separator") +  "HibernateLogs" + System.getProperty("file.separator");
	}

}
