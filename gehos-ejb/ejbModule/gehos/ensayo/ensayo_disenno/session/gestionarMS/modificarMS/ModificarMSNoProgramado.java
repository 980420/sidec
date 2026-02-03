package gehos.ensayo.ensayo_disenno.session.gestionarMS.modificarMS;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
















import gehos.bitacora.session.traces.IBitacora;
import gehos.ensayo.ensayo_configuracion.session.custom.SeguridadEstudio;
import gehos.ensayo.ensayo_disenno.gestionarEstudio.modificarEstudioControlador;
import gehos.ensayo.entity.Cronograma_ensayo;
import gehos.ensayo.entity.HojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneralHojaCrd_ensayo;
import gehos.ensayo.entity.MomentoSeguimientoGeneral_ensayo;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.persistence.EntityManager;















import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage.Severity;




@Name("modificarMSNoProgramado")
@Scope(ScopeType.CONVERSATION)
public class ModificarMSNoProgramado {

	@In EntityManager entityManager;
	@In FacesMessages facesMessages;	
	@In IBitacora bitacora;

	FacesMessage facesMessage;
	private MomentoSeguimientoGeneral_ensayo ms = new MomentoSeguimientoGeneral_ensayo();
	MomentoSeguimientoGeneralHojaCrd_ensayo msCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();

	private long cid;
	private long idCronograma;	 	
	private boolean inicioConversacion = false;
	private long idMomentoSeguimiento;
	private List<MomentoSeguimientoGeneralHojaCrd_ensayo> listaMsHojaCrd = new ArrayList<MomentoSeguimientoGeneralHojaCrd_ensayo>();
	private HojaCrd_ensayo hojaCRD = new HojaCrd_ensayo();
	Hashtable<Long,HojaCrd_ensayo> hojaCRDSeleccionada = new Hashtable<Long, HojaCrd_ensayo>();
	private long idHojaCRD;
	List<HojaCrd_ensayo> listaHojasCRD=new ArrayList<HojaCrd_ensayo>();


	List <String> descripciones = new ArrayList<String>();
	private String descripcion;

	//Inicializar.
	public void inicializarMSNoProgramado(){			
		inicioConversacion = true;		
		this.descripciones.add(SeamResourceBundle.getBundle().getString("prm_unaVez_ens"));
		this.descripciones.add(SeamResourceBundle.getBundle().getString("prm_cadaVez_ens"));
		this.ms =(MomentoSeguimientoGeneral_ensayo) entityManager.find(MomentoSeguimientoGeneral_ensayo.class, idMomentoSeguimiento);
		descripcion = ms.getDescripcion();


		listaMsHojaCrd = (List<MomentoSeguimientoGeneralHojaCrd_ensayo>) entityManager.createQuery("select msCrd from MomentoSeguimientoGeneralHojaCrd_ensayo msCrd "
				+ "where msCrd.momentoSeguimientoGeneral.id = :idMs")				  
				.setParameter("idMs", ms.getId())
				.getResultList();
		for(int i = 0;i<listaMsHojaCrd.size();i++){
			if(!listaMsHojaCrd.get(i).getEliminado()){
				HojaCrd_ensayo crd = listaMsHojaCrd.get(i).getHojaCrd();
				listaHojasCRD.add(crd);
				hojaCRDSeleccionada.put(crd.getId(), crd);
			}
		}

		this.cid = bitacora.registrarInicioDeAccion(SeamResourceBundle.getBundle().getString("prm_bitacoraModificar_ens"));
	}


	//Metodo para crear el MS programado
	public String modificarMSNoProgramado()
	{
		try {

			if(listaHojasCRD.size()==0){
				facesMessages.clear();
				facesMessages.addFromResourceBundle("msg_validacionHojasCrd_ens");
				return "error";
			}

			ms.setDescripcion(descripcion);				
			ms.setEliminado(false);
			ms.setFechaActualizacion(Calendar.getInstance().getTime());					
			ms.setProgramado(false);
			ms.setCid(cid);				
			entityManager.persist(ms);			
			modificarMSHojaCRD();				
			entityManager.flush();		
			return "ok";

		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
			return "error";

		}
	}
	//Metodo que devuelve un MomentoSeguimientoGeneralHojaCrd_ensayo dado una hojaCRD
	public MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd(
			HojaCrd_ensayo hojaCRD, MomentoSeguimientoGeneral_ensayo ms) {
		for (int i = 0; i < listaMsHojaCrd.size(); i++) {
			if (listaMsHojaCrd.get(i).getHojaCrd().getId() == hojaCRD.getId() && 
					listaMsHojaCrd.get(i).getMomentoSeguimientoGeneral().getId() == ms.getId())
				return listaMsHojaCrd.get(i);
		}

		return null;


	}
	//Metodo para crear la relacion entre MS programado y las hojas CRD
	public void modificarMSHojaCRD()
	{			
		for (int i = 0; i < listaHojasCRD.size(); i++) {
			MomentoSeguimientoGeneralHojaCrd_ensayo msHojaCrd = msHojaCrd(listaHojasCRD.get(i),ms);
			
			if (msHojaCrd == null) {
				msHojaCrd = new MomentoSeguimientoGeneralHojaCrd_ensayo();
				msHojaCrd.setHojaCrd(listaHojasCRD.get(i));
				msHojaCrd.setMomentoSeguimientoGeneral(ms);
				msHojaCrd.setEliminado(false);
				listaMsHojaCrd.add(msHojaCrd);
			} else{
				listaMsHojaCrd.remove(msHojaCrd);
				msHojaCrd.setEliminado(false);				
				listaMsHojaCrd.add(msHojaCrd);
			}	
			if(listaMsHojaCrd.size()==0){
				listaMsHojaCrd.add(msHojaCrd);			
			}

		}
		
		for (int i = 0; i < listaMsHojaCrd.size(); i++) {

			if (!listaHojasCRD.contains(listaMsHojaCrd.get(i).getHojaCrd()))
				listaMsHojaCrd.get(i).setEliminado(true);
			entityManager.persist(listaMsHojaCrd.get(i));

		}	
	}
	//Hojas CRD
	//Metodo para maracar y desmarcar las Hojas CRD

	public void seleccionarHojaCRD(){
		try {
			if (!hojaCRDSeleccionada.containsKey(idHojaCRD)) {
				// cie
				HojaCrd_ensayo crd = (HojaCrd_ensayo) entityManager
						.createQuery(
								"select crd from HojaCrd_ensayo crd where crd.id = :idCRD")
								.setParameter("idCRD", idHojaCRD).getSingleResult();
				listaHojasCRD.add(crd);

				hojaCRDSeleccionada.put(crd.getId(), crd);

			} else {
				HojaCrd_ensayo c = hojaCRDSeleccionada.get(idHojaCRD);
				hojaCRDSeleccionada.remove(idHojaCRD);
				listaHojasCRD.remove(c);
				

			}
		} catch (Exception e) {
			facesMessages.clear();
			facesMessages.add(e.getMessage());
		}

	}
	//Metodo para eliminar una hoja CRD seleccionada
	public void eliminarHojaCRD(long idHojaCRD){
		HojaCrd_ensayo c = hojaCRDSeleccionada.get(idHojaCRD);
		hojaCRDSeleccionada.remove(idHojaCRD);
		listaHojasCRD.remove(c);

	}

	//Fin hojas CRD

	/**
	 * Habilitar el panel de los datos de los Momentos de Seguimiento no se llaman Pesquisaje, Eval Inicial e Interrupcion
	 * @return false si coinciden los nombres, true sino
	 * @author Tania
	 */

	public boolean habilitarModificar(){
		if (ms.getNombre().equals(SeamResourceBundle.getBundle().getString("prm_pesquisaje_ens")) || ms.getNombre().equals(SeamResourceBundle.getBundle().getString("prm_evaluacionInicial_ens")) || ms.getNombre().equals(SeamResourceBundle.getBundle().getString("prm_interrupcion_ens"))) {
			return false;
		}
		else return true;

	}

	/**
	 * 
	 * Validaciones

	 */
	private String message = "";

	// mensajes en los campos (azterizcos rojos)
	public void validatorManagerExeption(String mensaje) {
		this.message = mensaje;
		this.facesMessage = new FacesMessage(message, null);
		this.facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		throw new ValidatorException(facesMessage);
	}

	// mensajes arriba
	public void validatorManagerGlobalExeption(String mensaje) {
		facesMessages.addToControlFromResourceBundle("message", Severity.ERROR,
				mensaje);
		this.facesMessage = new FacesMessage();
		throw new ValidatorException(facesMessage);
	}

	// Validacion de numeros
	public void number4cifras(FacesContext context, UIComponent component, Object value) {

		if (value.toString().matches("-^?\\d+$"))// valida que no sea //
			// negativo
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"));

		if (!value.toString().matches("^(?:\\+)?\\d+$"))// valida que no //
			// tenga caracteres
			// // extranos
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"));
		if(value.toString().length()>4)
			//que no exceda las 4 cifras
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"mas4cifras"));
		try {// valida que sea entero
			@SuppressWarnings("unused")
			Integer val = new Integer(value.toString());
		} catch (Exception e) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"msg_validacionNumero_ens"));
		}

	}
	private static final String CARACTERES_ESPECIALES = SeamResourceBundle
			.getBundle().getString("caracteresEspeciales");


	public void textnumberCE100(FacesContext context, UIComponent component,
			Object value) {

		if (value.toString().length() > 100) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres100"));
		}

	}

	public void textnumber100(FacesContext context, UIComponent component,
			Object value) {

		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 100) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres100"));
		}

	}


	public void textnumber250(FacesContext context, UIComponent component,
			Object value) {

		if (!value.toString()
				.matches("^(\\s*[A-Za-z"+CARACTERES_ESPECIALES+"0-9]+\\s*)++$")) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"caracteresIncorrectos"));
		}

		if (value.toString().length() > 250) {
			validatorManagerExeption(SeamResourceBundle.getBundle().getString(
					"maximoCaracteres250"));
		}

	}

	//get y set
	public Long getCid() {
		return cid;
	}

	public void setCid(Long cid) {
		this.cid = cid;
	}

	public MomentoSeguimientoGeneral_ensayo getMs() {
		return ms;
	}


	public void setMs(MomentoSeguimientoGeneral_ensayo ms) {
		this.ms = ms;
	}

	public long getIdHojaCRD() {
		return idHojaCRD;
	}


	public void setIdHojaCRD(long idHojaCRD) {
		this.idHojaCRD = idHojaCRD;
	}


	public Hashtable<Long, HojaCrd_ensayo> getHojaCRDSeleccionada() {
		return hojaCRDSeleccionada;
	}


	public void setHojaCRDSeleccionada(
			Hashtable<Long, HojaCrd_ensayo> hojaCRDSeleccionada) {
		this.hojaCRDSeleccionada = hojaCRDSeleccionada;
	}



	public List<HojaCrd_ensayo> getListaHojasCRD() {
		return listaHojasCRD;
	}


	public void setListaHojasCRD(List<HojaCrd_ensayo> listaHojasCRD) {
		this.listaHojasCRD = listaHojasCRD;
	}



	public HojaCrd_ensayo getHojaCRD() {
		return hojaCRD;
	}

	public void setHojaCRD(HojaCrd_ensayo hojaCRD) {
		this.hojaCRD = hojaCRD;
	}


	public List<String> getDescripciones() {
		return descripciones;
	}


	public void setDescripciones(List<String> descripciones) {
		this.descripciones = descripciones;
	}


	public String getDescripcion() {
		return descripcion;
	}


	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}


	public MomentoSeguimientoGeneralHojaCrd_ensayo getMsCrd() {
		return msCrd;
	}


	public void setMsCrd(MomentoSeguimientoGeneralHojaCrd_ensayo msCrd) {
		this.msCrd = msCrd;
	}


	public boolean isInicioConversacion() {
		return inicioConversacion;
	}


	public void setInicioConversacion(boolean inicioConversacion) {
		this.inicioConversacion = inicioConversacion;
	}


	public long getIdMomentoSeguimiento() {
		return idMomentoSeguimiento;
	}


	public void setIdMomentoSeguimiento(long idMomentoSeguimiento) {
		this.idMomentoSeguimiento = idMomentoSeguimiento;
	}


	public List<MomentoSeguimientoGeneralHojaCrd_ensayo> getListaMsHojaCrd() {
		return listaMsHojaCrd;
	}


	public void setListaMsHojaCrd(
			List<MomentoSeguimientoGeneralHojaCrd_ensayo> listaMsHojaCrd) {
		this.listaMsHojaCrd = listaMsHojaCrd;
	}


	public long getIdCronograma() {
		return idCronograma;
	}


	public void setIdCronograma(long idCronograma) {
		this.idCronograma = idCronograma;
	}

}
