package com.capgemini.starterkit.javafx.imageviewer.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.starterkit.javafx.imageviewer.dataprovider.data.ImageFileVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class ImageSearch {

	private final StringProperty dirPath = new SimpleStringProperty();
	private final ListProperty<ImageFileVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));

	public final String getDirPath() {
		return dirPath.get();
	}

	public final void setDirPath(String value) {
		dirPath.set(value);
	}

	public StringProperty dirPathProperty() {
		return dirPath;
	}

	public final List<ImageFileVO> getResult() {
		return result.get();
	}

	public final void setResult(List<ImageFileVO> value) {
		result.setAll(value);
	}

	public ListProperty<ImageFileVO> resultProperty() {
		return result;
	}

	@Override
	public String toString() {
		return "ImageSearch [name=" + dirPath + ", result=" + result + "]";
	}

}
