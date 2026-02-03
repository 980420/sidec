package gehos.comun.incidencias.session;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import gehos.comun.incidencias.entity.Incidencia_Incidencias;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;

@Name("misIncidencias")
@Scope(ScopeType.PAGE)
public class MisIncidencias extends EntityQuery<Incidencia_Incidencias> {
	
	private static final long serialVersionUID = 1L;

	private static final String[] RESTRICTIONS = {
		"inc.usuarioByIdUsuario.id = #{user.id}",
		"inc.estado.valor = #{misIncidencias.estado}"
	};

	private String estado;
	private long idSub;
	private long id;
	
	@In
	private EntityManager entityManager;
	
	public MisIncidencias(){
		setEjbql("select inc from Incidencia_Incidencias inc");
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("inc.estado.orden asc");
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getEstados(){
		return (List<String>)entityManager.createQuery(
				"select est.valor from Estados_Incidencias est where est.eliminado = false")
				.getResultList();
	}
	
	public boolean isHayIncidencias(){
		Long cant = (Long)entityManager.createQuery(
				"select count(inc) from Incidencia_Incidencias inc where inc.usuarioByIdUsuario.id = #{user.id}")
				.getSingleResult();
		return cant.longValue() != 0L;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public long getIdSub() {
		return idSub;
	}

	public void setIdSub(long idSub) {
		this.idSub = idSub;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
