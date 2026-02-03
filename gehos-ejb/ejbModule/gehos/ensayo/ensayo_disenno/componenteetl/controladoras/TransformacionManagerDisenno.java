package gehos.ensayo.ensayo_disenno.componenteetl.controladoras;


import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.FXMLDisenno;
import gehos.ensayo.ensayo_disenno.componenteetl.auxiliares.TransformacionDisenno;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleXMLException;

@Name("transfManagerDisenno")
@Scope(ScopeType.SESSION)
public class TransformacionManagerDisenno {

	private List<TransformacionDisenno> listadetransfs;
	List<TransformacionDisenno> listbusqueda;
	private List<String> logs;
	private Boolean buscando;
	private Boolean cargada;
	private String dirTransf;

	public String getDirTransf() {
		return dirTransf;
	}

	public void setDirTransf(String dirTransf) {
		this.dirTransf = dirTransf;
	}

	public List<TransformacionDisenno> getListadetransfs() {
		if (buscando == true) {
			return listbusqueda;
		} else
			return listadetransfs;
	}

	public void setListadetransfs(List<TransformacionDisenno> listadetransfs) {
		this.listadetransfs = listadetransfs;
	}

	public List<String> getLogs() {
		return logs;
	}

	public void setLogs(List<String> logs) {
		this.logs = logs;
	}

	public TransformacionManagerDisenno() {
		logs = new ArrayList<String>();
		listadetransfs = new ArrayList<TransformacionDisenno>();
		listbusqueda = new ArrayList<TransformacionDisenno>();
		buscando = false;
		cargada = false;
		dirTransf = System.getProperty("jboss.server.home.dir")
				+ File.separator + "deploy" + File.separator + "gehos-ear.ear"
				+ File.separator + "componente_etl" + File.separator
				+ "transformaciones";
		try {
			if (!KettleEnvironment.isInitialized()) {
				KettleEnvironment.init();
			}
		} catch (Exception e) {
			addLog(e.getMessage(), true);
		}
	}

	public void cargaInicial() throws Exception {
		try {
			if (cargada == false) {
				File direct = new File(dirTransf);
				if (direct.exists()) {
					File[] ficheros = direct.listFiles();
					for (File file : ficheros) {
						FXMLDisenno ftran = new FXMLDisenno(file.getAbsolutePath());
						TransformacionDisenno transf = new TransformacionDisenno(ftran);
						agregar(transf);
					}

				} else
					addLog("El directorio de las transformaciones no existe.",
							true);
				cargada = true;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	public void addLog(String msg, Boolean time) {
		if (time) {
			Calendar fecha = Calendar.getInstance();
			int ano = fecha.get(Calendar.YEAR);
			int mes = fecha.get(Calendar.MONTH);
			int dia = fecha.get(Calendar.DAY_OF_MONTH);
			int hora = fecha.get(Calendar.HOUR_OF_DAY);
			int minuto = fecha.get(Calendar.MINUTE);
			int segundo = fecha.get(Calendar.SECOND);
			String fechaactual = String.format("[" + dia + "/" + (mes + 1)
					+ "/" + ano + " %02d:%02d:%02d] - ", hora, minuto, segundo);
			logs.add(fechaactual + msg);
		} else
			logs.add(msg);
	}

	public void buscar(String nombreTr) {
		listbusqueda.clear();
		for (TransformacionDisenno transformacion : listadetransfs) {
			if (comparar(transformacion.getNombre(), nombreTr) != 0)
				listbusqueda.add(transformacion);
		}
		buscando = true;
	}

	public void cancelar() {
		buscando = false;
		ordenar("no");
	}

	private int comparar(String cadena, String subcadena) {
		int c = 0;
		int sizec = cadena.length();
		int sizesc = subcadena.length();
		String cIsc = subcadena.substring(0, 1);
		int i = 0;
		while (i <= (sizec - sizesc) && c == 0) {
			String cIc = cadena.substring(i, i + 1);
			if (cIsc.equals(cIc)) {
				if (cadena.substring(i, i + sizesc).equals(subcadena)) {
					c++;
				}
			}
			i++;
		}
		return c;
	}

	public void ordenar(String orden) {
		if (orden.equals("asc")) {
			if (buscando == true) {
				orderA(listbusqueda);
			} else
				orderA(listadetransfs);
		} else if(orden.equals("desc")){
			if (buscando == true) {
				orderD(listbusqueda);
			} else
				orderD(listadetransfs);
		}else{
			if (buscando == true) {
				orderE(listbusqueda);
			} else
				orderE(listadetransfs);
		}
	}

	@SuppressWarnings("unchecked")
	private void orderA(List<TransformacionDisenno> lista) {
		Collections.sort(lista, new Comparator() {
			public int compare(Object o1, Object o2) {
				TransformacionDisenno t1 = (TransformacionDisenno) o1;
				TransformacionDisenno t2 = (TransformacionDisenno) o2;
				return t1.getNombre().compareTo(t2.getNombre());
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void orderD(List<TransformacionDisenno> lista) {
		Collections.sort(lista, new Comparator() {
			public int compare(Object o1, Object o2) {
				TransformacionDisenno t1 = (TransformacionDisenno) o1;
				TransformacionDisenno t2 = (TransformacionDisenno) o2;
				return t2.getNombre().compareTo(t1.getNombre());
			}
		});
	}
	
	@SuppressWarnings("unchecked")
	private void orderE(List<TransformacionDisenno> lista) {
		Collections.sort(lista, new Comparator() {
			public int compare(Object o1, Object o2) {
				TransformacionDisenno t1 = (TransformacionDisenno) o1;
				TransformacionDisenno t2 = (TransformacionDisenno) o2;
				Integer ord1 = new Integer(t1.getOrden());
				Integer ord2 = new Integer(t2.getOrden());
				return ord1.compareTo(ord2);
			}
		});
	}

	public void eliminar(TransformacionDisenno transf) {
		listadetransfs.remove(transf);
		if (buscando == true && listbusqueda.contains(transf))
			listbusqueda.remove(transf);
		File file = new File(transf.getDireccion());
		if (file.exists()) {
			file.delete();
		}
	}

	public void save(TransformacionDisenno transf) throws KettleXMLException {
		try {
			transf.getTmeta().writeXML(transf.getDireccion());
		} catch (KettleXMLException e) {
			throw e;
		}
	}

	public void limpiar() {
		if (listbusqueda.size() != 0) {
			listadetransfs.removeAll(listbusqueda);
			listbusqueda.clear();
		} else
			listadetransfs.clear();
	}

	public void agregar(TransformacionDisenno tran) {
		buscando = false;
		if (longitud() == 0 || tran.getOrden() == -1) {
			listadetransfs.add(tran);
		} else {
			int pos = 0;
			TransformacionDisenno taux = listadetransfs.get(0);
			while (pos < longitud() && taux.getOrden() != -1
					&& taux.getOrden() < tran.getOrden()) {
				pos++;
				if (pos != longitud())
					taux = listadetransfs.get(pos);
			}
			if (pos == longitud()) {
				listadetransfs.add(tran);
			} else {
				listadetransfs.add(pos, tran);
			}
		}

	}
	
	public void clearLog(){
		logs.clear();
	}

	public int longitud() {
		if (buscando == true) {
			return listbusqueda.size();
		}
		return listadetransfs.size();
	}

}
