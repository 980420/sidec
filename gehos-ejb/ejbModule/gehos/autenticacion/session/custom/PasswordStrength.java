package gehos.autenticacion.session.custom;

public class PasswordStrength {
	
	private int passwordLifeCicle;
	private int passwordLength;
	private String passwordStrengthType;
	private String passwordStrengthRegex;

	public int getPasswordLifeCicle() {
		return passwordLifeCicle;
	}

	public void setPasswordLifeCicle(int passwordLifeCicle) {
		this.passwordLifeCicle = passwordLifeCicle;
	}
	
	public int getPasswordLength() {
		return passwordLength;
	}

	public void setPasswordLength(int passwordLength) {
		this.passwordLength = passwordLength;
	}
	
	/*Weaker, Weak, Medium, Strong*/
	public String getPasswordStrengthType() {
		return passwordStrengthType;
	}

	public void setPasswordStrengthType(String passwordStrengthType) {
		this.passwordStrengthType = passwordStrengthType;
	}
	
	public String getPasswordStrengthRegex() {
		return passwordStrengthRegex;
	}

	public void setPasswordStrengthRegex(String passwordStrengthRegex) {
		this.passwordStrengthRegex = passwordStrengthRegex;
	}
}
