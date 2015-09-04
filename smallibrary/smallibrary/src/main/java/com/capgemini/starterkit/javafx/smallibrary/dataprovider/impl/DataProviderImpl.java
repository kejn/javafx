package com.capgemini.starterkit.javafx.smallibrary.dataprovider.impl;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.http.entity.StringEntity;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.AuthorVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;

public class DataProviderImpl implements DataProvider {

	private static final Logger LOG = Logger.getLogger(DataProviderImpl.class);

	private final String URL_BOOKS;

	public DataProviderImpl() {
		File configFile = new File("src/main/resources/com/capgemini/starterkit/javafx/smallibrary/config/config.json");
		BufferedReader reader = null;
		JSONObject config = null;
		String url = null;
		try {
			reader = new BufferedReader(new FileReader(configFile));
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
				LOG.debug(line);
			}
			config = new JSONObject(builder.toString());
			url = config.getString("url");
		} catch (IOException e) {
			LOG.error(e.getMessage(), e);
		} catch (JSONException e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOG.error(e.getMessage(), e);
			}
		}
		URL_BOOKS = url;
	}

	@Override
	public Collection<BookVO> findBooks(String titlePrefix) throws IOException {
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
		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
			throw exception;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}

		LOG.debug("Response: " + response.toString());
		Collection<BookVO> result = new ArrayList<>();

		if (response.length() > 0) {
			JSONArray jsonBookArray = new JSONArray(response.toString());
			for (int i = 0; i < jsonBookArray.length(); ++i) {
				JSONObject jsonBook = jsonBookArray.getJSONObject(i);
				result.add(BookVO.fromJSONObject(jsonBook));
			}
		}
		LOG.debug("Leaving findBooks()");
		return result;
	}

	@Override
	public BookVO addBook(String title, Collection<String> authors) throws IllegalArgumentException, IOException {
		LOG.debug("Entering addBook(" + title + "," + authors + ")");

		if (title == null || authors == null) {
			throw new IllegalArgumentException("title or authors fields are empty!");
		}

		Set<AuthorVO> authorsToBeAdded = authors.stream().map(AuthorVO::fromString).collect(Collectors.toSet());
		HttpURLConnection connection = null;
		StringBuilder response = new StringBuilder();

		JSONObject bookJSON = new JSONObject(new BookVO(null, title, authorsToBeAdded));
		LOG.debug(bookJSON);

		DataOutputStream streamToServer = null;
		BufferedReader responseReader = null;
		try {
			URL url = new URL(URL_BOOKS + "book");
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", Integer.toString(bookJSON.toString().length()));
			connection.setDefaultUseCaches(false);
			connection.setDoOutput(true);

			streamToServer = new DataOutputStream(connection.getOutputStream());
			StringEntity bookTo = new StringEntity(bookJSON.toString(), "UTF-8");
			bookTo.writeTo(streamToServer);

			responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = responseReader.readLine()) != null) {
				response.append(line);
			}
		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
			throw exception;
		} finally {
			try {
				streamToServer.close();
			} catch (IOException exception) {
				LOG.error(exception.getMessage(), exception);
				throw exception;
			}
			try {
				responseReader.close();
			} catch (IOException exception) {
				LOG.error(exception.getMessage(), exception);
				throw exception;
			}
			if (connection != null) {
				connection.disconnect();
			}
		}

		LOG.debug("Response: " + response.toString());
		JSONObject jsonBook = new JSONObject(response.toString());
		LOG.debug("Leaving addBook()");
		return BookVO.fromJSONObject(jsonBook);
	}

	@Override
	public Boolean deleteBook(Long id) throws IllegalArgumentException, IOException {
		LOG.debug("Entering deleteBook(" + id + ")");
		HttpURLConnection connection = null;
		try {
			URL url = new URL(URL_BOOKS + "book/" + id);
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("DELETE");
			// DO NOT DELETE LINE BELOW!
			int response = connection.getResponseCode();
			LOG.debug("Response code: " + response);
			if (response != 200) {
				throw new IllegalArgumentException("HTTP Error " + response, null);
			}
		} catch (IOException exception) {
			LOG.error(exception.getMessage(), exception);
			throw exception;
		} catch (IllegalArgumentException exception) {
			LOG.error(exception.getMessage(), exception);
			throw exception;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
		LOG.debug("Leaving deleteBook()");
		return Boolean.TRUE;
	}

}
