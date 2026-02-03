package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.*;


import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.framework.EntityQuery;


import sun.reflect.ReflectionFactory.GetReflectionFactoryAction;

import java.util.Arrays;

import javax.persistence.EntityManager;

@Name("usuarioMedicoList_configuracionCustom")
@Scope(ScopeType.CONVERSATION)
public class UsuarioList_configuracionCustom extends EntityQuery<Usuario_configuracion> {	
	
	@Override
	public String getEjbql() {
		return "select user from Usuario_configuracion user where user.id not in (select medico.id from Medico_configuracion medico) and user.id not in (select enfermero.id from Enfermera_configuracion enfermero)";
	}
	
	private static final String[] RESTRICTIONSA = {
		
		"lower(user.nombre) like concat(lower(#{usuarioMedicoList_configuracionCustom.nombre.trim()}),'%')",
		"lower(user.username) like concat(lower(#{usuarioMedicoList_configuracionCustom.username.trim()}),'%')",		
		"lower(user.primerApellido) like concat(lower(#{usuarioMedicoList_configuracionCustom.primerapellido.trim()}),'%')",
		"lower(user.segundoApellido) like concat(lower(#{usuarioMedicoList_configuracionCustom.segundoapellido.trim()}),'%')",
		
	 };
	
	private static final String[] RESTRICTIONS = {
		
	};

	private static final String[] RESTRICTIONSS = {
	"lower(user.username) like concat(lower(#{usuarioMedicoList_configuracionCustom.nicik.trim()}),'%')",	
	};

	@In
	EntityManager entityManager;
	private Usuario_configuracion usuario = new Usuario_configuracion();
	private long id;	
	private boolean open = true;
	private boolean avanzada = false;

	private long idUserSelec= -1;
	

	private String nombre = "",username = "",primerapellido= "",segundoapellido= "",name= "",nicik= "";
	private String critB1,critB2,critB3,critB4,critB5;
	private Boolean critB6,avz;
	
	public void cambiar(boolean a){
		this.avanzada = a;
		
		if (a) {
			username = this.nicik ;
		}
		else {
			nicik = this.username  ;
		}
	}
	
	public void cancelar(){
		this.nombre = "";
		this.username = "";	
		this.primerapellido= "";
		this.segundoapellido = "";
	}
	
	public void buscar(boolean avan)
	{
		setFirstResult(0);
		String[] aux=(avan)?(RESTRICTIONSA):(RESTRICTIONSS);
	    setRestrictionExpressionStrings(Arrays.asList(aux));
	    this.refresh();
	}
	
	public void abrirCerrar(){
		this.open =! open;
	}

	public String getPrimerapellido() {
		return primerapellido;
	}

	public void setPrimerapellido(String primerapellido) {
		this.primerapellido = primerapellido;
	}

	public String getSegundoapellido() {
		return segundoapellido;
	}

	public void setSegundoapellido(String segundoapellido) {
		this.segundoapellido = segundoapellido;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNicik() {
		return nicik;
	}

	public void setNicik(String nicik) {
		this.nicik = nicik;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){		
	}
	
	public long getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void seleccionar(int idelim)
	{
		this.id = idelim;
	}
	

	public UsuarioList_configuracionCustom() {
		setEjbql(this.getEjbql());
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setMaxResults(5);
		setOrder("user.id desc");
	}

	@Override
	public Integer getMaxResults() {
		return 5;
	}

	public Usuario_configuracion getUsuario() {
		return usuario;
	}
	
	public void setUsuario(Usuario_configuracion usuario) {
		this.usuario = usuario;
	}
	
	public String eliminar() {
		
		Usuario_configuracion aux = (Usuario_configuracion)entityManager.find(Usuario_configuracion.class, this.id);
		
		aux.setEliminado(true);
		entityManager.persist(aux);
		entityManager.flush();
		return "access";
	}
	
	public String cargo(int id){
		String result = (String)this.getEntityManager().createQuery("select c.valor from Usuario_configuracion u join u.cargoFuncionarios c where u.id = :id")
			.setParameter("id", id)
			.getSingleResult();
		
		result = (result == null || result.isEmpty())? "-": result;
		
		return result;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getCritB1() {
		return critB1;
	}

	public void setCritB1(String critB1) {
		this.critB1 = critB1;
	}

	public String getCritB2() {
		return critB2;
	}

	public void setCritB2(String critB2) {
		this.critB2 = critB2;
	}

	public String getCritB3() {
		return critB3;
	}

	public void setCritB3(String critB3) {
		this.critB3 = critB3;
	}

	public String getCritB4() {
		return critB4;
	}

	public void setCritB4(String critB4) {
		this.critB4 = critB4;
	}

	public String getCritB5() {
		return critB5;
	}

	public void setCritB5(String critB5) {
		this.critB5 = critB5;
	}

	public Boolean getCritB6() {
		return critB6;
	}

	public void setCritB6(Boolean critB6) {
		this.critB6 = critB6;
	}

	public Boolean getAvz() {
		return avz;
	}

	public void setAvz(Boolean avz) {
		this.avz = avz;
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;
	}

	public long getIdUserSelec() {
		return idUserSelec;
	}

	public void setIdUserSelec(long idUserSelec) {
		this.idUserSelec = idUserSelec;
	}
	
	
}
