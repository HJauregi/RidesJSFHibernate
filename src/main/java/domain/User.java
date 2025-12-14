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
	private double cash;
	private double frozenMoney;


	public User(String name, String surname, String email, String pass) {
		this.name = name;
		this.surname = surname;
		this.email = email;
		this.password = pass;
		this.cash = 0;
		this.setFrozenMoney(0);
	}

	public double getFrozenMoney() {
		return frozenMoney;
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

	public boolean diruaAtera(double kop) {
		if (this.cash - kop >= 0.0) {
			this.cash = this.cash - kop;
			return true;
		} else {
			return false;
		}
	}

	public boolean diruaSartu(double kop) {
		if(kop>=0.0) {
			this.cash=this.cash+kop;
			return true;
		} else {
			return false;
		}
	}

	public double getCash() {
		return cash;
	}

	public void setCash(double cash) {
		this.cash = cash;
	}

	public void setFrozenMoney(double frozenMoney) {
		this.frozenMoney = frozenMoney;
	}

	public void addFrozenMoney(double kop) {
		if(kop>0.0) this.frozenMoney+=kop;
	}

	public void removeFrozenMoney(double kop) {
		if(kop>0.0) this.frozenMoney-=kop;
	}

	// ← AÑADIR ESTE MÉTODO
	public void addCash(double amount) {
		if(amount > 0.0) {
			this.cash += amount;
		}
	}
}