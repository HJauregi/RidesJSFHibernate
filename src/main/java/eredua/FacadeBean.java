package eredua;

import business_logic.BLFacade;
import business_logic.BLFacadeImplementation;
import data_access.HibernateDataAccess;

public class FacadeBean {
	private static FacadeBean singleton = new FacadeBean();
	private static BLFacade facadeInterface;

	private FacadeBean() {
		try {
			System.out.println("=== FacadeBean hasieratzen ===");
			
			// Datu-basea hasieratu
			HibernateDataAccess dataAccess = new HibernateDataAccess();
			dataAccess.open();
			dataAccess.initializeDB();
			dataAccess.close();
			
			System.out.println("Datu-basea hasieratuta!");
			
			// BusinessLogic sortu
			facadeInterface = new BLFacadeImplementation(new HibernateDataAccess());
			
		} catch (Exception e) {
			System.out.println("FacadeBean: negozioaren logika sortzean errorea: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public static BLFacade getBusinessLogic() {
		return facadeInterface;
	}
}