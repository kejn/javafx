package com.capgemini.starterkit.javafx.smallibrary.dataprovider;

import java.io.IOException;
import java.util.Collection;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl.DataProviderImpl;

/**
 * Provides data for the application.
 *
 * @author KNIEMCZY
 */
public interface DataProvider {

	/**
	 * Instance of this interface.
	 */
	DataProvider INSTANCE = new DataProviderImpl();

	/**
	 * Finds books which title starts with <b>titlePrefix</b>.
	 *
	 * @param titlePrefix
	 *            first characters of requested book title
	 * @return collection of books matching the given criteria
	 */
	Collection<BookVO> findBooks(String titlePrefix) throws IOException;

	/**
	 * Adds a book.
	 *
	 * @param title
	 *            of the book to be added.
	 * @param authors
	 *            collecion of strings representing book authors in format<br>
	 *            <code>"firstName firstName2 firstName3... lastName"</code><br>
	 *            eg. "Jack Alexander Wild" or "Nancy Smith"
	 * @return Added book.
	 */
	BookVO addBook(String title, Collection<String> authors) throws IllegalArgumentException, IOException;

	/**
	 * Deletes a book from the database.
	 *
	 * @param id
	 *            of the book to be deleted
	 */
	Void deleteBook(Long id) throws IllegalArgumentException, IOException;

}
