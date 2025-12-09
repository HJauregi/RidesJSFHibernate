package exceptions;

public class ErreserbaAlreadyExistsException extends Exception {
 private static final long serialVersionUID = 1L;
 
 	public ErreserbaAlreadyExistsException()
  {
    super();
  }
  /**This exception is triggered if the question already exists 
  *@param s String of the exception
  */
  public ErreserbaAlreadyExistsException(String s)
  {
    super(s);
  }
}