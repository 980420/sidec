package gehos.autenticacion.session.custom;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Hashtable;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPAuth {

	public static void main(String[] args) {
		//new LDAPAuth().autenticacionLDAP("salvadom@pdvsa.com", "PDVSA22");
		// new LDAPAuth().autenticacionLDAP("pimentellp@pdvsa.com",
		// "pimentellp");
	}	

	@SuppressWarnings("unchecked")
	public String autenticacionLDAP(String usuario, String password) {
		Hashtable auth = new Hashtable(11);

		String base = "DC=pdvsa,DC=com";

		// String dn = "uid=" + usuario + "," + base;
		String dn = usuario;
		String ldapURL = "ldap://ccschu101.pdvsa.com:389";
		//String ldapURL = "ldap://zstotel01.pdvsa.com:389";
		auth.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		auth.put(Context.PROVIDER_URL, ldapURL);
		auth.put(Context.SECURITY_AUTHENTICATION, "simple");
		// auth.put(Context.SECURITY_AUTHENTICATION, "DIGEST-MD5");
		auth.put(Context.SECURITY_PRINCIPAL, dn);
		auth.put(Context.SECURITY_CREDENTIALS, password);
		// auth.put("java.naming.security.sasl.realm", dn);

		try {
			DirContext authContext = new InitialDirContext(auth);
			System.out
					.println("LA AUTENTICACION SE REALIZÓ CORRECTAMENTE ANTE EL LDAP!");
			 SearchControls ctrl = new SearchControls();
	         ctrl.setSearchScope(SearchControls.SUBTREE_SCOPE);
	         String DISTINGUISHED_NAME = "pdvsacom-ad-cedula";
	         String CN = "cn";
	         String MEMBER_OF = "memberOf";
	         String user = usuario.substring(0, usuario.indexOf('@'));
	         ctrl.setReturningAttributes(new String[] {DISTINGUISHED_NAME, CN, MEMBER_OF});
	         NamingEnumeration enumeration = authContext.search("DC=pdvsa,DC=com", 
	        		 MessageFormat.format("(sAMAccountName={0})", new Object[] {user}),
	        		 ctrl);
	         SearchResult sr = (SearchResult) enumeration.next();
	         Attribute name = sr.getAttributes().get(DISTINGUISHED_NAME);
	         return (String)name.get(0);
			//return true;
		} catch (AuthenticationException authEx) {
			authEx.printStackTrace();
			System.out.println("");
			System.out.println("NO SE ENCONTRÓ ESTOS DATOS!");
			return null;
		} catch (NamingException namEx) {
			System.out.println("SUCEDIÓ ALGO!");
			namEx.printStackTrace();
			return null;
		}
	}
}
