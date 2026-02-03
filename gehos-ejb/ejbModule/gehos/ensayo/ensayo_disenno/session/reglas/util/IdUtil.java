package gehos.ensayo.ensayo_disenno.session.reglas.util;

import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("idUtil")
@Scope(ScopeType.APPLICATION)
public class IdUtil 
{
	public static String FORM_NAME = "idForm";
	public static String Prefix_Variable_InNoGroup = "var";
	public static String Prefix_Group = "tbvar";
	public static String IdSeparator = ":";
	/**
	 * Builds the id for a variable
	 * @param v
	 * @return
	 */
	public String For(Variable_ensayo v) 
	{
		return For(v,false);
	}
	
	public String For(Variable_ensayo v, boolean includePrefix) 
	{
		String id = "";
		if(includePrefix)
			id = FORM_NAME + IdSeparator;
		if(v.getGrupoVariables()==null)
			return id + Prefix_Variable_InNoGroup + String.valueOf(v.getSeccion().getId()) + String.valueOf(v.getId());
		return (includePrefix ? ForGroup(v.getGrupoVariables(),includePrefix) + IdSeparator : "")  + Prefix_Variable_InNoGroup + String.valueOf(v.getId());
	}
	
	
	public String ForGroup(Variable_ensayo v) 
	{
		return ForGroup(v,false);
	}
	
	public String ForGroup(Variable_ensayo v, boolean includePrefix) 
	{
		String id = "";
		if(includePrefix)
			id = FORM_NAME + IdSeparator;		
		return id + Prefix_Variable_InNoGroup + String.valueOf(v.getId());
	}
	
	public String addFormPrefix(String val)
	{
		return FORM_NAME + IdSeparator + val;
	}
		
	public String ForGroup(GrupoVariables_ensayo g)
	{
		return ForGroup(g, false);
	}
	
	public String ForGroup(GrupoVariables_ensayo g, boolean includePrefix)
	{	
		String id = "";
		if(includePrefix)
			id = FORM_NAME + IdSeparator;
		
		return id + Prefix_Group + String.valueOf(g.getId()) + String.valueOf(g.getSeccion().getId());		
	}
	/**
	 * jQuery requires the colon (:) to be scaped. On the other hand, DOM document does not.
	 * @param v
	 * @return
	 */
	public String ForClientSide(Variable_ensayo v) {
		String id = For(v);		
		return FORM_NAME+ "\\\\:"+id;
	}

}
