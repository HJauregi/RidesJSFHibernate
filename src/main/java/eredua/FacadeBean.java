package eredua;

import business_logic.BLFacade;
import business_logic.BLFacadeImplementation;
import data_access.HibernateDataAccess;

/**
 * FacadeBean klasea Singleton patroia erabiltzen du aplikazio osoan zehar
 * negozio-logikara sartzeko puntua eskaintzeko.
 */
public class FacadeBean {
	private static FacadeBean __singleton__ = new FacadeBean();
	
	private static BLFacade facadeInterface;

	/**
	 * Eraikitzaile pribatua. Kanpotik instantziak sortzea eragozten du (Singleton patroia).
	 * Negozio-logikaren inplementazioa sortzen du Hibernate datu-basearekin.
	 */
	private FacadeBean() {
		try {
			facadeInterface = new BLFacadeImplementation(new HibernateDataAccess());

		} catch (Exception e) {
			System.out.println("FacadeBean: negozioaren logika sortzean errorea: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Negozio-logikaren interfazea itzultzen du.
	 * Metodo hau da aplikazioaren edozein tokitatik BL-ra sartzeko bidea.
	 * 
	 * @return BLFacade negozio-logika
	 */
	public static BLFacade getBusinessLogic() {
		return facadeInterface;
	}
}