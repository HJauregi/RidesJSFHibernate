package domain;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class User implements Serializable {
	@Id
	private String email;
	private String password;
	private String name;
	private String surname;
	private float cash;


	public User(String name, String surname, String email, String pass) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = pass;
		this.cash = 0;
	}

	public User() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String pass) {
		this.password = pass;
	}

	public String toString() {
		return email + ";" + name + ";" + "surname";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (!getEmail().equals(other.getEmail()))
			return false;
		return true;
	}

	public boolean diruaAtera(float kop) {
		if (this.cash - kop >= 0.0) {
			this.cash = this.cash - kop;
			return true;
		} else {
			return false;
		}
	}

	public boolean diruaSartu(float kop) {
		if(kop>=0.0) {
			this.cash=this.cash+kop;
			return true;
		} else {
			return false;
		}
	}

	public float getCash() {
		return cash;
	}

	public void setCash(float cash) {
		this.cash = cash;
	}

	public void addCash(double amount) {
		if(amount > 0.0) {
			this.cash += amount;
		}
	}
}