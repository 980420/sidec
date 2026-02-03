package gehos.configuracion.management.gestionarTipoCama;

import gehos.configuracion.management.entity.*;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.LocaleSelector;
import org.jboss.seam.international.StatusMessage.Severity;

import java.util.Arrays;
import javax.persistence.EntityManager;

@Name("tipoCamaList_configuracionCustom") 
@Scope(ScopeType.CONVERSATION)
public class TipoCamaList_configuracionCustom extends
		EntityQuery<TipoCama_configuracion> {

	private static final String EJBQL = "select tipoCama from TipoCama_configuracion tipoCama";

	private static final String[] RESTRICTIONS = {
			"lower(tipoCama.valor) like concat(lower(#{tipoCamaList_configuracionCustom.valor.trim()}),'%')",
			"lower(tipoCama.codigo) like concat(lower(#{tipoCamaList_configuracionCustom.codigo.trim()}),'%')", 
			"#{tipoCamaList_configuracionCustom.idcama} <> tipoCama.id"
			};

	@In
	EntityManager entityManager;
	
	@In
	LocaleSelector localeSelector;
	
	@In(create = true)
	FacesMessages facesMessages;
	
	private TipoCama_configuracion tipoCama = new TipoCama_configuracion();
	private String codigo = "",valor = "";
	private Long idcama;
	private boolean open = true;

	public TipoCamaList_configuracionCustom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("tipoCama.id desc");
	}
	
	public void abrirCerrar(){
		this.open =! open;
	}
	
	public void buscar(){
		setFirstResult(0);
	}
	
	public void cancelar(){
		this.codigo = "";
		this.valor = "";
	}
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){	
		if(getResultList().size() == 0 && getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults()); 	
	}
	
	public void seleccionar(Long idcama){
		this.idcama = idcama;
	}
	
	public void eliminar(){
		try {
			tipoCama = entityManager.find(TipoCama_configuracion.class, this.idcama);	
			this.idcama = -1l;
			entityManager.remove(tipoCama);
			entityManager.flush();
			
			if(getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult()-getMaxResults()); 			
		
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "Este tipo de cama está en uso por lo tanto no puede ser eliminado.");
		}
		
	}
	
	public void cambiarVisibilidad(Long id){
		TipoCama_configuracion aux = entityManager.find(TipoCama_configuracion.class, id);
		aux.setEliminado(!aux.getEliminado());
		entityManager.merge(aux);
		entityManager.flush();
	}

	public TipoCama_configuracion getTipoCama() {
		return tipoCama;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public void setTipoCama(TipoCama_configuracion tipoCama) {
		this.tipoCama = tipoCama;
	}

	public Long getIdcama() {
		return idcama;
	}

	public void setIdcama(Long idcama) {
		this.idcama = idcama;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}
}
