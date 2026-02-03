package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("sexoList_ensayo")
public class SexoList_ensayo extends EntityQuery<Sexo_ensayo> {

	private static final String EJBQL = "select sexo from Sexo_ensayo sexo";

	private static final String[] RESTRICTIONS = {
			"lower(sexo.valor) like concat(lower(#{sexoList_ensayo.sexo.valor}),'%')",
			"lower(sexo.abreviatura) like concat(lower(#{sexoList_ensayo.sexo.abreviatura}),'%')",
			"lower(sexo.codigo) like concat(lower(#{sexoList_ensayo.sexo.codigo}),'%')",
			"lower(sexo.codigoSap) like concat(lower(#{sexoList_ensayo.sexo.codigoSap}),'%')", };

	private Sexo_ensayo sexo = new Sexo_ensayo();

	public SexoList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Sexo_ensayo getSexo() {
		return sexo;
	}
}
