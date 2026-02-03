package gehos.ensayo.ensayo_disenno.componenteetl.controladoras;



import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.FXMLDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.TransformacionDisenno;

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

@Name("addTransfDisenno")
@Scope(ScopeType.SESSION)
public class AdicionarTransformacionDisenno {

	@In
	TransformacionManagerDisenno transfManager;

	private FXMLDisenno ficheroXML;
	private List<FXMLDisenno> listatemporal;
	private int errores;

	public AdicionarTransformacionDisenno() {
		ficheroXML = new FXMLDisenno();
		listatemporal = new ArrayList<FXMLDisenno>();
		errores = 0;
	}

	public FXMLDisenno getFicheroXML() {
		return ficheroXML;
	}

	public void setFicheroXML(FXMLDisenno ficheroXML) {
		this.ficheroXML = ficheroXML;
	}

	public List<FXMLDisenno> getListatemporal() {
		return listatemporal;
	}

	public void cancelListaT(int pos) {
		listatemporal.remove(pos);
	}

	public void addListaT() {
		if (ficheroXML.getData() != null) {
			listatemporal.add(new FXMLDisenno(ficheroXML.getNombre(), ficheroXML
					.getData()));
			ficheroXML = new FXMLDisenno();
		}
	}

	private TransformacionDisenno subirTransf() {
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
			ficheroXML = new FXMLDisenno(file.getPath());
			ficheroXML.setNombre(file.getName());
			TransformacionDisenno transf = new TransformacionDisenno(ficheroXML);
			ficheroXML = new FXMLDisenno();
			return transf;
		} catch (KettleXMLException e) {
			errores++;
			addLog("No se ha podido agregar la transformaci\u00F3n '"
					+ ficheroXML.getNombre() + "'. Fichero corrupto.");
			if(file != null && file.exists())
				file.delete();
			return null;
		} catch (Exception e) {
			addLog(e.getMessage());
			return null;
		}
	}
	
	public void adicionarTransf(TransformacionDisenno transf) throws Exception {
		try {
			transfManager.agregar(transf);
			addLog("Se ha a\u00F1adido satisfactoriamente la transformaci\u00F3n '"
					+ transf.getNombre() + "'.");
		} catch (Exception e) {
			throw e;
		}
	}

	public void adicionarLista() {
		while (!listatemporal.isEmpty()) {
			this.ficheroXML = listatemporal.get(0);
			TransformacionDisenno transf = subirTransf();
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
		ficheroXML = new FXMLDisenno();
		listatemporal = new ArrayList<FXMLDisenno>();
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
