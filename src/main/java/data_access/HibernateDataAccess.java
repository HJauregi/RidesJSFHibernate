package data_access;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import domain.Driver;
import domain.Ride;
import domain.Traveler;
import domain.User;
import configuration.JPAUtil;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.UserAlreadyRegistered;
import configuration.UtilDate;

public class HibernateDataAccess {
	private EntityManager db;

	// Datu-basearen konexioa ireki
	public void open() {
		db = JPAUtil.getEntityManager();
	}

	// Datu-basearen konexioa itxi
	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	// Irteera-hiri guztiak lortu
	public List<String> getDepartCities() {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.departing FROM Ride r ORDER BY r.departing",
				String.class);
		return query.getResultList();
	}

	// Helmuga-hiri guztiak lortu irteera-hiri jakin baterako
	public List<String> getArrivalCities(String departing) {
		TypedQuery<String> query = db.createQuery(
				"SELECT DISTINCT r.arrival FROM Ride r WHERE r.departing=:from ORDER BY r.arrival", String.class);
		query.setParameter("from", departing);
		return query.getResultList();
	}

	// Bidaiak lortu irteera, helmuga eta dataren arabera
	public List<Ride> getRidesByValues(String departing, String arrival, Date rideDate) {
		if (departing == null || arrival == null || rideDate == null)
			return new ArrayList<Ride>();
		try {
			return db.createQuery(
					"SELECT r FROM Ride r WHERE r.departing=:departing AND r.arrival=:arrival AND r.date=:rideDate",
					Ride.class).setParameter("departing", departing).setParameter("arrival", arrival)
					.setParameter("rideDate", rideDate).getResultList();
		} catch (Exception e) {
			System.out.println("Error en getRidesByValues: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<Ride>();
		}
	}

	// Hilabete zehatz bateko bidaiak dituzten datak lortu
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {

		Date startDate = UtilDate.firstDayMonth(date);
		Date endDate = UtilDate.lastDayMonth(date);

		String jpql = "SELECT DISTINCT r.date FROM Ride r " + "WHERE r.departing = :fromCity "
				+ "AND r.arrival = :toCity " + "AND r.date BETWEEN :startDate AND :endDate";

		TypedQuery<Date> query = db.createQuery(jpql, Date.class);
		query.setParameter("fromCity", from);
		query.setParameter("toCity", to);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);

		return query.getResultList();
	}

	// Bidaia berria sortu gidari batentzat
	public Ride createRide(String from, String to, Date date, int nPlaces, float price, String driverEmail)
			throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		if (from == null || to == null || date == null || nPlaces <= 0 || price < 0 || driverEmail == null)
			return null;
		if (new Date().compareTo(date) > 0) {
			throw new RideMustBeLaterThanTodayException("Ride date must be later than today");
		}
		db.getTransaction().begin();
		Driver driver = db.find(Driver.class, driverEmail);
		if (driver == null) {
			System.out.println("Driver ez da aurkitu");
			db.getTransaction().rollback();
			return null;
		}
		if (driver.doesRideExists(from, to, date)) {
			db.getTransaction().rollback();
			throw new RideAlreadyExistException("Driver already has a equal ride");
		}
		Ride ride = driver.addRide(from, to, date, nPlaces, price);
		db.persist(driver);
		db.getTransaction().commit();

		System.out.println(" BIDAIA SORTUTA: " + from + " -> " + to + " (" + date + ") - " + price + "€");

		return ride;
	}

	// Erabiltzaile berria erregistratu (Driver edo Traveler)
	public User register(String name, String surname, String email, String password, boolean isDriver) throws UserAlreadyRegistered {
	    if (email == null || name == null || password == null)
	        return null;
	    try {
	        db.getTransaction().begin();
	        User u = db.find(Driver.class, email);
	        if (u == null) {
	            u = db.find(Traveler.class, email);
	        }
	        if (u != null) {
	            db.getTransaction().rollback();
	            throw new UserAlreadyRegistered("Already exists a user with the same email");
	        }
	        
	        if (isDriver) {
	            Driver newDriver = new Driver(name, surname, email, password);
	            db.persist(newDriver);
	            db.getTransaction().commit();
	            return newDriver;
	        } else {
	            Traveler newTraveler = new Traveler(name, surname, email, password);
	            db.persist(newTraveler);
	            db.getTransaction().commit();
	            return newTraveler;
	        }
	    } catch (UserAlreadyRegistered e) {
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	        throw e;
	    } catch (Exception e) {
	        if (db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	        e.printStackTrace();
	        return null;
	    }
	}

	// Erabiltzailearen saioa hasi email eta pasahitzarekin
	public User login(String email, String password) {
		try {
			if (email == null || password == null)
				return null;

			TypedQuery<Driver> driverQuery = db
					.createQuery("SELECT d FROM Driver d WHERE d.email=:email AND d.password=:password", Driver.class);
			driverQuery.setParameter("email", email);
			driverQuery.setParameter("password", password);

			try {
				Driver driver = driverQuery.getSingleResult();
				return driver;
			} catch (NoResultException e) {
				TypedQuery<Traveler> travelerQuery = db.createQuery(
						"SELECT t FROM Traveler t WHERE t.email=:email AND t.password=:password", Traveler.class);
				travelerQuery.setParameter("email", email);
				travelerQuery.setParameter("password", password);

				try {
					Traveler traveler = travelerQuery.getSingleResult();
					return traveler;
				} catch (NoResultException e2) {
					return null;
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	// Driver bat lortu email-aren bidez
	public Driver getDriver(String email) {
		return db.find(Driver.class, email);
	}
	
	/*
	public void dropDB() {
	    try {
	        if (db == null || !db.isOpen()) {
	            open();
	        }
	        
	        db.getTransaction().begin();
	        
	        db.createNativeQuery("DROP TABLE IF EXISTS traveler").executeUpdate();
	        db.createNativeQuery("DROP TABLE IF EXISTS driver_ride").executeUpdate();
	        db.createNativeQuery("DROP TABLE IF EXISTS ride").executeUpdate();
	        db.createNativeQuery("DROP TABLE IF EXISTS driver").executeUpdate();
	        db.createNativeQuery("DROP TABLE IF EXISTS hibernate_sequence").executeUpdate();
	        
	        db.getTransaction().commit();
	        
			System.out.println("=== DATU BASEA DROPEATUTA ===");
			System.out.println("=================================");

	        
	    } catch (Exception e) {
	        if (db != null && db.getTransaction().isActive()) {
	            db.getTransaction().rollback();
	        }
	        System.out.println("Error dropping database tables: " + e.getMessage());
	        e.printStackTrace();
	    }
	}
	*/
}