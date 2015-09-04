package com.capgemini.starterkit.javafx.smallibrary.dataprovider.data;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.json.JSONObject;

public class AuthorVO {
	private Long id;
	private String firstName;
	private String lastName;

	public AuthorVO(Long id, String firstName, String lastName) {
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public static AuthorVO fromString(String author) {
		String[] firstAndLastName = author.split("\\s+");
		int wordsInAuthor = firstAndLastName.length;
		String firstNames = Arrays.asList(firstAndLastName).stream().limit(wordsInAuthor - 1)
				.collect(Collectors.joining(" "));
		String lastName = firstAndLastName[wordsInAuthor - 1];
		return new AuthorVO(null, firstNames, lastName);
	}

	public static AuthorVO fromJSONObject(JSONObject jsonAuthor) {
		Long authorId = jsonAuthor.getLong("id");
		String authorFirstName = jsonAuthor.getString("firstName");
		String authorLastName = jsonAuthor.getString("lastName");
		return new AuthorVO(authorId, authorFirstName, authorLastName);
	}
}
