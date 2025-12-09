package exceptions;

public class EserlekurikLibreEzException extends Exception {
 private static final long serialVersionUID = 1L;
 
 public EserlekurikLibreEzException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public EserlekurikLibreEzException(String s)
  {
    super(s);
  }
}