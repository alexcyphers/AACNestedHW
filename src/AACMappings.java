import java.util.NoSuchElementException;
import java.util.Scanner;

import edu.grinnell.csc207.util.AssociativeArray;
import edu.grinnell.csc207.util.KeyNotFoundException;
import edu.grinnell.csc207.util.NullKeyException;

import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;



/**
 * Creates a set of mappings of an AAC that has two levels,
 * one for categories and then within each category, it has
 * images that have associated text to be spoken. This class
 * provides the methods for interacting with the categories
 * and updating the set of images that would be shown and handling
 * an interactions.
 * 
 * @author Catie Baker & Alex Cyphers
 *
 */
public class AACMappings implements AACPage {

	/*
	 * The associative array that is used for mapping filenames
	 * or names to their categories.
	 */
	private AssociativeArray<String, AACCategory> categories;

	/*
	 * The current category.
	 */
	private AACCategory currCategory;

	/*
	 * The default category.
	 */
	private AACCategory defaultCategory;
	
	/**
	 * Creates a set of mappings for the AAC based on the provided
	 * file. The file is read in to create categories and fill each
	 * of the categories with initial items. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * @param filename the name of the file that stores the mapping information
	 */
	public AACMappings(String filename) {
		this.categories = new AssociativeArray<>();
		this.defaultCategory = new AACCategory("");
		this.currCategory = this.defaultCategory;
		try {
			Scanner scanner = new Scanner(new File(filename));
			AACCategory tempLoc = null;

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();

				if (line.isEmpty()) {
					continue;
				} // if

				if (line.startsWith(">")) {
					String image = line.substring(1, line.indexOf(" "));
					String text = line.substring(line.indexOf(" ") + 1);
					tempLoc.addItem(image, text);
				} else {
					String image = line.substring(0, line.indexOf(" "));
					String text = line.substring(line.indexOf(" ") + 1);
					tempLoc = new AACCategory(text);
					try {
						this.categories.set(image, tempLoc);
					} catch (NullKeyException e) {
						// Should not recieve a null key.
					} // try/catch
				} // if/else
			} // while-loop
			scanner.close();
		} catch (FileNotFoundException e) {
			throw new NullPointerException("File not found: " + filename);
		} // try/catch
	} // AACMappings(String)
	
	/**
	 * Given the image location selected, it determines the action to be
	 * taken. This can be updating the information that should be displayed
	 * or returning text to be spoken. If the image provided is a category, 
	 * it updates the AAC's current category to be the category associated 
	 * with that image and returns the empty string. If the AAC is currently
	 * in a category and the image provided is in that category, it returns
	 * the text to be spoken.
	 * @param imageLoc the location where the image is stored
	 * @return if there is text to be spoken, it returns that information, otherwise
	 * it returns the empty string
	 * @throws NoSuchElementException if the image provided is not in the current 
	 * category
	 */
	public String select(String imageLoc) {
		if (this.currCategory == this.defaultCategory) {
			try {
				this.currCategory = this.categories.get(imageLoc);
				return "";
			} catch (KeyNotFoundException e) {
				throw new NoSuchElementException("Image location not found: " + imageLoc);
			} // try-catch
		} else {
			if (this.currCategory.hasImage(imageLoc)) {
				return this.currCategory.select(imageLoc);
			} else {
				throw new NoSuchElementException("Image location not found: " + imageLoc);
			} // if/else
		} // try-catch
	} // select(String)
	
	/**
	 * Provides an array of all the images in the current category
	 * @return the array of images in the current category; if there are no images,
	 * it should return an empty array
	 */
	public String[] getImageLocs() {
		if (this.currCategory == this.defaultCategory) {
			return this.categories.keyStrings();
		} else {
			return this.currCategory.getImageLocs();
		} // if/else
	} // getImageLocs()
	
	/**
	 * Resets the current category of the AAC back to the default
	 * category
	 */
	public void reset() {
		this.currCategory = this.defaultCategory;
	} // reset()
	
	
	/**
	 * Writes the ACC mappings stored to a file. The file is formatted as
	 * the text location of the category followed by the text name of the
	 * category and then one line per item in the category that starts with
	 * > and then has the file name and text of that image
	 * 
	 * for instance:
	 * img/food/plate.png food
	 * >img/food/icons8-french-fries-96.png french fries
	 * >img/food/icons8-watermelon-96.png watermelon
	 * img/clothing/hanger.png clothing
	 * >img/clothing/collaredshirt.png collared shirt
	 * 
	 * represents the file with two categories, food and clothing
	 * and food has french fries and watermelon and clothing has a 
	 * collared shirt
	 * 
	 * @param filename the name of the file to write the
	 * AAC mapping to
	 */
	public void writeToFile(String filename) {
		try {
			PrintWriter pen = new PrintWriter(new File(filename));
			for (int i = 0; i < this.categories.size(); i++) {
				AACCategory category = new AACCategory(filename);
				pen.println(category.getCategory());
				String[] imageLocs = category.getImageLocs();
				for (int j = 0; j < imageLocs.length; j++) {
					pen.println(">" + imageLocs[j] + " " + category.select(imageLocs[j]));
				} // for-loop
			} // for-loop
			pen.close();
		} catch (FileNotFoundException e) {
			throw new NullPointerException("File not found: " + filename);
		} // try/catch
	} // writeToFile(String)
	
	/**
	 * Adds the mapping to the current category (or the default category if
	 * that is the current category)
	 * @param imageLoc the location of the image
	 * @param text the text associated with the image
	 */
	public void addItem(String imageLoc, String text) {
		if (this.currCategory == this.defaultCategory) {
			AACCategory category = new AACCategory(text);
			try {
				this.categories.set(imageLoc, category);
			} catch (NullKeyException e) {
				// There should not be a null key.
			} // try/catch
		} else {
			this.currCategory.addItem(imageLoc, text);
		} // if/else
	} // addItem(String, String)


	/**
	 * Gets the name of the current category
	 * @return returns the current category or the empty string if 
	 * on the default category
	 */
	public String getCategory() {
		return this.currCategory.getCategory();
	} // getCategory()


	/**
	 * Determines if the provided image is in the set of images that
	 * can be displayed and false otherwise
	 * @param imageLoc the location of the category
	 * @return true if it is in the set of images that
	 * can be displayed, false otherwise
	 */
	public boolean hasImage(String imageLoc) {
		return this.currCategory.hasImage(imageLoc);
	} // hasImage(String)
}

