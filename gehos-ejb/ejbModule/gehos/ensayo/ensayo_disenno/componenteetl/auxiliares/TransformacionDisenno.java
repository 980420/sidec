package gehos.ensayo.ensayo_disenno.componenteetl.auxiliares;

import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.FXMLDisenno;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.steps.excelinput.ExcelInputMeta;

import com.ibm.icu.text.CharsetDetector;
import com.ibm.icu.text.CharsetMatch;

public class TransformacionDisenno {

	private Trans transformacion;
	private TransMeta tmeta;
	private FXMLDisenno fichero;
	private ConexionDisenno con;
	private FExcelDisenno forigen;

	public TransformacionDisenno(FXMLDisenno ftran) throws Exception {
		this.fichero = ftran;
		try {
			crear();
		} catch (Exception e) {
			throw e;
		}
	}

	private void crear() throws Exception {
		try {
			tmeta = new TransMeta(fichero.getDir());
			transformacion = new Trans(tmeta);
		} catch (Exception e) {
			throw e;
		}
	}

	public Trans getTransformacion() {
		return transformacion;
	}

	public TransMeta getTmeta() {
		return tmeta;
	}

	public FExcelDisenno getForigen() {
		if (forigen == null) {
			List<StepMeta> lstep = tmeta.getSteps();
			int pos = buscar(lstep, ExcelInputMeta.class,
					new ArrayList<Integer>());
			StepMeta stepmeta = lstep.get(pos);
			ExcelInputMeta excel = (ExcelInputMeta) stepmeta
					.getStepMetaInterface();
			String[] ficheros = excel.getFileName();
			forigen = new FExcelDisenno(ficheros[0]);
		}
		return forigen;
	}

	// Modificar el valor de la direccion del excel origen
	public void setForigen(FExcelDisenno forigen) throws Exception {
		this.forigen = forigen;
		List<StepMeta> lstep = tmeta.getSteps();
		List<Integer> encontrados = new ArrayList<Integer>();
		int pos = buscar(lstep, ExcelInputMeta.class, encontrados);

		do {
			encontrados.add(pos);
			StepMeta stepmeta = lstep.get(pos);
			ExcelInputMeta excel = (ExcelInputMeta) stepmeta
					.getStepMetaInterface();
			String[] dirs = { forigen.getDir() };
			excel.setFileName(dirs);
			excel.setEncoding(getEncoding(dirs[0]));
			stepmeta.setStepMetaInterface(excel);
			tmeta.setStep(pos, stepmeta);
			pos = buscar(lstep, ExcelInputMeta.class, encontrados);
		} while (pos != -1);
		transformacion.setTransMeta(tmeta);
	}

	public ConexionDisenno getCon() {
		if (con == null) {
			List<DatabaseMeta> ldbs = tmeta.getDatabases();
			DatabaseMeta dbmeta = ldbs.get(0);
			con = new ConexionDisenno();
			con.setNombreServer(dbmeta.getHostname());
			con.setPuerto(Integer.parseInt(dbmeta.getDatabasePortNumberString()));
			con.setNombreBD(dbmeta.getDatabaseName());
			con.setUser(dbmeta.getUsername());
			con.setPass(dbmeta.getPassword());
		}
		return con;
	}

	// Modificar los valores de la conexion a la base de datos
	public void setCon(ConexionDisenno con) {
		this.con = con;
		List<DatabaseMeta> ldbs = tmeta.getDatabases();
		for (int i = 0; i < ldbs.size(); i++) {
			DatabaseMeta dbmeta = ldbs.get(i);
			dbmeta.setHostname(con.getNombreServer());
			dbmeta.setDBPort(con.getPuerto() + "");
			dbmeta.setDBName(con.getNombreBD());
			dbmeta.setUsername(con.getUser());
			dbmeta.setPassword(con.getPass());
			ldbs.set(i, dbmeta);
		}
		tmeta.setDatabases(ldbs);
		transformacion.setTransMeta(tmeta);
	}

	public String getDireccion() {
		return fichero.getDir();
	}

	public String getNombre() {
		return fichero.getNombre();
	}

	public int getOrden() {
		String descrip = tmeta.getDescription();
		if (descrip != null && !descrip.equals("")) {
			return Integer.parseInt(descrip);
		} else
			return -1;
	}

	public int buscar(List<StepMeta> lista, Class<?> clase,
			List<Integer> encontrados) {
		int i = 0;
		int pos = -1;
		while (i < lista.size() && pos == -1) {
			if (lista.get(i).getStepMetaInterface().getClass().equals(clase)) {
				if (!encontrados.contains(i)) {
					pos = i;
				}
			}
			i++;
		}
		return pos;
	}

	private String getEncoding(String dir) throws Exception {
		try {
			String encoding = "UTF-8";
			File file = new File(dir);
			FileInputStream fis = new FileInputStream(file.getPath());
			byte[] contenido = new byte[(int) file.length()];
			fis.read(contenido);
			byte[] data = contenido;
			CharsetDetector detector = new CharsetDetector();
			detector.setText(data);
			CharsetMatch cm = detector.detect();
			encoding = cm.getName();
			if (!encoding.equals("UTF-8")) {
				encoding = "windows-1252";
			}
			fis.close();
			return encoding;
		} catch (Exception e) {
			throw e;
		}

	}

}
