package com.capgemini.starterkit.javafx.smallibrary.dataprovider.data;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

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

	public static BookVO fromJSONObject(JSONObject jsonBook) {
		Long bookId = jsonBook.getLong("id");
		String bookTitle = jsonBook.getString("title");
		Set<AuthorVO> bookAuthors = new HashSet<>();

		JSONArray authorsArray = jsonBook.getJSONArray("authors");
		for (int i = 0; i < authorsArray.length(); ++i) {
			JSONObject author = authorsArray.getJSONObject(i);
			bookAuthors.add(AuthorVO.fromJSONObject(author));
		}
		return new BookVO(bookId, bookTitle, bookAuthors);
	}
}
