package gehos.bitacora.session.traces;

import gehos.bitacora.entity.Funcionalidad_Bitacora;
import gehos.bitacora.entity.TrazaAccion;
import gehos.bitacora.entity.TrazaModuloAccedido;
import gehos.bitacora.entity.TrazaSession;
import gehos.bitacora.entity.User_Bitacora;

import java.util.Calendar;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@AutoCreate
@Name("bitacora")
@Scope(ScopeType.SESSION)
@Stateful
public class Bitacora implements IBitacora {

	@In(value = "#{remoteAddr}", required = false)
	private String ipString;

	@PersistenceContext(unitName = "gehos")
	private EntityManager entityManager;

	private TrazaSession session;
	private TrazaModuloAccedido moduloAccedido;

	@Remove
	@Destroy
	public void destructor() {
	}

	public void registrarInicioDeSession(String username) {
		session = new TrazaSession();
		User_Bitacora user = (User_Bitacora) entityManager
				.createQuery(
						"select user from User_Bitacora user where user.username = :username and user.eliminado = false")
				.setParameter("username", username).getSingleResult();
		session.setUser(user);

		Calendar calendar = Calendar.getInstance();
		session.setAnno(calendar.get(Calendar.YEAR));
		session.setMes(calendar.get(Calendar.MONTH));
		session.setDia(calendar.get(Calendar.DAY_OF_MONTH));

		session.setFechaInicio(calendar.getTime());

		session.setHoraInicio(calendar.getTime());

		session.setDireccionIp(ipString);

		entityManager.persist(session);
		entityManager.flush();
	}

	public void registrarModuloAccedido(Long moduleId) {
		if (this.session != null) {
			// entityManager.merge(this.session);

			moduloAccedido = new TrazaModuloAccedido();
			moduloAccedido.setHora(Calendar.getInstance().getTime());

			Funcionalidad_Bitacora module = (Funcionalidad_Bitacora) entityManager
					.createQuery(
							"select m from Funcionalidad_Bitacora m where m.id = :id")
					.setParameter("id", moduleId).getSingleResult();

			moduloAccedido.setModulo(module);
			moduloAccedido.setTrazaSession(this.session);
			this.session.getTrazaModuloAccedidos().add(moduloAccedido);

			entityManager.persist(moduloAccedido);
			entityManager.flush();

		}
	}

	public Long registrarInicioDeAccion(String accion) {
		// entityManager.merge(this.moduloAccedido);

		TrazaAccion trazaAccion = new TrazaAccion();
		trazaAccion.setAccionRealizada(accion);

		Calendar calendar = Calendar.getInstance();
		trazaAccion.setHoraInicio(calendar.getTime());

		trazaAccion.setTrazaModuloAccedido(this.moduloAccedido);

		this.moduloAccedido.getTrazaAccions().add(trazaAccion);

		entityManager.persist(trazaAccion);

		entityManager.flush();

		return trazaAccion.getId();
	}

}
