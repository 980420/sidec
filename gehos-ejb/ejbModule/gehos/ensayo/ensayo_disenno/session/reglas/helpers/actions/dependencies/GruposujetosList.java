package gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.dependencies;

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
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@Scope(ScopeType.CONVERSATION)
@Name("listadosujetosList")
public class GruposujetosList extends EntityQuery<Usuario_ensayo> {

	
	boolean busquedaAvanzada = false;
	@In	EntityManager entityManager;
	@In	FacesMessages facesMessages;
	@In SeguridadEstudio seguridadEstudio;
	@In IBitacora bitacora;
	int error = -1;
	private int pagina;	
	String nombreUsuario="";
	
	private static final String EJBQL = "select u from Usuario_ensayo u where u.eliminado = false";
	
	/*
	 * Restricciones para la busqueda simple
	 */
	private static final String[] RESTRICTIONSS = {
		"lower (u.nombre) like concat('%', concat(lower(#{listadosujetosList.nombreUsuario.trim()}),'%'))",
		
		
		};

	/*
	 * Restricciones para la busqueda avanzada
	 */
	private static final String[] RESTRICTIONSA = { };

	public GruposujetosList() {
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
		
		

		if (busquedaAvanzada) {
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSA));
			this.refresh();
		} else{
			setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
			this.refresh();
		}
	}
	
	public void cancelarBusqueda() {
		
	}
	
	public boolean isBusquedaAvanzada() {
		return busquedaAvanzada;
	}

	public void setBusquedaAvanzada(boolean busquedaAvanzada) {
		this.busquedaAvanzada = busquedaAvanzada;
	}

	
	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
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