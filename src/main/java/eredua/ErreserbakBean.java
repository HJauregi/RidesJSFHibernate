package eredua;

import business_logic.BLFacade;
import domain.Driver;
import domain.ErreserbaData;
import domain.Erreserba;
import domain.Ride;
import domain.Traveler;
import exceptions.DatuakNullException;
import exceptions.DiruaEzDaukaException;
import exceptions.ErreserbaAlreadyExistsException;
import exceptions.EserlekurikLibreEzException;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named("erreserbakBean")
@ViewScoped
public class ErreserbakBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CredentialsBean credentials;

    private Integer selectedRideNumber;
    private List<Erreserba> bookings;

    public String erreserbatu(Ride ride, Integer requestedSeats) {
        BLFacade facade = FacadeBean.getBusinessLogic();
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            String currentUserEmail = credentials.getLoggedEmail();

            if (currentUserEmail == null) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Saioa hasi behar duzu erreserba bat egiteko", ""));
                return null;
            }

            if (credentials.isLoggedIsDriver()) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Gidariak ezin du erreserba egin! Logeatu bidaiari bezala", "Gidariek ezin dute bidaiak erreserbatu"));
                return null;
            }

            if (requestedSeats == null || requestedSeats <= 0) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Sartu eserleku kopurua", "Mesedez, sartu erreserbatu nahi dituzun eserleku kopurua"));
                return null;
            }

            if (requestedSeats > ride.getnPlaces()) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Ez dago nahiko eserleku", 
                        "Eskatutako eserlekuak (" + requestedSeats + ") gehiago dira eskuragarri daudenak baino (" + ride.getnPlaces() + ")"));
                return null;
            }

            System.out.println("=== ERRESERBA PROZESUA HASTEN ===");
            System.out.println("Erabiltzailea: " + currentUserEmail);
            System.out.println("Bidaia: " + ride.getRideNumber());
            System.out.println("Eserleku kopurua: " + requestedSeats);

            Traveler traveler = facade.getTraveler(currentUserEmail);

            if (traveler == null) {
                System.err.println("ERROR: Ez da aurkitu traveler-a: " + currentUserEmail);
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Bidaiaria ez da aurkitu", "Bidaiaria ez da aurkitu"));
                return null;
            }

            System.out.println("Traveler aurkituta: " + traveler.getEmail());
            System.out.println("Dirua: " + traveler.getCash() + "€");

            float prezioUnitarioa = ride.getPrice();
            float prezioTotala = prezioUnitarioa * requestedSeats;

            ErreserbaData erreData = new ErreserbaData(
                ride.getRideNumber(),
                ride.getFrom(),
                ride.getTo(),
                requestedSeats
            );

            boolean success = facade.sortuErreserba(traveler, erreData);

            if (success) {
                String mezua = String.format(
                    "Erreserba arrakastatsua! %d eserleku erreserbatu dira %s-tik %s-ra. " +
                    "Prezio unitarioa: %.2f€. Prezio totala: %.2f€",
                    requestedSeats,
                    ride.getFrom(),
                    ride.getTo(),
                    prezioUnitarioa,
                    prezioTotala
                );
                
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Arrakasta!", mezua));
                        
                System.out.println("=== ERRESERBA ARRAKASTATSUA ===");
                System.out.println(mezua);
                System.out.println("================================");
            } else {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ezin izan da erreserba sortu", "Ezin izan da erreserba sortu"));
            }

        } catch (EserlekurikLibreEzException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Ez dago eserlekurik", "Ez dago nahiko eserleku libre bidaia honetan"));
        } catch (DiruaEzDaukaException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Ez dago dirurik", "Ez duzu nahiko diru erreserba hau egiteko"));
        } catch (ErreserbaAlreadyExistsException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_WARN,
                    "Dagoeneko erreserbatuta", "Dagoeneko erreserba bat duzu bidaia honetan"));
        } catch (DatuakNullException e) {
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Datu baliogabeak", "Bidaia edo erabiltzaile datuak ez dira baliozkoak"));
        } catch (Exception e) {
            System.err.println("ERROREA erreserba sortzerakoan: " + e.getMessage());
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Ustekabeko errorea gertatu da", ": " + e.getMessage()));
        }

        return null;
    }


    public Integer getSelectedRideNumber() {
        return selectedRideNumber;
    }

    public void setSelectedRideNumber(Integer selectedRideNumber) {
        this.selectedRideNumber = selectedRideNumber;
    }

    public List<Erreserba> getBookings() {
        return bookings;
    }

    public void setBookings(List<Erreserba> bookings) {
        this.bookings = bookings;
    }

    public List<Ride> getDriverRides() {
        try {
            Driver driver = FacadeBean.getBusinessLogic().getDriver(credentials.getLoggedEmail());
            if (driver != null) {
                return new ArrayList<>(driver.getRides());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public String loadBookings() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (selectedRideNumber == null) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "Mesedez, aukeratu bidaia bat", null));
                return null;
            }

            bookings = FacadeBean.getBusinessLogic().getBookingsByRide(selectedRideNumber);

            System.out.println("=== ERRESERBAK KARGATUTA ===");
            System.out.println("Bidaia: " + selectedRideNumber);
            System.out.println("Erreserba kopurua: " + (bookings != null ? bookings.size() : 0));
            System.out.println("===========================");

            if (bookings == null || bookings.isEmpty()) {
                context.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Ez dago erreserbarik bidaia honetan", null));
            }

            return null;

        } catch (Exception e) {
            e.printStackTrace();
            context.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Errorea erreserbak kargatzen", null));
            return null;
        }
    }


    public CredentialsBean getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsBean credentials) {
        this.credentials = credentials;
    }
}