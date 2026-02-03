package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;
//CU 20 Eliminar cronograma especifico
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.Bitacora;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.EstadoSujeto_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;

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

@Name("eliminarCronogramaEspecifico")
@Scope(ScopeType.CONVERSATION)
public class EliminarCronogramaEspecifico {
	@In
	Usuario user;
	@In
	SeguridadEstudio seguridadEstudio;
	private String causaDescripcion="";
	private String causaGuardar = "";

	public String getCausaGuardar() {
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar) {
		this.causaGuardar = causaGuardar;
	}

	private Object causa = null;

	public Object getCausa() {
		return causa;
	}

	public void setCausa(Object causa) {
		this.causa = causa;
	}
	protected @In EntityManager entityManager;
	protected @In(create=true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	private Sujeto_ensayo sujeto;
	
	@In(create = true, value = "cronogramasEspecificosCustomList")
	private CronogramasEspecificosCustomList listaCronogramas;
	
	private Long idSujeto;
	
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle.getBundle()
			.getString("caracteresEspeciales");
	
	@Begin(join=true, flushMode=FlushModeType.MANUAL)
	public void loadData(){
		this.sujeto = (Sujeto_ensayo)entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:id").setParameter("id", idSujeto).getSingleResult();
		
	}	
	public void seleccionarSujetoEliminarCronograma(long id)
	{
		idSujeto=id;
	}
	
	public String validarCausa() {
		if (causa != null) {
			causaGuardar = causa.toString();
			int longitud = causa.toString().length();
			boolean noExtranno = causa.toString().matches(
					"^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"\u00BF?.,0-9]+\\s*)++$");
			if (causa != null && noExtranno && longitud <= 250) {
				causa = null;
				return "Richfaces.showModalPanel('mpAdvertenciaEliminarCronogramaEsp')";
			}
		}
		return "";
	}
	
	public void eliminarCronogramaEspecifico()
	{
		Sujeto_ensayo sujeto=(Sujeto_ensayo) entityManager.createQuery("select s from Sujeto_ensayo s where s.id=:idSujeto").setParameter("idSujeto", idSujeto).getSingleResult();
		//String nombre="Habilitado";
		//EstadoSujeto_ensayo estadoHabilitado=(EstadoSujeto_ensayo) entityManager.createQuery("select e from EstadoSujeto_ensayo e where e.nombre=:nombre").setParameter("nombre", nombre).getSingleResult();
		sujeto.setCronogramaEspecifico(false);
		entityManager.persist(sujeto);
		entityManager.flush();
		
		List<MomentoSeguimientoEspecifico_ensayo> listadoMomentosEspecificos=entityManager.createQuery("select mse from MomentoSeguimientoEspecifico_ensayo mse where mse.sujeto.id=:idSujeto and mse.eliminado = False").setParameter("idSujeto", idSujeto).getResultList();
		for(int i=0;i<listadoMomentosEspecificos.size();i++)
		{
			
			MomentoSeguimientoEspecifico_ensayo momento=listadoMomentosEspecificos.get(i);
			/*List<CrdEspecifico_ensayo> listadoCrdEspecifico=entityManager.createQuery("select crd from CrdEspecifico_ensayo crd where crd.sujeto.id=:idSujeto and mse.programado=True and mse.eliminado =: false").setParameter("idSujeto", idSujeto).getResultList();
			Buscar las Hojas CRD asociadas al MSE y ponerles estado eliminado a todas.
			*/
			if(!momento.getMomentoSeguimientoGeneral().getNombre().equals("Pesquisaje") && !momento.getMomentoSeguimientoGeneral().getNombre().equals("Evaluaci\u00F3n Inicial")){
				momento.setEliminado(true);
				entityManager.persist(momento);
				entityManager.flush();
			}
			
		}
		listaCronogramas.refresh();
		
		
		agregarCausaEliminado();
	}
	
	public void agregarCausaEliminado()
	{
		Sujeto_ensayo sujeto=(Sujeto_ensayo) entityManager.createQuery("select s from Sujeto_ensayo s where s.id=:idSujeto").setParameter("idSujeto", idSujeto).getSingleResult();
		Cronograma_ensayo cronograma=(Cronograma_ensayo) entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id=:idGrupo").setParameter("idGrupo", sujeto.getGrupoSujetos().getId()).getSingleResult();
		
		Causa_ensayo causa = new Causa_ensayo();
		causa.setCronograma(cronograma);
		causa.setDescripcion(this.causaGuardar);
		causa.setSujeto(sujeto);
		causa.setTipoCausa("Eliminando cronograma especifico");
		causa.setEstudio(seguridadEstudio.getEstudioEntidadActivo()
				.getEstudio());
		Usuario_ensayo usuario=entityManager.find(Usuario_ensayo.class, user.getId());
		causa.setUsuario(usuario);
		causa.setFecha(Calendar.getInstance().getTime());
		entityManager.persist(causa);
		entityManager.flush();
	}

	public String getCausaDescripcion() {
		return causaDescripcion;
	}

	public void setCausaDescripcion(String causaDescripcion) {
		this.causaDescripcion = causaDescripcion;
	}
	
	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}
	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}
}
