package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eFaseEstudioList_ensayo")
public class EFaseEstudioList_ensayo extends EntityQuery<EFaseEstudio_ensayo> {

	private static final String EJBQL = "select eFaseEstudio from EFaseEstudio_ensayo eFaseEstudio";

	private static final String[] RESTRICTIONS = {
			"lower(eFaseEstudio.nombre) like concat(lower(#{eFaseEstudioList_ensayo.eFaseEstudio.nombre}),'%')",
			"lower(eFaseEstudio.descripcion) like concat(lower(#{eFaseEstudioList_ensayo.eFaseEstudio.descripcion}),'%')", };

	private EFaseEstudio_ensayo eFaseEstudio = new EFaseEstudio_ensayo();

	public EFaseEstudioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public EFaseEstudio_ensayo getEFaseEstudio() {
		return eFaseEstudio;
	}
}
