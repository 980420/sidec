package gehos.ensayo.ensayo_disenno.session.reglas;

import java.util.ArrayList;

import gehos.comun.listadoControler.ListadoControler;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Name("listadoReglas")
@Scope(ScopeType.CONVERSATION)
public class ListadoReglas 
{	
	ListadoControler<ReglaWrapper> reglas = new ListadoControler<ReglaWrapper>(new ArrayList<ReglaWrapper>());
	private int pagina;
	public int getPagina() {
		if (this.getReglas().getNextFirstResult() != 0)
			return this.getReglas().getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getReglas().getResultList().size() / 10) + 1;
		if (this.pagina > 0) {
			if (getReglas().getResultList().size() % 10 != 0) {
				if (pagina <= num)
					this.getReglas().setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.getReglas().setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	public ListadoControler<ReglaWrapper> getReglas() {
		return reglas;
	}

	public void setReglas(ListadoControler<ReglaWrapper> reglas) {
		this.reglas = reglas;
	}
	
}
