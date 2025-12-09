package domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

@Entity
public class Driver extends User implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.PERSIST)
	private List<Ride> rides=new ArrayList<Ride>();
	

	public Driver(String email, String pass, String name, String surname) {
		super(email, pass, name, surname);
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

	public String toString(){
		return super.toString()+";"+rides;
	}

	public Ride addRide(String departing, String arriving, Date date, double price, int nPlaces)  {
        Ride ride=new Ride(departing,arriving,date,nPlaces,price, this);
        rides.add(ride);
        return ride;
	}

	/**
	 * This method checks if the ride already exists for that driver
	 * 
	 * @param from the origin location 
	 * @param to the destination location 
	 * @param date the date of the ride 
	 * @return true if the ride exists and false in other case
	 */
	public boolean doesRideExists(String from, String to, Date date)  {
		for (Ride r:rides)
			if (Objects.equals(r.getDate(),date) && Objects.equals(r.getFrom(), from) && Objects.equals(r.getTo(), to))
			 return true;
		
		return false;
	}
}
