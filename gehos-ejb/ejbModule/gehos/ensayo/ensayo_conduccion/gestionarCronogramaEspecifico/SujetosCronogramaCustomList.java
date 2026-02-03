package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoSujeto_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.SujetoList_ensayo;

import org.hibernate.criterion.Distinct;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.pentaho.di.trans.steps.groupby.GroupBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;
//CU 17 Buscar cronograma especifico
@SuppressWarnings("serial")
@Name("sujetosCronogramaCustomList")
@Scope(ScopeType.CONVERSATION)
public class SujetosCronogramaCustomList extends SujetoList_ensayo {
	
	private static final String EJBQL = "select distinct momento.sujeto from  MomentoSeguimientoEspecifico_ensayo"
			+ " momento where (select count(*) from MomentoSeguimientoEspecifico_ensayo ms where "
			+ "ms.momentoSeguimientoGeneral.etapa='Evaluaci\u00F3n')=(select count(*) "
			+ "from MomentoSeguimientoEspecifico_ensayo ms where ms.momentoSeguimientoGeneral.etapa='Evaluaci\u00F3n' "
			+ "and ms.estadoMomentoSeguimiento.nombre='completado') and momento.eliminado = false "
			+ "and momento.sujeto.grupoSujetos.estudio = #{sujetoCustomList.seguridadEstudio.getEstudioEntidadActivo().getEstudio()}"
			+ "  and momento.sujeto.estadoSujeto.nombre != 'Planificado'";
	
	private static final String[] RESTRICTIONS = {
			"lower(momento.sujeto.codigoPaciente) like concat(lower(#{sujetoCustomList.sujeto.codigoPaciente.trim()}),'%')",
			"lower(momento.sujeto.grupoSujetos.nombreGrupo) like concat(lower(#{sujetoCustomList.nombeGrup.trim()}),'%')",
			"lower(momento.sujeto.estadoInclusion.nombre) like concat(lower(#{sujetoCustomList.estadoI.trim()}),'%')",
			"lower(momento.sujeto.fechaNacimiento) like concat(lower(#{sujetoCustomList.sujeto.fechaNacimiento.trim()}),'%')",
			"lower(momento.sujeto.fechaNacimiento) like concat(lower(#{sujetoCustomList.sujeto.fechaNacimiento.trim()}),'%')",
			"#{sujetoCustomList.idSujeto} <> momento.sujeto.id" };

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;
	


	private boolean existResultados = true;
	private String estadoI;
	private String nombeGrup;

	protected List<EstadoInclusion_ensayo> listarEstados;

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private boolean seleccionar = false;
	private boolean modificar = false;
	private boolean ver = false;
	private boolean crear = false;
	private boolean gestionar = false;

	private Sujeto_ensayo sujeto = new Sujeto_ensayo();

	private Long idSujeto, ideliminarSuj, idhabilitado;

	public SujetosCronogramaCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		//setOrder("momento.sujeto.id desc");
	
		this.ver = true;
		this.modificar = true;
		this.crear = true;
		this.gestionar = true;
	}
	
	
	@SuppressWarnings("unchecked")
	public List<String> listEstadoI() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager
				.createQuery("select e from EstadoInclusion_ensayo e")
				.getResultList();
		List<String> nombreEst = new ArrayList<String>();
		for (int i = 0; i < listarEstados.size(); i++) {
			nombreEst.add(listarEstados.get(i).getNombre());
		}
		return nombreEst;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> listNombreG() {
		List<GrupoSujetos_ensayo> listarGrupo = (List<GrupoSujetos_ensayo>) entityManager.createQuery("select g from GrupoSujetos_ensayo g where g.estudio=:estud").setParameter("estud", seguridadEstudio.getEstudioEntidadActivo().getEstudio()).getResultList();
		List<String> nombreG = new ArrayList<String>();
		for (int i = 0; i < listarGrupo.size(); i++) {
			nombreG.add(listarGrupo.get(i).getNombreGrupo());
		}
		return nombreG;
	}

	public void buscar() {
		this.refresh();
		this.setFirstResult(0);
		this.getResultList();
		this.existResultados = (this.getResultCount() != 0);
		setOrder("sujeto.id desc");
	}

	public void SeleccionarInstanciaSuj(long id) {
		this.setideliminarSuj(id);
	}

	public void SeleccionarDeshabilitarSuj(long id) {
		this.setIdhabilitado(id);
	}

	@SuppressWarnings("unchecked")
	public void EliminarInstanciaSuj() {

		List<Sujeto_ensayo> sujEliminar = entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id =:id and suj.eliminado = false")
				.setParameter("id", ideliminarSuj).getResultList();
		Sujeto_ensayo nuevo = sujEliminar.get(0);
		nuevo.setEliminado(true);
		entityManager.persist(nuevo);
		entityManager.flush();

	}

	@SuppressWarnings("unchecked")
	public void DeshabilitarInstanciaSuj() {
		EstadoSujeto_ensayo habil = new EstadoSujeto_ensayo();
		EstadoSujeto_ensayo deshabil = new EstadoSujeto_ensayo();

		List<Sujeto_ensayo> sujDeshabilitar = entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id =:id and suj.eliminado = false")
				.setParameter("id", idhabilitado).getResultList();
		Sujeto_ensayo nuevo = sujDeshabilitar.get(0);
		List<EstadoSujeto_ensayo> estadosSuje = entityManager.createQuery(
				"select estados from EstadoSujeto_ensayo estados")
				.getResultList();
		for (int i = 0; i < estadosSuje.size(); i++) {
			if (estadosSuje.get(i).getNombre().equals("Habilitado")) {
				habil = estadosSuje.get(i);
			} else if (estadosSuje.get(i).getNombre().equals("Deshabilitado")) {
				deshabil = estadosSuje.get(i);
			}
		}
		if (nuevo.getEstadoSujeto().getNombre().equals("Habilitado")) {
			nuevo.setEstadoSujeto(deshabil);
		} else {
			nuevo.setEstadoSujeto(habil);
		}
		entityManager.persist(nuevo);
		entityManager.flush();
	}

	public void cambiarBusqueda() {
		if (this.displayBA.equals("display:none"))
			this.displayBA = "display:block";
		else
			this.displayBA = "display:none";

		if (this.displayBN.equals("display:none"))
			this.displayBN = "display:block";
		else
			this.displayBN = "display:none";
	}

	public Long getideliminarSuj() {
		return ideliminarSuj;
	}

	public void setideliminarSuj(Long ideliminarSuj) {
		this.ideliminarSuj = ideliminarSuj;
	}

	public Long getIdhabilitado() {
		return idhabilitado;
	}

	public void setIdhabilitado(Long idhabilitado) {
		this.idhabilitado = idhabilitado;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
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

	public boolean isExistResultados() {
		return existResultados;
	}

	public void setExistResultados(boolean existResultados) {
		this.existResultados = existResultados;
	}

	public boolean isSeleccionar() {
		return seleccionar;
	}

	public void setSeleccionar(boolean seleccionar) {
		this.seleccionar = seleccionar;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

	public boolean isVer() {
		return ver;
	}

	public void setVer(boolean ver) {
		this.ver = ver;
	}

	public boolean isCrear() {
		return crear;
	}

	public void setCrear(boolean crear) {
		this.crear = crear;
	}

	public String getEstadoI() {
		return estadoI;
	}

	public void setEstadoI(String estadoI) {
		this.estadoI = estadoI;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public String getNombeGrup() {
		return nombeGrup;
	}

	public void setNombeGrup(String nombeGrup) {
		this.nombeGrup = nombeGrup;
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

}
