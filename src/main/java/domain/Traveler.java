package domain;

import java.io.Serializable;
import javax.persistence.Entity;



@Entity
public class Traveler extends User implements Serializable{

	
	public Traveler (String email, String password, String name, String surname) {
		super(email, password, name, surname);
	}
	
	public Traveler() {
		super();
	}
}
