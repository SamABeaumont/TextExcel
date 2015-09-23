package textexcel;

/**
 * Thrown to indicate that the user has called a function on the spreadsheet that is not recognized.
 * @author Sam Beaumont
 */
class UnrecognizedFunctionException extends IllegalArgumentException {
	/**
	 * Constructs a new {@code UnrecognizedFunctionException} with no detail message.
	 */
	UnrecognizedFunctionException () {
		super();
	}
	
	/**
	 * Constructs a new {@code UnrecognizedFunctionException} with the specified detail message.
	 * @param message The detail message.
	 */
	UnrecognizedFunctionException (String message) {
		super(message);
	}
	
	private static final long serialVersionUID = 473928734L;
}