package business_logic;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import data_access.HibernateDataAccess;
import domain.Driver;
import domain.Erreserba;
import domain.ErreserbaData;
import domain.Ride;
import domain.Traveler;
import domain.User;
import exceptions.*;

public class BLFacadeImplementation implements BLFacade {
	private HibernateDataAccess dbManager;

	public BLFacadeImplementation(HibernateDataAccess dataAccess) {
		this.dbManager = dataAccess;
	}
    
	@Override
	public List<String> getDepartCities() {
		dbManager.open();
		List<String> departCities = dbManager.getDepartCities();
		dbManager.close();
		return departCities;
	}

	@Override
	public List<String> getDestinationCities(String departingCity) {
		dbManager.open();
		List<String> arrivalCities = dbManager.getArrivalCities(departingCity);
		dbManager.close();
		return arrivalCities;
	}

	@Override
	public List<Ride> getRides(String departingCity, String arrivalCity, Date rideDate) {
		dbManager.open();
		List<Ride> rides = dbManager.getRidesByValues(departingCity, arrivalCity, rideDate);
		dbManager.close();
		return rides;
	}

	@Override
	public List<Date> getThisMonthDatesWithRides(String from, String to, Date date) {
		dbManager.open();
		List<Date> dates = dbManager.getThisMonthDatesWithRides(from, to, date);
		dbManager.close();
		return dates;
	}

	@Override
	public Ride createRide(String departingCity, String arrivalCity, Date rideDate, int nPlaces, float price,
			String driverEmail) throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		dbManager.open();
		Ride r = dbManager.createRide(departingCity, arrivalCity, rideDate, nPlaces, price, driverEmail);
		dbManager.close();
		return r;
	}


	@Override
	public Driver getDriver(String email) {
		dbManager.open();
		Driver d = dbManager.getDriver(email);
		dbManager.close();
		return d;
	}

	
	@Override
	public User register(String name, String surname, String email, String password, boolean isDriver) throws UserAlreadyRegistered {
		dbManager.open();
		try {
			User u = dbManager.register(name, surname, email, password, isDriver);
			dbManager.close();
			return u;
		} catch(UserAlreadyRegistered e) {
			dbManager.close();
			throw e;
		}
	}
	
	
	@Override
	public User login(String email, String password) {
		dbManager.open();
		User u = dbManager.login(email, password);
		dbManager.close();
		return u;
	}

	@Override
	public void open() {
	    dbManager.open();
	}

	@Override
	public void close() {
	    dbManager.close();
	}
	
	@Override
	public void dropDB() {
		dbManager.dropDB();
	}
	
	@Override
	public boolean sortuErreserba(Traveler t, ErreserbaData eData) throws
	EserlekurikLibreEzException, ErreserbaAlreadyExistsException, DiruaEzDaukaException, DatuakNullException {
		dbManager.open();
		boolean b = dbManager.sortuErreserba(t, eData);
		dbManager.close();
		return b;
	}

	@Override
	public Traveler getTraveler(String currentUserEmail) {
		return dbManager.getTraveler(currentUserEmail);
	}
	
	@Override
	public void updateTraveler(Traveler traveler) {
	    dbManager.open();
	    dbManager.updateTraveler(traveler);
	    dbManager.close();
	}
	
	@Override
	public List<Erreserba> getBookingsByRide(Integer rideNumber) {
	    dbManager.open();
	    List<Erreserba> bookings = dbManager.getBookingsByRide(rideNumber);
	    dbManager.close();
	    return bookings;
	}

	@Override
	public void updateDriver(Driver driver) {
		dbManager.updateDriver(driver);
	}
}