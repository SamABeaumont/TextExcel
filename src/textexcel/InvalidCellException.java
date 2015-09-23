package textexcel;

/**
 * Thrown to indicate that the suer attempted to create a cell, but the command that they typed
 * could not be parsed into a valid cell type.
 * @author Sam Beaumont
 */
class InvalidCellException extends IllegalArgumentException {
	/**
	 * Constructs an {@code InvalidCellException} with no detail message.
	 */
	InvalidCellException () {
		super();
	}
	
	private static final long serialVersionUID = 1847291929L;
}