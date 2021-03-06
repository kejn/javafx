package com.capgemini.starterkit.javafx.imageviewer.dataprovider.data;

/**
 * Contain information about image file name.
 *
 * @author KNIEMCZY
 */
public class ImageFileVO {
	private String name;

	public ImageFileVO(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Image [name=" + name + "]";
	}
}
