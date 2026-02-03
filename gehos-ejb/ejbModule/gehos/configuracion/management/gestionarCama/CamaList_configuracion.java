package gehos.configuracion.management.gestionarCama;

import gehos.configuracion.management.entity.*;
import gehos.configuracion.management.utilidades.Parameters;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.framework.EntityQuery;
import org.jboss.seam.international.LocaleSelector;

import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

@SuppressWarnings("serial")
@Name("buscarCamaControlador")
@Scope(ScopeType.CONVERSATION)
public class CamaList_configuracion extends EntityQuery<Cama_configuracion> {

	private static final String EJBQL = "select cama from Cama_configuracion cama where cama.eliminado=false";

	private static final String[] RESTRICTIONS = { "#{buscarCamaControlador.idElimCama} <> cama.id" };

	private static final String[] RESTRICTIONSS = {
			"lower(cama.descripcion) like concat(lower(#{buscarCamaControlador.descripcion}),'%')",
			"#{buscarCamaControlador.idElimCama} <> cama.id" };

	private static final String[] RESTRICTIONSA = {
			"lower(cama.descripcion) like concat(lower(#{buscarCamaControlador.descripcionA}),'%')",
			"lower(cama.estadoCama.valor) like concat(lower(#{buscarCamaControlador.estadoCama}),'%')",
			"lower(cama.categoriaCama.valor) like concat(lower(#{buscarCamaControlador.categoriaCama}),'%')",
			"lower(cama.tipoCama.valor) like concat(lower(#{buscarCamaControlador.tipoCama}),'%')",
			"lower(cama.servicioInEntidadByIdServicio.servicio.nombre) like concat(lower(#{buscarCamaControlador.nombreServ}),'%')",
			"#{buscarCamaControlador.idElimCama} <> cama.id" };

	// Atributos
	// otras funcionalidades
	private Parameters parametros = new Parameters();

	@In
	EntityManager entityManager;

	@In
	LocaleSelector localeSelector;

	@In(create = true)
	FacesMessages facesMessages;

	private Cama_configuracion cama = new Cama_configuracion();
	private String descripcion = "", descripcionA = "", estadoCama = "",
			categoriaCama = "", tipoCama = "";
	private Long idCama, idElimCama;
	private boolean open = true;
	private boolean avanzada = false;
	private String nombreServ = "";

	// Metodos

	public CamaList_configuracion() {
		setEjbql(EJBQL);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("cama.id desc");

	}

	@Begin(flushMode = FlushModeType.MANUAL, join = true)
	public void begin() {
	}

	@SuppressWarnings("unchecked")
	public List<String> catg() {

		List<String> lc = entityManager
				.createQuery(
						"select cat.valor from CategoriaCama_configuracion cat where cat.eliminado=false order by cat.valor")
				.getResultList();
		lc.add(0, "<Seleccione>");
		return lc;
	}

	@SuppressWarnings("unchecked")
	public List<String> listEstados() {
		List<String> le = entityManager
				.createQuery(
						"select est.valor from EstadoCama_configuracion est where est.eliminado=false order by est.valor")
				.getResultList();
		le.add(0, "<Seleccione>");
		return le;

	}

	@SuppressWarnings("unchecked")
	public List<String> listTipoCama() {
		List<String> lt = entityManager
				.createQuery(
						"select tipo.valor from TipoCama_configuracion tipo where tipo.eliminado=false order by tipo.valor")
				.getResultList();
		lt.add(0, "<Seleccione>");
		return lt;
	}

	@SuppressWarnings("unchecked")
	public List<String> listServ() {
		List<String> ls = entityManager
				.createQuery(
						"select serv.nombre from Servicio_configuracion serv where serv.eliminado=false order by serv.nombre")
				.getResultList();
		ls.add(0, "<Seleccione>");
		return ls;
	}

	public void cambiar(boolean a) {
		this.avanzada = a;
	}

	public void abrirCerrar() {
		this.open = !open;
	}

	public void buscar(boolean avan) {
		setFirstResult(0);
		String[] aux = (avan) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	public void cancelar() {
		this.descripcion = "";
		this.descripcionA = "";
		this.estadoCama = "";
		this.categoriaCama = "";
		this.tipoCama = "";
	}

	public void eliminar() {
		
		
		if (getResultList().size() == 1 && getFirstResult() != null)
			setFirstResult(getFirstResult() - getMaxResults());

		Cama_configuracion aux = entityManager.find(Cama_configuracion.class,
				this.idCama);
		this.idElimCama = -1l;
		aux.setEliminado(true);
		entityManager.persist(aux);
		entityManager.flush();
	}

	public void seleccionar(Long idelim) {
		this.idCama = idelim;
	}

	// Propiedades

	public Cama_configuracion getCama() {
		return cama;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = parametros.decodec(descripcion);
	}

	public String getEstadoCama() {
		return estadoCama;
	}

	public void setEstadoCama(String estadoCama) {
		if (estadoCama.equals("<Seleccione>"))
			this.estadoCama = "";
		else
			this.estadoCama = parametros.decodec(estadoCama);
	}

	public String getCategoriaCama() {
		return categoriaCama;
	}

	public void setCategoriaCama(String categoriaCama) {
		if (categoriaCama.endsWith("<Seleccione>"))
			this.categoriaCama = "";
		else
			this.categoriaCama = parametros.decodec(categoriaCama);
	}

	public String getTipoCama() {
		return parametros.decodec(tipoCama);
	}

	public void setTipoCama(String tipoCama) {
		if (tipoCama.equals("<Seleccione>"))
			this.tipoCama = "";
		else
			this.tipoCama = tipoCama;
	}

	public void setCama(Cama_configuracion cama) {
		this.cama = cama;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

	public boolean isAvanzada() {
		return avanzada;
	}

	public void setAvanzada(boolean avanzada) {
		this.avanzada = avanzada;

		String[] aux = (avanzada) ? (RESTRICTIONSA) : (RESTRICTIONSS);
		setRestrictionExpressionStrings(Arrays.asList(aux));
		this.refresh();
	}

	public Long getIdCama() {
		return idCama;
	}

	public void setIdCama(Long idCama) {
		this.idCama = idCama;
	}

	public Long getIdElimCama() {
		return idElimCama;
	}

	public void setIdElimCama(Long idElimCama) {
		this.idElimCama = idElimCama;
	}

	public String getDescripcionA() {
		return descripcionA;
	}

	public void setDescripcionA(String descripcionA) {
		this.descripcionA = parametros.decodec(descripcionA);
	}

	public String getNombreServ() {
		return nombreServ;
	}

	public void setNombreServ(String nombreServ) {
		if (nombreServ.equals("<Seleccione>"))
			this.nombreServ = "";
		else
			this.nombreServ = parametros.decodec(nombreServ);
	}

}
