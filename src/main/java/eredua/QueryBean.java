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
	private List<Ride> allRides;

	
	private String selectedDeparting;

	private boolean searchPerformed;

	// Bean hasieratzen denean irteera-hiriak kargatu
	@PostConstruct
	public void init() {
		System.out.println("=== QUERYBEAN HASIERATZEN ===");

		departCities = new ArrayList<>();
		arrivalCities = new ArrayList<>();
		ridesList = new ArrayList<>();
		searchPerformed = false;

		try {
			departCities = FacadeBean.getBusinessLogic().getDepartCities();
			System.out.println("Irteera-hiriak kargatuta: " + departCities.size());
			System.out.println("============================");
		} catch (Exception e) {
			System.err.println("ERROREA: Irteera-hiriak kargatzean");
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

	// Irteera-hiria aldatzean helmuga-hiriak kargatu
	public void onDepartCityChange() {
		arrivalCity = null;
		ridesList = new ArrayList<>();
		searchPerformed = false;

		if (departCity != null && !departCity.isEmpty()) {
			System.out.println("=== HELMUGA-HIRIAK KARGATZEN ===");
			System.out.println("Irteera hiria: " + departCity);
			arrivalCities = FacadeBean.getBusinessLogic().getDestinationCities(departCity);
			System.out.println("Helmuga-hiriak aurkituta: " + arrivalCities.size());
			System.out.println("=================================");
		} else {
			arrivalCities = new ArrayList<>();
		}
	}

	// Data hautatzean bidaiak bilatu
	public void onDateSelect() {
		searchRides();
	}

	// Bidaiak bilatu hautatutako parametroekin
	public void searchRides() {
		if (departCity != null && !departCity.isEmpty() &&
		    arrivalCity != null && !arrivalCity.isEmpty() &&
		    rideDate != null) {

			rideDate = clearTime(rideDate);

			System.out.println("=== BIDAIAK BILATZEN ===");
			System.out.println("Nondik: " + departCity);
			System.out.println("Nora: " + arrivalCity);
			System.out.println("Data: " + new SimpleDateFormat("yyyy-MM-dd").format(rideDate));

			ridesList = FacadeBean.getBusinessLogic().getRides(departCity, arrivalCity, rideDate);
			if (ridesList == null)
				ridesList = new ArrayList<>();

			searchPerformed = true;

			System.out.println("Emaitzak: " + ridesList.size() + " bidaia aurkitu");
			System.out.println("========================");
		}
	}

	// Datatik ordua kendu (ordu, minutu eta segundoak 0-ra jarri)
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

	// Hautatutako data formatu goxoan itzuli
	public String getSelectedDateFormatted() {
		if (rideDate == null)
			return "";
		return new SimpleDateFormat("MMMM dd, yyyy").format(rideDate);
	}
	
	
	
	public void onDEpartCitySelect() {
		searchPerformed = false;

		if (departCity != null && !departCity.isEmpty()) {
			System.out.println("=== BIDAI GUZTIAK KARGATZEN ===");
			System.out.println("Irteera hiria: " + departCity);
			allRides = FacadeBean.getBusinessLogic().getAllRides(departCity);
			System.out.println("Bidaiak aurkituta: " + allRides.size());
			System.out.println("=================================");
		} else {
			arrivalCities = new ArrayList<>();
		}
	}
}