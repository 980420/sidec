package gehos.ensayo.ensayo_disenno.session.reglas;

public class KeyValue 
{
	String key, value;
	
	public KeyValue(String k, String v) {
		this.key = k;
		this.value = v;
	}

	public static KeyValue create(String k, String v)
	{
		return new KeyValue(k,v);
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	
}
