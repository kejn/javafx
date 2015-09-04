package com.capgemini.starterkit.javafx.smallibrary.dataprovider.data;

public enum ErrorVO {
	SEARCH, ADD, DELETE;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
