package exceptions;

public class UserAlreadyRegistered extends Exception{
	public UserAlreadyRegistered() {
		super("The user is already registered.");
	}
	public UserAlreadyRegistered(String message) {
		super(message);
	}
}