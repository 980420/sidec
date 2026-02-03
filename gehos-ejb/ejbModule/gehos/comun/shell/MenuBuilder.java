package gehos.comun.shell;

import gehos.autorizacion.management.logical.LogicPermissionResolver;
import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.component.html.HtmlGraphicImage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;

import org.ajax4jsf.component.html.HtmlAjaxOutputPanel;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.ui.component.html.HtmlDiv;
import org.jboss.seam.ui.component.html.HtmlLabel;
import org.richfaces.component.html.HtmlSimpleTogglePanel;

@Name(value = "menuBuilder")
public class MenuBuilder {

	@In(scope = ScopeType.SESSION)
	IActiveModule activeModule;
	@In
	FacesContext facesContext;
	@In("logicPermissionResolver")
	LogicPermissionResolver permissionResolver;

	private HtmlDiv divmenu;
	private Funcionalidad iconsContainer;

	public void llenarDiv(List<Funcionalidad> listF) {
		divmenu.setId("menuLateral");
		for (int i = 0; i < listF.size(); i++) {
			if (permissionResolver.currentUserCanSeeThisFunctionality(listF
					.get(i), activeModule.getActiveModule(), true)) {
				if (listF.get(i).getEliminado() == null
						|| listF.get(i).getEliminado() == false)
					divmenu.getChildren().add(recDiv(listF.get(i), 0));
			}
		}
	}

	public String processUrl(String url) {
		if (url.indexOf("codebase") < 0)
			return url;
		List<String> resultList = Arrays.asList(activeModule.getActiveModule()
				.getUrl().split("/"));
		int modules = resultList.indexOf("modules");
		if (modules < 0)
			return url;
		String substitude = resultList.get(modules) + "/"
				+ resultList.get(modules + 1);
		String result = url.replace("codebase", substitude);
		return result;
	}

	public UIComponent recDiv(Funcionalidad f, int padding) {
		String labelText = f.getLabel();
		if (labelText.length() > (17 + (45 - padding) / 5)) {
			if (f.getUrl().indexOf("selector") == -1)
				labelText = labelText.substring(0, 15 + (45 - padding) / 5);
			else
				labelText = labelText.substring(0, 13 + (45 - padding) / 5);
			labelText += "...";
		}
		if (f.getUrl().indexOf("selector") < 0) {
			HtmlAjaxOutputPanel item = new HtmlAjaxOutputPanel();
			HtmlLabel lab = new HtmlLabel();
			HtmlGraphicImage image = new HtmlGraphicImage();

			String real_url = this.processUrl(f.getUrl());

			String action = "redirectMenu(event,'"
					+ facesContext.getExternalContext().getRequestContextPath()
					+ real_url + "?idancestors=" + f.getId() + "a');";
			lab.setOnclick(action);
			item.setOnclick(action);
			item.setLayout("block");
			lab.setStyleClass("labelPanelStyle");
			image.setUrl("/resources/funcionalidadesIcons/"
					+ iconsContainer.getNombre() + "/" + f.getImagen());
			image.setWidth("16");
			image.setHeight("16");
			image.setStyle("vertical-align:bottom;");
			lab.setTitle(f.getLabel());
			lab.setValue(labelText);
			item.setTitle(f.getLabel());
			item.getChildren().add(image);
			item.getChildren().add(lab);
			item.setStyleClass("divPanelStyle");
			item.setStyle("padding-left:" + (padding + 4) + "px !important;");

			return item;
		}

		HtmlSimpleTogglePanel group = new HtmlSimpleTogglePanel();
		HtmlAjaxOutputPanel item = new HtmlAjaxOutputPanel();
		if (f.getFuncionalidadPadre() != null) {
			group.setHeaderClass("phstyle");
			item.setStyle("padding-left:" + (padding + 4) + "px !important;");
		} else {
			group.setHeaderClass("panelHeaderStyle");
		}

		group.setSwitchType("client");
		if (activeModule.getCategoriesExpanded().containsKey(
				"" + f.getId() + ""))
			group.setOpened(true);
		else
			group.setOpened(false);
		group.setOnclick("event.cancelBubble=true; sendexpandorcollapse('"
				+ f.getId() + "')");
		group.setStyle("border:0px;margin:0px;");
		HtmlGraphicImage image = new HtmlGraphicImage();
		image.setUrl("/resources/funcionalidadesIcons/"
				+ iconsContainer.getNombre() + "/" + f.getImagen());
		image.setWidth("16");
		image.setHeight("16");
		image.setStyle("vertical-align:bottom;");
		item.getChildren().add(image);
		HtmlLabel lab = new HtmlLabel();
		// String labelId = lab.setId("lbl" + f.getId()
		// + new Long(new Date().getTime()).toString());
		group.setOnexpand("document.getElementById('"
				+ lab.getClientId(facesContext)
				+ "').setAttribute('style', 'font-style:italic')");
		group.setOncollapse("document.getElementById('"
				+ lab.getClientId(facesContext)
				+ "').setAttribute('style', 'font-style:normal')");
		if (group.isOpened())
			lab.setStyle("font-style:italic");
		lab.setValue(labelText);
		lab.setTitle(f.getLabel());
		String action = "redirectMenu(event,'"
				+ facesContext.getExternalContext().getRequestContextPath()
				+ "/modCommons/funSelector/selector.gehos"
				// + f.getUrl()
				+ "?moduleName=" + activeModule.getActiveModuleName()
				+ "&idFuncionalidad=" + f.getId() + "&idancestors=" + f.getId()
				+ "a');";
		lab.setOnclick(action);
		lab.setStyleClass("labelPanelStyle");
		item.setTitle(f.getLabel());
		item.getChildren().add(lab);

		group.getFacets().put("header", item);
		group.setBodyClass("panelStyle");
		group.setStyleClass("panelStyle");

		EntityManager entityManager = (EntityManager) Component.getInstance(
				"entityManager", true);
		f = entityManager.merge(f);

		ArrayList<Funcionalidad> hijos = new ArrayList<Funcionalidad>(f
				.getFuncionalidadesHijas());
		for (int i = 0; i < hijos.size(); i++) {
			if (permissionResolver.currentUserCanSeeThisFunctionality(hijos
					.get(i), activeModule.getActiveModule(), true)) {
				if (hijos.get(i).getEliminado() == null
						|| hijos.get(i).getEliminado() == false)
					group.getChildren().add(recDiv(hijos.get(i), padding + 15));
			}
		}

		return group;
	}

	public HtmlDiv getDivmenu() {
		divmenu = new HtmlDiv();
		if (this.activeModule.getActiveModule() != null) {
			if (this.activeModule.getActiveModule().getModuloFisico()) {
				List<Funcionalidad> list = this.activeModule.getModuleMenu();
				iconsContainer = this.activeModule.getActiveModule();
				while (!iconsContainer.getContenedorIconos())
					iconsContainer = iconsContainer.getFuncionalidadPadre();
				this.llenarDiv(list);
			}
		}
		return divmenu;
	}

	public void setDivmenu(HtmlDiv divmenu) {
		this.divmenu = divmenu;
	}

}
