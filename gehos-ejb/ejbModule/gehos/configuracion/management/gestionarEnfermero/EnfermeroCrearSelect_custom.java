package gehos.configuracion.management.gestionarEnfermero;

import org.jboss.seam.annotations.Name;

@Name("enfermeroCrearSelect_custom")
public class EnfermeroCrearSelect_custom {	
			
	private String selectOption = "2";	//2:Para que aparezca la opción de 'No' seleccionada por defecto
		
	//PROPIEDADES
	public String getSelectOption() {
		return selectOption;
	}

	public void setSelectOption(String selectOption) {
		this.selectOption = selectOption;
	}
	
	public String optionSelect(){
		if(this.selectOption.equals("1"))
			return "gotoSelecUser";
		return "gotoCreteEnfermeroAndUser";
	}
}

	