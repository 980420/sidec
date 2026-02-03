//CU 4 Modificar sujeto
package gehos.ensayo.ensayo_conduccion.gestionarSujeto;


import gehos.autenticacion.entity.Usuario;
import gehos.bitacora.session.traces.IBitacora;
import gehos.comun.funcionalidades.entity.Funcionalidad;
import gehos.comun.shell.IActiveModule;
import gehos.configuracion.management.entity.Entidad_configuracion;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.entity.Causa_ensayo;
import gehos.ensayo.entity.CrdEspecifico_ensayo;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.Entidad_ensayo;
import gehos.ensayo.entity.EstadoHojaCrd_ensayo;
import gehos.ensayo.entity.EstadoInclusion_ensayo;
import gehos.ensayo.entity.EstadoMomentoSeguimiento_ensayo;
import gehos.ensayo.entity.EstadoMonitoreo_ensayo;
import gehos.ensayo.entity.GrupoSujetos_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoEspecifico_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;
import gehos.ensayo.entity.Role_ensayo;
import gehos.ensayo.entity.Sujeto_ensayo;
import gehos.ensayo.entity.Usuario_ensayo;
import gehos.ensayo.entity.VariableDato_ensayo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;

@Name("modificarSujeto")
@Scope(ScopeType.CONVERSATION)
public class ModificarSujeto{

	
	private Long idSujeto;
	
	@In
	private IActiveModule activeModule;
	@In
	private Usuario user;
	@In(scope = ScopeType.SESSION) SeguridadEstudio seguridadEstudio;

	// Valores seleccionados de nomencladores--------------------------
	
	protected String grupo = "";
	protected String estado = "";
	protected Date fechaInclucion;
	protected String inicialesP;
	protected Long numeroInclucion;
	protected String inicialesCentro;
	protected String inicialesCentroPromotor;
	protected String hora;
	protected String minutos;
	protected String am_pm;
	
	
	//Cronnogrma Especifico by Eiler
	private MomentoSeguimientoGeneral_ensayo listadoMomentosGeneral;

	// Nomencladores-------------------------------------------------------
	
	protected List<GrupoSujetos_ensayo> listarGrupo;
	protected List<EstadoInclusion_ensayo> listarEstados;
	private Usuario_ensayo usuario = new Usuario_ensayo();
	protected @In
	EntityManager entityManager;
	protected @In
	IBitacora bitacora;
	protected @In(create = true)
	FacesMessages facesMessages;

	private long cid = -1;

	protected String seleccione;

	// variables que definen si los campos son obligatorios o no
	protected boolean inicialesRequired;
	protected boolean numInclusionRequired;
	
	protected boolean fechaInclusionRequired;

	protected boolean grupoRequired;
	protected boolean seleccionoNoIncluido;
	
	protected boolean estadoRequired;
	protected boolean inicialesCentroRequired;
	protected boolean inicializado = false;

	protected String pagAnterior;

	protected Sujeto_ensayo sujeto;
	private String descripcionCausaCambioEstado; 

	

	private boolean error;
	
	@Begin(join = true, flushMode = FlushModeType.MANUAL)
	public void initConversation() throws ParseException {
		this.inicialesRequired = true;
		this.numInclusionRequired = true;
		this.fechaInclusionRequired = true;
		
		this.grupoRequired = true;
		
		this.estadoRequired = false;
		this.inicialesCentroRequired = false;
		
		if(seguridadEstudio.VerificarActivo()){
			listarGrupo();
		}
		listarEstados();

		this.sujeto = (Sujeto_ensayo)entityManager.createQuery("select suj from Sujeto_ensayo suj where suj.id=:id").setParameter("id", idSujeto).getSingleResult();
		this.inicialesP = this.sujeto.getInicialesPaciente();
		
		if(sujeto.getNombre()!=null && !sujeto.getNombre().isEmpty()){
			String[] valores = this.sujeto.getNombre().split(":");
			this.hora=valores[0];
			this.minutos=valores[1];
			this.am_pm=this.sujeto.getNombre().substring(this.sujeto.getNombre().length()-2,this.sujeto.getNombre().length());
		}
		this.numeroInclucion = this.sujeto.getNumeroInclucion();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
		if(this.sujeto.getFechaInclucion() != null){
			String valoraux = this.sujeto.getFechaInclucion();
			
			 String[] fechaArreglo = valoraux.toString().split("/");
			 String fecha = "";
			 if(fechaArreglo.length == 1){
				 fecha = "01" + "/" + "01" + "/" + fechaArreglo[0];
			 }else if(fechaArreglo.length == 2){
				 fecha = "01" + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
			 }
			 else if(fechaArreglo.length == 3){
				 if(fechaArreglo[0].equals("****")){
					 fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + "2000";
				 }else if(fechaArreglo[0].length() == 4 && !fechaArreglo[0].equals("****")){
					 fecha = fechaArreglo[2] + "/" + fechaArreglo[1] + "/" + fechaArreglo[0];
				 }
				 else{
					 fecha = fechaArreglo[0] + "/" + fechaArreglo[1] + "/" + fechaArreglo[2];
				 }
				 
				 
			 }
			this.fechaInclucion = formatter.parse(fecha);
			
		}
		
		//this.sexo = this.sujeto.getSexo().getValor();
		this.grupo = this.sujeto.getGrupoSujetos().getNombreGrupo();
		this.estado = this.sujeto.getEstadoInclusion().getNombre();
		this.inicialesCentro = this.sujeto.getInicialesCentro();
		this.inicialesCentroPromotor = this.getHospitalPromotor();
		this.inicializado = true;
	}
	
	public String getHospitalPromotor() {

		@SuppressWarnings("unchecked")
        List<Entidad_ensayo> entidadEnsayo = (List<Entidad_ensayo>) entityManager
                .createQuery(
                        "select e.entidad from EstudioEntidad_ensayo e where e.entidad.tipoEntidad.valor=:Biotec and e.estudio=:EstudioActivo")
                .setParameter(
                        "EstudioActivo",
                        this.seguridadEstudio.getEstudioEntidadActivo()
                                .getEstudio())
                .setParameter(
                        "Biotec", SeamResourceBundle.getBundle().getString(
                                "bioTecnologica")).getResultList();
        String nom = "";
        nom = entidadEnsayo.get(0).getFax();
		return nom;
	}
	
	public Role_ensayo DevolverRol() {
		Role_ensayo rol = (Role_ensayo) entityManager.createQuery("select usuarioE.role from UsuarioEstudio_ensayo usuarioE inner join usuarioE.usuario usuario inner join usuarioE.estudioEntidad estEnt where estEnt.id=:estudId and usuario.id=:idusua and usuarioE.eliminado <> true")
				.setParameter("estudId",seguridadEstudio.getEstudioEntidadActivo().getId())
				.setParameter("idusua",user.getId())
				.getSingleResult();
		
		return rol;
	}
	
	public CrdEspecifico_ensayo ObtenerCRD(){
		CrdEspecifico_ensayo crd = new CrdEspecifico_ensayo();
		
		String queryCRD = "select CrdEspecifico "
				+ "from CrdEspecifico_ensayo CrdEspecifico "
				+ "inner join CrdEspecifico.momentoSeguimientoEspecifico momentoEspecifico "
				+ "inner join momentoEspecifico.momentoSeguimientoGeneral momentoSeguimientoGeneral "
				+ "inner join momentoEspecifico.sujeto sujeto "
				+ "where sujeto.id=:idSujeto and momentoSeguimientoGeneral.nombre = 'Pesquisaje' ";
				

		
		crd = (CrdEspecifico_ensayo)entityManager.createQuery(queryCRD)
				.setParameter("idSujeto", this.idSujeto).getSingleResult();
		
		return crd;
	}
	
	public VariableDato_ensayo ObtenerDatoVariable(){
		VariableDato_ensayo dato = new VariableDato_ensayo();
		
		String queryCRD = "select variableDato "
				+ "from VariableDato_ensayo variableDato "
				+ "inner join variableDato.variable variable "
				+ "inner join variableDato.crdEspecifico crdEspecifico "
				+ "inner join variable.seccion seccion "
				+ "inner join seccion.hojaCrd hojaCrd "
				+ "where hojaCrd.id=:idHoja and crdEspecifico.id=:idCRD and variable.descripcionVariable = 'Grupo de sujetos' ";
				

		try {
			dato = (VariableDato_ensayo)entityManager.createQuery(queryCRD)
					.setParameter("idHoja", ObtenerCRD().getHojaCrd().getId()).setParameter("idCRD", ObtenerCRD().getId()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
		
		
		return dato;
	}
	
	public VariableDato_ensayo ObtenerDatoVariableFecha(){
		VariableDato_ensayo dato = new VariableDato_ensayo();
		
		String queryCRD = "select variableDato "
				+ "from VariableDato_ensayo variableDato "
				+ "inner join variableDato.variable variable "
				+ "inner join variableDato.crdEspecifico crdEspecifico "
				+ "inner join variable.seccion seccion "
				+ "inner join seccion.hojaCrd hojaCrd "
				+ "where hojaCrd.id=:idHoja and crdEspecifico.id=:idCRD and variable.descripcionVariable=:fechaInc ";
				

		try {
			dato = (VariableDato_ensayo)entityManager.createQuery(queryCRD)
					.setParameter("idHoja", ObtenerCRD().getHojaCrd().getId()).setParameter("fechaInc", SeamResourceBundle.getBundle()
							.getString("fechaInclusion")).setParameter("idCRD", ObtenerCRD().getId()).getSingleResult();
		} catch (Exception e) {
			return null;
		}
		
		
		return dato;
	}
	
	

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	

	@SuppressWarnings("unchecked")
	public void listarGrupo() {
		listarGrupo = (List<GrupoSujetos_ensayo>) entityManager.createQuery(
				"select g from GrupoSujetos_ensayo g where g.estudio=:estud and g.habilitado=true and g.eliminado=false").setParameter("estud", seguridadEstudio.getEstudioEntidadActivo().getEstudio()).getResultList();
	}

	public List<String> listarGrupo1() {
		List<String> nombregrupo = new ArrayList<String>();
		try {
			for (int i = 0; i < listarGrupo.size(); i++) {
				if(!listarGrupo.get(i).getNombreGrupo().equals("Grupo Pesquisaje")){
					nombregrupo.add(listarGrupo.get(i).getNombreGrupo());
				}
			}

		} catch (Exception e) {

		}
		return nombregrupo;
	}

	@SuppressWarnings("unchecked")
	public void listarEstados() {
		listarEstados = (List<EstadoInclusion_ensayo>) entityManager.createQuery(
				"select e from EstadoInclusion_ensayo e").getResultList();

	}

	public List<String> listarEstados1() {
		List<String> nombreEst = new ArrayList<String>();
		for (int i = 0; i < listarEstados.size(); i++) {
			if(DevolverRol().getCodigo().equals("ecCord") || DevolverRol().getCodigo().equals("ecInv")){
				if(listarEstados.get(i).getNombre().equals("No Incluido") || listarEstados.get(i).getNombre().equals("Incluido")){
					nombreEst.add(listarEstados.get(i).getNombre());
				}
			}else if(DevolverRol().getCodigo().equals("ecMon")){
				if(listarEstados.get(i).getNombre().equals("Mal Incluido") || listarEstados.get(i).getNombre().equals("Incluido")){
					nombreEst.add(listarEstados.get(i).getNombre());
				}
			}
		}
		return nombreEst;
	}

	public GrupoSujetos_ensayo GrupoxNombre() {
		GrupoSujetos_ensayo grup = new GrupoSujetos_ensayo();
		for (int i = 0; i < listarGrupo.size(); i++) {
			if (listarGrupo.get(i).getNombreGrupo().equals(grupo)) {
				grup = listarGrupo.get(i);
			}
		}

		return grup;
	}
	
	


	public EstadoInclusion_ensayo EstadoxNombre() {
		EstadoInclusion_ensayo estad = new EstadoInclusion_ensayo();
		for (int i = 0; i < listarEstados.size(); i++) {
			if (listarEstados.get(i).getNombre().equals(this.estado)) {
				estad = listarEstados.get(i);
			}

		}

		return estad;
	}
	
	

	// Codigo para picar la cadena de dias by Evelio.
			private List<String> picarCadena(MomentoSeguimientoGeneral_ensayo ms) {
				String dias = ms.getDia();
				String[] listaDiasSelecc;
				String[] listaUltDiaSelecc;
				List<String> listaDiasPlanificados = new ArrayList<String>();
				String y = "y";
				if (dias.contains(",")) {
					listaDiasSelecc = dias.split(", ");
					listaUltDiaSelecc = listaDiasSelecc[listaDiasSelecc.length - 1]
							.split(" " + y + " ");
					for (int j = 0; j < listaDiasSelecc.length - 1; j++) {
						listaDiasPlanificados.add(listaDiasSelecc[j]);
					}
					listaDiasPlanificados.add(listaUltDiaSelecc[0]);
					listaDiasPlanificados.add(listaUltDiaSelecc[1]);

				} else if (dias.contains(y)) {
					listaUltDiaSelecc = dias.split(" " + y + " ");
					listaDiasPlanificados.add(listaUltDiaSelecc[0]);
					listaDiasPlanificados.add(listaUltDiaSelecc[1]);

				} else {
					listaDiasPlanificados.add(dias);

				}
				return listaDiasPlanificados;
			}



	@Transactional
	public void persistir() {
		if (!this.errores()) {
			if (this.cid == -1) {
				this.cid = this.bitacora
						.registrarInicioDeAccion(SeamResourceBundle.getBundle()
								.getString("modificandoSujeto"));
			}
			
			this.usuario = (Usuario_ensayo) entityManager.find(
					Usuario_ensayo.class, user.getId());
			this.sujeto = entityManager.merge(this.sujeto);
			this.sujeto.setCid(this.cid);
			this.sujeto.setInicialesPaciente(inicialesP);
			
			if((!hora.isEmpty() && (minutos.isEmpty() || am_pm.isEmpty()))){
				error = true;
				this.facesMessages
				.addToControlFromResourceBundle(
						"horaInclusion",
						Severity.ERROR,
						"Introduzca los valores restantes");
				return;
			}
			
			if(!hora.isEmpty() && !minutos.isEmpty() && !am_pm.isEmpty()){
				this.sujeto.setNombre(hora + ":" + minutos + ":00 " + am_pm.replace("1", ""));
			}
			
			SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date fecha = fechaInclucion;
			if (fechaInclucion != null) {
				String fechaAxu = formatter.format(fecha);
				this.sujeto.setFechaInclucion(fechaAxu);
			}
			
			
			/*EstadoSujeto_ensayo estadosSuje = (EstadoSujeto_ensayo) entityManager.createQuery("select est from EstadoSujeto_ensayo est where est.nombre='Habilitado'").getSingleResult();

			this.sujeto.setEstadoSujeto(estadosSuje);*/
			GrupoSujetos_ensayo grupoViejo = this.sujeto.getGrupoSujetos();
			this.sujeto.setGrupoSujetos(GrupoxNombre());
			// Si el estado de inclusion seleccionado es distinto del 
			// estado actual del sujeto, crear la causa con la descripcion y gaurdarla
			String estadoSujeto=sujeto.getEstadoInclusion().getNombre();
			if(this.descripcionCausaCambioEstado!=null && 
					!this.descripcionCausaCambioEstado.isEmpty() && 
					!estadoSujeto.trim().toLowerCase().equalsIgnoreCase(this.estado.trim().toLowerCase())){
				Causa_ensayo causa=new Causa_ensayo();
				causa.setCid(this.cid);
				causa.setTipoCausa("Cambiar estado sujeto");
				causa.setDescripcion(this.descripcionCausaCambioEstado);
				Usuario_ensayo usuario = entityManager.find(Usuario_ensayo.class,
						user.getId());
				causa.setUsuario(usuario);
				causa.setFecha(Calendar.getInstance().getTime());
				causa.setEstudio(seguridadEstudio.getEstudioActivo());
				causa.setSujeto(this.sujeto);
				entityManager.persist(causa);
				entityManager.flush();
			}

			this.sujeto.setEstadoInclusion(EstadoxNombre());
			this.sujeto.setInicialesCentro(inicialesCentro);
			this.sujeto.setNumeroInclucion(numeroInclucion);
			this.sujeto.setUsuario(usuario);
			//this.sujeto.setEliminado(false);
			if (numeroInclucion == null) {
				this.sujeto.setCodigoPaciente(inicialesCentroPromotor + "_" + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getIdentificador() + "_" + inicialesCentro + "_" + inicialesP);
			} else {
				this.sujeto.setCodigoPaciente(inicialesCentroPromotor + "_" + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getIdentificador() + "_" + inicialesCentro + "_" + inicialesP  
						+ "_" + numeroInclucion);
			}
			

			entityManager.persist(this.sujeto);
			VariableDato_ensayo datoGrupo = this.ObtenerDatoVariable();
			if(datoGrupo != null && !this.grupo.equals(datoGrupo.getValor())){
				datoGrupo.setValor(grupo);
				entityManager.persist(datoGrupo);
				entityManager.flush();
			}
			
			VariableDato_ensayo datoFecha = this.ObtenerDatoVariableFecha();
			if(datoFecha != null && this.sujeto.getFechaInclucion() != null){
				datoFecha.setValor(this.sujeto.getFechaInclucion());
				entityManager.persist(datoFecha);
				entityManager.flush();
			}
			
			
			

			entityManager.flush();
			//me quede aqui
			
			Cronograma_ensayo cronograma_General = (Cronograma_ensayo) this.entityManager.createQuery("select c from Cronograma_ensayo c where c.grupoSujetos.id = :id").setParameter("id", this.sujeto.getGrupoSujetos().getId()).getSingleResult();
			// Aqui tengo el listado de los momento de seguimientos general
			String dia = "0";
			Integer diaEsp = 0;
			@SuppressWarnings("unchecked")
			List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneral = (List<MomentoSeguimientoGeneral_ensayo>) this.entityManager.createQuery("select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id = :id and msg.programado = true and msg.dia = :eva1 and (msg.eliminado = null or msg.eliminado = false)").setParameter("eva1", dia).setParameter("id", cronograma_General.getId()).getResultList();
			
			//Dias Planificados
			// Visualizar las hojas CRD en los momentos 0
			@SuppressWarnings("unchecked")
			List<MomentoSeguimientoGeneral_ensayo> listadoMomentosGeneralDias = entityManager
					.createQuery(
							"select msg from MomentoSeguimientoGeneral_ensayo msg where msg.cronograma.id=:id and msg.programado=True")
					.setParameter("id", cronograma_General.getId()).getResultList();
			for (int i = 0; i < listadoMomentosGeneralDias.size(); i++) {
				MomentoSeguimientoGeneral_ensayo ms = listadoMomentosGeneralDias
						.get(i);
				if ((ms.getEliminado() != true) && (!ms.getDia().equals("0"))) {
					List<String> listaDiasPlanificados = picarCadena(ms);
					if(listaDiasPlanificados.contains("0")){
						listadoMomentosGeneral.add(ms);
					}
				}
			}

			//Fin Dias Planificados
			MomentoSeguimientoEspecifico_ensayo momentoEvaluacion =  new MomentoSeguimientoEspecifico_ensayo();
			for (int i = 0; i < listadoMomentosGeneral.size(); i++) {
				try {
					momentoEvaluacion = (MomentoSeguimientoEspecifico_ensayo) this.entityManager.createQuery("select crd from MomentoSeguimientoEspecifico_ensayo crd where crd.sujeto.id = :idSujeto and (crd.eliminado = null or crd.eliminado = false) and crd.momentoSeguimientoGeneral.id = :eva1 and crd.dia = :dia").setParameter("eva1", listadoMomentosGeneral.get(i).getId()).setParameter("dia", diaEsp).setParameter("idSujeto", this.sujeto.getId()).getSingleResult();
				} catch (Exception e) {
					momentoEvaluacion = null;
				}
			
			if((DevolverRol().getCodigo().equals("ecCord") || DevolverRol().getCodigo().equals("ecInv")) && this.sujeto.getEstadoInclusion().getId() == 4 && momentoEvaluacion != null){
				momentoEvaluacion.setFechaInicio(fechaInclucion);
				entityManager.persist(momentoEvaluacion);
				entityManager.flush();
			}
			
			if((DevolverRol().getCodigo().equals("ecCord") || DevolverRol().getCodigo().equals("ecInv")) && momentoEvaluacion != null && this.sujeto.getGrupoSujetos().getId() != grupoViejo.getId()){
				//Busco el cronograma que le corresponde al sujeto.	
				
				
				momentoEvaluacion.setEliminado(true);
				entityManager.persist(momentoEvaluacion);
				entityManager.flush();
				@SuppressWarnings("unchecked")
				List<CrdEspecifico_ensayo> lista = (List<CrdEspecifico_ensayo>)entityManager.createQuery("select crd from CrdEspecifico_ensayo crd where crd.momentoSeguimientoEspecifico.id=:id and crd.eliminado = false").setParameter("id", momentoEvaluacion.getId()).getResultList();
				for (int k = 0; k < lista.size(); k++) {
					lista.get(k).setEliminado(true);
					entityManager.persist(lista.get(k));
					entityManager.flush();
				}
	        
			
				//List<String> listaDiasPlanificados =picarCadena(ms);
				long idEstadoSeguimiento=2;
				//EstadoMomentoSeguimiento_ensayo estadoMomento=(EstadoMomentoSeguimiento_ensayo) entityManager.createQuery("select e from EstadoMomentoSeguimiento_ensayo e where e.id=:id").setParameter("id", idEstadoSeguimiento).getSingleResult();
				//EstadoMonitoreo_ensayo estadoMonitoreo=(EstadoMonitoreo_ensayo) entityManager.createQuery("select e from EstadoMonitoreo_ensayo e where e.id=:id").setParameter("id", idEstadoSeguimiento).getSingleResult();
				EstadoMomentoSeguimiento_ensayo estadoMomento=entityManager.find(EstadoMomentoSeguimiento_ensayo.class, idEstadoSeguimiento);
				EstadoMonitoreo_ensayo estadoMonitoreo=entityManager.find(EstadoMonitoreo_ensayo.class, idEstadoSeguimiento);
				
					@SuppressWarnings("unchecked")
					List<HojaCrd_ensayo> listaHojas=entityManager.createQuery("select momentoGhojaCRD.hojaCrd from MomentoSeguimientoGeneralHojaCrd_ensayo momentoGhojaCRD where momentoGhojaCRD.momentoSeguimientoGeneral.id=:id and momentoGhojaCRD.eliminado = 'false'").setParameter("id", listadoMomentosGeneral.get(i).getId()).getResultList();
					//List<HojaCrd_ensayo> listaHojas=entityManager.createQuery("select crd from HojaCrd_ensayo crd where crd.momentoSeguimientoGeneral.id=:id").setParameter("id", listadoMomentosGeneral.getId()).getResultList();
					MomentoSeguimientoEspecifico_ensayo momentoEspecifico=new MomentoSeguimientoEspecifico_ensayo();
					Calendar cal=Calendar.getInstance();
					momentoEspecifico.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("momentoCrear")));
					momentoEspecifico.setDia(0);
					momentoEspecifico.setEliminado(false);
					momentoEspecifico.setEstadoMomentoSeguimiento(estadoMomento);
					momentoEspecifico.setEstadoMonitoreo(estadoMonitoreo);
					
					momentoEspecifico.setFechaCreacion(cal.getTime());
					momentoEspecifico.setFechaInicio(this.fechaInclucion);
					cal.add(Calendar.DATE, listadoMomentosGeneral.get(i).getTiempoLlenado());
					momentoEspecifico.setFechaFin(cal.getTime());
					momentoEspecifico.setMomentoSeguimientoGeneral(listadoMomentosGeneral.get(i));
					momentoEspecifico.setSujeto(sujeto);
					entityManager.persist(momentoEspecifico);
					entityManager.flush();
					
					String noIniciada="No iniciada";
					EstadoHojaCrd_ensayo estadoNoIniciada = (EstadoHojaCrd_ensayo) entityManager.createQuery("select e from EstadoHojaCrd_ensayo e where e.nombre=:noIniciada").setParameter("noIniciada", noIniciada).getSingleResult();
					
					
					for (int j2 = 0; j2 < listaHojas.size(); j2++) {
						CrdEspecifico_ensayo crdEsp=new CrdEspecifico_ensayo();
						crdEsp.setCid(bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("hojaCrear")));
						crdEsp.setEliminado(false);
						crdEsp.setEstadoHojaCrd(listaHojas.get(j2).getEstadoHojaCrd());
						crdEsp.setEstadoMonitoreo(estadoMonitoreo);
						crdEsp.setHojaCrd(listaHojas.get(j2));
						crdEsp.setMomentoSeguimientoEspecifico(momentoEspecifico);
						crdEsp.setEstadoHojaCrd(estadoNoIniciada);
						entityManager.persist(crdEsp);
						entityManager.flush();
					}
			}
		}

		}else{
			this.facesMessages.addToControlFromResourceBundle("idForm",Severity.INFO,"sujetoRegistrado");
			//String mensage = this.facesMessages.add(SeamResourceBundle.getBundle().getString("sujetoRegistrado"));
		}
	}

	public String getHospitalActivo() {
		
		Funcionalidad aux = activeModule.getActiveModule();
		aux = entityManager.merge(aux);
		Entidad_configuracion hosp = aux.getEntidad();
		hosp = entityManager.merge(hosp);
		String nom=hosp.getNombre();
		if(nom.equals("Centro de Inmunolog\u00EDa Molecular")){
			nom = "CIM";
		}
		
		return nom;

	}
	
	public void resetEstadoInclusion() {
		this.estado=this.sujeto.getEstadoInclusion().getNombre();
	}


	@SuppressWarnings("unchecked")
	public boolean errores() {
		error = false;
		String codigoSujeto = "";
		if (numeroInclucion == null) {
			codigoSujeto = inicialesCentroPromotor + "_" + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getIdentificador() + "_" + inicialesCentro + "_" + inicialesP;
		} else {
			codigoSujeto = inicialesCentroPromotor + "_" + this.seguridadEstudio.getEstudioEntidadActivo().getEstudio().getIdentificador() + "_" + inicialesCentro + "_" + inicialesP + "_"
					+ numeroInclucion;
		}
		
		String codigoViejo = sujeto.getCodigoPaciente();
		// validando que el sujeto no este registrado para evitar duplicados
		try {
			List<Sujeto_ensayo> listaSujeto = new ArrayList<Sujeto_ensayo>();
 			if(!codigoSujeto.equals(codigoViejo)){
 				listaSujeto = (List<Sujeto_ensayo>) entityManager
						.createQuery(
								"select s from Sujeto_ensayo s where s.codigoPaciente= :codigo")
						.setParameter("codigo", codigoSujeto)
						.getResultList();
			}
 			//modificamos este if para que permita modificar sujetos que coinciden en codigo pero que uno ha sido eliminado
			if (listaSujeto.size() > 0 && !listaSujeto.get(0).getEliminado()) {
				error = true;
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return error;
	}

	public Sujeto_ensayo getSujeto() {
		return sujeto;
	}

	public void setSujeto(Sujeto_ensayo sujeto) {
		this.sujeto = sujeto;
	}

	
	public String getGrupo() {
		return grupo;
	}

	public void setGrupo(String grupo) {
		this.grupo = grupo;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.seleccionoNoIncluido = false;
		this.estado = estado;
		if(this.estado.equals("No Incluido") || this.estado.equals(SeamResourceBundle.getBundle()
				.getString("evaluacion"))){
			this.seleccionoNoIncluido = true;
		}
	}

	

	public List<GrupoSujetos_ensayo> getListarGrupo() {
		return listarGrupo;
	}

	public void setListarGrupo(List<GrupoSujetos_ensayo> listarGrupo) {
		this.listarGrupo = listarGrupo;
	}

	public List<EstadoInclusion_ensayo> getListarEstados() {
		return listarEstados;
	}

	public void setListarEstados(List<EstadoInclusion_ensayo> listarEstados) {
		this.listarEstados = listarEstados;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getSeleccione() {
		return seleccione;
	}

	public void setSeleccione(String seleccione) {
		this.seleccione = seleccione;
	}

	public boolean isInicialesRequired() {
		return inicialesRequired;
	}

	public void setInicialesRequired(boolean inicialesRequired) {
		this.inicialesRequired = inicialesRequired;
	}

	public boolean isNumInclusionRequired() {
		return numInclusionRequired;
	}

	public void setNumInclusionRequired(boolean numInclusionRequired) {
		this.numInclusionRequired = numInclusionRequired;
	}

	public boolean isFechaInclusionRequired() {
		return fechaInclusionRequired;
	}

	public void setFechaInclusionRequired(boolean fechaInclusionRequired) {
		this.fechaInclusionRequired = fechaInclusionRequired;
	}

	public boolean isGrupoRequired() {
		return grupoRequired;
	}

	public void setGrupoRequired(boolean grupoRequired) {
		this.grupoRequired = grupoRequired;
	}

	public boolean isEstadoRequired() {
		return estadoRequired;
	}

	public void setEstadoRequired(boolean estadoRequired) {
		this.estadoRequired = estadoRequired;
	}

	public boolean isInicialesCentroRequired() {
		return inicialesCentroRequired;
	}

	public void setInicialesCentroRequired(boolean inicialesCentroRequired) {
		this.inicialesCentroRequired = inicialesCentroRequired;
	}

	public String getPagAnterior() {
		return pagAnterior;
	}

	public void setPagAnterior(String pagAnterior) {
		this.pagAnterior = pagAnterior;
	}

	public String getInicialesP() {
		return inicialesP;
	}

	public void setInicialesP(String inicialesP) {
		this.inicialesP = inicialesP;
	}

	public Long getNumeroInclucion() {
		return numeroInclucion;
	}

	public boolean isInicializado() {
		return inicializado;
	}

	public void setInicializado(boolean inicializado) {
		this.inicializado = inicializado;
	}

	public void setNumeroInclucion(Long numeroInclucion) {
		this.numeroInclucion = numeroInclucion;
	}

	public String getInicialesCentro() {
		return inicialesCentro;
	}

	public void setInicialesCentro(String inicialesCentro) {
		this.inicialesCentro = inicialesCentro;
	}

	public Long getIdSujeto() {
		return idSujeto;
	}

	public void setIdSujeto(Long idSujeto) {
		this.idSujeto = idSujeto;
	}
	
	@End
	public void salir(){}

	public boolean isSeleccionoNoIncluido() {
		return seleccionoNoIncluido;
	}

	public void setSeleccionoNoIncluido(boolean seleccionoNoIncluido) {
		this.seleccionoNoIncluido = seleccionoNoIncluido;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	

	public String getMinutos() {
		return minutos;
	}

	public void setMinutos(String minutos) {
		this.minutos = minutos;
	}

	public String getAm_pm() {
		return am_pm;
	}

	public void setAm_pm(String am_pm) {
		this.am_pm = am_pm;
	}

	public Date getFechaInclucion() {
		return fechaInclucion;
	}

	public void setFechaInclucion(Date fechaInclucion) {
		this.fechaInclucion = fechaInclucion;
	}
	
	public String getDescripcionCausaCambioEstado() {
		return descripcionCausaCambioEstado;
	}

	public void setDescripcionCausaCambioEstado(String descripcionCausaCambioEstado) {
		this.descripcionCausaCambioEstado = descripcionCausaCambioEstado;
	}

	public String getInicialesCentroPromotor() {
		return inicialesCentroPromotor;
	}

	public void setInicialesCentroPromotor(String inicialesCentroPromotor) {
		this.inicialesCentroPromotor = inicialesCentroPromotor;
	}
	
	
	
	
	
}
