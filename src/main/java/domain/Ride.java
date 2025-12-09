package domain;

import java.io.*;
import java.util.Date;


import javax.persistence.*;


@Entity
public class Ride implements Serializable {
	@Id 
	@GeneratedValue
	private Integer rideNumber;
	private int nPlaces;
	private int eserLibre;
	private Date date;
	private String departing;
	private String arrival;
	private double price;
	
	@ManyToOne(fetch=FetchType.EAGER)
	private Driver driver;


	public int getEserLibre() {
		return eserLibre;
	}

	public void setEserLibre(int eserLibre) {
		this.eserLibre = eserLibre;
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

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public Ride(){
		super();
	}
	
	public Ride(Integer rideNumber, String departing, String arrival, Date date, int nPlaces, double price, Driver driver) {
		super();
		this.rideNumber = rideNumber;
		this.departing = departing;
		this.arrival = arrival;
		this.nPlaces = nPlaces;
		this.date=date;
		this.price=price;
		this.driver = driver;
		this.eserLibre=nPlaces;
	}

	

	public Ride(String departing, String arrival,  Date date, int nPlaces, double price, Driver driver) {
		super();
		this.departing = departing;
		this.arrival = arrival;
		this.nPlaces = nPlaces;
		this.date=date;
		this.price=price;
		this.driver = driver;
		this.eserLibre=nPlaces;
	}

	/*public Ride(List<String> hiriList, List<Double> prezioList, Date date, int nPlaces, Driver driver) {
		super();
		this.nPlaces = nPlaces;
		this.date=date;
		this.driver = driver;
		this.eserLibre=nPlaces;
		this.car=c;
		this.egoera=RideEgoera.MARTXAN;
		List<Geldialdia> geldialdiList = new LinkedList<Geldialdia>();
		for(int i=0;i<hiriList.size();i++) {
			Geldialdia g = null;
			if(i==hiriList.size()-1) {
				g = new Geldialdia(i+1,0,i==hiriList.size()-1,hiriList.get(i));
			} else {
				g = new Geldialdia(i+1,prezioList.get(i),i==hiriList.size()-1,hiriList.get(i));
			}
			geldialdiList.add(g);
		}
		this.geldialdiak=geldialdiList;
	}
	*/
	
	/**
	 * Get the  number of the ride
	 * 
	 * @return the ride number
	 */
	public Integer getRideNumber() {
		return rideNumber;
	}

	
	/**
	 * Set the ride number to a ride
	 * 
	 * @param ride Number to be set	 */
	
	public void setRideNumber(Integer rideNumber) {
		this.rideNumber = rideNumber;
	}

	/**
	 * Get the free places of the ride
	 * 
	 * @return the available places
	 */
	
	/**
	 * Get the date  of the ride
	 * 
	 * @return the ride date 
	 */
	public Date getDate() {
		return date;
	}
	/**
	 * Set the date of the ride
	 * 
	 * @param date to be set
	 */	
	public void setDate(Date date) {
		this.date = date;
	}

	
	public float getnPlaces() {
		return nPlaces;
	}

	/**
	 * Set the free places of the ride
	 * 
	 * @param  nPlaces places to be set
	 */

	public void setBetMinimum(int nPlaces) {
		this.nPlaces = nPlaces;
	}

	/**
	 * Get the driver associated to the ride
	 * 
	 * @return the associated driver
	 */
	public Driver getDriver() {
		return driver;
	}

	/**
	 * Set the driver associated to the ride
	 * 
	 * @param driver to associate to the ride
	 */
	public void setDriver(Driver driver) {
		this.driver = driver;
	}
	
	@Override
	public String toString() {
		return this.rideNumber+";"+this.date;
	}

	public boolean eserlekuakLibre(int kop) {
		if(kop<=this.eserLibre) return true;
		else return false;
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
		if (this.rideNumber==other.rideNumber)
			return true;
		return false;
	}
	
	public void itzuliEserlekuak(int kop) {
		this.eserLibre+=kop;
	}
	
	/*
	public double prezioaKalkulatu(String from, String to) {
		double kop = 0.0;
		boolean fromAurkitua=false;
		for(Geldialdia g:geldialdiak) {
			if(fromAurkitua) {
				if(g.getHiria().equals(to)) {
					break;
				}
				kop+=g.getPrezioa();
			} else if(g.getHiria().equals(from)) {
				fromAurkitua=true;
				kop+=g.getPrezioa();
			}
		}
		return kop;
	}
	*/
	
	public String getFrom() {
		return departing;
	}

	public String getTo() {
		return arrival;
	}
}
