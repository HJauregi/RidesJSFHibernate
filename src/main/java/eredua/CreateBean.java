package eredua;

import java.util.Date;
import java.util.List;

import org.primefaces.event.SelectEvent;

import domain.Ride;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import configuration.UtilDate;

@Named("create")
@ApplicationScoped
public class CreateBean {

	@Inject
	private CredentialsBean driver;

	private String from;
	private String to;
	private Date data;
	private int seats;
	private float price;

	private List<String> gertaerak;

	// Getters eta setters
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	// Irteera-hirien zerrenda lortu
	public List<String> getGertaerak() {
		if (FacadeBean.getBusinessLogic() != null) {
			gertaerak = FacadeBean.getBusinessLogic().getDepartCities();
		}
		return gertaerak;
	}

	// Data aukeratzean Ajax listener-ak exekutatzen duen metodoa
	public void onDateSelect(SelectEvent event) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Data aukeratua: " + event.getObject()));
	}

	// Bidaia berria sortu eta formularioa garbitu
	public String createRide() {
		try {
			String driverEmail = driver.getLoggedEmail();

			Date trimmedDate = UtilDate.trim(data);
			Ride ride = FacadeBean.getBusinessLogic().createRide(from, to, trimmedDate, seats, price, driverEmail);

			if (ride != null) {
				// Kontsolan informazioa erakutsi
				System.out.println("=== BIDAIA SORTU DA ===");
				System.out.println("Nondik: " + from);
				System.out.println("Nora: " + to);
				System.out.println("Data: " + trimmedDate);
				System.out.println("Lekuak: " + seats);
				System.out.println("Prezioa: " + price + "€");
				System.out.println("Gidaria: " + driverEmail);
				System.out.println("=======================");

				from = "";
				to = "";
				data = null;
				seats = 0;
				price = 0;

				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, 
							"Bidaia ondo sortu da", null));
				return "ok";
			} else {
				System.err.println("ERROREA: Ezin izan da bidaia sortu");
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, 
							"Ezin izan da bidaia sortu", null));
			}

		} catch (RideMustBeLaterThanTodayException e) {
			System.err.println("ERROREA: Data gaur baino beranduago izan behar da");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Data gaur baino beranduago izan behar da", null));
		} catch (RideAlreadyExistException e) {
			System.err.println("ERROREA: Bidaia hori dagoeneko existitzen da");
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, 
						"Bidaia hori dagoeneko existitzen da", null));
		}

		return null;
	}
}