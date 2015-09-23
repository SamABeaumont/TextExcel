package textexcel;

import java.util.*;
import java.util.regex.*;

/**
 * A class to represent user-called functions, such as {@code AVG}, {@code SUM}, etc.
 * @author Sam Beaumont
 */
class Function {
	/**
	 * Indicates that the function returns the average of the values of a range of {@link Cell}s.
	 */
	static final int AVG = 0;
	/**
	 * Indicates that the function returns the maximum value in a range of {@link Cell}s.
	 */
	static final int MAX = 1;
	/**
	 * Same as {@link AVG}.
	 */
	static final int MEAN = 0;
	/**
	 * Indicates that the function returns the median value of a range of {@link Cell}s.
	 */
	static final int MED = 2;
	/**
	 * Indicates that the function returns the minimum value from a range of {@link Cells}.
	 */
	static final int MIN = 3;
	/**
	 * Indicates that the function returns the square root of two expressions that both evaluate
	 * to real numbers.
	 */
	static final int SQRT = 4;
	/**
	 * Indicates that the function returns the sum of the values of all of the {@link Cell}s
	 * in the specified range.
	 */
	static final int SUM = 5;
	
	private int type;
	private String original;
	private List<String> args;
	private boolean isFormatted;
	private Spreadsheet spreadsheet; // To allow references to cells or ranges of cells
	
	/**
	 * Constructs a new {@code Function} object by parsing the given {@link String} to determine
	 * the type of function represented and the arguments passed to that function.
	 * @param func
	 * @param spreadsheet
	 */
	Function (String func, Spreadsheet spreadsheet) {
		args = readArgs(func);
		
		if (func.matches("[A-Za-z]+\\(.*\\)")) {
			if ((Library.startsWithIgnoreCase(func, "AVG") || Library
					.startsWithIgnoreCase(func, "MEAN"))) {
				type = AVG;
			} else if (Library.startsWithIgnoreCase(func, "MAX")) {
				type = MAX;
			} else if (Library.startsWithIgnoreCase(func, "MED")) {
				type = MEAN;
			} else if (Library.startsWithIgnoreCase(func, "MIN")) {
				type = MIN;
			} else if (Library.startsWithIgnoreCase(func, "SQRT")) {
				type = SQRT;
			} else if (Library.startsWithIgnoreCase(func, "SUM")) {
				type = SUM;
			} else {
				Matcher funcNameFinder = Pattern.compile("([A-Za-z]+)\\(.*\\)").matcher(func);
				String funcName = null;
				if (funcNameFinder.find()) { // always evaluates to true
					funcName = funcNameFinder.group(1);
				}
				throw new UnrecognizedFunctionException(funcName);
			}
			
			isFormatted = true;
		} else if (func.matches("(?iu)(AVG|MEAN|SUM)\\s+[A-Z]+\\d+\\s*-\\s*[A-Z]+\\d+")) {
			Matcher argReader
					= Pattern.compile("(?iu)(AVG|MEAN|SUM)\\s+([A-Z]+\\d+)\\s*-\\s*([A-Z]+\\d+)")
					.matcher(func);
			
			if (argReader.find()) { // always evaluates to true
				args.add(argReader.group(1));
				args.add(argReader.group(2));
			}
			
			if (Library.startsWithIgnoreCase(func, "avg") || Library.startsWithIgnoreCase(func, "mean")) {
				type = AVG;
			} else if (Library.startsWithIgnoreCase(func, "sum")) {
				type = SUM;
			} else {
				String funcName = null;
				if (func.matches("[A-Za-z]+\\(.*\\)")) {
					funcName = func.substring(0, func.indexOf("("));
				} else if (func.matches("[A-Za-z]+\\s+[A-Z]+\\d+\\s*-\\s*[A-Z]+\\d+\\s*")) {
					funcName = func.substring(0, func.indexOf(" "));
				}
				
				if (funcName == null) {
					throw new UnrecognizedFunctionException();
				} else {
					throw new UnrecognizedFunctionException(funcName);
				}
			}
			
			isFormatted = false;
		} else {
			throw new UnrecognizedFunctionException();
		}
		
		this.spreadsheet = spreadsheet;
	}
	
	/**
	 * Returns the value computed by the {@code Function}.
	 */
	double getValue () {
		if (type == AVG) {
			
		} else if (type == MAX) {
			
		} else if (type == MED) {
			
		} else if (type == MIN) {
			
		} else if (type == SQRT) {
			return Math.sqrt(Double.parseDouble(args.get(0)));
		} else { 
			return 0;
		}
		return 0;
	}
	
	/**
	 * Returns an {@code int} representing the type of {@code Function} that is stored.
	 */
	int getType () {
		return type;
	}
	
	/**
	 * Returns the {@link String} that was originally used to construct the {@code Function}.
	 */
	public String toString () {
		return original;
	}
	
	/**
	 * Reads a {@link String} representing a user-called function, parsing its parameters.
	 * @param func A {@link String} of parameters.
	 * @return A {@link List}{@code <}{@link String}{@code >} representing the parameters
	 * 			passed to the function.
	 */
	private static List<String> readArgs (String func) {
		List<String> args = new LinkedList<String>();
		if (func.matches("[A-Za-z]+\\(.*\\)")) {
			addArgs(func, args);
		} else if (func.matches("[A-za-z]+\\s*[A-Z]+\\d+\\s*-\\s*[A-Z]+\\d+")) {
			Matcher argFinder = Pattern.compile("[A-za-z]+\\s*([A-Z]+\\d+)\\s*-\\s*([A-Z]+\\d+)").matcher(func);
			args.add(argFinder.group(1));
			args.add(argFinder.group(2));
		} else {
			throw new UnrecognizedFunctionException();
		}
		return args;
	}
	
	/**
	 * Adds the arguments of a function to an {@link List}, assuming that the {@link String}
	 * representing the function matches the regular expression "{@code [A-Za-z]\(.*\)}".
	 * @param func The function to be parsed.
	 * @param args The {@link List} of {@link String} to be filled.
	 */
	private static void addArgs (String func, List<String> args) {
		Scanner argReader = new Scanner(func).useDelimiter(",\\s*");
		while (argReader.hasNext()) {
			String current = argReader.next();
			if (Library.parensAreClosed(current)) {
				args.add(current);
			} else {
				recursiveAddArgs(argReader, args);
			}
		}
	}
	
	private static void recursiveAddArgs (Scanner argReader, List<String> args) {
		
	}
}