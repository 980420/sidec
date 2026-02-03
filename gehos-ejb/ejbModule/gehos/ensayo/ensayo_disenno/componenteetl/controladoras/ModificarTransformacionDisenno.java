package gehos.ensayo.ensayo_disenno.componenteetl.controladoras;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.ConexionDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.FExcelDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.TransformacionDisenno;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("editTransfDisenno")
@Scope(ScopeType.SESSION)
public class ModificarTransformacionDisenno {

	@In
	TransformacionManagerDisenno transfManager;
	@In (create = true)
	FacesMessages facesMessages;

	private ConexionDisenno con;
	private FExcelDisenno forigen;
	private byte[] data;

	private TransformacionDisenno transfSel;
	private String dirExcels = System.getProperty("jboss.server.home.dir")
			+ File.separator + "deploy" + File.separator + "gehos-ear.ear"
			+ File.separator + "componente_etl" + File.separator + "excels";

	public ModificarTransformacionDisenno() {
		con = new ConexionDisenno();
		forigen = new FExcelDisenno();
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public ConexionDisenno getCon() {
		return con;
	}

	public void setCon(ConexionDisenno con) {
		this.con = con;
	}

	public FExcelDisenno getForigen() {
		return forigen;
	}

	public void setForigen(FExcelDisenno forigen) {
		this.forigen = forigen;		
	}

	public TransformacionDisenno getTransfSel() {
		return transfSel;
	}

	public void setTransfSel(TransformacionDisenno transfSel) {
		this.transfSel = transfSel;
		con = transfSel.getCon();
		forigen = transfSel.getForigen();
	}

	private void subirExcel() {
		String rootpath = dirExcels + File.separator + forigen.getNombre();
		try {
			File file = new File(rootpath);
			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);
			dataOutputStream.write(this.data);
			dataOutputStream.flush();
			dataOutputStream.close();
			fileOutputStream.close();
			forigen = new FExcelDisenno(file.getPath());
			forigen.setNombre(file.getName());
		} catch (IOException e) {
			addLog(e.getMessage());
		}

	}

	public String modificarTransf() {
		try {
			if(data.length == 0){
				facesMessages.addToControlFromResourceBundle("addExcel",Severity.ERROR,"Valor requerido");
				return null;
			}
			subirExcel();
			transfSel.setForigen(forigen);
			transfSel.setCon(con);
			transfManager.save(transfSel);
			addLog("La transformaci\u00F3n " + transfSel.getNombre()
					+ " ha sido modificada satisfactoriamente.");
			return "ok";
		} catch (Exception e) {
			addLog(e.getMessage());
			return null;
		}
	}

	private void addLog(String log) {
		transfManager.addLog(log, true);
	}


}
