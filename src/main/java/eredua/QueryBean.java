package eredua;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import domain.Ride;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;

@Named("queryRides")
@ViewScoped
public class QueryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String departCity;
	private String arrivalCity;
	private Date rideDate;

	private List<String> departCities;
	private List<String> arrivalCities;
	private List<Ride> ridesList;

	private boolean searchPerformed;

	@PostConstruct
	public void init() {
		System.out.println("=== QueryBean hasieratzen ===");
		
		// Hasieratu zerrendak
		departCities = new ArrayList<>();
		arrivalCities = new ArrayList<>();
		ridesList = new ArrayList<>();
		searchPerformed = false;
		
		// Kargatu irteera-hiriak
		try {
			departCities = FacadeBean.getBusinessLogic().getDepartCities();
			System.out.println("Irteera-hiriak kargatuta: " + departCities.size());
		} catch (Exception e) {
			System.err.println("Errorea irteera-hiriak kargatzean: " + e.getMessage());
			e.printStackTrace();
		}
	}

	// Getters and Setters
	public List<String> getDepartCities() {
		return departCities;
	}

	public List<String> getArrivalCities() {
		return arrivalCities;
	}

	public List<Ride> getRides() {
		return ridesList;
	}

	public String getDepartCity() {
		return departCity;
	}

	public void setDepartCity(String departCity) {
		this.departCity = departCity;
	}

	public String getArrivalCity() {
		return arrivalCity;
	}

	public void setArrivalCity(String arrivalCity) {
		this.arrivalCity = arrivalCity;
	}

	public Date getRideDate() {
		return rideDate;
	}

	public void setRideDate(Date rideDate) {
		this.rideDate = rideDate;
	}

	public boolean isSearchPerformed() {
		return searchPerformed;
	}

	// Irteera hiria aldatzean helmuga-hiria lortzeko metodoa
	public void onDepartCityChange() {
		arrivalCity = null;
		ridesList = new ArrayList<>();
		searchPerformed = false;

		if (departCity != null && !departCity.isEmpty()) {
			arrivalCities = FacadeBean.getBusinessLogic().getDestinationCities(departCity);
		} else {
			arrivalCities = new ArrayList<>();
		}
	}

	// Data hautatzean exekutatuko den metodoa datu horietako rides-ak lortzeko
	public void onDateSelect() {
		searchRides();
	}

	// Bidaien bilaketa egiteko metodoa
	public void searchRides() {
		if (departCity != null && !departCity.isEmpty() && 
		    arrivalCity != null && !arrivalCity.isEmpty() && 
		    rideDate != null) {

			rideDate = clearTime(rideDate);

			ridesList = FacadeBean.getBusinessLogic().getRides(departCity, arrivalCity, rideDate);
			if (ridesList == null)
				ridesList = new ArrayList<>();

			searchPerformed = true;
			
			System.out.println("Bilaketa eginda: " + departCity + " → " + arrivalCity + 
			                   " (" + rideDate + ") - " + ridesList.size() + " bidaia aurkitu");
		}
	}

	// Dataetik ordu, minutu eta segundoak garbitzeko metodoa
	public Date clearTime(Date date) {
		if (date == null)
			return null;
		
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return cal.getTime();
	}

	// Hautatutako data formateatzeko metodoa
	public String getSelectedDateFormatted() {
		if (rideDate == null)
			return "";
		return new SimpleDateFormat("MMMM dd, yyyy").format(rideDate);
	}
}