package gehos.pki.servidores.proxy;

import gehos.pki.entity.Proxy_pki;

import java.util.Arrays;

import javax.ejb.Remove;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;

@Scope(ScopeType.CONVERSATION)
@Name("buscarProxyControlador")
public class BuscarProxyControlador extends EntityQuery<Proxy_pki>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final String EJBQL = "select proxy from Proxy_pki proxy";

	private static final String[] RESTRICTIONS = {
			"lower(proxy.direccion) like concat(lower(#{buscarProxyControlador.direccion}),'%')",
			 };

	private Proxy_pki proxy = null;
	private String direccion = "";
	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;
	
	private int idProxy;
	private boolean open = true;
	
	public BuscarProxyControlador() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(8);
		setOrder("proxy.id desc");
	}
	
	
	@Remove
	@Destroy
	public void clean(){
		System.out.println("El objeto es removido del contexto:::" + "[" + this.toString() + "]");
	}
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){	
		update();
	}
	
	public void eliminar(){
		try {
			this.proxy = entityManager.find(Proxy_pki.class, this.idProxy);	
			this.idProxy = -1;
			entityManager.remove(this.proxy);
			entityManager.flush();
			update(); 			
		
		} catch (Exception e) {
			facesMessages.addToControlFromResourceBundle("btnSi",
					Severity.ERROR, SeamResourceBundle.getBundle().getString("error1"));
			e.printStackTrace();
		}
		
	}
	
	private void update(){
		this.refresh();
		if(this.getResultList() != null && this.getResultList().size() == 0 && 
				this.getFirstResult() != null 
				&& this.getFirstResult() != 0)
			setFirstResult(getFirstResult()-getMaxResults());
	}
	
	
	public void seleccionar(int id){
		this.idProxy = id;
	}
	
	public void abrirCerrar(){
		this.open =! open;
	}
	
	public void buscar(){
		setFirstResult(0);
		setOrder("proxy.id desc");
	}
	
	public void cancelar(){
		
	}
	
	
	//Propiedades
	

	

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public Proxy_pki getProxy() {
		return proxy;
	}

	public void setProxy(Proxy_pki proxy) {
		this.proxy = proxy;
	}

	public int getIdProxy() {
		return idProxy;
	}

	public void setIdProxy(int idProxy) {
		this.idProxy = idProxy;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	
	
}
