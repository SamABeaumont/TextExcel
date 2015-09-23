package textexcel;

/**
 * A class to search a {@link String} for unmatched parentheses.
 */
class ParenMatcher {
	private String str;
	
	/**
	 * Initializes a new {@code ParenMatcher} object using the given {@link String}.
	 * Everything besides parentheses is stripped from the initial {@link String}
	 * when the {@code ParenMatcher} object is initialized.
	 * @param str The {@link String} to initialize the object with.
	 */
	ParenMatcher (String str) {
		// Remove anything besides parentheses
		this.str = str.replaceAll("[^\\(\\)]", "");
	}
	
	/**
	 * Returns {@code true} if there are any more pairs of parentheses to remove,
	 * {@code false} otherwise.
	 */
	boolean hasNext () {
		return str.contains("(")
				&& str.substring(str.indexOf("(") + 1, str.length()).contains(")");
	}
	
	/**
	 * Removes the next pair of parentheses.
	 */
	void remove () {
		if (str.contains("(")
				&& str.substring(str.indexOf("(") + 1, str.length()).contains(")")) {
			String temp = str.substring(str.indexOf("(") + 1,
					str.length()).replaceFirst("\\)", "");
			str = str.substring(0, str.indexOf("(")) + temp;

		}
	}
	
	/**
	 * Returns {@code true} if all matchable parentheses have been removed
	 * and no parentheses remain, {@code false} otherwise.
	 * @return
	 */
	boolean isEmpty () {
		return str.length() == 0;
	}
	
	public String toString () {
		return str;
	}
}