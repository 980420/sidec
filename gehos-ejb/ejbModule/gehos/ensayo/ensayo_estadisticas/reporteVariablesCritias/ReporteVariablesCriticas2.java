package gehos.ensayo.ensayo_estadisticas.reporteVariablesCritias;

import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.reportes.session.FileType;
import gehos.comun.reportes.session.ReportManager;
import gehos.comun.shell.IActiveModule;
import gehos.ensayo.ensayo_conduccion.gestionarNewCronograma.SujetoGeneralNewCronograma;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_estadisticas.entity.Ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoNota_ensayo;
import gehos.ensayo.entity.EstudioEntidad_ensayo;
import gehos.ensayo.entity.Estudio_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Nota_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

@SuppressWarnings("unchecked")
@Name("reporteVariablesCriticas2")
@Scope(ScopeType.CONVERSATION)
public class ReporteVariablesCriticas2{
	
	private static final Object Estudio_ensayo = null;
	private Date fechaInicio;
	private Date fechaFin;
	private DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

	@In(create = true, value = "reportManager")
	ReportManager reportManager;

	protected @In EntityManager entityManager;
	protected @In(create = true) FacesMessages facesMessages;
	protected @In IBitacora bitacora;
	
	@In private Usuario user;
	
	List<VariablesCriticasConstruc> VariablesCC;
	List<String> listaSujetos = new ArrayList<String>(); //lista para almacenar los sujetos 
	
	@In(scope = ScopeType.SESSION)
	SeguridadEstudio seguridadEstudio;
	@In
	private IActiveModule activeModule;
	private Boolean flag = false;
	private Boolean haySujetos = false;
	private Integer style;
	private Map reporteVariablesCriticas2;
	private String nombreReport;
	private String pathExportedReport = "";
	private Boolean flag2 = true;
	private String noResult = SeamResourceBundle.getBundle().getString(
			"noResult1");
	
	private List<Estudio_ensayo> listaEstudioEntidad;
	private List<Entidad_ensayo> listaEntidadEst;
	private List<EstudioEntidad_ensayo> listaEntidadEstSeleccionado;
	private List<MomentoSeguimientoGeneral_ensayo> listaMomentosDSSeleccionado;
	private List<GrupoSujetos_ensayo> listaGrupoEstSeleccionado;
	private List<String> listarEstudios;
	private List<String> listarEntidades;
	private List<String> listarMomentosDSSeleccionado = new ArrayList<String>();
	private List<String> listarGruposEstSeleccionado = new ArrayList<String>();
	private String estudio;
	private String msGeneral = "";
	private String crdSpe = "";
	private String entidad;
	private String variable = "";
	private String grupo;
	private Object msG;
	private String estadoN = "";
	
	
	private String fileformatToExport;
	private List<String> filesFormatCombo;
	private List<String> nombreVariable = new ArrayList<String>();
	private final String seleccione = SeamResourceBundle.getBundle().getString(
			"cbxSeleccionPorDefecto");
	

	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void cargarEstudios() {
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
		listaEstudioEntidad = new ArrayList<Estudio_ensayo>();
		listarEstudios = new ArrayList<String>();
		listaEstudioEntidad = (List<Estudio_ensayo>) entityManager
				.createQuery("select distinct estudioEnt.estudioEntidad.estudio from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.estudio.eliminado = false and estudioEnt.estudioEntidad.estudio.estadoEstudio = 3 and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId())
				.getResultList();
		for (int i = 0; i < listaEstudioEntidad.size(); i++) {
				listarEstudios.add(listaEstudioEntidad.get(i).getNombre());	
		}
	}
	
	public void cargarEntidades(){
		Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class, user.getId());
		listaEntidadEst = new ArrayList<Entidad_ensayo>();
		listarEntidades = new ArrayList<String>();
		listaEntidadEst = (List<Entidad_ensayo>) entityManager.createQuery("select distinct estudioEnt.estudioEntidad.entidad from UsuarioEstudio_ensayo estudioEnt where estudioEnt.estudioEntidad.entidad.eliminado = false and estudioEnt.usuario.id=:idUser")
				.setParameter("idUser", usuario.getId())
				.getResultList();
		for (int i = 0; i < listaEntidadEst.size(); i++) {			
			listarEntidades.add(listaEntidadEst.get(i).getNombre());						
		}		
	}
	
/*	public void cargarMomentos(){
		listarMomentosDSSeleccionado.clear(); 
		listaMomentosDSSeleccionado = (List<MomentoSeguimientoGeneral_ensayo>)entityManager
				.createQuery(
						"select nomb from MomentoSeguimientoGeneral_ensayo nomb where nomb.eliminado = FALSE and  nomb.momentoSeguimientoGeneral.programado = TRUE")
				.setParameter("nomb",this.msG).getResultList();
		listarMomentosDSSeleccionado.add("<Seleccione>");
		for (int j = 0; j < listaMomentosDSSeleccionado.size(); j++) {
			listarMomentosDSSeleccionado.add(listaMomentosDSSeleccionado.get(j).getEstadoMomentoSeguimientoGeneral().getNombre());			
		}
		cargarGruposEstudio();
		this.msGeneral = "";
		this.crdSpe = "";
		this.variable = "";
	}*/
	
	public void cargarGruposEstudio(){
		listarGruposEstSeleccionado.clear();
		listaGrupoEstSeleccionado = (List<GrupoSujetos_ensayo>) entityManager
				.createQuery(
						"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.nombre=:nomEst and grupo.nombreGrupo <> 'Grupo Validación'")
				.setParameter("nomEst",this.estudio).getResultList();
		listarGruposEstSeleccionado.add("<Seleccione>");
		for (int i = 0; i < listaGrupoEstSeleccionado.size(); i++) {
			listarGruposEstSeleccionado.add(listaGrupoEstSeleccionado.get(i).getNombreGrupo());		
		}
	}
	
	
	
	
	public Entidad_ensayo getHospitalActivo() {
		Entidad_ensayo entidadEnsayo = entityManager.find(Entidad_ensayo.class,
				this.activeModule.getActiveModule().getEntidad().getId());

		return entidadEnsayo;
	}
	
	/*public int sizeMaxString(List<String> listReturn){
        if(listReturn.size() == 0)
            return 150;
        int max = listReturn.get(0).length();
        for(int i = 1; i < listReturn.size(); i++){
            if(listReturn.get(i).length() > max)
                max = listReturn.get(i).length();
        }
        if ((max*6) > 150)
            return max*6;
        return 150;
    }*/
	public int sizeMaxString(List<String> listReturn){
	    // Validar si la lista es null
	    if(listReturn == null || listReturn.size() == 0)
	        return 150;
	    
	    int max = listReturn.get(0).length();
	    for(int i = 1; i < listReturn.size(); i++){
	        // También validar elementos null dentro de la lista
	        if(listReturn.get(i) != null && listReturn.get(i).length() > max)
	            max = listReturn.get(i).length();
	    }
	    
	    if ((max*6) > 150)
	        return max*6;
	    return 150;
	}
	
	
	/*// Listado de los msGenerales por los que se puede buscar
		private List<String> momentGene = new ArrayList<String>();

		// llenar msGeneral que se puede buscar
		public List<String> llenarMsGene() {
			this.variable="";
			nombreVariable.clear();
			momentGene = entityManager
					.createQuery(
							"select distinct ms.momentoSeguimientoGeneral.nombre from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.grupoSujetos.estudio.id =:idEst order by ms.momentoSeguimientoGeneral.nombre ")
					.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId()).getResultList();
			momentGene.add(0, seleccione);
			return momentGene;
		}

		@SuppressWarnings("rawtypes")
		public List getMomentGene() {
			return llenarMsGene();
		}
		
		
		public List<String> llenarMsGene() {
		    this.variable="";
		    nombreVariable.clear();
		    
		    // Inicializar momentGene como lista vacía
		    List<String> momentGene = new ArrayList<String>();
		    
		    // Validar que seguridadEstudio y getEstudioEntidadActivo() no sean null
		    if(seguridadEstudio != null && seguridadEstudio.getEstudioEntidadActivo() != null) {
		        try {
		            momentGene = entityManager
		                    .createQuery(
		                            "select distinct ms.momentoSeguimientoGeneral.nombre from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.grupoSujetos.estudio.id =:idEst order by ms.momentoSeguimientoGeneral.nombre ")
		                    .setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
		                    .getResultList();
		        } catch (Exception e) {
		            // En caso de error, mantener lista vacía
		            System.out.println("Error en llenarMsGene: " + e.getMessage());
		        }
		    }
		    
		    momentGene.add(0, seleccione);
		    return momentGene;
		}
	
	
			public List<String> llenarMsGene() {
				this.variable = "";
				if (nombreVariable != null) {
					nombreVariable.clear();
				}
		
				List<String> momentGene = new ArrayList<String>();
				String seleccioneVal = "";
		
				try {
					// Validar seleccione no sea null
					seleccioneVal = SeamResourceBundle.getBundle().getString(
							"cbxSeleccionPorDefecto");
					if (seleccioneVal == null) {
						seleccioneVal = "<Seleccione>";
					}
				} catch (Exception e) {
					seleccioneVal = "<Seleccione>";
				}
		
				// Validar todas las dependencias
				if (entityManager == null) {
					System.out.println("Error: entityManager es null");
					momentGene.add(0, seleccioneVal);
					return momentGene;
				}
		
				if (seguridadEstudio == null) {
					System.out.println("Error: seguridadEstudio es null");
					momentGene.add(0, seleccioneVal);
					return momentGene;
				}
		
				if (seguridadEstudio.getEstudioEntidadActivo() == null) {
					System.out.println("Error: estudioEntidadActivo es null");
					momentGene.add(0, seleccioneVal);
					return momentGene;
				}
		
				if (seguridadEstudio.getEstudioEntidadActivo().getEstudio() == null) {
					System.out.println("Error: estudio es null");
					momentGene.add(0, seleccioneVal);
					return momentGene;
				}
		
				try {
					Long estudioId = seguridadEstudio.getEstudioEntidadActivo()
							.getEstudio().getId();
					if (estudioId == null) {
						System.out.println("Error: estudioId es null");
						momentGene.add(0, seleccioneVal);
						return momentGene;
					}
		
					momentGene = entityManager
							.createQuery(
									"select distinct ms.momentoSeguimientoGeneral.nombre from MomentoSeguimientoEspecifico_ensayo ms where ms.estudio.id = :idEst order by ms.momentoSeguimientoGeneral.nombre")
							.setParameter("idEst", estudioId).getResultList();
		
					// Validar que la consulta no retorne null
					if (momentGene == null) {
						momentGene = new ArrayList<String>();
					}
		
				} catch (Exception e) {
					System.out.println("Error en llenarMsGene: " + e.getMessage());
					e.printStackTrace(); // Esto dará más detalles del error
					momentGene = new ArrayList<String>();
				}
		
				momentGene.add(0, seleccioneVal);
				return momentGene;
			}
	
	
	
	
	
		
		// Lista de crd especifico
		public void crdEspecifico(){
			llenarMsGeneEspecifico();
			crdMomento.clear();
			for(int x = 0; x < momentGeneEspecifico.size();x++){
				Long momento = null;
				try {
					momento = momentGeneEspecifico.get(x);;
				} catch (Exception e) {
					momento = null;
				}		
						
				if( momento != null ){
				     crdMomento.addAll(entityManager
								.createQuery(
										"select crd.id from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id =:idMomento and crd.hojaCrd.estudio.id =:idEst and crd.eliminado = false")
								.setParameter("idMomento", momento)
								.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())	
								.getResultList());
				     }
				}
		}
		
		// lo que hace falta
		private List<Long> momentGeneEspecifico = new ArrayList<Long>();
		private List<String> crdEspecificosM = new ArrayList<String>();
		private List<Long> crdMomento = new ArrayList<Long>();
		private List<String> crdHoja = new ArrayList<String>();
		// Lista de momento seguimiento especifico
		public void llenarMsGeneEspecifico(){
		momentGeneEspecifico = new ArrayList<Long>();
		momentGeneEspecifico.clear();
		if(msGeneral != null && !msGeneral.isEmpty() ){
			momentGeneEspecifico = entityManager
					.createQuery(
							"select distinct ms.id from MomentoSeguimientoEspecifico_ensayo ms where ms.sujeto.grupoSujetos.estudio.id =:idEst and ms.momentoSeguimientoGeneral.nombre =:MomentoSeguimiento")
					.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
					.setParameter("MomentoSeguimiento", msGeneral)
					.getResultList();
		}
		
             
	}

		// Lista hoja crd
		public void hojacrdName(){
			crdEspecifico();
			crdHoja.clear();
			this.variable="";
			if(crdMomento.size() >0)
				crdHoja.addAll(
						entityManager
						.createQuery(
									"select distinct hoja.nombreHoja from HojaCrd_ensayo hoja WHERE hoja.id IN (select crd.hojaCrd.id from CrdEspecifico_ensayo crd where crd.hojaCrd.estudio.id = :idEst"
									+ " and hoja.eliminado = false and crd.id in (:idcrd))")
							.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
							.setParameter("idcrd", crdMomento)
							.getResultList());
			crdHoja.add(0, seleccione);
		}
		
		public List<String> getCrdHoja() {
			hojacrdName();
			return crdHoja;
		}
		private Long hojaId;
		private List<Long> idCrdespecifico = new ArrayList<Long>();
		private List<Long> idVariable = new ArrayList<Long>();
		
		public void idHjaName(){
//			crdEspecifico();
				hojaId = (Long) entityManager
						.createQuery(
								"select distinct hoja.id from HojaCrd_ensayo hoja WHERE hoja.nombreHoja=:nombreHoja and hoja.estudio.id =:idEst and hoja.eliminado = false ")
				        .setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
				        .setParameter("nombreHoja", crdSpe)
				        .getSingleResult();
		}
		
		public void idCrdHoja(){
			idHjaName();
			idCrdespecifico.clear();
			this.crdSpe="";
			idCrdespecifico = entityManager
					.createQuery(
							"select distinct crd.id from CrdEspecifico_ensayo crd WHERE crd.hojaCrd.id=:idhoja and crd.hojaCrd.estudio.id =:idEst and crd.eliminado = false ")
							.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
							.setParameter("idhoja",hojaId )
							.getResultList();
		}
		
		public List<Long> idVariabled(){
			idCrdHoja();
			idVariable.clear();
			idVariable.addAll(entityManager
					.createQuery(
							"select distinct vd.variable.id from VariableDato_ensayo vd Where vd.crdEspecifico.id in (:IdCrdEsp) and vd.crdEspecifico.hojaCrd.estudio.id =:idEst ")
							.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
							.setParameter("IdCrdEsp", idCrdespecifico )
							.getResultList());
//			for(int x = 0; x < idCrdespecifico.size();x++){
//				Long crdespecifico = idCrdespecifico.get(x);
//				if( crdespecifico != null )
//					idVariable.addAll(entityManager
//							.createQuery(
//									"select vd.variable.id from VariableDato_ensayo vd Where vd.crdEspecifico.id=:IdCrdEsp and vd.crdEspecifico.hojaCrd.estudio.id =:idEst ")
//									.setParameter("idEst", seguridadEstudio.getEstudioEntidadActivo().getEstudio().getId())
//									.setParameter("IdCrdEsp",crdespecifico )
//									.getResultList());
//			}
			return idVariable;
		}
		
		public List<String> idVariable(){
			nombreVariable.clear();
			if(crdSpe.equals("<Seleccione>")){
				crdSpe = "";
				}
			else if(crdSpe != null && !crdSpe.isEmpty()){
				idVariabled();
				nombreVariable.addAll(entityManager
						.createQuery(
								"select distinct v.nombreVariable from Variable_ensayo v WHERE v.id in (:idVar)")
								.setParameter("idVar", idVariable )
								.getResultList());
			}
			nombreVariable.add(0, seleccione);
			return nombreVariable;
		}*/
		
	// Método auxiliar para obtener el ID del estudio desde el nombre
	@SuppressWarnings("unused")
	private Long obtenerIdEstudioDesdeNombre(String nombreEstudio) {
	    if (nombreEstudio == null || nombreEstudio.isEmpty() || nombreEstudio.equals(seleccione)) {
	        return null;
	    }
	    
	    try {
	        return (Long) entityManager
	            .createQuery(
	                "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombre AND e.eliminado = false")
	            .setParameter("nombre", nombreEstudio)
	            .getSingleResult();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    }
	}
	
	
	// Listado de los msGenerales por los que se puede buscar
			private List<String> momentGene = new ArrayList<String>();
	// llenar msGeneral que se puede buscar
	public List<String> llenarMsGene() {
	    this.variable = "";
	    nombreVariable.clear();
	    
	    // Verificar si hay un estudio seleccionado
	    if (estudio == null || estudio.isEmpty() || estudio.equals(seleccione)) {
	        momentGene.clear();
	        momentGene.add(0, seleccione);
	        return momentGene;
	    }
	    
	    try {
	        // Obtener el ID del estudio desde el nombre
	        Long idEstudio = (Long) entityManager
	            .createQuery(
	                "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	            .setParameter("nombreEst", estudio)
	            .getSingleResult();
	        
	        if (idEstudio != null) {
	            momentGene = entityManager
	                .createQuery(
	                    "SELECT DISTINCT ms.momentoSeguimientoGeneral.nombre " +
	                    "FROM MomentoSeguimientoEspecifico_ensayo ms " +
	                    "WHERE ms.sujeto.grupoSujetos.estudio.id = :idEst " +
	                    "ORDER BY ms.momentoSeguimientoGeneral.nombre")
	                .setParameter("idEst", idEstudio)  // ← Cambiado de seguridadEstudio a estudio
	                .getResultList();
	        } else {
	            momentGene.clear();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        momentGene.clear();
	    }
	    
	    momentGene.add(0, seleccione);
	    return momentGene;
	}
	
	
	
	////2
	private List<Long> momentGeneEspecifico = new ArrayList<Long>();
	// Lista de momento seguimiento especifico
	public void llenarMsGeneEspecifico() {
	    momentGeneEspecifico = new ArrayList<Long>();
	    momentGeneEspecifico.clear();
	    
	    if (msGeneral != null && !msGeneral.isEmpty() && estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null) {
	                momentGeneEspecifico = entityManager
	                    .createQuery(
	                        "SELECT DISTINCT ms.id " +
	                        "FROM MomentoSeguimientoEspecifico_ensayo ms " +
	                        "WHERE ms.sujeto.grupoSujetos.estudio.id = :idEst " +
	                        "AND ms.momentoSeguimientoGeneral.nombre = :MomentoSeguimiento")
	                    .setParameter("idEst", idEstudio)  // ← Cambiado
	                    .setParameter("MomentoSeguimiento", msGeneral)
	                    .getResultList();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	
	
	//3
	private List<String> crdEspecificosM = new ArrayList<String>();
	private List<Long> crdMomento = new ArrayList<Long>();
	private List<String> crdHoja = new ArrayList<String>();
	private Long hojaId;
	private List<Long> idCrdespecifico = new ArrayList<Long>();
	private List<Long> idVariable = new ArrayList<Long>();
	
	// Lista de crd especifico
	public void crdEspecifico() {
	    llenarMsGeneEspecifico();
	    crdMomento.clear();
	    
	    if (estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null && momentGeneEspecifico != null && !momentGeneEspecifico.isEmpty()) {
	                for (int x = 0; x < momentGeneEspecifico.size(); x++) {
	                    Long momento = momentGeneEspecifico.get(x);
	                    if (momento != null) {
	                        crdMomento.addAll(entityManager
	                            .createQuery(
	                                "SELECT crd.id " +
	                                "FROM CrdEspecifico_ensayo crd " +
	                                "WHERE crd.momentoSeguimientoEspecifico.id = :idMomento " +
	                                "AND crd.hojaCrd.estudio.id = :idEst " +
	                                "AND crd.eliminado = false")
	                            .setParameter("idMomento", momento)
	                            .setParameter("idEst", idEstudio)  // ← Cambiado
	                            .getResultList());
	                    }
	                }
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	//4
	// Lista hoja crd
	public void hojacrdName() {
	    crdEspecifico();
	    crdHoja.clear();
	    this.variable = "";
	    
	    if (crdMomento.size() > 0 && estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null) {
	                crdHoja.addAll(
	                    entityManager
	                    .createQuery(
	                        "SELECT DISTINCT hoja.nombreHoja " +
	                        "FROM HojaCrd_ensayo hoja " +
	                        "WHERE hoja.id IN (" +
	                        "    SELECT crd.hojaCrd.id " +
	                        "    FROM CrdEspecifico_ensayo crd " +
	                        "    WHERE crd.hojaCrd.estudio.id = :idEst " +
	                        "    AND hoja.eliminado = false " +
	                        "    AND crd.id IN (:idcrd)" +
	                        ")")
	                    .setParameter("idEst", idEstudio)  // ← Cambiado
	                    .setParameter("idcrd", crdMomento)
	                    .getResultList());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    crdHoja.add(0, seleccione);
	}
	public List<String> getCrdHoja() {
		hojacrdName();
		return crdHoja;
	}
	
	//5
	public void idHjaName() {
	    if (crdSpe != null && !crdSpe.isEmpty() && !crdSpe.equals(seleccione) && 
	        estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null) {
	                hojaId = (Long) entityManager
	                    .createQuery(
	                        "SELECT DISTINCT hoja.id " +
	                        "FROM HojaCrd_ensayo hoja " +
	                        "WHERE hoja.nombreHoja = :nombreHoja " +
	                        "AND hoja.estudio.id = :idEst " +
	                        "AND hoja.eliminado = false")
	                    .setParameter("idEst", idEstudio)  // ← Cambiado
	                    .setParameter("nombreHoja", crdSpe)
	                    .getSingleResult();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	            hojaId = null;
	        }
	    }
	}
	//6
	public void idCrdHoja() {
	    idHjaName();
	    idCrdespecifico.clear();
	    this.crdSpe = "";
	    
	    if (hojaId != null && estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null) {
	                idCrdespecifico = entityManager
	                    .createQuery(
	                        "SELECT DISTINCT crd.id " +
	                        "FROM CrdEspecifico_ensayo crd " +
	                        "WHERE crd.hojaCrd.id = :idhoja " +
	                        "AND crd.hojaCrd.estudio.id = :idEst " +
	                        "AND crd.eliminado = false")
	                    .setParameter("idEst", idEstudio)  // ← Cambiado
	                    .setParameter("idhoja", hojaId)
	                    .getResultList();
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}
	//7
	public List<Long> idVariabled() {
	    idCrdHoja();
	    idVariable.clear();
	    
	    if (idCrdespecifico != null && !idCrdespecifico.isEmpty() && 
	        estudio != null && !estudio.isEmpty() && !estudio.equals(seleccione)) {
	        try {
	            // Obtener ID del estudio desde el nombre
	            Long idEstudio = (Long) entityManager
	                .createQuery(
	                    "SELECT e.id FROM Estudio_ensayo e WHERE e.nombre = :nombreEst AND e.eliminado = false")
	                .setParameter("nombreEst", estudio)
	                .getSingleResult();
	            
	            if (idEstudio != null) {
	                idVariable.addAll(entityManager
	                    .createQuery(
	                        "SELECT DISTINCT vd.variable.id " +
	                        "FROM VariableDato_ensayo vd " +
	                        "WHERE vd.crdEspecifico.id IN (:IdCrdEsp) " +
	                        "AND vd.crdEspecifico.hojaCrd.estudio.id = :idEst")
	                    .setParameter("idEst", idEstudio)  // ← Cambiado
	                    .setParameter("IdCrdEsp", idCrdespecifico)
	                    .getResultList());
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	    return idVariable;
	}
	
	public List<String> idVariable(){
		nombreVariable.clear();
		if(crdSpe.equals("<Seleccione>")){
			crdSpe = "";
			}
		else if(crdSpe != null && !crdSpe.isEmpty()){
			idVariabled();
			nombreVariable.addAll(entityManager
					.createQuery(
							"select distinct v.nombreVariable from Variable_ensayo v WHERE v.id in (:idVar)")
							.setParameter("idVar", idVariable )
							.getResultList());
		}
		nombreVariable.add(0, seleccione);
		return nombreVariable;
	}
	
	@SuppressWarnings("rawtypes")
	public List getVariablesNombre() {
		return idVariable();
	}
	
	
	
	
	
	
	
	
	

	

	//RF3 - Generar reporte  
	/*public void listadoVariablesCC()	{
		
		VariablesCC = new ArrayList<VariablesCriticasConstruc>();
		style = -1;
		Integer cantSujetos=0;
		Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
		
		for (int j = 0; j < listaEstudioEntidad.size(); j++) {
			if (this.estudio != null && this.estudio.equals(listaEstudioEntidad.get(j).getNombre())){
				estudioEnsayo = listaEstudioEntidad.get(j);
				break;
			}
		}
		
		haySujetos=false;
		
		if(fechaFin != null && fechaInicio != null && !this.estudio.equals("")){
			listaSujetos.clear();
			List<EstudioEntidad_ensayo> listaEntidadEst;
			if(this.entidad.equals("")  || this.entidad.equals("<Seleccione>")){				
				listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
						.createQuery(
								"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false")
						.setParameter("idEst",estudioEnsayo.getId()).getResultList();				
			}
			else{
				listaEntidadEst = (List<EstudioEntidad_ensayo>)entityManager
						.createQuery(
								"select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.estudio.eliminado = false and estudioEnt.entidad.nombre=:entidadNom and estudioEnt.entidad.eliminado = false")
						.setParameter("idEst",estudioEnsayo.getId())
						.setParameter("entidadNom", this.entidad).getResultList();
			}
				
			for (int z = 0; z < listaEntidadEst.size(); z++){
				List<GrupoSujetos_ensayo> grupos;
				if ("".equals(this.grupo) || "<Seleccione>".equals(this.grupo)){
					grupos = (List<GrupoSujetos_ensayo>) entityManager
							.createQuery(
									"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId()).getResultList();
				}
				else{
					grupos = (List<GrupoSujetos_ensayo>) entityManager
							.createQuery(
									"select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo=:grupoNom")
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())
							.setParameter("grupoNom", this.grupo).getResultList();
				}
				for (int i = 0; i < grupos.size(); i++) {
					List<Sujeto_ensayo> sujetosDelGrupo = entityManager
							.createQuery(
									"select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false")	// and sujeto.fechaCreacion>=:fechaInicio and sujeto.fechaCreacion<=:fechaFin			
							.setParameter("idEstudio",listaEntidadEst.get(z).getEstudio().getId())	
							.setParameter("idEntidad",listaEntidadEst.get(z).getEntidad().getId())
							.setParameter("idGrupo",grupos.get(i).getId()).getResultList();
					
					cantSujetos+=sujetosDelGrupo.size();
					
					if(!sujetosDelGrupo.isEmpty()) {
						for(int k=0;k<sujetosDelGrupo.size();k++) {
							Sujeto_ensayo sujeto=sujetosDelGrupo.get(k);
							List<Nota_ensayo> notasSujeto;
							if(estadoN.equals("") || estadoN.equals("<Seleccione>")){
								notasSujeto = entityManager
										.createQuery(
												"select nota from Nota_ensayo nota where nota.eliminado = false and nota.notaPadre = null and nota.notaSitio = false and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id=:idSujeto and nota.fechaCreacion >=:fechaInicio and nota.fechaCreacion <=:fechaFin")									
										.setParameter("idSujeto",sujeto.getId())
										.setParameter("fechaInicio",fechaInicio)
										.setParameter("fechaFin",fechaFin).getResultList();
							}
							else{
								notasSujeto = entityManager
										.createQuery(
												"select nota from Nota_ensayo nota where nota.eliminado = false and nota.notaPadre = null and nota.notaSitio = false and nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id=:idSujeto and nota.fechaCreacion >=:fechaInicio and nota.fechaCreacion <=:fechaFin and nota.estadoNota.nombre=:estadoNota")									
										.setParameter("idSujeto",sujeto.getId())
										.setParameter("fechaInicio",fechaInicio)
										.setParameter("fechaFin",fechaFin)
										.setParameter("estadoNota", this.estadoN).getResultList();
							}
								
							for(int m=0; m < notasSujeto.size(); m++) {
								Nota_ensayo nota=notasSujeto.get(m);
								String msE="";
								String nombreCRD="";
								String nombreVariable="";
								String valorVariable="";							
												
								haySujetos = true;
																
								if(nota.getVariableDato() != null)
									valorVariable=nota.getVariableDato().getValor();
								else {
									//Obtener el valor de la variable (desde la tabla nota en ocaciones el valor id_variable_dato viene null)
									VariableDato_ensayo variables = (VariableDato_ensayo) entityManager
											.createQuery(
													"select variable from VariableDato_ensayo variable where variable.crdEspecifico.id =:idCrd and variable.variable.id =:idVariable and (variable.eliminado = false or variable.eliminado is null)")
											.setParameter("idCrd", nota.getCrdEspecifico().getId())
											.setParameter("idVariable", nota.getVariable().getId()).getSingleResult();
									
									valorVariable=variables.getValor();									
								}									
								
								listaSujetos.add(sujeto.getCodigoPaciente()); //adicionar el codigo del sujeto
								VariablesCriticasConstruc variableSuj = new VariablesCriticasConstruc(sujeto.getCodigoPaciente(), msE, nombreCRD, nombreVariable, valorVariable,  this.style.toString());
								VariablesCC.add(variableSuj);
							}								
						}							 
					}					
				}
			} 
						
			if (haySujetos) {
				this.style = 1;
				Set<String> distintSet = new HashSet<String>(listaSujetos); //Valores distindos dentro de la lista
				int totalSujetos = distintSet.size(); // total de valores distintos
				reporteVariablesCriticas2=new HashMap();
				reporteVariablesCriticas2.put("nombreEstudio", this.estudio);
				reporteVariablesCriticas2.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
				reporteVariablesCriticas2.put("msE", SeamResourceBundle.getBundle().getString("msE"));
				reporteVariablesCriticas2.put("nombreCRD", SeamResourceBundle.getBundle().getString("nombreCRD"));
				reporteVariablesCriticas2.put("nombreVariable", SeamResourceBundle.getBundle().getString("nombreVariable"));
				reporteVariablesCriticas2.put("valorVariable", SeamResourceBundle.getBundle().getString("valorVariable"));		
				nombreReport=reportManager.ExportReport("reportVariablesCriticas", reporteVariablesCriticas2, VariablesCC, FileType.HTML_FILE);
				flag=true;
				flag2=false;
			} 
			else {
				flag = false;
				flag2 = true;
			}			
		}
		else {
			flag = false;
			flag2 = true;
		}	
		
		this.estudio="";
		this.entidad = "";
		this.grupo = "";
		this.fechaInicio=null;
		this.fechaFin=null;
	}*/
	
	
	public void listadoVariablesCC() {
	    VariablesCC = new ArrayList<VariablesCriticasConstruc>();
	    style = -1;
	    Estudio_ensayo estudioEnsayo = new Estudio_ensayo();
	    
	    // Buscar el estudio seleccionado
	    for (int j = 0; j < listaEstudioEntidad.size(); j++) {
	        if (listaEstudioEntidad.get(j).getNombre().equals(this.estudio)) {
	            estudioEnsayo = listaEstudioEntidad.get(j);
	            break;
	        }
	    }
	    
	    haySujetos = false;
	    
	    // Verificar que se tengan todos los datos necesarios
	    if (fechaFin != null && fechaInicio != null && this.estudio != null && !this.estudio.isEmpty() && !this.estudio.equals(seleccione)) {
	        
	        // Inicializar VariablesCC
	        if (VariablesCC == null) {
	            VariablesCC = new ArrayList<VariablesCriticasConstruc>();
	        } else {
	            VariablesCC.clear();
	        }
	        
	        // Obtener la lista de entidades del estudio
	        List<EstudioEntidad_ensayo> listaEntidadEst;
	        if ("".equals(this.entidad) || "<Seleccione>".equals(this.entidad)) {                
	            listaEntidadEst = (List<EstudioEntidad_ensayo>) entityManager
	                    .createQuery(
	                            "select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false")
	                    .setParameter("idEst", estudioEnsayo.getId()).getResultList();                
	        } else {
	            listaEntidadEst = (List<EstudioEntidad_ensayo>) entityManager
	                    .createQuery(
	                            "select estudioEnt from EstudioEntidad_ensayo estudioEnt where estudioEnt.estudio.id=:idEst and estudioEnt.eliminado = false and estudioEnt.entidad.nombre=:entidadNom and estudioEnt.entidad.eliminado = false")
	                    .setParameter("idEst", estudioEnsayo.getId())
	                    .setParameter("entidadNom", this.entidad).getResultList();
	        }
	        
	        // Recorrer cada entidad
	        for (int z = 0; z < listaEntidadEst.size(); z++) {
	            
	            // Obtener grupos según filtro
	            List<GrupoSujetos_ensayo> grupos;
	            if ("".equals(this.grupo) || "<Seleccione>".equals(this.grupo)) {
	                grupos = (List<GrupoSujetos_ensayo>) entityManager
	                        .createQuery(
	                                "select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo <> 'Grupo Validación'")
	                        .setParameter("idEstudio", listaEntidadEst.get(z).getEstudio().getId()).getResultList();
	            } else {
	                grupos = (List<GrupoSujetos_ensayo>) entityManager
	                        .createQuery(
	                                "select grupo from GrupoSujetos_ensayo grupo where grupo.eliminado = FALSE and grupo.estudio.id=:idEstudio and grupo.nombreGrupo=:grupoNom")
	                        .setParameter("idEstudio", listaEntidadEst.get(z).getEstudio().getId())
	                        .setParameter("grupoNom", this.grupo).getResultList();
	            }
	            
	            // Recorrer cada grupo
	            for (int i = 0; i < grupos.size(); i++) {
	                // Obtener sujetos del grupo
	                List<Sujeto_ensayo> sujetosDelGrupo = entityManager
	                        .createQuery(
	                                "select sujeto from Sujeto_ensayo sujeto where sujeto.grupoSujetos.id =:idGrupo and sujeto.grupoSujetos.estudio.id =:idEstudio and sujeto.entidad.id =:idEntidad and sujeto.eliminado = false")
	                        .setParameter("idEstudio", listaEntidadEst.get(z).getEstudio().getId())    
	                        .setParameter("idEntidad", listaEntidadEst.get(z).getEntidad().getId())
	                        .setParameter("idGrupo", grupos.get(i).getId()).getResultList();
	                
	                // Si hay sujetos en el grupo
	                if (!sujetosDelGrupo.isEmpty()) {
	                    for (int k = 0; k < sujetosDelGrupo.size(); k++) {
	                        Sujeto_ensayo sujeto = sujetosDelGrupo.get(k);
	                        
	                        // Obtener las NOTAS del sujeto en el período que tienen variables
	                        List<Nota_ensayo> notasSujeto;
	                        
	                        if ("".equals(estadoN) || "<Seleccione>".equals(estadoN)) {
	                            notasSujeto = entityManager
	                                    .createQuery(
	                                            "select nota from Nota_ensayo nota " +
	                                            "where nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = :idSujeto " +
	                                            "and nota.fechaCreacion >= :fechaInicio " +
	                                            "and nota.fechaCreacion <= :fechaFin " +
	                                            "and nota.eliminado = false " +
	                                            "and nota.notaPadre = null " +
	                                            "and nota.notaSitio = false " +
	                                            "and nota.variable is not null") // Solo notas que tengan variable asociada
	                                    .setParameter("idSujeto", sujeto.getId())
	                                    .setParameter("fechaInicio", fechaInicio)
	                                    .setParameter("fechaFin", fechaFin)
	                                    .getResultList();
	                        } else {
	                            notasSujeto = entityManager
	                                    .createQuery(
	                                            "select nota from Nota_ensayo nota " +
	                                            "where nota.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = :idSujeto " +
	                                            "and nota.fechaCreacion >= :fechaInicio " +
	                                            "and nota.fechaCreacion <= :fechaFin " +
	                                            "and nota.eliminado = false " +
	                                            "and nota.notaPadre = null " +
	                                            "and nota.notaSitio = false " +
	                                            "and nota.variable is not null " +
	                                            "and nota.estadoNota.nombre = :estadoNota")
	                                    .setParameter("idSujeto", sujeto.getId())
	                                    .setParameter("fechaInicio", fechaInicio)
	                                    .setParameter("fechaFin", fechaFin)
	                                    .setParameter("estadoNota", this.estadoN)
	                                    .getResultList();
	                        }
	                        
	                        // Procesar cada nota del sujeto
	                        for (int m = 0; m < notasSujeto.size(); m++) {
	                            Nota_ensayo nota = notasSujeto.get(m);
	                            
	                            // Obtener los datos necesarios
	                            String msE = "";
	                            String nombreCRD = "";
	                            String nombreVariable = "";
	                            String valorVariable = "";
	                            
	                            // Obtener msE (nombre del momento seguimiento)
	                            if (nota.getCrdEspecifico() != null && 
	                                nota.getCrdEspecifico().getMomentoSeguimientoEspecifico() != null &&
	                                nota.getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral() != null) {
	                                msE = nota.getCrdEspecifico().getMomentoSeguimientoEspecifico()
	                                        .getMomentoSeguimientoGeneral().getNombre();
	                            }
	                            
	                            // Obtener nombreCRD (nombre de la hoja CRD)
	                            if (nota.getCrdEspecifico() != null && 
	                                nota.getCrdEspecifico().getHojaCrd() != null) {
	                                nombreCRD = nota.getCrdEspecifico().getHojaCrd().getNombreHoja();
	                            }
	                            
	                            // Obtener nombreVariable
	                            if (nota.getVariable() != null) {
	                                nombreVariable = nota.getVariable().getNombreVariable();
	                            }
	                            
	                            // Obtener valorVariable
	                            if (nota.getVariableDato() != null) {
	                                valorVariable = nota.getVariableDato().getValor();
	                            } else {
	                                // Buscar el valor en VariableDato_ensayo si no está en la nota
	                                try {
	                                    // Primero intentamos buscar por crdEspecifico y variable
	                                    if (nota.getCrdEspecifico() != null && nota.getVariable() != null) {
	                                        VariableDato_ensayo variableDato = (VariableDato_ensayo) entityManager
	                                                .createQuery(
	                                                        "select vd from VariableDato_ensayo vd " +
	                                                        "where vd.crdEspecifico.id = :idCrd " +
	                                                        "and vd.variable.id = :idVariable " +
	                                                        "and (vd.eliminado = false or vd.eliminado is null)")
	                                                .setParameter("idCrd", nota.getCrdEspecifico().getId())
	                                                .setParameter("idVariable", nota.getVariable().getId())
	                                                .getSingleResult();
	                                        
	                                        if (variableDato != null) {
	                                            valorVariable = variableDato.getValor();
	                                        }
	                                    }
	                                } catch (Exception e) {
	                                    // Si no se encuentra en VariableDato_ensayo, usar la descripción de la nota
	                                    if (nota.getDescripcion() != null && !nota.getDescripcion().isEmpty()) {
	                                        valorVariable = nota.getDescripcion();
	                                    } else if (nota.getDetallesNota() != null && !nota.getDetallesNota().isEmpty()) {
	                                        valorVariable = nota.getDetallesNota();
	                                    }
	                                }
	                            }
	                            
	                            // Solo agregar si hay datos válidos
	                            if (nombreVariable != null && !nombreVariable.isEmpty() && 
	                                valorVariable != null && !valorVariable.isEmpty()) {
	                                haySujetos = true;
	                                
	                                // Crear el objeto VariablesCriticasConstruc con los datos obtenidos
	                                VariablesCriticasConstruc variableSuj = new VariablesCriticasConstruc(
	                                    sujeto.getCodigoPaciente(), // nombre sujeto
	                                    msE,                       // msE
	                                    nombreCRD,                 // nombreCRD
	                                    nombreVariable,            // nombreVariable
	                                    valorVariable,             // valorVariable
	                                    this.style.toString()      // style
	                                );
	                                
	                                VariablesCC.add(variableSuj);
	                            }
	                        }
	                        
	                        // También buscar directamente en VariableDato_ensayo para capturar
	                        // variables que no tengan notas asociadas
	                        List<VariableDato_ensayo> variablesDirectas;
	                        
	                        if ("".equals(estadoN) || "<Seleccione>".equals(estadoN)) {
	                            variablesDirectas = entityManager
	                                    .createQuery(
	                                            "select vd from VariableDato_ensayo vd " +
	                                            "where vd.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = :idSujeto " +
	                                            "and vd.fechaCreacion >= :fechaInicio " +
	                                            "and vd.fechaCreacion <= :fechaFin " +
	                                            "and (vd.eliminado = false or vd.eliminado is null) " +
	                                            "and vd.valor is not null " +
	                                            "and vd.valor <> ''")
	                                    .setParameter("idSujeto", sujeto.getId())
	                                    .setParameter("fechaInicio", fechaInicio)
	                                    .setParameter("fechaFin", fechaFin)
	                                    .getResultList();
	                        } else {
	                            // Para filtrar por estado, necesitamos unir con Nota_ensayo
	                            variablesDirectas = entityManager
	                                    .createQuery(
	                                            "select vd from VariableDato_ensayo vd " +
	                                            "left join Nota_ensayo nota on nota.variableDato.id = vd.id " +
	                                            "where vd.crdEspecifico.momentoSeguimientoEspecifico.sujeto.id = :idSujeto " +
	                                            "and vd.fechaCreacion >= :fechaInicio " +
	                                            "and vd.fechaCreacion <= :fechaFin " +
	                                            "and (vd.eliminado = false or vd.eliminado is null) " +
	                                            "and vd.valor is not null " +
	                                            "and vd.valor <> '' " +
	                                            "and (nota is null or nota.estadoNota.nombre = :estadoNota)")
	                                    .setParameter("idSujeto", sujeto.getId())
	                                    .setParameter("fechaInicio", fechaInicio)
	                                    .setParameter("fechaFin", fechaFin)
	                                    .setParameter("estadoNota", this.estadoN)
	                                    .getResultList();
	                        }
	                        
	                        // Procesar variables directas
	                        for (int m = 0; m < variablesDirectas.size(); m++) {
	                            VariableDato_ensayo variableDato = variablesDirectas.get(m);
	                            
	                            String msE = "";
	                            String nombreCRD = "";
	                            String nombreVariable = "";
	                            String valorVariable = variableDato.getValor();
	                            
	                            // Obtener msE (nombre del momento seguimiento)
	                            if (variableDato.getCrdEspecifico() != null && 
	                                variableDato.getCrdEspecifico().getMomentoSeguimientoEspecifico() != null &&
	                                variableDato.getCrdEspecifico().getMomentoSeguimientoEspecifico().getMomentoSeguimientoGeneral() != null) {
	                                msE = variableDato.getCrdEspecifico().getMomentoSeguimientoEspecifico()
	                                        .getMomentoSeguimientoGeneral().getNombre();
	                            }
	                            
	                            // Obtener nombreCRD (nombre de la hoja CRD)
	                            if (variableDato.getCrdEspecifico() != null && 
	                                variableDato.getCrdEspecifico().getHojaCrd() != null) {
	                                nombreCRD = variableDato.getCrdEspecifico().getHojaCrd().getNombreHoja();
	                            }
	                            
	                            // Obtener nombreVariable
	                            if (variableDato.getVariable() != null) {
	                                nombreVariable = variableDato.getVariable().getNombreVariable();
	                            }
	                            
	                            // Solo agregar si hay datos válidos y si no existe ya
	                            if (nombreVariable != null && !nombreVariable.isEmpty() && 
	                                valorVariable != null && !valorVariable.isEmpty()) {
	                                
	                                // Verificar si ya existe esta combinación
	                                boolean existe = false;
	                                for (VariablesCriticasConstruc vcc : VariablesCC) {
	                                    if (vcc.getNombreSujeto().equals(sujeto.getCodigoPaciente()) &&
	                                        vcc.getNombreVariable().equals(nombreVariable) &&
	                                        vcc.getValorVariable().equals(valorVariable)) {
	                                        existe = true;
	                                        break;
	                                    }
	                                }
	                                
	                                if (!existe) {
	                                    haySujetos = true;
	                                    
	                                    VariablesCriticasConstruc variableSuj = new VariablesCriticasConstruc(
	                                        sujeto.getCodigoPaciente(), // nombre sujeto
	                                        msE,                       // msE
	                                        nombreCRD,                 // nombreCRD
	                                        nombreVariable,            // nombreVariable
	                                        valorVariable,             // valorVariable
	                                        this.style.toString()      // style
	                                    );
	                                    
	                                    VariablesCC.add(variableSuj);
	                                }
	                            }
	                        }
	                    }
	                }
	            }
	        }
	        
	        // Si se encontraron datos, generar el reporte
	        if (haySujetos) {
	            this.style = 1;
	            
	            // Crear mapa con los parámetros para el reporte
	            reporteVariablesCriticas2 = new HashMap();
	            reporteVariablesCriticas2.put("nombreEstudio", this.estudio);
	            reporteVariablesCriticas2.put("nombreSujeto", SeamResourceBundle.getBundle().getString("nombreSujeto"));
	            reporteVariablesCriticas2.put("msE", SeamResourceBundle.getBundle().getString("msE"));
	            reporteVariablesCriticas2.put("nombreCRD", SeamResourceBundle.getBundle().getString("nombreCRD"));
	            reporteVariablesCriticas2.put("nombreVariable", SeamResourceBundle.getBundle().getString("nombreVariable"));
	            reporteVariablesCriticas2.put("valorVariable", SeamResourceBundle.getBundle().getString("valorVariable"));        
	            
	            // Generar el reporte con VariablesCC que ahora contiene los datos correctos
	            nombreReport = reportManager.ExportReport("reportVariablesCriticas", reporteVariablesCriticas2, VariablesCC, FileType.HTML_FILE);
	            flag = true;
	            flag2 = false;
	        } else {
	            flag = false;
	            flag2 = true;
	        }
	    } else {
	        flag = false;
	        flag2 = true;
	    }
	    
	    // Limpiar los campos
	    this.estudio = "";
	    this.entidad = "";
	    this.grupo = "";
	    this.fechaInicio = null;
	    this.fechaFin = null;
	    this.estadoN = "";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void exportReportToFileFormat() {
		pathExportedReport = "";
		if (fileformatToExport.equals(filesFormatCombo.get(0))) {
			pathExportedReport = reportManager.ExportReport("reportVariablesCriticas", reporteVariablesCriticas2, VariablesCC, FileType.PDF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(1))) {
			pathExportedReport = reportManager.ExportReport("reportVariablesCriticas", reporteVariablesCriticas2, VariablesCC, FileType.RTF_FILE);
		} else if (fileformatToExport.equals(filesFormatCombo.get(2))) {
			pathExportedReport = reportManager.ExportReport("reportVariablesCriticas", reporteVariablesCriticas2, VariablesCC, FileType.EXCEL_FILE);
		}
	}

	public String getNombreReport() {
		return nombreReport;
	}

	public void setNombreReport(String nombreReport) {
		this.nombreReport = nombreReport;
	}

	public Boolean getFlag() {
		return flag;
	}

	public void setFlag(Boolean flag) {
		this.flag = flag;
	}

	public String getPathExportedReport() {
		return pathExportedReport;
	}

	public void setPathExportedReport(String pathExportedReport) {
		this.pathExportedReport = pathExportedReport;
	}

	public String getFileformatToExport() {
		return fileformatToExport;
	}

	public void setFileformatToExport(String fileformatToExport) {
		this.fileformatToExport = fileformatToExport;
	}

	public List<String> getFilesFormatCombo() {
		filesFormatCombo = reportManager.fileFormatsToExport();
		return filesFormatCombo;
	}

	public void setFilesFormatCombo(List<String> filesFormatCombo) {
		this.filesFormatCombo = filesFormatCombo;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}

	public Boolean getFlag2() {
		return flag2;
	}

	public void setFlag2(Boolean flag2) {
		this.flag2 = flag2;
	}

	public String getNoResult() {
		return noResult;
	}

	public void setNoResult(String noResult) {
		this.noResult = noResult;
	}

	public List<String> getListarEstudios() {
		return listarEstudios;
	}

	public void setListarEstudios(List<String> listarEstudios) {
		this.listarEstudios = listarEstudios;
	}
	
	public List<String> getListarEntidades() {
		return listarEntidades;
	}

	public void setListarEntidades(List<String> listarEntidades) {
		this.listarEntidades = listarEntidades;
	}
	
	

	public String getEntidad() {
		return entidad;
	}

	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}

	public String getEstudio() {
		return estudio;
	}

	public void setEstudio(String estudio) {
		this.estudio = estudio;
	}
		
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}
	
	public List<String> getListarGruposEstSeleccionado() {
		return listarGruposEstSeleccionado;
	}

	public void setListarGruposEstSeleccionado(
			List<String> listarGruposEstSeleccionado) {
		this.listarGruposEstSeleccionado = listarGruposEstSeleccionado;
	}

	
	public SeguridadEstudio getSeguridadEstudio() {
		return seguridadEstudio;
	}

	public void setSeguridadEstudio(SeguridadEstudio seguridadEstudio) {
		this.seguridadEstudio = seguridadEstudio;
	}

	public List<Estudio_ensayo> getListaEstudioEntidad() {
		return listaEstudioEntidad;
	}

	public void setListaEstudioEntidad(List<Estudio_ensayo> listaEstudioEntidad) {
		this.listaEstudioEntidad = listaEstudioEntidad;
	}

	public Integer getStyle() {
		return style;
	}

	public void setStyle(Integer style) {
		this.style = style;
	}

	public Long getHojaId() {
		return hojaId;
	}

	public void setHojaId(Long hojaId) {
		this.hojaId = hojaId;
	}

	public List<Long> getIdCrdespecifico() {
		return idCrdespecifico;
	}

	public void setIdCrdespecifico(List<Long> idCrdespecifico) {
		this.idCrdespecifico = idCrdespecifico;
	}

	public List<Long> getIdVariable() {
		return idVariable;
	}

	public void setIdVariable(List<Long> idVariable) {
		this.idVariable = idVariable;
	}
	public List<String> getNombreVariable() {
		return nombreVariable;
	}

	public void setNombreVariable(List<String> nombreVariable) {
		this.nombreVariable = nombreVariable;
	}
	public String getMsGeneral() {
		return msGeneral;
	}

	public void setMsGeneral(String msGeneral) {
		this.msGeneral = msGeneral;
	}
	public String getCrdSpe() {
		return crdSpe;
	}

	public void setCrdSpe(String crdSpe) {
		this.crdSpe = crdSpe;
	}

	public String getVariable() {
		return variable;
	}

	public void setVariable(String variable) {
		this.variable = variable;
	}

	public List<String> getCrdEspecificosM() {
		return crdEspecificosM;
	}

	public void setCrdEspecificosM(List<String> crdEspecificosM) {
		this.crdEspecificosM = crdEspecificosM;
	}

	public List<MomentoSeguimientoGeneral_ensayo> getListaMomentosDSSeleccionado() {
		return listaMomentosDSSeleccionado;
	}

	public void setListaMomentosDSSeleccionado(
			List<MomentoSeguimientoGeneral_ensayo> listaMomentosDSSeleccionado) {
		this.listaMomentosDSSeleccionado = listaMomentosDSSeleccionado;
	}

	

}