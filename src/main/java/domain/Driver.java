package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class Driver extends User implements Serializable {

	private static final long serialVersionUID = 1L;

    @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Ride> rides = new ArrayList<Ride>();

	public Driver(String name, String surname, String email, String pass) {
		super(name, surname, email, pass);
	}

	public Driver() {
		super();
	}

	public List<Ride> getRides() {
		return rides;
	}

	public void setRides(List<Ride> rides) {
		this.rides = rides;
	}

	public String toString() {
		return super.toString() + ";" + rides;
	}

	public Ride addRide(String departing, String arriving, Date date, int nPlaces, float price) {
		Ride ride = new Ride(departing, arriving, date, nPlaces, price, this);
		rides.add(ride);
		return ride;
	}

	/**
	 * This method checks if the ride already exists for that driver
	 *
	 * @param from the origin location
	 * @param to   the destination location
	 * @param date the date of the ride
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesRideExists(String from, String to, Date date) {
		for (Ride r : rides)
			if (Objects.equals(r.getDate(), date) && Objects.equals(r.getFrom(), from) && Objects.equals(r.getTo(), to))
				return true;

		return false;
	}
}