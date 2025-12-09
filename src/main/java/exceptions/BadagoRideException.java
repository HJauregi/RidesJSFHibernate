package exceptions;

public class BadagoRideException extends Exception {
 private static final long serialVersionUID = 1L;
 
 	public BadagoRideException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public BadagoRideException(String s)
  {
    super(s);
  }
}
