package com.capgemini.starterkit.javafx.imageviewer.dataprovider;

import java.util.Collection;

import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.impl.DataProviderImpl;

public interface DataProvider {

	/**
	 * Instance of this interface.
	 */
	DataProvider INSTANCE = new DataProviderImpl();

	/**
	 * Finds images with their name containing specified string.
	 *
	 * @param name
	 *            string contained in name
	 * @return collection of images matching the given criteria
	 */
	Collection<ImageFileVO> findImages(String name);
}
