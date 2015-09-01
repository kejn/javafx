package com.capgemini.starterkit.javafx.smallibrary.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.model.BookSearch;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class BookSearchController {

	private static final Logger LOG = Logger.getLogger(BookSearchController.class);

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private GridPane searchAddForm;

	@FXML
	private TextField titleField;

	@FXML
	private TextField authorsField;

	@FXML
	private Button searchButton;

	@FXML
	private Button addButton;

	@FXML
	private Label progressLabel;

	@FXML
	private TableView<BookVO> resultTable;

	@FXML
	private TableColumn<BookVO, String> titleColumn;

	@FXML
	private TableColumn<BookVO, String> authorsColumn;

	private final DataProvider dataProvider = DataProvider.INSTANCE;

	private final BookSearch model = new BookSearch();

	public BookSearchController() {
	}

	@FXML
	private void initialize() {
		initializeForm();
		initializeResultTable();
		LOG.debug("initializing -- finished");
	}

	private void initializeForm() {
		titleField.textProperty().bindBidirectional(model.titleProperty());
		titleField.setPromptText(resources.getString("title.placeholder"));
		authorsField.textProperty().bindBidirectional(model.authorProperty());
		authorsField.setPromptText(resources.getString("authors.placeholder"));
		searchButton.disableProperty().bind(titleField.textProperty().isEmpty());
		addButton.disableProperty()
				.bind(Bindings.or(titleField.textProperty().isEmpty(), authorsField.textProperty().isEmpty()));
		progressLabel.setVisible(false);
		LOG.debug("form initialized");
	}

	private void initializeResultTable() {
		titleColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getTitle()));
		authorsColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().getAuthors()
				.stream().map(author -> author.getFirstName() + " " + author.getLastName())
				.collect(Collectors.joining(",\n"))));
		resultTable.setPlaceholder(new Label(resources.getString("table.emptyText")));
		resultTable.itemsProperty().bind(model.resultProperty());
		LOG.debug("resultTable initialized");
	}

	@FXML
	public void searchButtonAction() {
		progressLabel.setText(resources.getString("label.searching"));
		Task<Collection<BookVO>> backgroundTask = new Task<Collection<BookVO>>() {

			@Override
			protected Collection<BookVO> call() throws Exception {
				LOG.debug("searchButtonAction call() called");
				Collection<BookVO> result = dataProvider.findBooks(model.getTitle());
				return result;
			}

			@Override
			protected void succeeded() {
				LOG.debug("searchButtonAction succeeded() called");
				model.setResult(new ArrayList<BookVO>(getValue()));
				resultTable.getSortOrder().clear();
			}

		};

		progressLabel.visibleProperty().bind(backgroundTask.runningProperty());
		backgroundTask.setOnSucceeded(event -> {
			progressLabel.textProperty().unbind();
		});

		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void addButtonAction() {
		progressLabel.setText(resources.getString("label.adding"));
		Task<BookVO> backgroundTask = new Task<BookVO>() {

			@Override
			protected BookVO call() throws Exception {
				LOG.debug("addButtonAction call() called");
				BookVO result = dataProvider.addBook(model.getTitle(), model.getAuthor());
				return result;
			}

			@Override
			protected void succeeded() {
				LOG.debug("addButtonAction succeeded() called");
				List<BookVO> result = model.getResult();
				if (getValue() == null) {
					return;
				}
				result.add(new BookVO(getValue()));
				// model.setResult(result);
				resultTable.getSortOrder().clear();
			}

		};

		progressLabel.visibleProperty().bind(backgroundTask.runningProperty());
		backgroundTask.setOnSucceeded(event -> progressLabel.textProperty().unbind());

		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

}
