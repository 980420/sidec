package gehos.pki.servidores.tsp;

import gehos.pki.entity.ServidorSelloTiempo_pki;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

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

@Name("buscarServidorTSPControlador")
@Scope(ScopeType.CONVERSATION)
public class BuscarServidorTSPControlador extends EntityQuery<ServidorSelloTiempo_pki>{
	
	
	private static final String EJBQL = "select server from ServidorSelloTiempo_pki server";

	private static final String[] RESTRICTIONS = {
			"lower(server.url) like concat(lower(#{buscarServidorControlador.url}),'%')",
			"lower(server.serverProtocol.protocolType) like " +
			"concat(lower(#{buscarServidorControlador.server_protocol}),'%')",
			"server.serverType.typeName = #{buscarServidorControlador.server_type}",};

	private ServidorSelloTiempo_pki server = new ServidorSelloTiempo_pki();
	
	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;
	
	private int pagina;
	private boolean open = true;
	private boolean avanzada = false;
	private String url = "";
	private boolean activo;
	private String act;
	private int idServer;
	private String server_type = "";
	
	public BuscarServidorTSPControlador(){
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(8);
		setOrder("server.id desc");
	}
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){	
		update();
	}
	
	public void cambiarTipodeBusqueda(boolean a){
		this.avanzada = a;
	}
	
	public void buscar(boolean avan)
	{
		setFirstResult(0);
		setOrder("server.id desc");
	}
	
	public void eliminar(){
		try {
			server = entityManager.find(ServidorSelloTiempo_pki.class, this.idServer);	
			this.idServer = -1;
			entityManager.remove(server);
			entityManager.flush();
			update();		
		
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, "msg_servidorEnUso_modConfig");
		}
		
	}
	
	
	public List<String> activoList(){
		List<String> list = new ArrayList<String>(3);
		list.add(SeamResourceBundle.getBundle().getString("seleccione"));
		list.add(SeamResourceBundle.getBundle().getString("si"));
		list.add(SeamResourceBundle.getBundle().getString("no"));
		return list;
	}
	
	private void update(){
		this.refresh();
		if(this.getResultList() != null && this.getResultList().size() == 0 && 
				this.getFirstResult() != null 
				&& this.getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	public void seleccionar(int id){
		this.idServer = id;
	}
	
	public void abrirCerrar(){
		this.open =! open;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	

	public boolean isActivo() {
		return activo;
	}

	public void setActivo(boolean activo) {
		this.activo = activo;
	}

	public String getAct() {
		return act;
	}

	public void setAct(String act) {
		this.act = act;
		if(this.act.equals(SeamResourceBundle.getBundle().getString("si"))){
			this.setEjbql(EJBQL + " where server.activo = true");
		}
			
		else if(this.act.equals(SeamResourceBundle.getBundle().getString("no"))){
			this.setEjbql(EJBQL + " where server.activo = false");
		}
		else this.setEjbql(EJBQL);
	}

	public String getServer_type() {
		return server_type;
	}

	public void setServer_type(String serverType) {
		server_type = serverType;
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
