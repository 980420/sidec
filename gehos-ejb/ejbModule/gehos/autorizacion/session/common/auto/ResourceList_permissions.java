package gehos.autorizacion.session.common.auto;

import gehos.autorizacion.entity.*;

import org.jboss.seam.annotations.Name;
import org.jboss.seam.framework.EntityQuery;
import java.util.List;
import java.util.Arrays;

@Name("resourceList_permissions")
public class ResourceList_permissions extends EntityQuery {

	private static final String[] RESTRICTIONS = { "lower(resource.virtualPath) like concat(lower(#{resourceList_permissions.resource.virtualPath}),'%')", };

	private Resource resource = new Resource();

	@Override
	public String getEjbql() {
		return "select resource from Resource resource";
	}

	@Override
	public Integer getMaxResults() {
		return 25;
	}

	public Resource getResource() {
		return resource;
	}

	@Override
	public List<String> getRestrictions() {
		return Arrays.asList(RESTRICTIONS);
	}

}
