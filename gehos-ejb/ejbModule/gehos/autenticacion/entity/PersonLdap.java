package gehos.autenticacion.entity;

public class PersonLdap {
	
	private String distinguishedName;
	private String fullName;
	private String firstname;
	private String lastnames;
	private String lastname1;
	private String lastname2;
	private String user;
	private String email;
	private String mobile;

	private byte[] thumbnailphoto;

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName.trim();
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname.trim();
	}

	public String getLastnames() {
		return lastnames;
	}

	private void splitLastnames() {
		if ((getLastname1()==null || getLastname1().equals("")) && (getLastname2() == null || getLastname2().equals(""))) {
			StringBuffer concat = new StringBuffer();
			String[] parts = getLastnames().split(" ");

			if (parts.length > 2 && parts[0].length() == 2) {
				concat.append(parts[0].trim()).append(" ")
						.append(parts[1].trim());
				setLastname1(concat.toString());
				concat = new StringBuffer();
				for (int i = 2; i < parts.length; i++) {
					concat.append(parts[i].trim()).append(" ");

				}

			} else {
				concat = new StringBuffer();
				concat.append(parts[0].trim());
				setLastname1(concat.toString());
				concat = new StringBuffer();
				for (int i = 1; i < parts.length; i++) {
					concat.append(parts[i].trim()).append(" ");

				}

			}
			setLastname2(concat.toString().trim());

		}

	}

	public void setLastnames(String lastnames) {
		this.lastnames = lastnames.trim();
		splitLastnames();

	}

	public String getLastname1() {
		return lastname1;
	}

	public void setLastname1(String lastname1) {
		this.lastname1 = lastname1.trim();
	}

	public String getLastname2() {
		return lastname2;
	}

	public void setLastname2(String lastname2) {
		this.lastname2 = lastname2.trim();
		if (getLastname1()!=null && getLastname1().length() > 0  && this.lastname2 != null && this.lastname2.length() > 0) {
			StringBuffer concat = new StringBuffer();
			concat.append(getLastname1()).append(" ").append(getLastname2());
			setLastnames(concat.toString());
		}
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user.trim();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email.trim();
	}

	public byte[] getThumbnailphoto() {
		return thumbnailphoto;
	}

	public void setThumbnailphoto(byte[] thumbnailphoto) {
		this.thumbnailphoto = thumbnailphoto;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile.trim();
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

}
