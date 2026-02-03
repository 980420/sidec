package gehos.ensayo.ensayo_disenno.session.reglas;

public class Framework
{
	enum Family  {
		php("PHP"),
		cf("CF"),
		dotNet("DOTNET");
		String val;
		Family(String val)
		{
			this.val = val;
		}
		public String getVal() {
			return val;
		}
		public void setVal(String val) {
			this.val = val;
		}
		
	};
	String name;
	Family family;
	String fname;
	public Framework(String name, Family family, String fname) {		
		this.name = name;
		this.family = family;
		this.fname = fname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Family getFamily() {
		return family;
	}
	public void setFamily(Family family) {
		this.family = family;
	}
	public String getFname() {
		return fname;
	}
	public void setFname(String fname) {
		this.fname = fname;
	}
	
	
	
	
}
