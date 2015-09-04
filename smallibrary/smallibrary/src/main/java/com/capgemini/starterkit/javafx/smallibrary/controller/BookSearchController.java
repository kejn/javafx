package com.capgemini.starterkit.javafx.smallibrary.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.capgemini.starterkit.javafx.smallibrary.dataprovider.DataProvider;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.BookVO;
import com.capgemini.starterkit.javafx.smallibrary.dataprovider.data.ErrorVO;
import com.capgemini.starterkit.javafx.smallibrary.model.BookSearch;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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
	private Button deleteButton;

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
		authorsField.textProperty().bindBidirectional(model.authorProperty());
		searchButton.disableProperty().bind(titleField.textProperty().isEmpty());
		addButton.disableProperty()
				.bind(Bindings.or(titleField.textProperty().isEmpty(), authorsField.textProperty().isEmpty()));
		deleteButton.disableProperty().bind(resultTable.selectionModelProperty().get().selectedItemProperty().isNull());
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
				setProgressMessage(null, false);
				if (getValue() == null) {
					return;
				}
				model.setResult(new ArrayList<BookVO>(getValue()));
				resultTable.getSortOrder().clear();
			}

			@Override
			protected void failed() {
				LOG.debug("searchButtonAction failed() called");
				setProgressMessage(null, false);
				showErrorAlert(ErrorVO.SEARCH, resources.getString("error.server"));
			}

		};

		setProgressMessage(resources.getString("label.searching"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void addButtonAction() {
		Task<BookVO> backgroundTask = new Task<BookVO>() {

			@Override
			protected BookVO call() throws Exception {
				LOG.debug("addButtonAction call() called");
				BookVO result = dataProvider.addBook(model.getTitle(), Arrays.asList(model.getAuthor().split(",")));
				return result;
			}

			@Override
			protected void succeeded() {
				LOG.debug("addButtonAction succeeded() called");
				setProgressMessage(null, false);
				List<BookVO> result = model.getResult();
				if (getValue() == null) {
					return;
				}
				result.add(new BookVO(getValue()));
			}

			@Override
			protected void failed() {
				LOG.debug("addButtonAction failed() called");
				setProgressMessage(null, false);
				showErrorAlert(ErrorVO.ADD, resources.getString("error.server"));
			}

		};

		setProgressMessage(resources.getString("label.adding"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	@FXML
	public void deleteButtonAction() {
		BookVO bookToDelete = resultTable.getSelectionModel().getSelectedItem();
		Task<Boolean> backgroundTask = new Task<Boolean>() {

			@Override
			protected Boolean call() throws Exception {
				LOG.debug("deleteButtonAction call() called");
				Boolean result = dataProvider.deleteBook(bookToDelete.getId());
				return result;
			}

			@Override
			protected void succeeded() {
				LOG.debug("deleteButtonAction succeeded() called");
				setProgressMessage(null, false);
				List<BookVO> result = model.getResult();
				result.remove(bookToDelete);
				resultTable.getSelectionModel().clearSelection();
			}

			@Override
			protected void failed() {
				LOG.debug("deleteButtonAction failed() called");
				setProgressMessage(null, false);
				if (getException() instanceof IOException) {
					showErrorAlert(ErrorVO.DELETE, resources.getString("error.server"));
				} else {
					showErrorAlert(ErrorVO.DELETE, getException().getMessage());
				}
			}

		};

		setProgressMessage(resources.getString("label.deleting"), true);
		Thread thread = new Thread(backgroundTask);
		thread.setDaemon(true);
		thread.start();
	}

	private void setProgressMessage(String message, boolean isVisible) {
		progressLabel.setText(message);
		progressLabel.setVisible(isVisible);
	}

	private void showErrorAlert(ErrorVO resourceErrorType, String moreInformation) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(resources.getString("error.title"));
		alert.setHeaderText(null);
		alert.setContentText(resources.getString("error." + resourceErrorType) + "\n" + moreInformation);
		alert.showAndWait();
	}
}
