package gehos.ensayo.ensayo_disenno.session.custom;

import gehos.bitacora.session.traces.Bitacora;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_configuracion.session.custom.SeleccionarEstudio;
import gehos.ensayo.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@Scope(ScopeType.CONVERSATION)
@Name("gruposujetosMSCutomList_ensayo")
public class GruposujetosMSCutomList_ensayo extends EntityQuery<GrupoSujetos_ensayo> {

	private GrupoSujetos_ensayo gruposujetos = new GrupoSujetos_ensayo();
	boolean busquedaAvanzada = false;
	@In	EntityManager entityManager;
	@In	FacesMessages facesMessages;
	@In SeguridadEstudio seguridadEstudio;
	@In IBitacora bitacora;
	int error = -1;
	long idGruposujetos;
	private int pagina;
	Date desde, hasta;      
	
	private static final String EJBQL = "select gruposujetos from GrupoSujetos_ensayo gruposujetos "
			+ "where gruposujetos.eliminado = false "
			+ "and gruposujetos in (select c.grupoSujetos from Cronograma_ensayo c) "
			+ "and gruposujetos.habilitado <> false "
			+ "and gruposujetos.estudio.id = #{seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()}";
	
	/*
	 * Restricciones para la busqueda simple
	 */
	private static final String[] RESTRICTIONSS = {
		"lower (gruposujetos.nombreGrupo) like concat('%', concat(lower(#{gruposujetosMSCutomList_ensayo.gruposujetos.nombreGrupo.trim()}),'%'))",
		"gruposujetos.fechaCreacion >= #{gruposujetosMSCutomList_ensayo.desde}",
		"gruposujetos.fechaCreacion <= #{gruposujetosMSCutomList_ensayo.hasta}"};

	/*
	 * Restricciones para la busqueda avanzada
	 */
	private static final String[] RESTRICTIONSA = { };

	public GruposujetosMSCutomList_ensayo() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(10);
		setOrder("id desc");
		
	}

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void begin() {
	}

	/**
	 * Metodo que cambia el tipo de busqueda que se realizara
	 */
	public void cambiarBusqueda() {
		busquedaAvanzada = !busquedaAvanzada;
		gruposujetos = new GrupoSujetos_ensayo();
		desde = null;
		hasta = null;

		if (busquedaAvanzada) {
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSA));
			this.refresh();
		} else{
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
			this.refresh();
		}
	}
	
	public void cancelarBusqueda() {
		gruposujetos = new GrupoSujetos_ensayo();
	}

	/**
	 * Metodo para seleccionar el(la) Gruposujetos a modificar
	 */
	public void seleccionarModificar(long idGruposujetos) throws IllegalStateException,
			SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager.createQuery(
					"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetos).getSingleResult();

			this.idGruposujetos = idGruposujetos;
			error = 0;
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	/**
	 * Metodo para seleccionar el(la) gruposujetos a ver
	 */
	public void seleccionarVer(long idGruposujetos) throws IllegalStateException,
			SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager.createQuery(
					"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1")
					.setParameter("param1", idGruposujetos).getSingleResult();

			this.idGruposujetos = idGruposujetos;
			error = 0;
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos", Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	/**
	 * Metodo para seleccionar el(la) gruposujetos a eliminar 
	 */
	public void seleccionarEliminar(long idGruposujetos)
			throws IllegalStateException, SecurityException, SystemException {
		this.idGruposujetos = idGruposujetos;
	}
	/**
	 * Elimina de la base de datos
	 */
	public void eliminar() throws IllegalStateException, SecurityException,
			SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager.createQuery(
					"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1").setParameter("param1", this.idGruposujetos)
					.getSingleResult();
			
			gruposujetos.setEliminado(true);
			gruposujetos.setCid(
					bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("biteliminandoGS_enDis")+" -"+gruposujetos.getNombreGrupo().toString()));
			entityManager.persist(gruposujetos);
			entityManager.flush();			
			error = 0;

			if (getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult() - getMaxResults());

			this.refresh();
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos", Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}
	/**
	 * Cambiar el estado del grupo de sujetos habilitado/deshabilitado
	 */
	public void cambiarEstado(long idGruposujetos) throws IllegalStateException,
		SecurityException, SystemException {
		try {
			GrupoSujetos_ensayo gruposujetos = (GrupoSujetos_ensayo) entityManager.createQuery(
					"select gruposujetos from GrupoSujetos_ensayo gruposujetos where gruposujetos.id =:param1").setParameter("param1", idGruposujetos)
					.getSingleResult();
			
			if (gruposujetos.getHabilitado()) {
				gruposujetos.setHabilitado(false);
				gruposujetos.setCid(
						bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitDeshaGS_enDis")+" -"+gruposujetos.getNombreGrupo().toString()));				
			} else {
				gruposujetos.setHabilitado(true);
				gruposujetos.setCid(
						bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitDeshaGS_enDis")+" -"+gruposujetos.getNombreGrupo().toString()));				
			
			}
			entityManager.persist(gruposujetos);
			entityManager.flush();			
			error = 0;
			this.refresh();
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "eliminado");
			Transaction.instance().rollback();
			error = 1;
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.addToControlFromResourceBundle("listOfGruposujetos",
					Severity.ERROR, "msjEliminar");
			Transaction.instance().rollback();
			error = 1;
		}
	}

	public boolean isBusquedaAvanzada() {
		return busquedaAvanzada;
	}

	public void setBusquedaAvanzada(boolean busquedaAvanzada) {
		this.busquedaAvanzada = busquedaAvanzada;
	}

	public GrupoSujetos_ensayo getGruposujetos() {
		return gruposujetos;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public long getIdGruposujetos() {
		return idGruposujetos;
	}

	public void setIdGruposujetos(long idGruposujetos) {
		this.idGruposujetos = idGruposujetos;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}	

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}	
	public int getPagina() {
        if(this.getNextFirstResult() != 0)
            return this.getNextFirstResult()/10;
            else
                return 1;
    }

    public void setPagina(int pagina) {
        this.pagina = pagina;
       
        long num=(getResultCount()/10)+1;
        if(this.pagina>0){
        if(getResultCount()%10!=0){
            if(pagina<=num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        else{
            if(pagina<num)
                this.setFirstResult((this.pagina - 1 )*10);
        }
        }
    }
}