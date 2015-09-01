package com.capgemini.starterkit.javafx.smallibrary.dataprovider.data;

import java.util.Set;

public class BookVO {
	private Long id;
	private String title;
	private Set<AuthorVO> authors;

	public BookVO(Long id, String title, Set<AuthorVO> authors) {
		this.id = id;
		this.title = title;
		this.authors = authors;
	}

	public BookVO(BookVO other) {
		this.id = other.id;
		this.title = other.title;
		this.authors = other.authors;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Set<AuthorVO> getAuthors() {
		return authors;
	}

	public void setAuthors(Set<AuthorVO> authors) {
		this.authors = authors;
	}
}
