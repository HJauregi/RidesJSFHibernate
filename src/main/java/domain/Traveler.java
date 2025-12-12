package domain;

import java.io.Serializable;
import javax.persistence.Entity;



@Entity
public class Traveler extends User implements Serializable{

	
	public Traveler (String name, String surname, String email, String password) {
		super(name, surname, email, password);
	}
	
	public Traveler() {
		super();
	}
}
