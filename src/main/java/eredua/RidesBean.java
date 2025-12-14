package eredua;

import java.util.List;

import org.primefaces.event.SelectEvent;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

@Named("rides")
@ApplicationScoped
public class RidesBean {

	private List<String> gertaerak;

    public List<String> getGertaerak() {
        return gertaerak;
    }

    // Data aukeratzean Ajax listener-ak exekutatzen duen metodoa
    public void onDateSelect(SelectEvent event) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data aukeratua: " + event.getObject()));
	}

	// Erregistro orrira bidaltzen du
	public String register() {
		return "register";
	}

	// Login orrira bidaltzen du
	public String login() {
		return "login";
	}
	
	public String hirikoBidaiak() {
		return "hirikoBidaiak";
	}

}