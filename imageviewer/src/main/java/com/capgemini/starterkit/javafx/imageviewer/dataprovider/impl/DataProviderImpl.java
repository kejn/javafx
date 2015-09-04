package com.capgemini.starterkit.javafx.imageviewer.dataprovider.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.imageviewer.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;

public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	public DataProviderImpl() {
	}

	@Override
	public Collection<ImageFileVO> findImages(String dirPath) {
		LOG.debug("Entering findImages()");
		Collection<ImageFileVO> images = new ArrayList<>();

		File folder = new File(dirPath);

		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				if (isImage(name)) {
					return true;
				}
				return false;
			}
		};

		Collection<File> listOfFiles = new ArrayList<>(Arrays.asList(folder.listFiles(filter)));
		LOG.debug("SIZE: " + listOfFiles.size());

		for (File file : listOfFiles) {
			if (file.isFile() && isImage(file.getName())) {
				images.add(new ImageFileVO(file.getName()));
				LOG.debug(file.getName());
			}
		}

		LOG.debug("Leaving findImages()");
		return images;
	}

	/**
	 * Checks file formats basing on file names.
	 * 
	 * @param filename
	 *            name of file
	 * @return true if <b>filename</b> suggests it is an image.
	 */
	private boolean isImage(String filename) {
		final String lower = filename.toLowerCase();
		if (lower.endsWith(".jpg") || lower.endsWith(".jpeg") || lower.endsWith(".png") || lower.endsWith(".bmp")
				|| lower.endsWith(".gif") || lower.endsWith(".tif") || lower.endsWith(".tiff")) {
			return true;
		}
		return false;
	}
}
