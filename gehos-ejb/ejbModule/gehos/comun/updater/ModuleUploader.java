package gehos.comun.updater;

import gehos.autenticacion.session.custom.UserLogged;
import gehos.comun.funcionalidades.entity.Funcionalidad;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;

@Scope(ScopeType.PAGE)
@Name("moduleUploader")
public class ModuleUploader {

	private byte[] data;
	private String fileName;
	
	private TreeNode<String> tree;
	
	@In
	private FacesMessages facesMessages;
	@In
	private UserLogged userLogged;
	@In
	private EntityManager entityManager;
	@In(value = "#{remoteAddr}", required = false)
	private String ipString;
	
	
	public String getKey1(){
		return KeysGenerator.nowKeysString()[0];
	}
	
	public String getKey2(){
		return KeysGenerator.nowKeysString()[1];
	}
	
	private String keyVal1;
	private String keyVal2;	
	
	public String getKeyVal1() {
		return keyVal1;
	}

	public void setKeyVal1(String keyVal1) {
		this.keyVal1 = keyVal1;
	}

	public String getKeyVal2() {
		return keyVal2;
	}

	public void setKeyVal2(String keyVal2) {
		this.keyVal2 = keyVal2;
	}

	public void upload() throws Exception{
		String historyPath = SeamResourceBundle.getBundle().getString("history");
		String updatePath = SeamResourceBundle.getBundle().getString("update");
		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		String date = formater.format(Calendar.getInstance().getTime());
		File folder = new File(historyPath + "/" + date);
		if(!folder.exists()){
			folder.mkdir();
		}
		File file = new File(historyPath + "/" + date + "/" + fileName);
		FileOutputStream fileOutputStream = new FileOutputStream(file);
		DataOutputStream dataOutputStream = new DataOutputStream(
				fileOutputStream);
		dataOutputStream.write(this.data);
		dataOutputStream.close();
		fileOutputStream.close();
		unZip(historyPath + "/" + date + "/" + fileName, updatePath);
		facesMessages.add(SeamResourceBundle.getBundle().getString("uploaded").replace("{1}", fileName));
		tree = null;
		userLogged.setNeedUpdate(true);
	}
	
	
	private void unZip(String module, String path) throws Exception{
        ZipFile zipFile = new ZipFile(module);
        Enumeration entries = zipFile.entries();
        String moduleName = "";
        while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();
	
	        if(entry.isDirectory()) {
	          continue;
	        }
	
	        String name = entry.getName();
	        name = name.replace("/", "\\");
	        name = name.substring(name.indexOf("\\") + 1);        
	        if(!name.startsWith("fuentes") && !name.startsWith("reveng")){ 
	        	String name2 = name;
	            String auxName = name.replace("\\", "/");
	            String folderName = "";
	            while(name.contains("\\")){
	                folderName += folderName.length() > 0 ? "/" : "";
	                folderName += name.substring(0, name.indexOf("\\"));
	                name = name.substring(name.indexOf("\\") + 1);
	                File folder = new File(path + folderName);
	                if(!folder.exists()){
	                    folder.mkdir();
	                }
	            }
	            copyInputStream(zipFile.getInputStream(entry),
	            new BufferedOutputStream(new FileOutputStream(path + auxName)));
	            if(name2.startsWith("paginas") && name2.contains("\\")){
	            	name2 = name2.substring(name2.indexOf("\\") + 1);
	            	if(name2.contains("\\")){
	            		name2 = name2.substring(0, name2.indexOf("\\"));
	            	}
	            	moduleName = name2;
	            }
	        }
	        
	      }
        
        if(!moduleName.equals("")){
        	List<Funcionalidad> submodules = (List<Funcionalidad>)entityManager.createQuery(
        			"select f from Funcionalidad f where f.url like :name " +
        			"and f.esModulo = true " +
        			"and f.moduloFisico = true"
        			).setParameter("name", "/" + moduleName + "%").getResultList();
        	for (Funcionalidad mod : submodules) {
        		Funcionalidad tipo = mod.getFuncionalidadPadre();
        		String modpath = tipo.getCodebase().substring(0,
        				tipo.getCodebase().indexOf("codebase"));
        		modpath = SeamResourceBundle.getBundle().getString("update")
        				+ "/paginas/" + modpath;
        		modpath = modpath + "/modules/" + mod.getNombre();

        		String base = tipo.getCodebase().substring(0,
        				tipo.getCodebase().indexOf("codebase"));
        		base = SeamResourceBundle.getBundle().getString("update")
        				+ "/paginas/" + base;
        		File template = new File(base + "/template/");
        		File target = new File(modpath);

        		FileUtils.copyDirectory(template, target);
			}
        }
    }
    
	private void copyInputStream(InputStream in, OutputStream out) throws IOException{
        byte[] buffer = new byte[1024];
        int len;

        while((len = in.read(buffer)) >= 0)
        out.write(buffer, 0, len);

        in.close();
        out.close();
    }


	public byte[] getData() {
		return data;
	}


	public void setData(byte[] data) {
		this.data = data;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	
	public TreeNode<String> getTree(){
		if(tree == null){
			tree = new TreeNodeImpl<String>();
			tree.setData(SeamResourceBundle.getBundle().getString("root"));
			String updatePath = SeamResourceBundle.getBundle().getString("update");
			File up = new File(updatePath);
			addNode(tree, up);
		}
		return tree;
	}
	
	private void addNode(TreeNode<String> node, File folder){
		node.setData(folder.getName());
		for (File file : folder.listFiles()) {
			TreeNode<String> auxNode = new TreeNodeImpl<String>();
			if(file.isDirectory()){
				addNode(auxNode, file);
			}
			else{
				auxNode.setData(file.getName());
			}
			node.addChild(file.getPath(), auxNode);
		}
	}
	
	public void restart(){
		try {
			if(KeysGenerator.validateKeys(keyVal1, keyVal2)){
				String action = SeamResourceBundle.getBundle().getString("updateAddress");
				action = action.replace("{keys}", KeysGenerator.keyString());
				action = action.replace("{ip}", ipString);
				SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				action = action.replace("{programedDate}", "\"" + dateformat.format(Calendar.getInstance().getTime()) + "\"");
				System.out.println("Runing: " + action);
				Runtime.getRuntime().exec(action);
			}
			else{
				facesMessages.add(SeamResourceBundle.getBundle().getString("invalidKey"));
			}
			cleanKeys();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@In
	private ScheduledManagement scheduledManagement;
	
	private String hora = "3";
	private String minuto = "00";
	private String am = "AM";	
	
	public String getHora() {
		return hora;
	}


	public void setHora(String hora) {
		this.hora = hora;
	}


	public String getMinuto() {
		return minuto;
	}


	public void setMinuto(String minuto) {
		this.minuto = minuto;
	}


	public String getAm() {
		return am;
	}


	public void setAm(String am) {
		this.am = am;
	}


	public List<String> getHoras(){
		List<String> horas = new ArrayList<String>();
		for (int i = 1; i <= 12; i++) {
			horas.add(Integer.toString(i));
		}
		return horas;
	}
	
	public List<String> getMinutos(){
		List<String> minutos = new ArrayList<String>();
		for (int i = 0; i < 60; i += 5) {
			minutos.add(i < 10 ? "0" + Integer.toString(i) : Integer.toString(i));
		}
		return minutos;
	}
	
	public List<String> getAms(){
		List<String> ams = new ArrayList<String>();
		ams.add("AM");
		ams.add("PM");
		return ams;
	}
	
	private void cleanKeys(){
		keyVal1 = "";
		keyVal2 = "";
	}
	
	public void programRestart(){
		try {
			if(KeysGenerator.validateKeys(keyVal1, keyVal2)){
				createProcess(hora, minuto, am);
				scheduledManagement.setHora(hora + ":" + minuto + " " + am);
			}
			else{
				facesMessages.add(SeamResourceBundle.getBundle().getString("invalidKey"));
			}
			cleanKeys();
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void createProcess(String hora, String minuto, String am) throws SchedulerException{
		Calendar cal = Calendar.getInstance();
		int horaInt = Integer.parseInt(hora);
		if(am.equals("PM")){
			horaInt += 12;
		}
		else if(horaInt == 12){
			horaInt = 0;
		}
		cal.set(Calendar.HOUR_OF_DAY, horaInt);
		cal.set(Calendar.MINUTE, Integer.parseInt(minuto));
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		if(Calendar.getInstance().getTime().getTime() > cal.getTime().getTime()){
			cal.add(Calendar.DAY_OF_MONTH, 1);
		}
		Date inicio = cal.getTime();
		cal.add(Calendar.HOUR, 1);
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		sched.start();
		JobDetail jd = new JobDetail("scheduledRestart", sched.DEFAULT_GROUP, ScheduledRestart.class);
		JobDataMap dataMap = new JobDataMap();
		dataMap.put("ip", ipString);
		dataMap.put("hora", hora);
		dataMap.put("minuto", minuto);
		dataMap.put("fecha", Calendar.getInstance().getTime());
		jd.setJobDataMap(dataMap);
		SimpleTrigger st = new SimpleTrigger("restartTrigger", sched.DEFAULT_GROUP, inicio, null, 0, 0L);
		sched.scheduleJob(jd, st);
	}
	
	public void cancelRestart() throws SchedulerException{
		SchedulerFactory sf = new StdSchedulerFactory();
		Scheduler sched = sf.getScheduler();
		sched.start();
		sched.unscheduleJob("restartTrigger", sched.DEFAULT_GROUP);
		scheduledManagement.setHora(null);
	}
}
