package gehos.ensayo.ensayo_disenno.session.reglas.examples;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;

@Name("ModalExtraSheetData")
@Scope(ScopeType.CONVERSATION)
public class ModalExtraSheetData extends IActionWithUserInput
{	
	String nombre;

	public void Process() 
	{
		//do operations here
		//and if all was successful then set the completed property to true

		setCompleted(nombre!=null && nombre.equals("aa"));
	}	

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	@Override					
	public String Export() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean CanExport() {		
		return false;
	}
}
