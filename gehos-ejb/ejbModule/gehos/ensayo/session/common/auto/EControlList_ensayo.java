package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eControlList_ensayo")
public class EControlList_ensayo extends EntityQuery<EControl_ensayo> {

	private static final String EJBQL = "select eControl from EControl_ensayo eControl";

	private static final String[] RESTRICTIONS = {
			"lower(eControl.nombre) like concat(lower(#{eControlList_ensayo.eControl.nombre}),'%')",
			"lower(eControl.descripcion) like concat(lower(#{eControlList_ensayo.eControl.descripcion}),'%')", };

	private EControl_ensayo eControl = new EControl_ensayo();

	public EControlList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EControl_ensayo getEControl() {
		return eControl;
	}
}
