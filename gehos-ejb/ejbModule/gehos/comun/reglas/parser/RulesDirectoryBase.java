package gehos.comun.reglas.parser;

public enum RulesDirectoryBase {
	business_rules("business_rules"),
	business_processes("business_processes");
	
	private String directorio;
	private RulesDirectoryBase(String directorio) {
		this.directorio = directorio;
	}
	public String getDirectorio() {
		return directorio;
	}
	public void setDirectorio(String directorio) {
		this.directorio = directorio;
	}
}
