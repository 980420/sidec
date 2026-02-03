package gehos.comun.phaseListeners;

import gehos.comun.shell.ActiveModuleSelector;
import gehos.comun.shell.IActiveModule;

import java.util.Arrays;
import java.util.List;

import javax.faces.application.Application;
import javax.faces.application.ViewHandler;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.component.html.HtmlInputHidden;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;

import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;

public class ModuleSelectorListener implements PhaseListener {
	private static final Long serialVersionUID = 1L;

	private IActiveModule activeModule;
	private ActiveModuleSelector activeModuleSelector;

	public void afterPhase(PhaseEvent arg0) {
		FacesContext context = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
		String viewId = request.getRequestURI();

		/*
		 * if(context.getViewRoot().findComponent("moduleNameHidden") != null){
		 * UIComponent component =
		 * context.getViewRoot().findComponent("moduleNameHidden");
		 * HtmlInputHidden hidden= (HtmlInputHidden)component; String moduleName
		 * = hidden.getValue().toString(); activeModule =
		 * (IActiveModule)Component .getInstance("activeModule",
		 * ScopeType.SESSION, false, false); if(activeModule != null)
		 * activeModule.setActiveModuleName(moduleName); } else{
		 */
		if (!viewId.contains("a4j")) {
			activeModuleSelector = (ActiveModuleSelector) Component
					.getInstance("activeModuleSelector", ScopeType.SESSION,
							false, false);
			if (activeModuleSelector != null)
				activeModuleSelector.selectModuleByViewId(viewId);
		}
		/* } */
	}

	public void beforePhase(PhaseEvent event) {
		/*
		 * FacesContext context = FacesContext.getCurrentInstance();
		 * HttpServletRequest request = (HttpServletRequest)
		 * context.getExternalContext().getRequest(); String viewId =
		 * request.getRequestURI();
		 * 
		 * if (viewId.contains("modules")) { List<String> resultList =
		 * Arrays.asList(viewId.split("/")); int modules =
		 * resultList.indexOf("modules"); String substitude =
		 * resultList.get(modules) + "/" + resultList.get(modules + 1); String
		 * appPath = context.getExternalContext().getRequestContextPath();
		 * String result = viewId.replace(substitude,
		 * "codebase").replace(appPath, "");
		 * 
		 * Application app = context.getApplication(); ViewHandler viewHandler =
		 * app.getViewHandler();
		 * 
		 * UIViewRoot viewRoot = viewHandler.createView(context, result);
		 * 
		 * UIComponent component =
		 * app.createComponent(HtmlInputHidden.COMPONENT_TYPE); HtmlInputHidden
		 * hidden= (HtmlInputHidden)component; hidden.setId("moduleNameHidden");
		 * hidden.setValue(resultList.get(modules + 1));
		 * viewRoot.getChildren().add(hidden);
		 * 
		 * context.setViewRoot(viewRoot); context.renderResponse(); }
		 */
	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}

}
