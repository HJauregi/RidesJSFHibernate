package domain;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.persistence.*;

@Entity
public class Ride implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue
	private Integer rideNumber;
	private int nPlaces;
	private int eserLibre;
	private Date date;
	private String departing;
	private String arrival;
	private float price;

	@ManyToOne(fetch = FetchType.EAGER)
	private Driver driver;

	@OneToMany(mappedBy = "ride", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Erreserba> erreserbak = new Vector<Erreserba>();

	public Ride() {
		super();
	}

	public Ride(Integer rideNumber, String departing, String arrival, Date date, int nPlaces, float price, Driver driver) {
		super();
		this.rideNumber = rideNumber;
		this.departing = departing;
		this.arrival = arrival;
		this.nPlaces = nPlaces;
		this.date = date;
		this.price = price;
		this.driver = driver;
		this.eserLibre = nPlaces;
	}

	public Ride(String departing, String arrival, Date date, int nPlaces, float price, Driver driver) {
		super();
		this.departing = departing;
		this.arrival = arrival;
		this.nPlaces = nPlaces;
		this.date = date;
		this.price = price;
		this.driver = driver;
		this.eserLibre = nPlaces;
	}

	public Integer getRideNumber() {
		return rideNumber;
	}

	public void setRideNumber(Integer rideNumber) {
		this.rideNumber = rideNumber;
	}

	public int getnPlaces() {
		return nPlaces;
	}

	public void setnPlaces(int nPlaces) {
		this.nPlaces = nPlaces;
	}

	public int getEserLibre() {
		return eserLibre;
	}

	public void setEserLibre(int eserLibre) {
		this.eserLibre = eserLibre;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getDeparting() {
		return departing;
	}

	public void setDeparting(String departing) {
		this.departing = departing;
	}

	public String getArrival() {
		return arrival;
	}

	public void setArrival(String arrival) {
		this.arrival = arrival;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public List<Erreserba> getErreserbak() {
		return erreserbak;
	}

	public void setErreserbak(List<Erreserba> erreserbak) {
		this.erreserbak = erreserbak;
	}

	public String getFrom() {
		return departing;
	}

	public String getTo() {
		return arrival;
	}

	public void gehituErreserba(Erreserba e) {
		this.erreserbak.add(e);
		this.eserLibre -= e.getPlazaKop();
	}

	public boolean eserlekuakLibre(int kop) {
		return kop <= this.eserLibre;
	}

	public void itzuliEserlekuak(int kop) {
		this.eserLibre += kop;
	}

	public void setBetMinimum(int nPlaces) {
		this.nPlaces = nPlaces;
	}

	@Override
	public String toString() {
		return this.rideNumber + ";" + this.date;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ride other = (Ride) obj;
		if (this.rideNumber == null || other.rideNumber == null)
			return false;
		return this.rideNumber.equals(other.rideNumber);
	}
}