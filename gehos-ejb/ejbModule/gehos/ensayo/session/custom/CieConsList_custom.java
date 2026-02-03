package gehos.ensayo.session.custom;

import gehos.ensayo.entity.Cie_ensayo;
import gehos.ensayo.entity.TipoCie_ensayo;
import gehos.ensayo.entity.Diccionarios_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.framework.EntityQuery;

@Name("cieConsList_custom")
@Scope(ScopeType.CONVERSATION)
public class CieConsList_custom extends EntityQuery<Cie_ensayo> {

	private static final String EJBQL = "select cie from Cie_ensayo cie where cie.eliminado = false";

	private static final String[] RESTRICTIONS = {
			"lower(cie.incluye) like concat(lower(#{cieConsList_custom.cie.incluye.trim()}),'%')",
			"lower(cie.excluye) like concat(lower(#{cieConsList_custom.cie.excluye.trim()}),'%')",
			"lower(cie.observaciones) like concat(lower(#{cieConsList_custom.cie.observaciones.trim()}),'%')",
			"cie.diccionario.nombre like #{cieConsList_custom.diccionario}",
			"cie.tipoCie.valor like #{cieConsList_custom.estructura}"};

	private Cie_ensayo cie = new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(), new Diccionarios_ensayo(),null,null,null,null,null,null,null,null);
	String codCapitulo,codGrupo,codCategoria,codSubcategoria, estructura, codigo, descripcion,diccionario;
	List<String> tiposCie=new ArrayList<String>();
	boolean avanzada;
	
	String seleccione = SeamResourceBundle.getBundle().getString("lbl_seleccione_ens");
	
	public String getSeleccione() {
		return seleccione;
	}

	public void setSeleccione(String seleccione) {
		this.seleccione = seleccione;
	}

	private String displayBA="display:none";
	private String displayBN="display:block";
	private int pagina;
	Set<Integer> filasActualizar = new HashSet<Integer>();
	Integer rowIndex=-1;
	
	public CieConsList_custom() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("cie.id desc");
		tiposCie=this.obtenerTiposCie();
	}
	
	public void clearAllRestrictions() {
		cie = new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(-1,new Cie_ensayo(),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null),new TipoCie_ensayo(),new Diccionarios_ensayo(),null,null,null,null,null,null,null,null);
		codCapitulo="";
		codGrupo="";
		codCategoria="";
		codSubcategoria="";
	 	estructura = "";
		setEjbql(EJBQL);
	}
	
	public void setSimpleSearch(){
		this.setFirstResult(0);
		this.setOrder(null);
		codCapitulo="";
		codGrupo="";
		codCategoria="";
		codSubcategoria="";			
	}
	public void setSimpleSearch1(){
		this.setFirstResult(0);
		this.setOrder(null);
		diccionario="";
		descripcion = "";			
	}
	
	public void RestablecerListado(){	
		estructura= "";
		codigo = "";
		descripcion="";
		codCapitulo="";
		codGrupo="";
		codCategoria="";
		codSubcategoria="";	
		this.setOrder(null);
		this.setFirstResult(0);						
	}

	public int getPagina() {
		if(this.getNextFirstResult() != 0)
			return this.getNextFirstResult()/10;
			else
				return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;
		
		long num=(getResultCount()/10)+1;
		if(this.pagina>0){
		if(getResultCount()%10!=0){
			if(pagina<=num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		else{
			if(pagina<num)
				this.setFirstResult((this.pagina - 1 )*10);
		}
		}
	}
	
	@Override
	public java.util.List<Cie_ensayo> getResultList() {
		this.setEjbql();
		return super.getResultList();
	}
	
	public void setEjbql(){		
		String ejbql="";
		if(avanzada){
			this.estructura = "";
			if(codCapitulo == null || codCapitulo == ""){		//Buscando capitulos
				ejbql="select cie from Cie_ensayo cie where cie.eliminado=false";
				if(codigo!=null && codigo!="")
					if(!codigo.trim().isEmpty()) 
						ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
				 if(descripcion!=null && descripcion!="")
					 if(!descripcion.trim().isEmpty())
						 ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
				setEjbql(ejbql);
			}
			if((codCapitulo != null && codCapitulo != "") 
					&& (codGrupo == null || codGrupo == "") 
					&& (codCategoria == null || codCategoria == "") 
					&& (codSubcategoria == null || codSubcategoria == "")){		//Buscando grupos	
				 ejbql="select cie from Cie_ensayo aux join aux.cies cie where lower(sp_ascii(aux.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCapitulo.trim()})),'%')) and cie.eliminado = false and cie.tipoCie.codigo = 'grupo'";
				 if(codigo!=null && codigo!="")
					 if(!codigo.trim().isEmpty())
					 	ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
				 if(descripcion!=null && descripcion!="")
					 if(!descripcion.trim().isEmpty())
						 ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
				 setEjbql(ejbql);
			}
			
			 if((codCapitulo != null && codCapitulo != "") 
					&& (codGrupo != null && codGrupo != "") 
					&& (codCategoria == null || codCategoria == "") 
					&& (codSubcategoria == null || codSubcategoria == "")){	  //Buscando categorias		
				 ejbql="select cie from Cie_ensayo aux join aux.cies g join g.cies cie where lower(sp_ascii(g.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codGrupo.trim()})),'%')) and lower(sp_ascii(aux.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCapitulo.trim()})),'%')) and cie.eliminado=false and cie.tipoCie.codigo = 'categoria'";
				 if(codigo!=null && codigo!="")
					 if(!codigo.trim().isEmpty())
						 ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
				 if(descripcion!=null && descripcion!="")
					 if(!descripcion.trim().isEmpty())
						 ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
				 setEjbql(ejbql);
	
			}
			
			 if((codCapitulo != null && codCapitulo != "") 
					&& (codGrupo != null && codGrupo != "") 
					&& (codCategoria != null && codCategoria != "") 
					&& (codSubcategoria == null	||codSubcategoria == "")){		//Buscando subactegorias	
				 ejbql="select cie from Cie_ensayo aux join aux.cies g join g.cies c join c.cies cie where lower(sp_ascii(c.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCategoria.trim()})),'%')) and lower(sp_ascii(g.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codGrupo.trim()})),'%')) and lower(sp_ascii(aux.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCapitulo.trim()})),'%')) and cie.eliminado=false and cie.tipoCie.codigo = 'subcategoria'";
				 if(codigo!=null && codigo!="")
					 if(!codigo.trim().isEmpty())
						 ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
				 if(descripcion!=null && descripcion!="")
					 if(!descripcion.trim().isEmpty())
						 ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
				 setEjbql(ejbql);
	
			}
			
			 if((codCapitulo != null && codCapitulo != "") 
					&& (codGrupo != null && codGrupo != "") 
					&& (codCategoria != null && codCategoria != "") 
					&& (codSubcategoria != null	&& codSubcategoria != "")){		//Buscando enfermedades	
				 ejbql="select cie from Cie_ensayo aux join aux.cies g join g.cies c join c.cies s join s.cies cie where lower(sp_ascii(s.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codSubcategoria.trim()})),'%')) and lower(sp_ascii(c.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCategoria.trim()})),'%')) and lower(sp_ascii(g.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codGrupo.trim()})),'%')) and lower(sp_ascii(aux.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.codCapitulo.trim()})),'%')) and cie.eliminado=false and cie.tipoCie.codigo = 'enfermedad'";
				 if(codigo!=null && codigo!="")
					 if(!codigo.trim().isEmpty())
						 ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
				 if(descripcion!=null && descripcion!="")
					 if(!descripcion.trim().isEmpty())
						 ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
				 setEjbql(ejbql);
			}
		}
		else {
			ejbql=EJBQL;
			if(codigo!=null && codigo!="")
				if(!codigo.trim().isEmpty())
					ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
			if(descripcion!=null && descripcion!="")
				if(!descripcion.trim().isEmpty())
					ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
			setEjbql(ejbql);
		}
		if (ejbql=="") {
			ejbql=EJBQL;
			if(codigo!=null && codigo!="")
				if(!codigo.trim().isEmpty())
					ejbql+=" and lower(cie.codigo) like concat('%',concat(lower(#{cieConsList_custom.codigo.trim()}),'%'))";
			if(descripcion!=null && descripcion!="")
				if(descripcion.trim().isEmpty())
					ejbql+=" and lower(sp_ascii(cie.descripcion)) like concat('%',concat(lower(sp_ascii(#{cieConsList_custom.descripcion.trim()})),'%'))";
			setEjbql(ejbql);
		}
	}
		
	public void setAdvancedSearchRestrictions() {
		this.setFirstResult(0);
		this.setOrder(null);
	}
	
	List<Cie_ensayo> capitulos=new ArrayList<Cie_ensayo>();
	List<Cie_ensayo> grupos=new ArrayList<Cie_ensayo>();
	List<Cie_ensayo> categorias=new ArrayList<Cie_ensayo>();
	List<Cie_ensayo> subcategorias=new ArrayList<Cie_ensayo>();
	
	@SuppressWarnings("unchecked")
	public void buscarCapitulos() {
		try {
			if(capitulos.isEmpty())
				capitulos=getEntityManager().createQuery("from Cie_ensayo c where c.tipoCie.codigo = 'capitulo'")
				.getResultList();
			
			grupos=new ArrayList<Cie_ensayo>();
			categorias=new ArrayList<Cie_ensayo>();
			subcategorias=new ArrayList<Cie_ensayo>();
			
		} catch (Exception e) {
			addFacesMessageFromResourceBundle("errorInesperado");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarGrupos() {
		try {
			grupos.clear();
			if(codCapitulo != null && codCapitulo != "" && !capSuggestion.isEmpty())
				grupos=getEntityManager().createQuery("from Cie_ensayo c where c.tipoCie.codigo = 'grupo' and lower(c.cie.descripcion) like lower(concat('%',concat(:descripcion, '%')))")
				.setParameter("descripcion", codCapitulo.trim())
				.getResultList();
			codGrupo="";
			codCategoria="";
			codSubcategoria="";
			categorias=new ArrayList<Cie_ensayo>();
			subcategorias=new ArrayList<Cie_ensayo>();
			
			
		} catch (Exception e) {
			addFacesMessageFromResourceBundle("errorInesperado");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarCategorias() {
		try {
			categorias.clear();
			if(codGrupo != null && codGrupo != "" && !grupSuggestion.isEmpty())
				categorias=getEntityManager().createQuery("from Cie_ensayo c where c.tipoCie.codigo = 'categoria' and lower(c.cie.descripcion) like lower(concat('%',concat(:descripcion, '%')))")
				.setParameter("descripcion", codGrupo.trim())
				.getResultList();
			codCategoria="";
			codSubcategoria="";
			subcategorias=new ArrayList<Cie_ensayo>();
		} catch (Exception e) {
			addFacesMessageFromResourceBundle("errorInesperado");
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buscarSubcategorias() {
		try {
			subcategorias.clear();
			if(codCategoria != null && codCategoria != "" && !catSuggestion.isEmpty())
				subcategorias=getEntityManager().createQuery("from Cie_ensayo c where c.tipoCie.codigo = 'subcategoria' and lower(c.cie.descripcion) like lower(concat('%',concat(:descripcion, '%')))")
				.setParameter("descripcion", codCategoria.trim())
				.getResultList();
			codSubcategoria="";
		} catch (Exception e) {
			addFacesMessageFromResourceBundle("errorInesperado");
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<String> obtenerTiposCie(){
		try {
			List<String> res=new ArrayList<String>();
			res=getEntityManager().createQuery("select t.valor from TipoCie_ensayo t where t.eliminado = false order by t.valor")
			.getResultList();
			res.add(0, seleccione);
			return res;
		} catch (Exception e) {
			addFacesMessageFromResourceBundle("errorInesperado");
			return new ArrayList<String>();
		}
	}
	
	List<Cie_ensayo> capSuggestion=new ArrayList<Cie_ensayo>();
	
	public List<Cie_ensayo> obtenerCapitulos(Object o) {				
		String cod=(String)o;
		capSuggestion.clear();
		
		if(capitulos.isEmpty())
			this.buscarCapitulos();
		
		for (Cie_ensayo c : capitulos) {
			 if (((c.getDescripcion() != null && c.getDescripcion().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod))
					  ||((c.getCodigo() != null && c.getCodigo().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod)))
	            {
				 capSuggestion.add(c);
	            }
		}
		
		return capSuggestion;
	}
	
	List<Cie_ensayo> grupSuggestion=new ArrayList<Cie_ensayo>();
	
	public List<Cie_ensayo> obtenerGrupos(Object o) {				
		String cod=(String)o;
		grupSuggestion.clear();
		
		for (Cie_ensayo c : grupos) {
			 if (((c.getDescripcion() != null && c.getDescripcion().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod))
					  ||((c.getCodigo() != null && c.getCodigo().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod)))
	            {
				 grupSuggestion.add(c);
	            }
		}
		
		return grupSuggestion;
	}
	
	List<Cie_ensayo> catSuggestion=new ArrayList<Cie_ensayo>();
	
	public List<Cie_ensayo> obtenerCategorias(Object o) {				
		String cod=(String)o;
		catSuggestion.clear();
		
		for (Cie_ensayo c : categorias) {
			  if (((c.getDescripcion() != null && c.getDescripcion().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod))
					  ||((c.getCodigo() != null && c.getCodigo().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod)))
	            {
				  catSuggestion.add(c);
	            }
		}
		
		return catSuggestion;
	}
	
	List<Cie_ensayo> subcatSuggestion=new ArrayList<Cie_ensayo>();
	
	public List<Cie_ensayo> obtenerSubcategorias(Object o) {				
		String cod=(String)o;
		subcatSuggestion.clear();
		
		for (Cie_ensayo c : subcategorias) {
			 if (((c.getDescripcion() != null && c.getDescripcion().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod))
					  ||((c.getCodigo() != null && c.getCodigo().toLowerCase().indexOf(cod.toLowerCase()) == 0) || "".equals(cod)))
	            {
				 subcatSuggestion.add(c);
	            }
		}
		
		return subcatSuggestion;
	}
	
		/// Metotodo para mostrar la lista de diccionarios de la bd
		@SuppressWarnings("unchecked")
		public List<Diccionarios_ensayo> seleccionDiccionario(){
			try {
				List<Diccionarios_ensayo> res=new ArrayList<Diccionarios_ensayo>();
				res=(List<Diccionarios_ensayo>)getEntityManager().createQuery("select d from Diccionarios_ensayo d where d.eliminado = null or d.eliminado = false").getResultList();
				return res;
			} catch (Exception e) {
				addFacesMessageFromResourceBundle("errorInesperado");
				return new ArrayList<Diccionarios_ensayo>();
			}
		}
		
		
		/// Metotodo para seleccionar  la lista de diccionarios de la bd
		@SuppressWarnings("unchecked")
		public List<String> listaDiccionario(){
			try {
				List<String> res=new ArrayList<String>();
			    List<Diccionarios_ensayo> diccionarios = getEntityManager().createQuery("select d from Diccionarios_ensayo d where d.eliminado = null or d.eliminado = false").getResultList();
				for(Diccionarios_ensayo diccionario : diccionarios){
					res.add(diccionario.getNombre());
				}
				
				return res;
			} catch (Exception e) {
				addFacesMessageFromResourceBundle("errorInesperado");
				return new ArrayList<String>();
			}
		}
		
	
	public void cambiarBusqueda(){
		 if (this.displayBA.equals("display:none")){
			   this.displayBA="display:block";
			   avanzada=true;
		 }
		 else{
		   this.displayBA="display:none";
		   avanzada=false;
		 }
		 if (this.displayBN.equals("display:none")){
			   this.displayBN="display:block";
		 }
		 		
		   else
			   this.displayBN="display:none";
		   this.clearAllRestrictions();
	}

	public List<Cie_ensayo> getCapitulos() {
		return capitulos;
	}

	public void setCapitulos(List<Cie_ensayo> capitulos) {
		this.capitulos = capitulos;
	}

	public List<Cie_ensayo> getGrupos() {
		return grupos;
	}

	public void setGrupos(List<Cie_ensayo> grupos) {
		this.grupos = grupos;
	}

	public List<Cie_ensayo> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Cie_ensayo> categorias) {
		this.categorias = categorias;
	}

	public List<Cie_ensayo> getSubcategorias() {
		return subcategorias;
	}

	public void setSubcategorias(List<Cie_ensayo> subcategorias) {
		this.subcategorias = subcategorias;
	}

	public Cie_ensayo getCie() {
		return cie;
	}

	public String getCodCapitulo() {
		return codCapitulo;
	}

	public void setCodCapitulo(String codCapitulo) {
		this.codCapitulo = codCapitulo.trim();
	}

	public String getCodGrupo() {
		return codGrupo;
	}

	public void setCodGrupo(String codGrupo) {
		this.codGrupo = codGrupo.trim();
	}

	public String getCodCategoria() {
		return codCategoria;
	}

	public void setCodCategoria(String codCategoria) {
		this.codCategoria = codCategoria.trim();
	}

	public String getCodSubcategoria() {
		return codSubcategoria;
	}

	public void setCodSubcategoria(String codSubcategoria) {
		this.codSubcategoria = codSubcategoria.trim();
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;
	}

	public String getDisplayBA() {
		return displayBA;
	}

	public void setDisplayBA(String displayBA) {
		this.displayBA = displayBA;
	}

	public String getDisplayBN() {
		return displayBN;
	}

	public void setDisplayBN(String displayBN) {
		this.displayBN = displayBN;
	}

	public String getEstructura() {
		return estructura;
	}
	
	public void setEstructura(String estructura) {		
		if(!estructura.equals(seleccione))
			this.estructura = estructura.trim();
		else
			this.estructura = "";
	}
	
	public String getDiccionario() {
		return diccionario;
	}
	
	public void setDiccionario(String diccionario) {	
			this.diccionario = diccionario;
	}

	public List<String> getTiposCie() {
		return tiposCie;
	}

	public void setTiposCie(List<String> tiposCie) {
		this.tiposCie = tiposCie;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo.trim();
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion.trim();
	}

	public Set<Integer> getFilasActualizar() {
		return filasActualizar;
	}

	public void setFilasActualizar(Set<Integer> filasActualizar) {
		this.filasActualizar = filasActualizar;
	}

	public Integer getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(Integer rowIndex) {
		this.rowIndex = rowIndex;
		filasActualizar=Collections.singleton(rowIndex);
	}	
}
