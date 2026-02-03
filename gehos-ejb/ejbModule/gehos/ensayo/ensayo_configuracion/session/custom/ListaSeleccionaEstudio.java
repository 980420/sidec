package gehos.ensayo.ensayo_configuracion.session.custom;

import gehos.autenticacion.entity.Usuario;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.session.common.auto.EstudioEntidadList_ensayo;

import java.util.Arrays;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Name("listaSeleccionarEstudios")
@Scope(ScopeType.PAGE)
public class ListaSeleccionaEstudio extends EstudioEntidadList_ensayo {
	@In
	SeguridadEstudio seguridadEstudio;
	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;
	@In
	Usuario user;

	private String nombre;
	private int pagina;

	private static final String EJBQL = "select estudioEntidad from "
			+ "EstudioEntidad_ensayo estudioEntidad "
			+ "JOIN estudioEntidad.entidad entidadE "
			+ "JOIN estudioEntidad.estudio estudioE " + "where "
			+ "estudioEntidad.eliminado <> true "
			+ "and entidadE.eliminado <> true "
			+ "and estudioE.eliminado <> true ";

	private static final String[] RESTRICTIONSS = {
			"lower(estudioE.nombre) like concat('%',concat(lower(#{listaSeleccionarEstudios.nombre.trim()}),'%'))",
			"entidadE.id = #{listaSeleccionarEstudios.activeModule.getActiveModule().getEntidad().getId()}",
			"estudioEntidad.id in (select u.estudioEntidad.id from UsuarioEstudio_ensayo u where u.usuario.id = #{listaSeleccionarEstudios.user.id} and u.eliminado <> true and u.role.eliminado <> true and u.estudioEntidad.eliminado <> true)" };

	public ListaSeleccionaEstudio() {

		setEjbql(EJBQL);
		setMaxResults(10);
		setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONSS));
		setOrder("estudioEntidad.id desc");
	}
	// ----Properties-----

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
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

	public IActiveModule getActiveModule() {
		// rafa
		return activeModule;
	}

	public void setActiveModule(IActiveModule activeModule) {
		this.activeModule = activeModule;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

}
