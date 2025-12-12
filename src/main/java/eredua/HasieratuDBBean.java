package eredua;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

@Named("hasieratu")
@ApplicationScoped
public class HasieratuDBBean {
    
    private boolean initialized = false;
    
    @PostConstruct
    public void init() {
        if (!initialized) {
            System.out.println("=== APLIKAZIOA HASIERATZEN ===");
            try {
                // Datu-basea ireki eta itxi hasieratzeko
                FacadeBean.getBusinessLogic().open();
                System.out.println("Datu-basea ondo hasieratuta");
                FacadeBean.getBusinessLogic().close();
                System.out.println("==============================");
                initialized = true;
            } catch (Exception e) {
                System.err.println("ERROREA datu-basea hasieratzean: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}