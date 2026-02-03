package gehos.ensayo.ensayo_disenno.session.reglas.components.mailer;

import gehos.comun.shell.IActiveModule;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("mailer")
@Scope(ScopeType.CONVERSATION)
public class MailSender
{
	public static final String MAIL_REGEX = "(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$";

	// Inyecciones
	@In(create = true)
	SendMailTLS sendMailTLS;
	
	@In MailConf mailConf;
	@In(create = true)
	IActiveModule activeModule;
	@In	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;

	public void Send()
	{
		sendMailTLS.PutParams("yoandrygc@uci.cu", "G3nius4ever", "smtp.uci.cu", 25);
		
		List<File> attachments = new ArrayList<File>();
		attachments.add(new File("/home/yoandry/Escritorio/sigeccode/estadisticas/gehos-ejb/ejbModule/gehos/ensayo/ensayo_disenno/session/reglas/ffsdf.java"));
		attachments.add(new File("/home/yoandry/Escritorio/sigeccode/estadisticas/gehos-ejb/ejbModule/gehos/ensayo/ensayo_disenno/session/reglas/Framework.java"));
		
		List<String> to = new ArrayList<String>();
		to.add("yoandrygc@uci.cu");
		try 
		{
			sendMailTLS.Send(to, "Testing SIGEC","It's a test", attachments	);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void Send(String subject, String message, List<String> to, List<File> files)
	{		
		sendMailTLS.PutParams(mailConf.getEmail(), mailConf.getPassword(), mailConf.getServer(), mailConf.getPort());		
		FacesContext facesContext = FacesContext.getCurrentInstance();
		String path = ((ServletContext)facesContext.getExternalContext().getContext()
				).getRealPath(mailConf.getTrustStorePath());
		sendMailTLS.setTrustStorePath(path);
		sendMailTLS.setTrustStorePass(mailConf.getTrustStorePass());
		try 
		{
			sendMailTLS.Send(to, subject, message, files);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}		
}