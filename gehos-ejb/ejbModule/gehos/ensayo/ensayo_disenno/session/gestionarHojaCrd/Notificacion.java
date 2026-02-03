package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHoja;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.GestionarHojaGrupoControlador;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.IRuleAction;
import gehos.ensayo.ensayo_disenno.session.reglas.helpers.actions.user_input.IActionWithUserInput;
import gehos.ensayo.ensayo_disenno.session.reglas.util.IdUtil;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.Notificacion_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.util.ArrayList;
import java.util.List;

import javax.faces.component.UIInput;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("notificacion")
@Scope(ScopeType.CONVERSATION)
public class Notificacion extends IActionWithUserInput {

	protected @In EntityManager entityManager;	
	protected @In IBitacora bitacora;	
	protected @In(create = true) FacesMessages facesMessages;
	@In(create = true) IdUtil idUtil;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	@In private Usuario user;
	@In private IActiveModule activeModule;
	@In(value = "gestionarHoja", required = false)
	GestionarHoja gestionarHoja;
	@In(value = "gestionarHojaGrupoControlador_ec", required = false)
	GestionarHojaGrupoControlador gestionarHojaGrupo;

	@Override
	public String Export(){
		return null;
	}

	@Override
	public boolean CanExport(){
		return false;
	}

	private Variable_ensayo variableRegla;
	private Variable_ensayo variablereglaGlobal = null;
	private Usuario_ensayo usuario = new Usuario_ensayo();

	private String titEst = "";
	private String codEst = "";
	private String idSujeto = "";
	private Integer edad;
	private String tipoEdad;
	private String prodcInv = "";
	private String sitioInv = "";
	private String invest = "";
	private String promo = "";
	private String fechaOcu;
	private String fechaUAdm;
	private String notifico = "";
	private String fechaNoti;
	private String viaNoti;
	private String descrEvent = "";
	private String accTomada = "";
	private String nombN = "";
	private String apeN = "";
	private String cargoN = "";
	private String direccN = "";
	private String telefN = "";
	private String opinInv = "";

	private Estudio_ensayo estudioE;
	private Entidad_ensayo entidadA;
	private Notificacion_ensayo notifi;
	CrdEspecifico_ensayo objetoGestionarHoja;
	List<String> productos = new ArrayList<String>();

	public void activar(){
		if (!this.notifico.equals(SeamResourceBundle.getBundle().getString("lbl_n_sii"))){
			if (this.fechaNoti != null)
				this.setFechaNoti(null);
		}
	}

	public boolean activarNotiFecha(){
		return (!this.notifico.equals("") && this.notifico.equals(SeamResourceBundle.getBundle().getString("lbl_n_sii")));
	}

	@SuppressWarnings("unchecked")
	public void initConversation(){
		this.limpiar();
		this.setCompleted(false);
		this.usuario = (Usuario_ensayo) this.entityManager.find(Usuario_ensayo.class, this.user.getId());
		this.estudioE = this.seguridadEstudio.getEstudioEntidadActivo().getEstudio();
		this.entidadA = this.seguridadEstudio.getEstudioEntidadActivo().getEntidad();
		this.objetoGestionarHoja = this.gestionarHoja.getHoja();
		this.notifi = null;
		this.titEst = this.estudioE.getNombre();
		this.codEst = this.estudioE.getIdentificador();
		this.idSujeto = this.gestionarHoja.getHoja().getMomentoSeguimientoEspecifico().getSujeto().getCodigoPaciente();
		this.nombN = this.user.getNombre();
		this.apeN = this.user.getPrimerApellido();
		this.sitioInv = this.entidadA.getNombre();
		this.productos = this.entityManager.createQuery("select nombre from EProducto_ensayo pro where pro.estudio.id=:idEst").setParameter("idEst", this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
		for (int j = 0; j < this.productos.size(); j++)
			this.prodcInv = ((j == 0) ? this.productos.get(j) : (this.prodcInv + ", " + this.productos.get(j)));
		this.telefN = ((this.user.getTelefono().equals("") || this.user.getTelefono().equals("prueba")) ? null : this.user.getTelefono()); 
		if (this.notifi != null){
			this.idSujeto = this.notifi.getidentSujeto();
			this.edad = this.notifi.getedadSuj();
			this.invest = this.notifi.getinvestigador();
			this.promo = this.notifi.getpromotor();
			this.descrEvent = this.notifi.getdescrEven();
			this.accTomada = this.notifi.getaccTomada();
			this.opinInv = this.notifi.getopiInves();
			this.fechaOcu = this.notifi.getfechaOcu();
			this.fechaUAdm = this.notifi.getfechaUltA();
			this.fechaNoti = this.notifi.getfecha_notifico();
			this.viaNoti = this.notifi.getViaNoti();
			this.cargoN = this.notifi.getcargoN();
			this.direccN = this.notifi.getdireccN();
			this.prodcInv = this.notifi.getprodInv();
			this.sitioInv = this.notifi.getsitioInv();
			this.telefN = this.notifi.gettelefonoN();
			if (this.notifi.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_dias")))
				this.tipoEdad = SeamResourceBundle.getBundle().getString("lbl_n_dias");
			else if (this.notifi.getTipoEdad().equals(SeamResourceBundle.getBundle().getString("lbl_n_mes")))
				this.tipoEdad = SeamResourceBundle.getBundle().getString("lbl_n_mes");
			else 
				this.tipoEdad = SeamResourceBundle.getBundle().getString("lbl_n_anno");
			this.notifico = (this.notifi.getnotifico() ? SeamResourceBundle.getBundle().getString("lbl_n_sii") : SeamResourceBundle.getBundle().getString("lbl_n_no")); 
		}
	}

	public UIInput FindComponent(String componentId){
		UIViewRoot viewRoot = FacesContext.getCurrentInstance().getViewRoot();
		UIInput component = (UIInput) viewRoot.findComponent(componentId);
		return component;
	}

	public void limpiar(){
		this.productos = null;
		this.edad = null;
		this.tipoEdad = "";
		this.prodcInv = "";
		this.sitioInv = "";
		this.invest = "";
		this.promo = "";
		this.fechaOcu = null;
		this.fechaUAdm = null;
		this.notifico = "";
		this.fechaNoti = null;
		this.descrEvent = "";
		this.accTomada = "";
		this.cargoN = "";
		this.direccN = "";
		this.viaNoti = "";
		this.opinInv = "";
		this.notifi = new Notificacion_ensayo();
	}

	public void cleanFromGroup(){
		this.limpiar();
		this.variableRegla = null;
	}

	@Override
	public void Process(){
		try {
			if(this.notifi == null)
				this.notifi = new Notificacion_ensayo();
			if(this.variableRegla != null){
				this.notifi.setVariable(this.variableRegla);
				this.notifi.setCrdEspecifico(this.gestionarHoja.getHoja());
				this.notifi.settituloEst(this.titEst);
				this.notifi.setcodigoEst(this.codEst);
				this.notifi.setidentSujeto(this.idSujeto);
				this.notifi.setedadSuj(this.edad);
				this.notifi.setUsuario(this.usuario);
				if(this.tipoEdad.equals(SeamResourceBundle.getBundle().getString("lbl_n_dias")))
					this.notifi.setTipoEdad(SeamResourceBundle.getBundle().getString("lbl_n_dias"));
				else if(this.tipoEdad.equals(SeamResourceBundle.getBundle().getString("lbl_n_mes")))
					this.notifi.setTipoEdad(SeamResourceBundle.getBundle().getString("lbl_n_mes"));
				else
					this.notifi.setTipoEdad(SeamResourceBundle.getBundle().getString("lbl_n_anno"));
				this.notifi.setprodInv(this.prodcInv);
				this.notifi.setsitioInv(this.sitioInv);
				this.notifi.setinvestigador(this.invest);
				this.notifi.setpromotor(this.promo);
				this.notifi.setfechaOcu(this.fechaOcu);
				this.notifi.setfechaUltA(this.fechaUAdm);
				this.notifi.setnotifico(this.notifico.equals(SeamResourceBundle.getBundle().getString("lbl_n_sii")));
				this.notifi.setfecha_notifico(this.fechaNoti);
				this.notifi.setViaNoti(this.viaNoti);
				this.notifi.setdescrEven(this.descrEvent);
				this.notifi.setaccTomada(this.accTomada);
				this.notifi.setnombreN(this.nombN);
				this.notifi.setapellidosN(this.apeN);
				this.notifi.setcargoN(this.cargoN);
				this.notifi.setdireccN(this.direccN);
				this.notifi.settelefonoN(this.telefN.toString());
				this.notifi.setopiInves(this.opinInv);
				this.notifi.setIdSujeto(this.objetoGestionarHoja.getMomentoSeguimientoEspecifico().getSujeto().getId());
				this.notifi.setIdEstudio(this.estudioE.getId());
				this.notifi.setIdEntidad(this.estudioE.getEntidad().getId());
				if(this.variableRegla.getGrupoVariables() != null && this.gestionarHojaGrupo.getGroup() != null){
					if(this.gestionarHojaGrupo.getData() != null && this.gestionarHojaGrupo.getData().getData() != null && !this.gestionarHojaGrupo.getData().getData().isEmpty() && this.gestionarHojaGrupo.getData().getData().containsKey(this.variableRegla.getId()) && this.variableRegla.getGrupoVariables().getId() == this.gestionarHojaGrupo.getGroup().getId()){
						this.gestionarHojaGrupo.getData().getData().get(this.variableRegla.getId()).setNotification(this.notifi);
						this.gestionarHojaGrupo.getData().setNotification(this.notifi);
					}
				} else if(this.variableRegla.getGrupoVariables() == null && this.variableRegla.getSeccion() != null){
					if(this.gestionarHoja.getMapWD() != null && !this.gestionarHoja.getMapWD().isEmpty() && this.gestionarHoja.getMapWD().containsKey(this.variableRegla.getSeccion().getId())){
						if(this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()) != null && this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()).getData() != null && !this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()).getData().isEmpty() && this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()).getData().containsKey(this.variableRegla.getId())){
							this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()).getData().get(this.variableRegla.getId()).setNotification(this.notifi);
							this.gestionarHoja.getMapWD().get(this.variableRegla.getSeccion().getId()).setNotification(this.notifi);
						}
					}
				} 
				this.setCompleted(true);
			}			
		} catch (Exception e){
			System.out.print(e);
		}
	}

	public EntityManager getEntityManager(){
		return entityManager;
	}

	public void setEntityManager(EntityManager entityManager){
		this.entityManager = entityManager;
	}

	public IBitacora getBitacora(){
		return bitacora;
	}

	public void setBitacora(IBitacora bitacora){
		this.bitacora = bitacora;
	}

	public FacesMessages getFacesMessages(){
		return facesMessages;
	}

	public void setFacesMessages(FacesMessages facesMessages){
		this.facesMessages = facesMessages;
	}

	public IdUtil getIdUtil(){
		return idUtil;
	}

	public void setIdUtil(IdUtil idUtil){
		this.idUtil = idUtil;
	}

	public SeguridadEstudio getSeguridadEstudio(){
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio){
		this.seguridadEstudio = seguridadEstudio;
	}

	public Usuario getUser(){
		return user;
	}

	public void setUser(Usuario user){
		this.user = user;
	}

	public IActiveModule getActiveModule(){
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule){
		this.activeModule = activeModule;
	}

	public GestionarHoja getGestionarHoja(){
		return gestionarHoja;
	}

	public void setGestionarHoja(GestionarHoja gestionarHoja){
		this.gestionarHoja = gestionarHoja;
	}

	public String getTitEst(){
		return titEst;
	}

	public void setTitEst(String titEst){
		this.titEst = titEst;
	}

	public String getCodEst(){
		return codEst;
	}

	public void setCodEst(String codEst){
		this.codEst = codEst;
	}

	public String getIdSujeto(){
		return idSujeto;
	}

	public void setIdSujeto(String idSujeto){
		this.idSujeto = idSujeto;
	}

	public Integer getEdad(){
		return edad;
	}

	public void setEdad(Integer edad){
		this.edad = edad;
	}

	public String getProdcInv(){
		return prodcInv;
	}

	public void setProdcInv(String prodcInv){
		this.prodcInv = prodcInv;
	}

	public String getSitioInv(){
		return sitioInv;
	}

	public void setSitioInv(String sitioInv){
		this.sitioInv = sitioInv;
	}

	public String getInvest(){
		return invest;
	}

	public void setInvest(String invest){
		this.invest = invest;
	}

	public String getPromo(){
		return promo;
	}

	public void setPromo(String promo){
		this.promo = promo;
	}

	public String getFechaOcu(){
		return fechaOcu;
	}

	public void setFechaOcu(String fechaOcu){
		this.fechaOcu = fechaOcu;
	}

	public String getFechaUAdm(){
		return fechaUAdm;
	}

	public void setFechaUAdm(String fechaUAdm){
		this.fechaUAdm = fechaUAdm;
	}

	public String getFechaNoti(){
		return fechaNoti;
	}

	public void setFechaNoti(String fechaNoti){
		this.fechaNoti = fechaNoti;
	}

	public String getDescrEvent(){
		return descrEvent;
	}

	public void setDescrEvent(String descrEvent){
		this.descrEvent = descrEvent;
	}

	public String getAccTomada(){
		return accTomada;
	}

	public void setAccTomada(String accTomada){
		this.accTomada = accTomada;
	}

	public String getNombN(){
		return nombN;
	}

	public void setNombN(String nombN){
		this.nombN = nombN;
	}

	public String getApeN(){
		return apeN;
	}

	public void setApeN(String apeN){
		this.apeN = apeN;
	}

	public String getCargoN(){
		return cargoN;
	}

	public void setCargoN(String cargoN){
		this.cargoN = cargoN;
	}

	public String getDireccN(){
		return direccN;
	}

	public void setDireccN(String direccN){
		this.direccN = direccN;
	}

	public String getOpinInv(){
		return opinInv;
	}

	public void setOpinInv(String opinInv){
		this.opinInv = opinInv;
	}

	public String getNotifico(){
		return notifico;
	}

	public void setNotifico(String notifico){
		this.notifico = notifico;
	}

	public String getTipoEdad(){
		return tipoEdad;
	}

	public void setTipoEdad(String tipoEdad){
		this.tipoEdad = tipoEdad;
	}

	public Variable_ensayo getVariableRegla(){
		return variableRegla;
	}

	public void setVariableRegla(Variable_ensayo variableRegla){
		this.variableRegla = variableRegla;
	}

	public Variable_ensayo getVariablereglaGlobal(){
		return variablereglaGlobal;
	}

	public void setVariablereglaGlobal(Variable_ensayo variablereglaGlobal){
		this.variablereglaGlobal = variablereglaGlobal;
	}

	public String getViaNoti(){
		return viaNoti;
	}

	public void setViaNoti(String viaNoti){
		this.viaNoti = viaNoti;
	}

	public String getTelefN(){
		return telefN;
	}

	public void setTelefN(String telefN){
		this.telefN = telefN;
	}

	@Override
	public void setAction(IRuleAction action){
		super.setAction(action);
		if(action != null && action.getRootVariable() != null && this.variableRegla == null)
			this.variableRegla = action.getRootVariable();
	}	

}