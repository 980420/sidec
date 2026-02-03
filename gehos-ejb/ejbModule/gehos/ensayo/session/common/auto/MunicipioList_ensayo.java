package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("municipioList_ensayo")
public class MunicipioList_ensayo extends EntityQuery<Municipio_ensayo> {

	private static final String EJBQL = "select municipio from Municipio_ensayo municipio";

	private static final String[] RESTRICTIONS = {
			"lower(municipio.valor) like concat(lower(#{municipioList_ensayo.municipio.valor}),'%')",
			"lower(municipio.codigo) like concat(lower(#{municipioList_ensayo.municipio.codigo}),'%')", };

	private Municipio_ensayo municipio = new Municipio_ensayo();

	public MunicipioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Municipio_ensayo getMunicipio() {
		return municipio;
	}
}
