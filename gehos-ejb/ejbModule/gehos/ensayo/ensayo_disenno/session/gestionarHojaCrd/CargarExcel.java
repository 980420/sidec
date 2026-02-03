package gehos.ensayo.ensayo_disenno.session.gestionarHojaCrd;

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

@Name("cargarexcel")
@Scope(ScopeType.SESSION)
public class CargarExcel {


	private static final Logger log = LoggerFactory.getLogger(CargarExcel.class);

	private FileExcel forigen;

	public FileExcel getForigen() {
		return forigen;
	}

	public void setForigen(FileExcel forigen) {
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

	public CargarExcel() {
		super();
		this.forigen = new FileExcel("");
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

		String zipName = "CRD" + format.format(new Date()) + ".xls";
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

			forigen = new FileExcel(file.getPath());
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
			Sheet crd = workbook.getSheet(0);
			Cell[] datosCRD = crd.getRow(0);
			
			Boolean a = false;
			if (datosCRD[0].getContents().trim().equals( 
					SeamResourceBundle.getBundle().getString(
							"prm_Nombre_CRD_enClin"))
							&& datosCRD[1].getContents().trim().equals(
									SeamResourceBundle.getBundle().getString(
											"prm_Version_ensClin"))
											&& datosCRD[2].getContents().trim().equals(
													SeamResourceBundle.getBundle().getString(
															"prm_Descripcion_version_ensClin"))
															&& datosCRD[3].getContents().trim().equals(
																	SeamResourceBundle.getBundle().getString(
																			"prm_Notas_revision_ensClin"))) {
				a = true;
			}

			// validar la hoja seccion
			Sheet seccion = workbook.getSheet(1);
			Cell[] datosSeccion = seccion.getRow(0);
			
			Boolean b = false;
			if (datosSeccion[0].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
							"prm_Nombre_seccion_ensClin"))
							&& datosSeccion[1].getContents().trim().equals(
									SeamResourceBundle.getBundle().getString(
											"prm_Titulo_seccion_ensClin"))
											&& datosSeccion[2].getContents().trim().equals(
													SeamResourceBundle.getBundle().getString(
															"prm_Subtitulo_ensClin"))
															&& datosSeccion[3].getContents().trim().equals(
																	SeamResourceBundle.getBundle().getString(
																			"prm_Instrucciones_ensCin"))) {
				b = true;
			}

			// validar la hoja grupo
			Sheet grupo = workbook.getSheet(2);
			Cell[] datosGrupo = grupo.getRow(0);
			Boolean c = false;
			if (datosGrupo[0].getContents().trim().equals(
				SeamResourceBundle.getBundle().getString(
						"prm_Nombre_grupo_ensClin"))
				&& datosGrupo[1].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Descripcion_grupo_ensClin"))
				&& datosGrupo[2].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Encabezado_grupo_ensClin"))
				&& datosGrupo[3].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Nombre_seccion_ensClin"))
				&& datosGrupo[4].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Numero_repeticiones_ensClin"))
				&& datosGrupo[5].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Numero_Maximo_repeticiones_ensClin"))
				&& datosGrupo[6].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Visibilidad_grupo_ensClin"))) {
				c = true;
			}

			// validar la hoja variable
			Sheet variable = workbook.getSheet(3);
			Cell[] datosVar = variable.getRow(0);
			Boolean d = false;
			if (datosVar[0].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Nombre_variable_ensClin"))
				&& datosVar[1].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Descripcion_variable_ensClin"))
				&& datosVar[2].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Texto_izquierda_variable_ensClin"))
				&& datosVar[3].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Unidades_ensClin"))
				&& datosVar[4].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Texto_derecha_variable_ensClin"))
				&& datosVar[5].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Nombre_seccion_ensClin"))
				&& datosVar[6].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Nombre_grupo_ensClin"))
				&& datosVar[7].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
					"prm_Encabezado_variable_ensClin"))
				&& datosVar[8].getContents().trim().equals(
					SeamResourceBundle.getBundle().getString(
								"prm_Subencabezado_variables_ensClin"))
				&& datosVar[9].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Numero_columna_ensClin"))
				&& datosVar[10].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Numero_pregunta_ensClin"))
				&& datosVar[11].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Presentacion_formulario_ensClin"))
				&& datosVar[12].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Tipo_dato_ensClin"))
				&& datosVar[13].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Nombre_nomenclador_ensClin"))
				&& datosVar[14]
						.getContents().trim()
						.equals(SeamResourceBundle.getBundle().getString(
								"prm_Opciones_de_valores_en_texto_ensClin"))
				&& datosVar[15].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Valor_por_defecto_ensClin"))
				&& datosVar[16].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Ubicacion_respuesta_ensClin"))
				&& datosVar[17].getContents().trim().equals(
						SeamResourceBundle.getBundle().getString(
								"prm_Requerido_ensClin"))) {
				d = true;
			}

			if (a && b && c && d)
				return true;
			else {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("addExcel",
						Severity.ERROR, SeamResourceBundle.getBundle()
						.getString("msg_noformato_ens"));
				return false;
			}

		} catch (Exception e) {

			e.printStackTrace();

			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle(
					"addExcel",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"msg_noformato_ens"));
			return false;

		}

	}

	public void Execute() throws IOException, Exception {

		if (forigen.getNombre().isEmpty()) {
			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle(
					"addExcel",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"msg_selectcrd_ens"));
			return;
		}

		// Subo el excel al servidor
		if (!subirExcel()) {
			return;
		}

		// Valido si el excel cumple las normas de la plantilla
		if (!validarExcel()) {
			facesMessages.clear();
			facesMessages.addToControlFromResourceBundle(
					"globalMessage",
					Severity.ERROR,
					SeamResourceBundle.getBundle().getString(
							"msg_noformato_ens"));
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
	@SuppressWarnings("unchecked")
	public void cargar() throws Exception, IOException {
		String hojaExcel = SeamResourceBundle.getBundle()
				.getString("msg_hojaCRD_ens");
		try {


			File subi = new File(forigen.getDir());

			WorkbookSettings cod = new WorkbookSettings();
			cod.setEncoding("Cp1252");
			Workbook workbook = Workbook.getWorkbook(subi,cod);
			List<Variable_ensayo> vars = new ArrayList<Variable_ensayo>();
			List<Seccion_ensayo> secciones = new ArrayList<Seccion_ensayo>();
			List<GrupoVariables_ensayo> grupos = new ArrayList<GrupoVariables_ensayo>();

			/**
			 * Leer datos CRD
			 */

			Sheet crd = workbook.getSheet(0);
			HojaCrd_ensayo crd_ensayo = new HojaCrd_ensayo();
			// Verifico si la hoja crd esta vacia
			if (crd.getRows() == 1) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("editar",
						Severity.ERROR,
						SeamResourceBundle.getBundle()
						.getString("msg_crdvacio_ens"));
				return;
			}
			/*
			 * Victor: No creo necesario esta condición, porque al final el crd no 
			 * va a tener mas nombre y si se no se utiliza esa información
			 */
			else if(crd.getRows() > 2) {
				facesMessages.clear();
				facesMessages.addToControlFromResourceBundle("editar",
						Severity.ERROR,
						SeamResourceBundle.getBundle()
						.getString("msg_crdDatosDeMas_ens"));
			}
			else {
				Cell[] datosCRD = crd.getRow(1);
				//la asignación de fila y columna es para en caso de error saber donde se ocurrió el problema
				filaActual=2;
				
				if(CampoVacio(datosCRD[0])){
					MsgCampoObligatorioVacio(hojaExcel, 0, 2);
					return;
				}else{
					crd_ensayo.setNombreHoja(datosCRD[0].getContents().trim());
				}
				
				
				// verifico si dentro del estudio activo ya existe una hija con el
				// mismo nombre
				Estudio_ensayo est = seguridadEstudio.getEstudioEntidadActivo()
						.getEstudio();

				Long idEstudio = est.getId();
				String nombreEstudioEntradoExcel = datosCRD[0].getContents().trim();

				List<HojaCrd_ensayo> siCRD = (List<HojaCrd_ensayo>) entityManager
						.createQuery(
								"select crd from HojaCrd_ensayo crd where crd.nombreHoja =:name and crd.estudio.id =:idest and crd.eliminado <> true")
								.setParameter("name", nombreEstudioEntradoExcel).setParameter("idest", idEstudio)
								.getResultList();
				
				if (siCRD.isEmpty()) {
					if (datosCRD.length >= 2)
						if (datosCRD[1].getContents().trim() != "")
							crd_ensayo.setVersion(Integer.parseInt(datosCRD[1]
									.getContents()));
					if (datosCRD.length >= 3)
						crd_ensayo.setDescripcion(datosCRD[2].getContents().trim());

					crd_ensayo.setEstudio(seguridadEstudio.EstudioActivo());
					Usuario_ensayo u = entityManager.find(Usuario_ensayo.class,
							user.getId());
					crd_ensayo.setUsuario(u);
					crd_ensayo.setEliminado(false);
					crd_ensayo.setFechaCreacion(new Date());
					// poner la bitacora

				} else {
					facesMessages.clear();
					facesMessages.addToControlFromResourceBundle(
							"editar",
							Severity.ERROR,
							SeamResourceBundle.getBundle().getString(
									"msg_namecrd_ens")
									+ datosCRD[0].getContents().trim()
									+ " "
									+ SeamResourceBundle.getBundle().getString(
											"msg_yacrd_ens"));
					return;
				}

				/**
				 * leer datos de la hoja seccion
				 */
				Sheet seccion = workbook.getSheet(1);	
				//la asignación de fila y columna es para en caso de error saber donde se ocurrió el problema
				filaActual=0;	
				
				if (seccion.getRows() == 1) {
					facesMessages.clear();
					facesMessages.addToControlFromResourceBundle("editar",
							Severity.ERROR, SeamResourceBundle.getBundle()
							.getString("msg_seccionvacio_ens"));
					return;
				} else {
					// obtener los datos de cada seccion y guardarlas en una lista
					hojaExcel = SeamResourceBundle.getBundle()
							.getString("msg_hojaSeccion_ens");
					for (int i = 1; i < seccion.getRows(); i++) {
						//la asignación de fila y columna es para en caso de error saber donde se ocurrió el problema
						filaActual=i;
						
						Cell[] datosSeccion = seccion.getRow(i);
						
						if(!FilaVacia(datosSeccion)){
							
							if(datosSeccion.length<2){
								MsgCampoObligatorioVacioFila(hojaExcel, i+1);
								return;
							}
							else{
								Seccion_ensayo seccion_ensayo = new Seccion_ensayo();
								
								if(CampoVacio(datosSeccion[0])){
									MsgCampoObligatorioVacio(hojaExcel, 0, i+1);
									return;
								}
								else{
									seccion_ensayo.setEtiquetaSeccion(datosSeccion[0]
											.getContents().trim());
								}
								if(CampoVacio(datosSeccion[1])){
									MsgCampoObligatorioVacio(hojaExcel, 1, i+1);
									return;
								}
								else{
									seccion_ensayo.setTituloSeccion(datosSeccion[1]
											.getContents().trim());
								}
								if (datosSeccion.length >= 3)
									if (datosSeccion[2].getContents().trim() != "")
										seccion_ensayo.setSubtitulo(datosSeccion[2]
												.getContents().trim());
								
								if (datosSeccion.length >= 4)
									if (datosSeccion[3].getContents().trim() != "")
										seccion_ensayo.setInstrucciones(datosSeccion[3]
												.getContents().trim());
								seccion_ensayo.setEliminado(false);
								// poner bitacora
								secciones.add(seccion_ensayo);
							}
							
						}
						
					}
					/*
					
					}*/
					
					/**
					 * Leer hoja grupos
					 */
					Sheet grupo = workbook.getSheet(2);
					//la asignación de fila y columna es para en caso de error saber donde se ocurrió el problema
					filaActual=0;

					/*
					 *Victor: Condición por GUSTO hacer un if(grupo.getRows() != 1))
					*/
					if (grupo.getRows() != 1) {
						hojaExcel = SeamResourceBundle.getBundle()
								.getString("msg_hojaGrupo_ens");
						for (int i = 1; i < grupo.getRows(); i++) {
							filaActual=i;
							//try{
							
							Cell[] datosgrupo = grupo.getRow(i);
							
							if(!FilaVacia(datosgrupo)){
								if(datosgrupo.length<4){
									MsgCampoObligatorioVacioFila(hojaExcel, i+1);
									return;
								}
								else{
									GrupoVariables_ensayo grupoVariables_ensayo = new GrupoVariables_ensayo();
									
									if(CampoVacio(datosgrupo[0])){
										MsgCampoObligatorioVacio(hojaExcel, 0, i+1);
										return;
									}else{
										grupoVariables_ensayo.setEtiquetaGrupo(datosgrupo[0]
												.getContents().trim());
									}								
									
									if (datosgrupo[1].getContents().trim() != "")
										grupoVariables_ensayo.setDescripcionGrupo(datosgrupo[1]
												.getContents().trim());
									
									if(CampoVacio(datosgrupo[2])){
										MsgCampoObligatorioVacio(hojaExcel, 2, i+1);
										return;
									}else{
										grupoVariables_ensayo.setEncabezado(datosgrupo[2].getContents().trim());
									}																
									
									if(CampoVacio(datosgrupo[3])){
										MsgCampoObligatorioVacio(hojaExcel, 3, i+1);
										return;
									}else{
										String secc = datosgrupo[3].getContents().trim();
										boolean flag = false;
										for (int j = 0; j < secciones.size(); j++) {

											if (secciones.get(j).getEtiquetaSeccion()
													.equals(secc)) {
												grupoVariables_ensayo.setSeccion(secciones
														.get(j));
												flag = true;
												break;
											}

										}
										if (!flag) {
											facesMessages.clear();
											facesMessages
											.addToControlFromResourceBundle(
													"editar",
													Severity.ERROR,
													SeamResourceBundle
													.getBundle()
													.getString(
															"prm_seccion_ensClin")
															+ secc +" "
															+ SeamResourceBundle
															.getBundle()
															.getString(
																	"prm_delgrupo_ensClin")
																	+ " "
																	+ grupoVariables_ensayo
																	.getEtiquetaGrupo()
																	+ " "
																	+ SeamResourceBundle
																	.getBundle()
																	.getString(
																			"prm_nosecc_enClin")
																			+ "(fila: "+(i+1)+")");
											return;
										}
									}
									
									
									if (datosgrupo.length >= 5) {
										if (datosgrupo[4].getContents().trim() == "")
											grupoVariables_ensayo.setNumRepeticiones(1);
										else
											grupoVariables_ensayo
											.setNumRepeticiones(Integer
													.parseInt(datosgrupo[4]
															.getContents().trim()));
									} else
										grupoVariables_ensayo.setNumRepeticiones(1);
									if (datosgrupo.length >= 6) {
										if (datosgrupo[5].getContents() == "")
											grupoVariables_ensayo.setNumMaxRepeticiones(500);
										else
											grupoVariables_ensayo
											.setNumMaxRepeticiones(Integer
													.parseInt(datosgrupo[5]
															.getContents().trim()));
									} else {
										grupoVariables_ensayo.setNumMaxRepeticiones(500);
									}

									// ver aqui lo de la visibilidad que en la bd no esta
									// poner aqui la bitacora
									grupoVariables_ensayo.setEliminado(false);
									grupos.add(grupoVariables_ensayo);
								}								
							}
						
						}// end for del grupo
						/*
						// buscar grupo duplicados
						for (int i = 0; i < grupos.size(); i++) {
							for (int j = i + 1; j < grupos.size(); j++) {
								if (grupos.get(i).getEtiquetaGrupo()
										.equals(grupos.get(j).getEtiquetaGrupo())) {
									facesMessages.clear();
									facesMessages
									.addToControlFromResourceBundle(
											"editar",
											Severity.ERROR,
											SeamResourceBundle
											.getBundle()
											.getString(
													"msg_namegrupo_ens")
													+ " "
													+ grupos.get(i)
													.getEtiquetaGrupo()
													+ " "
													+ SeamResourceBundle
													.getBundle()
													.getString(
															"msg_yagrupo_ens"));
									return;
								}
							}
						}*/
						
					}// end else del grupo

					/**
					 * Leer hoja variables
					 */

					Sheet var = workbook.getSheet(3);
					if (var.getRows() == 1) {
						facesMessages.clear();
						facesMessages.addToControlFromResourceBundle("editar",
								Severity.ERROR, SeamResourceBundle.getBundle()
								.getString("msg_varvacia_ens"));
						return;
					} else {
						hojaExcel = SeamResourceBundle.getBundle()
								.getString("msg_hojaVariable_ens");
						//la asignación de fila y columna es para en caso de error saber donde se ocurrió el problema
						filaActual=0;
						// Variable_ensayo variable_ensayo = new Variable_ensayo();
						for (int i = 1; i < var.getRows(); i++) {	
							filaActual=i;
							Cell[] datosvar = var.getRow(i);
							
							if(!FilaVacia(datosvar)){
								if(datosvar.length<13){
									MsgCampoObligatorioVacioFila(hojaExcel, i+1);
								}else{
									Variable_ensayo variable_ensayo = new Variable_ensayo();
									
									if(CampoVacio(datosvar[0])){
										MsgCampoObligatorioVacio(hojaExcel, 0, i+1);
										return;
									}
									else{
										variable_ensayo.setNombreVariable(datosvar[0]
												.getContents());
									}
									
									if(CampoVacio(datosvar[1])){
										MsgCampoObligatorioVacio(hojaExcel, 1, i+1);
										return;
									}else{
										variable_ensayo.setDescripcionVariable(datosvar[1]
												.getContents().trim());
									}
									
									
									//if (datosvar.length >= 3)
									if (datosvar[2].getContents().trim() != "")
										variable_ensayo
												.setTextoIzquierdaVariable(datosvar[2]
														.getContents().trim());
									//if (datosvar.length >= 4)
									if (datosvar[3].getContents().trim() != "")
											variable_ensayo
													.setUnidadesVariable(datosvar[3]
															.getContents().trim());
									//if (datosvar.length >= 5)
								    if (datosvar[4].getContents().trim() != "")
											variable_ensayo
											.setTextoDerechaVariable(datosvar[4]
													.getContents().trim());
										
											
									// poner la seccion en la variable
									//if (datosvar.length >= 6) {
								    if(CampoVacio(datosvar[5])){
								    	MsgCampoObligatorioVacio(hojaExcel, 5, i+1);
										return;
								    }else{
										String secc = datosvar[5].getContents().trim();
										boolean flag = false;
										for (int x = 0; x < secciones.size(); x++) {

											if (secciones.get(x).getEtiquetaSeccion()
													.equals(secc)) {
												variable_ensayo.setSeccion(secciones
														.get(x));
												flag = true;
												break;
											}

										}
										if (!flag) {
											facesMessages.clear();
											facesMessages
													.addToControlFromResourceBundle(
															"editar",
															Severity.ERROR,
															SeamResourceBundle
																	.getBundle()
																	.getString(
																			"prm_seccion_ensClin")
																	+ " "
																	+ secc
																	+ " "
																	+ SeamResourceBundle
																			.getBundle()
																			.getString(
																					"prm_dlvar_enClin")
																	+ " "
																	+ variable_ensayo
																			.getNombreVariable()
																	+ " "
																	+ SeamResourceBundle
																			.getBundle()
																			.getString(
																					"prm_nosecc_enClin")
																					+ "(fila: "+(i+1)+")");
											return;
										}
									}
									// poner el grupo en la variable
									//if (datosvar.length >= 7) {
									if (!datosvar[6].getContents().trim().isEmpty()) {
										String group = datosvar[6].getContents().trim();

										boolean flag = false;
										for (int j = 0; j < grupos.size(); j++) {

											group = StringUtils.stripBack(group,
													" ");
											group = StringUtils.stripFront(group,
													" ");

											String grupoaux = grupos.get(j)
													.getEtiquetaGrupo().toString();
											grupoaux = StringUtils.stripBack(
													grupoaux, " ");
											grupoaux = StringUtils.stripFront(
													grupoaux, " ");

											if (grupoaux.equals(group)) {
												variable_ensayo
														.setGrupoVariables(grupos
																.get(j));
												flag = true;
												break;
											}

										}

										if (!flag) {
											facesMessages.clear();
											facesMessages
													.addToControlFromResourceBundle(
															"editar",
															Severity.ERROR,
															SeamResourceBundle
																	.getBundle()
																	.getString(
																			"prm_grupo_ensClin")
																	+ " "
																	+ group
																	+ " "
																	+ SeamResourceBundle
																			.getBundle()
																			.getString(
																					"prm_dlvar_enClin")
																	+ " "
																	+ variable_ensayo
																			.getNombreVariable()
																	+ " "
																	+ SeamResourceBundle
																			.getBundle()
																			.getString(
																					"prm_nogrupo_enClin"));
												return;
											}
										}

									//if (datosvar.length >= 8)
									if (datosvar[7].getContents().trim() != "")
										variable_ensayo
												.setEncabezadoVariable(datosvar[7]
														.getContents().trim());
								//if (datosvar.length >= 9)
									if (datosvar[8].getContents().trim() != "")
										variable_ensayo
												.setSubencabezadoVariable(datosvar[8]
														.getContents().trim());

									if (datosvar[9].getContents().trim() != "")
										variable_ensayo.setNumeroColumna(Long
												.parseLong(datosvar[9]
														.getContents().trim()));
									else
											variable_ensayo.setNumeroColumna(1L);
									
									if (datosvar[10].getContents().trim() != "")
										variable_ensayo.setNumeroPregunta(Float
												.parseFloat(datosvar[10]
														.getContents().trim()));
									else
										variable_ensayo.setNumeroColumna(1L);
									
									/*AQUI*/
									if(CampoVacio(datosvar[12])){
										MsgCampoObligatorioVacio(hojaExcel, 12, i+1);
										return;
									}
									else{											
										TipoDato_ensayo td = (TipoDato_ensayo) entityManager
												.createQuery(
														"select pf from TipoDato_ensayo pf where pf.codigo =:name")
												.setParameter("name",
														datosvar[12].getContents().trim())
												.getSingleResult();

										variable_ensayo.setTipoDato(td);
										
										if(CampoVacio(datosvar[11])){
											MsgCampoObligatorioVacio(hojaExcel, 11, i+1);
											return;
										}else{			
											try{												
											
												PresentacionFormulario_ensayo pf = (PresentacionFormulario_ensayo) entityManager
														.createQuery(
																"select pf from PresentacionFormulario_ensayo pf where pf.nombre =:name and pf.tipoDato.id =:td")
														.setParameter(
																"name",
																datosvar[11]
																		.getContents().trim()
																)
														.setParameter("td",
																td.getId())
														.getSingleResult();
												
												variable_ensayo
												.setPresentacionFormulario(pf);
											}
											catch(Exception ex){
												facesMessages.clear();
												facesMessages
														.addToControlFromResourceBundle(
																"editar",
																Severity.ERROR,SeamResourceBundle
																.getBundle()
																.getString(
																		"msg_probIncorcond")
																+ " "+hojaExcel+ " ("+ 
																SeamResourceBundle
																.getBundle()
																.getString(
																		"msg_cell")					
																+(char)(11+65)
																+(i+1)
																+")");
												return;
											}										
											
										}
										
										if (td.getCodigo().equals("NOM")) {
											if (datosvar.length >= 14) {
												if (datosvar[13].getContents().trim() != "") {
													String nameNOM = datosvar[13]
															.getContents().trim();													
													Nomenclador_ensayo nom = new Nomenclador_ensayo();
													nom.setNombre(nameNOM);
													nom.setEliminado(false);
													if (datosvar.length >= 16) {
														if (datosvar[15]
																.getContents().trim() != "")
															nom.setValorDefecto(datosvar[15]
																	.getContents().trim());
													}
													// poner la bitacora
													entityManager.persist(nom);

													String ovt = datosvar[14]
															.getContents();
													String[] separados = ovt
															.split(",");

													for (int b = 0; b < separados.length; b++) {
														NomencladorValor_ensayo nomval = new NomencladorValor_ensayo();

														nomval.setEliminado(false);
														nomval.setNomenclador(nom);
														nomval.setValor(separados[b]);
														nomval.setValorCalculado(b
																);
														entityManager
														.persist(nomval);
													}


													variable_ensayo.setNomenclador(nom);
												}else{
													MsgCampoObligatorioVacio(hojaExcel, 13, i+1);
													return;
												}												
											}
										}
									}
									
									if (datosvar.length >= 17) {
										if (datosvar[16].getContents().trim() != "")
											variable_ensayo
													.setUbicacionRespuesta(datosvar[16]
															.getContents().trim().toUpperCase());
										else
											variable_ensayo
													.setUbicacionRespuesta("VERTICAL");
									} else
										variable_ensayo
												.setUbicacionRespuesta("VERTICAL");

									if (datosvar.length >= 18) {
										String aux = datosvar[17].getContents().trim();
										if (aux.equals("1"))
											variable_ensayo.setRequerido(true);
										else
											variable_ensayo.setRequerido(false);
									} else
										variable_ensayo.setRequerido(false);
									
									if (datosvar.length >= 19) {
										String aux = datosvar[18].getContents().trim();
										if (aux.equals("1"))
											variable_ensayo.setUnica(true);
										else
											variable_ensayo.setUnica(false);
									} else
										variable_ensayo.setUnica(false);

									variable_ensayo.setEliminado(false);
									vars.add(variable_ensayo);
								}								
							}
							// poner bitacora
						}// end for de variables
/*
						for (int i = 0; i < vars.size(); i++) {
							for (int j = i + 1; j < vars.size(); j++) {
								if (vars.get(i).getNombreVariable()
										.equals(vars.get(j).getNombreVariable())) {
									facesMessages.clear();
									facesMessages
											.addToControlFromResourceBundle(
													"editar",
													Severity.ERROR,
													SeamResourceBundle
															.getBundle()
															.getString(
																	"msg_namevar_ens")
															+ " "
															+ vars.get(i)
																	.getNombreVariable()
															+ " "
															+ SeamResourceBundle
																	.getBundle()
																	.getString(
																			"msg_yavar_ens"));
									return;
								}
							}
						}*/
						
					}// end else de variable
				}// end else de la seccion
			}// end else del crd
			entityManager.persist(crd_ensayo);
			for (Iterator iterator = secciones.iterator(); iterator.hasNext();) {
				Seccion_ensayo seccion_ensayo = (Seccion_ensayo) iterator.next();
				seccion_ensayo.setHojaCrd(crd_ensayo);
				entityManager.persist(seccion_ensayo);

			}

			for (Iterator iterator = grupos.iterator(); iterator.hasNext();) {
				GrupoVariables_ensayo grupoVariables_ensayo = (GrupoVariables_ensayo) iterator
						.next();
				entityManager.persist(grupoVariables_ensayo);
			}
			for (Iterator iterator = vars.iterator(); iterator.hasNext();) {
				Variable_ensayo variable_ensayo2 = (Variable_ensayo) iterator
						.next();
				entityManager.persist(variable_ensayo2);
			}
			entityManager.flush();
			} catch (Exception e) {
				facesMessages.clear();
				facesMessages
						.addToControlFromResourceBundle(
								"editar",
								Severity.ERROR,SeamResourceBundle
								.getBundle()
								.getString(
										"msg_problmGnrl")
								+ " "+hojaExcel+ 
								SeamResourceBundle
								.getBundle()
								.getString(
										"msg_fila")						
								+filaActual
								+")");
				return;
			}
		}// end del metodo
	
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
							
							
								
