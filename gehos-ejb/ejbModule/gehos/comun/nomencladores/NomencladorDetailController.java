package gehos.comun.nomencladores;

import java.util.ArrayList;
import java.util.List;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import gehos.comun.mantenimiento.funcionalidades.dataaccess.DBInfo;
import org.jboss.seam.Component;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

@Name("nomencladorDetailController")
public class NomencladorDetailController {
	
	@In(create = true)
	NomencladoresDetailController nomencladoresDetailController;
	@In(create = true)
	DBInfo dbInfo;
	
	public void constructor(){
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		if(aFacesContext.getExternalContext().getSessionMap().get("nomenclador") != null){
			this.nomenclador = (NomencladorGenerico)aFacesContext.getExternalContext().getSessionMap().get("nomenclador");
			aFacesContext.getExternalContext().getSessionMap().put("nomenclador",null);
		}
		
	}
	
	private NomencladorGenerico nomenclador;

	public NomencladorGenerico getNomenclador() {
		return nomenclador;
	}


	public void setNomenclador(NomencladorGenerico nomenclador) {
		this.nomenclador = nomenclador;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<NomencladorGenerico> getList(){
		
		List<NomencladorGenerico> res=new ArrayList<NomencladorGenerico>();
		if(this.nomenclador==null)
			System.out.println("Es nulo");
		List<String[]> ref=dbInfo.getReferencias("nomencladores", this.nomenclador.getEntityName(), "");
		int len=ref.size();
		for(int i=0;i<len;i++){
			res.add(new NomencladorGenerico());
		}
		for(int i=0;i<len;i++){
			boolean[] aux1=dbInfo.existColumns("nomencladores", ref.get(i)[0], new String[]{"codigo","valor"});
			List aux2=dbInfo.getListauxiliar();
			String colun=ref.get(i)[1];
			res.get(i).setTieneCodigo(aux1[0]);
			res.get(i).setTieneValor(aux1[1]);
			res.get(i).setEntityName(ref.get(i)[0]);
			String queryy = "select * from nomencladores." + ref.get(i)[0]+
			" where id = (select "+colun+" from nomencladores."+this.nomenclador.getEntityName()+
			" where id = "+this.nomenclador.getId()+")";
			List resultadoqueryy=((EntityManager)Component.getInstance("entityManager")).createNativeQuery(queryy).getResultList();
			for (int m = 0; m < aux2.size(); m++) {
				Object[] temp = (Object[])aux2.get(m);
				Short indice = (Short)temp[0];
				String nombre_atributo = (String)temp[1];
				
				for (int k = 0; k < resultadoqueryy.size(); k++) {
					Object value = ((Object[])resultadoqueryy.get(i))[indice-1];
					if(nombre_atributo.equals("id")){res.get(i).setId((Integer)value);}
					else if(nombre_atributo.equals("cid")){	res.get(i).setCid((Long)value);}
					else if(nombre_atributo.equals("version")){res.get(i).setVersion((Integer)value);}
					else if(nombre_atributo.equals("eliminado")){res.get(i).setEliminado((Boolean)value);	}
					else if(nombre_atributo.equals("valor")){res.get(i).setValor((String)value);}
					else if(nombre_atributo.equals("codigo")){res.get(i).setCodigo((String)value);}
					
				}
				
			}
			String aux=res.get(i).getEntityName();
			char change=Character.toUpperCase(aux.charAt(0));
			aux=aux.substring(1);
			res.get(i).setEntityName(String.valueOf(change)+aux);
			
		}
		return res;
	}


}
