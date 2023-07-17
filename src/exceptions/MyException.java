package exceptions;

public class MyException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyException() {
		super("Invalid instruction.");
	}
	
	public MyException(String msg) {
		super(msg);
	}

}
