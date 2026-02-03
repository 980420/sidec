package gehos.ensayo.ensayo_conduccion.gestionarCRD.custom;

import java.util.ArrayList;
import java.util.List;

import org.richfaces.model.UploadItem;

import gehos.ensayo.entity.Variable_ensayo;

public class VariableWrapper {
	
	private Variable_ensayo variable;
	private Object valor;
	private Object[] valores;
	
	private String nombreFichero;
	private byte[] data;
	String fileSizeString;
	
	List<UploadItem> fileItems = new ArrayList<UploadItem>();
	
	public VariableWrapper(Variable_ensayo variable, Object valor, Object[] valores, String nombreFichero, byte[] data) {
		super();
		this.variable = variable;
		this.valor = valor;
		this.valores = valores;
		this.nombreFichero = nombreFichero;
		this.data = data;
		if(data!=null)
			this.fileSizeString = ConvertSize(data.length);
	}
	
	public static String ConvertSize(int bytes)
	{
		String unit = "B";
		int divisor = 1024;
		if(bytes>divisor)
		{	
			bytes = bytes/divisor;
			unit = "KB";
		}
		if(bytes>divisor)
		{	
			bytes = bytes/divisor;
			unit = "MB";
		}
		if(bytes>divisor)
		{	
			bytes = bytes/divisor;
			unit = "GB";
		}
		return Math.round(bytes * 10)/10+unit;
	}
	
	public boolean IsChexBox(){
		return variable.getPresentacionFormulario().getNombre().equals("checkbox"); 
	}

	public Variable_ensayo getVariable() {
		return variable;
	}


	public void setVariable(Variable_ensayo variable) {
		this.variable = variable;
	}


	public Object getValor() {
		return valor;
	}


	public void setValor(Object valor) {
		this.valor = valor;
	}


	public Object[] getValores() {
		return valores;
	}


	public void setValores(Object[] valores) {
		this.valores = valores;
	}

	public String getNombreFichero() {
		return nombreFichero;
	}

	public void setNombreFichero(String nombreFichero) {
		this.nombreFichero = nombreFichero;
	}
	
	/**
	 * Devuelve el nombre del fichero sin el id de la variable como prefijo
	 * @return
	 */
	public String getNombreFicheroLimpio() {
		if(nombreFichero==null)
			return null;
		String out = nombreFichero;
		out = out.replaceFirst(String.valueOf(variable.getId()), "");
		return out;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
		if(data!=null)
			this.fileSizeString = ConvertSize(data.length);
	}

	public String getFileSizeString() {
		return fileSizeString;
	}

	public void setFileSizeString(String fileSizeString) {
		this.fileSizeString = fileSizeString;
	}
}
