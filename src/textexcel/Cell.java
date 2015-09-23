package textexcel;

/**
 * A class to represent cells that are stored in the {@link Spreadsheet} object.
 * @author Sam Beaumont
 */
class Cell {
	private Date date;
	private double d;
	private Formula formula;
	private String s;
	private Types type;
	
	/**
	 * Creates a new cell without initializing any fields besides the type field,
	 * which is set to Types.EMPTY
	 */
	Cell () {
		type = Types.EMPTY;
	}
	
	/**
	 * Creates a new cell that, depending on the format of the {@link String} that
	 * is passed to it, holds an {@code int}, {@code double}, {@link Date}, or
	 * {@link String}.
	 * @param iS The {@link String} to be parsed.
	 * @throws ArithmeticException
	 * @throws IllegalArgumentException If {@code iS} cannot be parsed into a valid cell.
	 */
	Cell (String iS) {
		try {
			d = Double.parseDouble(iS);
			type = Types.DOUBLE;
		} catch (NumberFormatException nfe) { // Not a valid real number
			try {
				date = new Date(iS);
				type = Types.DATE;
			} catch (IllegalArgumentException iae) { // Not a valid date
				if (iS.matches("\".+\"")) {
					s = iS.substring(1, iS.length() - 1);
					type = Types.STRING;
				} else if (iS.equals("<empty>") || iS.equals("\"\"")) {
					type = Types.EMPTY;
				} else {
					throw new IllegalArgumentException();
				}
			}
		}
	}
	
	/**
	 * Creates a new cell using the given {@link String} as a formula.
	 * The {@link Spreadsheet} object is used to reference other cells if necessary.
	 * @param formula
	 * @param spreadsheet
	 */
	Cell (String formula, String name, Spreadsheet spreadsheet) {
		this.formula = new Formula(formula, name, spreadsheet);
		type = Types.FORMULA;
	}
	
	/**
	 * Returns one of the constants of the enumerated type {@link Types}, depending
	 * on the type of data that is stored in the cell.
	 */
	Types getType () {
		return type;
	}
	
	/**
	 * Returns the value of the {@link Formula} in the cell if the cell contains a formula.
	 * Otherwise, returns {@code 0.0}.
	 */
	double getValue () {
		if (type == Types.FORMULA) {
			return formula.getValue();
		} else if (type == Types.DOUBLE) {
			return d;
		} else {
			return 0;
		}
	}
	
	/**
	 * Returns a copy of the cell.
	 */
	public Cell clone () {
		Cell copy = new Cell();
		copy.date = date;
		copy.d = d;
		copy.formula = formula;
		copy.s = s;
		copy.type = type;
		return copy;
	}
	
	/**
	 * Returns a textual representation of the contents of the cell, formatted as a {@link String}.
	 */
	public String toString () {
		switch (type) {
			case DATE: // date
				return date.toString();
			case DOUBLE: // number
				return Double.toString(d);
			case FORMULA: // Formula
				return formula.toString();
			case STRING: // String
				return "\"" + s + "\"";
			default: // case EMPTY:
				return "<empty>";
		}
	}
}