package com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.AuthorVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	private final String URL_BOOKS = "http://localhost:9721/workshop/rest/books/";

	private Collection<BookVO> books = new ArrayList<>();

	public DataProviderImpl() {
	}

	@Override
	public synchronized Collection<BookVO> findBooks(String titlePrefix) {
		LOG.debug("Entering findBooks()");

		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		if (titlePrefix == null || titlePrefix.equals("*")) {
			titlePrefix = "";
		}

		try {
			String query = String.format("titlePrefix=%s", URLEncoder.encode(titlePrefix, "UTF-8"));
			URL url = new URL(URL_BOOKS + "books-by-title?" + query);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setDefaultUseCaches(false);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("Make sure you have launched the server instance!");
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		LOG.debug("Response: " + response.toString());
		Collection<BookVO> result = new ArrayList<>();

		if (response.length() > 0) {
			JSONArray bookArray = new JSONArray(response.toString());
			for (int i = 0; i < bookArray.length(); ++i) {
				JSONObject book = bookArray.getJSONObject(i);
				Long bookId = book.getLong("id");
				String bookTitle = book.getString("title");
				Set<AuthorVO> bookAuthors = new HashSet<>();

				JSONArray authorsArray = book.getJSONArray("authors");
				for (int j = 0; j < authorsArray.length(); ++j) {
					JSONObject author = authorsArray.getJSONObject(j);
					Long authorId = author.getLong("id");
					String authorFirstName = author.getString("firstName");
					String authorLastName = author.getString("lastName");
					bookAuthors.add(new AuthorVO(authorId, authorFirstName, authorLastName));
				}
				result.add(new BookVO(bookId, bookTitle, bookAuthors));
			}
		}
		books.clear();
		books.addAll(result);
		LOG.debug("Leaving findBooks()");
		return result;
	}

	@Override
	public BookVO addBook(String title, String authors) throws InterruptedException {
		LOG.debug("Entering addBook(" + title + "," + authors + ")");

		if (title == null || authors == null) {
			throw new InterruptedException("title or authors fields empty!");
		}

		Set<AuthorVO> authorsToBeAdded = new HashSet<>();
		for (String author : authors.split(",")) {
			String[] names = author.split("\\s+");
			StringBuilder firstName = new StringBuilder();
			String lastName = null;
			for (int i = 0; i < names.length - 1; i++) {
				firstName.append(names[i] + " ");
			}
			if (!firstName.toString().isEmpty()) {
				firstName.deleteCharAt(firstName.length() - 1);
			}
			lastName = names[names.length - 1];
			authorsToBeAdded.add(new AuthorVO(null, firstName.toString(), lastName));
		}

		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		JSONObject bookJSON = new JSONObject(new BookVO(null, title, authorsToBeAdded));
		LOG.debug(bookJSON);

		try {
			URL url = new URL(URL_BOOKS + "book");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", Integer.toString(bookJSON.toString().length()));
			connection.setDefaultUseCaches(false);
			connection.setDoOutput(true);

			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			StringEntity bookTo = new StringEntity(bookJSON.toString(), "UTF-8");
			bookTo.writeTo(wr);
			wr.close();

			InputStream inputStream = connection.getInputStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
			}
			rd.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		LOG.debug("Response: " + response.toString());
		BookVO result = null;

		JSONObject book = new JSONObject(response.toString());
		Long bookId = book.getLong("id");
		String bookTitle = (String) book.get("title");
		Set<AuthorVO> bookAuthors = new HashSet<>();

		JSONArray authorsArray = book.getJSONArray("authors");
		for (int j = 0; j < authorsArray.length(); ++j) {
			JSONObject author = authorsArray.getJSONObject(j);
			Long authorId = author.getLong("id");
			String authorFirstName = (String) author.get("firstName");
			String authorLastName = (String) author.get("lastName");
			bookAuthors.add(new AuthorVO(authorId, authorFirstName, authorLastName));
		}
		result = new BookVO(bookId, bookTitle, bookAuthors);
		LOG.debug("Leaving addBook()");
		return result;
	}

	@Override
	public Boolean deleteBook(Long id) {
		LOG.debug("Entering deleteBook(" + id + ")");
		HttpURLConnection connection = null;
		try {
			URL url = new URL(URL_BOOKS + "book/" + id);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			// DO NOT DELETE LINE BELOW!
			LOG.debug("Response code: " + connection.getResponseCode());
		} catch (Exception e) {
			e.printStackTrace();
			return Boolean.FALSE;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		LOG.debug("Leaving deleteBook()");
		return Boolean.TRUE;
	}

}
