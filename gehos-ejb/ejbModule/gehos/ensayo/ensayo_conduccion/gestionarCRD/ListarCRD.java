//CU 11 Visualizar listado de hojas CRD por MS del sujeto
package gehos.ensayo.ensayo_conduccion.gestionarCRD;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_conduccion.gestionarMS.WrapperMomento;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.verEstudioControlador;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.session.common.auto.CrdEspecificoList_ensayo;
import gehos.ensayo.session.common.auto.HojaCrdList_ensayo;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import javax.faces.context.FacesContext;
//import java.util.List;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

@SuppressWarnings("serial")
@Name("listarCRD")
@Scope(ScopeType.CONVERSATION)
public class ListarCRD extends CrdEspecificoList_ensayo {

	private static final String EJBQL = "select crd from CrdEspecifico_ensayo crd "
			+ "inner join crd.hojaCrd HojaCRD "
			+ "inner join crd.momentoSeguimientoEspecifico MSE "
			+ "inner join MSE.momentoSeguimientoGeneral MSG "
			+ "inner join HojaCRD.momentoSeguimientoGeneralHojaCrds MSHoja "
			+ "where MSE.id = #{listarCRD.idMS} and crd.eliminado = 'false' and MSHoja.eliminado = 'false' and MSHoja.momentoSeguimientoGeneral.id = MSG.id";
	// private static final String EJBQL =
	// "select crd from HojaCrd_ensayo crd where crd.eliminado = FALSE";

	@In(create = true)
	WrapperMomento wrapperMomento;
	protected @In
	IBitacora bitacora;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private Usuario user;
	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;

	private boolean gestionar = false;
	private boolean ver = false;
	private String fromP;
	private String causaGuardar = "";

	private static final String CARACTERES_ESPECIALES = SeamResourceBundle
			.getBundle().getString("caracteresEspeciales");

	public String getCausaGuardar() {
		return causaGuardar;
	}

	public void setCausaGuardar(String causaGuardar) {
		this.causaGuardar = causaGuardar;
	}

	private Object causa = null;

	public Object getCausa() {
		return causa;
	}

	public void setCausa(Object causa) {
		this.causa = causa;
	}

	private boolean causaRequired = true;

	private Long idMS, instanciaFirmarHoja;
	private int pagina;
	private Long idSujeto;
	private CrdEspecifico_ensayo hojaElimininarFirma;
	private Sujeto_ensayo sujeto;

	public String NombreSujetoById() {
		String nom = "";
		sujeto = (Sujeto_ensayo) entityManager
				.createQuery(
						"select suj from Sujeto_ensayo suj where suj.id=:idSujeto")
				.setParameter("idSujeto", this.idSujeto).getSingleResult();
		nom = sujeto.getCodigoPaciente();
		return nom;
	}

	public String NombreMomentoById() {
		String nom = "";
		MomentoSeguimientoEspecifico_ensayo otro = (MomentoSeguimientoEspecifico_ensayo) entityManager
				.createQuery(
						"select mom from MomentoSeguimientoEspecifico_ensayo mom where mom.id=:idMom")
				.setParameter("idMom", this.idMS).getSingleResult();
		nom = otro.getMomentoSeguimientoGeneral().getNombre();
		return nom;
	}

	public void SeleccionarInstanciaHoja(long id) {
		this.setInstanciaFirmarHoja(id);
	}

	public void SeleccionarHoja(CrdEspecifico_ensayo CRD) {
		this.setHojaElimininarFirma(CRD);
	}

	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager
				.createQuery(
						"select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",
						seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua", user.getId()).getSingleResult();

		return rol;
	}

	/*
	 * Devuelve la url del icono para estado pasado por parametro.
	 * 
	 * @parametro: estado, estado del cual vamos a obtener el icono.
	 */
	public String estadoIcon(EstadoHojaCrd_ensayo estado) {
		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();

		String path = "/resources/modEnsayo/estadosIcon/"
				+ estado.getClass().getSimpleName().split("_")[0] + "/"
				+ estado.getCodigo() + ".png";

		String rootpath = context.getRealPath(path);
		java.io.File dir = new java.io.File(rootpath);
		if (dir.exists())
			return path;
		else
			return "/resources/modEnsayo/estados/" + "generic.png";

	}

	@SuppressWarnings("unchecked")
	public ArrayList<Integer> CantidadNotasDiscrepancias() {
		ArrayList<Integer> listaCant = new ArrayList<Integer>();
		List<Nota_ensayo> listaNotas = new ArrayList<Nota_ensayo>();
		listaNotas = (List<Nota_ensayo>) entityManager
				.createQuery(
						"select nota from Nota_ensayo nota where nota.crdEspecifico=:Hoj and nota.notaSitio = 'FALSE' and nota.eliminado = 'FALSE' and nota.notaPadre = null ")
				.setParameter("Hoj", this.hojaElimininarFirma).getResultList();
		int contNuevas = 0;
		int contResueltas = 0;
		int contCerradas = 0;
		int contActualizadas = 0;
		for (int i = 0; i < listaNotas.size(); i++) {
			if (listaNotas.get(i).getEstadoNota().getCodigo() == 1) {
				contNuevas++;
			} else if (listaNotas.get(i).getEstadoNota().getCodigo() == 2) {
				contActualizadas++;
			} else if (listaNotas.get(i).getEstadoNota().getCodigo() == 3) {
				contResueltas++;
			} else {
				contCerradas++;
			}
		}

		listaCant.add(0, contNuevas);
		listaCant.add(1, contActualizadas);
		listaCant.add(2, contResueltas);
		listaCant.add(3, contCerradas);

		return listaCant;
	}

	public String validarCampo() {
		if (causa != null) {
			causaGuardar = causa.toString();
			int longitud = causa.toString().length();
			boolean noExtranno = causa.toString().matches(
					"^(\\s*[A-Za-z" + CARACTERES_ESPECIALES
							+ "\u00BF?.,0-9]+\\s*)++$");
			if (causa != null && noExtranno && longitud <= 250) {
				causa = null;
				return "Richfaces.showModalPanel('mpAdvertenciaEliminarFirma')";
			}
		}
		return "";
	}

	// CU 14 Firmar hoja CRD
	public void FirmarInstanciaHoja() {
		CrdEspecifico_ensayo hojaFirmar = entityManager.find(
				CrdEspecifico_ensayo.class, instanciaFirmarHoja);
		EstadoHojaCrd_ensayo estadoFir = (EstadoHojaCrd_ensayo) entityManager
				.createQuery(
						"select e from EstadoHojaCrd_ensayo e where e.codigo = 4")
				.getSingleResult();
		hojaFirmar.setEstadoHojaCrd(estadoFir);
		;
		entityManager.persist(hojaFirmar);
		wrapperMomento.cambiarEstadoAFirmado(hojaFirmar
				.getMomentoSeguimientoEspecifico());
		entityManager.flush();

		}
		
		//3.13  CU 15 Eliminar firma de hoja CRD
				public void EliminarFirmaInstanciaHoja() {
					
					//CrdEspecifico_ensayo hojaFirmar = entityManager.find(CrdEspecifico_ensayo.class, instanciaFirmarHoja);
					EstadoHojaCrd_ensayo estadoFir = (EstadoHojaCrd_ensayo) entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.codigo = 3").getSingleResult();
					hojaElimininarFirma.setEstadoHojaCrd(estadoFir);;
					entityManager.persist(hojaElimininarFirma);
					wrapperMomento.cambiarEstadoMomento(hojaElimininarFirma.getMomentoSeguimientoEspecifico());
					long cid = bitacora.registrarInicioDeAccion(SeamResourceBundle
							.getBundle().getString("bitCrearCausa"));
					Causa_ensayo causa = new Causa_ensayo();
					causa.setCid(cid);
					Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
							user.getId());
					causa.setUsuario(usuario);
					causa.setEstudio(seguridadEstudio.getEstudioActivo());
					causa.setCronograma(hojaElimininarFirma.getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral().getCronograma());
					Calendar cal=Calendar.getInstance();
					causa.setDescripcion(this.causaGuardar);
					causa.setCrdEspecifico(hojaElimininarFirma);
					causa.setSujeto(sujeto);
					MomentoSeguimientoEspecifico_ensayo msEspeficoCausa= (MomentoSeguimientoEspecifico_ensayo) entityManager.createQuery("select e from MomentoSeguimientoEspecifico_ensayo e where e.id =:idMS ").setParameter("idMS", this.idMS).getSingleResult();
					causa.setMomentoSeguimientoEspecifico(msEspeficoCausa);
					causa.setFecha(cal.getTime());
					causa.setTipoCausa(SeamResourceBundle
							.getBundle().getString("bitCrearCausa"));
					entityManager.persist(causa);
					entityManager.flush();

	}

	/*
	 * @SuppressWarnings("unchecked") public List<MomentoSeguimientoEspecifico>
	 * ListaMS(){ List<MomentoSeguimientoEspecifico> lista; lista =
	 * (List<MomentoSeguimientoEspecifico>)entityManager.createQuery(
	 * "select ms from MomentoSeguimientoEspecifico ms where ms.id=:idMS"
	 * ).setParameter("id", idMS).getResultList(); int cant = lista.size();
	 * return lista; }
	 */

	public ListarCRD() {
		setEjbql(EJBQL);
		setMaxResults(10);
		setOrder("crd.hojaCrd.nombreHoja");
		this.gestionar=true;
		this.ver=true;
	}

	@SuppressWarnings("unchecked")
	public List<Nota_ensayo> NotasMonitoreo(Long idCRD) {
		String queryNotas = "select notaEnsayo "
				+ "from Nota_ensayo notaEnsayo "
				// + "inner join notaEnsayo.estadoNota estado "
				+ "inner join notaEnsayo.crdEspecifico crdEspecifico "
				+ "where crdEspecifico.id=:idCRD and notaEnsayo.notaSitio='FALSE' and notaEnsayo.eliminado='FALSE' and notaEnsayo.notaPadre = null ";

		List<Nota_ensayo> datosNotas = entityManager.createQuery(queryNotas)
				.setParameter("idCRD", idCRD).getResultList();

		return datosNotas;
	}

	public Integer CantidadNotas(Long idCRD) {
		return NotasMonitoreo(idCRD).size();
	}

	public Long getIdMS() {
		return idMS;
	}

	public void setIdMS(Long idMS) {
		this.idMS = idMS;
	}

	public boolean isGestionar() {
		return gestionar;
	}

	public void setGestionar(boolean gestionar) {
		this.gestionar = gestionar;
	}

	public Long getIdSujeto() {
		return idSujeto;
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

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}

	public boolean isVer() {
		return ver;
	}

	public void setVer(boolean ver) {
		this.ver = ver;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	public Long getInstanciaFirmarHoja() {
		return instanciaFirmarHoja;
	}

	public void setInstanciaFirmarHoja(Long instanciaFirmarHoja) {
		this.instanciaFirmarHoja = instanciaFirmarHoja;
	}

	public CrdEspecifico_ensayo getHojaElimininarFirma() {
		return hojaElimininarFirma;
	}

	public void setHojaElimininarFirma(CrdEspecifico_ensayo hojaElimininarFirma) {
		this.hojaElimininarFirma = hojaElimininarFirma;
	}

	public boolean isCausaRequired() {
		return causaRequired;
	}

	public void setCausaRequired(boolean causaRequired) {
		this.causaRequired = causaRequired;
	}

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public String getFromP() {
		return fromP;
	}

	public void setFromP(String fromP) {
		this.fromP = fromP;
	}

	public Long cdrCompletadaSinFirmar() {
	
		try {
					
			BigInteger result = (BigInteger) entityManager
					.createNativeQuery("SELECT count(ensayo.crd_especifico.id_hoja_crd) FROM ensayo.crd_especifico "
							+ "JOIN ensayo.momento_seguimiento_especifico ON ensayo.crd_especifico.id_momento_seg_especifico = ensayo.momento_seguimiento_especifico.id "
							+ "JOIN ensayo.momento_seguimiento_general ON ensayo.momento_seguimiento_especifico.id_momento_seguimiento_g = ensayo.momento_seguimiento_general.id "
							+ "JOIN ensayo.cronograma ON ensayo.momento_seguimiento_general.id_cronograma = ensayo.cronograma.id "
							+ "JOIN ensayo.grupo_sujetos ON ensayo.cronograma.id_grupo_sujetos = ensayo.grupo_sujetos.id "
							+ "JOIN ensayo.estudio	ON ensayo.grupo_sujetos.id_estudio = ensayo.estudio.id "
							+ "WHERE ensayo.crd_especifico.eliminado = false and ensayo.crd_especifico.id_estado_monitoreo = 3 AND ensayo.crd_especifico.id_estado_hoja_crd = 3 AND ensayo.estudio.id = :idEstudioActivo AND ensayo.grupo_sujetos.nombre_grupo <> 'Grupo Validación'")
					.setParameter("idEstudioActivo", seguridadEstudio.idEstudioActivo)
					.getSingleResult();

			return result.longValue();

		} catch (Exception e) {
			// manejar la excepción
			System.out.println("error cdrCompletadaSinFirmar: "
					+ e.getMessage());
			return null;
		}
	}

}
