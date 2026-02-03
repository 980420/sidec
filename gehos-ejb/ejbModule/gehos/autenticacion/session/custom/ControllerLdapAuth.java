package gehos.autenticacion.session.custom;

import gehos.autenticacion.entity.PersonLdap;
import java.util.Hashtable;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.persistence.EntityManager;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.Name;

@Name("controllerLdapAuth")
public class ControllerLdapAuth {
	
	private static final String CONTEXT = "com.sun.jndi.ldap.LdapCtxFactory";
	private static final String TIPO_AUTH = "simple";

	private String user;
	private String pass;
	private String domain;
	private String hostURL;
	private PersonLdap person;
	
	

	public ControllerLdapAuth() {
	
	}

	public ControllerLdapAuth(String user, String pass, String hostURL,
			String domain) {
		this.user = user;
		this.pass = pass;
		this.domain = domain;
		this.hostURL = hostURL;
		this.person = new PersonLdap();
	}

	public ControllerLdapAuth(String user, String pass, String domain) {
		this.user = user;
		this.pass = pass;
		this.domain = domain;
		this.person = new PersonLdap();
		findHostLDAP(domain);
	}

	private void findHostLDAP(String domain) {
		try {
			EntityManager entityManager = (EntityManager)Component.getInstance("entityManager");
			this.hostURL = (String) entityManager.createQuery("select l.host from Ldap l where l.active = true and l.domain= :dom").setParameter("dom", domain).getResultList().get(0);

		} catch (Exception ex) {
			this.hostURL = "";
		}

		// this.hostURL = "ldap://uci.cu";
		// this.hostURL = "ldap://192.168.2.10:389/DC=cce,DC=sld,DC=cu";
	}

	private DirContext context() throws NamingException {
		Hashtable<String, String> env = new Hashtable<String, String>();
		env.put(Context.INITIAL_CONTEXT_FACTORY, CONTEXT);
		env.put(Context.PROVIDER_URL, getHostURL());
		env.put(Context.SECURITY_AUTHENTICATION, TIPO_AUTH);
		env.put(Context.SECURITY_PRINCIPAL, getUser() + '@' + getDomain());
		env.put(Context.SECURITY_CREDENTIALS, getPass());
		env.put(Context.REFERRAL, "follow");
		return new InitialDirContext(env);
	}

	public boolean correctLogin() throws Exception{

		if (getPass().compareTo("") == 0 || getUser().compareTo("") == 0)
			return false;

		try {
			DirContext ctx = context();
			getPerson(ctx);
			ctx.close();
			System.out.println("Authentication successful");
			return true;

		} catch (AuthenticationException ex) {
			System.out.println("Authentication LDAP failed: " + ex.toString());
			
			return false;

		} catch (Exception ex) {
			System.out.println("LDAP failed: " + ex.toString());
			throw new Exception();
		}
	}

	private String argumentsDC() {

		StringBuffer result = new StringBuffer();
		String tmp_domain = getDomain();
		String[] split_domain = tmp_domain.split("\\.");
		int lenght = split_domain.length;

		if (lenght != 0) {
			int c = 0;
			String aux = "DC=";
			while (c != lenght) {
				if (c != 0)
					result.append(",");
				result.append(aux);
				result.append(split_domain[c]);
				c++;
			}
		}

		return result.toString();
	}

	private SearchControls searchCtls() {
		SearchControls searchCtls = new SearchControls();
		searchCtls.setReturningObjFlag(true);
		searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		return searchCtls;
	}

	private void getPerson(DirContext contexto) {

		try {
			String arg0 = argumentsDC();
			SearchControls searchCtls = searchCtls();
			NamingEnumeration<SearchResult> answer = contexto.search(arg0,
					"sAMAccountName=" + getUser(), searchCtls);

			if (answer.hasMore()) {

				Attributes attrs = ((SearchResult) answer.next())
						.getAttributes();

				String distinguishedName = (attrs.get("distinguishedName") != null) ? attrs
						.get("distinguishedName").get().toString()
						: "";
				this.person.setDistinguishedName(distinguishedName);

				String displayName = (attrs.get("displayName") != null) ? attrs
						.get("displayName").get().toString() : "";
				this.person.setFullName(displayName);

				String givenname = (attrs.get("givenname") != null) ? attrs
						.get("givenname").get().toString() : "";
				this.person.setFirstname(givenname);

				String sn = (attrs.get("sn") != null) ? attrs.get("sn").get()
						.toString() : "";
				this.person.setLastnames(sn);

				String sAMAccountName = (attrs.get("sAMAccountName") != null) ? attrs
						.get("sAMAccountName").get().toString()
						: "";
				this.person.setUser(sAMAccountName);

				String mail = (attrs.get("mail") != null) ? attrs.get("mail")
						.get().toString() : "";
				this.person.setEmail(mail);

				String mobile = (attrs.get("mobile") != null) ? attrs
						.get("mobile").get().toString() : "";
				this.person.setMobile(mobile);

				// System.out.println(attrs.get("thumbnailphoto"));

			} else {
				System.out.println("Invalid User");
			}

		} catch (NamingException ex) {
			System.out.println("Authentication LDAP failed: " + ex.toString());

		} catch (Exception ex) {
			System.out.println("Something went wrong: " + ex.toString());

		}

	}

	public void retrieveAllAttributesLDAP(DirContext contexto) {

		try {
			String arg0 = argumentsDC();
			SearchControls searchCtls = searchCtls();
			NamingEnumeration<SearchResult> answer = contexto.search(arg0,
					"sAMAccountName=" + getUser(), searchCtls);

			while (answer.hasMore()) {
				SearchResult result = (SearchResult) answer.next();

				Attributes attribs = result.getAttributes();
				System.out.println("Attributes:" + attribs);
				NamingEnumeration<String> attribsIDs = attribs.getIDs();

				while (attribsIDs.hasMore()) {
					String attrID = attribsIDs.next();
					System.out.println(attrID);
				}
			}

		} catch (Exception ex) {
			System.out.println(ex.toString());

		}

	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getDomain() {
		return domain;
	}

	public String getHostURL() {
		return hostURL;
	}

	public void setHostURL(String hostURL) {
		this.hostURL = hostURL;
	}

	public PersonLdap getPerson() {
		return person;
	}

	public void setPerson(PersonLdap person) {
		this.person = person;
	}

}
