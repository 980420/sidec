package gehos.ensayo.ensayo_disenno.session.reglas.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

public class FileSigec 
{
	String name;
	int size; //in bytes
	String type;
	
	public int SizeKB()
	{
		return size/1024;
	}
	
	public String Extension()
	{
		String ext = "";
		int dotIndex = name.lastIndexOf(".");
		if(dotIndex!=-1)
		{
			ext = name.substring(dotIndex);
		}
		return ext;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}	
	
	public String valueAsjsonString()
	{
		String json = "{}";
		try 
		{
			json = new ObjectMapper().writeValueAsString(this);
		} 
		catch (Exception e){
			e.printStackTrace();
		} 
		return json;
	}
	
}
