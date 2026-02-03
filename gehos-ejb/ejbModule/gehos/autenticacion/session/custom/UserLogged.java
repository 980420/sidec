package gehos.autenticacion.session.custom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.AutoCreate;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;

@Name("userLogged")
@Scope(ScopeType.APPLICATION)
@AutoCreate
public class UserLogged {

	private HashMap<String, HashMap<String, List<SessionTime>>> users = new HashMap<String, HashMap<String, List<SessionTime>>>();
	private boolean needUpdate = false;

	public HashMap<String, HashMap<String, List<SessionTime>>> getUsers() {
		return users;
	}

	public void setUsers(
			HashMap<String, HashMap<String, List<SessionTime>>> users) {
		this.users = users;
	}

	public synchronized void registerUser(String un, String ipString,
			String userAgent) {
		if (!users.containsKey(un)) {
			HashMap<String, List<SessionTime>> userMaph = new HashMap<String, List<SessionTime>>();
			List<SessionTime> agents = new ArrayList<SessionTime>();
			agents.add(new SessionTime(userAgent));
			userMaph.put(ipString, agents);
			users.put(un, userMaph);
		} else {
			if (users.get(un).containsKey(ipString)) {
				List<SessionTime> agents = users.get(un).get(ipString);
				if (!agents.contains(new SessionTime(userAgent))) {
					agents.add(new SessionTime(userAgent));
					users.get(un).remove(ipString);
					users.get(un).put(ipString, agents);
				}

			} else {
				List<SessionTime> agents = new ArrayList<SessionTime>();
				agents.add(new SessionTime(userAgent));
				users.get(un).put(ipString, agents);
			}
		}
	}

	public synchronized void unregisterUser(String un, String userAgent) {
		Set<String> ips = users.get(un).keySet();
		for (String ip : ips) {
			List<SessionTime> agents = users.get(un).get(ip);
			if(agents.contains(new SessionTime(userAgent))){
				agents.remove(new SessionTime(userAgent));
				users.get(un).remove(ip);
				if(agents.isEmpty()){
					if(users.get(un).size() == 0){
						users.remove(un);
					}
				}
				else{				
					users.get(un).put(ip, agents);
				}
				break;
			}			
		}
		if(needUpdate && users.isEmpty()){
			try {
				String action = SeamResourceBundle.getBundle().getString("updateAddress");
				System.out.println("Runing: " + action);
				Runtime.getRuntime().exec(action);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void purge() {

		Date time = Calendar.getInstance().getTime();

		for (Iterator<String> usersI = users.keySet().iterator(); usersI.hasNext();) {
			String user = (String) usersI.next();
			Set<String> ipsList = users.get(user).keySet();
			Iterator<String> ips = ipsList.iterator();
			while (ips.hasNext()) {
				Boolean deleteIps = false;

				List<SessionTime> sessionList = users.get(user).get(ips.next());
				Iterator<SessionTime> sessions = sessionList.iterator();
				while (sessions.hasNext()) {
					SessionTime session = sessions.next();
					long tics = time.getTime() - session.getDate().getTime();
					if (tics > 7200000L ) {
						sessions.remove();
						if (sessionList.size() == 0) {
							deleteIps = true;
						}
					}
				}
				if (deleteIps) {
					ips.remove();
					if (ipsList.size() == 0) {
						usersI.remove();
					}
				}
			}
		}
	}

	public void updateSessionLastTime(String userName, String id) {
		Set<String> ips = users.get(userName).keySet();
		for (String ip : ips) {
			List<SessionTime> agents = users.get(userName).get(ip);
			if (agents.contains(new SessionTime(id))) {
				SessionTime session = agents.get(agents
						.indexOf(new SessionTime(id)));
				session.setDate(Calendar.getInstance().getTime());
			}
		}
	}

	public boolean isNeedUpdate() {
		return needUpdate;
	}

	public void setNeedUpdate(boolean needUpdate) {
		this.needUpdate = needUpdate;
	}

	

}
