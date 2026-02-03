//CU 12 Visualizar hoja CRD
package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.WrapperGroupData;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.GrupoWrapper;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.MapWrapperDataPlus;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.ValorVariable;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.VariableGrupoWrapper;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Nomenclador_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import javax.ejb.Remove;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("visualizarHoja_Disenno")
@Scope(ScopeType.CONVERSATION)
public class VisualizarHoja_Disenno {

	protected @In EntityManager entityManager;
	protected @In IBitacora bitacora;
	protected @In(create = true) FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;

	private long cid = -1;

	// protected VariableDatoInt_ensayo datoVariable;
	private List<Variable_ensayo> variables;
	private List<Seccion_ensayo> secciones;
	private List<GrupoVariables_ensayo> grupoVariables;
	private List<GrupoWrapper> listaGrupoW = new ArrayList<GrupoWrapper>();
	private Map<Long, List<Variable_ensayo>> listaVariables;
	private Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables;
	List<GrupoVariables_ensayo> listaDataTable;
	private HojaCrd_ensayo hoja;
	private Long idcrd;
	private String ctcCategoria;
	private Long idTabala; 
	private boolean inicializado = false;
	private WrapperGroupData data;
	

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() {
				
		if (idcrd != null) {
			hoja = entityManager.find(HojaCrd_ensayo.class, idcrd);			

			if (hoja != null) {
				listadoSecciones();
				listaVariables = new HashMap<Long, List<Variable_ensayo>>();
				listaGrupoVariables = new HashMap<Long, List<GrupoVariables_ensayo>>();
				for (Iterator iterator = secciones.iterator(); iterator.hasNext();) {
						Seccion_ensayo seccion_ensayo = (Seccion_ensayo) iterator.next();
						listadoGrupoVariables(seccion_ensayo);
						listadoVariables(seccion_ensayo);
						listaVariables.put(seccion_ensayo.getId(), variables);
						listaGrupoVariables.put(seccion_ensayo.getId(), grupoVariables);
					
				}
				inicializado=true;
			}
		}

	}
	
	

	@SuppressWarnings("unchecked")
	public List<Seccion_ensayo> listadoSecciones() {
		secciones = new ArrayList<Seccion_ensayo>();
		secciones = (List<Seccion_ensayo>) entityManager
				.createQuery(
						"select seccion from Seccion_ensayo seccion where seccion.hojaCrd=:Hoj and seccion.eliminado <> true")
				.setParameter("Hoj", this.hoja).getResultList();
		return secciones;
	}
	
	public void AgregarFila(long idGrupo ,int cantiMaxRepeticiones) {
		for (GrupoWrapper grupoWrapper : listaGrupoW) {
			if(grupoWrapper.getGrupoVariables().getId() == idGrupo){				
				for (VariableGrupoWrapper vw : grupoWrapper.getListaVarWrapper()) {
					if(cantiMaxRepeticiones > vw.getListaValores().size()){
						vw.getListaValores().add(new ValorVariable());
					}
				}
				break;
			}			
		}
	}
	
	public void EliminarFila(long idGrupo ,int pos) {
		for (GrupoWrapper grupoW : listaGrupoW) {
			if(grupoW.getGrupoVariables().getId() == idGrupo){
				for (VariableGrupoWrapper variableW : grupoW.getListaVarWrapper()) {
					variableW.getListaValores().remove(pos);
				}
			}
		}
		
	}

	/*
	 * public DatoVariable_ensayo DatoVariable(Variable_ensayo var) {
	 * DatoVariable_ensayo dato = new DatoVariable_ensayo(); try { dato =
	 * (DatoVariable_ensayo) entityManager.createQuery(
	 * "select dat from DatoVariable_ensayo dat where dat.variable=:Var"
	 * ).setParameter("Var", var).getSingleResult(); } catch (Exception e) {
	 * return null; }
	 * 
	 * return dato; }
	 */
	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> listadoVariables(Seccion_ensayo seccion) {
		variables = new ArrayList<Variable_ensayo>();
		variables = (List<Variable_ensayo>) entityManager
				.createQuery(
						"select var from Variable_ensayo var where var.seccion=:Seccion and var.grupoVariables = null and var.eliminado='FALSE' order by var.idClinica, var.numeroPregunta ASC")
				.setParameter("Seccion", seccion).getResultList();
		return variables;
	}

	@SuppressWarnings("unchecked")
	public List<GrupoVariables_ensayo> listadoGrupoVariables(Seccion_ensayo seccion) {
		grupoVariables = new ArrayList<GrupoVariables_ensayo>();
		grupoVariables = (List<GrupoVariables_ensayo>) entityManager
				.createQuery(
						"select grup from GrupoVariables_ensayo grup where grup.seccion=:Seccion and grup.eliminado = 'FALSE'")
				.setParameter("Seccion", seccion).getResultList();
		
		for (int i = 0; i < grupoVariables.size(); i++) {
			GrupoWrapper grupo = new GrupoWrapper(grupoVariables.get(i));
			listaGrupoW.add(grupo);
		}
		
		return grupoVariables;
	}
	
	public GrupoWrapper grupoWrapper(long idGrupo){
		GrupoWrapper salida = null;
		
		for (GrupoWrapper gw : listaGrupoW) {
			if(gw.getGrupoVariables().getId() == idGrupo){
				salida = gw;
				break;
			}
		}
		
		return salida;
	}
	
	public String concatenarId(long idGrupo, long idSeccion) {
		return "var" + String.valueOf(idGrupo) + String.valueOf(idSeccion);
	}

	
	@SuppressWarnings("unchecked")
	public List ValoresRadio(Nomenclador_ensayo nomenc) {
		List<NomencladorValor_ensayo> lista = new ArrayList<NomencladorValor_ensayo>();
		List listaRadio = new ArrayList();
		lista = (List<NomencladorValor_ensayo>) entityManager
				.createQuery(
						"select nom from NomencladorValor_ensayo nom where nom.nomenclador=:nomenc and nom.valor != null ORDER BY nom.valorCalculado ASC")
				.setParameter("nomenc", nomenc).getResultList();
		for (int i = 0; i < lista.size(); i++) {
			listaRadio.add(new SelectItem(lista.get(i).getId(), lista.get(i).getValor()));
		}
		return listaRadio;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> Valores(Nomenclador_ensayo nomenc) {
		List<String> lista = new ArrayList<String>();
		lista = (List<String>) entityManager
				.createQuery(
						"select nom.valor from NomencladorValor_ensayo nom where nom.nomenclador=:nomenc ORDER BY nom.valorCalculado ASC")
				.setParameter("nomenc", nomenc).getResultList();
		return lista;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> ValoresCEA(Nomenclador_ensayo nomenc) {
		List<String> lista = new ArrayList<String>();
		if (nomenc.getNombre().equals("CEA")) {
			lista = (List<String>) entityManager
					.createQuery("select nom.nombreCategoria from CtcCategoria_ensayo nom where nom.diccionario.id =:idDicc ORDER BY nom.nombreCategoria ASC")
					.setParameter("idDicc", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getDiccionario().getId())
					.getResultList();
		}
		return lista;
	}

	@SuppressWarnings("unchecked")
	public List<String> ValoresEA(Nomenclador_ensayo nomen) {
		
		
		List<String> lista = new ArrayList<String>();
		
			lista = (List<String>) entityManager.createQuery("select nom.eventoAdverso from Ctc_ensayo nom where nom.ctcCategoria.nombreCategoria = :nombreCategoria and nom.ctcCategoria.diccionario.id =:idDicc ORDER BY nom.eventoAdverso ASC")
					.setParameter("nombreCategoria", this.ctcCategoria)
					.setParameter("idDicc", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getDiccionario().getId())
					.getResultList();
		
		return lista;
	
	}


	public boolean perteneceVariableAGrupo(Variable_ensayo var, Seccion_ensayo seccion) {
		for (int i = 0; i < listaGrupoVariables.get(seccion.getId()).size(); i++) {
			for (int j = 0; j < listaGrupoVariables.get(seccion.getId()).get(i).getVariables().size(); j++) {
				if (listaGrupoVariables.get(seccion.getId()).get(i).getVariables().contains(var)) {
					return true;
				}

			}

		}
		return false;
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> tieneVariableGrupoSeccion(Seccion_ensayo seccion, GrupoVariables_ensayo grupo) {
		List<Variable_ensayo> lista = new ArrayList<Variable_ensayo>();
		listaDataTable = new ArrayList<GrupoVariables_ensayo>();
		try {
			lista = (List<Variable_ensayo>) entityManager.createQuery(
							"select var from Variable_ensayo var where var.grupoVariables=:Grupo and var.seccion=:Seccion and var.eliminado = 'FALSE' order by var.idClinica, var.numeroPregunta ASC")
							.setParameter("Grupo", grupo)
							.setParameter("Seccion", seccion).getResultList();
			listaDataTable.add(grupo);
		} catch (Exception e) {
			return null;
		}

		return lista;
	}

	@Transactional
	public void persistir() throws ParseException {
		this.cid = this.bitacora.registrarInicioDeAccion(SeamResourceBundle
				.getBundle().getString("modificandoSujeto"));
		Calendar fecha = new GregorianCalendar();
		int anno = fecha.get(Calendar.YEAR);
		int mes = fecha.get(Calendar.MONTH);
		int dia = fecha.get(Calendar.DAY_OF_MONTH);
		String fechaActual = dia + "/" + (mes + 1) + "/" + anno;
		SimpleDateFormat formatoDeFecha = new SimpleDateFormat("dd/MM/yyyy");
		Date fechaNew = null;
		fechaNew = formatoDeFecha.parse(fechaActual);
		/*
		 * for (List<DatoVariable_ensayo> valores : listaDatosVariable.values())
		 * { for (int i = 0; i < valores.size(); i++) {
		 * valores.get(i).setEliminado(false); valores.get(i).setCid(cid);
		 * valores.get(i).setFechaCreacion(fechaNew);
		 * entityManager.persist(valores.get(i)); entityManager.flush(); } }
		 */
	}

	@End
	public void salir() {

	}

	@Remove
	@Destroy
	public void destroy() {
	}

	public HojaCrd_ensayo getHoja() {
		return hoja;
	}

	public void setHoja(HojaCrd_ensayo hoja) {
		this.hoja = hoja;
	}

	public long getIdcrd() {
		return idcrd;
	}

	public void setIdcrd(long idcrd) {
		this.idcrd = idcrd;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public List<Seccion_ensayo> getSecciones() {
		return secciones;
	}

	public void setSecciones(List<Seccion_ensayo> secciones) {
		this.secciones = secciones;
	}

	public List<GrupoVariables_ensayo> getGrupoVariables() {
		return grupoVariables;
	}

	public void setGrupoVariables(List<GrupoVariables_ensayo> grupoVariables) {
		this.grupoVariables = grupoVariables;
	}

	

	public Map<Long, List<Variable_ensayo>> getListaVariables() {
		return listaVariables;
	}

	public void setListaVariables(Map<Long, List<Variable_ensayo>> listaVariables) {
		this.listaVariables = listaVariables;
	}

	public Map<Long, List<GrupoVariables_ensayo>> getListaGrupoVariables() {
		return listaGrupoVariables;
	}

	public void setListaGrupoVariables(
			Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables) {
		this.listaGrupoVariables = listaGrupoVariables;
	}

	public List<GrupoWrapper> getListaGrupoW() {
		return listaGrupoW;
	}

	public void setListaGrupoW(List<GrupoWrapper> listaGrupoW) {
		this.listaGrupoW = listaGrupoW;
	}

	public Long getIdTabala() {
		return idTabala;
	}

	public void setIdTabala(Long idTabala) {
		this.idTabala = idTabala;
	}



	public String getctcCategoria() {
		return ctcCategoria;
	}



	public void setctcCategoria(String ctcCategoria) {
		this.ctcCategoria = ctcCategoria;
	}



	public List<GrupoVariables_ensayo> getListaDataTable() {
		return listaDataTable;
	}



	public void setListaDataTable(List<GrupoVariables_ensayo> listaDataTable) {
		this.listaDataTable = listaDataTable;
	}
	
	

	
}
