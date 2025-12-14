package data_access;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import domain.Admin;
import domain.Driver;
import domain.Erreserba;
import domain.ErreserbaData;
import domain.Ride;
import domain.Traveler;
import domain.User;
import configuration.JPAUtil;
import exceptions.DatuakNullException;
import exceptions.DiruaEzDaukaException;
import exceptions.ErreserbaAlreadyExistsException;
import exceptions.EserlekurikLibreEzException;
import exceptions.RideAlreadyExistException;
import exceptions.RideMustBeLaterThanTodayException;
import exceptions.UserAlreadyRegistered;
import configuration.UtilDate;

public class HibernateDataAccess {
	private EntityManager db;

	// Datu-basearen konexioa ireki
	public void open() {
		db = JPAUtil.getEntityManager();
		initializeAdmin();
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
	public User register(String name, String surname, String email, String password, boolean isDriver)
			throws UserAlreadyRegistered {
		if (email == null || name == null || password == null)
			return null;

		if ("admin".equals(email)) {
			throw new UserAlreadyRegistered("Cannot register with admin email");
		}

		try {
			db.getTransaction().begin();

			User u = db.find(Admin.class, email);
			if (u == null) {
				u = db.find(Driver.class, email);
			}
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

			TypedQuery<Admin> adminQuery = db
					.createQuery("SELECT a FROM Admin a WHERE a.email=:email AND a.password=:password", Admin.class);
			adminQuery.setParameter("email", email);
			adminQuery.setParameter("password", password);

			try {
				Admin admin = adminQuery.getSingleResult();
				return admin;
			} catch (NoResultException e) {
				TypedQuery<Driver> driverQuery = db.createQuery(
						"SELECT d FROM Driver d WHERE d.email=:email AND d.password=:password", Driver.class);
				driverQuery.setParameter("email", email);
				driverQuery.setParameter("password", password);

				try {
					Driver driver = driverQuery.getSingleResult();
					return driver;
				} catch (NoResultException e2) {
					TypedQuery<Traveler> travelerQuery = db.createQuery(
							"SELECT t FROM Traveler t WHERE t.email=:email AND t.password=:password", Traveler.class);
					travelerQuery.setParameter("email", email);
					travelerQuery.setParameter("password", password);

					try {
						Traveler traveler = travelerQuery.getSingleResult();
						return traveler;
					} catch (NoResultException e3) {
						return null;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
	}

	// Driver bat lortu email-aren bidez
	public Driver getDriver(String email) {
		try {
			if (db == null || !db.isOpen()) {
				open();
			}

			Driver driver = db.find(Driver.class, email);

			if (driver != null) {
				driver.getRides().size();
			}

			return driver;

		} catch (Exception e) {
			System.err.println("Error getting driver: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public void dropDB() {
		try {
			if (db == null || !db.isOpen()) {
				open();
			}

			db.getTransaction().begin();

			db.createNativeQuery("DROP TABLE IF EXISTS erreserba").executeUpdate();
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
			e.printStackTrace();
		}
	}

	// Defektuzko admin erabiltzailea sortzeko
	public void initializeAdmin() {
		try {
			db.getTransaction().begin();

			Admin existingAdmin = db.find(Admin.class, "admin");

			if (existingAdmin == null) {
				Admin defaultAdmin = new Admin("Admin", "Admin", "admin", "admin");
				db.persist(defaultAdmin);

				System.out.println("=== DEFEKTUZKO ADMINISTRATZAILEA SORTUTA ===");
				System.out.println("Email: admin");
				System.out.println("Password: admin");
				System.out.println("================================");
			}

			db.getTransaction().commit();
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.out.println("Error initializing admin: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	//Erreserba sortzeko metodoa
	public boolean sortuErreserba(Traveler t, ErreserbaData erreData) throws EserlekurikLibreEzException,
			ErreserbaAlreadyExistsException, DiruaEzDaukaException, DatuakNullException {

		if (erreData == null) {
			throw new DatuakNullException("ErreserbaData ez da baliozkoa");
		}

		if (erreData.kop <= 0) {
			throw new DatuakNullException("Eserleku kopuruak 0 baino handiagoa izan behar du");
		}

		if (db == null || !db.isOpen()) {
			open();
		}

		db.getTransaction().begin();
		try {
			Ride r = db.find(Ride.class, erreData.rNumber);
			Traveler tr = db.find(Traveler.class, t.getEmail());

			if (r == null || tr == null) {
				db.getTransaction().rollback();
				throw new DatuakNullException("Bidaia edo bidaiaria ez da aurkitu");
			}

			System.out.println("=== ERRESERBA PROZESUA DATA ACCESS-EN ===");
			System.out.println("Bidaia: " + r.getRideNumber() + " (" + r.getFrom() + " -> " + r.getTo() + ")");
			System.out.println("Eserleku eskaerak: " + erreData.kop);
			System.out.println("Eserleku libreak ORAIN: " + r.getnPlaces());
			System.out.println("=========================================");

			boolean result = erreserbaSortuEtaGehitu(erreData.kop, erreData.from, erreData.to, r, tr);
			return result;

		} catch (EserlekurikLibreEzException | DiruaEzDaukaException | ErreserbaAlreadyExistsException e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			throw e;
		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.err.println("Errorea sortuErreserba deitzean: " + e.getMessage());
			e.printStackTrace();
			throw new DatuakNullException("Errorea erreserba sortzean: " + e.getMessage());
		}
	}

	//Erreserba sortzeko metodo laguntzailea
	private boolean erreserbaSortuEtaGehitu(int kop, String from, String to, Ride r, Traveler tr)
			throws EserlekurikLibreEzException, DiruaEzDaukaException, ErreserbaAlreadyExistsException {

		if (kop <= 0) {
			throw new IllegalArgumentException("Eserleku kopuruak 0 baino handiagoa izan behar du");
		}

		if (!tr.existBook(r)) {
			float prezioTotala = r.getPrice() * kop;

			System.out.println("=== PREZIOA KALKULATZEN ===");
			System.out.println("Eserleku bakoitzeko prezioa: " + r.getPrice() + "€");
			System.out.println("Eserleku kopurua: " + kop);
			System.out.println("Prezio totala: " + prezioTotala + "€");
			System.out.println("Bidaiariaren dirua: " + tr.getCash() + "€");
			System.out.println("===========================");

			if (tr.getCash() >= prezioTotala) {
				if (r.eserlekuakLibre(kop)) {
					Erreserba erreserbaBerria = tr.sortuErreserba(r, kop, from, to, prezioTotala);
					r.gehituErreserba(erreserbaBerria);

					int eserlekuBerriak = r.getnPlaces() - kop;
					r.setnPlaces(eserlekuBerriak);
					
					Driver gidaria = r.getDriver();
					float gidariarenDiruaAurretik = gidaria.getCash();
					gidaria.setCash(gidariarenDiruaAurretik + prezioTotala);

					System.out.println("=== ESERLEKUAK EGUNERATZEN ===");
					System.out.println("Eserleku libreak AURRETIK: " + (r.getnPlaces() + kop));
					System.out.println("Erreserbatutako eserlekuak: " + kop);
					System.out.println("Eserleku libreak ORAIN: " + r.getnPlaces());
					System.out.println("==============================");

					System.out.println("=== GIDARIARI DIRUA GEHITZEN ===");
					System.out.println("Gidaria: " + gidaria.getEmail());
					System.out.println("Dirua aurretik: " + gidariarenDiruaAurretik + "€");
					System.out.println("Prezio totala: " + prezioTotala + "€");
					System.out.println("Dirua orain: " + gidaria.getCash() + "€");
					System.out.println("=================================");

					db.persist(erreserbaBerria);
					db.merge(tr);
					db.merge(r); 
					db.merge(gidaria);

					db.getTransaction().commit();

					System.out.println("=== ERRESERBA SORTUTA ===");
					System.out.println("Bidaiaria: " + tr.getEmail());
					System.out.println("Gidaria: " + r.getDriver().getEmail());
					System.out.println("Bidaia: " + r.getFrom() + " -> " + r.getTo());
					System.out.println("Eserlekuak erreserbatuta: " + kop);
					System.out.println("Prezio unitarioa: " + r.getPrice() + "€");
					System.out.println("Prezio totala: " + prezioTotala + "€");
					System.out.println("Bidaiariaren dirua gelditzen: " + tr.getCash() + "€");
					System.out.println("Gidariari gehitu zaio: " + prezioTotala + "€");
					System.out.println("Eserleku libreak gelditzen: " + r.getnPlaces());
					System.out.println("========================");

					return true;
				} else {
					throw new EserlekurikLibreEzException(
							"Ez dago nahiko eserlekurik libre (" + r.getnPlaces() + " libre, " + kop + " eskatuta)");
				}
			} else {
				throw new DiruaEzDaukaException(
						"Ez dauka nahiko dirurik. Behar: " + prezioTotala + "€, Dauka: " + tr.getCash() + "€");
			}
		} else {
			throw new ErreserbaAlreadyExistsException("Dagoeneko erreserba bat du erabiltzaile honek bidaia honetan");
		}
	}

	//Bidaiaria lortzeko bere emaila jakinda
	public Traveler getTraveler(String email) {
		if (db == null || !db.isOpen()) {
			open();
		}

		Traveler traveler = db.find(Traveler.class, email);
		return traveler;
	}

	
	//Bidaiaria eguneratzeko, bere diru kopurua berriarekin
	public void updateTraveler(Traveler traveler) {
		try {
			if (db == null || !db.isOpen()) {
				open();
			}

			db.getTransaction().begin();
			db.merge(traveler);
			db.getTransaction().commit();

			System.out.println("=== TRAVELER EGUNERATUTA ===");
			System.out.println("Email: " + traveler.getEmail());
			System.out.println("Dirua: " + traveler.getCash() + "€");
			System.out.println("===========================");

		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.err.println("Error updating traveler: " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	//Bidai bakoitzaren erreserbak lortzeko metodoa
	public List<Erreserba> getBookingsByRide(Integer rideNumber) {
		try {
			if (db == null || !db.isOpen()) {
				open();
			}

			Ride ride = db.find(Ride.class, rideNumber);
			if (ride != null) {
				List<Erreserba> erreserbak = ride.getErreserbak();
				erreserbak.size();
				return new ArrayList<>(erreserbak);
			}
			return new ArrayList<>();

		} catch (Exception e) {
			System.err.println("Error getting bookings by ride: " + e.getMessage());
			e.printStackTrace();
			return new ArrayList<>();
		}
	}

	
	//Gidaria eguneratzeko bere diru kopuru berriarekin
	public void updateDriver(Driver driver) {
		try {
			if (db == null || !db.isOpen()) {
				open();
			}

			db.getTransaction().begin();
			db.merge(driver);
			db.getTransaction().commit();

			System.out.println("=== DRIVER EGUNERATUTA ===");
			System.out.println("Email: " + driver.getEmail());
			System.out.println("Dirua: " + driver.getCash() + "€");
			System.out.println("==========================");

		} catch (Exception e) {
			if (db.getTransaction().isActive()) {
				db.getTransaction().rollback();
			}
			System.err.println("Error updating driver: " + e.getMessage());
			e.printStackTrace();
		}
	}
}