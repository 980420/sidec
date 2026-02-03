package gehos.comun.nomencladores;


import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import gehos.comun.mantenimiento.funcionalidades.dataaccess.DBInfo;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("nomencladorEditController")
public class NomencladorEditController {
	
	@In EntityManager entityManager;
	@In(create = true)
	NomencladorDetailController nomencladorDetailController;
	@In(create = true)
	DBInfo dbInfo;
	private String codigoaux,valoraux;
	private NomencladorGenerico nomenclador;
	private List<NomencladorGenerico> listref;
	public NomencladorGenerico getNomenclador() {
		return nomenclador;
	}
	
	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void setNomenclador(NomencladorGenerico nomenclador) {
		this.nomenclador = nomenclador;
		nomencladorDetailController.setNomenclador(this.nomenclador);
		this.listref=nomencladorDetailController.getList();
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> comboboxCodigo(String table){
		String aux=table;
		char change=Character.toLowerCase(aux.charAt(0));
		aux=aux.substring(1);
		aux=change+aux;
		String query="select codigo from nomencladores."+aux;
		return entityManager.createNativeQuery(query).getResultList();
	}
	@End
	public void Salvar(){
		String query = "UPDATE nomencladores." + this.nomenclador.getEntityName() + 
					   " SET ";
		if(this.nomenclador.getTieneCodigo())
			query += " codigo = '" + this.nomenclador.getCodigo() + "' , ";
		if(this.nomenclador.getTieneValor())
			query += " valor = '" + this.nomenclador.getValor() + "' ";
		query += " WHERE id = " + this.nomenclador.getId() + " ";
		
		entityManager.createNativeQuery(query).executeUpdate();
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		List<String[]> ref=dbInfo.getReferencias("nomencladores", this.nomenclador.getEntityName(), "");
		aFacesContext.getExternalContext().getSessionMap().put("nomenclador", this.nomenclador);
		for(int i=0;i<listref.size();i++){
			String aux=listref.get(i).getEntityName();
			char change=Character.toLowerCase(aux.charAt(0));
			aux=aux.substring(1);
			aux=change+aux;
			query="UPDATE nomencladores." + this.nomenclador.getEntityName() + 
			   " SET "+ref.get(i)[1]+" = (select " +
			   		"id from nomencladores."+aux+" where codigo = :cod) where id = :i";
			
			entityManager.createNativeQuery(query).setParameter("cod", listref.get(i).getCodigo()).setParameter("i", this.nomenclador.getId())
			.executeUpdate();
		}
		
	}

	public List<NomencladorGenerico> getListref() {
		return listref;
	}

	public void setListref(List<NomencladorGenerico> listref) {
		this.listref = listref;
	}

	public String getCodigoaux() {
		return codigoaux;
	}

	public void setCodigoaux(String codigoaux) {
		this.codigoaux = codigoaux;
	}

	public String getValoraux() {
		return valoraux;
	}

	public void setValoraux(String valoraux) {
		this.valoraux = valoraux;
	}
	

}



















