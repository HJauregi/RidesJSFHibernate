package exceptions;

public class DiruaEzDaukaException extends Exception {
 private static final long serialVersionUID = 1L;
 
 	public DiruaEzDaukaException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public DiruaEzDaukaException(String s)
  {
    super(s);
  }
}