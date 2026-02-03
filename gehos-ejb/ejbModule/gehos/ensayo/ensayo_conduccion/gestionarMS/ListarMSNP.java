//CU 10 Visualizar MS por sujeto (No progamados)

package gehos.ensayo.ensayo_conduccion.gestionarMS;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.MomentoSeguimientoEspecificoList_ensayo;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("listarMSNP")
@Scope(ScopeType.CONVERSATION)
public class ListarMSNP extends MomentoSeguimientoEspecificoList_ensayo {

	private static final String EJBQL = "select ms from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.id = #{listarMSNP.idSujeto} and ms.momentoSeguimientoGeneral.programado = 'false' and ms.eliminado = 'false'";
	// Obtiene todas las hojas crd de un momento de seguimiento que NO ESTAN DESHASOCIADAS 
	private static final String consultaHojaCrdMomentoSeguimiento="select crd from CrdEspecifico_ensayo crd inner join crd.hojaCrd HojaCRD inner join crd.momentoSeguimientoEspecifico MSE inner join MSE.momentoSeguimientoGeneral MSG inner join HojaCRD.momentoSeguimientoGeneralHojaCrds MSHoja where MSE.id=:momentoSeguimientoEspecifico and crd.eliminado = 'false' and MSHoja.eliminado = 'false' and MSHoja.momentoSeguimientoGeneral.id = MSG.id";
		
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;

	private boolean gestionar = false;
	private boolean crear = false;
	private Long ideliminarSuj;
	private int pagina;
	private Long idSujeto;
	
	@SuppressWarnings("unchecked")
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
		MomentoSeguimientoEspecifico_ensayo momentoEspecifico = new MomentoSeguimientoEspecifico_ensayo();
		long idEstadoSeguimientoInic=1;
		long idEstadoSeguimientoComp=3;
		long idEstadoSeguimientoFrim=5;
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoInic=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoInic);
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoComp=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoComp);
		EstadoMomentoSeguimiento_ensayo estadoMomentoAtrasadoFirm=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimientoFrim);
		List<MomentoSeguimientoEspecifico_ensayo> listaMomentos = (List<MomentoSeguimientoEspecifico_ensayo>) entityManager.createQuery("select momento from MomentoSeguimientoEspecifico_ensayo momento where momento.sujeto.id=:id and momento.eliminado = 'false' and momento.momentoSeguimientoGeneral.programado = False").setParameter("id", this.idSujeto).getResultList();
		for (int i = 0; i < listaMomentos.size(); i++) {
			momentoEspecifico = listaMomentos.get(i);
			
			if(atrasadoIniciado(momentoEspecifico)){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoInic);
				entityManager.persist(momentoEspecifico);
			}
			if(atrasadoCompletado(momentoEspecifico) ){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoComp);
				entityManager.persist(momentoEspecifico);
			}
			
			if(atrasadoFirmado(momentoEspecifico) ){
				momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomentoAtrasadoFirm);
				entityManager.persist(momentoEspecifico);
			}
		}
		entityManager.flush();
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoFirmado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean firmado = true;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 4 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				firmado = false;
				break;
			}
		}
		return firmado;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoIniciado(MomentoSeguimientoEspecifico_ensayo momeSegui){
		List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
		listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
		boolean completa = false;
		for (int i = 0; i < listaHojas.size(); i++) {
			if(listaHojas.get(i).getEstadoHojaCrd().getCodigo() == 1 && !estaEliminadaMomentoHojaCrd(listaHojas.get(i).getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral(), listaHojas.get(i).getHojaCrd())){
				completa = true;
				break;
			}
		}
		return completa;
		
		
	}
	
	@SuppressWarnings("unchecked")
	public boolean atrasadoCompletado(MomentoSeguimientoEspecifico_ensayo momeSegui){
        List<CrdEspecifico_ensayo> listaHojas = new ArrayList<CrdEspecifico_ensayo>();
        listaHojas = (List<CrdEspecifico_ensayo>) entityManager.createQuery(consultaHojaCrdMomentoSeguimiento).setParameter("momentoSeguimientoEspecifico", momeSegui.getId()).getResultList();
        boolean completa = true;
        for (int i = 0; i < listaHojas.size(); i++) {
                if((listaHojas.get(i).getEstadoHojaCrd().getCodigo() != 3) || (listaHojas.get(i).getEstadoHojaCrd().getCodigo() == 3 && momeSegui.getEstadoMomentoSeguimiento().getCodigo() == 5)){
                        completa = false;
                        break;
                }
        }
        return completa;
		
	}
	
	public boolean estaEliminadaMomentoHojaCrd(MomentoSeguimientoGeneral_ensayo general, HojaCrd_ensayo hoja){
		boolean esta = false;
		MomentoSeguimientoGeneralHojaCrd_ensayo otro = null;
		try {
			otro = (MomentoSeguimientoGeneralHojaCrd_ensayo) this.entityManager.createQuery("select mom from MomentoSeguimientoGeneralHojaCrd_ensayo mom where mom.momentoSeguimientoGeneral.id=:idMomGen and mom.hojaCrd.id=:idHoja").setParameter("idMomGen", general.getId()).setParameter("idHoja", hoja.getId()).getSingleResult();
		} catch (Exception e){
			otro = null;
			e.printStackTrace();
		}		
		if(otro != null && otro.getEliminado())
			esta = true;
		return esta;
	}
	
	

	public Sujeto_ensayo NombreSujetoById() {
		Sujeto_ensayo otro = (Sujeto_ensayo) entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id=:idSujeto")
				.setParameter("idSujeto", idSujeto).getSingleResult();
		
		return otro;
	}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = new Role_ensayo();
		rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}

	// CU 20 Eliminar MS no programado
	public void EliminarInstanciaSuj() {

		MomentoSeguimientoEspecifico_ensayo momentoEliminar = entityManager
				.find(MomentoSeguimientoEspecifico_ensayo.class,
						this.ideliminarSuj);
		momentoEliminar.setEliminado(true);
		entityManager.persist(momentoEliminar);
		entityManager.flush();

	}
	
	public boolean isMonitor(){
		Role_ensayo rolLogueado = (Role_ensayo) this.entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true").setParameter("estudId", this.seguridadEstudio.getEstudioEntidadActivo().getId()).setParameter("idusua", this.user.getId()).getSingleResult();
		return (rolLogueado != null && rolLogueado.getCodigo().equals("ecMon"));
	}

	/*
	 * @SuppressWarnings("unchecked") public List<MomentoSeguimientoEspecifico>
	 * ListaMSProg(){ List<MomentoSeguimientoEspecifico> lista; lista =
	 * (List<MomentoSeguimientoEspecifico>)entityManager.createQuery(
	 * "select ms from MomentoSeguimientoEspecifico ms where ms.id=:idSujeto and ms.momentoSeguimientoGeneral.tipo = 'prog' order By ms.id desc"
	 * ).setParameter("idSujeto", this.idSujeto).getResultList(); return lista;
	 * }
	 * 
	 * @SuppressWarnings("unchecked") public List<MomentoSeguimientoEspecifico>
	 * ListaMSNoProg(){ List<MomentoSeguimientoEspecifico> lista; lista =
	 * (List<MomentoSeguimientoEspecifico>)entityManager.createQuery(
	 * "select ms from MomentoSeguimientoEspecifico ms where ms.id=:idSujeto and ms.momentoSeguimientoGeneral.tipo = 'no p' order By ms.id desc"
	 * ).setParameter("idSujeto", this.idSujeto).getResultList(); return lista;
	 * }
	 */

	public ListarMSNP() {
		setEjbql(EJBQL);
		setMaxResults(10);
		setOrder("ms.id desc");
		this.gestionar = true;
		this.crear = true;
	}

	public void SeleccionarInstanciaSuj(long id) {
		this.setIdeliminarSuj(id);
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

	public boolean isCrear() {
		return crear;
	}

	public void setCrear(boolean crear) {
		this.crear = crear;
	}

	public Long getIdeliminarSuj() {
		return ideliminarSuj;
	}

	public void setIdeliminarSuj(Long ideliminarSuj) {
		this.ideliminarSuj = ideliminarSuj;
	}

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

}
