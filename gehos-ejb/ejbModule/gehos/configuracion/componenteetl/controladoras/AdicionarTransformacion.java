package gehos.configuracion.componenteetl.controladoras;

import gehos.configuracion.componenteetl.auxiliares.FXML;
import gehos.configuracion.componenteetl.auxiliares.Transformacion;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.pentaho.di.core.exception.KettleXMLException;

@Name("addTransf")
@Scope(ScopeType.SESSION)
public class AdicionarTransformacion {

	@In
	TransformacionManager transfManager;

	private FXML ficheroXML;
	private List<FXML> listatemporal;
	private int errores;

	public AdicionarTransformacion() {
		ficheroXML = new FXML();
		listatemporal = new ArrayList<FXML>();
		errores = 0;
	}

	public FXML getFicheroXML() {
		return ficheroXML;
	}

	public void setFicheroXML(FXML ficheroXML) {
		this.ficheroXML = ficheroXML;
	}

	public List<FXML> getListatemporal() {
		return listatemporal;
	}

	public void cancelListaT(int pos) {
		listatemporal.remove(pos);
	}

	public void addListaT() {
		if (ficheroXML.getData() != null) {
			listatemporal.add(new FXML(ficheroXML.getNombre(), ficheroXML
					.getData()));
			ficheroXML = new FXML();
		}
	}

	private Transformacion subirTransf() {
		String rootpath = transfManager.getDirTransf() + File.separator
				+ ficheroXML.getNombre();
		File file = null;
		try {
			file = new File(rootpath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(ficheroXML.getData());
			dataOutputStream.flush();
			dataOutputStream.close();
			fileOutputStream.close();
			ficheroXML = new FXML(file.getPath());
			ficheroXML.setNombre(file.getName());
			Transformacion transf = new Transformacion(ficheroXML);
			ficheroXML = new FXML();
			return transf;
		} catch (KettleXMLException e) {
			errores++;
			addLog("No se ha podido agregar la transformación '"
					+ ficheroXML.getNombre() + "'. Fichero corrupto.");
			if(file != null && file.exists())
				file.delete();
			return null;
		} catch (Exception e) {
			addLog(e.getMessage());
			return null;
		}
	}
	
	public void adicionarTransf(Transformacion transf) throws Exception {
		try {
			transfManager.agregar(transf);
			addLog("Se ha añadido satisfactoriamente la transformación '"
					+ transf.getNombre() + "'.");
		} catch (Exception e) {
			throw e;
		}
	}

	public void adicionarLista() {
		while (!listatemporal.isEmpty()) {
			this.ficheroXML = listatemporal.get(0);
			Transformacion transf = subirTransf();
			if (transf != null) {
				try {
					adicionarTransf(transf);
				} catch (Exception e) {
					addLog(e.getMessage());
				}
			}
			cancelListaT(0);
		}
	}

	public void terminar() {
		ficheroXML = new FXML();
		listatemporal = new ArrayList<FXML>();
	}

	public String getMensaje() {
		if (errores == 0) {
			return "Las transformaciones fueron agregadas satisfactoriamente.";
		} else {
			String msg = "Hubieron "
					+ errores
					+ " tranformaciones que no fueron agregadas. Consultar opcion 'Ver registro' para mas detalles.";
			errores = 0;
			return msg;
		}
	}

	private void addLog(String log) {
		transfManager.addLog(log, true);
	}

}
