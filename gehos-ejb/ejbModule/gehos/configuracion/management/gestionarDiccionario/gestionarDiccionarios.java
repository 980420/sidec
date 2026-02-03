package gehos.configuracion.management.gestionarDiccionario;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.componenteetl.auxiliares.Conexion;
import gehos.configuracion.componenteetl.auxiliares.FExcel;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.Validations_ensayo;
import gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd.tools.FileExcel;
import gehos.ensayo.ensayo_extraccion.session.gestionarConjuntoDatos.UtilConjuntoDatos;
import gehos.ensayo.entity.*;
import gehos.ensayo.session.custom.CieConsList_custom;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

import org.hibernate.validator.NotEmpty;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.international.StatusMessage.Severity;
import org.jboss.seam.framework.EntityQuery;


@Name("gestionarDiccionarios")
@Scope(ScopeType.CONVERSATION)
public class gestionarDiccionarios {

	@In
	FacesMessages facesMessages;

	@In
	EntityManager entityManager;

	@In
	Usuario user;
	
	@In(create = true, value="cargarDiccionario")
	CargarDiccionario cargarDiccionario;

	@In
	private IActiveModule activeModule;
	
	@In(create = true)
	CieConsList_custom cieConsList_custom;
	
	private Conexion con;
	private FExcel forigen;
	private byte[] data;
	private String descripcion;
	private Diccionarios_ensayo diccionariosEnsayo = new Diccionarios_ensayo();
	
	public gestionarDiccionarios() {
		con = new Conexion();
		forigen = new FExcel();
	}
	
	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	public Conexion getCon() {
		return con;
	}

	public void setCon(Conexion con) {
		this.con = con;
	}

	public FExcel getForigen() {
		if(forigen == null)
			forigen = new FExcel();
		return forigen;
	}

	public void setForigen(FExcel forigen) {
		this.forigen = forigen;		
	}
	
	public void prueba(){
		System.out.print("NADA");
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
	
	private boolean subirExcel() {

		FacesContext aFacesContext = FacesContext.getCurrentInstance();
		ServletContext context = (ServletContext) aFacesContext
				.getExternalContext().getContext();
		String rootpath = context.getRealPath(File.separator + "resources"
				+ File.separator + "modEnsayo" + File.separator + "crdImport"
				+ File.separator);
		SimpleDateFormat format = new SimpleDateFormat(
				"dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");

		String zipName = forigen.getNombre();
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
			Cell [] datosDICC = dicc.getRow(0);
			
			if (datosDICC[0].getContents().trim().equals("MedDRA Code")
							&& datosDICC[1].getContents().trim().equals("MedDRA SOC")
											&& datosDICC[2].getContents().trim().equals("CTCAE Term") 
												&& datosDICC[3].getContents().trim().equals("Descripcion")) {
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
	
	public void cargar() throws Exception, IOException {

		String hojaExcel = "Hoja Excel";
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
			if (dicc.getRows() == 1) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("editar",Severity.ERROR, "Esta vacio inserte datos");
				return;
			}
			
			else {
				
				hojaExcel = "Hoja numero 1";
				Diccionarios_ensayo d=new Diccionarios_ensayo();
				String nombreFinal=forigen.getNombre().substring(0,forigen.getNombre().length()-4);
				@SuppressWarnings("unchecked")
				List<Diccionarios_ensayo> res=(List<Diccionarios_ensayo>)this.entityManager.createQuery("select d from Diccionarios_ensayo d where d.eliminado = false and d.nombre=:nombreFinal").setParameter("nombreFinal", nombreFinal).getResultList();
				if (!res.isEmpty()){
					facesMessages.clear();
					facesMessages.addToControlFromResourceBundle("editar",Severity.ERROR,"Ya el sistema contiene ese diccionario");
					return;
				}
				Cell[] a = dicc.getRow(1);
				/*if(CampoVacio(a[3])){
					MsgCampoObligatorioVacio(hojaExcel, 3, 0 + 1);
					return;
				}
				else{
					//d.setDescripcion(a[3].getContents().trim());
				}
				d.setNombre(nombreFinal);
				d.setEliminado(false);
				//desc
				d.setDescripcion(this.descripcion);
				entityManager.persist(d);*/
				
				for (int i = 1; i < dicc.getRows(); i++) {
					Cell[] datosDicc = dicc.getRow(i);

					if (!FilaVacia(datosDicc)) {

						if (datosDicc.length < 2) {
							MsgCampoObligatorioVacioFila(hojaExcel, i + 1);
							return;
						}
						// De esta manera se comprueba que los campos no esten
						// vacios y lee los datos de las filas
						else {
							Relacion_cat_metra_ensayo relacion = new Relacion_cat_metra_ensayo();
							CtcCategoria_ensayo categoria_ensayo = new CtcCategoria_ensayo();
							Ctc_ensayo ea_ensayo = new Ctc_ensayo();
						
							
                            // Valida y guarda si los datos estan bien el valor del codigo metra
							if (CampoVacio(datosDicc[0])) {
								MsgCampoObligatorioVacio(hojaExcel, 0, i + 1);
								return;
							} 
							else {
								ea_ensayo.setCodigoMedra(Integer.parseInt(datosDicc[0].getContents().trim()));								
							}
							
							// Valida y guarda si los datos estan bien el nombre de la categoria
							if (CampoVacio(datosDicc[1])) {
								MsgCampoObligatorioVacio(hojaExcel, 1, i + 1);
								return;
							} 
							else {
								categoria_ensayo.setNombreCategoria(datosDicc[1].getContents().trim());

							}
							
							// Valida y guarda si los datos estan bien el valor del termino
							if (CampoVacio(datosDicc[2])) {
								MsgCampoObligatorioVacio(hojaExcel, 2, i + 1);
								return;
							} 
							else {
								ea_ensayo.setEventoAdverso(datosDicc[2].getContents().trim());
							}
							//DESCRIPCIOOONNNN
							if(CampoVacio(a[3])){
								MsgCampoObligatorioVacio(hojaExcel, 3, i + 1);
								return;
							}
							else{
								d.setDescripcion(a[3].getContents().trim());
							}
							d.setNombre(nombreFinal);
							d.setEliminado(false);
							//desc
							d.setDescripcion(this.diccionariosEnsayo.getDescripcion() );
							entityManager.persist(d);
							
							// Todos los datos correcto y objetos creados
							categoria_ensayo.setDiccionario(d);
							
                           // Consulta para verificar si la categoría ya existe
							List<CtcCategoria_ensayo> categoriaBD = (List<CtcCategoria_ensayo>)this.entityManager
									.createQuery("select n from CtcCategoria_ensayo n where n.nombreCategoria=:nombre and n.diccionario.id=:iddiccionario")
									.setParameter("nombre", categoria_ensayo.getNombreCategoria())
									.setParameter("iddiccionario", categoria_ensayo.getDiccionario().getId())
									.getResultList();
							
							if(categoriaBD.isEmpty()){
								entityManager.persist(categoria_ensayo);
								ea_ensayo.setCtcCategoria(categoria_ensayo);
								relacion.setCategoria(categoria_ensayo);
							}else {
								ea_ensayo.setCtcCategoria(categoriaBD.get(0));
								relacion.setCategoria(categoriaBD.get(0));
							}
							
							/*List<Ctc_ensayo> t =(List<Ctc_ensayo>)this.entityManager
									.createQuery("select u from Ctc_ensayo u where u.codigoMedra=:codigoActual")
									.setParameter("codigoActual", ea_ensayo.getCodigoMedra())
									.getResultList();
							if(t.isEmpty()){*/
								relacion.setCtc(ea_ensayo);
								entityManager.persist(ea_ensayo);
							/*}else{
								relacion.setCtc(t.get(0));
							}*/
							entityManager.persist(relacion);
						}
					}
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
	
	public void ocultarDiccionario(Diccionarios_ensayo diccionario){
		
		diccionario.setEliminado(true);
		entityManager.merge(diccionario);
		entityManager.flush();
			
	}
	
	
	public Diccionarios_ensayo getDiccionariosEnsayo() {
		return diccionariosEnsayo;
	}

	public void setDiccionariosEnsayo(Diccionarios_ensayo diccionariosEnsayo) {
		this.diccionariosEnsayo = diccionariosEnsayo;
	}
	
	
}
