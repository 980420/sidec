package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("eTipoProtocoloList_ensayo")
public class ETipoProtocoloList_ensayo extends
		EntityQuery<ETipoProtocolo_ensayo> {

	private static final String EJBQL = "select eTipoProtocolo from ETipoProtocolo_ensayo eTipoProtocolo";

	private static final String[] RESTRICTIONS = {
			"lower(eTipoProtocolo.nombre) like concat(lower(#{eTipoProtocoloList_ensayo.eTipoProtocolo.nombre}),'%')",
			"lower(eTipoProtocolo.descripcion) like concat(lower(#{eTipoProtocoloList_ensayo.eTipoProtocolo.descripcion}),'%')", };

	private ETipoProtocolo_ensayo eTipoProtocolo = new ETipoProtocolo_ensayo();

	public ETipoProtocoloList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public ETipoProtocolo_ensayo getETipoProtocolo() {
		return eTipoProtocolo;
	}
}
