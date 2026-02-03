package gehos.ensayo.ensayo_disenno.componenteetl.controladoras;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.ConexionDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.FExcelDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.TransformacionDisenno;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.di.core.logging.KettleLoggingEvent;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.core.logging.LoggingBuffer;
import org.pentaho.di.core.logging.LogMessage;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaDataCombi;
import org.pentaho.di.trans.steps.writetolog.WriteToLogMeta;
import org.pentaho.di.version.BuildVersion;
import org.pentaho.di.core.Const;

@Name("ejecutarTransformacionDisenno")
@Scope(ScopeType.SESSION)
public class EjecutarTransformacionDisenno {
	@In
	TransformacionManagerDisenno transfManager;
	@In
	IBitacora bitacora;
	private TransformacionDisenno seleccionada;
	private Trans transformacion;
	private String salida;

	public EjecutarTransformacionDisenno() {

	}

	public TransformacionDisenno getSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(TransformacionDisenno seleccionada) {
		this.seleccionada = seleccionada;
	}

	public String getSalida() {
		return salida;
	}

	private Boolean esEjecutable() throws SQLException, ClassNotFoundException {
		try {
			// Conexion correcta?
			ConexionDisenno aux = seleccionada.getCon();
			String cc = "jdbc:postgresql://" + aux.getNombreServer() + ":"
					+ aux.getPuerto() + "/" + aux.getNombreBD();
			Class.forName("org.postgresql.Driver").newInstance();
			Connection conexion = DriverManager.getConnection(cc,
					aux.getUser(), aux.getPass());
			if (conexion == null) {
				transfManager.addLog("No se puede ejecutar la transformaci\u00F3n '"
						+ seleccionada.getNombre()
						+ "'. Imposible conectar con la BD.", true);
				return false;
			}
			conexion.close();
			// Existe fichero excel?
			FExcelDisenno excelaux = seleccionada.getForigen();
			File faux = new File(excelaux.getDir());
			if (!faux.exists()) {
				transfManager.addLog("No se puede ejecutar la transformaci\u00F3n '"
						+ seleccionada.getNombre()
						+ "'. La direcci\u00F3n del fichero excel no es v\u00E1lida.",
						true);
				return false;
			}
			return true;
		} catch (Exception e) {
			transfManager.addLog("No se puede ejecutar la transformaci\u00F3n '"
					+ seleccionada.getNombre()
					+ "'. Imposible conectar con la BD y/o Excel.", true);
			return false;
		}

	}

	public void ejecutar(){
		try {
			transfManager.clearLog();
			if (esEjecutable()) {
				transfManager.addLog("Ejecutando la transformaci\u00F3n '"
						+ seleccionada.getNombre() + "'... ", true);
				transformacion = seleccionada.getTransformacion();
				transformacion.setLogLevel(LogLevel.BASIC);
				transformacion.execute(new String[0]);
				transformacion.waitUntilFinished();
				Result resultado = transformacion.getResult();
				int errors = (int) (resultado.getNrErrors() + errores());
				salida = "La transformaci\u00F3n "
						+ seleccionada.getNombre()
						+ " se ejecut\u00F3 "
						+ (errors == 0 ? "satisfactoriamente." : "con "
								+ errors + " error(es).");

				writeToLog();
				transfManager.addLog(salida, true);
				bitacora.registrarInicioDeAccion("Ejecutando la transformaci\u00F3n: "
						+ seleccionada.getNombre());
			} else
				salida = "No se puede ejecutar la transformaci\u00F3n '"
						+ seleccionada.getNombre() + "'.";

		} catch (Exception e) {
			transfManager.addLog(e.getMessage(), true);
		}
	}

	private int errores() {
		List<Integer> encontrados = new ArrayList<Integer>();
		List<StepMetaDataCombi> lstepmc = transformacion.getSteps();
		List<StepMeta> lstepm = new ArrayList<StepMeta>();
		for (StepMetaDataCombi stepmc : lstepmc) {
			lstepm.add(stepmc.stepMeta);
		}
		int errors = 0;
		int pos = -1;
		do {
			pos = seleccionada
					.buscar(lstepm, WriteToLogMeta.class, encontrados);
			if (pos != -1)
				encontrados.add(pos);
		} while (pos != -1);
		for (Integer i : encontrados) {
			errors += lstepmc.get(i).step.getLinesWritten();
		}
		return errors;
	}

	private void writeToLog() {
		LoggingBuffer appender = KettleLogStore.getAppender();
		List<KettleLoggingEvent> eventos = appender.getLogBufferFromTo(
				transformacion.getLogChannelId(), false, 0,
				appender.getLastBufferLineNr());
		for (KettleLoggingEvent evento : eventos) {
			formato(evento);
		}
	}

	private void formato(KettleLoggingEvent evento) {
		String[] separado = null;
		LogMessage mensaje;
		Object objeto = evento.getMessage();
		if (objeto instanceof LogMessage) {
			mensaje = (LogMessage) objeto;
			separado = mensaje.getMessage() == null ? new String[] {} : mensaje
					.getMessage().split(Const.CR);
			String linea;
			for (int i = 0; i < separado.length; i++) {
				linea = "";
				if (mensaje.getSubject() != null) {
					linea = linea.concat(mensaje.getSubject());
					if (mensaje.getCopy() != null) {
						linea = linea.concat(".").concat(mensaje.getCopy());
					}
					linea = linea.concat(" - ");
				}
				if (i == 0 && mensaje.isError()) {
					BuildVersion buildVersion = BuildVersion.getInstance();
					linea = linea.concat("ERROR");
					linea = linea.concat(" (version ");
					linea = linea.concat(buildVersion.getVersion());
					if (!Const.isEmpty(buildVersion.getRevision())) {
						linea = linea.concat(", build ");
						linea = linea.concat(buildVersion.getRevision());
					}
					if (!Const.isEmpty(buildVersion.getBuildDate())) {
						linea = linea.concat(" from ");
						linea = linea.concat(buildVersion.getBuildDate());
					}
					if (!Const.isEmpty(buildVersion.getBuildUser())) {
						linea = linea.concat(" by ");
						linea = linea.concat(buildVersion.getBuildUser());
					}
					linea = linea.concat( ") : " );
				}
				linea = linea.concat(separado[i]);
				transfManager.addLog(linea, true);
			}
		} else{
			separado = new String[] { objeto != null ? objeto.toString()
					: "<null>" };
			transfManager.addLog(separado[0], true);
		}
	}

}
