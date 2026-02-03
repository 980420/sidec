
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoTratamiento_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("causaCambiarSujeto")
@Scope(ScopeType.CONVERSATION)
public class CausaCambiarSujeto {

	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	
	protected boolean causaRequired = false;
	protected boolean inicializado = false;
	private EstudioEntidad_ensayo estudEnt = new EstudioEntidad_ensayo();
	private Long idCentro, idSujeto;
	
	
	private String causa = "";
	protected @In IBitacora bitacora;
	private Long cid;
	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void loadData(){
		estudEnt = entityManager.find(EstudioEntidad_ensayo.class , this.idCentro);
		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitCrearCausa"));
		this.causaRequired = true;
		this.inicializado = true;
	}
	
	//Cambiar sujeto de centro
		public void CambiarSujetoCentro() {
			Sujeto_ensayo sujeto = entityManager.find(Sujeto_ensayo.class , this.idSujeto);
			sujeto.setEntidad(estudEnt.getEntidad());
			entityManager.persist(sujeto);
			/*Causa_ensayo causa = new Causa_ensayo();
			causa.setCid(cid);
			causa.setDescripcion(this.causa);
			causa.setSujeto(sujeto);
			causa.setTipoCausa("Creando causa de cambiar sujeto");
			entityManager.persist(causa);*/
			entityManager.flush();

		}
	
	public String validarCampo(){	
		if(!causa.equals("")){
			return "Richfaces.showModalPanel('mpAdvertenciaCambiarCentro')";
		}
		return "";
	}

	public Long getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(Long idCentro) {
		this.idCentro = idCentro;
	}

	public String getCausa() {
		return causa;
	}

	public void setCausa(String causa) {
		this.causa = causa;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public boolean isCausaRequired() {
		return causaRequired;
	}

	public void setCausaRequired(boolean causaRequired) {
		this.causaRequired = causaRequired;
	}

	public EstudioEntidad_ensayo getEstudEnt() {
		return estudEnt;
	}

	public void setEstudEnt(EstudioEntidad_ensayo estudEnt) {
		this.estudEnt = estudEnt;
	}

	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}
	
}
