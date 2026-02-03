package gehos.ensayo.ensayo_estadisticas.session.reportes;

public class ReporteEstudioSource {
	
	
	
	String ano;
	String meses;
	String variable1;
	String variable2;
	String reporte;
	String usuario;
	String PRODUCT_IMG;
	String LOGO_URL;
	String sincluidos;
	String snoincluidos;
	String smalincluidos;
	String eevaluacion;
	String etratamiento;
	String eseguimiento;
	String einterrupcion;
	
	public ReporteEstudioSource(String ano,
			String meses, String variable1, String variable2,String reporte,
			String usuario,String PRODUCT_IMG,String LOGO_URL,String sincluidos,String snoincluidos,String smalincluidos,
			String eevaluacion,String etratamiento,String eseguimiento,String einterrupcion) {
		super();
		
		this.ano = ano;
		this.meses = meses;
		this.variable1 = variable1;
		this.variable2 = variable2;
		this.reporte = reporte;
		this.usuario = usuario;
		this.PRODUCT_IMG = PRODUCT_IMG; 
		this.LOGO_URL = LOGO_URL;
	    this.sincluidos = sincluidos;
	    this.snoincluidos = snoincluidos;
	    this.smalincluidos = smalincluidos;
	    this.eevaluacion = eevaluacion;
	    this.etratamiento = etratamiento;
	    this.eseguimiento = eseguimiento;
	    this.einterrupcion = einterrupcion;
		
		
	}
	

	






	public ReporteEstudioSource() {
		super();
		// TODO Auto-generated constructor stub
	}



	public String getReporte() {
		return reporte;
	}
	public void setReporte(String reporte) {
		this.reporte = reporte ;
	}
	public String getAno() {
		return ano;
	}
	public void setAno(String ano) {
		this.ano = ano;
	}
	public String getMeses() {
		return meses;
	}
	public void setMeses(String meses) {
		this.meses = meses;
	}
	public String getVariable1() {
		return variable1;
	}
	public void setVariable1(String variable1) {
		this.variable1 = variable1;
	}
	public String getVariable2() {
		return variable2;
	}
	public void setVariable2(String variable2) {
		this.variable2 = variable2;
	}
	public String getUsuario() {
		return usuario;
	}
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPRODUCT_IMG() {
        return PRODUCT_IMG;
    } 
	 public void setPRODUCT_IMG(String PRODUCT_IMG) {
	        this.PRODUCT_IMG = PRODUCT_IMG;
	    }   
	 public String getLOGO_URL() {
			return LOGO_URL;
		}

		public void setLOGO_URL(String lOGO_URL) {
			LOGO_URL = lOGO_URL;
		}
		public String getSincluidos() {
			return sincluidos;
		}
		public void setSincluidos(String sincluidos) {
			this.sincluidos = sincluidos;
		}
	
		public String getSnoincluidos() {
			return snoincluidos;
		}

		public void setSnoincluidos(String snoincluidos) {
			this.snoincluidos = snoincluidos;
		}
		public String getSmalincluidos() {
			return smalincluidos;
		}

		public void setSmalincluidos(String smalincluidos) {
			this.smalincluidos = smalincluidos;
		}
		public String getEevaluacion() {
			return eevaluacion;
		}
		public void setEevaluacion(String eevaluacion) {
			this.eevaluacion = eevaluacion;
		}
		public String getEtratamiento() {
			return etratamiento;
		}
		public void setEtratamiento(String etratamiento) {
			this.etratamiento = etratamiento;
		}
		public String getEseguimiento() {
			return eseguimiento;
		}
		public void setEseguimiento(String eseguimiento) {
			this.eseguimiento = eseguimiento;
		}

		public String getEinterrupcion() {
			return einterrupcion;
		}

		public void setEinterrupcion(String einterrupcion) {
			this.einterrupcion = einterrupcion;
		}



}
