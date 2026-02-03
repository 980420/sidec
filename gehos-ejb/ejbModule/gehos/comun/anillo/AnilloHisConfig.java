package gehos.comun.anillo;

import gehos.configuracion.management.entity.InstanciaHis_configuracion;

import java.math.BigInteger;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Startup;
import org.jboss.seam.core.SeamResourceBundle;

/**
 * @author yurien
 * 
 *         CLASE ENCARGADA DE GESTIONAR LA CONFIGURACION DE LA INSTANCIA DEL HIS
 *         PERMITIENDO RESTRINGIR LAS ENTIDADES A AQUELLAS QUE PERTENEZCAN A LA
 *         INSTANCIA DEL HIS ACTUAL.
 */
@Name("anilloHisConfig")
@Scope(ScopeType.APPLICATION)
@Startup
public class AnilloHisConfig {

	// Almacena el identificador del anillo
	private Long hisInstanceNumber;

	private InstanciaHis_configuracion hisInstance;

	// contiene el identificador que se usa por defecto
	private static final Long ANILLO_DEFAULT = 1000l;

	@In
	private EntityManager entityManager;

	/**
	 * @author yurien Cuando inicia el servidor ejecuta la funcion get_rhio_id()
	 *         y guarda el identificador del anillo en hisInstanceNumber
	 * **/
	@Create
	public void initAnilloHisConfig() {

		try {

			hisInstanceNumber = getFunctionRhio();
			hisInstance = findInstance();

		} catch (Exception e) {
			System.err
					.println(SeamResourceBundle.getBundle().getString("msg_identAnilloErrorFunc_modConfig"));
		}

	}

	/**
	 * @author yurien
	 * @return InstanciaHis_configuracion la instancia asociada al anillo
	 * **/
	private InstanciaHis_configuracion findInstance() {

		try {
			return entityManager.find(InstanciaHis_configuracion.class,
					hisInstanceNumber);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	// Devuelve si el identificador actual
	// es el que esta por defecto
	public Boolean identificadorPorDefecto() throws Exception {

		Boolean isIdentificadorPorDefecto = null;
		try {
			// Si el identificador del anillo no es null
			if (hisInstanceNumber != null)
				isIdentificadorPorDefecto = hisInstanceNumber
						.equals(ANILLO_DEFAULT);
		} catch (Exception e) {
			// en caso que sea null devolver true para que no se pueda hacer
			// nada 
			throw new Exception(
					SeamResourceBundle.getBundle().getString("msg_identAnilloErrorConf_modConfig"));
		}
		return isIdentificadorPorDefecto;

	}

	// Ejecuta la funcion que devuelve
	// el identificador del anillo configurado (destanque)
	public Long getFunctionRhio() {

		return Long.parseLong(((BigInteger) entityManager.createNativeQuery(
				"select get_rhio_id()").getSingleResult()).toString());
	}

	// Actualiza la funcion get_rhio_id ()
	public void updateFunctionRhio(String idAnillo) throws Exception {

		if (idAnillo != null && !idAnillo.isEmpty()) {
			String query = "CREATE OR REPLACE FUNCTION public.get_rhio_id () RETURNS bigint AS "
					+ "$body$ "
					+ "BEGIN "
					+ "    RETURN "
					+ idAnillo
					+ "; "
					+ "END; "
					+ "$body$ "
					+ "LANGUAGE 'plpgsql' "
					+ "VOLATILE "
					+ "CALLED ON NULL INPUT "
					+ "SECURITY INVOKER "
					+ "COST 100;";

			entityManager.createNativeQuery(query).executeUpdate();
		} else
			
			throw new Exception(
					SeamResourceBundle.getBundle().getString("msg_identAnilloNull_modConfig"));

	}

	

	/**
	 * @author yurien 8/04/2014 Para cuando no se haya configurado un
	 *         identificador de anillo en la instancia del his actual
	 * **/
	public boolean hayAnilloConfigurado() {

		try {
			if (this.hisInstance == null)
				return false;
			else
				return true;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void setHisInstanceNumber(Long hisInstanceNumber) {
		this.hisInstanceNumber = hisInstanceNumber;
		this.hisInstance = findInstance();

	}
	
	/**
	 * @return the hisInstanceNumber
	 */
	public Long getHisInstanceNumber() {
		return hisInstanceNumber;
	}
	public InstanciaHis_configuracion getHisInstance() {
		return hisInstance;
	}

	
}
