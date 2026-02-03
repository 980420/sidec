package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("certificadosConfiablesList_pki")
public class CertificadosConfiablesList_pki extends
		EntityQuery<CertificadosConfiables_pki> {

	private static final String EJBQL = "select certificadosConfiables from CertificadosConfiables_pki certificadosConfiables";

	private static final String[] RESTRICTIONS = {
			"lower(certificadosConfiables.certificado) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificado}),'%')",
			"lower(certificadosConfiables.certificadoC) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoC}),'%')",
			"lower(certificadosConfiables.certificadoSt) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoSt}),'%')",
			"lower(certificadosConfiables.certificadoL) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoL}),'%')",
			"lower(certificadosConfiables.certificadoO) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoO}),'%')",
			"lower(certificadosConfiables.certificadoOu) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoOu}),'%')",
			"lower(certificadosConfiables.certificadoCn) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.certificadoCn}),'%')",
			"lower(certificadosConfiables.descripcion) like concat(lower(#{certificadosConfiablesList_pki.certificadosConfiables.descripcion}),'%')", };

	private CertificadosConfiables_pki certificadosConfiables = new CertificadosConfiables_pki();

	public CertificadosConfiablesList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CertificadosConfiables_pki getCertificadosConfiables() {
		return certificadosConfiables;
	}
}
