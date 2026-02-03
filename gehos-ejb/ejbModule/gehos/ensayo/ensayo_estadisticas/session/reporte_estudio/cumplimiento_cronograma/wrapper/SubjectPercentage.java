package gehos.ensayo.ensayo_estadisticas.session.reporte_estudio.cumplimiento_cronograma.wrapper;

/**
 * Wrapper to use to be sent to the report 'Porciento cumplimiento'
 * @author yoandry
 *
 */
public class SubjectPercentage 
{
	String name;
	String study;
	String datestart;
	String dateend;
	String momentcount;
	String percent;	
	
	
	
	public SubjectPercentage(String name, String study, String datestart,
			String dateend, String momentcount, String percent) {
		this.name = name;
		this.study = study;
		this.datestart = datestart;
		this.dateend = dateend;
		this.momentcount = momentcount;
		this.percent = percent;
	}

	public SubjectPercentage(String name, String percent) 
	{
		this.name = name;
		this.percent = percent;
	}

	public SubjectPercentage() {
		// TODO Auto-generated constructor stub
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPercent() {
		return percent;
	}

	public void setPercent(String percent) {
		this.percent = percent;
	}

	public String getStudy() {
		return study;
	}

	public void setStudy(String study) {
		this.study = study;
	}

	public String getDatestart() {
		return datestart;
	}

	public void setDatestart(String datestart) {
		this.datestart = datestart;
	}

	public String getDateend() {
		return dateend;
	}

	public void setDateend(String dateend) {
		this.dateend = dateend;
	}

	public String getMomentcount() {
		return momentcount;
	}

	public void setMomentcount(String momentcount) {
		this.momentcount = momentcount;
	}
	
	
}
