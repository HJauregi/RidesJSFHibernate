package domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "erreserba")
public class Erreserba implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue
	private Integer eskaeraNum;
	private int plazaKop;
	private Date erreserbaData;
	private String departing;
	private String arrival;
	private double prezioa;

	@ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
	private Traveler bidaiaria;

	@ManyToOne(fetch = FetchType.EAGER)
	private Ride ride;

	public Erreserba() {
		super();
	}

	public Erreserba(int kop, Traveler bidaiaria, Ride ride, String from, String to, double prezioa) {
		this.plazaKop = kop;
		this.bidaiaria = bidaiaria;
		this.ride = ride;
		this.erreserbaData = new Date();
		this.departing = from;
		this.arrival = to;
		this.prezioa = prezioa;
	}

	public boolean containsRide(Ride r) {
		return (this.ride.equals(r));
	}

	// Getters y Setters
	public Integer getEskaeraNum() {
		return eskaeraNum;
	}

	public void setEskaeraNum(Integer eskaeraNum) {
		this.eskaeraNum = eskaeraNum;
	}

	public int getPlazaKop() {
		return plazaKop;
	}

	public void setPlazaKop(int plazaKop) {
		this.plazaKop = plazaKop;
	}

	public Date getErreserbaData() {
		return erreserbaData;
	}

	public void setErreserbaData(Date erreserbaData) {
		this.erreserbaData = erreserbaData;
	}

	public Traveler getBidaiaria() {
		return bidaiaria;
	}

	public String getBidaiariaEmail() {
		return this.bidaiaria.getEmail();
	}

	public void setBidaiaria(Traveler bidaiaria) {
		this.bidaiaria = bidaiaria;
	}

	public Ride getRide() {
		return ride;
	}

	public void setRide(Ride ride) {
		this.ride = ride;
	}

	public String getFrom() {
		return departing;
	}

	public void setFrom(String from) {
		this.departing = from;
	}

	public String getTo() {
		return arrival;
	}

	public void setTo(String to) {
		this.arrival = to;
	}

	public double getPrezioa() {
		return prezioa;
	}

	public void setPrezioa(double prezioa) {
		this.prezioa = prezioa;
	}

	public Date getRideDate() {
		return this.ride.getDate();
	}

	@Override
	public String toString() {
		return eskaeraNum + " " + plazaKop + " " + erreserbaData + " " + bidaiaria.getEmail();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Erreserba other = (Erreserba) obj;
		return this.eskaeraNum.equals(other.eskaeraNum);
	}
}