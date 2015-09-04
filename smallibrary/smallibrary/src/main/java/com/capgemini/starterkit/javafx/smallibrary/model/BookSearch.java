package com.capgemini.starterkit.javafx.smallibrary.model;

import java.util.ArrayList;
import java.util.List;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

public class BookSearch {
	private final StringProperty title = new SimpleStringProperty();
	private final StringProperty author = new SimpleStringProperty();
	private final ListProperty<BookVO> result = new SimpleListProperty<>(
			FXCollections.observableList(new ArrayList<>()));

	public final String getTitle() {
		return title.get();
	}

	public final void setTitle(String value) {
		title.set(value);
	}

	public StringProperty titleProperty() {
		return title;
	}

	public final String getAuthor() {
		return author.get();
	}

	public final void setAuthor(String value) {
		author.set(value);
	}

	public StringProperty authorProperty() {
		return author;
	}

	public final List<BookVO> getResult() {
		return result.get();
	}

	public final void setResult(List<BookVO> value) {
		result.setAll(value);
	}

	public ListProperty<BookVO> resultProperty() {
		return result;
	}

	@Override
	public String toString() {
		return "PersonSearch [name=" + title + "author=" + author + ", result=" + result + "]";
	}
}
