package com.capgemini.starterkit.javafx.smallibrary.dataprovider;

import java.util.Collection;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl.DataProviderImpl;

public interface DataProvider {

	DataProvider INSTANCE = new DataProviderImpl();

	Collection<BookVO> findBooks(String titlePrefix);

	BookVO addBook(String title, String authors) throws InterruptedException;

}
