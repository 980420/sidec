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

@Name("incidenciasAsignadas")
@Scope(ScopeType.CONVERSATION)
public class IncidenciasAsignadas extends EntityQuery<Incidencia_Incidencias> {
	
	private static final long serialVersionUID = 1L;

	private static final String[] RESTRICTIONS = {
		"inc.usuarioByIdAsignado.id = #{user.id}",
		"inc.componente.valor = #{incidenciasAsignadas.componente}",
		"inc.estado.valor = #{incidenciasAsignadas.estado}"
	};

	private String estado;
	private String componente;
	
	@In
	private EntityManager entityManager;
	
	public IncidenciasAsignadas(){
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
	
	@SuppressWarnings("unchecked")
	public List<String> getComponentes(){
		return (List<String>)entityManager.createQuery(
				"select comp.valor from Componente_Incidencias comp where comp.eliminado = false")
				.getResultList();
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getComponente() {
		return componente;
	}

	public void setComponente(String componente) {
		this.componente = componente;
	}
	
	
}
