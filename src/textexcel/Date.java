package textexcel;

/**
 * A class to represent dates.
 * @author Sam Beaumont
 */
class Date {
	private int month;
	private int day;
	private int year;
	
	/**
	 * Constructs a new {@link Date} from a {@link String}.
	 * @param s The {@link String} to be evaluated.
	 * @throws IllegalArgumentException If the {@link String} cannot be parsed into a date.
	 */
	Date (String s) {
		String monthNames = "(?iu)(Jan|January|Feb|February|Mar|March|Apr|April|May|" + 
				"Jun|June|Jul|July|Aug|August|Sep|Sept|September|Oct|October|" +
				"Nov|November|Dec|December)";
		if (s.matches("\\d{1,2}/\\d{1,2}/\\d+")) { // Something like "5/3/2014"
			setSlashDate(s);
		} else if (s.matches(monthNames + ".? \\d{1,2},? \\d+")) {
			// Something like "Apr. 5, 2014"
			setMdyDate(s);
		} else if (s.matches("\\d{1,2} " + monthNames + "(.|,) \\d+")) {
			// Something like "5 April 2014"
			setDmyDate(s);
		} else {
			// Not a valid date
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Converts the {@code Date} into a {@code String}.
	 * @return A {@code String} representing the {@code Date}.
	 */
	public String toString () {
		return ((Integer.toString(month).length() < 2) ? "0" + month
				: month) + "/" +
				((Integer.toString(day).length() < 2) ? "0" + day : day) + "/" + year;
	}
	
	/**
	 * Parses a {@code String} formatted like this: {@code 12/3/2014}, using it to
	 * set the month, day, and year fields.
	 * @param s The {@code String} to be parsed.
	 */
	private void setSlashDate (String s) {
		month = Library.fromString(s, 0, s.indexOf("/"));
		day = Library.fromString(s, s.indexOf("/") + 1,
				s.lastIndexOf("/"));
		year = Library.fromString(s, s.lastIndexOf("/") + 1, s.length());
		if (isInvalid()) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Parses a {@link String} in MDY format with the month represented as a word or
	 * abbreviation, using it to instantiate a new {@link Date} object.
	 * @param s The {@link String} to be parsed.
	 * @throws IllegalArgumentException If the resulting date is not valid.
	 */
	private void setMdyDate (String s) {
		setMonth(s.substring(0, s.indexOf(" "))); // Set the month
		
		// Set the day
		if (s.indexOf(",") != -1) { // For some reason, s.matches(",") doesn't work.
			day = Library.fromString(s, s.indexOf(" ") + 1, s.indexOf(","));
		} else {
			day = Library.fromString(s, s.indexOf(" ") + 1, s.lastIndexOf(" "));
		}
		
		// Set the year
		year = Library.fromString(s, s.lastIndexOf(" ") + 1, s.length());
		if (isInvalid()) {
			throw new IllegalArgumentException();
		}
	}
	
	/**
	 * Parses a {@link String} in MDY format with the month represented as a word or
	 * abbreviation, using it to instantiate a new {@link Date} object.
	 * @param s The {@link String} to be parsed.
	 * @throws IllegalArgumentException If the resulting date is not valid.
	 */
	private void setDmyDate (String s) {
		setMonth(s.substring(s.indexOf(" ") + 1, s.lastIndexOf(" ")));
		day = Library.fromString(s, 0, s.indexOf(" "));
		year = Library.fromString(s, s.lastIndexOf(" ") + 1, s.length());
		if (isInvalid()) {
			throw new IllegalArgumentException();
		}
	}
	
	private boolean isInvalid () {
		return
				// no such thing as year 0
				year == 0
				
				// invalid months
				|| month < 1 || month > 12
				
				// no day 0
				|| day < 1
				
				// February
				|| month == 2 && (year % 4 == 0 && day > 29
				|| year % 4 != 0 && day > 28)
				
				// 30-day months
				|| month == 4 && day > 30
				|| month == 6 && day > 30 || month == 9 && day > 30
				|| month == 11 && day == 30
				
				// normal months
				|| day > 31
		;
	}
	
	/**
	 * Parses a {@code String}, using it to set the month of the {@code Date},
	 * assuming that the month is formatted as a word, not a number.
	 * @param s The {@code String} to be parsed.
	 */
	private void setMonth (String s) {
		if (Library.startsWithIgnoreCase(s, "Jan")) {
			month = 1;
		} else if (Library.startsWithIgnoreCase(s, "Feb")
				|| Library.startsWithIgnoreCase(s, "February")) {
			month = 2;
		} else if (Library.startsWithIgnoreCase(s, "Mar")
				|| Library.startsWithIgnoreCase(s, "March")) {
			month = 3;
		} else if (Library.startsWithIgnoreCase(s, "Apr")
				|| Library.startsWithIgnoreCase(s, "April")) {
			month = 4;
		} else if (Library.startsWithIgnoreCase(s, "May")) {
			month = 5;
		} else if (Library.startsWithIgnoreCase(s, "Jun")
				|| Library.startsWithIgnoreCase(s, "June")) {
			month = 6;
		} else if (Library.startsWithIgnoreCase(s, "Jul")
				|| Library.startsWithIgnoreCase(s, "July")) {
			month = 7;
		} else if (Library.startsWithIgnoreCase(s, "Aug")
				|| Library.startsWithIgnoreCase(s, "August")) {
			month = 8;
		} else if (Library.startsWithIgnoreCase(s, "Sep")
				|| Library.startsWithIgnoreCase(s, "Sept")
				|| Library.startsWithIgnoreCase(s, "September")) {
			month = 9;
		} else if (Library.startsWithIgnoreCase(s, "Oct")
				|| Library.startsWithIgnoreCase(s, "October")) {
			month = 10;
		} else if (Library.startsWithIgnoreCase(s, "Nov")
				|| Library.startsWithIgnoreCase(s, "November")) {
			month = 11;
		} else if (Library.startsWithIgnoreCase(s, "Dec")
				|| Library.startsWithIgnoreCase(s, "December")) {
			month = 12;
		} else {
			throw new IllegalArgumentException();
		}
	}
}