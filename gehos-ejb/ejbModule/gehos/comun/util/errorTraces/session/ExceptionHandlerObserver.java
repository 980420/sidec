//package gehos.comun.util.errorTraces.session;
//
//import javax.faces.application.FacesMessage;
//
//import org.jboss.seam.Component;
//import org.jboss.seam.ScopeType;
//import org.jboss.seam.annotations.AutoCreate;
//import org.jboss.seam.annotations.Create;
//import org.jboss.seam.annotations.In;
//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Observer;
//import org.jboss.seam.annotations.Scope;
//import org.jboss.seam.annotations.Startup;
//import org.jboss.seam.core.Expressions;
//import org.jboss.seam.core.SeamResourceBundle;
//import org.jboss.seam.exception.ConfigRedirectHandler;
//import org.jboss.seam.exception.ExceptionHandler;
//import org.jboss.seam.exception.Exceptions;
//import org.jboss.seam.faces.FacesMessages;
//
///**
// * @author yurien Observer de todas las excepciones que ocurren en el sistema
// */
//@Name("exceptionHandlerObserver")
//@Scope(ScopeType.APPLICATION)
//@AutoCreate
//@Startup
//public class ExceptionHandlerObserver {
//
//	//Componente que se encarga de gestionar las excepciones
//	@In
//	ErrorDataManagement errorDataManagement;
//
//	
//	/**
//	 * Cuando se inicializa el componente se cargan los tipos de errores de la
//	 * BD si existen
//	 */
//	@Create
//	public void chargeErrorTypes() {
//		try {
//			errorDataManagement.chargeErrorTypes();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//	/**
//	 * ES UN OBSERVER PARA ESCUCHAR EL EVENTO org.jboss.seam.exceptionHandled
//	 * LANZADDO POR SEAM CUANDO PUDO MANEJAR UNA EXCEPCION
//	 * 
//	 * @param Throwable
//	 * 
//	 */
//	@Observer(create = true, value = "org.jboss.seam.exceptionHandled")
//	public void catchThrowableException(Throwable ex) {
//
//		try {
//			errorDataManagement.persistirError((Exception) ex);
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	/**
//	 * SE ENCARGA DE CREAR UN HANDLER DE LA EXCEPCION CAPTURADA
//	 * 
//	 * @return ConfigRedirectHandler
//	 * 
//	 */
//	@Deprecated
//	private ExceptionHandler createHandler() {
//
//		String message = SeamResourceBundle.getBundle().getString(
//				"generalPagesError");
//
//		return new ConfigRedirectHandler(Expressions.instance()
//				.createValueExpression("/error.xhtml", String.class),
//				Throwable.class, false, message, FacesMessage.SEVERITY_ERROR);
//
//	}
//
//}
