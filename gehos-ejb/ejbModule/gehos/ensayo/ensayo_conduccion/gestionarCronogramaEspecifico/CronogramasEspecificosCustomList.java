package gehos.ensayo.ensayo_conduccion.gestionarCronogramaEspecifico;

import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.SujetoList_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Scope(ScopeType.CONVERSATION)
@Name("cronogramasEspecificosCustomList")
public class CronogramasEspecificosCustomList extends SujetoList_ensayo {

	private static final String EJBQL = "select sujeto from Sujeto_ensayo sujeto where sujeto.eliminado = false and sujeto.estadoTratamiento != 4 and sujeto.cronogramaEspecifico = true "
			+ "and sujeto.grupoSujetos.estudio = #{cronogramasEspecificosCustomList.seguridadEstudio.getEstudioEntidadActivo().getEstudio()} ";

	private static final String[] RESTRICTIONS = {
			"lower(sujeto.codigoPaciente) like concat('%',lower(#{cronogramasEspecificosCustomList.sujeto.codigoPaciente.trim()}),'%')",
			"lower(sujeto.grupoSujetos.nombreGrupo) like concat(lower(#{cronogramasEspecificosCustomList.nombeGrup.trim()}),'%')",
			"lower(sujeto.estadoInclusion.nombre) like concat(lower(#{cronogramasEspecificosCustomList.estadoI.trim()}),'%')",
			"#{cronogramasEspecificosCustomList.idSujeto} <> sujeto.id" };

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;

	private boolean existResultados = true;
	private String estadoI;
	private String nombeGrup;

	protected List<EstadoInclusion_ensayo> listarEstados;

	private String displayBA = "display:none";
	private String displayBN = "display:block";
	private boolean seleccionar = false;
	private boolean modificar = false;
	private boolean ver = false;
	private int pagina;
	private boolean crear = false;
	private boolean gestionar = false;

	private Sujeto_ensayo sujeto = new Sujeto_ensayo();

	private Long idSujeto, ideliminarSuj, idhabilitado;
	
	
	@Override
	public List<Sujeto_ensayo> getResultList() {

		if (!this.getHospitalActivo().getTipoEntidad().getValor().equals(SeamResourceBundle
				.getBundle().getString("bioTecnologica"))) {

			String tempEJB = "select sujeto from Sujeto_ensayo sujeto "
					+ "where sujeto.eliminado = false and sujeto.estadoTratamiento != 4 and sujeto.cronogramaEspecifico = true and sujeto.grupoSujetos.estudio = #{cronogramasEspecificosCustomList.seguridadEstudio.getEstudioEntidadActivo().getEstudio()} "
					+ "and sujeto.entidad = #{cronogramasEspecificosCustomList.getHospitalActivo()} ";
			this.setEjbql(tempEJB);
		} else {
			this.setEjbql(EJBQL);
		}

		return super.getResultList();

	}

	public CronogramasEspecificosCustomList() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("sujeto.id desc");
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
		List<GrupoSujetos_ensayo> listarGrupo = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select g from GrupoSujetos_ensayo g where g.estudio=:estud")
				.setParameter("estud",
						seguridadEstudio.getEstudioEntidadActivo().getEstudio())
				.getResultList();
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

	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());
		return entidadEnsayo;
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

	public int getPagina() {
		if (this.getNextFirstResult() != 0)
			return this.getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getResultCount() / 10) + 1;
		if (this.pagina > 0) {
			if (getResultCount() % 10 != 0) {
				if (pagina <= num)
					this.setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.setFirstResult((this.pagina - 1) * 10);
			}
		}
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
