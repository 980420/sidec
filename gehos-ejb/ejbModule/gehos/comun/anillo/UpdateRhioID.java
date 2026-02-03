package gehos.comun.anillo;

import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.configuracion.management.entity.InstanciaHis_configuracion;

import java.math.BigInteger;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hibernate.validator.Max;
import org.hibernate.validator.Min;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.ui.util.Faces;

@Scope(ScopeType.PAGE)
@Name("updateRhioID")
public class UpdateRhioID {

	private Long id;
	private String nombreAnillo;
	private boolean modificar = false;

	@In
	private EntityManager entityManager;

	@In
	FacesMessages facesMessages;

	@In
	private AnilloHisConfig anilloHisConfig;

	@Create
	public void initAnilloConfig() {

		try {
			// Se busca si existe alguna instancia configurada con ese
			// identificador
			InstanciaHis_configuracion instanciaHis = anilloHisConfig
					.getHisInstance();

			if (instanciaHis != null) {
				id = instanciaHis.getId();
				nombreAnillo = instanciaHis.getNombreInstancia();
			}

		} catch (Exception e) {
			e.printStackTrace();
			facesMessages
					.add("msg_errorConfAnillo_modConfig");

		}

	}

	public boolean existeAnilloConfigurado() {
		boolean existeAnilloConfigurado = false;

		try {
			existeAnilloConfigurado = anilloHisConfig.getHisInstance() != null ? true
					: false;

		} catch (Exception e) {
			facesMessages.add(e.getMessage());
		}
		return existeAnilloConfigurado;
	}

	// // Devuelve si el anillo esta configurado
	// public Boolean existeAnilloConfigurado() {
	//
	// Boolean isIdentificadorPorDefecto = null;
	// try {
	// isIdentificadorPorDefecto = anilloHisConfig
	// .identificadorPorDefecto();
	//
	// // Si existe el identificador por defecto
	// if (isIdentificadorPorDefecto)
	// return false;
	//
	// // Si no existe el identificador por defecto pero no hay
	// // instancia_his configurada
	// else if (id == null)
	// return false;
	// else
	// return true;
	//
	// } catch (Exception e) {
	// facesMessages.add(e.getMessage());
	// return null;
	// }
	//		
	//
	// }

	// Actualiza el identificador del anillo
	public void actualizar() {

		try {
			if (id != anilloHisConfig.getHisInstanceNumber())
				anilloHisConfig.updateFunctionRhio(id.toString());
			this.modificar = false;
			createHisInstance();
		} catch (Exception e) {
			facesMessages.add(e.getMessage());

		}

	}

	// Se encarga de crear la instancia del his en la bd
	public void createHisInstance() {

		InstanciaHis_configuracion instanciaHis = anilloHisConfig
				.getHisInstance();
		String action = null;
		try {

			action = existeAnilloConfigurado() ? "modificado" : "creado";

			if (instanciaHis == null)
				instanciaHis = new InstanciaHis_configuracion();

			instanciaHis.setNombreInstancia(nombreAnillo);
			instanciaHis.setCid(-1l);
			instanciaHis.setId(id);

			entityManager.merge(instanciaHis);
			anilloHisConfig.setHisInstanceNumber(id);

			facesMessages.add("Se ha " + action
					+ " satisfactoriamente la instancia.");

		} catch (Exception e) {
			facesMessages.add("No se ha " + action
					+ " la instancia del his con identificador: " + id
					+ " y nombre: " + nombreAnillo
					+ ". Contacte al equipo de soporte");

		}

	}

	@Max(value = 9999)
	@Min(value = 1002)
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * @return the nombreAnillo
	 */
	public String getNombreAnillo() {
		return nombreAnillo;
	}

	/**
	 * @param nombreAnillo
	 *            the nombreAnillo to set
	 */
	public void setNombreAnillo(String nombreAnillo) {
		this.nombreAnillo = nombreAnillo;
	}

	public boolean isModificar() {
		return modificar;
	}

	public void setModificar(boolean modificar) {
		this.modificar = modificar;
	}

}
