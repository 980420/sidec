package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_conduccion.gestionarCRD.custom.GrupoWrapper;
import gehos.ensayo.entity.GrupoVariables_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.NomencladorValor_ensayo;
import gehos.ensayo.entity.Seccion_ensayo;
import gehos.ensayo.entity.Variable_ensayo;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileSystemView;

import jxl.CellType;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPalette;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.omg.CosNaming.NamingContextPackage.NotEmpty;

@Name("exel")
@Scope(ScopeType.CONVERSATION)
public class excel {

	protected @In
	EntityManager entityManager;
	protected @In
	IBitacora bitacora;
	protected @In(create = true)
	FacesMessages facesMessages;

	private long cid = -1;

	private List<Variable_ensayo> variables;
	private List<Seccion_ensayo> secciones;
	private List<HojaCrd_ensayo> crd;
	private List<GrupoVariables_ensayo> grupoVariables;
	private List<GrupoWrapper> listaGrupoW = new ArrayList<GrupoWrapper>();
	private Map<Long, List<Variable_ensayo>> listaVariables;
	private Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables;

	private HojaCrd_ensayo hoja;
	private Long idcrd;
	private Long idTabala;
	private boolean inicializado = false;

	private HSSFFont headerFont;
	private HSSFFont contentFont;
	private String path;

	@SuppressWarnings("unchecked")
	public List<HojaCrd_ensayo> listadoCRD() {
		crd = new ArrayList<HojaCrd_ensayo>();
		crd = (List<HojaCrd_ensayo>) entityManager
				.createQuery(
						"select crd from HojaCrd_ensayo crd where crd.id=:id")
				.setParameter("id", this.hoja.getId()).getResultList();
		return crd;
	}

	@SuppressWarnings("unchecked")
	public List<Seccion_ensayo> listadoSecciones() {
		secciones = new ArrayList<Seccion_ensayo>();
		secciones = (List<Seccion_ensayo>) entityManager
				.createQuery(
						"select seccion from Seccion_ensayo seccion where seccion.hojaCrd=:Hoj")
				.setParameter("Hoj", this.hoja).getResultList();
		return secciones;
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> listadoVariables(Seccion_ensayo seccion) {
		variables = new ArrayList<Variable_ensayo>();
		variables = (List<Variable_ensayo>) entityManager
				.createQuery(
						"select var from Variable_ensayo var where var.seccion=:Seccion and var.eliminado = false ORDER BY var.seccion.id ASC, var.numeroPregunta ASC")
				.setParameter("Seccion", seccion).getResultList();
		return variables;
	}

	@SuppressWarnings("unchecked")
	public List<GrupoVariables_ensayo> listadoGrupoVariables(
			Seccion_ensayo seccion) {
		grupoVariables = new ArrayList<GrupoVariables_ensayo>();
		grupoVariables = (List<GrupoVariables_ensayo>) entityManager
				.createQuery(
						"select grup from GrupoVariables_ensayo grup where grup.seccion=:Seccion and grup.eliminado = false")
				.setParameter("Seccion", seccion).getResultList();

		for (int i = 0; i < grupoVariables.size(); i++) {
			GrupoWrapper grupo = new GrupoWrapper(grupoVariables.get(i));
			listaGrupoW.add(grupo);
		}

		return grupoVariables;
	}

	public GrupoWrapper grupoWrapper(long idGrupo) {
		GrupoWrapper salida = null;

		for (GrupoWrapper gw : listaGrupoW) {
			if (gw.getGrupoVariables().getId() == idGrupo) {
				salida = gw;
				break;
			}
		}

		return salida;
	}

	// nuevo
	@SuppressWarnings("unchecked")
	public List<Seccion_ensayo> listadoSeccionesA() {
		secciones = new ArrayList<Seccion_ensayo>();
		secciones = (List<Seccion_ensayo>) entityManager
				.createQuery(
						"select seccion from Seccion_ensayo seccion where seccion.hojaCrd=:Hoj")
				.setParameter("Hoj", this.hoja).getResultList();
		return secciones;
	}

	@SuppressWarnings("unchecked")
	public List<GrupoVariables_ensayo> listadoGrupoVariablesA(
			Seccion_ensayo seccion) {
		grupoVariables = new ArrayList<GrupoVariables_ensayo>();
		grupoVariables = (List<GrupoVariables_ensayo>) entityManager
				.createQuery(
						"select grup from GrupoVariables_ensayo grup where grup.seccion=:Seccion and grup.eliminado = false")
				.setParameter("Seccion", seccion).getResultList();

		return grupoVariables;
	}

	public List<GrupoVariables_ensayo> listadoGrupoVariablesTotal() {

		List<GrupoVariables_ensayo> grupoVariablestotal = new ArrayList<GrupoVariables_ensayo>();

		for (int i = 0; i < listadoSeccionesA().size(); i++) {
			List<GrupoVariables_ensayo> grupoVariablesaux = new ArrayList<GrupoVariables_ensayo>();
			grupoVariablesaux = listadoGrupoVariablesA(listadoSeccionesA().get(
					i));
			for (int j = 0; j < grupoVariablesaux.size(); j++) {

				grupoVariablestotal.add(grupoVariablesaux.get(j));
			}
		}

		return grupoVariablestotal;
	}

	@SuppressWarnings("unchecked")
	public List<Variable_ensayo> listadoVariablesA(Seccion_ensayo seccion) {
		variables = new ArrayList<Variable_ensayo>();
		variables = (List<Variable_ensayo>) entityManager
				.createQuery(
						"select var from Variable_ensayo var where var.seccion=:Seccion and var.eliminado = false")
				.setParameter("Seccion", seccion).getResultList();
		return variables;
	}

	public List<Variable_ensayo> listadoVariablesTotal() {
		List<Variable_ensayo> variablestotal = new ArrayList<Variable_ensayo>();

		for (int i = 0; i < listadoSeccionesA().size(); i++) {
			List<Variable_ensayo> variablesaux = new ArrayList<Variable_ensayo>();
			variablesaux = listadoVariablesA(listadoSeccionesA().get(i));
			for (int j = 0; j < variablesaux.size(); j++) {

				variablestotal.add(variablesaux.get(j));
			}
		}

		return variablestotal;
	}

	public String SioNo(boolean bool) {

		String result = "No";
		if (bool) {
			result = "Si";
		}
		return result;
	}

	//

	// exel

	// @Begin(join = true, flushMode = FlushModeType.MANUAL)
	@SuppressWarnings("unchecked")
	public void main(Long idcrdbuscar) throws IOException {

		if (idcrdbuscar != null) {
			hoja = entityManager.find(HojaCrd_ensayo.class, idcrdbuscar);

			if (hoja != null) {
				listadoSecciones();
				listaVariables = new HashMap<Long, List<Variable_ensayo>>();
				listaGrupoVariables = new HashMap<Long, List<GrupoVariables_ensayo>>();
				for (Iterator iterator = secciones.iterator(); iterator
						.hasNext();) {
					Seccion_ensayo seccion_ensayo = (Seccion_ensayo) iterator
							.next();
					listadoVariables(seccion_ensayo);
					listadoGrupoVariables(seccion_ensayo);

					listaVariables.put(seccion_ensayo.getId(), variables);
					listaGrupoVariables.put(seccion_ensayo.getId(),
							grupoVariables);

				}
				inicializado = true;
			}
		}

		HSSFWorkbook libro = new HSSFWorkbook();
		// Crear paleta
		HSSFPalette palette = libro.getCustomPalette();
		
		// Color verde
		byte r = (byte) 0x66;
		byte g = (byte) 0xFF;
		byte b = (byte) 0x99;
		
		short customColorIndex=HSSFColor.LAVENDER.index;
		palette.setColorAtIndex(customColorIndex, r, g, b);
		
		
		
		// Para poner la letra del excel
		HSSFFont fuente = (HSSFFont) libro.createFont();
		fuente.setFontHeightInPoints((short) 11);
		fuente.setFontName(fuente.FONT_ARIAL);
		fuente.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);

		// Luego creamos el objeto que se encargará de aplicar el estilo a la
		// celda
		HSSFCellStyle estiloCelda = (HSSFCellStyle) libro.createCellStyle();
		estiloCelda.setWrapText(false);
		estiloCelda.setAlignment(HSSFCellStyle.ALIGN_JUSTIFY);
		estiloCelda.setVerticalAlignment(HSSFCellStyle.VERTICAL_TOP);
		// estiloCelda.setFont(fuente);

		// Creamos el estilo de celda del color ROJO
		HSSFCellStyle styleGroup3 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup3.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup3.setWrapText(true);
		styleGroup3.setFillForegroundColor(customColorIndex);

		// Creamos el estilo de celda del color AMARILLO
		HSSFCellStyle styleGroup2 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup2.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup2.setFillForegroundColor(customColorIndex);

		// Creamos el estilo de celda del color VERDE
		HSSFCellStyle styleGroup1 = (HSSFCellStyle) libro.createCellStyle();
		styleGroup1.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		styleGroup1.setFillForegroundColor(HSSFColor.GREEN.index);

		// Para crear la hoja del CRD y sus columnas
		Sheet hoja0 = libro.createSheet(SeamResourceBundle.getBundle()
				.getString("msg_crd_ens"));
		hoja0.setColumnWidth(0, 256 * 30);
		hoja0.setColumnWidth(1, 256 * 20);
		hoja0.setColumnWidth(2, 256 * 30);
		hoja0.setColumnWidth(3, 256 * 40);

		// Para crear la hoja secciones y sus columnas
		Sheet hoja1 = libro.createSheet(SeamResourceBundle.getBundle()
				.getString("msg_seccion_ens"));
		hoja1.setColumnWidth(0, 256 * 30);
		hoja1.setColumnWidth(1, 256 * 30);
		hoja1.setColumnWidth(2, 256 * 30);
		hoja1.setColumnWidth(3, 256 * 30);

		// Para crear la hoja grupo y sus columnas
		Sheet hoja2 = libro.createSheet(SeamResourceBundle.getBundle()
				.getString("msg_grupo_ens"));
		hoja2.setColumnWidth(0, 256 * 30);
		hoja2.setColumnWidth(1, 256 * 30);
		hoja2.setColumnWidth(2, 256 * 30);
		hoja2.setColumnWidth(3, 256 * 40);
		hoja2.setColumnWidth(4, 256 * 40);
		hoja2.setColumnWidth(5, 256 * 40);
		hoja2.setColumnWidth(6, 256 * 40);

		// Para crear la hoja variables y sus columnas
		Sheet hoja3 = libro.createSheet(SeamResourceBundle.getBundle()
				.getString("msg_variable_ens"));
		hoja3.setColumnWidth(0, 256 * 40);
		hoja3.setColumnWidth(1, 256 * 40);
		hoja3.setColumnWidth(2, 256 * 40);
		hoja3.setColumnWidth(3, 256 * 40);
		hoja3.setColumnWidth(4, 256 * 40);
		hoja3.setColumnWidth(5, 256 * 40);
		hoja3.setColumnWidth(6, 256 * 40);
		hoja3.setColumnWidth(7, 256 * 40);
		hoja3.setColumnWidth(8, 256 * 40);
		hoja3.setColumnWidth(9, 256 * 40);
		hoja3.setColumnWidth(10, 256 * 40);
		hoja3.setColumnWidth(11, 256 * 40);
		hoja3.setColumnWidth(12, 256 * 40);
		hoja3.setColumnWidth(13, 256 * 40);
		hoja3.setColumnWidth(14, 256 * 40);
		hoja3.setColumnWidth(15, 256 * 40);
		hoja3.setColumnWidth(16, 256 * 40);
		hoja3.setColumnWidth(17, 256 * 40);
		hoja3.setColumnWidth(18, 256 * 40); //variable unica

		// para la hoja crd

		Row filacrd = hoja0.createRow((short) 0);

		Cell celdacrd0 = filacrd.createCell((short) 0);
		celdacrd0.setCellStyle(styleGroup3);
		celdacrd0.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_crdname_ensClin"));

		Cell celdacrd1 = filacrd.createCell((short) 1);
		celdacrd1.setCellStyle(styleGroup3);
		celdacrd1.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_versioncrd_ensClin"));

		Cell celdacrd2 = filacrd.createCell((short) 2);
		celdacrd2.setCellStyle(styleGroup3);
		celdacrd2.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_descversioncrd_ensClin"));

		Cell celdacrd3 = filacrd.createCell((short) 3);
		celdacrd3.setCellStyle(styleGroup3);
		celdacrd3.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_notascrd_ensClin"));

		for (int i = 0; i < listadoCRD().size(); i++) {

			Row filacrd1 = hoja0.createRow((short) 1);

			Cell celdacrd9 = filacrd1.createCell((short) 0);
			celdacrd9.setCellValue(listadoCRD().get(i).getNombreHoja());

			if (hoja.getVersion() != null) {
				Cell celdacrd10 = filacrd1.createCell((short) 1);
				celdacrd10.setCellValue(listadoCRD().get(i).getVersion()
						.toString());
			}

			Cell celdacrd11 = filacrd1.createCell((short) 2);
			celdacrd11.setCellValue(listadoCRD().get(i).getDescripcion());

			Cell celdacrd12 = filacrd1.createCell((short) 3);
		}

		// para la hoja seccion
		Row filaseciones = hoja1.createRow((short) 0);

		Cell celdasecion = filaseciones.createCell((short) 0);
		celdasecion.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nameseccion_ensClin"));
		celdasecion.setCellStyle(styleGroup3);

		Cell celdasecion1 = filaseciones.createCell((short) 1);
		celdasecion1.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_tituloseccion_ensClin"));
		celdasecion1.setCellStyle(styleGroup3);

		Cell celdasecion2 = filaseciones.createCell((short) 2);
		celdasecion2.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_subtituloseccion_ensClin"));
		celdasecion2.setCellStyle(styleGroup3);

		Cell celdasecion3 = filaseciones.createCell((short) 3);
		celdasecion3.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_instruccioneseccion_ensClin"));
		celdasecion3.setCellStyle(styleGroup3);

		for (int x = 0; x < listadoSecciones().size(); x++) {
			Row fila5 = hoja1.createRow((short) x + 1);

			Cell celdasecion5 = fila5.createCell((short) 0);
			celdasecion5.setCellValue(listadoSecciones().get(x)
					.getEtiquetaSeccion());

			Cell celdasecion6 = fila5.createCell((short) 1);
			celdasecion6.setCellValue(listadoSecciones().get(x)
					.getTituloSeccion());

			Cell celdasecion7 = fila5.createCell((short) 2);
			celdasecion7.setCellValue(listadoSecciones().get(x).getSubtitulo());

			Cell celdasecion8 = fila5.createCell((short) 3);
			celdasecion8.setCellValue(listadoSecciones().get(x)
					.getInstrucciones());

		}

		// para la hoja grupo
		Row filagrupo = hoja2.createRow((short) 0);

		Cell celdagrupo = filagrupo.createCell((short) 0);
		celdagrupo.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_namegroup_ensClin"));
		celdagrupo.setCellStyle(styleGroup3);

		Cell celdagrupo1 = filagrupo.createCell((short) 1);
		celdagrupo1.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_descgroup_ensClin"));
		celdagrupo1.setCellStyle(styleGroup3);

		Cell celdagrupo2 = filagrupo.createCell((short) 2);
		celdagrupo2.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_headergroup_ensClin"));
		celdagrupo2.setCellStyle(styleGroup3);

		Cell celdagrupo3 = filagrupo.createCell((short) 3);
		celdagrupo3.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nameseccion_ensClin"));
		celdagrupo3.setCellStyle(styleGroup3);

		Cell celdagrupo4 = filagrupo.createCell((short) 4);
		celdagrupo4.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_repeatnumber_ensClin"));
		celdagrupo4.setCellStyle(styleGroup3);

		Cell celdagrupo5 = filagrupo.createCell((short) 5);
		celdagrupo5.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_repeatmaxnumber_ensClin"));
		celdagrupo5.setCellStyle(styleGroup3);

		Cell celdagrupo6 = filagrupo.createCell((short) 6);
		celdagrupo6.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_visibilitygroup_ensClin"));
		celdagrupo6.setCellStyle(styleGroup3);

		for (int x = 0; x < listadoGrupoVariablesTotal().size(); x++) {
			Row fila5 = hoja2.createRow((short) x + 1);

			Cell celdagrupo7 = fila5.createCell((short) 0);
			celdagrupo7.setCellValue(listadoGrupoVariablesTotal().get(x)
					.getEtiquetaGrupo());

			Cell celdagrupo8 = fila5.createCell((short) 1);
			celdagrupo8.setCellValue(listadoGrupoVariablesTotal().get(x)
					.getDescripcionGrupo());

			Cell celdagrupo9 = fila5.createCell((short) 2);
			celdagrupo9.setCellValue(listadoGrupoVariablesTotal().get(x)
					.getEncabezado());

			Cell celdagrupo10 = fila5.createCell((short) 3);
			celdagrupo10.setCellValue(listadoGrupoVariablesTotal().get(x)
					.getSeccion().getEtiquetaSeccion());

			Cell celdagrupo11 = fila5.createCell((short) 4);
			if (listadoGrupoVariablesTotal().get(x).getNumRepeticiones() != null) {
				celdagrupo11.setCellValue(listadoGrupoVariablesTotal().get(x)
						.getNumRepeticiones());
			} else {
				celdagrupo10.setCellValue("");
			}

			Cell celdagrupo12 = fila5.createCell((short) 5);
			if (listadoGrupoVariablesTotal().get(x).getNumMaxRepeticiones() != null) {
				celdagrupo12.setCellValue(listadoGrupoVariablesTotal().get(x)
						.getNumMaxRepeticiones());
			} else {
				celdagrupo12.setCellValue("");
			}

			Cell celdagrupo13 = fila5.createCell((short) 6);
			celdagrupo13.setCellValue("SHOW");

		}

		// para la hoja variable
		Row filavariable = hoja3.createRow((short) 0);

		Cell celdavariable = filavariable.createCell((short) 0);
		celdavariable.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_namevar_ensClin"));
		celdavariable.setCellStyle(styleGroup3);

		Cell celdavariable1 = filavariable.createCell((short) 1);
		celdavariable1.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_descvar_ensClin"));
		celdavariable1.setCellStyle(styleGroup3);

		Cell celdavariable2 = filavariable.createCell((short) 2);
		celdavariable2.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_txtleftvar_ensClin"));
		celdavariable2.setCellStyle(styleGroup3);

		Cell celdavariable3 = filavariable.createCell((short) 3);
		celdavariable3.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_unidadesvar_ensClin"));
		celdavariable3.setCellStyle(styleGroup3);

		Cell celdavariable4 = filavariable.createCell((short) 4);
		celdavariable4.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_txtrigthvar_ensClin"));
		celdavariable4.setCellStyle(styleGroup3);

		Cell celdavariable5 = filavariable.createCell((short) 5);
		celdavariable5.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nameseccion_ensClin"));
		celdavariable5.setCellStyle(styleGroup3);

		Cell celdavariable6 = filavariable.createCell((short) 6);
		celdavariable6.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_namegroup_ensClin"));
		celdavariable6.setCellStyle(styleGroup3);

		Cell celdavariable7 = filavariable.createCell((short) 7);
		celdavariable7.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_headervar_ensClin"));
		celdavariable7.setCellStyle(styleGroup3);

		Cell celdavariable8 = filavariable.createCell((short) 8);
		celdavariable8.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_subheadervar_ensClin"));
		celdavariable8.setCellStyle(styleGroup3);

		Cell celdavariable9 = filavariable.createCell((short) 9);
		celdavariable9.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_columnumbervar_ensClin"));
		celdavariable9.setCellStyle(styleGroup3);

		Cell celdavariable10 = filavariable.createCell((short) 10);
		celdavariable10.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_asknumbervar_ensClin"));
		celdavariable10.setCellStyle(styleGroup3);

		Cell celdavariable11 = filavariable.createCell((short) 11);
		celdavariable11.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_pfvar_ensClin"));
		celdavariable11.setCellStyle(styleGroup3);

		Cell celdavariable16 = filavariable.createCell((short) 12);
		celdavariable16.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_tdvar_ensClin"));
		celdavariable16.setCellStyle(styleGroup3);

		Cell celdavariable12 = filavariable.createCell((short) 13);
		celdavariable12.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_nameNOMvar_ensClin"));
		celdavariable12.setCellStyle(styleGroup3);

		Cell celdavariable13 = filavariable.createCell((short) 14);
		celdavariable13.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_optvar_ensClin"));
		celdavariable13.setCellStyle(styleGroup3);

		Cell celdavariable15 = filavariable.createCell((short) 15);
		celdavariable15.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_vpdvar_ensClin"));
		celdavariable15.setCellStyle(styleGroup3);

		Cell celdavariable14 = filavariable.createCell((short) 16);
		celdavariable14.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_ubicacionrespuestavar_ensClin"));
		celdavariable14.setCellStyle(styleGroup3);

		Cell celdavariable17 = filavariable.createCell((short) 17);
		celdavariable17.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_reqvar_ensClin"));
		celdavariable17.setCellStyle(styleGroup3);
		
		//variable unica
		Cell celdavariable18 = filavariable.createCell((short) 18);
		celdavariable18.setCellValue(SeamResourceBundle.getBundle().getString(
				"lbl_unicvar_ensClin"));
		celdavariable18.setCellStyle(styleGroup3);
		
		List<Variable_ensayo> variablesTotal = listadoVariablesTotal();

		for (int x = 0; x < variablesTotal.size(); x++) {
			Row filavariablea = hoja3.createRow((short) x + 1);

			Cell celdavariablea = filavariablea.createCell((short) 0);
			celdavariablea.setCellValue(variablesTotal.get(x)
					.getNombreVariable());

			Cell celdavariablea1 = filavariablea.createCell((short) 1);
			celdavariablea1.setCellValue(variablesTotal.get(x)
					.getDescripcionVariable());

			Cell celdavariablea2 = filavariablea.createCell((short) 2);
			celdavariablea2.setCellValue(variablesTotal.get(x)
					.getTextoIzquierdaVariable());

			Cell celdavariablea3 = filavariablea.createCell((short) 3);
			celdavariablea3.setCellValue(variablesTotal.get(x)
					.getUnidadesVariable());

			Cell celdavariablea4 = filavariablea.createCell((short) 4);
			celdavariablea4.setCellValue(variablesTotal.get(x)
					.getTextoDerechaVariable());

			Cell celdavariablea5 = filavariablea.createCell((short) 5);
			celdavariablea5.setCellValue(variablesTotal.get(x)
					.getSeccion().getEtiquetaSeccion());

			Cell celdavariablea6 = filavariablea.createCell((short) 6);
			if (variablesTotal.get(x).getGrupoVariables() != null) {
				celdavariablea6.setCellValue(variablesTotal.get(x)
						.getGrupoVariables().getEtiquetaGrupo());
			} else {
				celdavariablea6.setCellValue("");
			}

			Cell celdavariablea7 = filavariablea.createCell((short) 7);
			celdavariablea7.setCellValue(variablesTotal.get(x)
					.getEncabezadoVariable());

			Cell celdavariablea8 = filavariablea.createCell((short) 8);
			celdavariablea8.setCellValue(variablesTotal.get(x)
					.getSubencabezadoVariable());

			Cell celdavariablea9 = filavariablea.createCell((short) 9);
			if (variablesTotal.get(x).getNumeroColumna() != null) {
				celdavariablea9.setCellValue(variablesTotal.get(x)
						.getNumeroColumna().toString());
			} else {
				celdavariablea9.setCellValue("1");
			}

			Cell celdavariablea10 = filavariablea.createCell((short) 10);
			if (listadoVariablesTotal().get(x).getNumeroPregunta() != null) {
				celdavariablea10.setCellValue(listadoVariablesTotal().get(x)
						.getNumeroPregunta().toString());
			} else {
				celdavariablea10.setCellValue("1");
			}

			Cell celdavariablea11 = filavariablea.createCell((short) 11);
			celdavariablea11.setCellValue(listadoVariablesTotal().get(x)
					.getPresentacionFormulario().getNombre());

			Cell celdavariablea16 = filavariablea.createCell((short) 12);
			celdavariablea16.setCellValue(listadoVariablesTotal().get(x)
					.getTipoDato().getCodigo());

			Cell celdavariablea12 = filavariablea.createCell((short) 13);
			Cell celdavariablea13 = filavariablea.createCell((short) 14);
			Cell celdavariablea14 = filavariablea.createCell((short) 16);
			Cell celdavariablea15 = filavariablea.createCell((short) 15);
			Cell celdavariablea17 = filavariablea.createCell((short) 17);
			celdavariablea17.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			Cell celdavariablea18 = filavariablea.createCell((short) 18);
			celdavariablea18.setCellType(HSSFCell.CELL_TYPE_NUMERIC);
			if (listadoVariablesTotal().get(x).getNomenclador() != null) {
				celdavariablea12.setCellValue(listadoVariablesTotal().get(x)
						.getNomenclador().getNombre());

				List<NomencladorValor_ensayo> valores = (List<NomencladorValor_ensayo>) entityManager
						.createQuery(
								"select valorNOM from NomencladorValor_ensayo valorNOM "
										+ "join valorNOM.nomenclador nom "
										+ "where nom.id =:idnom ORDER BY valorNOM.valorCalculado ASC")
						.setParameter(
								"idnom",
								listadoVariablesTotal().get(x).getNomenclador()
										.getId()).getResultList();
				String a = "";
				for (int i = 0; i < valores.size() - 1; i++) {
					a = a + valores.get(i).getValor() + ",";
				}
				if(!valores.isEmpty())
				a = a + valores.get(valores.size() - 1).getValor();

				celdavariablea13.setCellValue(a);

				celdavariablea14.setCellValue(listadoVariablesTotal().get(x)
						.getUbicacionRespuesta());
				//celdavariablea14.setCellValue("VERTICAL");

				celdavariablea15.setCellValue(listadoVariablesTotal().get(x)
						.getNomenclador().getValorDefecto());
				
			
				if(listadoVariablesTotal().get(x).getRequerido() != null){
					if(listadoVariablesTotal().get(x)
							.getRequerido()){
						celdavariablea17.setCellValue(1);
					}else{
					celdavariablea17.setCellValue(0);
					}
				}else{
					celdavariablea17.setCellValue(0);
				}
				
				if(listadoVariablesTotal().get(x).getUnica() != null){
					if(listadoVariablesTotal().get(x)
							.getUnica()){
						celdavariablea18.setCellValue(1);
					}else{
					celdavariablea18.setCellValue(0);
					}
				}else{
					celdavariablea18.setCellValue(0);
				}
			} else {
				celdavariablea12.setCellValue("");
				celdavariablea13.setCellValue("");
				celdavariablea15.setCellValue("");
				// }

				
				celdavariablea14.setCellValue(listadoVariablesTotal().get(x)
						.getUbicacionRespuesta());
				//celdavariablea14.setCellValue("VERTICAL");

				
				if(listadoVariablesTotal().get(x).getRequerido() != null){
					if(listadoVariablesTotal().get(x)
							.getRequerido()){
						celdavariablea17.setCellValue(1);
					}else{
					celdavariablea17.setCellValue(0);
					}
				}else{
					celdavariablea17.setCellValue(0);
				}
				
				if(listadoVariablesTotal().get(x).getUnica() != null){
					int unica=0;
					if(listadoVariablesTotal().get(x)
							.getUnica()){
						unica=1;
					}else{
					celdavariablea18.setCellValue(unica);
					}
				}else{
					celdavariablea18.setCellValue(0);
				}

			}/*
			 * libro.write(archivo); archivo.close();
			 */
			FacesContext aFacesContext = FacesContext.getCurrentInstance();
			ServletContext context = (ServletContext) aFacesContext
					.getExternalContext().getContext();

			String rootpath = context
					.getRealPath(File.separator + "resources" + File.separator
							+ "modEnsayo" + File.separator + "crdExport"
							+ File.separator + tmpFolder + File.separator);

			SimpleDateFormat format = new SimpleDateFormat(
					"dd'_'MM'_'yyyy'_'HH'_'mm'_'ss");

			String name_crd =  stripAccents(hoja.getNombreHoja());
			String zipName = name_crd + " "
					+ format.format(new Date()) + ".xls";
				
			this.path = "/resources/modEnsayo/crdExport" + "/" + tmpFolder
					+ "/" + zipName;

			try {

				FileOutputStream archivo = new FileOutputStream(new File(
						rootpath, zipName));
				libro.write(archivo);
				archivo.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	/**
	 * Metodos para eliminar los acentos del nombre de la hoja ppara expportarla
	 */
		private static final String ORIGINAL
        	= "\u00C1\u00E1\u00C9\u00E9\u00CD\u00ED\u00D3\u00F3\u00DA\u00FA\u00D1\u00F1\u00DC\u00FC";
		private static final String REPLACEMENT
			= "AaEeIiOoUuNnUu";

		public static String stripAccents(String str) {
			if (str == null) {
				return null;
			}
			char[] array = str.toCharArray();
			for (int index = 0; index < array.length; index++) {
				int pos = ORIGINAL.indexOf(array[index]);
				if (pos > -1) {
					array[index] = REPLACEMENT.charAt(pos);
				}
			}
			return new String(array);
		}

	
	private static final String tmpFolder = "tmpFolder";

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	// set and get

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public List<Variable_ensayo> getVariables() {
		return variables;
	}

	public void setVariables(List<Variable_ensayo> variables) {
		this.variables = variables;
	}

	public List<Seccion_ensayo> getSecciones() {
		return secciones;
	}

	public void setSecciones(List<Seccion_ensayo> secciones) {
		this.secciones = secciones;
	}

	public List<GrupoVariables_ensayo> getGrupoVariables() {
		return grupoVariables;
	}

	public void setGrupoVariables(List<GrupoVariables_ensayo> grupoVariables) {
		this.grupoVariables = grupoVariables;
	}

	public List<GrupoWrapper> getListaGrupoW() {
		return listaGrupoW;
	}

	public void setListaGrupoW(List<GrupoWrapper> listaGrupoW) {
		this.listaGrupoW = listaGrupoW;
	}

	public Map<Long, List<Variable_ensayo>> getListaVariables() {
		return listaVariables;
	}

	public void setListaVariables(
			Map<Long, List<Variable_ensayo>> listaVariables) {
		this.listaVariables = listaVariables;
	}

	public Map<Long, List<GrupoVariables_ensayo>> getListaGrupoVariables() {
		return listaGrupoVariables;
	}

	public void setListaGrupoVariables(
			Map<Long, List<GrupoVariables_ensayo>> listaGrupoVariables) {
		this.listaGrupoVariables = listaGrupoVariables;
	}

	public HojaCrd_ensayo getHoja() {
		return hoja;
	}

	public void setHoja(HojaCrd_ensayo hoja) {
		this.hoja = hoja;
	}

	public Long getIdcrd() {
		return idcrd;
	}

	public void setIdcrd(Long idcrd) {
		this.idcrd = idcrd;
	}

	public Long getIdTabala() {
		return idTabala;
	}

	public void setIdTabala(Long idTabala) {
		this.idTabala = idTabala;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public HSSFFont getHeaderFont() {
		return headerFont;
	}

	public void setHeaderFont(HSSFFont headerFont) {
		this.headerFont = headerFont;
	}

	public HSSFFont getContentFont() {
		return contentFont;
	}

	public void setContentFont(HSSFFont contentFont) {
		this.contentFont = contentFont;
	}
}