package gehos.comun.mantenimiento.nomencladores;

import gehos.comun.mantenimiento.funcionalidades.dataaccess.DBInfo;



import javax.persistence.EntityManager;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("nomencladorAction")
public class NomencladorAction {
	@In
	EntityManager entityManager;
	@In (create = true)
	DBInfo dbInfo;
	private String nameNomenc;
	public void generateValor(){
		String query="ALTER TABLE nomencladores."+this.nameNomenc+" ADD COLUMN valor character varying";
		entityManager.createNativeQuery(query).executeUpdate();
		
	}
	public void generateCodigo(){
		String query="ALTER TABLE nomencladores."+this.nameNomenc+" ADD COLUMN codigo character varying";
		entityManager.createNativeQuery(query).executeUpdate();
	}
	public String getNameNomenc() {
		return nameNomenc;
	}
	public void setNameNomenc(String nameNomenc) {
		this.nameNomenc = nameNomenc;
		if(!dbInfo.existColumn("nomencladores", this.nameNomenc, "codigo"))
			this.generateCodigo();
		if(!dbInfo.existColumn("nomencladores", this.nameNomenc, "valor"))
			this.generateValor();
	}
	
	
}
