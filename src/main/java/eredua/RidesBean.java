package eredua;

import java.util.List;

import org.primefaces.event.SelectEvent;

import domain.Driver;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("rides")
@ApplicationScoped
public class RidesBean {
	
	private Driver driver = new Driver("driver3@gmail.com", "pass", "Test driver", "test");
	
    public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	public String getDriverEmail(){
		return driver.getEmail();
	}

	private List<String> gertaerak;

	private String language = "en";

    public List<String> getGertaerak() {
        return gertaerak;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void onDateSelect(SelectEvent event) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data aukeratua: " + event.getObject()));
	}

	public String createRides() {
		return "create";
	}

	public String queryRides() {
		return "query";
	}
    
}