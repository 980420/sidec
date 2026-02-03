package gehos.configuracion.management.gestionarEntidadesSistema;

import gehos.configuracion.management.entity.Entidad_configuracion;

import java.util.ArrayList;
import java.util.List;

public class ListControlerEntidad {
	private List<Entidad_configuracion> elementos;
	List<Entidad_configuracion> resultado = new ArrayList<Entidad_configuracion>();

	private Integer firstResult;
	private String valor = "";

	public ListControlerEntidad(List<Entidad_configuracion> elementos) {
		this.elementos = elementos;
		this.firstResult = 0;
	}

	public Integer getMaxResults() {
		return 5;
	}

	public int getPreviousFirstResult() {
		if (getFirstResult() == 0) {
			return 0;
		}
		return getFirstResult() - getMaxResults();
	}

	public List<Entidad_configuracion> getElementos() {
		return elementos;
	}

	public void setElementos(List<Entidad_configuracion> elementos) {
		this.elementos = elementos;
	}

	public int getNextFirstResult() {
		return getFirstResult() + getMaxResults();
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

	public List<Entidad_configuracion> getResultList() {
		resultado = new ArrayList<Entidad_configuracion>();

		// cando no se busca por ningun criterio
		if (valor.equals("")) {
			return elementos
					.subList(getFirstResult(),
							(getMaxResults() + getFirstResult()) > elementos
									.size() ? elementos.size()
									: getMaxResults() + getFirstResult());
		}

		// algoritmoo
		boolean aux = false;
		byte[] valorList = valor.toLowerCase().getBytes();
		byte[] nombreList;
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
				resultado.add(elementos.get(i));
			}
		}

		return resultado
				.subList(
						getFirstResult(),
						(getMaxResults() + getFirstResult()) > resultado.size() ? resultado
								.size()
								: getMaxResults() + getFirstResult());

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
