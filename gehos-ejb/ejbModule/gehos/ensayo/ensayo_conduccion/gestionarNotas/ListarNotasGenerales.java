//CU 26 Visualizar notas de sitio por sujeto
package gehos.ensayo.ensayo_conduccion.gestionarNotas;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.session.custom.NotaCustomList;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.NotaGeneral_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;
import gehos.ensayo.entity.Variable_ensayo;
import gehos.ensayo.session.common.auto.NotaGeneralList_ensayo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("serial")
@Name("listarNotasGenerales")
@Scope(ScopeType.CONVERSATION)
public class ListarNotasGenerales extends NotaGeneralList_ensayo {

	private static final String EJBQL = "select notaG from NotaGeneral_ensayo notaG where notaG.eliminado=false and notaG.notaGeneralPadre = null and notaG.grupoSujetos.id = #{listarNotasGenerales.idGrupo} ";

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;

	private Long ideliminarNota, idGrupo;
	private int pagina;

	public ListarNotasGenerales() {
		setEjbql(EJBQL);
		// setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		setMaxResults(10);
		setOrder("notaG.id desc");
	}
	
	@Override
	public List<NotaGeneral_ensayo> getResultList() {

		if (!this.EntidadActiva().getTipoEntidad().getValor().equals(SeamResourceBundle
				.getBundle().getString("bioTecnologica"))) {

			String EJBQL = "select notaG from NotaGeneral_ensayo notaG where notaG.entidad = #{listarNotasGenerales.EntidadActiva()} and notaG.eliminado=false and notaG.notaGeneralPadre = null and notaG.grupoSujetos.id = #{listarNotasGenerales.idGrupo} ";
			this.setEjbql(EJBQL);
		} else {
			this.setEjbql(EJBQL);
		}

		return super.getResultList();

	}

	public Entidad_ensayo EntidadActiva() {
		return entityManager.find(Entidad_ensayo.class, this.activeModule
				.getActiveModule().getEntidad().getId());
	}

	public void SeleccionarInstanciaNota(long id) {
		this.setIdeliminarNota(id);
	}

	// CU 38 Eliminar nota general
	//@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void EliminarInstanciaNota() {
		
			NotaGeneral_ensayo notaEliminar = entityManager.find(
				NotaGeneral_ensayo.class, this.ideliminarNota);
		notaEliminar.setEliminado(true);
		// entityManager.refresh(notaEliminar);
		entityManager.persist(notaEliminar);
		entityManager.flush();
		
		

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

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Long getIdeliminarNota() {
		return ideliminarNota;
	}

	public Long getIdGrupo() {
		return idGrupo;
	}

	public void setIdGrupo(Long idGrupo) {
		this.idGrupo = idGrupo;
	}

	public void setIdeliminarNota(Long ideliminarNota) {
		this.ideliminarNota = ideliminarNota;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}
}
