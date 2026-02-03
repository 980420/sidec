package gehos.ensayo.ensayo_conduccion.gestionarReporteExpedito;
import java.util.List;

public class ReDataSource {
	
	List<ReProductoDataSource> listaProductos;
	List<ReVacunasDataSource> listaVacunas;
	List<ReEnfermedadesDataSource> listaEnfermedades;
	List<ReFarmacosDataSource> listaFarmacos;
	private String si;
	private String no;
	private String indicacion;
		

	public ReDataSource(List<ReProductoDataSource> listaProductos, List<ReVacunasDataSource> listaVacunas, List<ReEnfermedadesDataSource> listaEnfermedades,List<ReFarmacosDataSource> listaFarmacos,
			String si, String no, String indicacion) {
		super();
		this.listaProductos = listaProductos;
		this.listaVacunas=listaVacunas;
		this.listaEnfermedades=listaEnfermedades;
		this.listaFarmacos=listaFarmacos;
		this.si=si;
		this.no=no;
		this.indicacion=indicacion;
	}

	public ReDataSource() {
		super();
		// TODO Auto-generated constructor stub
	}

	public List<ReProductoDataSource> getListaProductos() {
		return listaProductos;
	}

	public void setListaProductos(List<ReProductoDataSource> listaProductos) {
		this.listaProductos = listaProductos;
	}

	public String getSi() {
		return si;
	}

	public void setSi(String si) {
		this.si = si;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getIndicacion() {
		return indicacion;
	}

	public void setIndicacion(String indicacion) {
		this.indicacion = indicacion;
	}

	public List<ReVacunasDataSource> getListaVacunas() {
		return listaVacunas;
	}

	public void setListaVacunas(List<ReVacunasDataSource> listaVacunas) {
		this.listaVacunas = listaVacunas;
	}

	public List<ReEnfermedadesDataSource> getListaEnfermedades() {
		return listaEnfermedades;
	}

	public void setListaEnfermedades(
			List<ReEnfermedadesDataSource> listaEnfermedades) {
		this.listaEnfermedades = listaEnfermedades;
	}

	public List<ReFarmacosDataSource> getListaFarmacos() {
		return listaFarmacos;
	}

	public void setListaFarmacos(List<ReFarmacosDataSource> listaFarmacos) {
		this.listaFarmacos = listaFarmacos;
	}
	
	

	
	
	
	

}
