package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions;

import gehos.comun.datoslab.entity.Persona;
import gehos.ensayo.ensayo_disenno.session.reglas.components.mailer.MailSender;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.dependencies.GruposujetosList;
import gehos.ensayo.entity.Usuario_ensayo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;


@Name("SendEmailRuleAction")
@Scope(ScopeType.CONVERSATION)
@SuppressWarnings({"unchecked","rawtypes"})
public class SendEmailRuleAction extends IRuleAction{

	@In
	FacesMessages facesMessages;
	
	@In(create=true)
	GruposujetosList listadosujetosList;
		
	@In(create = true) 
	MailSender mailer;
	
	//String message;
	String msg;
	String subject;    	
	String destinations;
	
	public String getName() {
		return "send_email";
	}
	public String getLabel() {		
		return "Enviar email";
	}
	public boolean Execute() 
	{		
		List<String> to = new ArrayList<String>();
		
		to.addAll(parseDestinations());
		to.addAll(findUserEmails());
		
		if(to.size()>0)
		{	
			mailer.Send(subject, msg, to, null);
			return true;
		}
		return false;
	}
	@Override
	public boolean Validate() 
	{
		boolean hasParsedDestinations = parseDestinations().size() > 0;
		boolean hasListDestinations = usuariosSeleccionados.size() > 0;
		if(!hasParsedDestinations && !hasListDestinations)
		{
			facesMessages.add("Debe adicionar al menos un destinatario.");
			return false;
		}
		
		return true;
	}
	
	private List<String> parseDestinations()
	{
		List<String> to = new ArrayList<String>();
		if(destinations!=null && !destinations.trim().isEmpty())
		{
			destinations=destinations.trim();
			String[] arr = destinations.split(";");
			for (String e : arr) 			
				if(e.matches(MailSender.MAIL_REGEX))
					to.add(e);
			
		}
		return to;
	}
	
	private List<String> findUserEmails()
	{
		List<String> to = new ArrayList<String>();
		for (Usuario_ensayo user : usuariosSeleccionados) //this should be emails instead of user names
		{
			Persona p = entityManager.find(Persona.class, user.getIdPersona());
			String e = p.getCorreoElectronico();
			if(e!=null && e.matches(MailSender.MAIL_REGEX))
				to.add(e);
		}
		return to;
	}
	
	@Override
	public String Resume() {		
		return "Se enviar\u00E1 correo a " + (usuariosSeleccionados.size() + parseDestinations().size()) + " usuarios";
	}
	@Override
	public String getDescription() {
		return "Env\u00EDa un email a los usuarios seleccionados";
	}		
	@Override
	public String InstanceDescription() {		
		return "Enviar correo " + (usuariosSeleccionados.size() + parseDestinations().size()) + " usuarios";
	}
	
	@Override
	public void LoadComponentState() {
		if(super.state!=null && !super.state.isEmpty())
		{
			usuariosSeleccionados = new ArrayList<Usuario_ensayo>();
			List<Long> ids = (List<Long>)super.state.get("users");
			for (Long l : ids) {
				Usuario_ensayo user = (Usuario_ensayo)entityManager.find(Usuario_ensayo.class, l);
				usuariosSeleccionados.add(user);
			}
			
			msg  = (String)state.get("message");
			subject  = (String)state.get("subject");
			destinations  = (String)state.get("destinations");
			
		}
	}
	@Override
	public Map<String, Object> GetComponentState() 
	{
		Map<String, Object> state = new HashMap<String, Object>();		
		List<Long> ids = new ArrayList<Long>();
		for (Usuario_ensayo e : usuariosSeleccionados) 
			ids.add(e.getId());	
				
		state.put("users", ids);
		state.put("message", msg);
		state.put("subject", subject);
		state.put("destinations", destinations);
		return state;
	}
	
	@Override
	public void setParams(Object... objects) {
		String stringUsers = objects[0].toString();
		String[] usersEmails = stringUsers.trim().split(",");
		for (String string : usersEmails) //this should be emails instead of user names
		{
			Usuario_ensayo user = (Usuario_ensayo)entityManager.createQuery("select u from Usuario_ensayo u where u.username=:pid").setParameter("pid", string).getSingleResult();
			Persona p = entityManager.find(Persona.class, user.getIdPersona());
			String correo = p.getCorreoElectronico();
			//
		}
		//dispatch the list of emails
	}
		
	@Override
	public Map getExtra() {		
		Map m = new HashMap();		
		return m;
	}
	
	List<Usuario_ensayo> usuariosSeleccionados;
	Integer[] listaids;
	@In EntityManager entityManager;
	
	@Create
	@Begin(flushMode=FlushModeType.MANUAL, join=true)
	public void init()
	{		
		usuariosSeleccionados = new ArrayList<Usuario_ensayo>();
		listadosujetosList.setNombreUsuario("");		
	}
	public void mark(Usuario_ensayo u)
	{
		if(ismarked(u))
			usuariosSeleccionados.remove(u);
		else 
			usuariosSeleccionados.add(u);
	}
	
	public boolean ismarked(Usuario_ensayo u)
	{
		return usuariosSeleccionados.contains(u);
	}
	
	public List<Usuario_ensayo> getUsuariosSeleccionados() {
		return usuariosSeleccionados;
	}
	public void setUsuariosSeleccionados(List<Usuario_ensayo> usuariosSeleccionados) {
		this.usuariosSeleccionados = usuariosSeleccionados;
	}
	public Integer[] getListaids() {
		return listaids;
	}
	public void setListaids(Integer[] listaids) {
		this.listaids = listaids;
	}
	/*public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}*/
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public GruposujetosList getListadosujetosList() {
		return listadosujetosList;
	}
	public void setListadosujetosList(GruposujetosList listadosujetosList) {
		this.listadosujetosList = listadosujetosList;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public String getDestinations() {
		return destinations;
	}
	public void setDestinations(String destinations) {
		this.destinations = destinations;
	}

	@Override
	public int orderPosition() {		
		return 4;
	}
}
