//package gehos.comun.util.errorTraces.session;
//
//import gehos.autenticacion.entity.Usuario;
//import gehos.comun.shell.IActiveModule;
//import gehos.comun.util.errorTraces.entity.ErrorType;
//import gehos.comun.util.errorTraces.entity.StackTrace;
//
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//import java.util.TreeSet;
//
//import javax.persistence.EntityManager;
//import javax.servlet.ServletContext;
//import javax.servlet.http.HttpServletRequest;
//import javax.transaction.NotSupportedException;
//import javax.transaction.SystemException;
//import javax.transaction.Transaction;
//
//import org.jboss.seam.Component;
//import org.jboss.seam.ScopeType;
//import org.jboss.seam.annotations.AutoCreate;
//import org.jboss.seam.annotations.In;
//import org.jboss.seam.annotations.Name;
//import org.jboss.seam.annotations.Scope;
//import org.jboss.seam.annotations.Transactional;
//import org.jboss.seam.contexts.SessionContext;
//import org.jboss.seam.debug.Contexts;
//import org.jboss.seam.faces.FacesContext;
//import org.jboss.seam.security.Credentials;
//import org.jboss.seam.web.Session;
//
//import com.sun.swing.internal.plaf.synth.resources.synth;
//
//import gehos.comun.util.errorTraces.entity.ErrorTrace;
//
//@Name("errorDataManagement")
//@Scope(ScopeType.APPLICATION)
//@AutoCreate
//public class ErrorDataManagement {
//
//	private HashMap<String, ErrorType> errorTypes = null;
//	private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
//	private SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy");
//	private String ip;
//	private String user;
//	private String modulo;
//
//	@In
//	EntityManager entityManager;
//
//	@SuppressWarnings("unchecked")
//	public void chargeErrorTypes() {
//
//		List<ErrorType> errorTypeList = null;
//		errorTypes = new HashMap<String, ErrorType>();
//
//		try {
//			errorTypeList = entityManager.createQuery(
//					"select et from ErrorType et ").getResultList();
//
//			for (Iterator<ErrorType> iterator = errorTypeList.iterator(); iterator
//					.hasNext();) {
//				ErrorType errorType = (ErrorType) iterator.next();
//
//				errorTypes.put(errorType.getType(), errorType);
//
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	@Transactional
//	public void persistirError(Exception ex) {
//
//		try {
//			//Se obtiene el contexto para la direccion ip de la peticion http
//			FacesContext aFacesContext = new FacesContext();
//			HttpServletRequest context = (HttpServletRequest) aFacesContext
//					.getContext().getExternalContext().getRequest();
//
//			//Se obtiene el usuario que esta autenticado cuando ocurre la excepcion
//			Usuario user = (Usuario) Component.getInstance("user",
//					ScopeType.SESSION);
//
//			//Se obtiene el modulo en el que esta trabajando el usuario
//			IActiveModule activeModule = (IActiveModule) Component.getInstance(
//					"activeModule", ScopeType.SESSION);
//
//			
//			this.ip = context != null ? context.getRemoteAddr() : null;
//			this.user = user != null ? user.getUsername() : null;
//			this.modulo = (activeModule != null && activeModule
//					.getActiveModule() != null) ? activeModule
//					.getActiveModule().getNombre() : null;
//
//			// Persite las trazas de los errores
//			persistTraces(ex);
//			entityManager.flush();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}
//
//	// Recursivamente persiste los errores y sus causas
//	private ErrorTrace persistTraces(Throwable ex) {
//
//		ErrorTrace error = new ErrorTrace();
//		String hora = sdf.format(new Date());
//		String fecha = sdf2.format(new Date());
//
//		Date fechaHora = null;
//		Date fechaDate = null;
//		try {
//			fechaHora = sdf.parse(hora);
//			fechaDate = sdf2.parse(fecha);
//		} catch (ParseException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//
//		try {
//
//			if (ex != null) {
//				error.setErrorClass(ex.getClass().toString());
//				error.setErrorType(findErrorType(ex));
//				error.setLocalizedMessage(ex.getLocalizedMessage());
//				error.setMessage(ex.getMessage());
//				error.setRootcauseClass((ex.getCause()) != null ? ex.getCause()
//						.getClass().toString() : null);
//				error.setHora(fechaHora);
//				error.setFecha(fechaDate);
//				error.setModulo(modulo);
//				error.setIpAddress(ip);
//				error.setUsername(user);
//
//				ErrorTrace rootCause = persistTraces(ex.getCause());
//				error.setErrorTrace(rootCause);
//
//				entityManager.persist(error);
//				persistStackTraces(ex.getStackTrace(), error);
//			} else
//				return null;
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//
//		return error;
//	}
//
//	private void persistStackTraces(StackTraceElement[] stackTracceElements,
//			ErrorTrace error) {
//		StackTrace stackTrace = null;
//		try {
//			for (int i = 0; i < stackTracceElements.length; i++) {
//				if (stackTracceElements[i].getClassName().contains("gehos")) {
//					stackTrace = new StackTrace();
//					stackTrace.setClassName(stackTracceElements[i]
//							.getClassName());
//					stackTrace.setErrorTrace(error);
//					stackTrace
//							.setFileName(stackTracceElements[i].getFileName());
//					stackTrace.setLineNumber(Long.parseLong(String
//							.valueOf(stackTracceElements[i].getLineNumber())));
//					stackTrace.setMethodName(stackTracceElements[i]
//							.getMethodName());
//
//					entityManager.persist(stackTrace);
//				}
//
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	private ErrorType findErrorType(Throwable ex) {
//		ErrorType errorType = null;
//		String errorSplited = ex.getClass().toString()
//				.substring(5, ex.getClass().toString().length());
//		try {
//			if (errorTypes != null)
//				errorType = errorTypes.get(errorSplited.trim());
//
//			if (errorType == null)
//				createErrorType(errorSplited.trim());
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return errorTypes.get(errorSplited.trim());
//	}
//
//	private void createErrorType(String type) {
//
//		ErrorType et = new ErrorType();
//		try {
//			String codigo = type.substring(type.lastIndexOf(".") + 1,
//					type.length());
//			et.setCodigo(codigo);
//			et.setType(type);
//			entityManager.persist(et);
//			errorTypes.put(type, et);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	public HashMap<String, ErrorType> getErrorTypes() {
//		return errorTypes;
//	}
//
//	public void setErrorTypes(HashMap<String, ErrorType> errorTypes) {
//		this.errorTypes = errorTypes;
//	}
//
//	public String getIp() {
//		return ip;
//	}
//
//	public void setIp(String ip) {
//		this.ip = ip;
//	}
//
//	public String getUser() {
//		return user;
//	}
//
//	public void setUser(String user) {
//		this.user = user;
//	}
//
//	public String getModulo() {
//		return modulo;
//	}
//
//	public void setModulo(String modulo) {
//		this.modulo = modulo;
//	}
//
//}
