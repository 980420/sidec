package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("presentacionFormularioList_ensayo")
public class PresentacionFormularioList_ensayo extends
		EntityQuery<PresentacionFormulario_ensayo> {

	private static final String EJBQL = "select presentacionFormulario from PresentacionFormulario_ensayo presentacionFormulario";

	private static final String[] RESTRICTIONS = {
			"lower(presentacionFormulario.nombre) like concat(lower(#{presentacionFormularioList_ensayo.presentacionFormulario.nombre}),'%')",
			"lower(presentacionFormulario.descripcion) like concat(lower(#{presentacionFormularioList_ensayo.presentacionFormulario.descripcion}),'%')", };

	private PresentacionFormulario_ensayo presentacionFormulario = new PresentacionFormulario_ensayo();

	public PresentacionFormularioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public PresentacionFormulario_ensayo getPresentacionFormulario() {
		return presentacionFormulario;
	}
}
