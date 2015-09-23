package textexcel;

import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * A class to represent the spreadsheet object that is used in {@link Program}.
 * @author Sam Beaumont
 */
class Spreadsheet {
	private Cell[][] spreadsheet;
	private String path;
	
	/**
	 * Creates a new, empty {@link Spreadsheet} with ten rows and seven columns,
	 * and every {@link Cell} set to {@code <empty>}.
	 */
	Spreadsheet () {
		newSheet();
	}
	
	/**
	 * Parses the file at the specified location into a new {@link Spreadsheet}
	 * @param filepath The filepath of the file to be parsed.
	 * @throws FileNotFoundException If the file at the specified location is not found
	 * 			or cannot be read.
	 */
	Spreadsheet (String filepath) throws FileNotFoundException, SecurityException {
		// Set up a Scanner that uses two blank lines to separate tokens
		Scanner fileReader = new Scanner(new File(filepath));
		fileReader.useDelimiter(Pattern.compile("(?m)$^$^"));
		ArrayList<ArrayList<Cell>> list = new ArrayList<ArrayList<Cell>>();
		
		// Parse the file into an ArrayList of cells
		for (int i = 0; fileReader.hasNext(); i++) {
			Scanner thisRow = new Scanner(fileReader.next());
			list.add(new ArrayList<Cell>());
			while (thisRow.hasNextLine()) {
				try {
					list.get(i).add(new Cell(thisRow.nextLine()));
				} catch (IllegalArgumentException e) {
					list.get(i).add(new Cell());
				}
			}
			thisRow.close(); // see below
		}
		// Get rid of those annoying "Resource leak: some scanner is never closed" warnings
		fileReader.close();
		
		// Kludge - otherwise, there's always an extra row of one cell at the end.
		list.remove(list.size() - 1);
		
		// Kludge - otherwise, a blank cell is added to the front of every row except the first
		for (int i = 1; i < list.size(); i++) {
			list.get(i).remove(0);
		}
		
		// Convert the ArrayList into an array.
		Cell[][] sprArray = new Cell[list.size()][list.get(0).size()];
		for (int i = 0; i < sprArray.length; i++) {
			ArrayList<Cell> thisRow = list.get(i);
			
			for (int j = 0; j < sprArray[0].length; j++) {
				sprArray[i][j] = thisRow.get(j);
			}
		}
		spreadsheet = sprArray;
		path = filepath;
	}
	
	/**
	 * Gets the cell that is located at the specified location in the spreadsheet.
	 * @param cell A textual representation of the cell's location within the spreadsheet.
	 * @return The cell that is at the specified location.
	 * @see getCell
	 */
	Cell get (String cell) {
		int[] indices = getIndices(cell);
		if (indices[0] >= spreadsheet.length || indices[1] >= spreadsheet[0].length) {
			return new Cell(); // empty cell
		} else {
			return spreadsheet[indices[0]][indices[1]].clone();
		}
	}
	
	/**
	 * Returns the cell at the given indices of the array of {@link Cell}s
	 * that is used internally to represent the spreadsheet.
	 * @param row The index of the row of the desired {@link Cell}
	 * @param col The index of the column of the desired {@link Cell}
	 * @return A copy of the {@link Cell} at the specified indices, if the given indices
	 * 			are within the bounds of the array. Otherwise, an empty {@link Cell} is returned.
	 */
	Cell get (int row, int col) {
		try {
			return spreadsheet[row][col].clone();
		} catch (ArrayIndexOutOfBoundsException e) {
			return new Cell();
		}
	}
	
	/**
	 * Returns the average value of a given range of cells
	 * @param startCell
	 * @param endCell
	 * @return
	 */
	double avg (String startCell, String endCell) {
		int[] startIndices = getIndices(startCell);
		int[] endIndices = getIndices(endCell);
		return sum(startCell, endCell)
				/ (endIndices[0] - startIndices[0]) * (endIndices[1] - startIndices[1]);
	}
	
	/**
	 * Computes the sum of all cells in a rectangular region from the start cell (inclusive)
	 * to the end cell (exclusive)
	 * @param startCell The cell with the smallest row and column indices to be included in the sum.
	 * @param endCell The cell with the largest row and column indices to be included in the sum.
	 * @return
	 * @throws IllegalArgumentException if the cell range is invalid.
	 */
	double sum (String startCell, String endCell) {
		int[] start = getIndices(startCell);
		int[] end = getIndices(endCell);
		if (start[0] > end[0] || start[1] > end[1]) {
			throw new ArrayIndexOutOfBoundsException();
		}
		double sum = 0;
		for (int i = start[0]; i <= end[0] && i < spreadsheet.length; i++) {
			for (int j = start[1]; j <= end[1] && i < spreadsheet[0].length; j++) {
				sum += spreadsheet[i][j].getValue();
			}
		}
		return sum;
	}
	
	/**
	 * Sets the specified cell in the spreadsheet to the specified value.
	 * Overwrites the current spreadsheet array if necessary
	 * in order to set the cell to its correct value.
	 * @param cell A textual representation of the cell's location in the spreadsheet.
	 * @param value The contents of the cell.
	 * @see getCell
	 */
	void set (String cell, Cell value) {
		int[] indices = getIndices(cell);
		if (indices[0] >= spreadsheet.length || indices[1] >= spreadsheet[0].length) {
			Cell[][] nextSheet = new Cell[Math.max(spreadsheet.length, indices[0] + 1)]
					[Math.max(spreadsheet[0].length, indices[1] + 1)];
			for (int i = 0; i < nextSheet.length; i++) {
				for (int j = 0; j < nextSheet[0].length; j++) {
					if (i < spreadsheet.length && j < spreadsheet[0].length) { 
						nextSheet[i][j] = spreadsheet[i][j];
					} else {
						nextSheet[i][j] = new Cell();
					}
				}
			}
			spreadsheet = nextSheet;
		}
		spreadsheet[indices[0]][indices[1]] = value;
	}
	
	/**
	 * Replaces the current {@link Spreadsheet} with a spreadsheet made up of
	 * ten rows and seven columns, and every {@link Cell}
	 * set to {@code <empty>}.
	 */
	void clear () {
		newSheet();
	}
	
	/**
	 * Saves the {@link Spreadsheet} to the specified location.
	 * @param filepath The location that the {@link Spreadsheet} is to be saved at.
	 * @throws FileNotFoundException If the file cannot be written or an error occurs
	 * 			when opening the file.
	 * @throws SecurityException If the program that calls this method
	 * 			is denied permission to modify the file.
	 */
	void save (String filepath) throws FileNotFoundException, SecurityException {
		PrintStream writer = new PrintStream(new File(filepath));
		int numCols = spreadsheet[0].length;
		
		// Write the contents of the spreadsheet array to the file.
		for (int i = 0; i < spreadsheet.length; i++) {
			Cell[] thisRow = spreadsheet[i];
			for (int j = 0; j < numCols; j++) {
				writer.println(thisRow[j]);
			}
			writer.println();
		}
		writer.close();
		path = filepath;
	}
	
	/**
	 * Returns the non-absolute filepath of this {@link Spreadsheet} object as a {@link String},
	 * or {@code null} if the spreadsheet has not yet been loaded or saved.
	 */
	String getPath () {
		return path;
	}
	
	Cell[][] getArray () {
		Cell[][] copy = new Cell[spreadsheet.length][spreadsheet[0].length];
		for (int i = 0; i < copy.length; i++) {
			for (int j = 0; j < copy[0].length; j++) {
				copy[i][j] = spreadsheet[i][j].clone();
			}
		}
		return copy;
	}
	
	/**
	 * Returns a graphical representation of the spreadsheet as a {@link String}.
	 */
	public String toString () {
		String s = "            |"; // 12 spaces
		if (spreadsheet[0].length == 7) { // print the first row with exactly 7 columns
			for (char c = 'A'; c < 'H'; c++) {
				s += "     " + c + "      |";
			}
		} else { // print the first row with any other amount of columns
			for (int i = 0; i < spreadsheet[0].length; i++) {
				String thisCol = toBase26(i);
				s += multistr(" ", 6 - (thisCol.length() + 1) / 2) + thisCol;
				s += multistr(" ", 6 - thisCol.length() / 2) + "|";
			}
		}
		s += "\n" + multistr("------------+", spreadsheet[0].length + 1)  + "\n";
		
		for (int i = 0; i < spreadsheet.length; i++) { // print every subsequent row
			s += multistr(" ", 6 - (Integer.toString(i + 1).length() + 1) / 2);
			s += Integer.toString(i + 1);
			s += multistr(" ", 6 - Integer.toString(i + 1).length() / 2) + "|";
			
			for (int j = 0; j < spreadsheet[0].length; j++) {
				String thisCell = "";
				if (spreadsheet[i][j].getType() == Types.EMPTY) { // Empty cell
					thisCell = "";
				} else if (spreadsheet[i][j].getType() ==  Types.STRING) { // A String
					thisCell = spreadsheet[i][j].toString()
							.substring(1, spreadsheet[i][j].toString().length() - 1);
				} else if (spreadsheet[i][j].getType() == Types.FORMULA) {
					try {
						thisCell = Double.toString(spreadsheet[i][j].getValue());
					} catch (ArithmeticException ae) {
						thisCell = "#ERROR";
					} catch (StackOverflowError soe) {
						thisCell = "#ERROR";
					}
				} else { // Anything else (a date or real number)
					thisCell = spreadsheet[i][j].toString();
				}
				
				if (thisCell.length() > 12) { // Truncate the cell contents if necessary
					thisCell = thisCell.substring(0, 11) + ">";
				}
				
				s += multistr(" ", 6 - (thisCell.length() + 1) / 2);
				s += thisCell;
				s += multistr(" ", 6 - (thisCell.length()) / 2);
				s += "|";
			}
			s += "\n" + multistr("------------+", spreadsheet[0].length + 1) + "\n";
		}
		return s;
	}
	
	/**
	 * Calculates the indices of the {@link Spreadsheet} represented
	 * by a {@link String} of letters and numbers.
	 * <p>
	 * Calculates the actual indices in the {@link Spreadsheet} represented
	 * by a {@link String} formatted as follows:
	 * <pre>
	 *      {@code <column><row>}
	 * </pre>
	 * The column is represented by a sequence of one or more
	 * capital letters, such as "A", which evaluates to column 0, or "BE",
	 * which evaluates to column 56. The method {@link convert} is used to convert the
	 * sequence of capital letters into {@link int}s. The row is represented by the row number.
	 * @param cell The {@link String} that is parsed to determine the location of the cell
	 * 			that is specified.
	 * @return An array of {@code int}s with two elements. The element at index 0 is
	 * 			the row number, and the element at index 1 is the column number.
	 * @throws IllegalArgumentException If {@code cell} is not formatted as described above.
	 * @see toDec
	 */
	private static int[] getIndices(String cell) {
		if (cell.matches("[A-Z]+\\d+")) { // Something like "A1" or "AB50"
			Pattern regex = Pattern.compile("\\d+");
			Matcher m = regex.matcher(cell);
			int row = 0;
			int col = 0;
			if (m.find()) {
				col = toDec(cell.substring(0, m.start()));
				row = Library.fromString(cell, m.start(), cell.length());
			}
			return new int[] {row - 1, col};
		} else {
			throw new IllegalArgumentException();
		}
	}

	/**
	 * Replaces the current spreadsheet with a spreadsheet with ten rows and seven columns,
	 * and every {@link Cell} set to empty.
	 */
	private void newSheet () {
		Cell[][] spreadsheet = new Cell[10][7];
		for (int i = 0; i < spreadsheet.length; i++) {
			for (int j = 0; j < spreadsheet[0].length; j++) {
				spreadsheet[i][j] = new Cell();
			}
		}
		this.spreadsheet = spreadsheet;
	}

	/**
	 * Accepts a {@link String} ({@code s}) and an {@code int} ({@code n}), then concatenates
	 * {@code s} to itself <i> {@code n - 1} </i> times.
	 * @param s The {@link String} to be concatenated.
	 * @param n The number of times, plus one, that {@code s}
	 * 			is to be concatenated to itself.
	 * @return The resulting {@link String}.
	 */
	private static String multistr (String s, int n) {
		String result = "";
		for (int i = 0; i < n; i++) {
			result += s;
		}
		return result;
	}
	
	/**
	 * Converts a decimal {@code int} into base-26 (3 -> C, 32 -> AF, etc.),
	 * formatted as a {@link String}.
	 * @param n The {@code int} to be converted.
	 * @return The resulting {@link String}.
	 */
	private static String toBase26 (int n) {
		if (n < 26) {
			return Character.toString((char) (n + 65));
		} else if (n == 26) {
			return "AA";
		} else {
			String result = "";
			while (n > 26) {
				result = (char) (n % 26 + 65) + result;
				n /= 26;
			}
			result = (char) ((n + 1) % 26 + 63) + result;
			return result;
		}
	}
	
	/**
	 * Converts a base-26 number made up of capital letters into a base-10 {@code int}.
	 * @param n The base-26 integer, stored as a {@link String}, to be converted.
	 * @return The {@code int} that was parsed from the {@link String}.
	 */
	private static int toDec (String n) {
		int total = 0;
		total += (int) n.charAt(n.length() - 1) - 65;
		int timesBy = 26;
		for (int i = n.length() - 2; i >= 0; i--) {
			total += ((int) n.charAt(i) - 64) * timesBy;
			timesBy *= 26;
		}
		return total;
	}
}