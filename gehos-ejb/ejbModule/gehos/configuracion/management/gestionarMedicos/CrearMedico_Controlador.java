package gehos.configuracion.management.gestionarMedicos;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("crearMedico_Controlador")
@Scope(ScopeType.CONVERSATION)
public class CrearMedico_Controlador {
	
	@In 
	EntityManager entityManager;
	
	private String selectOption = "2";
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){		
	}

	public String getSelectOption() {
		return selectOption;
	}

	public void setSelectOption(String selectOption) {
		this.selectOption = selectOption;
	}
	
	public String goTo(){
		if (selectOption.equals("2")) {
			return "gotoMedicoUserCreate";
		}
		else {
			return "gotoMedicoUserExist";
		}
			
	}

}
