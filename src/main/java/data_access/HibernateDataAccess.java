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

	public void open() {
		db = JPAUtil.getEntityManager();
	}

	public void close() {
		if (db != null && db.isOpen()) {
			db.close();
		}
	}

	public List<String> getDepartCities() {
		TypedQuery<String> query = db.createQuery("SELECT DISTINCT r.departing FROM Ride r ORDER BY r.departing", String.class);
		return query.getResultList();
	}

	public List<String> getArrivalCities(String departing) {
		TypedQuery<String> query = db.createQuery(
				"SELECT DISTINCT r.arrival FROM Ride r WHERE r.departing=:from ORDER BY r.arrival",
				String.class);
		query.setParameter("from", departing);
		return query.getResultList();
	}

	public List<Ride> getRidesByValues(String departing, String arrival, Date rideDate) {
		if (departing == null || arrival == null || rideDate == null)
			return new ArrayList<Ride>();
		try {
			return db.createQuery(
					"SELECT r FROM Ride r WHERE r.departing=:departing AND r.arrival=:arrival AND r.date=:rideDate",
					Ride.class)
					.setParameter("departing", departing)
					.setParameter("arrival", arrival)
					.setParameter("rideDate", rideDate)
					.getResultList();
		} catch (Exception e) {
			System.out.println("Error en getRidesByValues: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<Ride>();
		}
	}

	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
	    
	    Date startDate = UtilDate.firstDayMonth(date);
	    Date endDate = UtilDate.lastDayMonth(date);

	    String jpql = "SELECT DISTINCT r.date FROM Ride r " +
	                  "WHERE r.departing = :fromCity " +
	                  "AND r.arrival = :toCity " +
	                  "AND r.date BETWEEN :startDate AND :endDate";
	    
	    TypedQuery<Date> query = db.createQuery(jpql, Date.class);
	    query.setParameter("fromCity", from);
	    query.setParameter("toCity", to);
	    query.setParameter("startDate", startDate);
	    query.setParameter("endDate", endDate);

	    return query.getResultList();
	}

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
		Ride ride = driver.addRide(from, to, date, price, nPlaces);
		db.persist(driver);
		db.getTransaction().commit();
		
		System.out.println("✓ BIDAIA SORTUTA: " + from + " → " + to + " (" + date + ") - " + price + "€");
		
		return ride;
	}

	public User register(String email, String name, String surname, String password, boolean isDriver) throws UserAlreadyRegistered {
		if (email == null || name == null || password == null)
			return null;
		try {
			db.getTransaction().begin();
			User u = db.find(User.class, email);
			if (u != null) {
				db.getTransaction().rollback();
				throw new UserAlreadyRegistered("Already exists a user with the same email");
			}
			User newUser;
			if (isDriver) {
				newUser = new Driver(email, password, name, surname);
			} else {
				newUser = new Traveler(email, password, name, surname);
			}
			db.persist(newUser);
			db.getTransaction().commit();
			return newUser;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		}
	}
	
	public User login(String email, String password) {
	    try {
	        if (email == null || password == null) return null;

	        TypedQuery<Driver> driverQuery = db.createQuery(
	            "SELECT d FROM Driver d WHERE d.email=:email AND d.password=:password", Driver.class);
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
	
	public Driver getDriver(String email) {
		return db.find(Driver.class, email);
	}
	
	public void initializeDB(){
		
		db.getTransaction().begin();

		try {
			// Egiaztatu ea jadanik datuak badauden
			Long driverCount = db.createQuery("SELECT COUNT(d) FROM Driver d", Long.class)
					.getSingleResult();
			
			if (driverCount > 0) {
				System.out.println("Datu-basea jadanik hasieratuta dago (" + driverCount + " driver)");
				db.getTransaction().rollback();
				return;
			}
			
			System.out.println("Datu-basea hutsik dago, hasieratzen...");

			Calendar today = Calendar.getInstance();
			
			int month=today.get(Calendar.MONTH);
			int year=today.get(Calendar.YEAR);
			if (month==12) { month=1; year+=1;}  
			
			//Create drivers 
			Driver driver1=new Driver("driver1@gmail.com","abc123", "Ane", "Gaztañaga");
			Driver driver2 = new Driver("1","1","1","1");
			Driver driver3 = new Driver("driver3@gmail.com", "pass", "Test driver", "test");
			
			//Create traveler
			Traveler traveler1 = new Traveler("2","2","2","2");
			Traveler traveler2 = new Traveler("3","3","3","3");
			
			Date data = new Date();
			Calendar cal = Calendar.getInstance();
			cal.set(2025, 5, 20);
			data = cal.getTime();
			Ride r = driver2.addRide("Donostia", "Bilbo", UtilDate.trim(data), 4.99, 5);
						
			Ride r2 = driver2.addRide("Usurbil", "Donosti", UtilDate.trim(data), 2.99, 5);
			
			db.persist(driver1);
			db.persist(driver2);
			db.persist(driver3);
			db.persist(traveler1);
			db.persist(traveler2);
			db.getTransaction().commit();
			
			System.out.println("Db initialized");
		}
		catch (Exception e){
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.err.println("Errorea datu-basea hasieratzerakoan: " + e.getMessage());
			e.printStackTrace();
		}
	}
}