package gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.ConjuntoDatos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Variable_ensayo;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@Name("verDetallesConjuntoDatos")
@Scope(ScopeType.CONVERSATION)
public class VerDetallesConjuntoDatos
{
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;
	@In
	Usuario user;
	FacesMessage facesMessage;
	private boolean inicioConversacion = false;
	private long cid;
	private String from;
	private ConjuntoDatos_ensayo conjuntoDatos = new ConjuntoDatos_ensayo();
	private long idConjuntoDatos;
	private ListadoControler_ensayo<VariableConjuntoDatos> listadoVariablesConjuntoDatos;
	private List<VariableConjuntoDatos> listadoVariablesSeleccionadas = new ArrayList<VariableConjuntoDatos>();
	List<String> listadoIdMomentos = new ArrayList<String>();
	List<String> listadoIdVariables = new ArrayList<String>();

	public VerDetallesConjuntoDatos() {}

	public void inicializarConjuntoDatos() {		
		conjuntoDatos = ((ConjuntoDatos_ensayo)entityManager.find(ConjuntoDatos_ensayo.class, Long.valueOf(idConjuntoDatos)));
		String hql = conjuntoDatos.getDeclaracionHql();
		List<VariableMomento> variablePorMomento=new ArrayList<VariableMomento>();
		boolean esHQLNuevo=UtilConjuntoDatos.esHQLNuevo(hql);
		if(esHQLNuevo){
			variablePorMomento= UtilConjuntoDatos.extraerDatosDeHQL(hql);
	        for (VariableMomento variableMomento : variablePorMomento) {
	        	listadoIdMomentos.add(String.valueOf(variableMomento.getIdMomento()));
	        	listadoIdVariables.add(String.valueOf(variableMomento.getIdVariable()));
			}			

		} else {
			String momentosConjuntoDatos = "";
			String variablesConjuntoDatos = "";
			boolean banderaMomentos = true;
			while (hql.indexOf("(") > -1) {
				if (banderaMomentos) {
					momentosConjuntoDatos = hql.substring(hql.indexOf("(") + 1, hql.indexOf(")"));
					hql = hql.substring(hql.indexOf(")") + 1, hql.length());
				}
				else {
					variablesConjuntoDatos = hql.substring(hql.indexOf("(") + 1, hql.indexOf(")"));
					hql = hql.substring(hql.indexOf(")") + 1, hql.length());
				}
				banderaMomentos = false;
			}
			listadoIdMomentos = Arrays.asList(momentosConjuntoDatos.split(","));
			listadoIdVariables = Arrays.asList(variablesConjuntoDatos.split(","));
		}

		List<Object> variables = entityManager.createQuery("select variable, momentoSeguimientoGeneral from Variable_ensayo variable inner join variable.seccion seccion inner join seccion.hojaCrd hojaCrd inner join hojaCrd.momentoSeguimientoGeneralHojaCrds momentoSeguimientoGeneralHojaCrds inner join momentoSeguimientoGeneralHojaCrds.momentoSeguimientoGeneral momentoSeguimientoGeneral where momentoSeguimientoGeneralHojaCrds.eliminado <> true and str(variable.id) in (#{verDetallesConjuntoDatos.listadoIdVariables}) and str(momentoSeguimientoGeneral.id) in (#{verDetallesConjuntoDatos.listadoIdMomentos}) ORDER BY variable.id")
				.getResultList();
		for (int i = 0; i < variables.size(); i++) {
			Variable_ensayo variable = (Variable_ensayo)((Object[])variables.get(i))[0];
			MomentoSeguimientoGeneral_ensayo momentoSeguimientoGeneral = (MomentoSeguimientoGeneral_ensayo)((Object[])variables.get(i))[1];
			VariableConjuntoDatos variableConjuntoDatos = new VariableConjuntoDatos(i, variable, momentoSeguimientoGeneral);
			
			if(esHQLNuevo){
				for (VariableMomento vm : variablePorMomento) {
					if(vm.getIdVariable()==variable.getId() && vm.getIdMomento()==momentoSeguimientoGeneral.getId()){
						listadoVariablesSeleccionadas.add(variableConjuntoDatos);
					}
				}
			}else{
				listadoVariablesSeleccionadas.add(variableConjuntoDatos);
			}
			
		}

		listadoVariablesConjuntoDatos = new ListadoControler_ensayo<VariableConjuntoDatos>(listadoVariablesSeleccionadas);

		inicioConversacion = true;
	}



	public void eliminar()
	{
		cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraEliminar_ens")).longValue();
	    conjuntoDatos.setCid(cid);
		conjuntoDatos.setEliminado(Boolean.valueOf(true));
		entityManager.persist(conjuntoDatos);
		entityManager.flush();
	}

	public boolean isInicioConversacion()
	{
		return inicioConversacion;
	}


	public void setInicioConversacion(boolean inicioConversacion)
	{
		this.inicioConversacion = inicioConversacion;
	}


	public long getCid()
	{
		return cid;
	}


	public void setCid(long cid)
	{
		this.cid = cid;
	}


	public ConjuntoDatos_ensayo getConjuntoDatos()
	{
		return conjuntoDatos;
	}


	public void setConjuntoDatos(ConjuntoDatos_ensayo conjuntoDatos)
	{
		this.conjuntoDatos = conjuntoDatos;
	}


	public long getIdConjuntoDatos()
	{
		return idConjuntoDatos;
	}


	public void setIdConjuntoDatos(long idConjuntoDatos)
	{
		this.idConjuntoDatos = idConjuntoDatos;
	}


	public ListadoControler_ensayo<VariableConjuntoDatos> getListadoVariablesConjuntoDatos()
	{
		return listadoVariablesConjuntoDatos;
	}



	public void setListadoVariablesConjuntoDatos(ListadoControler_ensayo<VariableConjuntoDatos> listadoVariablesConjuntoDatos)
	{
		this.listadoVariablesConjuntoDatos = listadoVariablesConjuntoDatos;
	}


	public List<VariableConjuntoDatos> getListadoVariablesSeleccionadas()
	{
		return listadoVariablesSeleccionadas;
	}



	public void setListadoVariablesSeleccionadas(List<VariableConjuntoDatos> listadoVariablesSeleccionadas)
	{
		this.listadoVariablesSeleccionadas = listadoVariablesSeleccionadas;
	}


	public String getFrom()
	{
		return from;
	}


	public void setFrom(String from)
	{
		this.from = from;
	}


	public List<String> getListadoIdMomentos()
	{
		return listadoIdMomentos;
	}


	public void setListadoIdMomentos(List<String> listadoIdMomentos)
	{
		this.listadoIdMomentos = listadoIdMomentos;
	}


	public List<String> getListadoIdVariables()
	{
		return listadoIdVariables;
	}


	public void setListadoIdVariables(List<String> listadoIdVariables)
	{
		this.listadoIdVariables = listadoIdVariables;
	}
}
