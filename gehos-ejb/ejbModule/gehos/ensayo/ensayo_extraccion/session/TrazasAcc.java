package gehos.ensayo.ensayo_extraccion.session;

public class TrazasAcc {

	String valor_antes;
	String valor_despues;
	String id_trazaMOD;
	String entidad;
	String id_Entidad;
	String atributo;
	
	public TrazasAcc(String valor_antes, String valor_despues, 
			String id_trazaMOD, String entidad, String id_Entidad, String atributo) {
		super();
		
		this.valor_antes = valor_antes;
		this.valor_despues = valor_despues;		
		this.id_trazaMOD = id_trazaMOD;
		this.entidad = entidad;
		this.id_Entidad = id_Entidad;
		this.atributo = atributo;
				
		}

	
	public TrazasAcc() {
		super();
		// TODO Auto-generated constructor stub
	}


	public String getValor_antes() {
		return valor_antes;
	}


	public void setValor_antes(String valor_antes) {
		this.valor_antes = valor_antes;
	}


	public String getValor_despues() {
		return valor_despues;
	}


	public void setValor_despues(String valor_despues) {
		this.valor_despues = valor_despues;
	}


	public String getId_trazaMOD() {
		return id_trazaMOD;
	}


	public void setId_trazaMOD(String id_trazaMOD) {
		this.id_trazaMOD = id_trazaMOD;
	}


	public String getEntidad() {
		return entidad;
	}


	public void setEntidad(String entidad) {
		this.entidad = entidad;
	}


	public String getId_Entidad() {
		return id_Entidad;
	}


	public void setId_Entidad(String id_Entidad) {
		this.id_Entidad = id_Entidad;
	}


	public String getAtributo() {
		return atributo;
	}


	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}
}
