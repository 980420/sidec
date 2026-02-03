
package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.session.common.auto.ReReporteexpeditoList_ensayo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("listarRe")
@Scope(ScopeType.CONVERSATION)
public class ListarRe extends ReReporteexpeditoList_ensayo {

	private static final String EJBQL = "select reT from ReReporteexpedito_ensayo reT where reT.idSujeto = #{listarRe.idSujeto} and reT.variableDato.eliminado = FALSE";

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In private Usuario user;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;

	private boolean gestionar = false;
	
	private Sujeto_ensayo sujeto;
	private Long idSujeto;
	private int pagina;
	private Date fechaActual= Calendar.getInstance().getTime();
	private Role_ensayo rolLogueado;
	
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		
	}	
	
	public String estadoIconMomento(EstadoMomentoSeguimiento_ensayo estado) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modEnsayo/estadosIcon/"
				+ estado.getClass().getSimpleName().split("_")[0] + "/"
				+ estado.getCodigo() + ".png";

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modEnsayo/estados/" + "generic.png";

	}
	
	public Role_ensayo obtainRole(){
		if (this.rolLogueado == null){
			try {
				this.rolLogueado = (Role_ensayo) this.entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true").setParameter("estudId", this.seguridadEstudio.getEstudioEntidadActivo().getId()).setParameter("idusua", this.user.getId()).getSingleResult();
			} catch (Exception e){
				this.rolLogueado = null;
			}
		}
		return rolLogueado;
	}
	
	@SuppressWarnings("unchecked")
	public String evenetoAnombre(Long id){
		String evento = "";
		List<VariableDato_ensayo> var = (List<VariableDato_ensayo>) entityManager.createQuery("select v from VariableDato_ensayo v where v.cid=:id").setParameter("id", id).getResultList();
		for (int i = 0; i < var.size(); i++){
			if(var.get(i).getVariable().getNombreVariable().equals("Evento Adverso")){
				evento = var.get(i).getValor();
				break;
			}
		}
		return evento;
	}
	
	public String concaNombreMSEstado (String ms, String est){
		return ms + " (" + est + ")";
	}
	
	/*@Override
	public List<ReReporteexpedito_ensayo> getResultList() {

		if (entityManager.find(Sujeto_ensayo.class,this.idSujeto).getFechaInterrupcion() != null) {

			String tempEJB = "select ms from ReReporteexpedito_ensayo ms where ms.idSujeto = #{listarRe.idSujeto}";
			this.setEjbql(tempEJB);
		} else {
			this.setEjbql(EJBQL);
		}

		return super.getResultList();

	}*/
		
	
	public Sujeto_ensayo ObtenerSujeto(){
		sujeto = (Sujeto_ensayo) entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:idSujeto").setParameter("idSujeto", this.idSujeto).getSingleResult();
		return sujeto;
	}

	public String NombreSujetoById() {
		String nom = "";
		Sujeto_ensayo otro = (Sujeto_ensayo) entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:idSujeto")
				.setParameter("idSujeto", this.idSujeto).getSingleResult();
		nom = otro.getCodigoPaciente();
		return nom;
	}

	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> NotasMonitoreoActONueva(Long idMomento) {
		String queryNotas = "select notaEnsayo "
				+ "from Nota_ensayo notaEnsayo "
				//+ "inner join notaEnsayo.estadoNota estado "
				+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
				+ "inner join crdEspecifico.momentoSeguimientoEspecifico momento "
				+ "where momento.id=:idMomento and notaEnsayo.notaSitio='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";
				
				

		
		List<Nota_ensayo> datosNotas = entityManager.createQuery(queryNotas)
				.setParameter("idMomento", idMomento).getResultList();
		
		List<Nota_ensayo> nuevasOActualizadas = new ArrayList<Nota_ensayo>();
		for (int i = 0; i < datosNotas.size(); i++) {
			if(datosNotas.get(i).getEstadoNota().getCodigo() == 1 || datosNotas.get(i).getEstadoNota().getCodigo() == 2){
				nuevasOActualizadas.add(datosNotas.get(i));
			}
		}
		
		return datosNotas;
	}
	
	@SuppressWarnings("unchecked")
	public boolean ObtenerCRD(){
		List<CrdEspecifico_ensayo> Listcrd = new ArrayList<CrdEspecifico_ensayo>();
		boolean completa = false;
		String queryCRD = "select CrdEspecifico "
				+ "from CrdEspecifico_ensayo CrdEspecifico "
				+ "inner join CrdEspecifico.momentoSeguimientoEspecifico momentoEspecifico "
				+ "inner join momentoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoEspecifico.sujeto sujeto "
				+ "where sujeto.id=:idSujeto and momentoSeguimientoGeneral.nombre = 'Evaluaci\u00F3n Inicial' ";
				

		
		Listcrd = (List<CrdEspecifico_ensayo>)entityManager.createQuery(queryCRD)
				.setParameter("idSujeto", this.idSujeto).getResultList();
		if(Listcrd.size() == 0){
			completa = true;
		}
		
		for (int i = 0; i < Listcrd.size(); i++) {
			if(Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 1 && Listcrd.get(i).getEstadoHojaCrd().getCodigo() != 3){
				completa = true;
				break;
			}
		}
		
		return completa;
	}
	
	
	
	public Integer CantidadNotasMActNuevas(Long idMomento){
		return NotasMonitoreoActONueva(idMomento).size();
	}

	@End 
	public void salir(){		
		
	}

	public ListarRe() {
		setEjbql(EJBQL);
		setMaxResults(10);
		setOrder("reT.fechainicio desc");
		this.gestionar = true;
	}

	public Long getidSujeto() {
		return idSujeto;
	}

	public void setidSujeto(Long idS) {
		this.idSujeto = idS;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
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

	public Date getFechaActual() {
		return fechaActual;
	}

	public void setFechaActual(Date fechaActual) {
		this.fechaActual = fechaActual;
	}

}
