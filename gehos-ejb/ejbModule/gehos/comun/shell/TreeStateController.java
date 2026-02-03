package gehos.comun.shell;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.richfaces.component.state.TreeState;

@AutoCreate
@Scope(ScopeType.SESSION)
@Name("treeStateController")
public class TreeStateController extends org.jboss.seam.framework.Controller {
	
	private static final long serialVersionUID = 4738647102748175499L;

	private TreeState funcionalidadesTreeState = new TreeState();
	private TreeState modulosTreeState = new TreeState();
	private TreeState ubicacionesTreeState = new TreeState();
	private TreeState camasTreeState = new TreeState();
	private TreeState physicalSecurityTreeState = new TreeState();
	private TreeState logicalSecurityTreeState = new TreeState();
	private TreeState bitacoraTreeState = new TreeState();
	private TreeState departamentosPorEntidadTreeState = new TreeState();
	private TreeState departamentosNoClinicosPorEntidadTreeState = new TreeState();
	private TreeState modulosPorEntidadTreeState = new TreeState();
	private TreeState departamentosClinicos = new TreeState();
	private TreeState departamentosNoClinicos = new TreeState();
	private TreeState usuariosPorEntidad = new TreeState();
	

	public TreeState getFuncionalidadesTreeState() {
		return funcionalidadesTreeState;
	}
	public void setFuncionalidadesTreeState(TreeState funcionalidadesTreeState) {
		this.funcionalidadesTreeState = funcionalidadesTreeState;
	}
	public TreeState getPhysicalSecurityTreeState() {
		return physicalSecurityTreeState;
	}
	public void setPhysicalSecurityTreeState(TreeState physicalSecurityTreeState) {
		this.physicalSecurityTreeState = physicalSecurityTreeState;
	}
	public TreeState getLogicalSecurityTreeState() {
		return logicalSecurityTreeState;
	}
	public void setLogicalSecurityTreeState(TreeState logicalSecurityTreeState) {
		this.logicalSecurityTreeState = logicalSecurityTreeState;
	}
	public TreeState getUbicacionesTreeState() {
		return ubicacionesTreeState;
	}
	public void setUbicacionesTreeState(TreeState ubicacionesTreeState) {
		this.ubicacionesTreeState = ubicacionesTreeState;
	}
	public TreeState getModulosTreeState() {
		return modulosTreeState;
	}
	public void setModulosTreeState(TreeState modulosTreeState) {
		this.modulosTreeState = modulosTreeState;
	}
	public TreeState getBitacoraTreeState() {
		return bitacoraTreeState;
	}
	public void setBitacoraTreeState(TreeState bitacoraTreeState) {
		this.bitacoraTreeState = bitacoraTreeState;
	}
	public TreeState getDepartamentosPorEntidadTreeState() {
		return departamentosPorEntidadTreeState;
	}
	public void setDepartamentosPorEntidadTreeState(
			TreeState departamentosPorEntidadTreeState) {
		this.departamentosPorEntidadTreeState = departamentosPorEntidadTreeState;
	}
	public TreeState getDepartamentosClinicos() {
		return departamentosClinicos;
	}
	public void setDepartamentosClinicos(TreeState departamentosClinicos) {
		this.departamentosClinicos = departamentosClinicos;
	}
	public TreeState getDepartamentosNoClinicos() {
		return departamentosNoClinicos;
	}
	public void setDepartamentosNoClinicos(TreeState departamentosNoClinicos) {
		this.departamentosNoClinicos = departamentosNoClinicos;
	}
	public TreeState getCamasTreeState() {
		return camasTreeState;
	}
	public void setCamasTreeState(TreeState camasTreeState) {
		this.camasTreeState = camasTreeState;
	}
	public TreeState getModulosPorEntidadTreeState() {
		return modulosPorEntidadTreeState;
	}
	public void setModulosPorEntidadTreeState(TreeState modulosPorEntidadTreeState) {
		this.modulosPorEntidadTreeState = modulosPorEntidadTreeState;
	}
	public TreeState getUsuariosPorEntidad() {
		return usuariosPorEntidad;
	}
	public void setUsuariosPorEntidad(TreeState usuariosPorEntidad) {
		this.usuariosPorEntidad = usuariosPorEntidad;
	}
	public TreeState getDepartamentosNoClinicosPorEntidadTreeState() {
		return departamentosNoClinicosPorEntidadTreeState;
	}
	public void setDepartamentosNoClinicosPorEntidadTreeState(
			TreeState departamentosNoClinicosPorEntidadTreeState) {
		this.departamentosNoClinicosPorEntidadTreeState = departamentosNoClinicosPorEntidadTreeState;
	}



	
}
