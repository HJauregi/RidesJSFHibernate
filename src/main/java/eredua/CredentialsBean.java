package eredua;

import domain.User;
import exceptions.UserAlreadyRegistered;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("credentials")
@SessionScoped
public class CredentialsBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String email;
	private String password;
	private String name;
	private String surname;
	private boolean isDriver;

	private String loggedEmail;
	private String loggedName;
	private boolean loggedIsDriver;
	private boolean loggedIsAdmin;


	// Erabiltzaile berria erregistratu
	public String register() {
		try {
			FacadeBean.getBusinessLogic().register(name, surname, email, password, isDriver);

			// Kontsolan informazioa erakutsi
			System.out.println("=== ERREGISTROA ONDO EGIN DA ===");
			System.out.println("Izena: " + name);
			System.out.println("Abizena: " + surname);
			System.out.println("Emaila: " + email);
			System.out.println("Mota: " + (isDriver ? "Gidaria" : "Bidaiaria"));
			System.out.println("================================");

			email = null;
			password = null;
			name = null;
			surname = null;
			isDriver = false;

			// Arrakasta mezua erakutsi
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, 
					"Erregistroa ondo egin da. Saioa hasi dezakezu orain.", null));

			return "registered";
		} catch (UserAlreadyRegistered e) {
			// Errore mezua erakutsi
			System.err.println("ERROREA: Email hori dagoeneko erregistratuta dago");
			FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, 
					"Email hori dagoeneko erregistratuta dago", null));
			return null;
		}
	}

	// Saioa hasi eta erabiltzaile mota (Driver/Traveler/Admin) itzuli
	public String login() {
	    User user = FacadeBean.getBusinessLogic().login(email, password);
	    if (user != null) {
	        loggedEmail = user.getEmail();
	        loggedName = user.getName();
	        loggedIsDriver = (user instanceof domain.Driver);
	        loggedIsAdmin = (user instanceof domain.Admin);

	        System.out.println("=== SAIOA HASI DA ===");
	        System.out.println("Erabiltzailea: " + loggedName);
	        System.out.println("Emaila: " + loggedEmail);
	        String userType = loggedIsAdmin ? "Admin" : (loggedIsDriver ? "Gidaria" : "Bidaiaria");
	        System.out.println("Mota: " + userType);
	        System.out.println("=====================");

	        email = null;
	        password = null;

	        if (user instanceof domain.Admin) {
	            return "admin";
	        } else if (user instanceof domain.Driver) {
	            return "driver";
	        } else {
	            return "traveler";
	        }
	    }

	    // Errore mezua
	    System.err.println("ERROREA: Email edo pasahitza okerra");
	    FacesContext.getCurrentInstance().addMessage(null,
	        new FacesMessage(FacesMessage.SEVERITY_ERROR,
	            "Email edo pasahitza okerra", null));

	    return null;
	}
	
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public boolean isDriver() {
		return isDriver;
	}

	public void setDriver(boolean isDriver) {
		this.isDriver = isDriver;
	}

	public String getLoggedEmail() {
		return loggedEmail;
	}

	public String getLoggedName() {
		return loggedName;
	}

	public boolean isLoggedIsDriver() {
		return loggedIsDriver;
	}
	
	public boolean isLoggedIsAdmin() {
	    return loggedIsAdmin;
	}
}