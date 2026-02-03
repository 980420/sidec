package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("ctcList_ensayo")
public class CtcList_ensayo extends EntityQuery<Ctc_ensayo> {

	private static final String EJBQL = "select ctc from Ctc_ensayo ctc";

	private static final String[] RESTRICTIONS = {
			"lower(ctc.eventoAdverso) like concat(lower(#{ctcList_ensayo.ctc.eventoAdverso}),'%')",
			"lower(ctc.descripcion) like concat(lower(#{ctcList_ensayo.ctc.descripcion}),'%')", };

	private Ctc_ensayo ctc = new Ctc_ensayo();

	public CtcList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Ctc_ensayo getCtc() {
		return ctc;
	}
}
