package gehos.configuracion.componenteetl.controladoras;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import gehos.configuracion.componenteetl.auxiliares.Conexion;
import gehos.configuracion.componenteetl.auxiliares.FExcel;
import gehos.configuracion.componenteetl.auxiliares.Transformacion;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("editTransf")
@Scope(ScopeType.SESSION)
public class ModificarTransformacion {

	
	
	@In(create = true)
	TransformacionManager transfManager;
	
	
	
	@In(create = true)
	FacesMessages facesMessages;
	

	
	private Conexion con;
	private FExcel forigen;
	private byte[] data;

	private Transformacion transfSel;
	private String dirExcels = System.getProperty("jboss.server.home.dir")
			+ File.separator + "deploy" + File.separator + "gehos-ear.ear"
			+ File.separator + "componente_etl" + File.separator + "excels";

	public ModificarTransformacion() {
		con = new Conexion();
		forigen = new FExcel();
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public Conexion getCon() {
		return con;
	}

	public void setCon(Conexion con) {
		this.con = con;
	}

	public FExcel getForigen() {
		return forigen;
	}

	public void setForigen(FExcel forigen) {
		this.forigen = forigen;		
	}

	public Transformacion getTransfSel() {
		return transfSel;
	}

	public void setTransfSel(Transformacion transfSel) {
		this.transfSel = getListaTransfA().get(0);
		con = getListaTransfA().get(0).getCon();
		forigen = getListaTransfA().get(0).getForigen();
	}
	
	public void cargaInicial(){
		try {
			transfManager.cargaInicial();
			this.transfSel = getListaTransfA().get(0);
			con = getListaTransfA().get(0).getCon();
			forigen = getListaTransfA().get(0).getForigen();
		} catch (Exception e) {
			addLog(e.getMessage(), true);
		}
	}
	private void addLog(String log, Boolean time) {
		transfManager.addLog(log, time);
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
			forigen = new FExcel(file.getPath());
			forigen.setNombre(file.getName());
		} catch (IOException e) {
			addLog(e.getMessage());
		}

	}
	public List<Transformacion> getListaTransfA(){
		List<Transformacion> listdoTranformaciones= new ArrayList<Transformacion>();
		for (Transformacion transformacion : transfManager.getListadetransfs()) {
			if(transformacion.getNombre().equals("Secciones.ktr") || transformacion.getNombre().equals("Grupos.ktr") || transformacion.getNombre().equals("CRD.ktr") || transformacion.getNombre().equals("Variables.ktr") )
			listdoTranformaciones.add(transformacion);
			
		}		
		return listdoTranformaciones;
	}
	public String modificarTransfA() {
		try {
			
			if(data.length == 0){
				facesMessages.addToControlFromResourceBundle("addExcel",Severity.ERROR,"Valor requerido");
				return null;
			}
			subirExcel();
			for (Transformacion transformacion : getListaTransfA()) {
				transformacion.setForigen(forigen);
				transformacion.setCon(con);				
				transfManager.save(transformacion);
				addLog("La transformación " + transformacion.getNombre()
						+ " ha sido modificada satisfactoriamente.");
			}
			transfManager.setEjecutarvicible(true);
			
			return "ok";
		} catch (Exception e) {
			addLog(e.getMessage());
			return null;
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
			addLog("La transformación " + transfSel.getNombre()
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
