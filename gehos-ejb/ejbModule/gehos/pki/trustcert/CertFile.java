package gehos.pki.trustcert;

public class CertFile {

	private String name;
    private byte[] data;
    
   
    
    public CertFile() {
		super();
		
	}
	public byte[] getData() {
        return data;
    }
    public void setData(byte[] data) {
        this.data = data;
    }
     
    public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
	
    
    
    
}
