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
			
			// Debug: mostrar las ciudades
			if (departCities != null && !departCities.isEmpty()) {
				System.out.println("Hiriak:");
				for (String city : departCities) {
					System.out.println("  - " + city);
				}
			} else {
				System.out.println("OHARRA: Ez dago irteera-hiririk datu-basean!");
			}
			
			System.out.println("============================");
		} catch (Exception e) {
			System.err.println("ERROREA: Irteera-hiriak kargatzean");
			e.printStackTrace();
			departCities = new ArrayList<>();
		}
	}

	// Getters and Setters
	public List<String> getDepartCities() {
		if (departCities == null) {
			departCities = new ArrayList<>();
		}
		return departCities;
	}

	public void setDepartCities(List<String> departCities) {
		this.departCities = departCities;
	}

	public List<String> getArrivalCities() {
		if (arrivalCities == null) {
			arrivalCities = new ArrayList<>();
		}
		return arrivalCities;
	}

	public void setArrivalCities(List<String> arrivalCities) {
		this.arrivalCities = arrivalCities;
	}

	public List<Ride> getRides() {
		if (ridesList == null) {
			ridesList = new ArrayList<>();
		}
		return ridesList;
	}

	public void setRides(List<Ride> ridesList) {
		this.ridesList = ridesList;
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
		System.out.println("=== onDepartCityChange DEITURIK ===");
		System.out.println("Hautatutako irteera hiria: " + departCity);
		
		arrivalCity = null;
		ridesList = new ArrayList<>();
		searchPerformed = false;

		if (departCity != null && !departCity.isEmpty()) {
			try {
				arrivalCities = FacadeBean.getBusinessLogic().getDestinationCities(departCity);
				
				System.out.println("Helmuga-hiriak aurkituta: " + arrivalCities.size());
				if (arrivalCities != null && !arrivalCities.isEmpty()) {
					System.out.println("Helmuga hiriak:");
					for (String city : arrivalCities) {
						System.out.println("  - " + city);
					}
				}
				
			} catch (Exception e) {
				System.err.println("ERROREA helmuga-hiriak kargatzean: " + e.getMessage());
				e.printStackTrace();
				arrivalCities = new ArrayList<>();
			}
		} else {
			arrivalCities = new ArrayList<>();
		}
		System.out.println("===================================");
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

			try {
				ridesList = FacadeBean.getBusinessLogic().getRides(departCity, arrivalCity, rideDate);
				if (ridesList == null) {
					ridesList = new ArrayList<>();
				}

				searchPerformed = true;

				System.out.println("Emaitzak: " + ridesList.size() + " bidaia aurkitu");
			} catch (Exception e) {
				System.err.println("ERROREA bidaiak bilatzean: " + e.getMessage());
				e.printStackTrace();
				ridesList = new ArrayList<>();
			}
			
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
}