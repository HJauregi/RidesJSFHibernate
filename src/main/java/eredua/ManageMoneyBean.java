package eredua;

import domain.Traveler;
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

    private double amount;

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getCurrentCash() {
        try {
            Traveler traveler = FacadeBean.getBusinessLogic().getTraveler(credentials.getLoggedEmail());
            if (traveler != null) {
                return traveler.getCash();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getFrozenMoney() {
        try {
            Traveler traveler = FacadeBean.getBusinessLogic().getTraveler(credentials.getLoggedEmail());
            if (traveler != null) {
                return traveler.getFrozenMoney();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public double getTotalMoney() {
        return getCurrentCash() + getFrozenMoney();
    }

    public String addMoney() {
        try {
            if (amount <= 0) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Zenbatekoak 0 baino handiagoa izan behar du", null));
                return null;
            }

            Traveler traveler = FacadeBean.getBusinessLogic().getTraveler(credentials.getLoggedEmail());
            
            if (traveler != null) {
                traveler.diruaSartu(amount);  // ← CAMBIADO A diruaSartu()
                FacadeBean.getBusinessLogic().updateTraveler(traveler);

                System.out.println("=== DIRUA GEHITUTA ===");
                System.out.println("Bidaiaria: " + traveler.getEmail());
                System.out.println("Zenbatekoa: " + amount + "€");
                System.out.println("Diru berria: " + traveler.getCash() + "€");
                System.out.println("=====================");

                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                        amount + "€ gehitu dira zure kontura", null));

                amount = 0; // Reset
                return null; // Mantener en la misma página para ver el mensaje
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Errorea: Erabiltzailea ez da aurkitu", null));
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Errorea dirua gehitzen: " + e.getMessage(), null));
            return null;
        }
    }
}