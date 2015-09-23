package textexcel;

/**
 * A class to store static methods that are used by the rest of {@link textexcel}. Cannot be instantiated.
 * @author Sam Beaumont
 */
class Library {
	private Library () {} // This class shouldn't be instantiated.
	
	/**
	 * Throws an {@link AssertionError} if the specified boolean expression
	 * does not evaluate to {@code true}.
	 * @param test The boolean expression.
	 */
	static void assertionTest (boolean test) {
		if (!test) {
			throw new AssertionError();
		}
	}
	
	/**
	 * Returns {@code true} if any portion of a {@link String} contains a regular expression,
	 * {@code false} otherwise.
	 * @param s The {@link String} to be searched.
	 * @param regex The regex to be searched for.
	 */
	static boolean containsRegex (String s, String regex) {
		return java.util.regex.Pattern.compile(regex).matcher(s).find();
	}
	
	/**
	 * Determines whether a {@link String} ends with another {@link String},
	 * regardless of case.
	 * @param s The original {@link String}.
	 * @param o The {@link String} that is used to check the other {@link String}.
	 * @return {@code true} if the {@link String}s match, {@code false} otherwise.
	 */
	static boolean endsWithIgnoreCase (String s, String o) {
		return s.toLowerCase().endsWith(o.toLowerCase());
	}
	
	/**
	 * Uses {@link Integer}{@code .parseInt} and {link String}{@code .substring} to convert
	 * a {@link String} between the given indices into an {@code int}.
	 * @param s The {@link String} to be parsed.
	 * @param index1 The index that the substring starts at, inclusive.
	 * @param index2 The index of that the substring ends at, exclusive.
	 * @return The {@code int} that was parsed from the {@link String}.
	 * @throws NumberFormatException If the specified substring is not a valid integer.
	 * @throws StringIndexOutOfBoundsException If one or more of the specified indices
	 * 			is outside of the bounds of the {@code String}.
	 */
	static int fromString (String s, int index1, int index2) {
		return Integer.parseInt(s.substring(index1, index2));
	}
	
	/**
	 * Determines whether a {@link String} contains any unmatched parentheses.
	 * @param s The {@link String} to be evaluated.
	 * @return {@code true} if <b>{@code s}</b> contains no unmatched parentheses,
	 * 			{@code false} otherwise.
	 */
	static boolean parensAreClosed (String s) {
		ParenMatcher pm = new ParenMatcher(s);
		while (pm.hasNext()) {
			pm.remove();
		}
		return pm.isEmpty();
	}
	
	/**
	 * Determines whether a {@link String} starts with another {@link String},
	 * regardless of case.
	 * @param s The original {@link String}.
	 * @param o The {@link String} that is used to check the other {@link String}.
	 * @return {@code true} if the {@link String}s match, {@code false} otherwise.
	 */
	static boolean startsWithIgnoreCase (String s, String o) {
		return s.toLowerCase().startsWith(o.toLowerCase());
	}
}