package gehos.configuracion.management.userLogged;

import gehos.autenticacion.session.custom.UserLogged;
import gehos.comun.listadoControler.ListadoControler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

@Scope(ScopeType.CONVERSATION)
@Name("userLoggedList")
public class UserLoggedList {

	private String[] RESTRICTIONS = new String[] {
			"lower(username) like concat(lower(#{userLoggedList.userName}),'%')",
			"lower(ip) like concat(lower(#{userLoggedList.ip}),'%')" };

	private String userName;
	private String ip;
	private int pagina;
	private ListadoControler<UserLoggedData> users;

	@In
	private UserLogged userLogged;

	public ListadoControler<UserLoggedData> getUsers() {
		if (users == null) {
			refreshUsers();
		}
		return users;
	}

	public void setUsers(ListadoControler<UserLoggedData> users) {
		this.users = users;
	}

	public int getPagina() {
		if (this.getUsers().getNextFirstResult() != 0)
			return this.getUsers().getNextFirstResult() / 10;
		else
			return 1;
	}

	public void setPagina(int pagina) {
		this.pagina = pagina;

		long num = (getUsers().getResultList().size() / 10) + 1;
		if (this.pagina > 0) {
			if (getUsers().getResultList().size() % 10 != 0) {
				if (pagina <= num)
					this.getUsers().setFirstResult((this.pagina - 1) * 10);
			} else {
				if (pagina < num)
					this.getUsers().setFirstResult((this.pagina - 1) * 10);
			}
		}
	}

	public void refreshUsers() {
		List<UserLoggedData> datas = new ArrayList<UserLoggedData>();
		Set<String> user = userLogged.getUsers().keySet();
		for (String usuario : user) {
			Set<String> ips = userLogged.getUsers().get(usuario).keySet();
			for (String ip : ips) {
				UserLoggedData data = new UserLoggedData();
				data.setConections(userLogged.getUsers().get(usuario).get(ip)
						.size());
				data.setIp(ip);
				data.setUsername(usuario);
				datas.add(data);
			}
		}
		users = new ListadoControler<UserLoggedData>(datas);
		try {
			users.setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void purgeAndRefresh() {
		userLogged.purge();
		refreshUsers();
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
