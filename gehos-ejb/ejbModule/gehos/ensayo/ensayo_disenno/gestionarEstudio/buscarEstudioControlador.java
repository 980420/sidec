package gehos.ensayo.ensayo_disenno.gestionarEstudio;



import gehos.ensayo.entity.Estudio_ensayo;

import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;

@Name("buscarEstudioControlador")
@Scope(ScopeType.CONVERSATION)
public class buscarEstudioControlador {

	@In
	FacesMessages facesMessages;
	@In EntityManager entityManager;

	@Create
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void nada()
	{
		
	}
	Estudio_ensayo estudio;
	
	public void iniCrearestudio()
	{
		
		estudio=new Estudio_ensayo();
		
		
		
	}
	public void crearEstudio()
	{ 
		try {
			entityManager.persist(estudio);
			entityManager.flush();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}

	public Estudio_ensayo getEstudio() {
		return estudio;
	}
	public void setEstudio(Estudio_ensayo estudio) {
		this.estudio = estudio;
	}
}
