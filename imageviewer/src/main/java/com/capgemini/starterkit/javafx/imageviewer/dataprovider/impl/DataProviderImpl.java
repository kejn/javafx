package com.capgemini.starterkit.javafx.imageviewer.dataprovider.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.imageviewer.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;

public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	/*
	 * REV: lista wynikowa nie powinna byc przechowywana jako pole w klasie
	 */
	private Collection<ImageFileVO> images = new ArrayList<>();

	public DataProviderImpl() {
	}

	@Override
	public Collection<ImageFileVO> findImages(String dirPath) {
		LOG.debug("Entering findImages()");
		images.clear();

		File folder = new File(dirPath);
		Collection<File> listOfFiles = new ArrayList<>(Arrays.asList(folder.listFiles()));
		LOG.debug("SIZE: " + listOfFiles.size());

		/*
		 * REV: lepiej uzyc FilenameFilter
		 */
		for (File file : listOfFiles) {
			if (file.isFile() && isImage(file.getName())) {
				images.add(new ImageFileVO(file.getName()));
				LOG.debug(file.getName());
			}
		}

		Collection<ImageFileVO> result = images;

		LOG.debug("Leaving findImages()");
		return result;
	}

	private boolean isImage(String filename) {
		final String lower = filename.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".bmp")
				|| lower.endsWith(".gif") || lower.endsWith(".tif") || lower.endsWith(".tiff")) {
			return true;
		}
		return false;
	}
}
