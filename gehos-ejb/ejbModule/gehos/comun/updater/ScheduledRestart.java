package gehos.comun.updater;

import gehos.comun.reglas.parser.RulesDirectoryBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

import javax.ejb.MessageDriven;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;
import org.jboss.annotation.ejb.ResourceAdapter;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.async.IntervalCron;
import org.jboss.seam.async.QuartzTriggerHandle;
import org.jboss.seam.core.SeamResourceBundle;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleTrigger;
import org.quartz.impl.StdSchedulerFactory;

@MessageDriven(name = "scheduledRestart")
@ResourceAdapter("quartz-ra.rar")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class ScheduledRestart  implements Job {	
	
	@SuppressWarnings("unchecked")
	@Transactional
	public void execute(JobExecutionContext jobContext) throws JobExecutionException {
		JobDataMap dataMap = jobContext.getMergedJobDataMap();
		try {			
			Date fecha = (Date)dataMap.get("fecha");
			if(Calendar.getInstance().getTime().getTime() - fecha.getTime() <= 86400000){
				String ip = dataMap.get("ip").toString();
				String keys = KeysGenerator.keyString();
				String propertyPath2 = System.getProperty("jboss.server.home.dir")
						+ "/deploy/gehos-ear.ear/gehos.war/WEB-INF/classes/modulos/modComun/modComun2_es.properties";
				propertyPath2 = propertyPath2.startsWith("file:/") ? propertyPath2.substring(6) : propertyPath2;
				String line= "";
				try{
					Scanner scan = new Scanner(new File(propertyPath2), "UTF-8");
					line = scan.nextLine();
					while(!line.startsWith("updateAddress") && scan.hasNextLine()){
						System.out.println("Address Line: " + line);
						line = scan.nextLine();
					}
				}
				catch (Exception e) {
					System.out.print(e.getMessage());
				}
				System.out.println("Address Line: " + line);
				String action = line.substring(line.indexOf("=") + 1).replace("\\\\", "*").replace("\\", "").replace("*", "\\");
				action = action.replace("{keys}", KeysGenerator.keyString());
				action = action.replace("{ip}", ip);
				SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
				action = action.replace("{programedDate}", "\"" + dateformat.format(fecha) + "\"");
				System.out.println("Runing: " + action);
				Runtime.getRuntime().exec(action);
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}
