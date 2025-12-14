package eredua;

import domain.Driver;
import domain.Traveler;
import domain.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("manageMoneyBean")
@RequestScoped
public class ManageMoneyBean implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private CredentialsBean credentials;

    private float amount;

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public double getCurrentCash() {
        try {
            String email = credentials.getLoggedEmail();
            
            if (credentials.isLoggedIsDriver()) {
                Driver driver = FacadeBean.getBusinessLogic().getDriver(email);
                if (driver != null) {
                    return driver.getCash();
                }
            } else {
                Traveler traveler = FacadeBean.getBusinessLogic().getTraveler(email);
                if (traveler != null) {
                    return traveler.getCash();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalMoney() {
        return getCurrentCash();
    }

    public String addMoney() {
        try {
            if (amount <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Zenbatekoak 0 baino handiagoa izan behar du", null));
                return null;
            }

            String email = credentials.getLoggedEmail();

            if (credentials.isLoggedIsDriver()) {
                Driver driver = FacadeBean.getBusinessLogic().getDriver(email);
                
                if (driver != null) {
                    float diruaAurretik = driver.getCash();
                    driver.setCash(diruaAurretik + amount);
                    FacadeBean.getBusinessLogic().updateDriver(driver);

                    System.out.println("=== DIRUA GEHITUTA (DRIVER) ===");
                    System.out.println("Gidaria: " + driver.getEmail());
                    System.out.println("Zenbatekoa: " + amount + "€");
                    System.out.println("Diru zaharra: " + diruaAurretik + "€");
                    System.out.println("Diru berria: " + driver.getCash() + "€");
                    System.out.println("================================");

                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                            amount + "€ gehitu dira zure kontura", null));

                    amount = 0;
                    return null;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Errorea: Gidaria ez da aurkitu", null));
                    return null;
                }
                
            } else {
                Traveler traveler = FacadeBean.getBusinessLogic().getTraveler(email);

                if (traveler != null) {
                    traveler.diruaSartu(amount);
                    FacadeBean.getBusinessLogic().updateTraveler(traveler);

                    System.out.println("=== DIRUA GEHITUTA (TRAVELER) ===");
                    System.out.println("Bidaiaria: " + traveler.getEmail());
                    System.out.println("Zenbatekoa: " + amount + "€");
                    System.out.println("Diru berria: " + traveler.getCash() + "€");
                    System.out.println("==================================");

                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                            amount + "€ gehitu dira zure kontura", null));

                    amount = 0;
                    return null;
                } else {
                    FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Errorea: Erabiltzailea ez da aurkitu", null));
                    return null;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Errorea dirua gehitzen: " + e.getMessage(), null));
            return null;
        }
    }
    
    public String goBack() {
        if (credentials.isLoggedIsDriver()) {
            return "driver";
        } else {
            return "traveler";
        }
    }
}