package gehos.configuracion.management.gestionarMedicos;

import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.transaction.Transaction;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.SystemException;

@SuppressWarnings("serial")
@Name("medicoList_configuracionCustom")
@Scope(ScopeType.CONVERSATION)
public class MedicoList_Custom extends EntityQuery<Medico_configuracion> {

	private static final String EJBQL = "select medico from Medico_configuracion medico where medico.eliminado = false";

	private static final String[] RESTRICTIONSA = {
			"lower(medico.matriculaMinisterio) like concat(lower(#{medicoList_configuracionCustom.matriculaMinisterio.trim()}),'%')",
			"lower(medico.matriculaColegioMedico) like concat(lower(#{medicoList_configuracionCustom.matriculaColegio.trim()}),'%')",
			
			"lower(medico.usuario.nombre) like concat(lower(#{medicoList_configuracionCustom.nombre.trim()}),'%')",
			"lower(medico.usuario.primerApellido) like concat(lower(#{medicoList_configuracionCustom.apellido.trim()}),'%')",
			"lower(medico.usuario.segundoApellido) like concat(lower(#{medicoList_configuracionCustom.segundoapellido.trim()}),'%')",
			"#{medicoList_configuracionCustom.idmedico} <> medico.id"
	};
	
	private static final String[] RESTRICTIONS = {
			"#{medicoList_configuracionCustom.idmedico} <> medico.id"
	};
	
	private static final String[] RESTRICTIONSS = {
		"lower(medico.matriculaMinisterio) like concat(lower(#{medicoList_configuracionCustom.matriculaMinisterio.trim()}),'%')",
		"#{medicoList_configuracionCustom.idmedico} <> medico.id"
	};
	
	//otras funcionalidades
	private Parameters parametros = new Parameters();

	@In
	EntityManager entityManager;
	@In FacesMessages facesMessages;
	
	private Medico_configuracion medico = new Medico_configuracion();
	private String nombre = "",apellido = "",name = "",segundoapellido = "";	
	private Long idmedico,ideliminarMed;
	private int error = 0;
	private String matriculaMinisterio = "",matriculaColegio = "";
	private boolean open = true;
	private boolean avanzada = false;
	private String pOrder = "";

	public MedicoList_Custom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(5);
		setOrder("medico.usuario.id desc");
	}
	
	public void cancelar(){
		this.nombre = "";
		this.name = "";
		this.apellido = "";
		this.segundoapellido="";
		this.matriculaColegio="";
		this.matriculaMinisterio = "";
		setFirstResult(0);
	}
	
	public void buscar(boolean avan)
	{
		setFirstResult(0);
		String[] aux=(avan)?(RESTRICTIONSA):(RESTRICTIONSS);
	    setRestrictionExpressionStrings(Arrays.asList(aux));
	    this.refresh();
	}
	
	public void cambiar(boolean a){
		this.avanzada = a;	
	}
	
	public void abrirCerrar(){
		this.open =! open;
	}
	
	@SuppressWarnings("unchecked")
	public String seleccionarModificar() throws IllegalStateException, SecurityException, SystemException {
		try {
			error = 0;
			List<Medico_configuracion> medConcu = entityManager.createQuery("select med from Medico_configuracion med where med.id =:id and med.eliminado = true").setParameter("id", idmedico).getResultList();
			if (medConcu.size() != 0) {
				throw new Exception("eliminado");				
			}	
			return "good";
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();	
			return "fail";
		}
						
	}
	
	@SuppressWarnings("unchecked")
	public String seleccionarVer() throws IllegalStateException, SecurityException, SystemException {
		try {
			error = 0;
			List<Medico_configuracion> medConcu = entityManager.createQuery("select med from Medico_configuracion med where med.id =:id and med.eliminado = true").setParameter("id", idmedico).getResultList();
			if (medConcu.size() != 0) {
				throw new Exception("eliminado");				
			}	
			return "good";
		} catch (Exception e) {
			error = 1;
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
			Transaction.instance().rollback();	
			return "fail";
		}
						
	}
	
	@SuppressWarnings("unchecked")
	public String seleccionarVer(Long idmedico) throws IllegalStateException, SecurityException, SystemException {
		try {
			List<Medico_configuracion> medConcu = entityManager.createNamedQuery("select med from Medico_configuracion med where med.id =:id and med.eliminado = false").setParameter("id", idmedico).getResultList();
			if (medConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
				Transaction.instance().rollback();
				return "fail";
			}
			else {
				this.idmedico = idmedico;
				return "good";
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;				
	}
	
	@SuppressWarnings("unchecked")
	public void eliminar(){
		try {
			Medico_configuracion aux = (Medico_configuracion) entityManager.createQuery("select med from Medico_configuracion med where med.id =:id and med.eliminado =false").setParameter("id", ideliminarMed).getSingleResult();
			this.idmedico = -1l;
			aux.setEliminado(true);
			entityManager.persist(aux);
			
			List<MedicoInEntidad_configuracion> medicoInEntidad = entityManager.createQuery("select medInEnt from MedicoInEntidad_configuracion medInEnt where medInEnt.medico.id =:id").setParameter("id", ideliminarMed).getResultList();
			for (int i = 0; i < medicoInEntidad.size(); i++) {
				medicoInEntidad.get(i).setEliminado(true);
			}		
			entityManager.flush();
			
			if(getResultList().size() == 0 && getFirstResult() != 0)
				setFirstResult(getFirstResult()-getMaxResults());
		} catch (NoResultException e) {
			facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");	
		}
		catch (Exception e) {
			e.printStackTrace();
		}	 		
	}
	
	@Begin(flushMode = FlushModeType.MANUAL,join = true)
	public void begin(){			
	}
	@SuppressWarnings("unchecked")
	public void seleccionar() throws IllegalStateException, SecurityException, SystemException{
		try {
			List<Medico_configuracion> medConcu = entityManager.createQuery("select med from Medico_configuracion med where med.id =:id and med.eliminado = true").setParameter("id", ideliminarMed).getResultList();
			if (medConcu.size() != 0) {
				facesMessages.addToControlFromResourceBundle("userSearch", Severity.ERROR, "eliminado");
				Transaction.instance().rollback();			
			}	
		} catch (Exception e) {
			e.printStackTrace();	
			Transaction.instance().rollback();
		}				
	}

	public Medico_configuracion getMedico() {
		return medico;
	}

	public String getMatriculaMinisterio() {
		return matriculaMinisterio;
	}

	public void setMatriculaMinisterio(String matriculaMinisterio) {
		this.matriculaMinisterio = parametros.decodec(matriculaMinisterio);
	}

	public String getMatriculaColegio() {
		return matriculaColegio;
	}

	public void setMatriculaColegio(String matriculaColegio) {
		this.matriculaColegio = parametros.decodec(matriculaColegio);
	}

	public void setMedico(Medico_configuracion medico) {
		this.medico = medico;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = parametros.decodec(nombre);
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = parametros.decodec(apellido);
	}

	public Long getIdmedico() {
		return idmedico;
	}

	public void setIdmedico(Long idmedico) {
		this.idmedico = idmedico;		
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;
		
			String[] aux=(avanzada)?(RESTRICTIONSA):(RESTRICTIONSS);
		    setRestrictionExpressionStrings(Arrays.asList(aux));
		    this.refresh();
		
	}

	public Long getIdeliminarMed() {
		return ideliminarMed;
	}

	public void setIdeliminarMed(Long ideliminarMed) {
		this.ideliminarMed = ideliminarMed;
	}

	public String getSegundoapellido() {
		return segundoapellido;
	}

	public void setSegundoapellido(String segundoapellido) {
		this.segundoapellido = parametros.decodec(segundoapellido);
	}

	public String getPOrder() {
		return pOrder;
	}

	public void setPOrder(String order) {
		pOrder = order;
	}

	public int getError() {
		return error;
	}

	public void setError(int error) {
		this.error = error;
	}

	

	
}
