package textexcel;

import java.util.*;
import java.util.regex.*;

/**
 * A class to represent mathematical formulas.
 * @author Sam Beaumont
 */
class Formula {
	private String formula;
	private Set<String> cells = new HashSet<String>();
	private Spreadsheet spreadsheet;
	
	/**
	 * Constructs a new {@code Formula} object with all fields set to {@code null}.
	 */
	private Formula () {}
	
	/**
	 * Initializes a new formula using the {@link String} "{@code (0)}"
	 */
	Formula (String name, Spreadsheet spreadsheet) {
		this("(0)", name, spreadsheet);
	}
	
	/**
	 * Constructs a new formula using a {@link String} that is input by the user.
	 * @throws NumberFormatException If the formula is not a mathematically valid expression
	 * 			or is not surrounded by parentheses.
	 * @throws ArithmeticException If the formula does not evaluate to a real number.
	 * @param formula The {@link String} used to create the formula.
	 */
	Formula (String formula, String name, Spreadsheet spreadsheet) {
		if (!formula.startsWith("(") || !formula.endsWith(")")) { // not surrounded by parentheses
			throw new NumberFormatException();
		}
		this.formula = formula;
		ensureValid(getValue(formula.replaceAll("[A-Z]+\\d+", "1"), cells));
		this.spreadsheet = spreadsheet;
		cells.add(name);
	}
	
	/**
	 * Returns the value of the formula as a {@code double}.
	 * @throws ArithmeticException If evaluating the formula results in division by zero.
	 * @throws NumberFormatException If the formula is not a mathematically valid
	 * 			expression.
	 */
	double getValue () {
		return getValue(cells);
	}
	
	/**
	 * Returns a copy of the formula.
	 */
	public Formula clone () {
		Formula f = new Formula();
		f.formula = formula;
		f.cells = cells;
		f.spreadsheet = spreadsheet;
		return f;
	}

	/**
	 * Returns the {@link String} that was originally used to construct the formula.
	 */
	public String toString () {
		return formula;
	}
	
	/**
	 * Throws an {@link ArithmeticException} if a call of {@link Double#toString(double)} on the
	 * {@link double} that is passed as a parameter returns {@code "NaN"}, or anything that ends
	 * with {@code "Infinity"}, case-insensitive. 
	 * @param value The {@code double} to be evaluated.
	 */
	private static void ensureValid (double value) {
		if (Double.toString(value).equals("NaN")
				|| Library.endsWithIgnoreCase(Double.toString(value), "Infinity")) {
			throw new ArithmeticException();
		}
	}
	
	private double getValue (Set<String> cells) {
		// Replace all instances of - - or -- with +.
		// For example, 5 - -5 would become 5 + 5, which would evaluate to 10.0
		String s = formula.replaceAll("-\\s*-", "+");
		double value = getValue(s, cells);
		ensureValid(value);
		return value;
	}
	
	/**
	 * Returns the value of the given {@link String} as a {@code double} if it is
	 * a well-formed, syntactically valid mathematical expression. A {@link NumberFormatException}
	 * is thrown if the formula is not valid.
	 * @param formula The {@link String} to be evaluated as a formula.
	 */
	private double getValue (String formula, Set<String> cells) {
		String[] regexes = new String[] {
			"\\s*\\(\\s*(.*)\\s*\\)\\s*", // parentheses
			"\\s*(.*)\\s*\\+\\s*(.*)\\s*", // addition
			"\\s*(.*)\\s*-\\s*(.*)\\s*", // subtraction
			"\\s*(.*)\\s*\\*\\s*(.*)\\s*", // multiplication
			"\\s*(.*)\\s*/\\s*(.*)\\s*", // division
			"\\s*(.*)\\s*%\\s*(.*)\\s*", // modulo
			"\\s*(.*)\\s*\\^\\s*(.*)\\s*", // exponentiation
			"\\s*-\\s*(\\(.*\\)|\\d)\\s*", // negation
			"\\s*([A-Z]+\\d+)\\s*", // cell reference
			"\\s+(\\d)\\s*", // number preceded by, possibly followed by spaces
			"(\\d+)\\s+", // number followed by spaces
		};
		
		// create a Matcher object for each pattern
		Matcher[] matchers = new Matcher[regexes.length];
		for (int i = 0; i < matchers.length; i++) {
			matchers[i] = Pattern.compile("^" + regexes[i] + "$").matcher(formula);
		}
		
		// eliminate redundancy (no "matchers[0].find()", "matchers[1].find()", etc.)
		boolean[] found = new boolean[matchers.length];
		for (int i = 0; i < found.length; i++) {
			if (i == 2) { // Subtraction; handled separately
				continue;
			}
			found[i] = matchers[i].find();
		}
		
		// The subtraction sign is not immediately preceded by a different operator,
		// and is not at the beginning of the expression
		found[2] = matchers[2].find()
				&& !Library.containsRegex(matchers[2].group(1),
						"[\\+\\*\\/%\\^]\\s*$")
				&& !matchers[2].group(1).matches("\\s*");
		
		// Make sure that any binary operator that is being evaluated isn't inside parentheses
		for (int i = 2; i < found.length - 4; i++) {
			found[i] = found[i]
					&& Library.parensAreClosed(matchers[i].group(1))
					&& Library.parensAreClosed(matchers[i].group(2));
		}
		
		// recursive cases
		if (formula.matches("(?iu)\\(\\s*avg\\s*[A-Z]+\\d+\\s*-\\s*[A-Z]+\\d+\\s*\\)")) { // average
			Matcher m
					= Pattern.compile("(?iu)\\(\\s*avg\\s*([A-Z]+\\d+)\\s*-\\s*([A-Z]+\\d+)\\s*\\)")
					.matcher(formula);
			String startIndex = "";
			String endIndex = "";
			if (m.find()) {
				startIndex = m.group(1);
				endIndex = m.group(2);
			}
			return spreadsheet.avg(startIndex, endIndex);
		} else if (formula.matches("(?iu)\\(\\s*sum\\s*[A-Z]+\\d+\\s*-\\s*[A-Z]+\\d+\\s*\\)")) { // sum
			Matcher m
					= Pattern.compile("(?iu)\\(\\s*sum\\s*([A-Z]+\\d+)\\s*-\\s*([A-Z]+\\d+)\\s*\\)")
					.matcher(formula);
			String startIndex = "";
			String endIndex = "";
			if (m.find()) {
				startIndex = m.group(1);
				endIndex = m.group(2);
			}
			return spreadsheet.sum(startIndex, endIndex);
		} else if (found[0]) { // the whole expression is surrounded by parentheses
			return getValue(matchers[0].group(1), cells);
		} else if (found[1] || found[2]) { // PEMDAS: "AS"
			if (found[1]) { // addition
				return getValue(matchers[1].group(1), cells)
						+ getValue(matchers[1].group(2), cells);
			} else { // found[2] (subtraction)
				return getValue(matchers[2].group(1), cells)
						- getValue(matchers[2].group(2), cells);
			}
		} else if (found[3] || found[4] || found[5]) { // PEMDAS: "MD", with modulo as well
			if (found[3]) { // multiplication
				return getValue(matchers[3].group(1), cells)
						* getValue(matchers[3].group(2), cells);
			} else if (found[4]) { // division
				return getValue(matchers[4].group(1), cells)
						/ getValue(matchers[4].group(2), cells);
			} else { // found[5] (modulo)
				return getValue(matchers[5].group(1), cells)
						% getValue(matchers[5].group(2), cells);
			}
		} else if (found[6]) { // exponentiation (PEMDAS: "E")
			return Math.pow(getValue(matchers[6].group(1), cells),
					getValue(matchers[6].group(2), cells));
		} else if (found[7]) { // cell reference
			return spreadsheet.get(matchers[7].group(1)).getValue();
		} else if (found[8]) { // negation
			return -getValue(matchers[8].group(1), cells);
		} else if (found[9]) { // a valid double, preceded by and possibly followed by whitespace
			return getValue(matchers[9].group(1), cells);
		} else if (found[10]) { // a valid double, followed by whitespace
			return getValue(matchers[10].group(1), cells);
		} else { // one digit, base case
			return Double.parseDouble(formula);
		}
	}
}