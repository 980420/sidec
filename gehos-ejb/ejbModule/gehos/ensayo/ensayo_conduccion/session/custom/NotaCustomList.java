package gehos.ensayo.ensayo_conduccion.session.custom;

import gehos.ensayo.session.common.auto.NotaList_ensayo;
import org.jboss.seam.annotations.Name;
import java.util.Arrays;

@SuppressWarnings("serial")
@Name("notaCustomList")
public class NotaCustomList extends NotaList_ensayo {
	
	private static final String EJBQL = "select nota from Nota_ensayo nota where nota.eliminado = false ";

	private static final String[] RESTRICTIONS = {
			"lower(nota.descripcion) like concat(lower(#{notaCustomList.descripcion}),'%')",};

	public NotaCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
	}
	
	
	private String descripcion;
	
	
		
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	
	
	
	
}
