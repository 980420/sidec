package gehos.comun.nomencladores;

import gehos.comun.mantenimiento.funcionalidades.dataaccess.DBInfo;
import java.util.LinkedList;
import java.util.List;
import javax.persistence.EntityManager;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("nomencladoresDetailController")
public class NomencladoresDetailController {
	
	@In EntityManager entityManager;
	@In(create = true)
	DBInfo dbInfo;
	private String nombreNomenclador = "";
	private String valor = "";
	private String codigo = "";
	private boolean tieneValor;
	private boolean tieneCodigo;
	private String order = "";
	private String order2 = "";
	
	@SuppressWarnings("unchecked")
	private List attList; 
	
	public boolean constructor(){
		boolean[] res=new boolean[2];
		res=dbInfo.existColumns("nomencladores", this.nombreNomenclador, new String[]{"codigo","valor"});
		this.attList=dbInfo.getListauxiliar();
		this.tieneCodigo=res[0];
		this.tieneValor=res[1];
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<NomencladorGenerico> getResultList() throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException{
		
		if(this.nombreNomenclador != null && this.nombreNomenclador != ""){
			String query = "select * from nomencladores." + this.nombreNomenclador;
			if(this.tieneCodigo || this.tieneValor){
				query += " where ";
				if(this.tieneCodigo && this.tieneValor){
					query += "(codigo like '" + this.codigo +"%' or codigo is null) and (valor like '" + this.valor + "%' or valor is null)";
				}
				else if(this.tieneCodigo){
					query += "codigo like '" + this.codigo + "%' or codigo is null ";
				}
				else if(this.tieneValor){
					query += "valor like '" + this.valor + "%' or valor is null ";
				}
			}
			if(this.order.equals("codigo asc")){query += " order by codigo asc";}
			else if(this.order.equals("codigo desc")){query += " order by codigo desc";	}
			else if(this.order.equals("valor asc")){query += " order by valor asc";	}
			else if(this.order.equals("valor desc")){query += " order by valor desc";	}
			
			List nomenc = entityManager.createNativeQuery(query).getResultList();

			List<NomencladorGenerico> resultado = new LinkedList<NomencladorGenerico>();
			for (int i = 0; i < nomenc.size(); i++) {
				resultado.add(new NomencladorGenerico());
			}
			
			for (int j = 0; j < attList.size(); j++) {
				Object[] temp = (Object[])attList.get(j);
				Short indice = (Short)temp[0];
				String nombre_atributo = (String)temp[1];
				
				for (int i = 0; i < resultado.size(); i++) {
					Object value = ((Object[])nomenc.get(i))[indice-1];
					if(nombre_atributo.equals("id")){resultado.get(i).setId((Integer)value);}
					else if(nombre_atributo.equals("cid")){	resultado.get(i).setCid((Long)value);}
					else if(nombre_atributo.equals("version")){	resultado.get(i).setVersion((Integer)value);}
					else if(nombre_atributo.equals("eliminado")){resultado.get(i).setEliminado((Boolean)value);	}
					else if(nombre_atributo.equals("valor")){resultado.get(i).setValor((String)value);}
					else if(nombre_atributo.equals("codigo")){resultado.get(i).setCodigo((String)value);}
					resultado.get(i).setTieneValor(this.tieneValor);
					resultado.get(i).setTieneCodigo(this.tieneCodigo);
					resultado.get(i).setEntityName(this.nombreNomenclador);
					
				}
			}
			
			return resultado;
		}
		else return new LinkedList<NomencladorGenerico>();
	}
	
	
	public List<String[]> getNomencladoresReferencias(){
		return dbInfo.getReferencias("nomencladores", this.nombreNomenclador, this.order2);
	}
	
	public String getNombreNomenclador() {
		return nombreNomenclador;
	}

	public void setNombreNomenclador(String nombreNomenclador) {
		this.nombreNomenclador = nombreNomenclador;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isTieneValor() {
		return tieneValor;
	}

	public void setTieneValor(boolean tieneValor) {
		this.tieneValor = tieneValor;
	}

	public boolean isTieneCodigo() {
		return tieneCodigo;
	}

	public void setTieneCodigo(boolean tieneCodigo) {
		this.tieneCodigo = tieneCodigo;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getOrder2() {
		return order2;
	}

	public void setOrder2(String order2) {
		this.order2 = order2;
	}
	@SuppressWarnings("unchecked")
	public List getAttList() {
		return attList;
	}
	
}
