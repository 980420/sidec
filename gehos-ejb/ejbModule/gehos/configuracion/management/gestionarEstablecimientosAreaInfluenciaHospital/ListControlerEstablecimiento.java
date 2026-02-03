package gehos.configuracion.management.gestionarEstablecimientosAreaInfluenciaHospital;

import gehos.configuracion.management.entity.EstablecimientoAreaInfluenciaHospital_configuracion;

import java.util.ArrayList;
import java.util.List;

public class ListControlerEstablecimiento {
	private List<EstablecimientoAreaInfluenciaHospital_configuracion> elementos;
	List<EstablecimientoAreaInfluenciaHospital_configuracion> resultado = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();
	List<EstablecimientoAreaInfluenciaHospital_configuracion> resultadoInicial = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();

	private Integer firstResult = 0;
	private String valor = "";
	private String segundoValor = "";

	public ListControlerEstablecimiento(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> elementos) {
		this.elementos = elementos;
	}

	public ListControlerEstablecimiento() {

	}

	public Integer getMaxResults() {
		return 10;
	}

	public int getPreviousFirstResult() {
		if (getFirstResult() == 0) {
			return 0;
		}
		return getFirstResult() - getMaxResults();
	}

	public List<EstablecimientoAreaInfluenciaHospital_configuracion> getElementos() {
		return elementos;
	}

	public void setElementos(
			List<EstablecimientoAreaInfluenciaHospital_configuracion> elementos) {
		this.elementos = elementos;
	}

	public int getNextFirstResult() {
		return getFirstResult() + getMaxResults();
	}

	public void remove(Integer pos) {
		elementos.remove(pos);
	}

	public int getLastFirstResult() {
		if (valor == "" && segundoValor == "") {
			int pos = elementos.size() / getMaxResults();
			if (elementos.size() % getMaxResults() == 0) {
				pos--;
			}
			return pos * getMaxResults();
		}
		int pos = resultado.size() / getMaxResults();
		if (resultado.size() % getMaxResults() == 0) {
			pos--;
		}
		return pos * getMaxResults();
	}

	public List<EstablecimientoAreaInfluenciaHospital_configuracion> getResultList() {
		resultado = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();
		resultadoInicial = new ArrayList<EstablecimientoAreaInfluenciaHospital_configuracion>();

		// cuando no se busca por ningun
		// criterio------------------------------------------------
		if (valor.equals("") && segundoValor.equals("")) {
			return elementos
					.subList(getFirstResult(),
							(getMaxResults() + getFirstResult()) > elementos
									.size() ? elementos.size()
									: getMaxResults() + getFirstResult());
		}
		// --------------------------------------------------------------------------------------

		boolean aux = false;
		boolean aux2 = false;
		byte[] valorList = valor.toLowerCase().getBytes();
		byte[] segundoValorList = segundoValor.toLowerCase().getBytes();
		byte[] tipoEstablecimientoList;
		byte[] nombreList;

		// cuando se busca por el
		// primero-------------------------------------------------------
		if (!valor.equals("")) {
			for (int i = 0; i < elementos.size(); i++) {
				nombreList = elementos.get(i).getNombre().toLowerCase()
						.getBytes();
				// validando primer valoir
				for (int k = 0; k < valorList.length; k++) {
					if (k < nombreList.length && valorList[k] == nombreList[k]) {
						aux = true;
					} else {
						aux = false;
						break;
					}
				}
				if (aux == true) {
					resultadoInicial.add(elementos.get(i));
				}
			}
		} else {
			resultadoInicial.addAll(elementos);
		}
		// -------------------------------------------------------------------------------------

		// cuando se busca por el segundo
		// valor--------------------------------------------------
		if (!segundoValor.equals("")) {
			for (int i = 0; i < resultadoInicial.size(); i++) {
				tipoEstablecimientoList = resultadoInicial.get(i)
						.getTipoEstablecimientoSalud().getValor().toLowerCase()
						.getBytes();

				// validando segundo valor
				for (int k = 0; k < segundoValorList.length; k++) {
					if (k < tipoEstablecimientoList.length
							&& segundoValorList[k] == tipoEstablecimientoList[k]) {
						aux2 = true;
					} else {
						aux2 = false;
						break;
					}
				}
				if (aux2 == true) {
					resultado.add(resultadoInicial.get(i));
				}
			}
			resultadoInicial = resultado;
		}

		return resultadoInicial
				.subList(getFirstResult(),
						(getMaxResults() + getFirstResult()) > resultadoInicial
								.size() ? resultadoInicial.size()
								: getMaxResults() + getFirstResult());

	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public boolean getPreviousExists() {
		return firstResult != 0;
	}

	public boolean getNextExists() {
		if (valor.equals("") && segundoValor.equals("")) {
			return (firstResult + getMaxResults()) < elementos.size();
		}
		return (firstResult + getMaxResults()) < resultado.size();

	}

	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}

	public String getValor() {
		return valor;
	}

	public void setValor(String valor) {
		this.valor = valor.trim();
	}

	public String getSegundoValor() {
		return segundoValor;
	}

	public void setSegundoValor(String segundoValor) {
		this.segundoValor = segundoValor;
	}

}
