package gehos.ensayo.ensayo_extraccion.session.custom;

import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.session.common.auto.ConjuntoDatosList_ensayo;

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;






@Name("conjuntoDatosCustomList_ensayo")
@Scope(ScopeType.PAGE)
public class ConjuntoDatosCustomList_ensayo
  extends ConjuntoDatosList_ensayo
{
  @In
  SeguridadEstudio seguridadEstudio;
  @In
  EntityManager entityManager;
  @In IActiveModule activeModule;
  private static final String EJBQL = "select cd from ConjuntoDatos_ensayo cd where cd.eliminado <> true "
  		+ "and cd.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()} "
  		+ "and cd.entidad.id = #{activeModule.getActiveModule().getEntidad().getId()}";
  private static final String[] RESTRICTIONS = { "lower(cd.nombre) like concat('%',lower(#{conjuntoDatosCustomList_ensayo.nombre}),'%'))" };
  
  public ConjuntoDatosCustomList_ensayo() {
    setEjbql(EJBQL);
    setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
    setOrder("cd.id");
    setMaxResults(Integer.valueOf(10));
  }
  
  private String nombre = "";
  
  private int pagina;
  
  public String getNombre()
  {
    return nombre;
  }
  
  public void setNombre(String nombre) {
    this.nombre = nombre;
  }
  
  public boolean pesquisajeHabilitado(){
		boolean habilitado = false;
		GrupoSujetos_ensayo grupo = new GrupoSujetos_ensayo();
		try {
			grupo = (GrupoSujetos_ensayo) entityManager
					.createQuery(
							"select g from GrupoSujetos_ensayo g where g.estudio=:estud and g.nombreGrupo = 'Grupo Pesquisaje'")
					.setParameter("estud",
							seguridadEstudio.getEstudioEntidadActivo().getEstudio())
					.getSingleResult();
		} catch (Exception e) {
			grupo = null;
		}
		
		
		if(grupo != null && grupo.getHabilitado()){
			habilitado = true;
		}
		
		return habilitado;
	}
  
  public int getPagina() {
    if (getNextFirstResult() != 0) {
      return getNextFirstResult() / 10;
    }
    return 1;
  }
  
  public void setPagina(int pagina) {
    this.pagina = pagina;
    
    long num = getResultCount().longValue() / 10L + 1L;
    if (this.pagina > 0) {
      if (getResultCount().longValue() % 10L != 0L) {
        if (pagina <= num) {
          setFirstResult(Integer.valueOf((this.pagina - 1) * 10));
        }
      } else if (pagina < num) {
        setFirstResult(Integer.valueOf((this.pagina - 1) * 10));
      }
    }
  }
}
