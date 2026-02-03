package gehos.configuracion.management.gestionarActividadesInvestigacionDocencia;

import gehos.configuracion.management.entity.ActividadInvestigacionDocencia_configuracion;

import java.util.ArrayList;

import java.util.List;

public class ListControlerActividades {
	private List<ActividadInvestigacionDocencia_configuracion> elementos;
	List<ActividadInvestigacionDocencia_configuracion> resultado = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
	List<ActividadInvestigacionDocencia_configuracion> resultadoInicial = new ArrayList<ActividadInvestigacionDocencia_configuracion>();

	private Integer firstResult = 0;
	private String valor = "";

	public ListControlerActividades(
			List<ActividadInvestigacionDocencia_configuracion> elementos) {
		this.elementos = elementos;
	}

	public ListControlerActividades() {

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

	public List<ActividadInvestigacionDocencia_configuracion> getElementos() {
		return elementos;
	}

	public void setElementos(
			List<ActividadInvestigacionDocencia_configuracion> elementos) {
		this.elementos = elementos;
	}

	public int getNextFirstResult() {
		return getFirstResult() + getMaxResults();
	}

	public void remove(Integer pos) {
		elementos.remove(pos);
	}

	public int getLastFirstResult() {
		if (valor == "") {
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

	public List<ActividadInvestigacionDocencia_configuracion> getResultList() {
		try {
			resultado = new ArrayList<ActividadInvestigacionDocencia_configuracion>();
			resultadoInicial = new ArrayList<ActividadInvestigacionDocencia_configuracion>();

			// cuando no se busca por ningun
			// criterio------------------------------------------------
			if (valor.equals("")) {
				return elementos
						.subList(getFirstResult(),
								(getMaxResults() + getFirstResult()) > elementos
										.size() ? elementos.size()
										: getMaxResults() + getFirstResult());
			}
			// --------------------------------------------------------------------------------------

			int pos = getFirstResult();
			int fin = getMaxResults() + pos;
			if (fin > elementos.size()) {
				fin = elementos.size();
			}

			// algoritmo
			boolean aux = false;
			byte[] valorList = valor.toLowerCase().getBytes();
			byte[] nombreList;

			for (int i = 0; i < elementos.size(); i++) {
				nombreList = elementos.get(i).getValor().toLowerCase().getBytes();
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

			return resultadoInicial
					.subList(getFirstResult(),
							(getMaxResults() + getFirstResult()) > resultadoInicial
									.size() ? resultadoInicial.size()
									: getMaxResults() + getFirstResult());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return elementos;

	}

	public Integer getFirstResult() {
		return firstResult;
	}

	public boolean getPreviousExists() {
		return firstResult != 0;
	}

	public boolean getNextExists() {
		if (valor == "") {
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

}
