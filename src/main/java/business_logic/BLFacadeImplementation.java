package business_logic;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import data_access.HibernateDataAccess;
import domain.Driver;
import domain.Ride;
import domain.User;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.UserAlreadyRegistered;

public class BLFacadeImplementation implements BLFacade {
	private HibernateDataAccess dbManager;

	public BLFacadeImplementation(HibernateDataAccess dataAccess) {
		this.dbManager = dataAccess;
	}

	
	public void close() {
		HibernateDataAccess dB4oManager;
		dB4oManager = new HibernateDataAccess();
		dB4oManager.close();

	}

	 public void initializeBD(){
    	dbManager.open();
		dbManager.initializeDB();
		dbManager.close();
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
	public Ride createRide(String departingCity, String arrivalCity, Date rideDate, int nPlaces, double price,
			String driverEmail) throws RideAlreadyExistException, RideMustBeLaterThanTodayException {
		dbManager.open();
		Ride r = dbManager.createRide(departingCity, arrivalCity, rideDate, nPlaces, nPlaces, driverEmail);
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

	/*
	@Override
	public User register(String email, String name, String password, boolean isDriver) throws UserAlreadyRegistered {
		dataAccess.open();
		try {
			User u = dataAccess.register(email, name, password, isDriver);
			dataAccess.close();
			return u;
		} catch(UserAlreadyRegistered e) {
			dataAccess.close();
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
	*/
}