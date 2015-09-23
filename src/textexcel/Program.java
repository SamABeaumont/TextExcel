package textexcel;

import java.io.*;
import java.util.*;

/**
 * A class that contains the main portion of the {@link textexcel} program.
 * @author Sam Beaumont
 * <div style="position: relative; right: 40px; font-weight: bold">
 * 
 * Class:
 * 
 * </div>
 * <div>
 * 
 * 		AP Computer Science, Period 1
 * 
 * </div>
 * <div style="position: relative; right: 40px; font-weight: bold">
 * 
 * Project:
 * 
 * </div>
 * <div>
 * 
 * 		TextExcel, #3
 * 
 * </div>
 * <div style="position: relative; right: 40px; font-weight: bold">
 * 
 * Extra Credit:
 * 
 * </div>
 * <ul>
 * 		<li> Command error handling </li>
 * </ul>
 */
public class Program {
	// So that the spreadsheet can be modified by any method in this class.
	private static Spreadsheet spr = new Spreadsheet();
	
	private Program () {} // prevent this class from being instantiated
	
	public static void main (String[] args) {
		System.out.println("Welcome to TextExcel!");
		
		// Prime the command loop
		System.out.print("\nEnter a command: ");
		Scanner console = new Scanner(System.in);
		String command = console.nextLine();
		
		// Command loop
		while (!command.equalsIgnoreCase("exit")) {
			if (command.equalsIgnoreCase("print")) { // Printing the spreadsheet
				System.out.println("\n" + spr);
			} else if (command.equalsIgnoreCase("help")) {
				help();
			} else if (command.matches("[A-Z]+\\d+.*")) {
				cell(command); // set or display an individual cell
			} else if (Library.startsWithIgnoreCase(command, "clear")) {
				clear(command); // clear cell/sheet
			} else if (Library.startsWithIgnoreCase(command, "save")) {
				handleSave(command); // save the spreadsheet
			} else if (Library.startsWithIgnoreCase(command, "load")) {
				load(command); // load a spreadsheet
			} else {
				// The command is completely invalid, ex. "This isn't working." or "al;skdfa;df"
				printError();
			}
			
			// Get ready for the next iteration of the loop
			System.out.print("Enter a command: ");
			command = console.nextLine();
		}
		
		console.close(); // To get rid of those annoying "resource leak" warnings
		System.out.print("\nFarewell!");
	}
	
	/**
	 * Processes the user's command, assuming that it starts with a valid cell identifier.
	 * @param command The command that the user has typed.
	 */
	private static void cell (String command) {
		if (command.matches("[A-Z]+\\d+ = \\(.*\\)") || command.matches("[A-Z]+\\d+ = [A-Z]+\\(.*\\)")) {
			
		} else if (command.matches("[A-Z]+\\d+ = .*")) { // Setting a cell
			String setWith = command.substring(command.indexOf("=") + 2, command.length());
			try {
				spr.set(command.substring(0, command.indexOf("=") - 1), new Cell(setWith));
			} catch (ArithmeticException e) {
				printError("Formulae containing non-real numbers are not supported"
						+ " by this application.");
			} catch (NumberFormatException nfe) {
				printError("Invalid formula.");
			} catch (StackOverflowError soe) {
				printError("Circular formula reference.");
			} catch (InvalidCellException e) { // Cell format is invalid
				printError();
			} catch (IllegalArgumentException e) {
				printError("Illegal cell range.");
			}
		} else if (command.matches("[A-Z]+\\d+")) { // Printing a cell
			System.out.println(command + " = " + spr.get(command) + "\n");
		} else {
			printError();
		}
	}
	
	/**
	 * Processes the user's command, assuming that it starts with {@code clear},
	 * case-insensitive. Either clears a cell, clears the entire spreadsheet,
	 * takes no action at all, or prints an error message, depending on the input.
	 * No guarantees are made concerning the functionality of this method if
	 * {@link Library#startsWithIgnoreCase(String, String)} does not return {@code true}
	 * when passed the parameters {@code (command, "clear")}.
	 * @param command The command that the user has typed.
	 */
	private static void clear (String command) {
		if (command.equalsIgnoreCase("clear")) { // clear the whole spreadsheet
			spr.clear();
		// clear an individual cell
		} else if (command.matches("(?iu)clear [A-Z]+\\d+")) {
			// Two if statements so that we don't get an IndexOutOfBoundsException
			if (spr.get(command.substring(command.indexOf(" ") + 1,
					command.length())).getType()
					!= Types.EMPTY) {
				/* 
				 * If we set it to empty no matter what, the spreadsheet will be
				 * unnecessarily resized if the user tries to clear a cell
				 * outside of the bounds of the spreadsheet
				 */
				spr.set(command.substring(command.indexOf(" ") + 1, command.length()), new Cell());
			}
		} else { // The input starts with "clear", but the rest is not valid
			printError();
		}
	}
	
	/**
	 * Processes the user's command, assuming that it starts with {@code save},
	 * case-insensitive. Either saves the file to the specified location
	 * or prints an error message, depending on whether or not this program
	 * is authorized to to modify the file at the specified location.
	 * No guarantees are made concerning the functionality of this method if
	 * {@link Library#startsWithIgnoreCase(String, String)} does not return {@code true}
	 * when passed the parameters {@code (command, "save")}.
	 * @param command The command that the user has typed.
	 */
	private static void handleSave (String command) {
		if (command.equalsIgnoreCase("save")) {
			String loc = spr.getPath();
			if (loc == null) {
				int i;
				for (i = 1; new File("Spreadsheet" + i + ".txt").exists(); i++);
				save("Spreadsheet" + i + ".txt",
						"An error occurred as this program attempted to save the spreadsheet"
						+ " to the path " + getPath("Spreadsheet" + i + ".txt"),
						"This program does not have permission to save the spreadsheet to the path"
						+ " Spreadsheet" + i + ".txt");
			} else {
				save(spr.getPath(),
						"The spreadshet could not be saved to its previous location (" + spr.getPath() + ")."
						+ "\nConsider manually saving it to a different filepath.",
						"This program does not have permission to save the spreadsheet\n"
						+ "to its previous location. Consider saving it manually to  different filepath.");
			}
		} else if (command.matches("(?iu)save .+\\.txt")) {
			String saveTo = command.substring(command.indexOf(" ") + 1, command.length());
			save(saveTo,
					"An error occurred as this program attempted to save the spreadsheet to "
					+ getPath(saveTo),
					"This program does not have permission to save the spreadsheet to " + saveTo);
		} else {
			printError("The file that the spreadsheet is saved to must have the extension .txt");
		}
	}
	
	/**
	 * Attempts to save the spreadsheet to the file at the specified path. If a
	 * {@link FileNotFoundException} is thrown, calls {@link Program#printError(String)}, passing the
	 * {@link String} {@code notFoundMessage} as a parameter. If a {@link SecurityException} is thrown,
	 * calls {@link Program#printError(String)}, passing the {@link String} {@code securityMessage}
	 * as a parameter.
	 * @param saveTo The filepath to save the spreadsheet to.
	 * @param notFoundMessage The message to be printed if a {@link FileNotFoundExcepton} is thrown.
	 * @param securityMessage The message to be printed if a {@link SecurityException} is thrown.
	 */
	private static void save (String saveTo, String notFoundMessage, String securityMessage) {
		try {
			spr.save(saveTo);
		} catch (FileNotFoundException nf) {
			printError(notFoundMessage);
		} catch (SecurityException se) {
			printError(securityMessage);
		}
	}
	
	/**
	 * Processes the user's command, assuming that it starts with {@code load}, case-insensitive.
	 * Attempts to load a file that the user inputs and use it as a spreadsheet. If the spreadsheet
	 * cannot be loaded, prints an error message.
	 * @param command The command that the user has typed.
	 */
	private static void load (String command) {
		if (command.matches("(?iu)load .+\\.txt")) {
			String loadFrom = command.substring(command.indexOf(" ") + 1, command.length());
			try {
				spr = new Spreadsheet(loadFrom);
			} catch (FileNotFoundException nf) {
				printError("No file was found at the path \"" + getPath(loadFrom) + "\".");
			} catch (SecurityException se) {
				printError("This program does not have permission to access the file at \""
						+ loadFrom + "\".");
			}
		} else {
			printError("A valid filepath must be input, with the extension .txt");
		}
	}
	
	/**
	 * Returns the absolute filepath, as a {@link String}, of the specified file.
	 * @param filename The file at the filepath to be evaluated
	 */
	private static String getPath (String filename) {
		return new File(filename).getAbsolutePath();
	}

	/**
	 * Calls {@link Program#printError(String)}, passing the {@link String} 
	 * "{@code Invalid command.}" as a parameter.
	 */
	private static void printError () {
		printError("Invalid command.");
	}
	
	/**
	 * Prints an error message to the console.
	 * @param message The message to be printed.
	 */
	private static void printError (String message) {
		System.out.println("ERROR: " + message + " For more information, type HELP.\n");
	}
	
	/**
	 * Prints a user's guide to the command line.
	 */
	private static void help () {
		System.out.println("\nThis program is a command-line spreadsheet application.");
		System.out.println("It supports the setting of cells to many different data types.");
		System.out.println("There are also commands available to save, display, load,");
		System.out.println("and clear spreadsheets.\n\n");
		
		
		System.out.println("LOADING A SPREADSHEET\n");
		
		System.out.println("The main purpose of this program is to enable the user");
		System.out.println("to create and edit spreadsheets.");
		System.out.println("To load a previously saved spreadsheet, use the following syntax:\n");
		
		System.out.println("\tload <filepath>\n");
		
		System.out.println("<filepath> is the location of the file to be loaded");
		System.out.println("and parsed into a spreadsheet.");
		System.out.println("If an invalid filepath is entered,");
		System.out.println("no file is found at the specified path,");
		System.out.println("or this program does not have permission to access");
		System.out.println("the file at the specified path, an error message");
		System.out.println("is printed to the command line and the program");
		System.out.println("asks for more input as usual.\n");
		System.out.println("The spreadsheet must be saved as a text file with the extension .txt\n");
		
		System.out.println("If an error occurs while this program");
		System.out.println("attempts to load the spreadsheet, an error message is printed");
		System.out.println("to the command line, the spreadsheet is not loaded, and this program");
		System.out.println("prompts the user for another command.\n\n");
		
		
		System.out.println("SAVING A SPREADSHEET\n");
		
		System.out.println("To save a spreadsheet to a specific filepath, use the following syntax:\n");
		
		System.out.println("\tsave <filepath>\n");
		
		System.out.println("If the above command is typed, this program will attempt to save");
		System.out.println("the current spreadsheet as a plain-text file to <filepath>.");
		System.out.println("Alternatively it is possible to type a command such as this:\n");
		
		System.out.println("\tsave\n");
		
		System.out.println("If the above command is typed, this program will attempt to save");
		System.out.println("the spreadsheet to the same file that it was loaded from, or");
		System.out.println("a default title such as \"Spreadsheet1.txt\", \"Spreadsheet2.txt\",");
		System.out.println("etc.\n");
		
		System.out.println("If some sort of error occurs while this program attempts");
		System.out.println("to save the spreadsheet to a file, an error message will be printed");
		System.out.println("to the command line, the spreadsheet will not be saved,");
		System.out.println("and the user will be prompted for another command as usual.");
	}
}