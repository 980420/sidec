package gehos.ensayo.ensayo_conduccion.gestionarNotas;


import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.Nota_ensayo;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;


@Name("buscarNota")
@Scope(ScopeType.CONVERSATION)
public class BuscarNota {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;
	
	Nota_ensayo nota = new Nota_ensayo();
	long idNota;
	Long cid = 1L;
	
	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	@Create
	public void start(){}
	
	public BuscarNota(){}
	
	
	/*Getters and Setters*/
	public void seleccionar(Nota_ensayo notaAux) {
		nota = notaAux;
	}
	
	
	@End
	public void eliminar()
	{
		try{
			cid=bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("bitEliminar"));
	
			
			nota.setEliminado(true);
			entityManager.persist(nota);
			entityManager.flush();
			
		} catch (Exception e) {
			System.err.print(e.getMessage());
		}
	}


	public long getIdNota() {
		return idNota;
	}


	public void setIdNota(long idNota) {
		this.idNota = idNota;
	}


	public Long getCid() {
		return cid;
	}


	public void setCid(Long cid) {
		this.cid = cid;
	}

}
