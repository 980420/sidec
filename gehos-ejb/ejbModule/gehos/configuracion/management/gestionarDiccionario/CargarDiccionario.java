package gehos.configuracion.management.gestionarDiccionario;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.configuracion.componenteetl.auxiliares.FExcel;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.tools.FileExcel;
import gehos.ensayo.entity.Cie_ensayo;
import gehos.ensayo.entity.CtcCategoria_ensayo;
import gehos.ensayo.entity.Ctc_ensayo;
import gehos.ensayo.entity.Diccionarios_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Nomenclador_ensayo;
import gehos.ensayo.entity.PresentacionFormulario_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.TipoDato_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.eclipse.osgi.framework.internal.core.Msg;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.log.Logging;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import antlr.StringUtils;

@Name("cargarDiccionario")
@Scope(ScopeType.PAGE)
public class CargarDiccionario {


	private static final Logger log = LoggerFactory.getLogger(CargarDiccionario.class);

	private FExcel forigen;

	public FExcel getForigen() {
		return forigen;
	}

	public void setForigen(FExcel forigen) {
		this.forigen = forigen;
	}

	private byte[] data;

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	private int filaActual;

	@In
	EntityManager entityManager;
	@In
	FacesMessages facesMessages;
	@In
	SeguridadEstudio seguridadEstudio;
	@In
	IBitacora bitacora;
	@In
	Usuario user;

	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public Usuario getUser() {
		return user;
	}

	public void setUser(Usuario user) {
		this.user = user;
	}

	public CargarDiccionario() {
		super();
		this.forigen = new FExcel("");
		this.forigen.setNombre("");
	}

	private boolean subirExcel() {

		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath(File.separator + "resources"
				+ File.separator + "modEnsayo" + File.separator + "crdImport"
				+ File.separator);
		SimpleDateFormat format = new SimpleDateFormat(
				"dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");

		String zipName = "Primer_Diccionario_Prueba" + format.format(new Date()) + ".xls";
		rootpath = rootpath + File.separator + zipName;

		try {

			System.out
			.println("*****************************************************************************");
			System.out.println("*SIZE: " + data.length); // Esto es byte
			System.out
			.println("*****************************************************************************");

			if (data.length > 3145728) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("globalMessage",
						Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("prm_ficherogrande_ensClin"));
				return false;
			}

			System.out
			.println("*****************************************************************************");
			System.out.println("*SIZE: " + data.length); // Esto es byte
			System.out
			.println("*****************************************************************************");

			if (data.length > 3145728) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("globalMessage",
						Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("prm_ficherogrande_ensClin"));
				return false;
			}

			File file = new File(rootpath);

			FileOutputStream fileOutputStream = new FileOutputStream(file);
			DataOutputStream dataOutputStream = new DataOutputStream(
					fileOutputStream);

			dataOutputStream.write(this.data);

			dataOutputStream.flush();
			dataOutputStream.close();
			fileOutputStream.close();

			forigen = new FExcel(file.getPath());
			forigen.setNombre(file.getName());

		} catch (IOException e) {

			e.printStackTrace();

		}

		return true;
	}

	private Boolean validarExcel() throws BiffException, IOException {

		try {
			File subi = new File(forigen.getDir());

			WorkbookSettings cod = new WorkbookSettings();
			cod.setEncoding("Cp1252");
			Workbook workbook = Workbook.getWorkbook(subi,cod);

			// validar la hoja crd
			Sheet dicc = workbook.getSheet(0);
			Cell[] datosDICC = dicc.getRow(0);
			
			if (datosDICC[0].getContents().trim().equals("MedDRA Code")
							&& datosDICC[1].getContents().trim().equals("MedDRA SOC")
											&& datosDICC[2].getContents().trim().equals("CTCAE Term")) {
				return true;
			}

			else {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("addExcel",Severity.ERROR, "EL archivo no cumple con los campos especificados");
				return false;
			}

		} catch (Exception e) {

			e.printStackTrace();

			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle("addExcel",Severity.ERROR,"No cumple con lo requerido");
			return false;

		}

	}
	
	

	public void Execute() throws IOException, Exception {

		if (forigen.getNombre().isEmpty()) {
			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle(
					"addExcel",
					Severity.ERROR,"EEEEEEEEEEEEERRRRRRRRRRRROOOOOOOOOOOOOOOORRRRRRRRRRRR");
			return;
		}

		// Subo el excel al servidor
		if (!subirExcel()) {
			return;
		}

		// Valido si el excel cumple las normas de la plantilla
		if (!validarExcel()) {
			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle("globalMessage",Severity.ERROR,"No se cumplen con lo especificado para el sistema");
			return;
		}

		// LLenar los datos
		// llenarDatos();
		cargar();
	}

	/***
	 * 
	 * @throws Exception
	 * @throws IOException
	 */
	
	
	/**
	 * @throws Exception
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void cargar() throws Exception, IOException {

		String hojaExcel = SeamResourceBundle.getBundle().getString("msg_hojaCRD_ens");
		try {

			File subi = new File(forigen.getDir());

			WorkbookSettings cod = new WorkbookSettings();
			cod.setEncoding("Cp1252");
			Workbook workbook = Workbook.getWorkbook(subi, cod);

			/**
			 * Leer datos
			 */
			Sheet dicc = workbook.getSheet(0);
			// Verifico si el diccionario esta vacio
			// la asignación de fila y columna es para en caso de error saber
			// donde se ocurrió el problema
			filaActual = 0;
			if (dicc.getRows() == 1) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("editar",Severity.ERROR, "Esta vacio inserte datos");
				return;
			}
			
			else {
				List<Ctc_ensayo> list = new ArrayList<Ctc_ensayo>();
				// obtener los datos y guardarlas en una lista
				hojaExcel = SeamResourceBundle.getBundle().getString("msg_hojaSeccion_ens");
				//this.forigen.getNombre();
				Diccionarios_ensayo d=new Diccionarios_ensayo();
				d.setNombre(this.forigen.getNombre());
				entityManager.persist(d);
				//persistir aqui o abajo
				for (int i = 1; i < dicc.getRows(); i++) {
					// la asignación de fila y columna es para en caso de error
					// saber donde se ocurrió el problema
					filaActual = i;

					Cell[] datosDicc = dicc.getRow(i);

					if (!FilaVacia(datosDicc)) {

						if (datosDicc.length < 2) {
							MsgCampoObligatorioVacioFila(hojaExcel, i + 1);
							return;
						}
						// De esta manera se comprueba que los campos no esten
						// vacios y lee los datos de las filas
						else {
							CtcCategoria_ensayo categoria_ensayo = new CtcCategoria_ensayo();
							Ctc_ensayo ea_ensayo = new Ctc_ensayo();

							if (CampoVacio(datosDicc[0])) {
								MsgCampoObligatorioVacio(hojaExcel, 0, i + 1);
								return;
							} 
							else {
								ea_ensayo.setCodigoMedra(Integer.parseInt(datosDicc[0].getContents().trim()));
							}
							if (CampoVacio(datosDicc[1])) {
								MsgCampoObligatorioVacio(hojaExcel, 1, i + 1);
								return;
							} 
							else {
								categoria_ensayo.setNombreCategoria(datosDicc[1].getContents().trim());

							}
							if (CampoVacio(datosDicc[2])) {
								MsgCampoObligatorioVacio(hojaExcel, 2, i + 1);
								return;
							} 
							else {
								ea_ensayo.setEventoAdverso(datosDicc[2].getContents().trim());
							}

							if (categoria_ensayo != null) {
								//ea_ensayo.setCtcCategoria(categoria_ensayo);
								list.add(ea_ensayo);// Aqui guardo el valor en
													// una lista de esa fila
							}
						}
					}
				}

				for (Ctc_ensayo c : list) {
					//entityManager.persist(c.getCtcCategoria());
					entityManager.persist(c);
				}
				entityManager.flush();
			}
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle("editar",Severity.ERROR,"Ocurrio un error desconocido intentelo de nuevo");
			return;
		}

	}
// end del metodo
	
	public boolean FilaVacia(Cell[] row){
		
		if(row.length==0 || row==null){
			return true;
		}			
		else{
			boolean flagEmpty=true;
			for(int i =0; i<row.length; i++){
				if(!row[i].getContents().trim().equals("")){
					flagEmpty=false;
					break;
				}					
			}
			if(flagEmpty)
				return true;
			return false;
		}
	}

	public boolean CampoVacio(Cell cell){	
		String type= cell.toString();
		if(cell.getContents().trim().equals("") || type.contains("EmptyCell")){
			return true;
		}
		return false;
	}
	
	public void MsgCampoObligatorioVacio(String hojaExcel, int column, int row){
		
		char columnValue = (char)(column+65);
		
		facesMessages.clear();
		facesMessages
				.addToControlFromResourceBundle(
						"editar",
						Severity.ERROR,SeamResourceBundle
						.getBundle()
						.getString(
								"msg_filasEnBlanco_ens")
						+ " "+hojaExcel+ " ("+ 
						SeamResourceBundle
						.getBundle()
						.getString(
								"msg_cell")					
						+columnValue
						+row
						+")");
	}
	
	public void MsgCampoObligatorioVacioFila(String hojaExcel, int row){
					
		facesMessages.clear();
		facesMessages
				.addToControlFromResourceBundle(
						"editar",
						Severity.ERROR,SeamResourceBundle
						.getBundle()
						.getString(
								"msg_filasEnBlanco_ens")
						+ " "+hojaExcel+ " ("+ 
						SeamResourceBundle
						.getBundle()
						.getString(
								"msg_fila")						
						+row
						+")");
	}
}// end clase
							
							
								
