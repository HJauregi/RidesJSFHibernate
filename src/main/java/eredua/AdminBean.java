package eredua;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import java.io.Serializable;

@Named("adminBean")
@SessionScoped
public class AdminBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public String dropDB() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            CredentialsBean credentials = context.getApplication()
                .evaluateExpressionGet(context, "#{credentials}", CredentialsBean.class);
            
            if (!credentials.isLoggedIsAdmin()) {
                FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Ez duzu baimen nahikorik ekintza hau egiteko", null));
                return null;
            }
            
            FacadeBean.getBusinessLogic().dropDB();
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Datu-basea arrakastaz ezabatu da", null));
            
            return null;
            
        } catch (Exception e) {
            System.err.println("ERROREA datu-basea ezabatzerakoan: " + e.getMessage());
            e.printStackTrace();
            
            FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "Errorea datu-basea ezabatzerakoan", null));
            
            return null;
        }
    }
}