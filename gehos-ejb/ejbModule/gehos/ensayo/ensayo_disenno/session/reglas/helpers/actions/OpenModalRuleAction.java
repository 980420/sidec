package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions;

import gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.Notificacion;
import gehos.ensayo.ensayo_disenno.session.reglas.ReglaPlayer;
import gehos.ensayo.ensayo_disenno.session.reglas.examples.ModalExtraSheetData;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.ensayo_disenno.session.reporteExpedito.ReporteExpeditoConduccion;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Context;
import org.jboss.seam.contexts.Contexts;
import org.jboss.seam.faces.FacesMessages;

@Name("OpenModalRuleAction")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings({"unchecked","rawtypes"})
public class OpenModalRuleAction extends IRuleAction{
	
	@In
	FacesMessages facesMessages;

	@In EntityManager entityManager;
	
	@In(create=true)
	IdUtil idUtil;
	
	String directory = "/modEnsayo/ensayo_disenno/codebase/reglas/actionModals";
	String pathFileContent; //path to the file that will be included in the modal container
	String fileName;
	Variable_ensayo variable;
	public String getName() {
		return "open_modal";
	}
	public String getLabel() {
			return "Mostrar ventana flotante";
	}
	@Override
	public String getDescription() {
		return "Muestra una ventana flotante";
	}
	
	public List<String> listFiles()
	{
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
		String path = context.getRealPath(directory);
		File directory = new File(path);
		String[] files = directory.list(new FilenameFilter() {
			
			@Override
			public boolean accept(File arg0, String arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		List<String> list = new ArrayList<String>(Arrays.asList(files));
		return list;
	}
	
	@Override
	public String ClientSideCode() 
	{
		return String.format("Richfaces.showModalPanel('%s')","modalContainer");
	}
	
	@Override
	public boolean RequiresUserInput() {
		return true;
	}
	@Override
	public boolean Execute() 
	{		
		Context convContext = Contexts.getConversationContext();
		Notificacion o = (Notificacion)Component.getInstance(Notificacion.class,true);
		convContext.set(IActionWithUserInput.actionManagerName, o);
		o.setVariableRegla(this.rootVariable);
		ReglaPlayer rp = (ReglaPlayer) Component.getInstance(ReglaPlayer.class);
		rp.setLastUserInputAction(this);
		o.initConversation();
		return true;
	}
	
	@Override
	public boolean Validate() 
	{		
		if(pathFileContent==null || pathFileContent.trim().isEmpty())
		{
			facesMessages.add("Debe seleccionar un formulario.");
			return false;
		}
		
		return true;
	}
		
	@Override
	public String Resume() {		
		return String.format("Se mostrar\u00E1 la ventana flotante con formulario [%s]",fileName);
	}
	
	@Override
	public String InstanceDescription() {		
		return String.format("Mostrar ventana flotante con formulario [%s]",fileName);
	}
	
	@Override
	public void LoadComponentState() {
		if(super.state!=null && !super.state.isEmpty())
		{
			pathFileContent = super.state.get("filepath").toString();
			File f = new File(pathFileContent);
			fileName = f.getName();
		}
	}
	
	public Map<String, Object> GetComponentState() 
	{
		Map<String, Object> state = new HashMap<String, Object>();
		state.put("filepath", pathFileContent);		
		return state;
	}
	
	@Override
	public String IdToRerender() {					
		return "modalContainerDiv";
	}
		
	public String getPathFileContent() {
		return pathFileContent;
	}
	public void setPathFileContent(String pathFileContent) {
		this.pathFileContent = pathFileContent;
	}
	
	public void SetFileName(String fileName) {
		this.fileName = fileName;
		this.pathFileContent = directory + File.separator + fileName;
	}
	
	public boolean FileExist()
	{
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext.getExternalContext().getContext();
		String path = context.getRealPath(directory);
		File file = new File(path,fileName);
		
		return file.exists();
		
	}
	
	@Override
	public int orderPosition() {		
		return 5;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}