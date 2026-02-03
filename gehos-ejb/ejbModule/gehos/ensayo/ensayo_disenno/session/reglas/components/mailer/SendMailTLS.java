package gehos.ensayo.ensayo_disenno.session.reglas.components.mailer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.faces.context.FacesContext;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;

import com.sun.mail.util.MailSSLSocketFactory;

@AutoCreate
@Name("sendMailTLS")
public class SendMailTLS {

	private String username, password, smtpServer;
	private Integer smtpPort;
	private boolean debug = false;
	private Properties props;
	private MailSSLSocketFactory socketFactory = null;
	private static final String expRMail = "(^[0-9a-zA-Z]+(?:[._][0-9a-zA-Z]+)*)@([0-9a-zA-Z]+(?:[._-][0-9a-zA-Z]+)*\\.[0-9a-zA-Z]{2,3})$";
	String trustStorePath, trustStorePass;
	
	
	public void PutParams(String username, String password, String smtpServer, Integer smtpPort) {
		this.username = username;
		this.password = password;
		this.smtpServer = smtpServer;
		this.smtpPort = smtpPort;
	}
	
	private void Properties() throws Exception 
	{
		this.props = new Properties();
		this.props.put("mail.smtp.host", this.smtpServer);
		this.props.put("mail.smtp.port", this.smtpPort);
		this.props.put("mail.smtp.auth", "true");
		this.props.put("mail.smtp.starttls.enable", "false");
		this.props.put("mail.smtp.socketFactory.port", this.smtpPort);
		this.props.put("mail.smtp.ssl.socketFactory", this.socketFactory);
		this.props.put("mail.debug", this.debug);
		
		System.setProperty("javax.net.ssl.trustStore", trustStorePath);
		System.setProperty("javax.net.ssl.trustStorePassword", trustStorePass);
	}

	private void SetTrust() {
		this.socketFactory = null;
		try {
			socketFactory = new MailSSLSocketFactory();
		} catch (GeneralSecurityException e1) {

			e1.printStackTrace();
		}
		this.socketFactory.setTrustAllHosts(true);
	}

	/**
	 * @param from
	 *            quien envia
	 * @param to
	 *            lista de usuarios a quien va
	 * @param subject
	 *            asunto
	 * @param text
	 *            texto del mensaje
	 * @param user
	 *            user del server
	 * @param pass
	 *            password del user en el server
	 * @param attachFile1
	 *            url en el server primer adjunto
	 * @param attachFile2
	 *            url en el server segundo adjunto
	 * @param pathPlantillaIMG
	 *            url en el server de la imagen de firma
	 * @return true en caso de que se envie, false en caso contrario
	 */
	public void Send(List<String> to, String subject, String text, List<File> files)
			throws Exception 
	{

		// Proveedor certificado
		SetTrust();
		// Propiedades
		Properties();
		// Creacion de la session
		Session session = Session.getInstance(this.props, null);

		// Creacion del mensaje
		Message mensaje = new MimeMessage(session);
		mensaje.setFrom(new InternetAddress(username));
		for (String dirMail : to) {
			if (dirMail.matches(expRMail))
				mensaje.addRecipient(Message.RecipientType.TO,
						new InternetAddress(dirMail));
		}
		
		mensaje.setSubject(subject);
		
		Multipart mp = new MimeMultipart("related");
		
		BodyPart texto = new MimeBodyPart();
		texto.setContent(text, "text/html");
		mp.addBodyPart(texto);
		
		if(files!=null && files.size()>0)
		{			
			for (File f : files) 			
			{
				MimeBodyPart attachments = new MimeBodyPart();
				attachments.attachFile(f);
				attachments.setFileName(f.getName());
				mp.addBodyPart(attachments);
			}			
		}			
		
		mensaje.setContent(mp);
				
		Transport transport = session.getTransport("smtp");
		transport.connect(this.smtpServer, this.smtpPort, this.username,
				this.password);
		transport.sendMessage(mensaje, mensaje.getAllRecipients());
		transport.close();

	}

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getTrustStorePass() {
		return trustStorePass;
	}

	public void setTrustStorePass(String trustStorePass) {
		this.trustStorePass = trustStorePass;
	}

	public String getTrustStorePath() {
		return trustStorePath;
	}

	public void setTrustStorePath(String trustStorePath) {
		this.trustStorePath = trustStorePath;
	}

}
