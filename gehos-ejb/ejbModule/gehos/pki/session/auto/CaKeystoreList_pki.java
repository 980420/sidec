package gehos.pki.session.auto;

import gehos.pki.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("caKeystoreList_pki")
public class CaKeystoreList_pki extends EntityQuery<CaKeystore_pki> {

	private static final String EJBQL = "select caKeystore from CaKeystore_pki caKeystore";

	private static final String[] RESTRICTIONS = {
			"lower(caKeystore.keystore) like concat(lower(#{caKeystoreList_pki.caKeystore.keystore}),'%')",
			"lower(caKeystore.numeroSerie) like concat(lower(#{caKeystoreList_pki.caKeystore.numeroSerie}),'%')", };

	private CaKeystore_pki caKeystore = new CaKeystore_pki();

	public CaKeystoreList_pki() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public CaKeystore_pki getCaKeystore() {
		return caKeystore;
	}
}
