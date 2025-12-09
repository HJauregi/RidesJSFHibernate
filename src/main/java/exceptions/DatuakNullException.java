package exceptions;

public class DatuakNullException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatuakNullException() {
		super();
	}

	/** This exception is triggered if the data is null
	 * 
	 * @param s String of the exception
	 */
	public DatuakNullException(String s) {
		super(s);
	}

}
