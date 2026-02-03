package gehos.configuracion.componenteetl.controladoras;

import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.componenteetl.auxiliares.Conexion;
import gehos.configuracion.componenteetl.auxiliares.FExcel;
import gehos.configuracion.componenteetl.auxiliares.Transformacion;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

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

@Name("ejecutarTransformacion")
@Scope(ScopeType.SESSION)
public class EjecutarTransformacion {
	@In
	TransformacionManager transfManager;
	@In
	IBitacora bitacora;
	private Transformacion seleccionada;
	private Trans transformacion;
	private String salida;

	public EjecutarTransformacion() {

	}

	public Transformacion getSeleccionada() {
		return seleccionada;
	}

	public void setSeleccionada(Transformacion seleccionada) {
		this.seleccionada = seleccionada;
	}

	public String getSalida() {
		return salida;
	}

	private Boolean esEjecutable() throws SQLException, ClassNotFoundException {
		try {
			// Conexion correcta?
			Conexion aux = seleccionada.getCon();
			String cc = "jdbc:postgresql://" + aux.getNombreServer() + ":"
					+ aux.getPuerto() + "/" + aux.getNombreBD();
			Class.forName("org.postgresql.Driver").newInstance();
			Connection conexion = DriverManager.getConnection(cc,
					aux.getUser(), aux.getPass());
			if (conexion == null) {
				transfManager.addLog("No se puede ejecutar la transformación '"
						+ seleccionada.getNombre()
						+ "'. Imposible conectar con la BD.", true);
				return false;
			}
			conexion.close();
			// Existe fichero excel?
			FExcel excelaux = seleccionada.getForigen();
			File faux = new File(excelaux.getDir());
			if (!faux.exists()) {
				transfManager.addLog("No se puede ejecutar la transformación '"
						+ seleccionada.getNombre()
						+ "'. La dirección del fichero excel no es válida.",
						true);
				return false;
			}
			return true;
		} catch (Exception e) {
			transfManager.addLog("No se puede ejecutar la transformación '"
					+ seleccionada.getNombre()
					+ "'. Imposible conectar con la BD y/o Excel.", true);
			return false;
		}

	}

	public List<Transformacion> getListaTransfA() {
		List<Transformacion> listdoTranformaciones = new ArrayList<Transformacion>();
		for (Transformacion transformacion : transfManager.getListadetransfs()) {
			if (transformacion.getNombre().equals("Secciones.ktr")
					|| transformacion.getNombre().equals("Grupos.ktr")
					|| transformacion.getNombre().equals("CRD.ktr")
					|| transformacion.getNombre().equals("Variables.ktr"))
				listdoTranformaciones.add(transformacion);

		}
		return listdoTranformaciones;
	}

	public void ejecutarA() {
		try {

			List<Transformacion> listadoTranformaciones = new ArrayList<Transformacion>();
			listadoTranformaciones = getListaTransfA();
			transfManager.clearLog();
			if (esEjecutable()) {

				for (int i = 0; i < listadoTranformaciones.size(); i++) {
					if (esEjecutable()) {
						transfManager.addLog("Ejecutando la transformación '"
								+ listadoTranformaciones.get(i).getNombre()
								+ "'... ", true);
						transformacion = listadoTranformaciones.get(i)
								.getTransformacion();
						transformacion.setLogLevel(LogLevel.BASIC);
						transformacion.execute(new String[0]);
						transformacion.waitUntilFinished();
						Result resultado = transformacion.getResult();
						int errors = (int) (resultado.getNrErrors() + errores());
						salida = "La transformación "
								+ listadoTranformaciones.get(0).getNombre()
								+ " se ejecutó "
								+ (errors == 0 ? "satisfactoriamente." : "con "
										+ errors + " error(es).");

						writeToLog();
						transfManager.addLog(salida, true);
						bitacora.registrarInicioDeAccion("Ejecutando la transformación: "
								+ listadoTranformaciones.get(0).getNombre());

					}

				}
				transfManager.setEjecutarvicible(false);

			} else
				salida = "No se puede ejecutar la transformación '"
						+ listadoTranformaciones.get(0).getNombre() + "'.";

		} catch (Exception e) {
			transfManager.addLog(e.getMessage(), true);
		}
	}

	public String exportAccion() {
		String pathExportedReport = "/resources/modEnsayo/plantillacrd/plantilla.xls";

		if (!pathExportedReport.equals("")) {
			return "window.open('"
					+ FacesContext.getCurrentInstance().getExternalContext()
							.getRequestContextPath() + pathExportedReport
					+ "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}

	public String exportAccionPesquisaje() {
		String pathExportedReport = "/resources/modEnsayo/plantillacrd/plantilla_Pesquisaje.xls";

		if (!pathExportedReport.equals("")) {
			return "window.open('"
					+ FacesContext.getCurrentInstance().getExternalContext()
							.getRequestContextPath() + pathExportedReport
					+ "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}
	
	public String exportEventoAderso() {
		String pathExportedReport = "/resources/modEnsayo/plantillacrd/plantilla_EA.xls";

		if (!pathExportedReport.equals("")) {
			return "window.open('"
					+ FacesContext.getCurrentInstance().getExternalContext()
							.getRequestContextPath() + pathExportedReport
					+ "'); Richfaces.hideModalPanel('exportPanel')";
		}
		return "return false;";
	}

	public boolean cbDescargar() {

		try {
			boolean retorno = true;
			FacesContext ctx;
			ServletContext request;
			File archCSV;
			FileInputStream fisArch;
			byte[] bytes;
			int leer;
			String nombreArchivo;

			ctx = FacesContext.getCurrentInstance();
			request = (ServletContext) ctx.getExternalContext().getContext();
			archCSV = new File(
					request.getRealPath("/resources/modEnsayo/plantillacrd/plantilla.xls"));
			fisArch = new FileInputStream(archCSV);
			bytes = new byte[10];
			leer = 0;

			if (!ctx.getResponseComplete()) {

				nombreArchivo = archCSV.getName();
				String contentType = "application/vnd.ms-excel";
				HttpServletResponse response = (HttpServletResponse) ctx
						.getExternalContext().getResponse();

				response.setContentType(contentType);
				response.setHeader("Content-Disposition",
						"attachment;filename=\"" + nombreArchivo + "\"");
				ServletOutputStream out = response.getOutputStream();

				while ((leer = fisArch.read(bytes)) != -1) {
					out.write(bytes, 0, leer);
				}

				out.flush();
				out.close();
				System.out.println("\nLayout descargado...\n");
				ctx.responseComplete();
			}
			return retorno;

		} catch (Exception e) {
			// TODO: handle exception
			return false;
		}

	}

	public void ejecutar() {
		try {
			List<Transformacion> listadoTranformaciones = new ArrayList<Transformacion>();
			listadoTranformaciones = getListaTransfA();

			transfManager.clearLog();
			if (esEjecutable()) {
				transfManager.addLog("Ejecutando la transformación '"
						+ seleccionada.getNombre() + "'... ", true);
				transformacion = seleccionada.getTransformacion();
				transformacion.setLogLevel(LogLevel.BASIC);
				transformacion.execute(new String[0]);
				transformacion.waitUntilFinished();
				Result resultado = transformacion.getResult();
				int errors = (int) (resultado.getNrErrors() + errores());
				salida = "La transformación "
						+ seleccionada.getNombre()
						+ " se ejecutó "
						+ (errors == 0 ? "satisfactoriamente." : "con "
								+ errors + " error(es).");

				writeToLog();
				transfManager.addLog(salida, true);
				bitacora.registrarInicioDeAccion("Ejecutando la transformación: "
						+ seleccionada.getNombre());
			} else
				salida = "No se puede ejecutar la transformación '"
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
					linea = linea.concat(") : ");
				}
				linea = linea.concat(separado[i]);
				transfManager.addLog(linea, true);
			}
		} else {
			separado = new String[] { objeto != null ? objeto.toString()
					: "<null>" };
			transfManager.addLog(separado[0], true);
		}
	}

}
