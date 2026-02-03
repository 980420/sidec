package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;



import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("buscarHojaCrd")
@Scope(ScopeType.CONVERSATION)
public class BuscarHojaCrd {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;
	@In IBitacora bitacora;	
	
	
	private HojaCrd_ensayo hojaCrd 	= new HojaCrd_ensayo();
	
	@Begin(join=true,flushMode=FlushModeType.MANUAL)
	@Create
	public void start(){}
	
	public BuscarHojaCrd(){}
	
	
	public void seleccionarHojaCrd(HojaCrd_ensayo hojaCrd)
	{
		this.hojaCrd = hojaCrd;
	}
	
	
	public void eliminar()
	{
		hojaCrd.setEliminado(true);
		entityManager.persist(hojaCrd);
		entityManager.flush();
	}

	public HojaCrd_ensayo getHojaCrd() {
		return hojaCrd;
	}

	public void setHojaCrd(HojaCrd_ensayo hojaCrd) {
		this.hojaCrd = hojaCrd;
	}	
	

	

	

}
