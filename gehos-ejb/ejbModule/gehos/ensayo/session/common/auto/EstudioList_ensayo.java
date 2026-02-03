package gehos.ensayo.session.common.auto;

import gehos.ensayo.entity.*;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.Arrays;

@Name("estudioList_ensayo")
public class EstudioList_ensayo extends EntityQuery<Estudio_ensayo> {

	private static final String EJBQL = "select estudio from Estudio_ensayo estudio";

	private static final String[] RESTRICTIONS = {
			"lower(estudio.identificador) like concat(lower(#{estudioList_ensayo.estudio.identificador}),'%')",
			"lower(estudio.tituloOficial) like concat(lower(#{estudioList_ensayo.estudio.tituloOficial}),'%')",
			"lower(estudio.tituloAbreviado) like concat(lower(#{estudioList_ensayo.estudio.tituloAbreviado}),'%')",
			"lower(estudio.nombre) like concat(lower(#{estudioList_ensayo.estudio.nombre}),'%')",
			"lower(estudio.investigadorPrincipal) like concat(lower(#{estudioList_ensayo.estudio.investigadorPrincipal}),'%')",
			"lower(estudio.resumen) like concat(lower(#{estudioList_ensayo.estudio.resumen}),'%')",
			"lower(estudio.descripcion) like concat(lower(#{estudioList_ensayo.estudio.descripcion}),'%')",
			"lower(estudio.patrocinador) like concat(lower(#{estudioList_ensayo.estudio.patrocinador}),'%')",
			"lower(estudio.colaboradores) like concat(lower(#{estudioList_ensayo.estudio.colaboradores}),'%')",
			"lower(estudio.otrosFase) like concat(lower(#{estudioList_ensayo.estudio.otrosFase}),'%')",
			"lower(estudio.otroProposito) like concat(lower(#{estudioList_ensayo.estudio.otroProposito}),'%')",
			"lower(estudio.nombrePromotor) like concat(lower(#{estudioList_ensayo.estudio.nombrePromotor}),'%')",
			"lower(estudio.rolContacto) like concat(lower(#{estudioList_ensayo.estudio.rolContacto}),'%')",
			"lower(estudio.emailContacto) like concat(lower(#{estudioList_ensayo.estudio.emailContacto}),'%')",
			"lower(estudio.palabrasClaves) like concat(lower(#{estudioList_ensayo.estudio.palabrasClaves}),'%')",
			"lower(estudio.duracion) like concat(lower(#{estudioList_ensayo.estudio.duracion}),'%')",
			"lower(estudio.sumarioBreve) like concat(lower(#{estudioList_ensayo.estudio.sumarioBreve}),'%')",
			"lower(estudio.otrasPatologias) like concat(lower(#{estudioList_ensayo.estudio.otrasPatologias}),'%')",
			"lower(estudio.tipofase) like concat(lower(#{estudioList_ensayo.estudio.tipofase}),'%')",
			"lower(estudio.tipoproposito) like concat(lower(#{estudioList_ensayo.estudio.tipoproposito}),'%')", };

	private Estudio_ensayo estudio = new Estudio_ensayo();

	public EstudioList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(25);
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}
}
