package gehos.ensayo.ensayo_disenno.session.reglas;

import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

/**
 * This class was created to solve the problem of the <s:fileUpload> which don't
 * work inside a <a4j:form> which is used and it's necessary to the rule
 * execution. <rich:fileUpload> component is used instead of <s:fileUpload>
 * 
 * @author yoandry
 * 
 */
@Name("ruleFileUpload")
@Scope(ScopeType.CONVERSATION)
public class RuleFileUpload {

	@In(value = "gestionarHoja", required = false)
	GestionarHoja gestionarHoja;

	@SuppressWarnings("resource")
	public void listener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		byte[] data = item.getData();// this is not the way, returns null
		data = new byte[(int) item.getFile().length()];
		File f = item.getFile();
		InputStream is = new FileInputStream(f);
		BufferedInputStream bis = new BufferedInputStream(is);
		bis.read(data);
		String id = event.getComponent().getId();
		id = id.replace("var", "");
		String idSection = id.substring(0, 19);
		String idVariable = id.substring(19);
		if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty() && this.gestionarHoja.getMapWD().containsKey(Long.parseLong(idSection)) && this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)) != null && this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData() != null && !this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData().isEmpty() && this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData().containsKey(Long.parseLong(idVariable)) && this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData().get(Long.parseLong(idVariable)) != null){
			this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData().get(Long.parseLong(idVariable)).setData(data);
			this.gestionarHoja.getMapWD().get(Long.parseLong(idSection)).getData().get(Long.parseLong(idVariable)).setFileName(item.getFileName());
		}
	}

}