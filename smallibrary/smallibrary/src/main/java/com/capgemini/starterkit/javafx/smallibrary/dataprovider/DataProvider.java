package com.capgemini.starterkit.javafx.smallibrary.dataprovider;

import java.io.IOException;
import java.util.Collection;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl.DataProviderImpl;

public interface DataProvider {

	DataProvider INSTANCE = new DataProviderImpl();

	Collection<BookVO> findBooks(String titlePrefix) throws IOException;

	BookVO addBook(String title, Collection<String> authors) throws IllegalArgumentException, IOException;

	Boolean deleteBook(Long id) throws IllegalArgumentException, IOException;

}
