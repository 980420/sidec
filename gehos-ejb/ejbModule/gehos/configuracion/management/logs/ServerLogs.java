package gehos.configuracion.management.logs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.Scanner;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("serverLogs")
@Scope(ScopeType.PAGE)
public class ServerLogs {
	
	private String destination;
	private String fileName;
	private int linesNumber = 150;
	private String lines;
	
	private static final int BUFFER_SIZE = 1024;

	public void Zippear(String pFile, String pZipFile) throws Exception {
		// objetos en memoria
		FileInputStream fis = null;
		FileOutputStream fos = null;
		ZipOutputStream zipos = null;
 
		// buffer
		byte[] buffer = new byte[BUFFER_SIZE];
		try {
			// fichero a comprimir
			fis = new FileInputStream(pFile);
			// fichero contenedor del zip
			fos = new FileOutputStream(pZipFile);
			// fichero comprimido
			zipos = new ZipOutputStream(fos);
			ZipEntry zipEntry = new ZipEntry("server.log");
			zipos.putNextEntry(zipEntry);
			int len = 0;
			// zippear
			while ((len = fis.read(buffer, 0, BUFFER_SIZE)) != -1)
				zipos.write(buffer, 0, len);
			// volcar la memoria al disco
			zipos.flush();
		} catch (Exception e) {
			throw e;
		} finally {
			// cerramos los files
			zipos.close();
			fis.close();
			fos.close();
		} // end try
	}
	
	public void copyLogs() throws Exception{
		
		String logsPath = System.getProperty("jboss.server.log.dir") + 
				"/server.log";
		//logsPath = logsPath.replace("file:/", "");
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext)aFacesContext.getExternalContext().getContext();
		fileName = "/logs_" + UUID.randomUUID().toString() + ".zip";
		if(destination == null)
			destination = context.getRealPath("/resources/modConfiguracion") +  fileName;

		Zippear(logsPath, destination);
		
	}
	
	public String downloadCommand(){
		return "window.open('/gehos/resources/modConfiguracion" + fileName + "');";
	}
	
	public String tail( File file, int lines) throws IOException {
		RandomAccessFile fileHandler = null;
		try {
	        fileHandler = new RandomAccessFile( file, "r" );
	        long fileLength = file.length() - 1;
	        StringBuilder sb = new StringBuilder();
	        int line = 0;

	        for( long filePointer = fileLength; filePointer != -1; filePointer-- ) {
	            fileHandler.seek( filePointer );
	            int readByte = fileHandler.readByte();

	            if( readByte == 0xA ) {
	                if (line == lines) {
	                    if (filePointer == fileLength) {
	                        continue;
	                    } else {
	                        break;
	                    }
	                }
	            } else if( readByte == 0xD ) {
	                line = line + 1;
	                if (line == lines) {
	                    if (filePointer == fileLength - 1) {
	                        continue;
	                    } else {
	                        break;
	                    }
	                }
	            }
	           sb.append( ( char ) readByte );
	        }

	        sb.deleteCharAt(sb.length()-1);
	        String lastLine = sb.reverse().toString();
	        return lastLine;
	    } catch( FileNotFoundException e ) {
	        e.printStackTrace();
	        return null;
	    } catch( IOException e ) {
	        e.printStackTrace();
	        return null;
	    }
	    finally { fileHandler.close(); }
	}
	
	public void updateLines() throws IOException{
		String logsPath = System.getProperty("jboss.server.log.dir") + 
				"/server.log";
		File file = new File(logsPath);
		lines = tail(file, linesNumber);
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public int getLinesNumber() {
		return linesNumber;
	}

	public void setLinesNumber(int linesNumber) {
		this.linesNumber = linesNumber;
	}

	public String getLines() {
		if(lines == null){
			try {
				updateLines();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return lines;
	}

	public void setLines(String lines) {
		this.lines = lines;
	}
	
	
}
