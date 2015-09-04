package com.capgemini.starterkit.javafx.smallibrary.dataprovider.data;

/**
 * Defines type of operation error category.
 *
 * @author KNIEMCZY
 */
public enum ErrorVO {
	SEARCH, ADD, DELETE;

	@Override
	public String toString() {
		return this.name().toLowerCase();
	}
}
