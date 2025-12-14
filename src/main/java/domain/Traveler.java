package domain;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

@Entity
public class Traveler extends User implements Serializable {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "bidaiaria", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Erreserba> bookedRides = new Vector<Erreserba>();

	public Traveler(String name, String surname, String email, String password) {
		super(name, surname, email, password);
	}

	public Traveler() {
		super();
	}

	public List<Erreserba> getBookedRides() {
		return bookedRides;
	}

	public void setBookedRides(List<Erreserba> bookedRides) {
		this.bookedRides = bookedRides;
	}

	public boolean existBook(Ride r) {
		for (Erreserba erre : bookedRides) {
			if (erre.containsRide(r))
				return true;
		}
		return false;
	}

	public boolean diruaDauka(double kop) {
		return (this.getCash() >= kop);
	}

	public Erreserba sortuErreserba(Ride r, int eserKop, String from, String to, float prezioa) {
	    this.diruaAtera(prezioa); 
	    Erreserba erre = new Erreserba(eserKop, this, r, from, to, prezioa);
	    bookedRides.add(erre);
	    return erre;
	}
}