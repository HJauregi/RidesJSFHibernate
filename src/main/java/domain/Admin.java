package domain;

import java.io.Serializable;

import javax.persistence.Entity;

@Entity
public class Admin  extends User implements Serializable{
	
	public Admin (String name, String surname, String email, String pass) {
		super(name, surname, email, pass);
	}
	
	public Admin() {
		super();
	}
}
